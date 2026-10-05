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
    }

    private String createFile(String content) throws IOException {
        Path path = folder.newFile().toPath();
        Files.writeString(path, content);
        return path.toString();
    }

    // --- Valid input ---

    @Test
    public void sampleOfTheAssignmentIsParsed() throws Exception {
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
        assertInvalid("2\nk 0 0 2\nh 1 1\n", "data is missing"); // Triangle missing size
        assertInvalid("3\nk 0 0 2\n", "data is missing"); // Missing whole shape
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
}