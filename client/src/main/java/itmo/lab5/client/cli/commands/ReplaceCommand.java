package itmo.lab5.client.cli.commands;

import java.time.ZoneId;
import java.util.Date;
import java.util.HashMap;
import java.util.Scanner;

import itmo.lab5.client.cli.CommandContext;
import itmo.lab5.client.interfaces.Command;
import itmo.lab5.client.net.RequestSender;
import itmo.lab5.shared.CommandType;
import itmo.lab5.shared.DataPacket;
import itmo.lab5.shared.models.Coordinates;
import itmo.lab5.shared.models.Flat;
import itmo.lab5.shared.models.House;
import itmo.lab5.shared.models.enums.Furnish;
import itmo.lab5.shared.models.enums.Transport;
import itmo.lab5.shared.models.enums.View;

/**
 *
 * @author oxff
 */
public class ReplaceCommand implements Command {
    private final Scanner scanner = new Scanner(System.in);
    private final ReaderUtil inputReader = new ReaderUtil(scanner);
    private static final String description = "command allows to update collection's element by provided id in k=v manner";
    private CommandType command_type;
    
    public ReplaceCommand(String classificator) {
        if("lower".equals(classificator)) {
            this.command_type = CommandType.REPLACE_IF_LOWER;
        } else {
            this.command_type = CommandType.REPLACE_IF_GREATER;
        }
    }
    
    public final String toString() {
        return this.description;
    }

    /**
     * Executes the update command, updating the flat with the specified ID.
     *
     * @param args    an array of arguments passed to the command, where the first
     *                element is expected to be the ID of the flat to update
     * @param context the command context that contains the collection of flats
     * @return a message indicating the result of the operation, or an error message
     *         if the ID is invalid or the collection cannot be parsed
     */
    @Override
    public String execute(String[] args, CommandContext context) {
        if (args.length < 1)
            return "Can't update element without ID!";

        int id;
        try {
            id = Integer.parseInt(args[0]);
        } catch (Exception e) {
            return "You provided wrong id!";
        }

        Flat updatedFlat = null;
        if (args.length > 1) {
            updatedFlat = updateByArgs(context, args, id);
        } else {
            updatedFlat = updateInteractive(context, id);
        }

        if (updatedFlat == null)
            return "Failed to update flat from arguments.";

        if (this.command_type == CommandType.REPLACE_IF_GREATER) {
            return RequestSender.getInstance().sendRequest(new DataPacket(CommandType.REPLACE_IF_GREATER, id, updatedFlat));
        } else if (this.command_type == CommandType.REPLACE_IF_LOWER) {
            return RequestSender.getInstance().sendRequest(new DataPacket(CommandType.REPLACE_IF_LOWER, id, updatedFlat));
        }

        return "Unknown replace condition.";
    }

