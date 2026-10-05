package shapes;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.util.Scanner;

public class Main {
    private static final String DEFAULT_FILE = "test1.txt";
    public static final int EXIT_OK = 0;
    public static final int EXIT_ERROR = 1;

    public static void main(String[] args) {
        System.exit(run(args, System.in, System.out, System.err));
    }

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

    private static String askFilename(InputStream in, PrintStream out) {
        out.print("Name of the input file (default:" + DEFAULT_FILE + "): ");
        Scanner console = new Scanner(in);
        String answer = console.hasNextLine() ? console.nextLine().trim() : "";
        return answer.isEmpty() ? DEFAULT_FILE : answer;
    }
}