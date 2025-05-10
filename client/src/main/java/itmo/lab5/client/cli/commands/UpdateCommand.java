package itmo.lab5.client.cli.commands;

/**
 *
 * @author oxff
 */

import itmo.lab5.client.cli.CommandContext;
import itmo.lab5.client.interfaces.Command;
import itmo.lab5.client.net.RequestSender;
import itmo.lab5.shared.CommandType;
import itmo.lab5.shared.models.*;
import itmo.lab5.shared.models.enums.*;
import java.time.ZoneId;
import java.util.*;

/**
 * This class implements the Command interface and provides
 * functionality to update an existing Flat object in the collection based on
 * its ID.
 *
 * When executed, this command retrieves the flat with the specified ID from
 * the collection, prompts the user for new values to update the flat's
 * properties, and then saves the updated flat back to the collection.
 *
 */
public class UpdateCommand implements Command {
    private final Scanner scanner = new Scanner(System.in);
    private final ReaderUtil inputReader = new ReaderUtil(scanner);
    private static final String description = "command allows to update collection's element by provided id in k=v manner";

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

        HashMap<Integer, Flat> collection;
        int id;

        try {
            id = Integer.parseInt(args[0]);
        } catch (Exception e) {
            return "You provided wrong id!";
        }

        try {
            collection = (HashMap<Integer, Flat>) context.get("collection");
        } catch (ClassCastException e) {
            return "Collection is corrupted or not found in context.";
        }

        if (!collection.containsKey(id))
            return "No element with id " + id;

        Flat updatedFlat = null;
        
        if (args.length > 1) {
            updatedFlat = updateByArgs(args, id);
            if (updatedFlat == null)
                return "Failed to update flat from arguments.";
            
            return RequestSender.getInstance().sendRequest(CommandType.UPDATE, id, updatedFlat);
        }
        
        updatedFlat = updateInteractive(context, id);
        return RequestSender.getInstance().sendRequest(CommandType.UPDATE, id, updatedFlat);
    }

    private Flat updateInteractive(CommandContext context, int id) {
        var creationDate = new Date().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();

        // Reading name from terminal then validating it (fix after first attempt)
        String name = inputReader.promptString("- Enter name: ", false, null);

        // Reading coordinates from terminal then compares it to null (fix after first
        // attempt)
        System.out.println("- Coordinates ");

        int x = inputReader.promptNumber("\t Enter x: ", Integer.MIN_VALUE, Integer.MAX_VALUE, Integer::parseInt, null);
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

        try {
            Flat flat = new Flat(
                    id,
                    name,
                    coordinates,
                    creationDate,
                    area,
                    numberOfRooms,
                    furnish,
                    view,
                    transport,
                    house);

            if (flat == null){ 
                System.out.println("Wrong data provided!");
                return null;
            }
                
            return flat;            
        } catch (Exception e) {
            System.out.println("There's an error while trying to add new element. Collection is broken.");
            return null;
        }
    }

    private Flat updateByArgs(String[] args, int id) {
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
            Integer x = null;
            Double y = null;
            Double area = null;
            Integer numberOfRooms = null;
            Furnish furnish = null;
            View view = null;
            Transport transport = null;
            String houseName = null;
            Integer year = null;
            Long floors = null;
            
            if(params.get("name").length() > 0)
                name = params.get("name");
            
            if(params.containsKey("x"))
                x = Integer.parseInt(params.get("x"));
            
            if(params.containsKey("y"))
                y = Double.parseDouble(params.get("y"));
            
            coordinates = new Coordinates(x, y);
            
            if(params.containsKey("area"))
                area = Double.parseDouble(params.get("area"));
            
            if(params.containsKey("numberOfRooms"))
                numberOfRooms = Integer.parseInt(params.get("numberOfRooms"));

            if(params.containsKey("furnish"))
                furnish = Furnish.valueOf(params.get("furnish").toUpperCase());
            
            if(params.containsKey("view"))
               view = View.valueOf(params.get("view").toUpperCase());
            
            if(params.containsKey("transport"))
                transport = Transport.valueOf(params.get("transport").toUpperCase());
            
            if (params.containsKey("name"))
                houseName = params.get("name");

            if(params.containsKey("year"))
                year = Integer.parseInt(params.get("year"));
                
            if(params.containsKey("floors"))
                floors = Long.parseLong(params.get("houseFloors"));
            
            house = new House(houseName, year, floors);
            var currentDate = new Date().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();

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
                    house);
        } catch (IllegalArgumentException e) {
            return null;
        }
    }
}