    private Flat updateInteractive(CommandContext context, int id) {
        var creationDate = new Date().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();

        // Reading name from terminal then validating it (fix after first attempt)
        String name = inputReader.promptString("- Enter name: ", false, null);

        // Reading coordinates from terminal then compares it to null (fix after first
        // attempt)
        System.out.println("- Coordinates ");

        Long x = inputReader.promptNumber("\t Enter x: ", Long.MIN_VALUE, Long.MAX_VALUE, Long::parseLong, null);
        Double y = inputReader.promptNumber("\t Enter y: ", Double.MIN_VALUE, Double.MAX_VALUE, Double::parseDouble,
                null);
        var coordinates = new Coordinates(x, y);

        // Reading flat area from terminal then validating it.
        // Also renamed "square" -> "area" after first attempt
        Double area = inputReader.promptNumber("\t Enter area: ", 0.0, 626.0, Double::parseDouble, null);
        int numberOfRooms = inputReader.promptNumber("\t Enter numberOfRooms: ", 1, Integer.MAX_VALUE,
                Integer::parseInt,
                null);

        // Reading FURNISH ENUM value from terminal
        System.out.println("- Furnish (can't be empty)");

        Furnish furnish = inputReader.promptEnum("\t Enter furnish type: ", Furnish.class, null);
        while (furnish == null) {
            System.out.println("\t Furnish can't be empty!");
            furnish = inputReader.promptEnum("\t Enter furnish type: ", Furnish.class, null);
        }

        // Reading View ENUM from terminal, it can be empty
        System.out.println("- View (can be empty)");
        View view = inputReader.promptEnumNullable("\t Enter view type: ", View.class, null);

        // Reading Transport Enum from terminal, it can't be empty
        System.out.println("- Transport (can't be empty)");
        Transport transport = inputReader.promptEnum("\t Enter transport type: ", Transport.class, null);

        while (transport == null) {
            System.out.println("\t Transport can't be empty!");
            transport = inputReader.promptEnum("\t Enter transport type: ", Transport.class, null);
        }

        // Reading House values from terminal
        // Strange situation: by the task field House in the Flat class can be null
        // btw, the fields of House can't be null. So it seems like House can't be null
        // anyway
        House house = null;
        System.out.println("- House");

        String houseName = inputReader.promptString("\t Enter House name: ", false, null);
        int year = inputReader.promptNumber("\t Enter house age: ", 1, 959, Integer::parseInt, null);
        long floors = inputReader.promptNumber("\t Enter house floors count: ", 1L, 77L, Long::parseLong, null);

        house = new House(houseName, year, floors);

        String ownerNick = null;
        try {
            ownerNick = (String) context.get("nick");
        } catch (Exception e) {}

        try {
            return new Flat(
                    id,
                    name,
                    coordinates,
                    creationDate,
                    area,
                    numberOfRooms,
                    furnish,
                    view,
                    transport,
                    house,
                    ownerNick);
        } catch (Exception e) {
            System.out.println("There's an error while trying to add new element. Collection is broken.");
            return null;
        }
    }

    private Flat updateByArgs(CommandContext context, String[] args, int id) {
        HashMap<String, String> params = new HashMap<>();
 
        for (String arg : args) {
            String[] parts = arg.split("=", 2);

            if (parts.length == 2)
                params.put(parts[0], parts[1]);
        }

        try {
            String name = null;
            House house = null;
            Coordinates coordinates = null;
            Long x = null;
            Double y = null;
            Double area = null;
            Integer numberOfRooms = null;
            Furnish furnish = null;
            View view = null;
            Transport transport = null;
            String houseName = null;
            Integer year = null;
            Long floors = null;

            if (params.get("name") != null && params.get("name").length() > 0)
                name = params.get("name");

            if (params.containsKey("x"))
                x = Long.parseLong(params.get("x"));

            if (params.containsKey("y"))
                y = Double.parseDouble(params.get("y"));

            coordinates = new Coordinates(x, y);

            if (params.containsKey("area"))
                area = Double.parseDouble(params.get("area"));

            if (params.containsKey("numberOfRooms"))
                numberOfRooms = Integer.parseInt(params.get("numberOfRooms"));

            if (params.containsKey("furnish"))
                furnish = Furnish.valueOf(params.get("furnish").toUpperCase());

            if (params.containsKey("view"))
                view = View.valueOf(params.get("view").toUpperCase());

            if (params.containsKey("transport"))
                transport = Transport.valueOf(params.get("transport").toUpperCase());

            if (params.containsKey("houseName"))
                houseName = params.get("houseName");

            if (params.containsKey("houseYear"))
                year = Integer.parseInt(params.get("houseYear"));

            if (params.containsKey("houseFloors"))
                floors = Long.parseLong(params.get("houseFloors"));

            house = new House(houseName, year, floors);
            var currentDate = new Date().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();

            String ownerNick = null;
            try {
                ownerNick = (String) context.get("nick");
            } catch (Exception e) {}

            return new Flat(
                    id,
                    name,
                    coordinates,
                    currentDate,
                    area,
                    numberOfRooms,
                    furnish,
                    view,
                    transport,
                    house,
                    ownerNick);
        } catch (IllegalArgumentException e) {
            return null;
        }
    }
}
