package itmo.lab5.client.cli;

import itmo.lab5.client.interfaces.Command;

/**
 * This class is responsible for constructing a CommandRegistry
 * which contains all commands 
 */
public class CommandBuilder {
  private final CommandRegistry registry;
  
  /**
  * Constructs a new instance, initializing an empty CommandRegistry.
  */
  public CommandBuilder() {
    this.registry = new CommandRegistry();
  }

  /**
  * Registers a new command with the specified name in the command registry.
  *
  * @param name the name of the command to register
  * @param newCommand the command instance to be registered
  * @return the current instance for method chaining
  */
  public CommandBuilder register(String name, Command newCommand) {
    this.registry.register(name, newCommand);
    return this;
  }
  
  /**
  * Builds and returns the constructed CommandRegistry containing all registered commands.
  *
  * @return the constructed CommandRegistry
  */
  public CommandRegistry build() {
    return this.registry;
  }
}