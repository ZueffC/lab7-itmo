package itmo.lab5.client.cli;

import java.util.HashMap;
import java.util.Map;

import itmo.lab5.client.interfaces.Command;

/**
 * Manages the registration and retrieval of commands.
 */
public class CommandRegistry {

  private final Map<String, Command> commands = new HashMap<>();

  /**
   * Registers a new command with the specified name.
   *
   * @param name       the name of the command to register.
   * @param newCommand the command instance to register.
   */
  public void register(String name, Command newCommand) {
    this.commands.put(name, newCommand);
  }

  /**
   * Retrieves a command by its name.
   *
   * @param name the name of the command to retrieve.
   * @return the command associated with the specified name, or null
   *         if no command is found.
   */
  public Command getByName(String name) {
    return this.commands.get(name);
  }

  /**
   * Returns a map of all registered commands.
   *
   * @return a map where the keys are command names and the values are
   *         the corresponding command instances.
   */
  public Map<String, Command> getAllCommands() {
    return this.commands;
  }
}