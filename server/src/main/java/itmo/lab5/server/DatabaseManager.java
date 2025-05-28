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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import itmo.lab5.shared.models.Coordinates;
import itmo.lab5.shared.models.Flat;
import itmo.lab5.shared.models.House;
import itmo.lab5.shared.models.enums.Furnish;
import itmo.lab5.shared.models.enums.Transport; // Добавим импорт для логирования
import itmo.lab5.shared.models.enums.View; // Добавим импорт для логирования

public class DatabaseManager {
    private static final Map<String, DatabaseManager> instances = new HashMap<>();
    private final Connection connection;
    private final ReentrantLock lock = new ReentrantLock();
    private final String schema;
    private static final Logger logger = LoggerFactory.getLogger(DatabaseManager.class); // Инициализация логгера

    private DatabaseManager(String url, String user, String password, String schema) throws SQLException {
        this.connection = DriverManager.getConnection(url, user, password);
        this.schema = schema;
        
        try (Statement stmt = connection.createStatement()) {
            // Попытка установить схему, игнорируем, если уже установлена или не поддерживается
            stmt.execute("SET SCHEMA '" + schema + "'");
        } catch (SQLException e) {
             logger.warn("Warning: Could not set schema to '{}': {}", schema, e.getMessage());
        }

        // Вызываем новые методы для создания ENUM типов и таблиц, если они не существуют
        createEnumTypesIfNotExist();
        createTablesIfNotExist();
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

    /**
     * Проверяет существование и создает необходимые ENUM типы в базе данных, если они отсутствуют.
     * Использует блокировку для обеспечения потокобезопасности при запуске.
     * @throws SQLException Если произошла ошибка при создании типов ENUM.
     */
    private void createEnumTypesIfNotExist() throws SQLException {
        lock.lock(); // Блокировка для предотвращения одновременного создания ENUM
        try (Statement stmt = connection.createStatement()) {
            logger.info("Checking/creating enum types for schema '{}'...", schema);

            // DDL для ENUM типа Furnish
            String createFurnishEnum = "CREATE TYPE " + schema + ".Furnish AS ENUM ('DESIGNER', 'FINE', 'BAD')";
            // DDL для ENUM типа View
            String createViewEnum = "CREATE TYPE " + schema + ".View AS ENUM ('STREET', 'PARK', 'NORMAL', 'GOOD')";
            // DDL для ENUM типа Transport
            String createTransportEnum = "CREATE TYPE " + schema + ".Transport AS ENUM ('FEW', 'NONE', 'LITTLE', 'NORMAL')";

            // Попытка создать Furnish
            try {
                stmt.execute(createFurnishEnum);
                logger.info("Enum type '{}.Furnish' created.", schema);
            } catch (SQLException e) {
                // Код состояния SQL "42710" означает, что объект уже существует
                if (e.getSQLState().equals("42710")) { 
                    logger.debug("Enum type '{}.Furnish' already exists.", schema);
                } else {
                    logger.error("Error creating enum type '{}.Furnish': {}", schema, e.getMessage());
                    throw e; // Перебрасываем, если это настоящая ошибка
                }
            }

            // Попытка создать View
            try {
                stmt.execute(createViewEnum);
                logger.info("Enum type '{}.View' created.", schema);
            } catch (SQLException e) {
                if (e.getSQLState().equals("42710")) {
                    logger.debug("Enum type '{}.View' already exists.", schema);
                } else {
                    logger.error("Error creating enum type '{}.View': {}", schema, e.getMessage());
                    throw e;
                }
            }

            // Попытка создать Transport
            try {
                stmt.execute(createTransportEnum);
                logger.info("Enum type '{}.Transport' created.", schema);
            } catch (SQLException e) {
                if (e.getSQLState().equals("42710")) {
                    logger.debug("Enum type '{}.Transport' already exists.", schema);
                } else {
                    logger.error("Error creating enum type '{}.Transport': {}", schema, e.getMessage());
                    throw e;
                }
            }
            logger.info("Enum type checks/creations complete.");
        } finally {
            lock.unlock();
        }
    }

    /**
     * Проверяет существование и создает необходимые таблицы в базе данных, если они отсутствуют.
     * Использует блокировку для обеспечения потокобезопасности при запуске.
     * @throws SQLException Если произошла ошибка при создании таблиц.
     */
    private void createTablesIfNotExist() throws SQLException {
        lock.lock(); // Блокировка для предотвращения одновременного создания таблиц
        try (Statement stmt = connection.createStatement()) {
            logger.info("Checking/creating tables for schema '{}'...", schema);

            // Таблица users
            String createUsersTable = "CREATE TABLE IF NOT EXISTS " + table("users") + " (" +
                                      "id SERIAL PRIMARY KEY," +
                                      "name VARCHAR(255) UNIQUE NOT NULL," +
                                      "password VARCHAR(255) NOT NULL" +
                                      ")";
            stmt.execute(createUsersTable);
            logger.info("Table '{}.users' checked/created.", schema);

            // Таблица Coordinates
            String createCoordinatesTable = "CREATE TABLE IF NOT EXISTS " + table("Coordinates") + " (" +
                                            "id SERIAL PRIMARY KEY," +
                                            "x BIGINT NOT NULL," +
                                            "y DOUBLE PRECISION NOT NULL" +
                                            ")";
            stmt.execute(createCoordinatesTable);
            logger.info("Table '{}.Coordinates' checked/created.", schema);

            // Таблица Houses
            String createHousesTable = "CREATE TABLE IF NOT EXISTS " + table("Houses") + " (" +
                                       "id SERIAL PRIMARY KEY," +
                                       "name VARCHAR(255) NOT NULL," +
                                       "year INTEGER CHECK (year > 0 AND year <= 959) NOT NULL," +
                                       "number_of_floors BIGINT CHECK (number_of_floors > 0 AND number_of_floors <= 77) NOT NULL" +
                                       ")";
            stmt.execute(createHousesTable);
            logger.info("Table '{}.Houses' checked/created.", schema);

            // Таблица Flats
            String createFlatsTable = "CREATE TABLE IF NOT EXISTS " + table("Flats") + " (" +
                                      "id SERIAL PRIMARY KEY," +
                                      "name VARCHAR(255) NOT NULL," +
                                      "coordinates_id INTEGER NOT NULL REFERENCES " + table("Coordinates") + "(id) ON DELETE CASCADE," +
                                      "creation_date DATE NOT NULL," +
                                      "area DOUBLE PRECISION CHECK (area > 0 AND area <= 626) NOT NULL," +
                                      "number_of_rooms INTEGER CHECK (number_of_rooms > 0) NOT NULL," +
                                      "furnish " + schema + ".Furnish NOT NULL," + // Используем квалифицированный по схеме тип ENUM
                                      "view " + schema + ".View," +                 // Может быть NULL
                                      "transport " + schema + ".Transport NOT NULL," +
                                      "house_id INTEGER REFERENCES " + table("Houses") + "(id) ON DELETE SET NULL," + // Может быть NULL
                                      "owner_id INTEGER NOT NULL REFERENCES " + table("users") + "(id) ON DELETE CASCADE" +
                                      ")";
            stmt.execute(createFlatsTable);
            logger.info("Table '{}.Flats' checked/created.", schema);
            logger.info("All table checks/creations complete.");

        } catch (SQLException e) {
            logger.error("Error during table creation: {}", e.getMessage());
            throw e; // Перебрасываем исключение, чтобы указать на критический сбой при инициализации
        } finally {
            lock.unlock();
        }
    }


    /**
     * Retrieves the user ID by username.
     * @param username The username to look up.
     * @return The user ID, or null if not found.
     * @throws SQLException If a database error occurs.
     */
    private Integer getUserIdByName(String username) throws SQLException {
        String sql = "SELECT id FROM " + table("users") + " WHERE name = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, username);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("id");
                }
            }
        }
        return null;
    }

    /**
     * Retrieves the username by user ID.
     * @param userId The user ID to look up.
     * @return The username, or null if not found.
     * @throws SQLException If a database error occurs.
     */
    private String getUsernameById(int userId) throws SQLException {
        String sql = "SELECT name FROM " + table("users") + " WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getString("name");
                }
            }
        }
        return null;
    }

    /**
     * Inserts a Flat object and its associated Coordinates and House objects into the database.
     * The flat's ID is auto-generated by the database.
     *
     * @param flat The Flat object to insert. Its ID will be ignored and set by the DB.
     * @param ownerName The username of the owner of this flat.
     * @return The auto-generated ID of the inserted flat, or -1 if insertion fails.
     * @throws SQLException If a database error occurs.
     */
    public int insertFlat(Flat flat, String ownerName) throws SQLException {
        lock.lock();
        try {
            connection.setAutoCommit(false); // Start transaction

            Integer ownerId = getUserIdByName(ownerName);
            if (ownerId == null)
                throw new SQLException("Owner user '" + ownerName + "' not found.");

            int coordinatesId = insertCoordinates(flat.getCoordinates());
            Integer houseId = flat.getHouse() != null ? insertHouse(flat.getHouse()) : null; // Use Integer for nullability

            String sql = "INSERT INTO " + table("Flats") + " (name, coordinates_id, creation_date, area, number_of_rooms, " +
                         "furnish, view, transport, house_id, owner_id) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?) RETURNING id";

            try (PreparedStatement stmt = connection.prepareStatement(sql)) {
                stmt.setString(1, flat.getName());
                stmt.setInt(2, coordinatesId);
                stmt.setDate(3, Date.valueOf(flat.getCreationDate()));
                stmt.setDouble(4, flat.getArea()); // DECIMAL handles DOUBLE PRECISION from Java fine
                stmt.setInt(5, flat.getNumberOfRooms());
                stmt.setObject(6, flat.getFurnish().name(), Types.OTHER); // Use .name() for ENUMs
                stmt.setObject(7, flat.getView() != null ? flat.getView().name() : null, Types.OTHER); // Use .name() for ENUMs
                stmt.setObject(8, flat.getTransport().name(), Types.OTHER); // Use .name() for ENUMs
                stmt.setObject(9, houseId, Types.INTEGER); // Use setObject for nullable Integer
                stmt.setInt(10, ownerId);

                ResultSet rs = stmt.executeQuery();
                if (rs.next()) {
                    int generatedId = rs.getInt("id");
                    connection.commit(); // Commit transaction
                    return generatedId;
                } else {
                    connection.rollback(); // Rollback if no ID returned
                    return -1;
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

    private Integer insertHouse(House house) throws SQLException {
        if (house == null) return null;

        String sql = "INSERT INTO " + table("Houses") + " (name, year, number_of_floors) VALUES (?, ?, ?) RETURNING id";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, house.getName());
            stmt.setInt(2, house.getYear());
            stmt.setLong(3, house.getNumberOfFloors());
            ResultSet rs = stmt.executeQuery();
            if (rs.next())
                return rs.getInt("id");
        }
        return null;
    }

    /**
     * Updates an existing Flat object in the database, including its associated
     * Coordinates and House. Only updates if the specified ownerName owns the flat.
     *
     * @param id The ID of the flat to update.
     * @param flat The Flat object containing the new values.
     * @param ownerName The username of the owner.
     * @return true if the flat was updated, false otherwise (e.g., not found or not owned).
     * @throws SQLException If a database error occurs.
     */
    public boolean updateFlat(int id, Flat flat, String ownerName) throws SQLException {
        lock.lock();
        try {
            connection.setAutoCommit(false);

            Integer ownerId = getUserIdByName(ownerName);
            if (ownerId == null) {
                connection.rollback();
                return false; // User not found
            }

            // Check if the flat exists and belongs to the ownerName (using owner_id)
            if (!checkFlatOwnership(id, ownerId)) {
                connection.rollback();
                return false; 
            }

            // Fetch current flat details to get existing coordinates_id and house_id
            String getFlatDetailsSql = "SELECT coordinates_id, house_id FROM " + table("Flats") + " WHERE id = ?";
            int currentCoordsId;
            Integer currentHouseId = null;

            try (PreparedStatement stmt = connection.prepareStatement(getFlatDetailsSql)) {
                stmt.setInt(1, id);
                ResultSet rs = stmt.executeQuery();
                if (rs.next()) {
                    currentCoordsId = rs.getInt("coordinates_id");
                    currentHouseId = rs.getObject("house_id", Integer.class);
                } else {
                    connection.rollback();
                    return false; // Flat not found (should be caught by checkFlatOwnership, but defensive)
                }
            }

            // Update Coordinates (coordinates_id is NOT NULL, so it always exists)
            if (flat.getCoordinates() != null) {
                updateCoordinates(currentCoordsId, flat.getCoordinates());
            }

            // Update or (re-)insert House
            Integer newHouseId = currentHouseId; // Assume it stays the same unless changed
            if (flat.getHouse() != null) {
                if (currentHouseId != null) {
                    // Update existing house
                    updateHouse(currentHouseId, flat.getHouse());
                } else {
                    // Insert new house and link it
                    newHouseId = insertHouse(flat.getHouse());
                }
            } else { // If newFlat.getHouse() is null, it means we want to set house_id to null
                newHouseId = null;
            }

            String sql = "UPDATE " + table("Flats") + " SET name = ?, creation_date = ?, area = ?, number_of_rooms = ?, " +
                         "furnish = ?, view = ?, transport = ?, house_id = ? WHERE id = ? AND owner_id = ?";
            try (PreparedStatement stmt = connection.prepareStatement(sql)) {
                stmt.setString(1, flat.getName());
                stmt.setDate(2, Date.valueOf(flat.getCreationDate()));
                stmt.setDouble(3, flat.getArea());
                stmt.setInt(4, flat.getNumberOfRooms());
                stmt.setObject(5, flat.getFurnish().name(), Types.OTHER);
                stmt.setObject(6, flat.getView() != null ? flat.getView().name() : null, Types.OTHER);
                stmt.setObject(7, flat.getTransport().name(), Types.OTHER);
                stmt.setObject(8, newHouseId, Types.INTEGER); // Set newHouseId (can be null)
                stmt.setInt(9, id);
                stmt.setInt(10, ownerId); 

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

    private boolean updateCoordinates(int id, Coordinates coordinates) throws SQLException {
        // Only update non-null fields in coordinates
        String sql = "UPDATE " + table("Coordinates") + " SET x = COALESCE(?, x), y = COALESCE(?, y) WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            // Using COALESCE to update only if new value is not null, otherwise keep old value
            if (coordinates.getX() != null) stmt.setLong(1, coordinates.getX()); else stmt.setNull(1, Types.BIGINT);
            if (coordinates.getY() != null) stmt.setDouble(2, coordinates.getY()); else stmt.setNull(2, Types.DOUBLE);
            stmt.setInt(3, id);
            return stmt.executeUpdate() > 0;
        }
    }

    private boolean updateHouse(int id, House house) throws SQLException {
        // Only update non-null fields in house
        String sql = "UPDATE " + table("Houses") + " SET name = COALESCE(?, name), year = COALESCE(?, year), number_of_floors = COALESCE(?, number_of_floors) WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            if (house.getName() != null) stmt.setString(1, house.getName()); else stmt.setNull(1, Types.VARCHAR);
            if (house.getYear() != null) stmt.setInt(2, house.getYear()); else stmt.setNull(2, Types.INTEGER);
            if (house.getNumberOfFloors() != null) stmt.setLong(3, house.getNumberOfFloors()); else stmt.setNull(3, Types.BIGINT);
            stmt.setInt(4, id);
            return stmt.executeUpdate() > 0;
        }
    }

    public HashMap<Integer, Flat> getAllFlats() throws SQLException {
        lock.lock();
        try {
            HashMap<Integer, Flat> flatsMap = new HashMap<>();
            String sql = "SELECT f.*, c.x, c.y, h.name AS house_name, h.year AS house_year, h.number_of_floors AS house_floors, u.name AS owner_username " +
                         "FROM " + table("Flats") + " f " +
                         "JOIN " + table("Coordinates") + " c ON f.coordinates_id = c.id " +
                         "LEFT JOIN " + table("Houses") + " h ON f.house_id = h.id " +
                         "JOIN " + table("users") + " u ON f.owner_id = u.id";

            try (Statement stmt = connection.createStatement();
                ResultSet rs = stmt.executeQuery(sql)) {

                while (rs.next()) {
                    Flat flat = new Flat();
                    int id = rs.getInt("id");
                    flat.setId(id);
                    flat.setName(rs.getString("name")); // Correctly sets the flat's name
                    
                    // Coordinates
                    flat.setCoordinates(new Coordinates(rs.getLong("x"), rs.getDouble("y")));
                    
                    flat.setCreationDate(LocalDate.parse(rs.getDate("creation_date").toString()));
                    flat.setArea(rs.getDouble("area")); // DECIMAL handles DOUBLE PRECISION from Java fine
                    flat.setNumberOfRooms(rs.getInt("number_of_rooms"));
                    
                    // Обработка ENUM Furnish
                    String furnishStr = rs.getString("furnish");
                    try {
                        flat.setFurnish(Furnish.valueOf(furnishStr));
                    } catch (IllegalArgumentException e) {
                        logger.error("Invalid Furnish value in DB for flat ID {}: '{}'. Setting to null.", id, furnishStr);
                        flat.setFurnish(null); // Или установите значение по умолчанию
                    }

                    // Обработка ENUM View (может быть null)
                    String viewStr = rs.getString("view");
                    if (viewStr != null) {
                        try {
                            flat.setView(View.valueOf(viewStr));
                        } catch (IllegalArgumentException e) {
                            logger.error("Invalid View value in DB for flat ID {}: '{}'. Setting to null.", id, viewStr);
                            flat.setView(null);
                        }
                    } else {
                        flat.setView(null);
                    }
                    
                    // Обработка ENUM Transport
                    String transportStr = rs.getString("transport");
                    try {
                        flat.setTransport(Transport.valueOf(transportStr));
                    } catch (IllegalArgumentException e) {
                        logger.error("Invalid Transport value in DB for flat ID {}: '{}'. Setting to null.", id, transportStr);
                        flat.setTransport(null); // Или установите значение по умолчанию
                    }


                    // House
                    String houseName = rs.getString("house_name");
                    if (houseName != null) {
                        House house = new House(
                            houseName,
                            rs.getInt("house_year"),
                            rs.getLong("house_floors")
                        );
                        flat.setHouse(house);
                    } else {
                        flat.setHouse(null);
                    }
                    
                    // FIX: This line was incorrect. It should set ownerName, not overwrite flat.name.
                    flat.setOwnerName(rs.getString("owner_username")); // Correctly sets the owner's username

                    flatsMap.put(id, flat);
                }
            }
            return flatsMap;
        } finally {
            lock.unlock();
        }
    }

    /**
     * Removes a flat from the database. Only removes if the specified ownerName owns the flat.
     *
     * @param id The ID of the flat to remove.
     * @param ownerName The username attempting to remove the flat.
     * @return true if the flat was removed, false otherwise (e.g., not found or not owned).
     * @throws SQLException If a database error occurs.
     */
    public boolean removeFlat(int id, String ownerName) throws SQLException {
        lock.lock();
        try {
            Integer ownerId = getUserIdByName(ownerName);
            if (ownerId == null) {
                return false; // User not found
            }

            // ON DELETE CASCADE on coordinates_id handles coordinate deletion.
            // ON DELETE SET NULL on house_id handles house association.
            String sql = "DELETE FROM " + table("Flats") + " WHERE id = ? AND owner_id = ?";
            try (PreparedStatement stmt = connection.prepareStatement(sql)) {
                stmt.setInt(1, id);
                stmt.setInt(2, ownerId);
                return stmt.executeUpdate() > 0;
            }
        } finally {
            lock.unlock();
        }
    }

    /**
     * Clears all flats belonging to a specific owner from the database.
     *
     * @param ownerName The username whose flats should be cleared.
     * @return true if successful, false otherwise.
     * @throws SQLException If a database error occurs.
     */
    public boolean clearFlatsByOwner(String ownerName) throws SQLException {
        lock.lock();
        try {
            Integer ownerId = getUserIdByName(ownerName);
            if (ownerId == null) {
                return false; // User not found
            }

            // ON DELETE CASCADE on coordinates_id handles coordinate deletion.
            String sql = "DELETE FROM " + table("Flats") + " WHERE owner_id = ?";
            try (PreparedStatement stmt = connection.prepareStatement(sql)) {
                stmt.setInt(1, ownerId);
                stmt.executeUpdate(); 
                return true;
            }
        } finally {
            lock.unlock();
        }
    }

    /**
     * Checks if a flat with the given ID is owned by the specified user ID.
     * @param flatId The ID of the flat to check.
     * @param ownerId The user ID to check ownership against.
     * @return true if the flat exists and is owned by the user, false otherwise.
     * @throws SQLException If a database error occurs.
     */
    public boolean checkFlatOwnership(int flatId, int ownerId) throws SQLException {
        String sql = "SELECT COUNT(*) FROM " + table("Flats") + " WHERE id = ? AND owner_id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, flatId);
            stmt.setInt(2, ownerId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        }
        return false;
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

    /**
     * Adds a new user to the database.
     * @param username The username.
     * @param password The raw password (will be hashed).
     * @return true if user was added, false if username already exists.
     * @throws SQLException If a database error occurs.
     */
    public boolean addUser(String username, String password) throws SQLException {
        lock.lock();
        try {
            if (usernameExists(username)) {
                return false; // User already exists
            }

            String salt = generateDeterministicSalt(username);
            String hashedPassword = hashPasswordWithSalt(password, salt);
            // Column name is 'password' in the DDL, so use that.
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

    /**
     * Checks if a user exists and the provided password matches the stored hash.
     * @param username The username.
     * @param password The raw password to check.
     * @return true if user exists and password matches, false otherwise.
     * @throws SQLException If a database error occurs.
     */
    public boolean userExists(String username, String password) throws SQLException {
        String storedHash = getPasswordHash(username);
        if (storedHash == null) {
            return false; // User does not exist
        }
        
        String salt = generateDeterministicSalt(username);
        String hashedInputPassword = hashPasswordWithSalt(password, salt);
        
        return storedHash.equals(hashedInputPassword);
    }

    private String getPasswordHash(String username) throws SQLException {
        // Column name is 'password' in the DDL.
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

    // MD5 hashing methods (unchanged)
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