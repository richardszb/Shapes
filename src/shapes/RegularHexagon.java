package shapes;

/**
 * A regular hexagon; its size is the side length. The bottom side is
 * horizontal (so the top side is horizontal, too).
 */
public class RegularHexagon extends Shape{

    /** The code of the regular hexagon in the input file. */
    public static final String CODE = "s";

    /**
     * @param centerX the x coordinate of the center
     * @param centerY the y coordinate of the center
     * @param side the side length, a positive number
     */
    public RegularHexagon(double centerX, double centerY, double side) {
        super(centerX, centerY, side);
    }

    /**
     * The distance between two opposite vertices is 2a, so the box is 2a wide.
     * The distance between the two horizontal sides is a * sqrt(3), so the box
     * is a * sqrt(3) high.
     */
    @Override
    public BoundingBox getBoundingBox() {
        double a = getSize();
        double halfHeight = a * Math.sqrt(3) / 2;
        return new BoundingBox(getCenterX() - a, getCenterY() - halfHeight, getCenterX() + a, getCenterY() + halfHeight);
    }

    @Override
    public String getTypeName() {
        return "Regular Hexagon";
    }
}
