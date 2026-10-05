package shapes;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.io.PrintStream;

/**
 * A collection of shapes: it reads them from a text file and finds the shape
 * with the largest bounding box.
 * <p>
 * The shapes of different types are stored together in one list, as they have
 * a common ancestor ({@link Shape}); they are processed uniformly.
 */
public class ShapeCollection {

    private final List<Shape> shapes = new ArrayList<>();

    /**
     * Reads the shapes from the given file. The file starts with the number of
     * the shapes, then every shape follows: its code, the x and y coordinate of
     * the center and its size (the radius or the side length). The items are
     * separated by white spaces.
     * <p>
     * If an error occurs, the collection remains unchanged, because the shapes
     * are added only at the end of a successful reading.
     *
     * @param filename the name of the file to read
     * @throws IOException if the file cannot be found or read
     * @throws InvalidInputException if the content of the file is invalid
     */
    public void read(String filename) throws IOException, InvalidInputException {

        List<Shape> loaded = new ArrayList<>();

        try (Scanner in = new Scanner(new File(filename), StandardCharsets.UTF_8)) {
            int count = readCount(in);
            for (int i = 1; i <= count; i++) {
                try {
                    loaded.add(readShape(in));
                } catch (NoSuchElementException ex) {
                    throw new InvalidInputException("Shape " + i + ": data is missing");
                } catch (IllegalArgumentException ex) {
                    throw new InvalidInputException("Shape " + i + ": " + ex.getMessage());
                }
            }
            if (in.hasNext()) { throw new InvalidInputException("More data than expected: " + in.next());
            }
        }
        shapes.addAll(loaded);
    }

    /**
     * Reads the first item of the file: the number of the shapes.
     */
    private static int readCount(Scanner in) throws InvalidInputException {
        try {
            int count = Integer.parseInt(in.next());
            if (count < 0) {
                throw new InvalidInputException("The number of shapes must not be negative: " + count);
            }
            return count;
        } catch (NoSuchElementException | NumberFormatException ex) {
            throw new InvalidInputException("The file must start with the number of shapes");
        }
    }

    /**
     * Reads one shape: code, x, y, size.
     */
    private static Shape readShape(Scanner in) {
        String code = in.next();
        double x = readNumber(in);
        double y = readNumber(in);
        double size = readNumber(in);
        return createShape(code, x, y, size);
    }

    /**
     * Reads one number.
     */
    private static double readNumber(Scanner in) {
        String token = in.next();
        try {
            return Double.parseDouble(token);
        } catch (NumberFormatException ex) {
            throw new IllegalArgumentException("Expected a number, but found: " + token);
        }
    }

    /**
     * "Factory" method: creates an instance of the descendant class that
     * belongs to the code.
     */
    private static Shape createShape(String code, double x, double y, double size) {
        return switch (code.toLowerCase()) {
            case Circle.CODE -> new Circle(x, y, size);
            case RegularTriangle.CODE -> new RegularTriangle(x, y, size);
            case Square.CODE -> new Square(x, y, size);
            case RegularHexagon.CODE -> new RegularHexagon(x, y, size);
            default -> throw new IllegalArgumentException("Unknown shape code: " + code);
        };
    }

    /**
     * Finds the shape whose bounding box has the largest area. If more shapes
     * share the largest area, the first one in the file is returned.
     *
     * @return the shape, or null if the collection is empty
     */
    public Shape findLargestBoundingBox() {
        Shape largest = null;
        for (Shape shape : shapes) {
            if (largest == null || shape.boundingBoxArea() > largest.boundingBoxArea()) {
                largest = shape;
            }
        }
        return largest;
    }

    /**
     * Prints the shapes with the area of their bounding boxes, then the shape
     * with the largest bounding box.
     */
    public void report(PrintStream out) {
        out.println("Shapes in the collection:");
        for (Shape shape : shapes) {
            out.println(shape + ", bounding box area: " + String.format( "%.2f", shape.boundingBoxArea()));
        }
        Shape largest = findLargestBoundingBox();
        if (largest == null) {
            out.println("The collection is empty.");
        } else {
            out.println("\nThe shape with the largest bounding box: " + largest);
            out.println("Its bounding box: " + largest.getBoundingBox() + ", area: " + String.format("%.2f", largest.boundingBoxArea()));
        }
    }

    /**
     * @return a read-only view of the shapes
     */
    public List<Shape> getShapes() {
        return Collections.unmodifiableList(shapes);
    }

    /**
     * @return the number of the shapes in the collection
     */
    public int size() {
        return shapes.size();
    }

}


