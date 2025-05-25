package itmo.lab5.server;

import itmo.lab5.server.commands.ClearCommand;
import itmo.lab5.server.commands.FieldCommand;
import itmo.lab5.server.commands.FilterCommand;
import itmo.lab5.server.commands.InfoCommand;
import itmo.lab5.server.commands.InsertCommand;
import itmo.lab5.server.commands.RemoveKeyCommand;
import itmo.lab5.server.commands.ReplaceCommand;
import itmo.lab5.server.commands.ShowCommand;
import itmo.lab5.server.commands.SignUpCommand;
import itmo.lab5.server.commands.UpdateCommand;
import static itmo.lab5.shared.CommandType.CLEAR;
import static itmo.lab5.shared.CommandType.FILTER_GREATER_THAN_VIEW;
import static itmo.lab5.shared.CommandType.FILTER_LESS_THAN_VIEW;
import static itmo.lab5.shared.CommandType.HISTORY;
import static itmo.lab5.shared.CommandType.INFO;
import static itmo.lab5.shared.CommandType.INSERT;
import static itmo.lab5.shared.CommandType.PRINT_FIELD_ASCENDING_NUMBER_OF_ROOMS;
import static itmo.lab5.shared.CommandType.REMOVE_KEY;
import static itmo.lab5.shared.CommandType.REPLACE_IF_GREATER;
import static itmo.lab5.shared.CommandType.REPLACE_IF_LOWER;
import static itmo.lab5.shared.CommandType.SHOW;
import static itmo.lab5.shared.CommandType.UPDATE;
import itmo.lab5.shared.DataPacket;

/**
 *
 * @author oxff
 */
public class CommandManager {
    private static final History history = new History();

    public static String getAppropriateCommand(DataPacket pack, Collection collection, DatabaseManager dbManager) {
        history.add(pack.getType().name().toLowerCase());

        var result = switch (pack.getType()) {
            case SHOW -> ShowCommand.execute(collection.getAllFlats());
            case INFO -> InfoCommand.execute(collection.getAllFlats());
            case CLEAR -> ClearCommand.execute(collection);
            case HISTORY -> history.toString();
            case REMOVE_KEY -> RemoveKeyCommand.execute(pack.getId(), collection);
            case INSERT -> InsertCommand.execute(pack.getFlat(), collection);
            case FILTER_LESS_THAN_VIEW -> FilterCommand.execute("less", collection.getAllFlats(), pack.getFlat());
            case FILTER_GREATER_THAN_VIEW -> FilterCommand.execute("greater", collection.getAllFlats(), pack.getFlat());
            case PRINT_FIELD_ASCENDING_NUMBER_OF_ROOMS -> FieldCommand.execute(collection.getAllFlats());
            case UPDATE -> UpdateCommand.execute(pack.getId(), pack.getFlat(), collection);
            case REPLACE_IF_GREATER -> ReplaceCommand.execute(pack.getId(), pack.getFlat(), collection);
            case REPLACE_IF_LOWER -> ReplaceCommand.execute(pack.getId() * -1, pack.getFlat(), collection);
            case SIGN_UP -> SignUpCommand.execute(dbManager);
            default -> "There's no such command!";
        };

        return result;
    }
}
