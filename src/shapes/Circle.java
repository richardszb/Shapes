package shapes;

/**
 * A circle; its size is the radius.
 */
public class Circle extends Shape{

    /** The code of the circle in the input file. */
    public static final String CODE = "k";

    /**
     * @param centerX the x coordinate of the center
     * @param centerY the y coordinate of the center
     * @param radius the radius, a positive number
     */
    public Circle(double centerX, double centerY, double radius) {
        super(centerX, centerY, radius);
    }

    /**
     * The bounding box of a circle is a square with side 2r around the center.
     */
    @Override
    public BoundingBox getBoundingBox() {
        double r = getSize();
        return new BoundingBox(getCenterX() - r, getCenterY() - r, getCenterX() + r, getCenterY() + r);
    }


    @Override
    public String getTypeName() {
        return "Circle";
    }
}
