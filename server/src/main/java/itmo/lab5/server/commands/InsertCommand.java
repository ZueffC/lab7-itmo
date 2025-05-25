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
    public static String execute(Flat newFlat, Collection collection) {
        int newId = -1;

        List<Integer> keys = new ArrayList<>(collection.getAllFlats().keySet());
        if (!keys.isEmpty())
            newId = keys.get(keys.size() - 1) + 1;
        else
            newId = 1;

        newFlat.setId(newId);
        collection.addFlat(newId, newFlat);

        return "Element was successfuly inserted to collection!";
    }
}
