
package shapes;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.util.Scanner;
/**
 * Command line entry point of the program.
 * <p>
 * Usage: {@code java shapes.Main [input file]}. If no file is given as an
 * argument, the program asks for its name on the standard input.
 */

public class Main {
    /** This file is read if the user gives no file name. */
    private static final String DEFAULT_FILE = "test1.txt";
    /** Exit code of a successful run. */
    public static final int EXIT_OK = 0;
    /** Exit code used when the input could not be read or was invalid. */
    public static final int EXIT_ERROR = 1;

    /**
     * Starts the program and terminates the JVM with the resulting exit code.
     *
     * @param args the command line arguments; the first (optional) one is the
     * name of the input file
     */
    public static void main(String[] args) {
        System.exit(run(args, System.in, System.out, System.err));
    }

    /**
     * Runs the program on the given streams. Separated from
     * {@link #main(String[])} so that it can be tested without terminating the
     * JVM.
     *
     * @param args the command line arguments
     * @param in the stream the file name is asked from if it is not in
     * {@code args}
     * @param out the stream of the results
     * @param err the stream of the error messages
     * @return {@link #EXIT_OK} on success, {@link #EXIT_ERROR} otherwise
     */
    public static int run(String[] args, InputStream in, PrintStream out, PrintStream err) {
        String filename = args.length > 0 ? args[0] : askFilename(in, out);

        ShapeCollection collection = new ShapeCollection();
        try {
            collection.read(filename);
            collection.report(out);
            return EXIT_OK;
        } catch (IOException ex) {
            err.println("Could not read the file: " + filename);
            return EXIT_ERROR;
        } catch (InvalidInputException ex) {
            err.println("Invalid input! " + ex.getMessage());
            return EXIT_ERROR;
        }
    }

    /**
     * Asks the user for the name of the input file. An empty answer means the
     * default file.
     */
    private static String askFilename(InputStream in, PrintStream out) {
        out.print("Name of the input file (default: " + DEFAULT_FILE + "): ");
        Scanner console = new Scanner(in);
        String answer = console.hasNextLine() ? console.nextLine().trim() : "";
        return answer.isEmpty() ? DEFAULT_FILE : answer;
    }
}