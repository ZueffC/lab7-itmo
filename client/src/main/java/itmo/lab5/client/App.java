package itmo.lab5.client;

import java.util.Arrays;
import java.util.NoSuchElementException;
import java.util.Scanner;

import itmo.lab5.client.cli.CommandBuilder;
import itmo.lab5.client.cli.CommandContext;
import itmo.lab5.client.cli.CommandInvoker;
import itmo.lab5.client.cli.CommandRegistry;
import itmo.lab5.client.cli.commands.ClearCommand;
import itmo.lab5.client.cli.commands.ExecuteCommand;
import itmo.lab5.client.cli.commands.ExitCommand;
import itmo.lab5.client.cli.commands.FieldCommand;
import itmo.lab5.client.cli.commands.FilterCommand;
import itmo.lab5.client.cli.commands.HelpCommand;
import itmo.lab5.client.cli.commands.HistoryCommand;
import itmo.lab5.client.cli.commands.InfoCommand;
import itmo.lab5.client.cli.commands.InsertCommand;
import itmo.lab5.client.cli.commands.RemoveKeyCommand;
import itmo.lab5.client.cli.commands.ReplaceCommand;
import itmo.lab5.client.cli.commands.ShowCommand;
import itmo.lab5.client.cli.commands.UpdateCommand;
import itmo.lab5.client.net.RequestSender;
import itmo.lab5.shared.CommandType;
import itmo.lab5.shared.DataPacket;

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
        RequestSender.init(System.getProperty("HOST", "localhost"), Integer.parseInt(System.getProperty("PORT", "7070")));
        CommandContext context = CommandContext.getInstance();
        CommandRegistry registry = new CommandBuilder()
                .register("show", new ShowCommand())
                .register("help", new HelpCommand())
                .register("exit", new ExitCommand())
                // .register("save", new SaveCommand())
                .register("info", new InfoCommand())
                .register("clear", new ClearCommand())
                .register("insert", new InsertCommand())
                .register("update", new UpdateCommand())
                .register("history", new HistoryCommand())
                .register("remove_key", new RemoveKeyCommand())
                .register("execute_script", new ExecuteCommand())
                .register("filter_greater_than_view", new FilterCommand("greater"))
                .register("replace_if_greater", new ReplaceCommand("greater"))
                .register("filter_less_than_view", new FilterCommand("less"))
                .register("replace_if_lower", new ReplaceCommand("lower"))
                .register("print_field_ascending_number_of_rooms", new FieldCommand())
                .build();

        context.set("registry", registry);
        CommandInvoker invoker = new CommandInvoker(registry, context);
        context.set("commandInvoker", invoker);
        var scanner = new Scanner(System.in);

        System.out.println("Hi! This app requires signup before working with data!");
        String regFlag;
        CommandType authType = null;

        String nick = new String("");
        String password = new String("");
        Boolean unauthorized = true;

        try {
            while(unauthorized) {
                do {
                    System.out.print("Do you want to sign in? (y/n): ");
                    regFlag = scanner.nextLine().trim();

                    if ("y".equalsIgnoreCase(regFlag))
                        authType = CommandType.SIGN_IN;
                    else if ("n".equalsIgnoreCase(regFlag))
                        authType = CommandType.SIGN_UP;
                    else
                        System.out.println("Invalid input. Please enter 'y' or 'n'.");
                } while (
                    !"y".equalsIgnoreCase(regFlag) && !"n".equalsIgnoreCase(regFlag));  

                do {
                    System.out.print("Your nick: ");
                    nick = scanner.nextLine().trim();

                    System.out.print("Your password: ");
                    password = scanner.nextLine().trim();
                } while (nick.trim().isEmpty() || password.trim().isEmpty());

                context.set("nick", nick);
                context.set("password", password);

                String result = new String();
                if(authType.equals(CommandType.SIGN_UP) || authType.equals(CommandType.SIGN_IN))
                    result = RequestSender.getInstance().sendRequest(new DataPacket(authType, null, null));     
                
                if(!result.toLowerCase().contains("such user"))
                    unauthorized = false;
                
                System.out.println(result);
            }       
        } catch (NoSuchElementException e) {
            System.out.println("Input proccess was interrupted!");
        }

        while (true) {
            try {
                System.out.print("> ");
                String input = scanner.nextLine().trim();
                if (input.isEmpty())
                    continue;

                String[] args = input.split(" ");
                String commandName = args[0];
                String[] commandArgs = args.length > 1 ? Arrays.copyOfRange(args, 1, args.length) : new String[0];

                String response = invoker.executeCommand(commandName, commandArgs);
                System.out.println(response);
            } catch (NoSuchElementException e) {
                System.out.println("\nExiting...");
                break;
            }
        }
        scanner.close();
    }
}
