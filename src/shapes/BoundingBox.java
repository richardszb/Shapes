package shapes;

import java.util.Locale;

/// An axis-parallel rectangle (the bounding box of a shape).
/// The object is immutable: once created, its corners cannot be changed.
public final class BoundingBox {

    private final double minX;
    private final double minY;
    private final double maxX;
    private final double maxY;

    /**
     * @param minX the smallest x coordinate (left side)
     * @param minY the smallest y coordinate (bottom side)
     * @param maxX the largest x coordinate (right side)
     * @param maxY the largest y coordinate (top side)
     * @throws IllegalArgumentException if a coordinate is not a number, or a
     * minimum is greater than the corresponding maximum
     * */
    public BoundingBox(double minX, double minY, double maxX, double maxY) {
        if (Double.isNaN(minX) || Double.isNaN(minY) || Double.isNaN(maxX) || Double.isNaN(maxY)) {
            throw new IllegalArgumentException("Invalid bounding box: a coordinate is not a number");
        }
        if (minX > maxX || minY > maxY) {
            throw new IllegalArgumentException("Invalid bounding box: the minimum is greater than the maximum");
        }
        this.minX = minX;
        this.minY = minY;
        this.maxX = maxX;
        this.maxY = maxY;
    }

    public double getMinX() {
        return minX;
    }

    public double getMinY() {
        return minY;
    }

    public double getMaxX() {
        return maxX;
    }

    public double getMaxY() {
        return maxY;
    }

    /**
     * @return the horizontal extent of the rectangle
     */
    public double width() {
        return maxX - minX;
    }

    /**
     * @return the vertical extent of the rectangle
     */
    public double height() {
        return maxY - minY;
    }

    /**
     * @return the area of the rectangle
     */
    public double area() {
        return width() * height();
    }

    @Override
    public String toString() {
        return String.format(Locale.ROOT, "[(%.2f, %.2f) - (%.2f, %.2f)]", minX, minY, maxX, maxY);
    }
}
