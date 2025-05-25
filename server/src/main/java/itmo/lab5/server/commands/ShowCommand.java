package itmo.lab5.server.commands;

import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

import itmo.lab5.shared.models.Flat;

public class ShowCommand {
    public static String execute(HashMap<Integer, Flat> flats) {
        var builder = new StringBuilder();

        if (flats.isEmpty())
            return builder.append("Collection is empty!").toString();

        List<Flat> sortedList = flats.values().stream()
                .sorted(Comparator.comparingInt(Flat::getId))
                .collect(Collectors.toList());

        sortedList.forEach((element) -> {
            builder.append(element);
        });

        return builder.toString();
    }
}
