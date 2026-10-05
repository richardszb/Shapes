package shapes;

/**
 * Common ancestor of the regular shapes (circle, regular triangle, square,
 * regular hexagon).
 * <p>
 * Every shape is described by its center and one length: the radius for the
 * circle, the side length for the polygons. For the polygons one side is
 * parallel to the horizontal axis, and the other vertices lie above the line
 * of this side.
 * <p>
 * The class is abstract, so it cannot be instantiated directly: only its
 * descendants ({@link Circle}, {@link RegularTriangle}, {@link Square},
 * {@link RegularHexagon}) can be created.
 */
public abstract class Shape {
    private final double centerX;
    private final double centerY;
    private final double size;

    /**
     * @param centerX the x coordinate of the center
     * @param centerY the y coordinate of the center
     * @param size the radius (circle) or the side length (polygons)
     * @throws IllegalArgumentException if a coordinate is not a finite number,
     * or the size is not a finite, positive number
     */
    protected Shape(double centerX, double centerY, double size) {
        if (!Double.isFinite(centerX) || !Double.isFinite(centerY)) {
            throw new IllegalArgumentException("The coordinates of the center must be finite numbers!");
        }
        if (!Double.isFinite(size) || size <= 0) {
            throw new IllegalArgumentException("The size must be a positive, finite number: " + size);
        }
        this.centerX = centerX;
        this.centerY = centerY;
        this.size = size;
    }

    public double getCenterX() {
        return centerX;
    }

    public double getCenterY() {
        return centerY;
    }

    public double getSize() {
        return size;
    }

    /**
     * @return the smallest axis-parallel rectangle that covers the shape;
     * every descendant computes it in its own way
     */
    public abstract BoundingBox getBoundingBox();

    /**
     * @return the name of the shape type, e.g. "Circle"
     */
    public abstract String getTypeName();

    /**
     * @return the area of the bounding box of the shape
     */
    public double boundingBoxArea() {
        return getBoundingBox().area();
    }


    @Override
    public String toString() {
        return getTypeName() + " {center=(" + centerX + ", " + centerY + "), size=" + size + '}';
    }
}