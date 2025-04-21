package database;

import models.*;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Core Database Manager that initializes the connection and delegates operations
 * to specialized managers for each utility type
 */
public class Database_Manager {
    private Connection connection;
    private static final String DB_URL = "jdbc:sqlite:house_utilities.db";
    
    // Specialized managers
    private Electricity_Manager electricityManager;
    private Gas_Manager gasManager;
    private Water_Manager waterManager;
    private Subscription_Manager subscriptionManager;
    private Bill_Manager billManager;
    private Reading_History_Manager readingHistoryManager;
    
    // Singleton pattern
    private static Database_Manager instance;
    
    public static Database_Manager getInstance() {
        if (instance == null) {
            instance = new Database_Manager();
        }
        return instance;
    }
    
    private Database_Manager() {
        try {
            // Load the SQLite JDBC driver
            Class.forName("org.sqlite.JDBC");
            
            // Create connection
            connection = DriverManager.getConnection(DB_URL);
            
            // Create tables
            createTables();
            
            // Initialize managers
            initializeManagers();
            
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }
    }
    
    private void initializeManagers() {
        electricityManager = new Electricity_Manager(connection);
        gasManager = new Gas_Manager(connection);
        waterManager = new Water_Manager(connection);
        subscriptionManager = new Subscription_Manager(connection);
        billManager = new Bill_Manager(connection);
        readingHistoryManager = new Reading_History_Manager(connection);
    }
    
    private void createTables() {
        try (Statement stmt = connection.createStatement()) {
            // Create Household table
            stmt.execute("CREATE TABLE IF NOT EXISTS household (" +
                    "id TEXT PRIMARY KEY, " +
                    "name TEXT, " +
                    "address TEXT, " +
                    "number_of_occupants INTEGER)");
            
            // Create Utility tables
            stmt.execute("CREATE TABLE IF NOT EXISTS electricity (" +
                    "id TEXT PRIMARY KEY, " +
                    "name TEXT, " +
                    "provider TEXT, " +
                    "account_number TEXT, " +
                    "rate_per_kwh REAL, " +
                    "meter_reading REAL)");
            
            stmt.execute("CREATE TABLE IF NOT EXISTS gas (" +
                    "id TEXT PRIMARY KEY, " +
                    "name TEXT, " +
                    "provider TEXT, " +
                    "account_number TEXT, " +
                    "rate_per_unit REAL, " +
                    "meter_reading REAL)");
            
            stmt.execute("CREATE TABLE IF NOT EXISTS water (" +
                    "id TEXT PRIMARY KEY, " +
                    "name TEXT, " +
                    "provider TEXT, " +
                    "account_number TEXT, " +
                    "rate_per_cubic_meter REAL, " +
                    "meter_reading REAL)");
            
            // Create Subscription table
            stmt.execute("CREATE TABLE IF NOT EXISTS subscription (" +
                    "id TEXT PRIMARY KEY, " +
                    "name TEXT, " +
                    "provider TEXT, " +
                    "account_number TEXT, " +
                    "type TEXT, " +
                    "monthly_cost REAL, " +
                    "next_billing_date TEXT)");
            
            // Create Bill table
            stmt.execute("CREATE TABLE IF NOT EXISTS bill (" +
                    "id TEXT PRIMARY KEY, " +
                    "utility_id TEXT, " +
                    "amount REAL, " +
                    "consumption REAL, " +
                    "issue_date TEXT, " +
                    "due_date TEXT, " +
                    "is_paid INTEGER, " +
                    "paid_date TEXT)");
            
            // Create reading history table
            stmt.execute("CREATE TABLE IF NOT EXISTS reading_history (" +
                    "id TEXT PRIMARY KEY, " +
                    "utility_id TEXT, " +
                    "utility_type TEXT, " +
                    "reading_date TEXT, " +
                    "reading_value REAL)");
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    // Getters for specialized managers
    public Electricity_Manager getElectricityManager() {
        return electricityManager;
    }
    
    public Gas_Manager getGasManager() {
        return gasManager;
    }
    
    public Water_Manager getWaterManager() {
        return waterManager;
    }
    
    public Subscription_Manager getSubscriptionManager() {
        return subscriptionManager;
    }
    
    public Bill_Manager getBillManager() {
        return billManager;
    }
    
    public Reading_History_Manager getReadingHistoryManager() {
        return readingHistoryManager;
    }
    
    public Connection getConnection() {
        return this.connection;
    }
    
    // Close connection
    public void closeConnection() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
}