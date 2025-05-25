package itmo.lab5.server.commands;

import java.util.HashMap;

import itmo.lab5.server.Collection;
import itmo.lab5.shared.models.Flat;

public class RemoveKeyCommand {
     public static HashMap<Integer, Flat> remapIDs(HashMap<Integer, Flat> originalMap) {
        HashMap<Integer, Flat> newMap = new HashMap<>();
        int newId = 1; 

        for (int id = 1; id <= originalMap.size() + 1; id++) {
            if (originalMap.containsKey(id)) {
                newMap.put(newId, originalMap.get(id));
                newId++;
            }
        }

        return newMap;
    }
    
    public static String execute(Integer id, Collection collection) {
        if (collection == null || collection.getAllFlats().isEmpty())
            return "Collection is empty now!";
        
        collection.removeFlat(id);
        
        return "Element was successfuly deleted from collection!";
    }
}