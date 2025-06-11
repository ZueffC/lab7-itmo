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
 
    public synchronized boolean updateFlat(int id, Flat updatedFlat, String ownerName) throws SQLException {
        if (!collection.containsKey(id))
            return false; // Проверяем, существует ли flat
        
        // Обновляем поля flat
        if (updatedFlat.getName() != null)
            collection.get(id).setName(updatedFlat.getName());
        
        // Обновляем координаты
        if (updatedFlat.getCoordinates() != null) {
            collection.get(id).getCoordinates().setX(updatedFlat.getCoordinates().getX());
            collection.get(id).getCoordinates().setY(updatedFlat.getCoordinates().getY());
        }
        
        // Обновляем остальные поля...
        
        // Сохраняем изменения в БД
        db.updateFlat(id, collection.get(id), ownerName);
        return true;
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
    
    public synchronized int addFlat(Flat flat, String nick) throws SQLException {
        if (flat == null)
            throw new IllegalArgumentException("Flat cannot be null");
        
        int generatedId = db.insertFlat(flat, nick); // Возвращает сгенерированный БД id
        if (generatedId == -1) {
            throw new SQLException("Failed to insert flat");
        }
        
        collection.put(generatedId, flat);
        return generatedId;
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
            collection.entrySet().removeIf(entry -> nick.equals(entry.getValue().getOwnerName()));
        } catch (SQLException e) {
            
        }
    }
    
    public synchronized HashMap<Integer, Flat> getAllFlats() {
        return new HashMap<>(collection);
    }
}