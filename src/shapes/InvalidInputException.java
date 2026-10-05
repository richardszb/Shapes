package shapes;

/**
 * Signals invalid input data.
 * <p>
 * It is a checked exception, so the caller has to deal with it: it must either
 * catch it or pass it on. Therefore, an invalid input file cannot be ignored.
 */
public class InvalidInputException extends Exception {
  /**
   * @param message the description of the error
   */
  public InvalidInputException(String message) {
    super(message);
  }
}
