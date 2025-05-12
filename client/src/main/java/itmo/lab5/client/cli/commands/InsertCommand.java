package itmo.lab5.client.cli.commands;

import itmo.lab5.client.cli.CommandContext;
import itmo.lab5.client.interfaces.Command;
import itmo.lab5.client.net.RequestSender;
import itmo.lab5.shared.CommandType;
import itmo.lab5.shared.models.*;
import itmo.lab5.shared.models.enums.*;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Scanner;

/**
 * This class implements the Command interface and provides
 * functionality to insert a new Flat object into the collection.
 * 
 * This command can be executed in two modes: interactively, where the user is
 * prompted for input, or with command-line arguments, where parameters are
 * passed as key-value pairs. The command gathers all necessary information to
 * create a new flat and adds it to the collection.
 * 
 */
public class InsertCommand implements Command {
    private final Scanner scanner = new Scanner(System.in);
    private final ReaderUtil inputReader = new ReaderUtil(scanner);

    /**
     * Executes the insert command, either interactively or with provided
     * arguments.
     *
     * @param args    an array of arguments passed to the command; if empty, the
     *                command will execute interactively
     * @param context the command context that contains the collection of flats
     * @return a message indicating the result of the operation, or an error message
     *         if the collection cannot be parsed
     */
    @Override
    public String execute(String[] args, CommandContext context) {
        if (args.length == 0)
            return executeInteractive(context);

        return executeWithArgs(args, context);
    }

    /**
     * Executes the insert command interactively, prompting the user for input
     * to create a new flat.
     *
     * @param context the command context that contains the collection of flats
     * @return a message indicating the result of the operation
     */
    private String executeInteractive(CommandContext context) {
        var creationDate = LocalDate.now();
        String name = inputReader.promptString("- Enter name: ", false, null);

        System.out.println("- Coordinates ");
        Long x = inputReader.promptNumber("\t Enter x: ", Long.MIN_VALUE, Long.MAX_VALUE, Long::parseLong, null);
        Double y = inputReader.promptNumber("\t Enter y: ", Double.MIN_VALUE, Double.MAX_VALUE, Double::parseDouble,
                null);
        var coordinates = new Coordinates(x, y);

        Double area = inputReader.promptNumber("\t Enter square: ", 0.0, 626.0, Double::parseDouble, null);
        int numberOfRooms = inputReader.promptNumber("\t Enter rooms count: ", 1, Integer.MAX_VALUE, Integer::parseInt,
                null);

        System.out.println("- Furnish");
        Furnish furnish = inputReader.promptEnum("\t Enter furnish type: ", Furnish.class, null);

        System.out.println("- View");
        View view = inputReader.promptEnumNullable("\t Enter view type: ", View.class, null);

        System.out.println("- Transport");
        Transport transport = inputReader.promptEnum("\t Enter transport type: ", Transport.class, null);

        System.out.println("- House");
        System.out.print("\t Enter house name: ");
        String houseName = scanner.nextLine().trim();

        House house = null;

        if (!houseName.isEmpty()) {
            int year = inputReader.promptNumber("\t Enter house age: ", 1, 959, Integer::parseInt, null);
            long floors = inputReader.promptNumber("\t Enter house floors count: ", 1L, 77L, Long::parseLong, null);
            house = new House(houseName, year, floors);
        }

        Flat flat = new Flat(
                -1,
                name,
                coordinates,
                creationDate,
                area,
                numberOfRooms,
                furnish,
                view,
                transport,
                house);

        return RequestSender.getInstance().sendRequest(CommandType.INSERT, -1, flat);
    }

