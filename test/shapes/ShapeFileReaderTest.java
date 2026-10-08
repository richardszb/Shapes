package shapes;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

/**
 * Tests of parsing and validating the shapes description file.
 */
public class ShapeFileReaderTest {

    private static final String SAMPLE = """
            6
            k 0 0 2.5
            h 5 1 10
            n -3 2 7
            s 4 4 6
            k 10 -3 4
            n 1 1 3
            """;

    @Rule
    public TemporaryFolder folder = new TemporaryFolder();

    private void assertInvalid(String content, String expectedInMessage) throws IOException {
        String filename = createFile(content);
        ShapeCollection collection = new ShapeCollection();
        InvalidInputException ex = assertThrows(InvalidInputException.class, () -> collection.read(filename));
        assertTrue("Unexpected message: " + ex.getMessage(),
                ex.getMessage().toLowerCase().contains(expectedInMessage.toLowerCase()));
        assertEquals("The collection must stay empty after an error", 0, collection.size());
    }

    private String createFile(String content) throws IOException {
        Path path = folder.newFile().toPath();
        Files.writeString(path, content);
        return path.toString();
    }

    // --- Valid input ---

    @Test
    public void sampleFileIsParsed() throws Exception {
        ShapeCollection collection = new ShapeCollection();
        collection.read(createFile(SAMPLE));

        assertEquals(6, collection.size());
        assertEquals("Circle", collection.getShapes().get(0).getTypeName());
        assertEquals("Regular Triangle", collection.getShapes().get(1).getTypeName());
    }

    @Test
    public void blankLinesAndExtraSpacesAreAccepted() throws Exception {
        ShapeCollection collection = new ShapeCollection();
        collection.read(createFile("\n 2 \n\n k  0\t0 \n2 \n\n n 1 1 3 \n\n"));
        assertEquals(2, collection.size());
    }

    // --- File problems ---

    @Test
    public void missingFileIsReported() {
        ShapeCollection collection = new ShapeCollection();
        assertThrows(FileNotFoundException.class, () -> collection.read(folder.getRoot().toPath().resolve("missing.txt").toString()));
    }

    // --- Invalid content ---

    @Test
    public void emptyInputIsRejected() throws Exception {
        assertInvalid("","must start with the number of shapes");
    }

    @Test
    public void countMustBeANumber() throws Exception {
        assertInvalid("six\nk 0 0 2\n", "must start with the number");
    }

    @Test
    public void countMustNotBeNegative() throws Exception {
        assertInvalid("-2\nk 0 0 2\n", "must not be negative");
    }

    @Test
    public void missingDataIsRejected() throws Exception {
        assertInvalid("2\nk 0 0 2\nh 1 1\n", "data is missing");
        assertInvalid("3\nk 0 0 2\n", "data is missing");
    }

    @Test
    public void extraDataIsRejected() throws Exception {
        assertInvalid("1\nk 0 0 2\nextra", "more data than expected");
    }

    @Test
    public void unknownShapeCodeIsRejected() throws Exception {
        assertInvalid("1\nx 0 0 2", "unknown shape code: x");
    }

    @Test
    public void coordinateMustBeANumber() throws Exception {
        assertInvalid("1\nk x 0 2", "expected a number");
    }

    // --- More valid input ---

    @Test
    public void zeroShapesIsAValidFile() throws Exception {
        ShapeCollection collection = new ShapeCollection();
        collection.read(createFile("0\n"));
        assertEquals(0, collection.size());
    }

    @Test
    public void everyKindOfShapeIsCreatedFromItsCode() throws Exception {
        ShapeCollection collection = new ShapeCollection();
        collection.read(createFile("4\nk 0 0 1\nh 0 0 1\nn 0 0 1\ns 0 0 1\n"));

        assertTrue(collection.getShapes().get(0) instanceof Circle);
        assertTrue(collection.getShapes().get(1) instanceof RegularTriangle);
        assertTrue(collection.getShapes().get(2) instanceof Square);
        assertTrue(collection.getShapes().get(3) instanceof RegularHexagon);
    }

    @Test
    public void shapeCodeIsCaseInsensitive() throws Exception {
        ShapeCollection collection = new ShapeCollection();
        collection.read(createFile("4\nK 0 0 1\nH 0 0 1\nN 0 0 1\nS 0 0 1\n"));
        assertEquals(4, collection.size());
    }

