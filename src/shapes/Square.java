package shapes;

/**
 * A square; its size is the side length. Its sides are parallel to the axes.
 */
public class Square extends Shape{

    /** The code of the square in the input file. */
    public static final String CODE = "n";

    /**
     * @param centerX the x coordinate of the center
     * @param centerY the y coordinate of the center
     * @param side the side length, a positive number
     */
    public Square(double centerX, double centerY, double side){
        super(centerX,centerY,side);
    }

    /**
     * The square is its own bounding box: it extends half of the side length
     * from the center in every direction.
     */
    @Override
    public BoundingBox getBoundingBox() {
        double half = getSize() / 2;
        return new BoundingBox(getCenterX() - half, getCenterY() - half, getCenterX() + half, getCenterY() + half);
    }

    @Override
    public String getTypeName() {
        return "Square";
    }
}
