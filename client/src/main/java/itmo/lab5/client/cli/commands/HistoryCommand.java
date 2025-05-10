package itmo.lab5.client.cli.commands;

import itmo.lab5.client.cli.CommandContext;
import itmo.lab5.client.interfaces.Command;
import itmo.lab5.client.net.RequestSender;
import itmo.lab5.shared.CommandType;

/**
 *
 * @author oxff
 */
public class HistoryCommand implements Command {
    private static final String description = "show last 8 called commands";

    @Override
    public String execute(String[] args, CommandContext context) {
        return RequestSender.getInstance().sendRequest(CommandType.HISTORY, null, null);
    }
    
    public final String toString() {
        return this.description;
    }
}