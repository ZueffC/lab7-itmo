package itmo.lab5.server.commands;

import itmo.lab5.server.Collection;

/**
 *
 * @author oxff
 */
public class ClearCommand {
        public static String execute(Collection collection, String nick) {
        if (collection == null || collection.getAllFlats().isEmpty())
            return "Collection is empty now!";

        collection.clear(nick);

        return "Collection has been cleared! "
                + "(Items now: " + collection.getAllFlats().size() + ")";
    }
}
