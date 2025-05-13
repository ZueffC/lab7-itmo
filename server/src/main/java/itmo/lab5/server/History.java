package itmo.lab5.server;

/**
 *
 * @author oxff
 */
import java.util.ArrayDeque;
import java.util.Deque;

/**
 * Manages the history of executed commands, maintaining a fixed size.
 */
public class History {
  
  private static final int MAX_SIZE = 8;
  private final Deque<String> history = new ArrayDeque<>(MAX_SIZE);

  /**
   * Adds a command to the history. If the history exceeds the maximum
   * size, the oldest command is removed.
   *
   * @param command the command to add to the history.
   */
  public void add(String command) {
    if (history.size() >= MAX_SIZE)
      history.removeFirst();

    history.add(command);
  }

  /**
   * Retrieves a command from the history by its index.
   *
   * @param x the index of the command to retrieve.
   * @return the command at the specified index, or an empty string
   *         if the index is out of bounds.
   */
  public String get(int x) {
    if (x >= MAX_SIZE || x < 0)
      return "";

    return (String) history.toArray()[x];
  }

  /**
   * Returns a string representation of the entire command history.
   *
   * @return a string listing all commands in the history.
   */
  public String get() {
    return history.toString();
  }

  /**
   * Returns a formatted string representation of the command history.
   *
   * @return a string that lists all commands in the history with
   *         a prefix.
   */
  @Override
  public String toString() {
    var builder = new StringBuilder("History: \n");
    history.forEach(command -> builder.append(" - " + command + "\n"));
    return builder.toString();
  }
}