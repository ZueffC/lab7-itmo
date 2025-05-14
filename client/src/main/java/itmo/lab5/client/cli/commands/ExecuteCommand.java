package itmo.lab5.client.cli.commands;

import itmo.lab5.client.cli.CommandContext;
import itmo.lab5.client.cli.CommandInvoker;
import itmo.lab5.client.interfaces.Command;
import java.io.File;
import java.io.FileNotFoundException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.Scanner;
import java.util.Set;

/**
 *
 * @author oxff
 */
public class ExecuteCommand implements Command {
    private static final Set<String> executingScripts = new HashSet<>();
    private static final String description = "command allows to execute script contains commands for this REPL";

    @Override
    public String toString() {
        return ExecuteCommand.description;
    }

    /**
     * Executes the script command, running the commands specified in the given
     * script file.
     *
     * @param args    an array of arguments passed to the command, where the first
     *                element is expected to be the name of the script file
     * @param context the command context that contains the command invoker
     * @return a string containing the output of the executed commands, or an error
     *         message if the script file cannot be found or executed
     */
    @Override
    public String execute(String args[], CommandContext context) {
        if (args.length < 1) {
            return "Usage: execute_script <file_name>";
        }

        String fileName = args[0];
        File scriptFile = new File(fileName);
        Path path = Paths.get(fileName.toString());

        if (executingScripts.contains(scriptFile.getAbsolutePath()))
            return "Error: Recursive script execution detected for file: " + fileName;

        executingScripts.add(scriptFile.getAbsolutePath());

        try (Scanner fileScanner = new Scanner(scriptFile)) {
            CommandInvoker commandInvoker = (CommandInvoker) context.get("commandInvoker");
            StringBuilder output = new StringBuilder();

            while (fileScanner.hasNextLine()) {
                String line = fileScanner.nextLine().trim();
                if (line.isEmpty() || line.startsWith("#")) {
                    continue;
                }

                try {
                    String[] parts = line.split(" ", 2);
                    String commandName = parts[0];
                    String[] commandArgs = parts.length > 1 ? parts[1].split(" ") : new String[0];

                    String result = commandInvoker.executeCommand(commandName, commandArgs);
                    output.append("> ").append(line).append("\n").append(result).append("\n");
                } catch (Exception e) {
                    output.append("Error executing command '").append(line).append("': ")
                            .append(e.getMessage()).append("\n");
                }
            }

            return output.toString();
        } catch (FileNotFoundException e) {
            return "Error: Script file not found: " + fileName;
        } finally {
            executingScripts.remove(scriptFile.getAbsolutePath());
        }
    }
}
