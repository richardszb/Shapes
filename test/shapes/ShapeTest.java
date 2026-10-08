package shapes;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

/**
 * Tests of the shapes, their bounding boxes, and initial validations.
 */
public class ShapeTest {

    private static final double DELTA = 1e-9;

    private interface ShapeFactory {
        Shape create(double centerX, double centerY, double size);
    }

    /** One factory for every kind of shape, so the common rules are checked for all of them. */
    private static final ShapeFactory[] FACTORIES = {
            Circle::new, RegularTriangle::new, Square::new, RegularHexagon::new
    };

    private static void assertBoundingBox(BoundingBox bb, double minX, double minY, double maxX, double maxY, double area) {
        assertEquals("minX", minX, bb.getMinX(), 0.001);
        assertEquals("minY", minY, bb.getMinY(), 0.001);
        assertEquals("maxX", maxX, bb.getMaxX(), 0.001);
        assertEquals("maxY", maxY, bb.getMaxY(), 0.001);
        assertEquals("area", area, bb.area(), 0.001);
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

    // --- Validation of every kind of shape ---

    @Test
    public void sizeMustBePositiveForEveryKindOfShape() {
        for (ShapeFactory factory : FACTORIES) {
            assertThrows(IllegalArgumentException.class, () -> factory.create(0, 0, 0));
            assertThrows(IllegalArgumentException.class, () -> factory.create(0, 0, -0.001));
            assertThrows(IllegalArgumentException.class, () -> factory.create(0, 0, -5));
        }
    }

    @Test
    public void sizeMustBeFiniteForEveryKindOfShape() {
        for (ShapeFactory factory : FACTORIES) {
            assertThrows(IllegalArgumentException.class, () -> factory.create(0, 0, Double.NaN));
            assertThrows(IllegalArgumentException.class, () -> factory.create(0, 0, Double.POSITIVE_INFINITY));
            assertThrows(IllegalArgumentException.class, () -> factory.create(0, 0, Double.NEGATIVE_INFINITY));
        }
    }

    @Test
    public void coordinatesMustBeFiniteForEveryKindOfShape() {
        for (ShapeFactory factory : FACTORIES) {
            assertThrows(IllegalArgumentException.class, () -> factory.create(Double.NaN, 0, 1));
            assertThrows(IllegalArgumentException.class, () -> factory.create(Double.NEGATIVE_INFINITY, 0, 1));
            assertThrows(IllegalArgumentException.class, () -> factory.create(0, Double.NaN, 1));
            assertThrows(IllegalArgumentException.class, () -> factory.create(0, Double.POSITIVE_INFINITY, 1));
        }
    }

    @Test
    public void verySmallPositiveSizeIsAccepted() {
        for (ShapeFactory factory : FACTORIES) {
            assertEquals(Double.MIN_VALUE, factory.create(0, 0, Double.MIN_VALUE).getSize(), 0);
        }
    }

    @Test
    public void centerMayBeAnywhereIncludingTheNegativeRange() {
        for (ShapeFactory factory : FACTORIES) {
            Shape shape = factory.create(-100.5, -0.25, 2);
            assertEquals(-100.5, shape.getCenterX(), DELTA);
            assertEquals(-0.25, shape.getCenterY(), DELTA);
        }
    }

    // --- Common behaviour of every kind of shape ---

    @Test
    public void gettersReturnTheGivenValues() {
        for (ShapeFactory factory : FACTORIES) {
            Shape shape = factory.create(1.5, -2.5, 3);
            assertEquals(1.5, shape.getCenterX(), DELTA);
            assertEquals(-2.5, shape.getCenterY(), DELTA);
            assertEquals(3, shape.getSize(), DELTA);
        }
    }

    @Test
    public void boundingBoxAreaIsTheAreaOfTheBoundingBox() {
        for (ShapeFactory factory : FACTORIES) {
            Shape shape = factory.create(4, -1, 3);
            assertEquals(shape.getBoundingBox().area(), shape.boundingBoxArea(), DELTA);
        }
    }

    @Test
    public void boundingBoxCoversTheCenter() {
        for (ShapeFactory factory : FACTORIES) {
            BoundingBox box = factory.create(4, -1, 3).getBoundingBox();
            assertTrue(box.getMinX() < 4 && 4 < box.getMaxX());
            assertTrue(box.getMinY() < -1 && -1 < box.getMaxY());
        }
    }

    @Test
    public void movingTheCenterMovesTheBoundingBoxButDoesNotResizeIt() {
        for (ShapeFactory factory : FACTORIES) {
            BoundingBox atOrigin = factory.create(0, 0, 3).getBoundingBox();
            BoundingBox moved = factory.create(10, -4, 3).getBoundingBox();

            assertEquals(atOrigin.getMinX() + 10, moved.getMinX(), DELTA);
            assertEquals(atOrigin.getMaxX() + 10, moved.getMaxX(), DELTA);
            assertEquals(atOrigin.getMinY() - 4, moved.getMinY(), DELTA);
            assertEquals(atOrigin.getMaxY() - 4, moved.getMaxY(), DELTA);
            assertEquals(atOrigin.area(), moved.area(), DELTA);
        }
    }

    @Test
    public void biggerSizeGivesBiggerBoundingBox() {
        for (ShapeFactory factory : FACTORIES) {
            assertTrue(factory.create(0, 0, 2).boundingBoxArea() < factory.create(0, 0, 2.5).boundingBoxArea());
        }
    }

    @Test
    public void boundingBoxAreaFollowsTheFormulaOfEachShape() {
        double a = 3;
        assertEquals(4 * a * a, new Circle(1, 1, a).boundingBoxArea(), DELTA);
        assertEquals(a * a, new Square(1, 1, a).boundingBoxArea(), DELTA);
        assertEquals(a * a * Math.sqrt(3) / 2, new RegularTriangle(1, 1, a).boundingBoxArea(), DELTA);
        assertEquals(2 * Math.sqrt(3) * a * a, new RegularHexagon(1, 1, a).boundingBoxArea(), DELTA);
    }

    @Test
    public void everyShapeHasItsOwnCodeInTheInputFile() {
        assertEquals("k", Circle.CODE);
        assertEquals("h", RegularTriangle.CODE);
        assertEquals("n", Square.CODE);
        assertEquals("s", RegularHexagon.CODE);
    }

    @Test
    public void toStringStartsWithTheTypeNameForEveryKindOfShape() {
        for (ShapeFactory factory : FACTORIES) {
            Shape shape = factory.create(1, 2, 3);
            assertTrue(shape.toString(), shape.toString().startsWith(shape.getTypeName()));
        }
    }
}
