package itmo.lab5.server.commands;

import java.util.ArrayList;
import java.util.List;

import itmo.lab5.server.Collection;
import itmo.lab5.shared.models.Flat;

/**
 *
 * @author oxff
 */
public class InsertCommand {
    public static String execute(Flat newFlat, Collection collection, String nick) {
        int newId = -1;

        List<Integer> keys = new ArrayList<>(collection.getAllFlats().keySet());
        if (!keys.isEmpty())
            newId = keys.get(keys.size() - 1) + 1;
        else
            newId = 1;

        if(collection.containsFlat(newId))
            return "Can't overwrite someone else data!";

        newFlat.setId(newId);
        collection.addFlat(newId, newFlat, nick);

        return "Element was successfully inserted to collection!";
    }
}
