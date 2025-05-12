package itmo.lab5.client.cli.commands;

import itmo.lab5.client.cli.CommandContext;
import itmo.lab5.client.interfaces.Command;
import itmo.lab5.client.net.RequestSender;
import itmo.lab5.shared.CommandType;

/**
 *
 * @author oxff
 */
public class FieldCommand implements Command {

  private static final String description = "command allows to see flat's id soted by their number of rooms";

  @Override
  public final String toString() {
    return FieldCommand.description;
  }

  /**
   * Executes the command, sorting and displaying the flats from the provided
   * collection.
   *
   * @param args    an array of arguments passed to the command.
   * @param context the context containing the collection of flats to be
   *                processed.
   * @return a message indicating the result of the execution. If the collection
   *         is empty,
   *         it returns "Nothing to show!". If the collection cannot be parsed, it
   *         returns
   *         "Can't parse collection!".
   */
  @Override
  public String execute(String args[], CommandContext context) {
    return RequestSender.getInstance().sendRequest(
        CommandType.PRINT_FIELD_ASCENDING_NUMBER_OF_ROOMS, null, null);
  }
}
