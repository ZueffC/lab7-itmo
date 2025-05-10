package itmo.lab5.server.commands;

import itmo.lab5.shared.models.Flat;
import java.util.HashMap;

/**
 *
 * @author oxff
 */
public class InfoCommand {
    public static String execute(HashMap<Integer, Flat> flats) {
        if (null == flats || flats.isEmpty())
            return "Collection is empty now!";

        var anyFlat = flats.values().iterator().next();

        return "Collections stores in: " + flats.getClass().getName() + "\n" +
               "Collection consists of: " + anyFlat.getClass().getName() + "\n"
                + "Items: " + flats.size();
    }
}
