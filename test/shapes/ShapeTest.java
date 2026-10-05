package shapes;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

/**
 * Tests of the shapes, their bounding boxes, and initial validations.
 */
public class ShapeTest {

    private static void assertBoundingBox(BoundingBox bb, double minX, double minY, double maxX, double maxY, double area) {
        assertEquals("minX", minX, bb.getMinX(), 0.001);
        assertEquals("minY", minY, bb.getMinY(), 0.001);
        assertEquals("maxX", maxX, bb.getMaxX(), 0.001);
        assertEquals("maxY", maxY, bb.getMaxY(), 0.001);
        assertEquals("area", area, bb.area(), 0.001);
    }

    // --- BoundingBox validation ---

    @Test
    public void boundingBoxRejectsInvalidCoordinates() {
        assertThrows(IllegalArgumentException.class, () -> new BoundingBox(5, 0, 4, 10)); // minX > maxX
        assertThrows(IllegalArgumentException.class, () -> new BoundingBox(0, 5, 10, 4)); // minY > maxY
    }

    // --- Shape validation ---

    @Test
    public void shapeRejectsNegativeOrZeroSize() {
        assertThrows(IllegalArgumentException.class, () -> new Square(0, 0, 0));
        assertThrows(IllegalArgumentException.class, () -> new Circle(0, 0, -5));
    }

    @Test
    public void shapeRejectsInfiniteCoordinates() {
        assertThrows(IllegalArgumentException.class, () -> new RegularHexagon(Double.POSITIVE_INFINITY, 0, 5));
    }

    // --- Bounding box calculations ---

    @Test
    public void squareBoundingBoxIsCalculatedCorrectly() {
        Square sq = new Square(2, 2, 4);
        assertBoundingBox(sq.getBoundingBox(), 0, 0, 4, 4, 16.0);
        assertEquals("Square", sq.getTypeName());
    }

    @Test
    public void circleBoundingBoxIsCalculatedCorrectly() {
        Circle circle = new Circle(0, 0, 5);
        assertBoundingBox(circle.getBoundingBox(), -5, -5, 5, 5, 100.0);
        assertEquals("Circle", circle.getTypeName());
    }

    @Test
    public void regularHexagonBoundingBoxIsCalculatedCorrectly() {
        RegularHexagon hex = new RegularHexagon(0, 0, 2);
        double h = 2 * Math.sqrt(3) / 2;
        assertBoundingBox(hex.getBoundingBox(), -2, -h, 2, h, 4 * 2 * h);
        assertEquals("Regular Hexagon", hex.getTypeName());
    }

    @Test
    public void regularTriangleBoundingBoxIsCalculatedCorrectly() {
        RegularTriangle tri = new RegularTriangle(0, 0, 2);
        double h = 2 * Math.sqrt(3) / 2;
        assertBoundingBox(tri.getBoundingBox(), -1, -h / 3, 1, 2 * h / 3, 2 * h);
        assertEquals("Regular Triangle", tri.getTypeName());
    }

    @Test
    public void toStringContainsImportantData() {
        String text = new Circle(1.5, 2.5, 4).toString();
        assertTrue(text, text.contains("Circle"));
        assertTrue(text, text.contains("1.5"));
        assertTrue(text, text.contains("2.5"));
        assertTrue(text, text.contains("4"));
    }
}