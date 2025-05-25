/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package itmo.lab5.client.cli.commands;

import java.util.Arrays;
import java.util.stream.Collectors;

import itmo.lab5.client.cli.CommandContext;
import itmo.lab5.client.interfaces.Command;
import itmo.lab5.client.net.RequestSender;
import itmo.lab5.shared.CommandType;
import itmo.lab5.shared.DataPacket;
import itmo.lab5.shared.models.Flat;
import itmo.lab5.shared.models.enums.View;


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
    if (args.length < 1) {
        StringBuilder builder = new StringBuilder();
        builder.append("There's no classificator provided! \n");
        
        String constants = Arrays.stream(View.values())
                .map(Enum::name)
                .collect(Collectors.joining(", "));
     
        return "All available options: " + constants;
    }
      
    Integer threshold = null;
    var flat = new Flat();
    
    try {
      threshold = Math.abs(Integer.parseInt(args[0]));
      flat.setView(View.fromValue(threshold));
    } catch (NumberFormatException e) {
      flat.setView(View.valueOf(args[0].toUpperCase()));
    }

    if (threshold == null && flat.getView() == null)
      return "Provided wrong type of threshold!";

    if ("less".equals(this.classificator))
      return RequestSender.getInstance().sendRequest(
          new DataPacket(CommandType.FILTER_LESS_THAN_VIEW, threshold, flat));

    return RequestSender.getInstance().sendRequest(
        new DataPacket(CommandType.FILTER_GREATER_THAN_VIEW, threshold, flat));
  }
}
