
/*************************************************************************
 * Name: Rafael Zuniga
 * Login: rzuniga
 * Precept: 
 *
 * Compilation:  javac Point.java
 *
 * Description: An immutable data type for points in the plane.
 *
 *************************************************************************/
import java.util.Comparator;

public class Point {

	private int x, y;

	
	public final Comparator<Point> SLOPE_ORDER; 
	
	public Point(int x, int y) {
		// construct the point (x, y)
		this.x = x;
		this.y = y;
		SLOPE_ORDER = new SlopeOrder();
	}

	public void draw() {
		// draw this point
		StdDraw.point(x, y);
	}

	public void drawTo(Point that) {
		// draw the line segment from this point to that point
		StdDraw.line(this.x, this.y, that.x, that.y);
	}

	public String toString() {
		// string representation
		return "(" + x + ", " + y + ")";
	}

	public int compareTo(Point that) {
		// is this point lexicographically smaller than that point?
		if (this.y < that.y)
			return -1;
		if (this.y > that.y)
			return 1;
		if (this.x < that.x)
			return -1;
		if (this.x > that.x)
			return 1;
		return 0;

	}

	public double slopeTo(Point that) {
		// the slope between this point and that point
		double rise = that.y - this.y;
		double run = that.x - this.x;
		if (this == that || (rise == 0 && run == 0)) // check if same instance,
														// or same coordinates
			return Double.NEGATIVE_INFINITY;
		if (rise == 0) // horizontal line
			return 0.0;// test if positive zero
		if (run == 0) // vertical line
			return Double.POSITIVE_INFINITY;
		return rise / run;
	}

	private class SlopeOrder implements Comparator<Point> {

		public int compare(Point a, Point b) {

			double slopeToa = Point.this.slopeTo(a);
			double slopeTob = Point.this.slopeTo(b);

			if (slopeToa < slopeTob) // point a is less then point b
				return -1;
			if (slopeToa > slopeTob) // point a is greater than b
				return 1;
			return 0; // slopes are equal

		}

	}

}