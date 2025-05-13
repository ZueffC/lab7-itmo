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
public class InfoCommand implements Command {
    private static final String description = "command describes colletion.";

    @Override
    public String execute(String[] args, CommandContext context) {
        return RequestSender.getInstance().sendRequest(
                new DataPacket(CommandType.INFO, null, null));
    }

    public final String toString() {
        return this.description;
    }
}
