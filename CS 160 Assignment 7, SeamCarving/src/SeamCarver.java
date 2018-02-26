import java.awt.Color;

public class SeamCarver {

	private Picture picture;

	// create a seam carver object based on the given picture
	public SeamCarver(Picture picture) {
		if (picture == null)
			throw new java.lang.NullPointerException();
		this.picture = new Picture(picture);
	}

	// current picture
	public Picture picture() {
		return picture;
	}

	// width of current picture
	public int width() {
		return picture.width();
	}

	// height of current picture
	public int height() {
		return picture.height();
	}

	// energy of pixel at column x and row y
	public double energy(int x, int y) {

		if (x < 0 || x > picture.width() - 1 || y < 0 || y > picture.height() - 1)
			throw new java.lang.IndexOutOfBoundsException();

		Color leftPixel;
		Color rightPixel;
		Color topPixel;
		Color botPixel;

		// set leftPixel
		if (x == 0)
			leftPixel = picture.get(picture.width() - 1, y);
		else
			leftPixel = picture.get(x - 1, y);

		// set rightPixel
		if (x == picture.width() - 1)
			rightPixel = picture.get(0, y);
		else
			rightPixel = picture.get(x + 1, y);

		// set topPixel
		if (y == 0)
			topPixel = picture.get(x, picture.height() - 1);
		else
			topPixel = picture.get(x, y - 1);

		// set rightPixel
		if (y == picture.height() - 1)
			botPixel = picture.get(x, 0);
		else
			botPixel = picture.get(x, y + 1);

		double squareXGradient = getSquareGradient(leftPixel, rightPixel);
		double squareYGradient = getSquareGradient(topPixel, botPixel);

		return Math.sqrt(squareXGradient + squareYGradient);
	}

	private double getSquareGradient(Color a, Color b) {
		double cntDiffRed = Math.abs(a.getRed() - b.getRed());
		double cntDiffGreen = Math.abs(a.getGreen() - b.getGreen());
		double cntDiffBlue = Math.abs(a.getBlue() - b.getBlue());
		return Math.pow(cntDiffRed, 2) + Math.pow(cntDiffGreen, 2) + Math.pow(cntDiffBlue, 2);
	}

	// sequence of indices for horizontal seam
	public int[] findHorizontalSeam() {
		picture = transpose(picture); // transpose
		int[] hSeam = findVerticalSeam();
		picture = transpose(picture); // transpose back
		return hSeam;
	}

	// transposes the picture parameter to horizontal or back to vertical
	private Picture transpose(Picture transposed) {
		transposed = new Picture(picture.height(), picture.width());
		for (int row = 0; row < transposed.height(); row++) {
			for (int col = 0; col < transposed.width(); col++) {
				transposed.set(col, row, picture.get(row, col));
			}
		}
		return transposed;
	}

	// sequence of indices for vertical seam
	public int[] findVerticalSeam() {
		// to compute shortest path
		double[][] distTo = new double[picture.width()][picture.height()];

		// original 2d energy matrix
		double[][] eMatrix = new double[picture.width()][picture.height()];
		for (int row = 0; row < picture.height(); row++) {
			for (int col = 0; col < picture.width(); col++) {
				eMatrix[col][row] = energy(col, row);

				// set all distances to infinity
				distTo[col][row] = Double.POSITIVE_INFINITY;
			}
		}

		// set the first row of distTo here
		for (int i = 0; i < picture.width(); i++) {
			distTo[i][0] = eMatrix[i][0];
		}

		// compute shortest paths
		for (int row = 0; row < picture.height() - 1; row++) {
			for (int col = 0; col < picture.width(); col++) {
				int x1, y1 = row + 1; // bottom-left pixel
				int x2 = col, y2 = row + 1; // bottom pixel
				int x3, y3 = row + 1; // bottom-right pixel

				// if at left edge of picture
				if (col == 0)
					x1 = picture.width() - 1;
				else
					x1 = col - 1;

				// if at right edge of picture
				if (col == picture.width() - 1)
					x3 = 0;
				else
					x3 = col + 1;

				double tempLeft = distTo[col][row] + eMatrix[x1][y1];
				double tempBottom = distTo[col][row] + eMatrix[x2][y2];
				double tempRight = distTo[col][row] + eMatrix[x3][y3];

				if (col != 0) // calculations dont wrap
					if (tempLeft < distTo[x1][y1])
						distTo[x1][y1] = tempLeft;
				if (tempBottom < distTo[x2][y2])
					distTo[x2][y2] = tempBottom;

				if (col != picture.width() - 1) // calculations dont wrap
					if (tempRight < distTo[x3][y3])
						distTo[x3][y3] = tempRight;

			}
		}
		return getSeam(distTo);
	}

	// traverses array bottom-up to find the indices of the seam
	private int[] getSeam(double[][] distTo) {

		// get the index of the lowest value in the last row
		int[] seamIndices = new int[picture.height()];
		double min = Double.POSITIVE_INFINITY;
		for (int col = 0; col < picture.width(); col++) {
			if (distTo[col][picture.height() - 1] < min) {
				seamIndices[picture.height() - 1] = col;
				min = distTo[col][picture.height() - 1];
			}
		}

		// bottom up algorithm to find seam
		int lastCol = seamIndices[picture.height() - 1];
		min = Double.POSITIVE_INFINITY;
		for (int row = picture.height() - 2; row >= 0; row--) {
			for (int col = lastCol - 1; col <= lastCol + 1; col++) {
				if (col == -1 || col == picture.width())
					continue; // avoids out of bound
				if (distTo[col][row] <= min) { // < or =<
					seamIndices[row] = col;
					min = distTo[col][row];
				}
			}
			lastCol = seamIndices[row];
		}
		return seamIndices;
	}

	// remove horizontal seam from current picture
	public void removeHorizontalSeam(int[] seam) {
		if (seam == null)
			throw new java.lang.NullPointerException();
		if (seam.length != picture.width())
			throw new java.lang.IllegalArgumentException();
		for (int i = 0; i < seam.length; i++)
			if (seam[i] < 0 || seam[i] > picture.height() - 1)
				throw new java.lang.IllegalArgumentException();
		if (picture.height() == 1)
			throw new java.lang.IllegalArgumentException();

		Picture newPic = new Picture(picture.width(), picture.height() - 1);

		for (int col = 0; col < newPic.width(); col++) {
			boolean found = false;
			for (int row = 0; row < newPic.height() - 1; row++) {
				if (row == seam[col]) // found pixel we need to remove
					found = true;

				if (!found)
					newPic.set(col, row, picture.get(col, row));
				else
					newPic.set(col, row, picture.get(col, row + 1));
			}
		}
		picture = newPic;
	}

	// remove vertical seam from current picture
	public void removeVerticalSeam(int[] seam) {
		if (seam == null)
			throw new java.lang.NullPointerException();
		if (seam.length != picture.height())
			throw new java.lang.IllegalArgumentException();
		for (int i = 0; i < seam.length; i++)
			if (seam[i] < 0 || seam[i] > picture.width() - 1)
				throw new java.lang.IllegalArgumentException();
		if (picture.width() == 1)
			throw new java.lang.IllegalArgumentException();

		Picture newPic = new Picture(picture.width() - 1, picture.height());
		for (int row = 0; row < newPic.height(); row++) {
			boolean found = false;
			for (int col = 0; col < newPic.width(); col++) {
				if (col == seam[row]) // found pixel we need to remove
					found = true;

				if (!found)
					newPic.set(col, row, picture.get(col, row));
				else
					newPic.set(col, row, picture.get(col + 1, row));
			}
		}
		picture = newPic;
	}

}