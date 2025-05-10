package itmo.lab5.client.cli;

import itmo.lab5.client.interfaces.Command;

/**
 * Invokes commands from a command registry and maintains command history.
 */
public class CommandInvoker {
  
  private final CommandRegistry registry;
  private final CommandContext context;
  /**
   * Constructs a CommandInvoker with the specified command registry,
   * context, and history.
   *
   * @param registry the command registry to retrieve commands from.
   * @param context the context to be passed to commands during execution.
   */
  public CommandInvoker(CommandRegistry registry, CommandContext context) {
    this.registry = registry;
    this.context = context;
  }

  /**
   * Executes a command by its name with the provided arguments.
   *
   * @param commandName the name of the command to execute.
   * @param args an array of arguments to pass to the command.
   * @return the result of the command execution, or an error message if
   *         the command is unknown or an exception occurs during execution.
   */
  public String executeCommand(String commandName, String[] args) {
    Command command = registry.getByName(commandName);
    if (command != null) {
      try {
        return command.execute(args, context);
      } catch (Exception e) {
        return "Error executing command '" + commandName + "': " + e.getMessage();
      }
    }
    return "Unknown command: " + commandName;
  }
}