package itmo.lab5.client.cli.commands;

import itmo.lab5.client.cli.CommandContext;
import itmo.lab5.client.interfaces.Command;
import itmo.lab5.client.net.RequestSender;
import itmo.lab5.shared.*;

/**
 *
 * @author oxff
 */
public class SaveCommand implements Command {
    private static final String description = "command allows to save collection on sever if enabled";

    @Override
    public String execute(String[] args, CommandContext context) {
        return RequestSender.getInstance().sendRequest(
                new DataPacket(CommandType.SERVER_SAVE, null, null));
    }

    @Override
    public final String toString() {
        return SaveCommand.description;
    }
}