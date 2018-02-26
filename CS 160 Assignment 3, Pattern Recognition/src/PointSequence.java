import java.util.Arrays;

public class PointSequence implements Comparable<PointSequence> {
	private Point[] points;

	// Constructor that takes an array of points. there must be at least 2
	// points.
	// No points may be null.

	public PointSequence(Point[] points) {
		if (points == null)
			throw new RuntimeException("null pointer passed to PointSequence constructor.");

		if (points.length < 2)
			throw new RuntimeException("PointSequence must consist of at least 2 points.");

		// exception if 1 or fewer points or if any points are null
		this.points = new Point[points.length];

		for (int i = 0; i < points.length; i++) {
			if (points[i] == null)
				throw new RuntimeException("Point #" + i + " passed to PointSequence constructor is null.");
			this.points[i] = points[i];
		}
	}

	// Constructor that takes an iterable of points. there must be at least 2
	// points.
	// No points may be null.

	public PointSequence(Iterable<Point> pointIterable) {

		if (pointIterable == null)
			throw new RuntimeException("null pointer passed to PointSequence constructor.");

		int cnt = 0;
		for (Point p : pointIterable) {
			cnt++;
		}

		if (cnt < 2)
			throw new RuntimeException("PointSequence must consist of at least 2 points.");

		this.points = new Point[cnt];
		int i = 0;

		// exception if 1 or fewer points or if any points are null

		for (Point p : pointIterable) {
			if (p == null)
				throw new RuntimeException("Point #" + i + " passed to PointSequence constructor is null.");
			points[i++] = p;
		}

	}

	// Methods below this line are utility methods that you might find helpful.

	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append(points[0]);
		for (int i = 1; i < points.length; i++)
			sb.append(" -> " + points[i]);

		return sb.toString();
	}

	public void draw() {
		int N = points.length;
		for (int i = 0; i < N - 1; i++) {
			points[i].drawTo(points[i + 1]);
		}
	}

	// compares two segments, using number of points then coordinate
	// of points as a tiebreaker

	// returns 0 for segments in reverse order, e.g. a->b->c vs. c->b->a

	public int compareTo(PointSequence that) {
		if (this.numberOfPoints() < that.numberOfPoints())
			return -1;

		if (this.numberOfPoints() > that.numberOfPoints())
			return +1;

		int N = this.numberOfPoints();

		boolean equalSegments = true;
		for (int L = 0; L < N; L++) {
			int R = N - L - 1;
			if (this.points[L].compareTo(that.points[R]) != 0)
				equalSegments = false;
		}

		if (equalSegments)
			return 0;

		for (int i = 0; i < N; i++) {
			if (this.points[i].compareTo(that.points[i]) < 0)
				return -1;
			if (this.points[i].compareTo(that.points[i]) > 0)
				return +1;
		}

		return 0;
	}

	public int numberOfPoints() {
		return points.length;
	}

	// get methods are often a bad idea, but this one is ok since points are
	// always immutable.

	public Point get(int i) {
		return points[i];
	}

	public boolean containsDuplicates() {
		// decided to switch to array version because of this method!
		Point[] sortedPoints = Arrays.copyOf(points, points.length);

		Arrays.sort(sortedPoints);

		for (int i = 1; i < sortedPoints.length; i++)
			if (sortedPoints[i] == sortedPoints[i - 1])
				return true;

		return false;
	}

	// returns false if points are not ordered. If two points are equal
	// the method will also return false.

	public boolean isOrdered() {
		// return true if has 2 points, and they are not equal
		if (points.length == 2)
			return (points[0].compareTo(points[1]) != 0);

		if (containsDuplicates())
			return false;

		boolean startsByDescending = (points[0].compareTo(points[1]) > 0);

		for (int i = 2; i < points.length; i++) {
			boolean isDescending = (points[i - 1].compareTo(points[i]) > 0);
			if (isDescending != startsByDescending)
				return false;
		}

		return true;
	}

	// returns false if points are not all collinear with each other.

	public boolean isCollinear() {
		if (points.length == 2)
			return true;

		for (int i = 2; i < points.length; i++)
			if (points[i].slopeTo(points[i - 1]) != points[i - 1].slopeTo(points[i - 2]))
				return false;

		return true;
	}
}
