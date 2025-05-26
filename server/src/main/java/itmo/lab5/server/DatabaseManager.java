package itmo.lab5.server;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.ReentrantLock;

import itmo.lab5.shared.models.Coordinates;
import itmo.lab5.shared.models.Flat;
import itmo.lab5.shared.models.House;
import itmo.lab5.shared.models.enums.Furnish;
import itmo.lab5.shared.models.enums.Transport;
import itmo.lab5.shared.models.enums.View;

public class DatabaseManager {
    private static final Map<String, DatabaseManager> instances = new HashMap<>();
    private final Connection connection;
    private final ReentrantLock lock = new ReentrantLock();
    private final String schema;

    private DatabaseManager(String url, String user, String password, String schema) throws SQLException {
        this.connection = DriverManager.getConnection(url, user, password);
        this.schema = schema;
        
        try (Statement stmt = connection.createStatement()) {
            stmt.execute("SET SCHEMA '" + schema + "'");
        } catch (SQLException e) {}
    }

    public static synchronized DatabaseManager getInstance(String url, String user, String password, String schema) throws SQLException {
        return instances.computeIfAbsent(schema, 
            k -> {
                try {
                    return new DatabaseManager(url, user, password, schema);
                } catch (SQLException e) {
                    throw new RuntimeException("Failed to create DatabaseManager for schema " + schema, e);
                }
            });
    }

    private String table(String tableName) {
        return schema + "." + tableName;
    }

    public boolean insertFlatWithDependencies(Flat flat) throws SQLException {
        lock.lock();
        try {
            connection.setAutoCommit(false);

            int coordinatesId = insertCoordinates(flat.getCoordinates());
            int houseId = insertHouse(flat.getHouse());

            String sql = "INSERT INTO " + table("Flats") + " (name, coordinates_id, creation_date, area, number_of_rooms, " +
                         "furnish, view, transport, house_id) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";

            try (PreparedStatement stmt = connection.prepareStatement(sql)) {
                stmt.setString(1, flat.getName());
                stmt.setInt(2, coordinatesId);
                stmt.setDate(3, Date.valueOf(flat.getCreationDate()));
                stmt.setDouble(4, flat.getArea());
                stmt.setInt(5, flat.getNumberOfRooms());
                stmt.setObject(6, flat.getFurnish().toString(), Types.OTHER);
                stmt.setObject(7, flat.getView() != null ? flat.getView().toString() : null, Types.OTHER);
                stmt.setObject(8, flat.getTransport().toString(), Types.OTHER);
                stmt.setObject(9, houseId);

                int rowsAffected = stmt.executeUpdate();
                if (rowsAffected > 0) {
                    connection.commit();
                    return true;
                } else {
                    connection.rollback();
                    return false;
                }
            }

        } catch (SQLException e) {
            connection.rollback();
            throw e;
        } finally {
            connection.setAutoCommit(true);
            lock.unlock();
        }
    }

    public int insertFlat(Flat flat) throws SQLException {
        lock.lock();
        try {
            int coordinatesId = insertCoordinates(flat.getCoordinates());
            int houseId = flat.getHouse() != null ? insertHouse(flat.getHouse()) : -1;

            String sql = "INSERT INTO " + table("Flats") + " (name, coordinates_id, creation_date, area, number_of_rooms, " +
                         "furnish, view, transport, house_id) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?) RETURNING id";

            try (PreparedStatement stmt = connection.prepareStatement(sql)) {
                stmt.setString(1, flat.getName());
                stmt.setInt(2, coordinatesId);
                stmt.setDate(3, Date.valueOf(flat.getCreationDate()));
                stmt.setDouble(4, flat.getArea());
                stmt.setInt(5, flat.getNumberOfRooms());
                stmt.setObject(6, flat.getFurnish().toString(), Types.OTHER);
                stmt.setObject(7, flat.getView() != null ? flat.getView().toString() : null, Types.OTHER);
                stmt.setObject(8, flat.getTransport().toString(), Types.OTHER);
                stmt.setObject(9, houseId == -1 ? null : houseId);

                ResultSet rs = stmt.executeQuery();
                if (rs.next())
                    return rs.getInt("id");
            }
        } finally {
            lock.unlock();
        }

        return -1;
    }

    private int insertCoordinates(Coordinates coordinates) throws SQLException {
        String sql = "INSERT INTO " + table("Coordinates") + " (x, y) VALUES (?, ?) RETURNING id";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setLong(1, coordinates.getX());
            stmt.setDouble(2, coordinates.getY());
            ResultSet rs = stmt.executeQuery();
            if (rs.next())
                return rs.getInt("id");
        }

