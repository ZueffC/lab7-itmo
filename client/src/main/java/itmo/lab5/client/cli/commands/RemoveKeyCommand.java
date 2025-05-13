package itmo.lab5.client.cli.commands;

import itmo.lab5.client.cli.CommandContext;
import itmo.lab5.client.interfaces.Command;
import itmo.lab5.client.net.RequestSender;
import itmo.lab5.shared.CommandType;
import itmo.lab5.shared.DataPacket;

/**
 *
 * @author oxff
 */
public class RemoveKeyCommand implements Command {
    private static final String description = "command provide ability to delete element by id";

    @Override
    public String execute(String[] args, CommandContext context) {
        return RequestSender.getInstance().sendRequest(
                new DataPacket(CommandType.REMOVE_KEY, Integer.valueOf(args[0]), null));
    }

    /**
     *
     * @return command about info
     */
    @Override
    public final String toString() {
        return RemoveKeyCommand.description;
    }
}
