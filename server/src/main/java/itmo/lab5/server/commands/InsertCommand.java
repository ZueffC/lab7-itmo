package itmo.lab5.server.commands;

import itmo.lab5.shared.models.Flat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 *
 * @author oxff
 */
public class InsertCommand {
    public static String execute(Flat newFlat, HashMap<Integer, Flat> flats) {
        int newId = -1; 
        
        List<Integer> keys = new ArrayList<>(flats.keySet());
        if (!keys.isEmpty()) {
            newId = keys.get(keys.size() - 1);
        } else {
            newId = 1;
        }
        
        newFlat.setId(newId);
        flats.put(newId, newFlat);
        
        return "Element was successfuly inserted to collection!";
    }
}
