import java.util.Arrays;
import java.util.Iterator;
import java.util.Stack;

public class FastCollinearPoints {

	private Stack<PointSequence> ptSeqStack = new Stack<PointSequence>();
	private Point[] points;
	private int N;

	// makes a defensive copy of the array of points
	public FastCollinearPoints(Point[] points) {
		N = points.length;
		this.points = points;

	}

	public int numberOfPoints() {
		// returns the number of total points in the array
		return points.length;
	}

	public int numberOfSegments(int minLength) {
		// returns the number of segments of length minLength or more
		Stack<PointSequence> ptSeqStackwRepeats = new Stack<PointSequence>();
		Point[] slopeOrderedPts = new Point[N];
		System.arraycopy(points, 0, slopeOrderedPts, 0, points.length); // defensive
		int qTemp = 0;

		for (int p = 0; p < N; p++) {

			Arrays.sort(slopeOrderedPts, points[p].SLOPE_ORDER);

			for (int q = 0, j = 0; q < N - 1; q += (j)) {
				j = 0;

				while ((q + j + 1) <= (N - 1)
						&& points[p].slopeTo(slopeOrderedPts[q + j]) == points[p].slopeTo(slopeOrderedPts[q + j + 1])) {

					if (points[p] != slopeOrderedPts[q + j]) {
						j++;

					}

				}

				// check if minLength lines
				if (j + 2 >= minLength) {

					Point[] tempPts = new Point[N];
					tempPts[0] = points[p]; // current point, always first in
					int sizeOfK = 0;

					for (int k = 0; k < j + 2; k++) {

						tempPts[k + 1] = slopeOrderedPts[qTemp + k];

						sizeOfK = k;

					}

					Point[] ptsForSeq = new Point[sizeOfK + 2];
					System.arraycopy(tempPts, 0, ptsForSeq, 0, sizeOfK + 2);
					// sort
					Arrays.sort(ptsForSeq);
					PointSequence ptSeq = new PointSequence(ptsForSeq);
					ptSeqStackwRepeats.push(ptSeq);
				}

				if (j == 0)
					j = 1;
				qTemp = q;

			}
		}

		// Removing repeats
		int stackFinalSize = ptSeqStackwRepeats.size();
		PointSequence currentSeq = null;
		PointSequence nextSeq = null;

		for (int i = 0; i < stackFinalSize - 1; i++) {
			if (currentSeq == null && nextSeq == null) {

				currentSeq = ptSeqStackwRepeats.pop();
				nextSeq = ptSeqStackwRepeats.pop();

			} else {
				currentSeq = nextSeq;
				nextSeq = ptSeqStackwRepeats.pop();

			}

			if (currentSeq.compareTo(nextSeq) != 0)
				ptSeqStack.push(currentSeq);

		}

		return ptSeqStack.size();
	}

	public Iterable<PointSequence> segments(int minLength) {
		Stack<PointSequence> ptSeqStackwRepeats = new Stack<PointSequence>();
		Point[] slopeOrderedPts = new Point[N];
		System.arraycopy(points, 0, slopeOrderedPts, 0, points.length); // defensive
		int qTemp = 0;

		for (int p = 0; p < N; p++) {

			Arrays.sort(slopeOrderedPts, points[p].SLOPE_ORDER);

			for (int q = 0, j = 0; q < N - 1; q += (j)) {
				j = 0;

				while ((q + j + 1) <= (N - 1)
						&& points[p].slopeTo(slopeOrderedPts[q + j]) == points[p].slopeTo(slopeOrderedPts[q + j + 1])) {

					if (points[p] != slopeOrderedPts[q + j]) {
						j++;

					}

				}

				// preparing for creating PointSequences with collinear max
				// width points
				if (j + 2 >= minLength) {

					Point[] tempPts = new Point[N];
					tempPts[0] = points[p]; // current point, always first in
					int sizeOfK = 0;

					for (int k = 0; k < j + 2; k++) {

						tempPts[k + 1] = slopeOrderedPts[qTemp + k];

						sizeOfK = k;

					}

					Point[] ptsForSeq = new Point[sizeOfK + 2];
					System.arraycopy(tempPts, 0, ptsForSeq, 0, sizeOfK + 2);
					// sort
					Arrays.sort(ptsForSeq);
					PointSequence ptSeq = new PointSequence(ptsForSeq);
					ptSeqStackwRepeats.push(ptSeq);
				}

				if (j == 0)
					j = 1;
				qTemp = q;

			}
		}

		// Removing repeats
		int stackFinalSize = ptSeqStackwRepeats.size();
		PointSequence currentSeq = null;
		PointSequence nextSeq = null;

		for (int i = 0; i < stackFinalSize - 1; i++) {
			if (currentSeq == null && nextSeq == null) {

				currentSeq = ptSeqStackwRepeats.pop();
				nextSeq = ptSeqStackwRepeats.pop();

			} else {
				currentSeq = nextSeq;
				nextSeq = ptSeqStackwRepeats.pop();

			}

			if (currentSeq.compareTo(nextSeq) != 0)
				ptSeqStack.push(currentSeq);

		}

		return ptSeqStack;
	}

	public static void main(String[] args) {
		// draws all maximal length segments of length 4 or more

		// animate
		StdDraw.setXscale(0, 32768);
		StdDraw.setYscale(0, 32768);
		StdDraw.show(0);

		// read input
		In in = new In(args[0]);
		int N = in.readInt();
		Point[] points = new Point[N];
		for (int i = 0; i < N; i++) {
			int x = in.readInt();
			int y = in.readInt();
			points[i] = new Point(x, y);
		}

		// Draw
		FastCollinearPoints fastPoints = new FastCollinearPoints(points);
		Iterator<PointSequence> ptSeqIterator = fastPoints.segments(4).iterator();

		while (ptSeqIterator.hasNext()) {
			ptSeqIterator.next().draw();

		}

		StdDraw.show(0);
	}

}