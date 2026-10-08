package shapes;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.Locale;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

/**
 * End-to-end tests of the command line program.
 */
public class MainTest {

    private static final String SINGLE_WINNER = """
            3
            n 0 0 2
            k 5 5 5
            h 2 2 3
            """;

    private static final String TIE = """
            2
            n 0 0 4
            k 10 10 2
            """;

    @Rule
    public TemporaryFolder folder = new TemporaryFolder();

    private final ByteArrayOutputStream out = new ByteArrayOutputStream();
    private final ByteArrayOutputStream err = new ByteArrayOutputStream();

    private String createFile(String content) throws IOException {
        return Files.writeString(folder.newFile().toPath(), content).toString();
    }

    /**
     * Runs the program with the given arguments.
     *
     * @param typedInput what the user types on the standard input
     */
    private int run(String typedInput, String... args) {
        return Main.run(args,
                new ByteArrayInputStream(typedInput.getBytes(Charset.defaultCharset())),
                new PrintStream(out, true, StandardCharsets.UTF_8),
                new PrintStream(err, true, StandardCharsets.UTF_8));
    }

    private String output() {
        return out.toString(StandardCharsets.UTF_8);
    }

    private String errors() {
        return err.toString(StandardCharsets.UTF_8);
    }

    @Test
    public void winnerIsPrintedForAFileGivenAsArgument() throws Exception {
        assertEquals(Main.EXIT_OK, run("", createFile(SINGLE_WINNER)));
        assertTrue(output(), output().contains("The shape with the largest bounding box: Circle"));
        assertEquals("", errors());
    }

    @Test
    public void fileNameIsAskedWhenThereIsNoArgument() throws Exception {
        String typed = createFile(SINGLE_WINNER) + System.lineSeparator();

        assertEquals(Main.EXIT_OK, run(typed));
        assertTrue(output(), output().contains("Name of the input file"));
        assertTrue(output(), output().contains("largest bounding box: Circle"));
    }

    @Test
    public void tieIsReportedAsFirstShape() throws Exception {
        assertEquals(Main.EXIT_OK, run("", createFile(TIE)));
        assertTrue(output(), output().contains("largest bounding box: Square"));
    }

    @Test
    public void missingFileIsAnError() {
        String missing = new File(folder.getRoot(), "missing.txt").getPath();

        assertEquals(Main.EXIT_ERROR, run("", missing));
        assertTrue(errors(), errors().contains("Could not read the file"));
        assertEquals("", output());
    }

    @Test
    public void invalidContentIsAnError() throws Exception {
        assertEquals(Main.EXIT_ERROR, run("", createFile("1\nx 0 0 2\n")));
        assertTrue(errors(), errors().contains("Invalid input!"));
        assertTrue(errors(), errors().contains("Unknown shape code: x"));
    }

    // --- Output ---

    @Test
    public void everyShapeIsListedWithTheAreaOfItsBoundingBox() throws Exception {
        assertEquals(Main.EXIT_OK, run("", createFile(SINGLE_WINNER)));
        assertTrue(output(), output().contains("Square {center=(0.0, 0.0), size=2.0}, bounding box area: 4.00"));
        assertTrue(output(), output().contains("Circle {center=(5.0, 5.0), size=5.0}, bounding box area: 100.00"));
        assertTrue(output(), output().contains("Regular Triangle {center=(2.0, 2.0), size=3.0}, bounding box area: 7.79"));
    }

    @Test
    public void boundingBoxOfTheWinnerIsPrinted() throws Exception {
        assertEquals(Main.EXIT_OK, run("", createFile(SINGLE_WINNER)));
        assertTrue(output(), output().contains("Its bounding box: [(0.00, 0.00) - (10.00, 10.00)], area: 100.00"));
    }

    @Test
    public void decimalPointIsUsedWhateverTheLanguageIs() throws Exception {
        Locale original = Locale.getDefault();
        try {
            Locale.setDefault(Locale.forLanguageTag("hu-HU"));
            assertEquals(Main.EXIT_OK, run("", createFile(SINGLE_WINNER)));
            assertTrue(output(), output().contains("bounding box area: 7.79"));
            assertTrue(output(), output().contains("area: 100.00"));
        } finally {
            Locale.setDefault(original);
        }
    }

    @Test
    public void emptyCollectionIsReportedWithoutError() throws Exception {
        assertEquals(Main.EXIT_OK, run("", createFile("0\n")));
        assertTrue(output(), output().contains("The collection is empty."));
        assertEquals("", errors());
    }

    // --- Errors ---

    @Test
    public void nothingIsPrintedToTheResultsForInvalidContent() throws Exception {
        assertEquals(Main.EXIT_ERROR, run("", createFile("2\nk 0 0 2\nn 0 0 -1\n")));
        assertEquals("", output());
        assertTrue(errors(), errors().contains("Shape 2"));
    }

    @Test
    public void directoryInsteadOfFileIsAnError() {
        assertEquals(Main.EXIT_ERROR, run("", folder.getRoot().getPath()));
        assertTrue(errors(), errors().contains("Could not read the file"));
        assertEquals("", output());
    }

    @Test
    public void fileNameAskedFromTheUserMayBeMissing() throws Exception {
        String missing = new File(folder.getRoot(), "missing.txt").getPath();

        assertEquals(Main.EXIT_ERROR, run(missing + System.lineSeparator()));
        assertTrue(errors(), errors().contains("Could not read the file"));
    }

    @Test
    public void exitCodesAreZeroForSuccessAndOneForError() {
        assertEquals(0, Main.EXIT_OK);
        assertEquals(1, Main.EXIT_ERROR);
    }
}
