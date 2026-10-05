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
}