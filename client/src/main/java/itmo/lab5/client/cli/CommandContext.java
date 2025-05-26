package itmo.lab5.client.cli;

import java.util.HashMap;

public class CommandContext {
    private static CommandContext instance;
    private HashMap<String, Object> data = new HashMap<>();

    // Private constructor to prevent instantiation
    private CommandContext() {}

    // Static method to get the single instance of the class
    public static CommandContext getInstance() {
        if (instance == null) 
            instance = new CommandContext();

        return instance;
    }

    /**
     * Stores a value associated with the specified key in the context.
     *
     * @param key the key under which the value is to be stored
     * @param value the value to be stored in the context
     */
    public void set(String key, Object value) {
        this.data.put(key, value);
    }

    /**
     * Retrieves the value associated with the specified key from the context.
     *
     * @param name the key associated with value
     * @return the value associated with the specified key, or {@code null}
     * if the key does not exist
     */
    public Object get(String name) {
        return this.data.get(name);
    }
}
