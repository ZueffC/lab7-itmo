package itmo.lab5.server;

import itmo.lab5.server.commands.*;
import itmo.lab5.shared.DataPacket;
import itmo.lab5.shared.models.Flat;
import java.util.HashMap;

/**
 *
 * @author oxff
 */
public class CommandManager {
    private static final History history = new History();
    
    public static String getAppropriateCommand(DataPacket pack, HashMap<Integer, Flat> collection) {
        history.add(pack.getType().name().toLowerCase());
        
        var result = switch (pack.getType()) {
            case SHOW -> ShowCommand.execute(collection);
            case INFO -> InfoCommand.execute(collection);
            case CLEAR -> ClearCommand.execute(collection);
            case HISTORY -> history.toString();
            case REMOVE_KEY -> RemoveKeyCommand.execute(pack.getId(), collection);
            case INSERT -> InsertCommand.execute(pack.getFlat(), collection);
            case FILTER_LESS_THAN_VIEW -> FilterCommand.execute("less", pack.getId(), collection);
            case FILTER_GREATER_THAN_VIEW -> FilterCommand.execute("greater", pack.getId(), collection);
            case PRINT_FIELD_ASCENDING_NUMBER_OF_ROOMS -> FieldCommand.execute(collection);
            case UPDATE -> UpdateCommand.execute(pack.getId(), pack.getFlat(), collection);
            default -> "There's no such command!";
        };
        
        return result;
    }
}
