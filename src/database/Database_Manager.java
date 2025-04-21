package database;

import models.*;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Database_Manager {
    private Connection connection;
    private static final String DB_URL = "jdbc:sqlite:house_utilities.db";
    
    // Singleton pattern for database manager
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
            
            // Create tables if they don't exist
            createTables();
            
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }
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
            
            // Create reading history table to store previous readings
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
    
    // Electricity methods
    public void saveElectricity(Electricity electricity) {
        String id = UUID.randomUUID().toString();
        String sql = "INSERT INTO electricity (id, name, provider, account_number, rate_per_kwh, meter_reading) VALUES (?, ?, ?, ?, ?, ?)";
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, id);
            pstmt.setString(2, electricity.getName());
            pstmt.setString(3, electricity.getProvider());
            pstmt.setString(4, electricity.getAccountNumber());
            pstmt.setDouble(5, electricity.getRatePerKwh());
            pstmt.setDouble(6, electricity.getMeterReading());
            pstmt.executeUpdate();
            
            // Save initial reading to history
            saveReadingHistory(id, "electricity", electricity.getMeterReading());
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    public List<Electricity> getAllElectricity() {
        List<Electricity> electricityList = new ArrayList<>();
        String sql = "SELECT * FROM electricity";
        
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                String name = rs.getString("name");
                String provider = rs.getString("provider");
                String accountNumber = rs.getString("account_number");
                double ratePerKwh = rs.getDouble("rate_per_kwh");
                double meterReading = rs.getDouble("meter_reading");
                
                Electricity electricity = new Electricity(name, provider, accountNumber, ratePerKwh);
                electricity.setMeterReading(meterReading);
                electricityList.add(electricity);
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return electricityList;
    }
    
    public void updateElectricityReading(String accountNumber, double newReading) {
        String sql = "UPDATE electricity SET meter_reading = ? WHERE account_number = ?";
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setDouble(1, newReading);
            pstmt.setString(2, accountNumber);
            pstmt.executeUpdate();
            
            // Get utility ID
            String idSql = "SELECT id FROM electricity WHERE account_number = ?";
            PreparedStatement idStmt = connection.prepareStatement(idSql);
            idStmt.setString(1, accountNumber);
            ResultSet rs = idStmt.executeQuery();
            
            if (rs.next()) {
                String utilityId = rs.getString("id");
                // Save new reading to history
                saveReadingHistory(utilityId, "electricity", newReading);
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    public double getPreviousElectricityReading(String accountNumber) {
        double previousReading = 0.0;
        
        try {
            // Get utility ID
            String idSql = "SELECT id FROM electricity WHERE account_number = ?";
            PreparedStatement idStmt = connection.prepareStatement(idSql);
            idStmt.setString(1, accountNumber);
            ResultSet idRs = idStmt.executeQuery();
            
            if (idRs.next()) {
                String utilityId = idRs.getString("id");
                
                // Get previous reading
                String sql = "SELECT reading_value FROM reading_history " +
                        "WHERE utility_id = ? AND utility_type = 'electricity' " +
                        "ORDER BY reading_date DESC LIMIT 1 OFFSET 1";
                
                PreparedStatement pstmt = connection.prepareStatement(sql);
                pstmt.setString(1, utilityId);
                ResultSet rs = pstmt.executeQuery();
                
                if (rs.next()) {
                    previousReading = rs.getDouble("reading_value");
                }
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return previousReading;
    }
    
    // Gas methods
    public void saveGas(Gas gas) {
        String id = UUID.randomUUID().toString();
        String sql = "INSERT INTO gas (id, name, provider, account_number, rate_per_unit, meter_reading) VALUES (?, ?, ?, ?, ?, ?)";
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, id);
            pstmt.setString(2, gas.getName());
            pstmt.setString(3, gas.getProvider());
            pstmt.setString(4, gas.getAccountNumber());
            pstmt.setDouble(5, gas.getRatePerUnit());
            pstmt.setDouble(6, gas.getMeterReading());
            pstmt.executeUpdate();
            
            // Save initial reading to history
            saveReadingHistory(id, "gas", gas.getMeterReading());
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    public List<Gas> getAllGas() {
        List<Gas> gasList = new ArrayList<>();
        String sql = "SELECT * FROM gas";
        
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                String name = rs.getString("name");
                String provider = rs.getString("provider");
                String accountNumber = rs.getString("account_number");
                double ratePerUnit = rs.getDouble("rate_per_unit");
                double meterReading = rs.getDouble("meter_reading");
                
                Gas gas = new Gas(name, provider, accountNumber, ratePerUnit);
                gas.setMeterReading(meterReading);
                gasList.add(gas);
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return gasList;
    }
    
    public void updateGasReading(String accountNumber, double newReading) {
        String sql = "UPDATE gas SET meter_reading = ? WHERE account_number = ?";
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setDouble(1, newReading);
            pstmt.setString(2, accountNumber);
            pstmt.executeUpdate();
            
            // Get utility ID
            String idSql = "SELECT id FROM gas WHERE account_number = ?";
            PreparedStatement idStmt = connection.prepareStatement(idSql);
            idStmt.setString(1, accountNumber);
            ResultSet rs = idStmt.executeQuery();
            
            if (rs.next()) {
                String utilityId = rs.getString("id");
                // Save new reading to history
                saveReadingHistory(utilityId, "gas", newReading);
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    public double getPreviousGasReading(String accountNumber) {
        double previousReading = 0.0;
        
        try {
            // Get utility ID
            String idSql = "SELECT id FROM gas WHERE account_number = ?";
            PreparedStatement idStmt = connection.prepareStatement(idSql);
            idStmt.setString(1, accountNumber);
            ResultSet idRs = idStmt.executeQuery();
            
            if (idRs.next()) {
                String utilityId = idRs.getString("id");
                
                // Get previous reading
                String sql = "SELECT reading_value FROM reading_history " +
                        "WHERE utility_id = ? AND utility_type = 'gas' " +
                        "ORDER BY reading_date DESC LIMIT 1 OFFSET 1";
                
                PreparedStatement pstmt = connection.prepareStatement(sql);
                pstmt.setString(1, utilityId);
                ResultSet rs = pstmt.executeQuery();
                
                if (rs.next()) {
                    previousReading = rs.getDouble("reading_value");
                }
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return previousReading;
    }
    
    // Water methods
    public void saveWater(Water water) {
        String id = UUID.randomUUID().toString();
        String sql = "INSERT INTO water (id, name, provider, account_number, rate_per_cubic_meter, meter_reading) VALUES (?, ?, ?, ?, ?, ?)";
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, id);
            pstmt.setString(2, water.getName());
            pstmt.setString(3, water.getProvider());
            pstmt.setString(4, water.getAccountNumber());
            pstmt.setDouble(5, water.getRatePerCubicMeter());
            pstmt.setDouble(6, water.getMeterReading());
            pstmt.executeUpdate();
            
            // Save initial reading to history
            saveReadingHistory(id, "water", water.getMeterReading());
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    public List<Water> getAllWater() {
        List<Water> waterList = new ArrayList<>();
        String sql = "SELECT * FROM water";
        
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                String name = rs.getString("name");
                String provider = rs.getString("provider");
                String accountNumber = rs.getString("account_number");
                double ratePerCubicMeter = rs.getDouble("rate_per_cubic_meter");
                double meterReading = rs.getDouble("meter_reading");
                
                Water water = new Water(name, provider, accountNumber, ratePerCubicMeter);
                water.setMeterReading(meterReading);
                waterList.add(water);
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return waterList;
    }
    
    public void updateWaterReading(String accountNumber, double newReading) {
        String sql = "UPDATE water SET meter_reading = ? WHERE account_number = ?";
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setDouble(1, newReading);
            pstmt.setString(2, accountNumber);
            pstmt.executeUpdate();
            
            // Get utility ID
            String idSql = "SELECT id FROM water WHERE account_number = ?";
            PreparedStatement idStmt = connection.prepareStatement(idSql);
            idStmt.setString(1, accountNumber);
            ResultSet rs = idStmt.executeQuery();
            
            if (rs.next()) {
                String utilityId = rs.getString("id");
                // Save new reading to history
                saveReadingHistory(utilityId, "water", newReading);
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    public double getPreviousWaterReading(String accountNumber) {
        double previousReading = 0.0;
        
        try {
            // Get utility ID
            String idSql = "SELECT id FROM water WHERE account_number = ?";
            PreparedStatement idStmt = connection.prepareStatement(idSql);
            idStmt.setString(1, accountNumber);
            ResultSet idRs = idStmt.executeQuery();
            
            if (idRs.next()) {
                String utilityId = idRs.getString("id");
                
                // Get previous reading
                String sql = "SELECT reading_value FROM reading_history " +
                        "WHERE utility_id = ? AND utility_type = 'water' " +
                        "ORDER BY reading_date DESC LIMIT 1 OFFSET 1";
                
                PreparedStatement pstmt = connection.prepareStatement(sql);
                pstmt.setString(1, utilityId);
                ResultSet rs = pstmt.executeQuery();
                
                if (rs.next()) {
                    previousReading = rs.getDouble("reading_value");
                }
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return previousReading;
    }
    
    // Subscription methods
    public void saveSubscription(Subscription subscription) {
        String id = UUID.randomUUID().toString();
        String sql = "INSERT INTO subscription (id, name, provider, account_number, type, monthly_cost, next_billing_date) VALUES (?, ?, ?, ?, ?, ?, ?)";
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, id);
            pstmt.setString(2, subscription.getName());
            pstmt.setString(3, subscription.getProvider());
            pstmt.setString(4, subscription.getAccountNumber());
            pstmt.setString(5, subscription.getType().toString());
            pstmt.setDouble(6, subscription.getMonthlyCost());
            pstmt.setString(7, subscription.getNextBillingDate().toString());
            pstmt.executeUpdate();
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    public List<Subscription> getAllSubscriptions() {
        List<Subscription> subscriptionList = new ArrayList<>();
        String sql = "SELECT * FROM subscription";
        
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                String name = rs.getString("name");
                String provider = rs.getString("provider");
                String accountNumber = rs.getString("account_number");
                SubscriptionType type = SubscriptionType.valueOf(rs.getString("type"));
                double monthlyCost = rs.getDouble("monthly_cost");
                LocalDate nextBillingDate = LocalDate.parse(rs.getString("next_billing_date"));
                
                Subscription subscription = new Subscription(name, provider, accountNumber, type, monthlyCost);
                subscription.setNextBillingDate(nextBillingDate);
                subscriptionList.add(subscription);
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return subscriptionList;
    }
    
    // Bill methods
    public void saveBill(Bill bill) {
        String sql = "INSERT INTO bill (id, utility_id, amount, consumption, issue_date, due_date, is_paid, paid_date) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, bill.getId().toString());
            pstmt.setString(2, bill.getUtilityId().toString());
            pstmt.setDouble(3, bill.getAmount());
            pstmt.setDouble(4, bill.getConsumption());
            pstmt.setString(5, bill.getIssueDate().toString());
            pstmt.setString(6, bill.getDueDate().toString());
            pstmt.setInt(7, bill.isPaid() ? 1 : 0);
            pstmt.setString(8, bill.getPaidDate() != null ? bill.getPaidDate().toString() : null);
            pstmt.executeUpdate();
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    public List<Bill> getBillsByUtilityId(UUID utilityId) {
        List<Bill> bills = new ArrayList<>();
        String sql = "SELECT * FROM bill WHERE utility_id = ? ORDER BY issue_date DESC";
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, utilityId.toString());
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                UUID id = UUID.fromString(rs.getString("id"));
                double amount = rs.getDouble("amount");
                double consumption = rs.getDouble("consumption");
                LocalDate issueDate = LocalDate.parse(rs.getString("issue_date"));
                LocalDate dueDate = LocalDate.parse(rs.getString("due_date"));
                boolean isPaid = rs.getInt("is_paid") == 1;
                String paidDateStr = rs.getString("paid_date");
                LocalDate paidDate = paidDateStr != null ? LocalDate.parse(paidDateStr) : null;
                
                Bill bill = new Bill(utilityId, amount, consumption, issueDate, dueDate);
                bill.setPaid(isPaid);
                if (paidDate != null) {
                    bill.setPaidDate(paidDate);
                }
                bills.add(bill);
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return bills;
    }
    
    // Reading history methods
    private void saveReadingHistory(String utilityId, String utilityType, double reading) {
        String id = UUID.randomUUID().toString();
        String sql = "INSERT INTO reading_history (id, utility_id, utility_type, reading_date, reading_value) VALUES (?, ?, ?, ?, ?)";
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, id);
            pstmt.setString(2, utilityId);
            pstmt.setString(3, utilityType);
            pstmt.setString(4, LocalDate.now().toString());
            pstmt.setDouble(5, reading);
            pstmt.executeUpdate();
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
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
 // Methods to update Electricity information
    public boolean updateElectricity(Electricity electricity) {
        String sql = "UPDATE electricity SET name = ?, provider = ?, rate_per_kwh = ?, meter_reading = ? WHERE account_number = ?";
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, electricity.getName());
            pstmt.setString(2, electricity.getProvider());
            pstmt.setDouble(3, electricity.getRatePerKwh());
            pstmt.setDouble(4, electricity.getMeterReading());
            pstmt.setString(5, electricity.getAccountNumber());
            
            int rowsAffected = pstmt.executeUpdate();
            
            // If the record was updated successfully, save the new reading to history
            if (rowsAffected > 0) {
                // Get utility ID
                String idSql = "SELECT id FROM electricity WHERE account_number = ?";
                PreparedStatement idStmt = connection.prepareStatement(idSql);
                idStmt.setString(1, electricity.getAccountNumber());
                ResultSet rs = idStmt.executeQuery();
                
                if (rs.next()) {
                    String utilityId = rs.getString("id");
                    // Save new reading to history
                    saveReadingHistory(utilityId, "electricity", electricity.getMeterReading());
                }
                
                return true;
            }
            
            return false;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean deleteElectricity(String id) {
        String sql = "DELETE FROM electricity WHERE id = ?";
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, id);
            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public Electricity getElectricityById(String id) {
        String sql = "SELECT * FROM electricity WHERE id = ?";
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, id);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                String name = rs.getString("name");
                String provider = rs.getString("provider");
                String accountNumber = rs.getString("account_number");
                double ratePerKwh = rs.getDouble("rate_per_kwh");
                double meterReading = rs.getDouble("meter_reading");
                
                Electricity electricity = new Electricity(name, provider, accountNumber, ratePerKwh);
                electricity.setMeterReading(meterReading);
                return electricity;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return null;
    }

    public Electricity getElectricityByAccountNumber(String accountNumber) {
        String sql = "SELECT * FROM electricity WHERE account_number = ?";
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, accountNumber);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                String name = rs.getString("name");
                String provider = rs.getString("provider");
                double ratePerKwh = rs.getDouble("rate_per_kwh");
                double meterReading = rs.getDouble("meter_reading");
                
                Electricity electricity = new Electricity(name, provider, accountNumber, ratePerKwh);
                electricity.setMeterReading(meterReading);
                return electricity;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return null;
    }

    // Methods to update Gas information
    public boolean updateGas(Gas gas) {
        String sql = "UPDATE gas SET name = ?, provider = ?, rate_per_unit = ?, meter_reading = ? WHERE account_number = ?";
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, gas.getName());
            pstmt.setString(2, gas.getProvider());
            pstmt.setDouble(3, gas.getRatePerUnit());
            pstmt.setDouble(4, gas.getMeterReading());
            pstmt.setString(5, gas.getAccountNumber());
            
            int rowsAffected = pstmt.executeUpdate();
            
            // If the record was updated successfully, save the new reading to history
            if (rowsAffected > 0) {
                // Get utility ID
                String idSql = "SELECT id FROM gas WHERE account_number = ?";
                PreparedStatement idStmt = connection.prepareStatement(idSql);
                idStmt.setString(1, gas.getAccountNumber());
                ResultSet rs = idStmt.executeQuery();
                
                if (rs.next()) {
                    String utilityId = rs.getString("id");
                    // Save new reading to history
                    saveReadingHistory(utilityId, "gas", gas.getMeterReading());
                }
                
                return true;
            }
            
            return false;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean deleteGas(String id) {
        String sql = "DELETE FROM gas WHERE id = ?";
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, id);
            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public Gas getGasById(String id) {
        String sql = "SELECT * FROM gas WHERE id = ?";
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, id);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                String name = rs.getString("name");
                String provider = rs.getString("provider");
                String accountNumber = rs.getString("account_number");
                double ratePerUnit = rs.getDouble("rate_per_unit");
                double meterReading = rs.getDouble("meter_reading");
                
                Gas gas = new Gas(name, provider, accountNumber, ratePerUnit);
                gas.setMeterReading(meterReading);
                return gas;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return null;
    }

    public Gas getGasByAccountNumber(String accountNumber) {
        String sql = "SELECT * FROM gas WHERE account_number = ?";
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, accountNumber);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                String name = rs.getString("name");
                String provider = rs.getString("provider");
                double ratePerUnit = rs.getDouble("rate_per_unit");
                double meterReading = rs.getDouble("meter_reading");
                
                Gas gas = new Gas(name, provider, accountNumber, ratePerUnit);
                gas.setMeterReading(meterReading);
                return gas;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return null;
    }

    // Methods to update Water information
    public boolean updateWater(Water water) {
        String sql = "UPDATE water SET name = ?, provider = ?, rate_per_cubic_meter = ?, meter_reading = ? WHERE account_number = ?";
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, water.getName());
            pstmt.setString(2, water.getProvider());
            pstmt.setDouble(3, water.getRatePerCubicMeter());
            pstmt.setDouble(4, water.getMeterReading());
            pstmt.setString(5, water.getAccountNumber());
            
            int rowsAffected = pstmt.executeUpdate();
            
            // If the record was updated successfully, save the new reading to history
            if (rowsAffected > 0) {
                // Get utility ID
                String idSql = "SELECT id FROM water WHERE account_number = ?";
                PreparedStatement idStmt = connection.prepareStatement(idSql);
                idStmt.setString(1, water.getAccountNumber());
                ResultSet rs = idStmt.executeQuery();
                
                if (rs.next()) {
                    String utilityId = rs.getString("id");
                    // Save new reading to history
                    saveReadingHistory(utilityId, "water", water.getMeterReading());
                }
                
                return true;
            }
            
            return false;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean deleteWater(String id) {
        String sql = "DELETE FROM water WHERE id = ?";
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, id);
            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public Water getWaterById(String id) {
        String sql = "SELECT * FROM water WHERE id = ?";
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, id);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                String name = rs.getString("name");
                String provider = rs.getString("provider");
                String accountNumber = rs.getString("account_number");
                double ratePerCubicMeter = rs.getDouble("rate_per_cubic_meter");
                double meterReading = rs.getDouble("meter_reading");
                
                Water water = new Water(name, provider, accountNumber, ratePerCubicMeter);
                water.setMeterReading(meterReading);
                return water;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return null;
    }

    public Water getWaterByAccountNumber(String accountNumber) {
        String sql = "SELECT * FROM water WHERE account_number = ?";
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, accountNumber);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                String name = rs.getString("name");
                String provider = rs.getString("provider");
                double ratePerCubicMeter = rs.getDouble("rate_per_cubic_meter");
                double meterReading = rs.getDouble("meter_reading");
                
                Water water = new Water(name, provider, accountNumber, ratePerCubicMeter);
                water.setMeterReading(meterReading);
                return water;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return null;
    }

    // Methods to update bill status
    public void markBillAsPaid(String billId, LocalDate paidDate) {
        String sql = "UPDATE bill SET is_paid = 1, paid_date = ? WHERE id = ?";
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, paidDate.toString());
            pstmt.setString(2, billId);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public boolean deleteBill(String billId) {
        String sql = "DELETE FROM bill WHERE id = ?";
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, billId);
            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Methods for subscription management
    public void updateSubscription(String id, Subscription subscription) {
        String sql = "UPDATE subscription SET name = ?, provider = ?, account_number = ?, " +
                     "type = ?, monthly_cost = ?, next_billing_date = ? WHERE id = ?";
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, subscription.getName());
            pstmt.setString(2, subscription.getProvider());
            pstmt.setString(3, subscription.getAccountNumber());
            pstmt.setString(4, subscription.getType().toString());
            pstmt.setDouble(5, subscription.getMonthlyCost());
            pstmt.setString(6, subscription.getNextBillingDate().toString());
            pstmt.setString(7, id);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public boolean deleteSubscription(String id) {
        String sql = "DELETE FROM subscription WHERE id = ?";
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, id);
            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public Subscription getSubscriptionById(String id) {
        String sql = "SELECT * FROM subscription WHERE id = ?";
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, id);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                String name = rs.getString("name");
                String provider = rs.getString("provider");
                String accountNumber = rs.getString("account_number");
                SubscriptionType type = SubscriptionType.valueOf(rs.getString("type"));
                double monthlyCost = rs.getDouble("monthly_cost");
                LocalDate nextBillingDate = LocalDate.parse(rs.getString("next_billing_date"));
                
                Subscription subscription = new Subscription(name, provider, accountNumber, type, monthlyCost);
                subscription.setNextBillingDate(nextBillingDate);
                return subscription;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return null;
    }

    // Methods to get reading history
    public List<ReadingHistory> getReadingHistory(String utilityId, String utilityType) {
        List<ReadingHistory> history = new ArrayList<>();
        String sql = "SELECT * FROM reading_history WHERE utility_id = ? AND utility_type = ? ORDER BY reading_date ASC";
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, utilityId);
            pstmt.setString(2, utilityType);
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                String id = rs.getString("id");
                LocalDate readingDate = LocalDate.parse(rs.getString("reading_date"));
                double readingValue = rs.getDouble("reading_value");
                
                ReadingHistory entry = new ReadingHistory(id, utilityId, utilityType, readingDate, readingValue);
                history.add(entry);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return history;
    }

    // Helper class for reading history
    public static class ReadingHistory {
        private String id;
        private String utilityId;
        private String utilityType;
        private LocalDate readingDate;
        private double readingValue;
        
        public ReadingHistory(String id, String utilityId, String utilityType, LocalDate readingDate, double readingValue) {
            this.id = id;
            this.utilityId = utilityId;
            this.utilityType = utilityType;
            this.readingDate = readingDate;
            this.readingValue = readingValue;
        }
        
        public String getId() {
            return id;
        }
        
        public String getUtilityId() {
            return utilityId;
        }
        
        public String getUtilityType() {
            return utilityType;
        }
        
        public LocalDate getReadingDate() {
            return readingDate;
        }
        
        public double getReadingValue() {
            return readingValue;
        }
    }
}