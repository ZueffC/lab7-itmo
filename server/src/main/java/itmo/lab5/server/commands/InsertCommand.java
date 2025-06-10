package itmo.lab5.server.commands;

import java.sql.SQLException;

import itmo.lab5.server.Collection;
import itmo.lab5.shared.models.Flat;

/**
 *
 * @author oxff
 */
public class InsertCommand {
    public static String execute(Flat newFlat, Collection collection, String nick) {
        try {
            int generatedId = collection.addFlat(newFlat, nick);
            newFlat.setId(generatedId);
            
            return "Element was successfuly inserted to collection!";
        } catch (SQLException ex) {
            return "There was an error while trying to add data to DB";
        }
    }
}
