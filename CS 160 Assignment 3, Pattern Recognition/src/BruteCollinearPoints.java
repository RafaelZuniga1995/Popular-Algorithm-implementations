import java.util.Arrays;
import java.util.Iterator;
import java.util.Stack;

public class BruteCollinearPoints {
	private Point[] points;
	private int numberOfSegments;

	private Stack<PointSequence> pointSeqStack = new Stack<PointSequence>();

	public BruteCollinearPoints(Point[] points) {
		// makes a defensive copy of the array of points
		this.points = points;
		double slope1;
		double slope2;
		double slope3;

		for (int i = 0; i < points.length; i++)
			for (int j = i + 1; j < points.length; j++) {
				slope1 = points[i].slopeTo(points[j]);
				for (int p = j + 1; p < points.length; p++) {
					slope2 = points[i].slopeTo(points[p]);
					if (slope1 == slope2)
						for (int q = p + 1; q < points.length; q++) {
							slope3 = points[i].slopeTo(points[q]);

							if (slope1 == slope3) { // 4 collinear points found
								Point[] tempSequence = new Point[4];cxdsxs c xd
								// set the 4 collinear points in new array
								tempSequence[0] = points[i];
								tempSequence[1] = points[j];
								tempSequence[2] = points[p];
								tempSequence[3] = points[q];

								Arrays.sort(tempSequence); // Sort

								PointSequence pointSequence = new PointSequence(tempSequence);
								pointSeqStack.push(pointSequence);
								numberOfSegments++;
							}
						}
				}
			}

	}

	public int numberOfPoints() {
		// returns the number of total points in the array
		return points.length;
	}

	public int numberOfSegments() {
		// returns the number of segments of length 4
		return numberOfSegments;
	}

	public Iterable<PointSequence> segments() {
		// returns an iterable of segments of length 4
		return pointSeqStack;
	}

	public static void main(String[] args) {

		// animate
		StdDraw.setXscale(0, 32768);
		StdDraw.setYscale(0, 32768);
		StdDraw.show(0);

		// draws all 4 point segments in file
		In in = new In(args[0]);
		int N = in.readInt();
		Point[] points = new Point[N];
		for (int i = 0; i < N; i++) {
			int x = in.readInt();
			int y = in.readInt();
			points[i] = new Point(x, y);
		}

		BruteCollinearPoints cPoints = new BruteCollinearPoints(points);

		Iterator<PointSequence> ptSeqIterator = cPoints.segments().iterator();

		while (ptSeqIterator.hasNext()) {
			ptSeqIterator.next().draw();

		}

		StdDraw.show(0);

	}
}