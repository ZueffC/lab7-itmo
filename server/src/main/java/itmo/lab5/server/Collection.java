package itmo.lab5.server;

import java.sql.SQLException;
import java.util.HashMap;

import itmo.lab5.shared.models.Flat;

public class Collection {
    private static volatile Collection instance;
    private static HashMap<Integer, Flat> collection = new HashMap<>();
    private static DatabaseManager db = null;
    
    private Collection(DatabaseManager dbManager) {
        Collection.db = dbManager;
    }
    
    public static Collection getInstance(DatabaseManager dbManager) throws SQLException {
        if (instance == null)
            synchronized(Collection.class) {
                if (instance == null)
                    instance = new Collection(dbManager);
            }

        try {
            collection = Collection.db.getAllFlats();
        } catch (SQLException e) {
            throw e;
        }

        return instance;
    }
    
    public synchronized void addFlat(int id, Flat flat, String nick) {
        if (flat == null)
            throw new IllegalArgumentException("Flat cannot be null");
        
        try {
            db.insertFlat(flat, nick);
        } catch (SQLException e) {
            return;
        }
    
        collection.put(id, flat);
    }
    
    public synchronized Flat removeFlat(int id, String nick) {
        try {
            db.removeFlat(id, nick);
        } catch (SQLException e) {
            return null;
        }

        return collection.remove(id);
    }
    
    public synchronized Flat getFlat(int id) {
        return collection.get(id);
    }
    
    public synchronized boolean containsFlat(int id) {
        return collection.containsKey(id);
    }
    
    public synchronized int size() {
        return collection.size();
    }
    
    public synchronized void clear(String nick) {
        try {
            db.clearFlatsByOwner(nick);
        } catch(SQLException e) {
            return;
        }

        collection.clear();
    }
    
    public synchronized HashMap<Integer, Flat> getAllFlats() {
        return new HashMap<>(collection);
    }
}