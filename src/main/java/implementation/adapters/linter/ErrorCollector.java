package implementation.adapters.linter;

import interpreter.ErrorHandler;

import java.util.ArrayList;
import java.util.List;

/**
 * ErrorCollector is an implementation of ErrorHandler that collects error messages
 * in a list for testing purposes.
 */
public class ErrorCollector implements ErrorHandler {
  private final List<String> errors;

  public ErrorCollector() {
    this.errors = new ArrayList<>();
  }

  @Override
  public void reportError(String message) {
    errors.add(message);
  }

  /**
   * Returns the list of collected error messages
   * @return list of error messages
   */
  public List<String> getErrors() {
    return new ArrayList<>(errors);
  }

  /**
   * Clears all collected errors
   */
  public void clear() {
    errors.clear();
  }

  /**
   * Returns true if no errors have been collected
   * @return true if no errors, false otherwise
   */
  public boolean hasNoErrors() {
    return errors.isEmpty();
  }

  /**
   * Returns the number of errors collected
   * @return number of errors
   */
  public int getErrorCount() {
    return errors.size();
  }
}