    /**
     * Executes the insert command with provided arguments, creating a new flat
     * based on the key-value pairs.
     *
     * @param args    an array of arguments passed to the command
     * @param context the command context that contains the collection of flats
     * @return a message indicating the result of the operation
     */
    private String executeWithArgs(String[] args, CommandContext context) {
        HashMap<String, String> params = new HashMap<>();

        for (String arg : args) {
            String[] parts = arg.split("=", 2);

            if (parts.length == 2)
                params.put(parts[0], parts[1]);
        }

        var creationDate = LocalDate.now();
        String name = params.containsKey("name") ? params.get("name")
                : inputReader.promptString("- Enter name: ", false, null);

        System.out.println("- Coordinates ");
        var x = params.containsKey("x") ? Long.parseLong(params.get("x"))
                : inputReader.promptNumber("\t Enter x: ", Long.MIN_VALUE, Long.MAX_VALUE, Long::parseLong,
                        null);
        var y = params.containsKey("y") ? Double.parseDouble(params.get("y"))
                : inputReader.promptNumber("\t Enter y: ", Double.MIN_VALUE, Double.MAX_VALUE, Double::parseDouble,
                        null);
        var coordinates = new Coordinates(x, y);

        Double area = params.containsKey("area") ? Double.parseDouble(params.get("area"))
                : inputReader.promptNumber("\t Enter square: ", 0.0, 626.0, Double::parseDouble, null);
        int numberOfRooms = params.containsKey("numberOfRooms") ? Integer.parseInt(params.get("numberOfRooms"))
                : inputReader.promptNumber("\t Enter rooms count: ", 1, Integer.MAX_VALUE, Integer::parseInt, null);

        System.out.println("- Furnish");
        Furnish furnish = null;
        if (params.containsKey("furnish")) {
            String value = params.get("furnish").toUpperCase();
            while (!isValidEnumValue(Furnish.class, value)) {
                System.out.println("Invalid furnish value: " + value + ". Please enter a valid furnish type.");
                var t_furnish = inputReader.promptEnum("\t Enter furnish type: ", Furnish.class, null);
                value = t_furnish.toString();
            }
            furnish = (value.equalsIgnoreCase("NONE")) ? null : Furnish.valueOf(value);
        } else {
            furnish = inputReader.promptEnum("\t Enter furnish type: ", Furnish.class, null);
        }

        System.out.println("- View");
        View view = null;
        if (params.containsKey("view")) {
            String value = params.get("view").toUpperCase();
            while (!isValidEnumValue(View.class, value)) {
                System.out.println("Invalid view value: " + value + ". Please enter a valid view type.");
                var t_view = (View) inputReader.promptEnum("\t Enter furnish type: ", View.class, null);
                value = t_view.toString();
            }
            view = (value.equalsIgnoreCase("NONE")) ? null : View.valueOf(value);
        } else {
            view = inputReader.promptEnumNullable("\t Enter view type: ", View.class, null);
        }

        System.out.println("- Transport");
        Transport transport = null;
        if (params.containsKey("transport")) {
            String value = params.get("transport").toUpperCase();
            while (!isValidEnumValue(Transport.class, value)) {
                System.out.println("Invalid transport value: " + value + ". Please enter a valid transport type.");
                var t_trapsport = inputReader.promptEnum("\t Enter transport type: ", Transport.class, null);
                value = t_trapsport.toString();
            }
            transport = (value.equalsIgnoreCase("NONE")) ? null : Transport.valueOf(value);
        } else {
            transport = inputReader.promptEnum("\t Enter transport type: ", Transport.class, null);
        }

        System.out.println("- House");
        String houseName = params.getOrDefault("houseName", "");
        if (houseName.isEmpty()) {
            System.out.print("\t Enter house name: ");
            houseName = scanner.nextLine().trim();
        }

        House house = null;

        if (!houseName.isEmpty()) {
            int year = params.containsKey("houseYear") ? Integer.parseInt(params.get("houseYear"))
                    : inputReader.promptNumber("\t Enter house age: ", 1, 959, Integer::parseInt, null);
            long floors = params.containsKey("houseFloors") ? Long.parseLong(params.get("houseFloors"))
                    : inputReader.promptNumber("\t Enter house floors count: ", 1L, 77L, Long::parseLong, null);
            house = new House(houseName, year, floors);
        }

        Flat flat = new Flat(
                -1,
                name,
                coordinates,
                creationDate,
                area,
                numberOfRooms,
                furnish,
                view,
                transport,
                house);

        return RequestSender.getInstance().sendRequest(CommandType.INSERT, -1, flat);
    }

    public static <E extends Enum<E>> boolean isValidEnumValue(Class<E> enumClass, String value) {
        if (value == null)
            return false;

        for (E constant : enumClass.getEnumConstants()) {
            if (constant.name().equals(value))
                return true;
        }
        return false;
    }

    private final static String description = "allows to insert new flat to collection";

    @Override
    public String toString() {
        return InsertCommand.description;
    }
}
