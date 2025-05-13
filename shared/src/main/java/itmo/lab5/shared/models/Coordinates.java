package itmo.lab5.shared.models;

import java.io.Serializable;

/**
 * Represents a point in the provided task.
 */
public class Coordinates implements Serializable {
  private static final long serialVersionUID = 1L;

  private Long x;
  private Double y; // Поле не может быть null

  public Coordinates(Long x, Double y) {
    this.x = x;
    this.y = y;
  }

  public void setX(Long x) {
    this.x = x;
  }

  public void setY(Double y) {
    this.y = y;
  }

  public Long getX() {
    return this.x;
  }

  public Double getY() {
    return this.y;
  }

  public String toString() {
    return "Point(x = " + Long.toString(x) + ", y = " + Double.toString(y) + ")";
  }
}
