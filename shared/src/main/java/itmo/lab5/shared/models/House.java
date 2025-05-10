package itmo.lab5.shared.models;

import java.io.Serializable;

/**
 * Represents a house with a name, year of construction, and number of floors.
 * The name cannot be null, the year must be greater than 0 and less than or equal to 959,
 * and the number of floors must be greater than 0 and less than or equal to 77.
 */
public class House implements Serializable {
  private String name; // Поле не может быть null
  private int year; // Максимальное значение поля: 959, Значение поля должно быть больше 0
  private long numberOfFloors; // Максимальное значение поля: 77, Значение поля должно быть больше 0
  private static final long serialVersionUID = 1L;

 /**
  * Constructs a House with the specified name, year, and number of floors.
  *
  * @param name the name of the house, must not be null
  * @param year the year of construction, must be greater than 0 and less
  * than or equal to 959
  * @param numberOfFloors the number of floors, must be greater than 0 and
  * less than or equal to 77
  */
  public House(String name, int year, long numberOfFloors) {
    this.name = name;
    this.year = year;
    this.numberOfFloors = numberOfFloors;
  }

 /**
  * Returns the name of the house.
  *
  * @return the name of the house
  */
  public String getName() {
    return this.name;
  }

 /**
  * Returns the year of construction of the house.
  *
  * @return the year of construction
  */
  public int getYear() {
    return this.year;
  }

 /**
  * Returns the number of floors in the house.
  *
  * @return the number of floors
  */
  public long getNumberOfFloors() {
    return this.numberOfFloors;
  }

 /**
  * Returns a string representation of the house, including its name, year of construction,
  * and number of floors.
  *
  * @return a string representation of the house
  */
  public String toString() {
    return "House(" +
        "name = '" + name +
        "', year = " + Integer.toString(year) +
        ", numberOfFloors = " + Long.toString(numberOfFloors) +
        ")";
  }
}