    @Test
    public void valuesAreStoredAsWritten() throws Exception {
        ShapeCollection collection = new ShapeCollection();
        collection.read(createFile("1\nk -1.5 2.25 0.5\n"));

        Shape shape = collection.getShapes().get(0);
        assertEquals(-1.5, shape.getCenterX(), 1e-9);
        assertEquals(2.25, shape.getCenterY(), 1e-9);
        assertEquals(0.5, shape.getSize(), 1e-9);
    }

    @Test
    public void windowsLineEndingsAndMissingFinalNewlineAreAccepted() throws Exception {
        ShapeCollection collection = new ShapeCollection();
        collection.read(createFile("2\r\nk 0 0 2\r\nn 1 1 3"));
        assertEquals(2, collection.size());
    }

    // --- File name problems ---

    @Test
    public void nullFileNameIsRejected() {
        ShapeCollection collection = new ShapeCollection();
        assertThrows(NullPointerException.class, () -> collection.read(null));
    }

    @Test
    public void emptyAndBlankFileNamesAreFileProblems() {
        ShapeCollection collection = new ShapeCollection();
        assertThrows(IOException.class, () -> collection.read(""));
        assertThrows(IOException.class, () -> collection.read("   "));
    }

    @Test
    public void directoryInsteadOfFileIsAFileProblem() {
        ShapeCollection collection = new ShapeCollection();
        assertThrows(IOException.class, () -> collection.read(folder.getRoot().getPath()));
    }

    // --- More invalid content ---

    @Test
    public void fileWithOnlyWhitespaceIsRejected() throws Exception {
        assertInvalid("  \n\n \t\n", "must start with the number of shapes");
    }

    @Test
    public void countMustBeAWholeNumber() throws Exception {
        assertInvalid("2.5\nk 0 0 2\nk 0 0 2\n", "must start with the number");
        assertInvalid("99999999999\nk 0 0 2\n", "must start with the number");
    }

    @Test
    public void moreShapesThanDeclaredAreRejected() throws Exception {
        assertInvalid("1\nk 0 0 2\nn 1 1 3\n", "more data than expected");
    }

    @Test
    public void shapeWithOnlyACodeIsRejected() throws Exception {
        assertInvalid("1\nk\n", "data is missing");
    }

    @Test
    public void shapeCodeMustBeASingleLetter() throws Exception {
        assertInvalid("1\nkk 0 0 2\n", "unknown shape code: kk");
    }

    @Test
    public void numberInPlaceOfTheShapeCodeIsRejected() throws Exception {
        assertInvalid("2\nk 0 0 2\n5 0 0 1\n", "unknown shape code: 5");
    }

    @Test
    public void sizeMustBeANumber() throws Exception {
        assertInvalid("1\nk 0 0 big\n", "expected a number");
    }

    @Test
    public void decimalCommaIsRejected() throws Exception {
        assertInvalid("1\nk 0 0 1,5\n", "expected a number");
    }

    @Test
    public void sizeMustBePositive() throws Exception {
        assertInvalid("1\nk 0 0 0\n", "positive");
        assertInvalid("1\nn 0 0 -3\n", "positive");
        assertInvalid("1\nh 0 0 -0.5\n", "positive");
    }

    @Test
    public void notANumberAndInfinityAreRejected() throws Exception {
        assertInvalid("1\nk NaN 0 2\n", "finite");
        assertInvalid("1\nk 0 Infinity 2\n", "finite");
        assertInvalid("1\ns 0 0 Infinity\n", "finite");
        assertInvalid("1\ns 0 0 -Infinity\n", "positive");
    }

    @Test
    public void tooBigNumberIsRejected() throws Exception {
        assertInvalid("1\nk 0 0 " + "9".repeat(400) + "\n", "finite");
    }

    @Test
    public void errorMessageNamesTheInvalidShape() throws Exception {
        assertInvalid("3\nk 0 0 1\nn 0 0 2\nh 0 0 abc\n", "Shape 3");
        assertInvalid("3\nk 0 0 1\nn 0 0 -2\nh 0 0 3\n", "Shape 2");
    }

    @Test
    public void invalidShapeAfterValidOnesLeavesNothingLoaded() throws Exception {
        assertInvalid("3\nk 0 0 1\nn 0 0 2\nx 0 0 3\n", "Shape 3");
    }
}
