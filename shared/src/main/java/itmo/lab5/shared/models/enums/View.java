package itmo.lab5.shared.models.enums;

import java.io.Serializable;

public enum View implements Serializable {
    STREET(0),
    PARK(1),
    NORMAL(2),
    GOOD(3);

    private final int value;

    View(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    public static View fromValue(int value) {
        for (View view : View.values()) {
            if (view.getValue() == value) {
                return view;
            }
        }
        return null;
    }
}