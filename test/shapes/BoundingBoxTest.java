package shapes;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;

import java.util.Locale;
import org.junit.Test;

/**
 * Tests of the bounding box: its size calculations and the validation of its corners.
 */
public class BoundingBoxTest {

    private static final double DELTA = 1e-9;

    // --- Calculations ---

    @Test
    public void widthHeightAndAreaAreCalculatedFromTheCorners() {
        BoundingBox box = new BoundingBox(-1, 2, 3, 7);

        assertEquals(4, box.width(), DELTA);
        assertEquals(5, box.height(), DELTA);
        assertEquals(20, box.area(), DELTA);
    }

    @Test
    public void cornersAreReturnedAsGiven() {
        BoundingBox box = new BoundingBox(-1.5, 2, 3.25, 7);

        assertEquals(-1.5, box.getMinX(), DELTA);
        assertEquals(2, box.getMinY(), DELTA);
        assertEquals(3.25, box.getMaxX(), DELTA);
        assertEquals(7, box.getMaxY(), DELTA);
    }

    @Test
    public void boxEntirelyInTheNegativeRangeHasPositiveArea() {
        assertEquals(6, new BoundingBox(-5, -4, -2, -2).area(), DELTA);
    }

    @Test
    public void boxWithoutWidthOrHeightHasNoArea() {
        assertEquals(0, new BoundingBox(1, 1, 1, 5).area(), DELTA);
        assertEquals(0, new BoundingBox(1, 5, 4, 5).area(), DELTA);
        assertEquals(0, new BoundingBox(2, 2, 2, 2).area(), DELTA);
    }

    // --- Validation ---

    @Test
    public void minimumGreaterThanMaximumIsRejected() {
        assertThrows(IllegalArgumentException.class, () -> new BoundingBox(5, 0, 4, 10));
        assertThrows(IllegalArgumentException.class, () -> new BoundingBox(0, 5, 10, 4));
    }

    @Test
    public void notANumberCornerIsRejected() {
        assertThrows(IllegalArgumentException.class, () -> new BoundingBox(Double.NaN, 0, 1, 1));
        assertThrows(IllegalArgumentException.class, () -> new BoundingBox(0, Double.NaN, 1, 1));
        assertThrows(IllegalArgumentException.class, () -> new BoundingBox(0, 0, Double.NaN, 1));
        assertThrows(IllegalArgumentException.class, () -> new BoundingBox(0, 0, 1, Double.NaN));
    }

    // --- Text form ---

    @Test
    public void textHasTwoDecimalsAndADecimalPoint() {
        assertEquals("[(-1.50, 2.00) - (3.25, 7.00)]", new BoundingBox(-1.5, 2, 3.25, 7).toString());
    }

    @Test
    public void textUsesADecimalPointWhateverTheLanguageIs() {
        Locale original = Locale.getDefault();
        try {
            Locale.setDefault(Locale.forLanguageTag("hu-HU"));
            assertEquals("[(-1.50, 2.00) - (3.25, 7.00)]", new BoundingBox(-1.5, 2, 3.25, 7).toString());
        } finally {
            Locale.setDefault(original);
        }
    }
}
