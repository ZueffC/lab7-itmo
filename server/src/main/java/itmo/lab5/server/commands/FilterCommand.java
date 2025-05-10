package itmo.lab5.server.commands;

import itmo.lab5.shared.models.Flat;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

/**
 *
 * @author oxff
 */
public class FilterCommand {
    public static String execute(String classficator, Integer num, HashMap<Integer, Flat> collection) {
        List<Flat> result;
        var builder = new StringBuilder();
        
        if (null == collection || collection.isEmpty() || num == null)
            return "Collection is empty now!";

        if (classficator.equals("less")) {
          result = collection.values().stream()
              .filter(flat -> flat.getView() != null && 
                              flat.getView().ordinal() < num)
              .sorted(Comparator.comparingInt(flat -> flat.getView().ordinal()))
              .collect(Collectors.toList());
        } else {
          result = collection.values().stream()
              .filter(flat -> flat.getView() != null && 
                              flat.getView().ordinal() > num)
              .sorted(Comparator.comparingInt(flat -> flat.getView().ordinal()))
              .collect(Collectors.toList());
        }

        result.forEach(entry -> builder.append(entry.toString()));
        return builder.toString();
    }
}
