package itmo.lab5.client.cli.commands;

import java.util.Arrays;
import java.util.Scanner;

/**
 * Utility class for reading user input with various prompt methods.
 */
public class ReaderUtil {
  private final Scanner scanner;

  /**
   * Constructs a ReaderUtil with the specified Scanner for input.
   *
   * @param scanner the Scanner to read user input.
   */
  public ReaderUtil(Scanner scanner) {
    this.scanner = scanner;
  }

  /**
   * Prompts the user for a string input, allowing for an optional old value.
   *
   * @param message    the message to display to the user.
   * @param allowEmpty whether to allow empty input.
   * @param oldValue   the previous value to display as a hint.
   * @return the user input or the old value if input is empty.
   */
  public String promptString(String message, boolean allowEmpty, String oldValue) {
    while (true) {
      System.out.print(message);
      if (oldValue != null)
        System.out.print(" (previously: " + oldValue + "): ");

      String input = scanner.nextLine().trim();

      if (input.isEmpty() && input.length() > 0)
        return oldValue;

      if (!input.isEmpty() || allowEmpty)
        return input;

      System.out.println("\t String can't be empty ");
    }
  }

  /**
   * Prompts the user to select an enum value from the specified options.
   *
   * @param message   the message to display to the user.
   * @param enumClass the class of the enum type.
   * @param oldValue  the previous value to display as a hint.
   * @param <T>       the type of the enum.
   * @return the selected enum value or the old value if input is empty.
   */
  public <T extends Enum<T>> T promptEnum(String message, Class<T> enumClass, T oldValue) {
    while (true) {
      if (oldValue != null)
        System.out.println("Now it's: " + oldValue + ". ");

      System.out.print(
          message + "(options: " +
              String.join(", ", Arrays.stream(enumClass.getEnumConstants())
                  .map(Enum::name).toList())
              + "): ");

      String input = scanner.nextLine().trim().toUpperCase();

      try {
        while (!isValidEnumValue(enumClass, input) || input.isEmpty()) {
          System.out.print("\t Wrong input! Retry: ");
          input = scanner.nextLine().trim().toUpperCase();
        }

        return Enum.valueOf(enumClass, input.toUpperCase());
      } catch (IllegalArgumentException e) {
        System.out.println("Invalid name; Please, try again: ");
      }
    }
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

  /**
   * Prompts the user to select an enum value, allowing for a nullable option.
   *
   * @param message   the message to display to the user.
   * @param enumClass the class of the enum type.
   * @param oldValue  the previous value to display as a hint.
   * @param <T>       the type of the enum.
   * @return the selected enum value or the old value if input is empty.
   */
  public <T extends Enum<T>> T promptEnumNullable(String message, Class<T> enumClass, T oldValue) {
    while (true) {
      if (oldValue != null)
        System.out.println("Now it's: " + oldValue + ". ");

      System.out.print(
          message + "(options: " +
              String.join(", ", Arrays.stream(enumClass.getEnumConstants())
                  .map(Enum::name).toList())
              + "): ");

      String input = scanner.nextLine().trim();

      if (input.isEmpty())
        return oldValue;

      try {
        return Enum.valueOf(enumClass, input.toUpperCase());
      } catch (IllegalArgumentException e) {
        System.out.println("Invalid name; Please, try again: ");
      }
    }
  }

  /**
   * Prompts the user for a number input within specified bounds.
   *
   * @param message  the message to display to the user.
   * @param min      the minimum acceptable value.
   * @param max      the maximum acceptable value.
   * @param parser   the parser to convert input to the desired type.
   * @param oldValue the previous value to display as a hint.
   * @param <T>      the type of the number, which must be comparable.
   * @return the user input as a number or the old value if input is empty.
   */
  public <T extends Comparable<T>> T promptNumber(String message, T min, T max, Parser<T> parser, T oldValue) {
    while (true) {
      System.out.print(message + " (previosly: " + oldValue + ", might be from " + min + " and up to: " + max + "): ");
      String input = scanner.nextLine().trim();

      if (input.isEmpty())
        if (oldValue != null)
          return oldValue;

      try {
        T value = parser.parse(input);
        if (value.compareTo(min) >= 0 && value.compareTo(max) <= 0)
          return value;
      } catch (Exception e) {

      }

      System.out.println("\t Invalid value; Please, try again: ");
    }
  }

  public interface Parser<T> {
    T parse(String input) throws Exception;
  }
}