/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package itmo.lab5.client.cli.commands;

import itmo.lab5.client.cli.CommandContext;
import itmo.lab5.client.interfaces.Command;
import itmo.lab5.client.net.RequestSender;
import itmo.lab5.shared.CommandType;
import itmo.lab5.shared.DataPacket;

import java.lang.Math;

/**
 *
 * @author oxff
 */
public class FilterCommand implements Command {
  private String classificator;
  private final static String description = "provides ability to sort elements by their VIEW component";

  @Override
  public String toString() {
    return FilterCommand.description;
  }

  /**
   * Constructs a FilterCommand with the specified classificator.
   *
   * @param classificator the type of filtering to apply ("less" or "greater").
   */
  public FilterCommand(String classificator) {
    this.classificator = classificator;
  }

  /**
   * Executes the command, filtering and displaying the flats from the
   * provided collection based on the threshold value.
   *
   * @param args    an array of arguments where the first element is the
   *                threshold value for filtering.
   * @param context the context containing the collection of flats to be
   *                processed.
   * @return a message indicating the result of the execution. If no
   *         classificator is provided, it returns "There's no
   *         classificator provided!". If the collection cannot be parsed,
   *         it returns "Can't parse collection!". If the collection is
   *         empty or the threshold is null, it returns "Nothing to show!".
   */
  @Override
  public String execute(String args[], CommandContext context) {
    if (args.length < 1)
      return "There's no classificator provided!";

    Integer threshold = null;

    try {
      threshold = Math.abs(Integer.parseInt(args[0]));
    } catch (NumberFormatException e) {
      return "Provided wrong type of threshold!";
    }

    if (null == threshold)
      return "Provided wrong type of threshold!";

    if ("less".equals(this.classificator))
      return RequestSender.getInstance().sendRequest(
          new DataPacket(CommandType.FILTER_LESS_THAN_VIEW, threshold, null));

    return RequestSender.getInstance().sendRequest(
        new DataPacket(CommandType.FILTER_GREATER_THAN_VIEW, threshold, null));
  }
}
