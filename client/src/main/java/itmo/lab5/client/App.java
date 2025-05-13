package itmo.lab5.client;

import itmo.lab5.client.cli.commands.ExitCommand;
import java.util.Arrays;
import java.util.Scanner;
import java.util.logging.FileHandler;
import java.util.logging.Logger;

import itmo.lab5.client.cli.*;
import itmo.lab5.client.cli.commands.*;
import itmo.lab5.client.net.RequestSender;

/**
 * This class is an entry point of the application.
 * The main method creates REPL that provide ability create or modificate data
 * over the TCP
 */
public class App {
    /**
     * The main method that serves as the entry point for the application.
     * 
     * @param g_args command-line arguments (not used)
     */
    public static void main(String[] g_args) {
        RequestSender.init("localhost", 8080);
        CommandContext context = new CommandContext();
        CommandRegistry registry = new CommandBuilder()
                .register("show", new ShowCommand())
                .register("help", new HelpCommand())
                .register("exit", new ExitCommand())
                //.register("save", new SaveCommand())
                .register("info", new InfoCommand())
                .register("clear", new ClearCommand())
                .register("insert", new InsertCommand())
                .register("update", new UpdateCommand())
                .register("history", new HistoryCommand())
                .register("remove_key", new RemoveKeyCommand())
                .register("execute_script", new ExecuteCommand())
                .register("replace_if_lower", new ReplaceCommand("lower"))
                .register("filter_less_than_view", new FilterCommand("less"))
                .register("replace_if_greater", new ReplaceCommand("greater"))
                .register("filter_greater_than_view", new FilterCommand("greater"))
                .register("print_field_ascending_number_of_rooms", new FieldCommand())
                .build();

        context.set("registry", registry);
        CommandInvoker invoker = new CommandInvoker(registry, context);
        context.set("commandInvoker", invoker);

        var scanner = new Scanner(System.in);
        while (true) {
            System.out.print("> ");
            String input = scanner.nextLine().trim();
            if (input.isEmpty())
                continue;

            String[] args = input.split(" ");
            String commandName = args[0];
            String[] commandArgs = args.length > 1 ? Arrays.copyOfRange(args, 1, args.length) : new String[0];

            String response = invoker.executeCommand(commandName, commandArgs);
            System.out.println(response);
        }
    }
}