        return -1;
    }

    private int insertHouse(House house) throws SQLException {
        if (house == null) return -1;

        String sql = "INSERT INTO " + table("Houses") + " (name, year, number_of_floors) VALUES (?, ?, ?) RETURNING id";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, house.getName());
            stmt.setInt(2, house.getYear());
            stmt.setLong(3, house.getNumberOfFloors());
            ResultSet rs = stmt.executeQuery();
            if (rs.next())
                return rs.getInt("id");
        }

        return -1;
    }

    public HashMap<Integer, Flat> getAllFlats() throws SQLException {
        lock.lock();
        try {
            HashMap<Integer, Flat> flatsMap = new HashMap<>();
            String sql = "SELECT * FROM " + table("Flats");

            try (Statement stmt = connection.createStatement();
                ResultSet rs = stmt.executeQuery(sql)) {

                while (rs.next()) {
                    Flat flat = new Flat();
                    int id = rs.getInt("id");
                    flat.setId(id);
                    flat.setName(rs.getString("name"));
                    flat.setCoordinates(getCoordinates(rs.getInt("coordinates_id")));
                    flat.setCreationDate(LocalDate.parse(rs.getDate("creation_date").toString()));
                    flat.setArea(rs.getDouble("area"));
                    flat.setNumberOfRooms(rs.getInt("number_of_rooms"));
                    flat.setFurnish(Furnish.valueOf(rs.getString("furnish")));
                    String viewStr = rs.getString("view");
                    flat.setView(viewStr != null ? View.valueOf(viewStr) : null);
                    flat.setTransport(Transport.valueOf(rs.getString("transport")));

                    Integer houseId = rs.getObject("house_id", Integer.class);
                    if (houseId != null)
                        flat.setHouse(getHouse(houseId));

                    flatsMap.put(id, flat);
                }
            }
            return flatsMap;
        } finally {
            lock.unlock();
        }
    }

    private Coordinates getCoordinates(int id) throws SQLException {
        String sql = "SELECT * FROM " + table("Coordinates") + " WHERE id = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next())
                    return new Coordinates(rs.getLong("x"), rs.getDouble("y"));
            }
        }

        return null;
    }

    private House getHouse(int id) throws SQLException {
        String sql = "SELECT * FROM " + table("Houses") + " WHERE id = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next())
                    return new House(
                            rs.getString("name"),
                            rs.getInt("year"),
                            rs.getLong("number_of_floors")
                    );
            }
        }

        return null;
    }

    public boolean removeFlat(int id) throws SQLException {
        lock.lock();

        try {
            String sql = "DELETE FROM " + table("Flats") + " WHERE id = ?";
            try (PreparedStatement stmt = connection.prepareStatement(sql)) {
                stmt.setInt(1, id);
                return stmt.executeUpdate() > 0;
            }
        } finally {
            lock.unlock();
        }
    }

    public boolean clearTable(String tableName) throws SQLException {
        lock.lock();
        try {
            String sql = "DELETE FROM " + table(tableName);
            try (PreparedStatement stmt = connection.prepareStatement(sql)) {
                stmt.executeUpdate();
                return true;
            }
        } finally {
            lock.unlock();
        }
    }

    public boolean truncateTable(String tableName) throws SQLException {
        lock.lock();
        try {
            // TRUNCATE is faster but may not be supported by all databases
            String sql = "TRUNCATE TABLE " + table(tableName) + " RESTART IDENTITY CASCADE";
            try (PreparedStatement stmt = connection.prepareStatement(sql)) {
                stmt.execute();
                return true;
            }
        } catch (SQLException e) {
            return clearTable(tableName);
        } finally {
            lock.unlock();
        }
    }

    private boolean usernameExists(String username) throws SQLException {
        String sql = "SELECT COUNT(*) FROM " + table("users") + " WHERE name = ?";
        
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, username);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        }
        return false;
    }

    public boolean addUser(String username, String password) throws SQLException {
        lock.lock();
        try {
            if (usernameExists(username)) {
                return false;
            }

            String salt = generateDeterministicSalt(username);
            String hashedPassword = hashPasswordWithSalt(password, salt);
            String sql = "INSERT INTO " + table("users") + " (name, password) VALUES (?, ?)";
            
            try (PreparedStatement stmt = connection.prepareStatement(sql)) {
                stmt.setString(1, username);
                stmt.setString(2, hashedPassword);
                
                int rowsAffected = stmt.executeUpdate();
                return rowsAffected > 0;
            }
        } finally {
            lock.unlock();
        }
    }

    public boolean userExists(String username, String password) throws SQLException {
        String storedHash = getPasswordHash(username);
        if (storedHash == null) {
            return false;
        }
        
        String salt = generateDeterministicSalt(username);
        String hashedInputPassword = hashPasswordWithSalt(password, salt);
        
        return storedHash.equals(hashedInputPassword);
    }

    private String getPasswordHash(String username) throws SQLException {
        String sql = "SELECT password FROM " + table("users") + " WHERE name = ?";
        
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, username);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getString("password");
                }
            }
        }
        return null;
    }

    private String generateDeterministicSalt(String username) {
        String prefix = username.length() > 0 ? 
            username.substring(0, Math.min(4, username.length())) : "";
        
        String reversed = new StringBuilder(prefix).reverse().toString();
        return reversed + username.length() + username.hashCode();
    }

    private String hashPasswordWithSalt(String password, String salt) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            String combined = salt.substring(0, Math.min(2, salt.length())) + 
                            password + 
                            salt.substring(Math.min(2, salt.length()));
            
            byte[] hash = md.digest(combined.getBytes(StandardCharsets.UTF_8));
            
            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("MD5 algorithm not found", e);
        }
    }

    public void close() throws SQLException {
        connection.close();
        instances.remove(schema);
    }
}