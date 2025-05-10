package itmo.lab5.server.commands;

import itmo.lab5.shared.models.Flat;
import java.util.HashMap;

public class ShowCommand {
    public static String execute(HashMap<Integer, Flat> flats) {
        var builder = new StringBuilder();
        
        if(flats.isEmpty())
            return builder.append("Collection is empty!").toString();
        
        flats.forEach((id, element) -> {
            builder.append(element);
        });
        
        return builder.toString();
    }
}