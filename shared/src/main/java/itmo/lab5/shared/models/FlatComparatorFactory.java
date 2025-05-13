package itmo.lab5.shared.models;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * The FlatComparatorFactory class provides functionality to create comparators 
 * for sorting Flat objects based on various fields such as ID, name, area, 
 * number of rooms, and creation date.
 */
public class FlatComparatorFactory {
    public enum SortField {
        ID,
        NAME,
        AREA,
        NUMBER_OF_ROOMS,
        CREATION_DATE
    }

    /**
     * Returns a Comparator for Flat objects based on the specified sort field.
     *
     * @param field the field by which to sort the Flat objects
     * @return a Comparator for Flat objects
     * @throws IllegalArgumentException if the specified sort field is unknown
     */
    public static Comparator<Flat> getComparator(SortField field) {
        switch (field) {
            case ID:
                return Comparator.comparingInt(Flat::getId);
            case NAME:
                return Comparator.comparing(Flat::getName);
            case AREA:
                return Comparator.comparingDouble(Flat::getArea);
            case NUMBER_OF_ROOMS:
                return Comparator.comparingInt(Flat::getNumberOfRooms);
            case CREATION_DATE:
                return Comparator.comparing(Flat::getCreationDate);
            default:
                throw new IllegalArgumentException("Unknown sort field: " + field);
        }
    }
    
    /**
     * Sorts a HashMap of Flat objects based on the specified sort field and 
     * returns a new sorted HashMap.
     *
     * @param flatsMap the HashMap of Flat objects to be sorted
     * @param field the field by which to sort the Flat objects
     * @return a new HashMap containing the sorted Flat objects
     */
    public static HashMap<Integer, Flat> sortFlats(HashMap<Integer, Flat> flatsMap, SortField field) {
        List<Flat> flatList = new ArrayList<>(flatsMap.values());
        Collections.sort(flatList, getComparator(field));


        var sortedMap = new HashMap<Integer, Flat>();
        for (Flat flat : flatList) {
            sortedMap.put(flat.getId(), flat);
        }
        return sortedMap;
    }
}