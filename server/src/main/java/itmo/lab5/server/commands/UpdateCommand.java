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

        if (newFlat.getName() != null && newFlat.getName().length() > 0)
            oldFlat.setName(newFlat.getName());

        if (newFlat.getCoordinates() != null && newFlat.getCoordinates().getX() != null)
            oldFlat.setCoordinates(newFlat.getCoordinates());

        if (newFlat.getArea() != null && newFlat.getArea() > 0 && newFlat.getArea() <= 626)
            oldFlat.setArea(newFlat.getArea());

        if (newFlat.getNumberOfRooms() != null && newFlat.getNumberOfRooms() > 0)
            oldFlat.setNumberOfRooms(newFlat.getNumberOfRooms());

        if (newFlat.getFurnish() != null)
            oldFlat.setFurnish(newFlat.getFurnish());

        if (newFlat.getTransport() != null)
            oldFlat.setTransport(newFlat.getTransport());

        oldFlat.setView(newFlat.getView());

        var newHouse = newFlat.getHouse();
        if (newHouse.getName() != null)
            oldFlat.getHouse().setName(newHouse.getName());

        if (newHouse.getNumberOfFloors() != null && newHouse.getNumberOfFloors() > 0
                && newHouse.getNumberOfFloors() <= 77)
            oldFlat.getHouse().setNumberOfFloors(newHouse.getNumberOfFloors());

        if (newHouse.getYear() != null && newHouse.getYear() > 0 && newHouse.getYear() <= 959)
            oldFlat.getHouse().setYear(newHouse.getYear());

        return "Flat has been successfully updated!";
    }
}
