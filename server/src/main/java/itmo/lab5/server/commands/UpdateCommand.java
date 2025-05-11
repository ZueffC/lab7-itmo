package itmo.lab5.server.commands;

import itmo.lab5.shared.models.Flat;
import java.util.HashMap;

/**
 *
 * @author oxff
 */
public class UpdateCommand {
    public static String execute(int id, Flat newFlat, HashMap<Integer, Flat> collection) {
        var oldFlat = collection.getOrDefault(id, null);
        if (oldFlat == null)
            return "There is not flat with such id!";
        
        if(newFlat.getName().length() > 0)
            oldFlat.setName(newFlat.getName()); 
        
        return null;
   }
}
