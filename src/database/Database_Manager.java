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
    private static final String DB_URL = "jdbc:sqlite:data/huts.db";
    
    // Specialized managers
    private User_Manager userManager;
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
        userManager = new User_Manager(connection);
        electricityManager = new Electricity_Manager(connection);
        gasManager = new Gas_Manager(connection);
        waterManager = new Water_Manager(connection);
        subscriptionManager = new Subscription_Manager(connection);
        billManager = new Bill_Manager(connection);
        readingHistoryManager = new Reading_History_Manager(connection);
    }
    
    private void createTables() {
        try (Statement stmt = connection.createStatement()) {
            // Create Users table
            stmt.execute("CREATE TABLE IF NOT EXISTS users (" +
                    "id TEXT PRIMARY KEY, " +
                    "username TEXT UNIQUE, " +
                    "password TEXT, " +
                    "email TEXT, " +
                    "full_name TEXT)");
            
            // Create Household table
            stmt.execute("CREATE TABLE IF NOT EXISTS household (" +
                    "id TEXT PRIMARY KEY, " +
                    "name TEXT, " +
                    "address TEXT, " +
                    "number_of_occupants INTEGER)");
            
            // Create Utility tables
            stmt.execute("CREATE TABLE IF NOT EXISTS electricity (" +
                    "id TEXT PRIMARY KEY, " +
                    "user_id TEXT, " +
                    "name TEXT, " +
                    "provider TEXT, " +
                    "account_number TEXT, " +
                    "rate_per_kwh REAL, " +
                    "meter_reading REAL)");
            
            stmt.execute("CREATE TABLE IF NOT EXISTS gas (" +
                    "id TEXT PRIMARY KEY, " +
                    "user_id TEXT, " +
                    "name TEXT, " +
                    "provider TEXT, " +
                    "account_number TEXT, " +
                    "rate_per_unit REAL, " +
                    "meter_reading REAL)");
            
            stmt.execute("CREATE TABLE IF NOT EXISTS water (" +
                    "id TEXT PRIMARY KEY, " +
                    "user_id TEXT, " +
                    "name TEXT, " +
                    "provider TEXT, " +
                    "account_number TEXT, " +
                    "rate_per_cubic_meter REAL, " +
                    "meter_reading REAL)");
            
            // Create Subscription table
            stmt.execute("CREATE TABLE IF NOT EXISTS subscription (" +
                    "id TEXT PRIMARY KEY, " +
                    "user_id TEXT, " +
                    "name TEXT, " +
                    "provider TEXT, " +
                    "account_number TEXT, " +
                    "type TEXT, " +
                    "monthly_cost REAL, " +
                    "next_billing_date TEXT)");
            
            // Create Bill table - UPDATED to include more fields for Electricity_Panel
            stmt.execute("CREATE TABLE IF NOT EXISTS bill (" +
                    "id TEXT PRIMARY KEY, " +
                    "utility_id TEXT, " +
                    "utility_type TEXT, " + // Added to distinguish between electricity, gas, water
                    "user_id TEXT, " + // Added to filter bills by user
                    "amount REAL, " +
                    "consumption REAL, " +
                    "issue_date TEXT, " +
                    "due_date TEXT, " +
                    "is_paid INTEGER, " +
                    "paid_date TEXT, " +
                    "notes TEXT)"); // Added for additional bill notes
            
            // Create reading history table
            stmt.execute("CREATE TABLE IF NOT EXISTS reading_history (" +
                    "id TEXT PRIMARY KEY, " +
                    "utility_id TEXT, " +
                    "utility_type TEXT, " +
                    "reading_date TEXT, " +
                    "reading_value REAL)");
            
            // NEW: Create electricity bill statistics table for faster querying
            stmt.execute("CREATE TABLE IF NOT EXISTS electricity_stats (" +
                    "id TEXT PRIMARY KEY, " +
                    "user_id TEXT, " +
                    "year INTEGER, " +
                    "month INTEGER, " +
                    "total_kwh REAL, " +
                    "total_cost REAL)");
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    // Getters for specialized managers
    public User_Manager getUserManager() {
        return userManager;
    }
    
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
    
    public boolean authenticateUser(String username, String password) {
        return userManager.authenticateUser(username, password);
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
    
    // Method to check if the database needs migration (for future updates)
    public boolean checkMigrationNeeded() {
        try (Statement stmt = connection.createStatement()) {
            // Check if electricity_stats table exists
            ResultSet rs = connection.getMetaData().getTables(null, null, "electricity_stats", null);
            if (!rs.next()) {
                return true;
            }
            
            // Add more checks as needed
            
            return false;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    // Method to get statistics for the Electricity_Panel
    public double[] getElectricityStatsByUser(String userId, int year) {
        double[] monthlyData = new double[12]; // One value for each month
        
        try (PreparedStatement pstmt = connection.prepareStatement(
                "SELECT month, total_cost FROM electricity_stats WHERE user_id = ? AND year = ? ORDER BY month")) {
            pstmt.setString(1, userId);
            pstmt.setInt(2, year);
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                int month = rs.getInt("month") - 1; // 0-based array
                double totalCost = rs.getDouble("total_cost");
                monthlyData[month] = totalCost;
            }
            
            return monthlyData;
        } catch (SQLException e) {
            e.printStackTrace();
            return monthlyData; // Return empty array on error
        }
    }
    
    // Method to get total yearly electricity cost for a user
    public double getTotalYearlyElectricityCost(String userId, int year) {
        try (PreparedStatement pstmt = connection.prepareStatement(
                "SELECT SUM(total_cost) as yearly_total FROM electricity_stats WHERE user_id = ? AND year = ?")) {
            pstmt.setString(1, userId);
            pstmt.setInt(2, year);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return rs.getDouble("yearly_total");
            }
            return 0.0;
        } catch (SQLException e) {
            e.printStackTrace();
            return 0.0;
        }
    }
    
    // Method to get average monthly electricity cost for a user
    public double getAvgMonthlyElectricityCost(String userId, int year) {
        try (PreparedStatement pstmt = connection.prepareStatement(
                "SELECT AVG(total_cost) as monthly_avg FROM electricity_stats WHERE user_id = ? AND year = ?")) {
            pstmt.setString(1, userId);
            pstmt.setInt(2, year);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return rs.getDouble("monthly_avg");
            }
            return 0.0;
        } catch (SQLException e) {
            e.printStackTrace();
            return 0.0;
        }
    }
}