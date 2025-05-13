package itmo.lab5.server.io;

import itmo.lab5.shared.models.*;
import java.io.*;
import java.util.HashMap;

public class Writer {
  public String writeCollection(String filename, HashMap<Integer, Flat> flats) {
    try (FileOutputStream fos = new FileOutputStream(filename);
        OutputStreamWriter osw = new OutputStreamWriter(fos);
        BufferedWriter writer = new BufferedWriter(osw)) {

      flats = FlatComparatorFactory.sortFlats(
          flats,
          FlatComparatorFactory.SortField.ID);

      for (Flat flat : flats.values()) {
        String date = "";

        try {
          date = flat.getCreationDate().toString();
        } catch (Exception e) {
          System.out.println(e.getMessage());
        }

        String line = String.format("%d,%s,%d,%s,%s,%s,%d,%s,%s,%s,%s,%d,%d\n",
            flat.getId(),
            flat.getName(),
            flat.getCoordinates().getX(),
            flat.getCoordinates().getY().toString().replace(",", "."),
            date,
            flat.getArea().toString().replace(",", "."),
            flat.getNumberOfRooms(),
            flat.getFurnish(),
            flat.getView() != null ? flat.getView() : "",
            flat.getTransport(),
            flat.getHouse() != null ? flat.getHouse().getName() : "",
            flat.getHouse() != null ? flat.getHouse().getYear() : 0,
            flat.getHouse() != null ? flat.getHouse().getNumberOfFloors() : 0);

        writer.write(line);
      }

      return "Collection has been saved to the file successfully.";
    } catch (IOException e) {
      e.printStackTrace();
      return "An error occurred while saving the collection to file.";
    }
  }
}