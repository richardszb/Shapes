package shapes;

/**
 * A regular (equilateral) triangle; its size is the side length. The bottom
 * side is horizontal, the third vertex is above it. The center is the
 * centroid of the triangle.
 */
public class RegularTriangle extends Shape{

    /** The code of the regular triangle in the input file. */
    public static final String CODE = "h";

    /**
     * @param centerX the x coordinate of the center
     * @param centerY the y coordinate of the center
     * @param side the side length, a positive number
     */
    public RegularTriangle(double centerX, double centerY, double side) {
        super(centerX, centerY, side);
    }

    /**
     * The width of the box is the side length a. The height of the triangle is
     * h = a * sqrt(3) / 2, and the centroid divides it in a 1:2 ratio: the
     * bottom side is h/3 below the center, the top vertex is 2h/3 above it.
     */
    @Override
    public BoundingBox getBoundingBox() {
        double a = getSize();
        double h = a * Math.sqrt(3) / 2;
        return new BoundingBox(getCenterX() - a / 2, getCenterY() - h / 3, getCenterX() + a / 2, getCenterY() + 2 * h / 3);
    }

    @Override
    public String getTypeName() {
        return "Regular Triangle";
    }
}
