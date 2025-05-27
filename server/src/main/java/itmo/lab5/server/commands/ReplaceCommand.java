package itmo.lab5.server.commands;

import itmo.lab5.server.Collection;
import itmo.lab5.shared.models.Flat;
import itmo.lab5.shared.models.House;

public class ReplaceCommand {
    public static String execute(Integer id, Flat newFlat, Collection container, String nick) {
        var collection = container.getAllFlats();

        if (collection == null || collection.isEmpty())
            return "Collection is empty!";

        if (newFlat == null)
            return "New flat is null!";

        int key = Math.abs(id);
        Flat oldFlat = collection.get(key);

        if (oldFlat == null)
            return "There's no element with ID = " + key;

        boolean replaced = false;
        boolean isNegativeId = id < 0;

        if (isNegativeId)
            replaced = ifLower(oldFlat, newFlat); 
        else
            replaced = ifGreater(oldFlat, newFlat);

        if (replaced) {
            if(oldFlat.getOwnerName() == null ? newFlat.getOwnerName() != null : !oldFlat.getOwnerName().equals(newFlat.getOwnerName()))
                return "Can't update someone else data!";

            collection.put(key, oldFlat);
            return "Element with ID = " + key + " has been updated.";
        } else {
            return "Replacement condition not met. No changes made.";
        }
    }

    private static boolean ifLower(Flat oldFlat, Flat newFlat) {
        boolean replaced = false;

        if (newFlat.getName() != null && oldFlat.getName() != null &&
            newFlat.getName().length() < oldFlat.getName().length()) {
            oldFlat.setName(newFlat.getName());
            replaced = true;
        }

        if (newFlat.getCoordinates() != null && oldFlat.getCoordinates() != null) {
            Long newX = newFlat.getCoordinates().getX();
            if (newX != null && newX < oldFlat.getCoordinates().getX()) {
                oldFlat.getCoordinates().setX(newX);
                replaced = true;
            }

            Double newY = newFlat.getCoordinates().getY();
            if (newY != null && newY < oldFlat.getCoordinates().getY()) {
                oldFlat.getCoordinates().setY(newY);
                replaced = true;
            }
        }

        if (newFlat.getArea() != null && newFlat.getArea() > 0 && newFlat.getArea() <= 626 &&
            newFlat.getArea() < oldFlat.getArea()) {
            oldFlat.setArea(newFlat.getArea());
            replaced = true;
        }

        if (newFlat.getNumberOfRooms() != null && newFlat.getNumberOfRooms() > 0 &&
            newFlat.getNumberOfRooms() < oldFlat.getNumberOfRooms()) {
            oldFlat.setNumberOfRooms(newFlat.getNumberOfRooms());
            replaced = true;
        }

        if (newFlat.getFurnish() != null && newFlat.getFurnish().ordinal() < oldFlat.getFurnish().ordinal()) {
            oldFlat.setFurnish(newFlat.getFurnish());
            replaced = true;
        }

        if (newFlat.getView() != null && newFlat.getView().ordinal() < oldFlat.getView().ordinal()) {
            oldFlat.setView(newFlat.getView());
            replaced = true;
        }

        if (newFlat.getTransport() != null && newFlat.getTransport().ordinal() < oldFlat.getTransport().ordinal()) {
            oldFlat.setTransport(newFlat.getTransport());
            replaced = true;
        }

        if (newFlat.getHouse() != null && oldFlat.getHouse() != null) {
            House newHouse = newFlat.getHouse();
            House oldHouse = oldFlat.getHouse();

            if (newHouse.getName() != null && oldHouse.getName() != null &&
                newHouse.getName().length() < oldHouse.getName().length()) {
                oldHouse.setName(newHouse.getName());
                replaced = true;
            }

            if (newHouse.getYear() != null && newHouse.getYear() > 0 && newHouse.getYear() <= 959 &&
                newHouse.getYear() < oldHouse.getYear()) {
                oldHouse.setYear(newHouse.getYear());
                replaced = true;
            }

            if (newHouse.getNumberOfFloors() != null && newHouse.getNumberOfFloors() > 0 && newHouse.getNumberOfFloors() <= 77 &&
                newHouse.getNumberOfFloors() < oldHouse.getNumberOfFloors()) {
                oldHouse.setNumberOfFloors(newHouse.getNumberOfFloors());
                replaced = true;
            }
        }

        return replaced;
    }

    private static boolean ifGreater(Flat oldFlat, Flat newFlat) {
        boolean replaced = false;

        if (newFlat.getName() != null && oldFlat.getName() != null &&
            newFlat.getName().length() > oldFlat.getName().length()) {
            oldFlat.setName(newFlat.getName());
            replaced = true;
        }

        if (newFlat.getCoordinates() != null && oldFlat.getCoordinates() != null) {
            Long newX = newFlat.getCoordinates().getX();
            if (newX != null && newX > oldFlat.getCoordinates().getX()) {
                oldFlat.getCoordinates().setX(newX);
                replaced = true;
            }

            Double newY = newFlat.getCoordinates().getY();
            if (newY != null && newY > oldFlat.getCoordinates().getY()) {
                oldFlat.getCoordinates().setY(newY);
                replaced = true;
            }
        }

        if (newFlat.getArea() != null && newFlat.getArea() > 0 && newFlat.getArea() <= 626 &&
            newFlat.getArea() > oldFlat.getArea()) {
            oldFlat.setArea(newFlat.getArea());
            replaced = true;
        }

        if (newFlat.getNumberOfRooms() != null && newFlat.getNumberOfRooms() > 0 &&
            newFlat.getNumberOfRooms() > oldFlat.getNumberOfRooms()) {
            oldFlat.setNumberOfRooms(newFlat.getNumberOfRooms());
            replaced = true;
        }

        if (newFlat.getFurnish() != null && newFlat.getFurnish().ordinal() > oldFlat.getFurnish().ordinal()) {
            oldFlat.setFurnish(newFlat.getFurnish());
            replaced = true;
        }

        if (newFlat.getView() != null && newFlat.getView().ordinal() > oldFlat.getView().ordinal()) {
            oldFlat.setView(newFlat.getView());
            replaced = true;
        }

        if (newFlat.getTransport() != null && newFlat.getTransport().ordinal() > oldFlat.getTransport().ordinal()) {
            oldFlat.setTransport(newFlat.getTransport());
            replaced = true;
        }

        if (newFlat.getHouse() != null && oldFlat.getHouse() != null) {
            House newHouse = newFlat.getHouse();
            House oldHouse = oldFlat.getHouse();

            if (newHouse.getName() != null && oldHouse.getName() != null &&
                newHouse.getName().length() > oldHouse.getName().length()) {
                oldHouse.setName(newHouse.getName());
                replaced = true;
            }

            if (newHouse.getYear() != null && newHouse.getYear() > 0 && newHouse.getYear() <= 959 &&
                newHouse.getYear() > oldHouse.getYear()) {
                oldHouse.setYear(newHouse.getYear());
                replaced = true;
            }

            if (newHouse.getNumberOfFloors() != null && newHouse.getNumberOfFloors() > 0 && newHouse.getNumberOfFloors() <= 77 &&
                newHouse.getNumberOfFloors() > oldHouse.getNumberOfFloors()) {
                oldHouse.setNumberOfFloors(newHouse.getNumberOfFloors());
                replaced = true;
            }
        }

        return replaced;
    }
}