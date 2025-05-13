package itmo.lab5.server.commands;

import itmo.lab5.shared.models.Flat;
import java.util.Comparator;
import java.util.HashMap;
import java.util.stream.Collectors;

/**
 *
 * @author oxff
 */
public class FieldCommand {
    public static String execute(HashMap<Integer, Flat> collection) {
        var builder = new StringBuilder();
        
        if (collection.isEmpty())
            return "Nothing to show!";

        var sortedEntries = collection.entrySet().stream()
            .sorted(Comparator.comparingInt(entry -> entry.getValue().getNumberOfRooms()))
            .collect(Collectors.toList());

        sortedEntries.forEach(entry -> builder
                .append("Key: ")
                .append(entry.getKey())
                .append(", Rooms: ")
                .append(entry.getValue().getNumberOfRooms())
                .append("\n")
        );
        
        return builder.toString();
     }
}
