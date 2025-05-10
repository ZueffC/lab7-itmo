package itmo.lab5.shared.models;

import java.io.Serializable;
import itmo.lab5.shared.models.enums.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * Represents a flat, provided by se.ifmo.ru (no 462025)
 */
public class Flat implements Comparable<Flat>, Serializable {
  private Integer id; // Значение поля должно быть больше 0, Значение этого поля должно быть
                  // уникальным, Значение этого поля должно генерироваться автоматически
  private String name; // Поле не может быть null, Строка не может быть пустой
  private Coordinates coordinates; // Поле не может быть null
  private String creationDateString; // Поле не может быть null, Значение этого поля должно генерироваться автоматически
  private Double area; // Максимальное значение поля: 626, Значение поля должно быть больше 0
  private int numberOfRooms; // Значение поля должно быть больше 0
  private Furnish furnish; // Поле не может быть null
  private View view; // Поле может быть null
  private Transport transport; // Поле не может быть null
  private House house; // Поле может быть null
  private static final long serialVersionUID = 1L;
  /**
   * Constructs a Flat object with the specified attributes.
   *
   * @param id            the unique identifier of the flat (must be greater than
   *                      0).
   * @param name          the name of the flat (must not be null or empty).
   * @param coordinates   the coordinates of the flat (must not be null).
   * @param creationDate  the creation date of the flat (must not be null).
   * @param area          the area of the flat (must be greater than 0 and less
   *                      than or equal to 626).
   * @param numberOfRooms the number of rooms in the flat (must be greater than
   *                      0).
   * @param furnish       the furnish type of the flat (must not be null).
   * @param view          the view from the flat (can be null).
   * @param transport     the transport type associated with the flat (must not be
   *                      null).
   * @param house         the house details associated with the flat (can be
   *                      null).
   */
  public Flat(int id, String name, Coordinates coordinates, LocalDate creationDate, Double area,
      int numberOfRooms, Furnish furnish, View view, Transport transport, House house) {
    this.id = id;
    this.name = name;
    this.coordinates = coordinates;
    this.creationDateString = creationDate.format(DateTimeFormatter.ISO_DATE);
    this.area = area;
    this.numberOfRooms = numberOfRooms;
    this.furnish = furnish;
    this.view = view;
    this.transport = transport;
    this.house = house;
  }

  /**
   * Default constructor for creating an empty Flat object.
   */
  public Flat() {

  }

  /**
   * Gets the unique identifier of the flat.
   *
   * @return the id of the flat.
   */
  public int getId() {
    return id;
  }

  /**
   * Sets the unique identifier of the flat.
   *
   * @param id the new id of the flat (must be greater than 0).
   */
  public void setId(int id) {
    this.id = id;
  }

  /**
   * Gets the name of the flat.
   *
   * @return the name of the flat.
   */
  public String getName() {
    return name;
  }

  /**
   * Sets the name of the flat.
   *
   * @param name the new name of the flat (must not be null or empty).
   */
  public void setName(String name) {
    this.name = name;
  }

  /**
   * Gets the coordinates of the flat.
   *
   * @return the coordinates of the flat (must not be null).
   */
  public Coordinates getCoordinates() {
    return coordinates;
  }

  /**
   * Sets the coordinates of the flat.
   *
   * @param coordinates the new coordinates of the flat (must not be null).
   */
  public void setCoordinates(Coordinates coordinates) {
    this.coordinates = coordinates;
  }

  /**
   * Gets the creation date of the flat.
   *
   * @return the creation date of the flat (must not be null).
   */
  public LocalDate getCreationDate() {
    return LocalDate.parse(creationDateString);
  }

  /**
   * Sets the creation date of the flat.
   *
   * @param creationDate the new creation date of the flat (must not be null).
   */
  public void setCreationDate(LocalDate creationDate) {
    this.creationDateString = creationDate.format(DateTimeFormatter.ISO_DATE);
  }

  /**
   * Gets the area of the flat.
   *
   * @return the area of the flat (must be greater than 0 and less than or equal
   *         to 626).
   */
  public Double getArea() {
    return area;
  }

  /**
   * Sets the area of the flat.
   *
   * @param area the new area of the flat (must be greater than 0 and less than or
   *             equal to 626).
   */
  public void setArea(Double area) {
    this.area = area;
  }

  /**
   * Gets the number of rooms in the flat.
   *
   * @return the number of rooms in the flat (must be greater than 0).
   */
  public int getNumberOfRooms() {
    return numberOfRooms;
  }

  /**
   * Sets the number of rooms in the flat.
   *
   * @param numberOfRooms the new number of rooms in the flat (must be greater
   *                      than 0).
   */
  public void setNumberOfRooms(int numberOfRooms) {
    this.numberOfRooms = numberOfRooms;
  }

  /**
   * Gets the furnish type of the flat.
   *
   * @return the furnish type of the flat (must not be null).
   */
  public Furnish getFurnish() {
    return furnish;
  }

  /**
   * Sets the furnish type of the flat.
   *
   * @param furnish the new furnish type of the flat (must not be null).
   */
  public void setFurnish(Furnish furnish) {
    this.furnish = furnish;
  }

  /**
   * Gets the view from the flat.
   *
   * @return the view from the flat (can be null).
   */
  public View getView() {
    return view;
  }

  /**
   * Sets the view from the flat.
   *
   * @param view the new view from the flat (can be null).
   */
  public void setView(View view) {
    this.view = view;
  }

  /**
   * Gets the transport type associated with the flat.
   *
   * @return the transport type associated with this class
   */
  public Transport getTransport() {
    return transport;
  }

  /**
   * Sets the transport associated with this flat.
   *
   * @param transport the transport to be set
   */
  public void setTransport(Transport transport) {
    this.transport = transport;
  }

  /**
   * Returns the house associated with this flat.
   *
   * @return the house
   */
  public House getHouse() {
    return house;
  }

  /**
   * Sets the house associated with this flat.
   *
   * @param house the house to be set
   */
  public void setHouse(House house) {
    this.house = house;
  }

  /**
   * Returns a string representation of the flat, including its ID, name,
   * coordinates, creation date, area, number of rooms, furnishing status,
   * view, transport, and house.
   *
   * @return a string representation of the flat
   */
  @Override
  public String toString() {
    var dateFormat = creationDateString;
    var builder = new StringBuilder();

    builder
        .append("Flat:\n")
        .append("  ID: ").append(id).append("\n")
        .append("  Name: ").append(name).append("\n")
        .append("  Coordinates: ").append(coordinates != null ? coordinates.toString() : "null").append("\n")
        .append("  Creation Date: ").append(dateFormat).append("\n")
        .append("  Area: ").append(area).append("\n")
        .append("  Number Of Rooms: ").append(numberOfRooms).append("\n")
        .append("  Furnish: ").append(furnish).append("\n")
        .append("  View: ").append(view != null ? view : "null").append("\n")
        .append("  Transport: ").append(transport).append("\n")
        .append("  House: ").append(house != null ? house.toString() : "null").append("\n");

    return builder.toString();
  }

  @Override
  public int compareTo(Flat o) {
    return Integer.compare(this.id, o.id);
  }
}