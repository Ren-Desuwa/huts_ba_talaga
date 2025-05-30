package database;

import models.Electricity;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Manages electricity-related database operations
 */
public class Electricity_Manager {
    private Connection connection;
    private Reading_History_Manager historyManager;
    
    public Electricity_Manager(Connection connection) {
        this.connection = connection;
        this.historyManager = new Reading_History_Manager(connection);
    }
    
    public void saveElectricity(Electricity electricity, String userId) {
        String id = UUID.randomUUID().toString();
        String sql = "INSERT INTO electricity (id, user_id, name, provider, account_number, rate_per_kwh, meter_reading) VALUES (?, ?, ?, ?, ?, ?, ?)";
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, id);
            pstmt.setString(2, userId);
            pstmt.setString(3, electricity.getName());
            pstmt.setString(4, electricity.getProvider());
            pstmt.setString(5, electricity.getAccountNumber());
            pstmt.setDouble(6, electricity.getRatePerKwh());
            pstmt.setDouble(7, electricity.getMeterReading());
            pstmt.executeUpdate();
            
            // Save initial reading to history
            historyManager.saveReadingHistory(id, "electricity", electricity.getMeterReading());
            
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
                String id = rs.getString("id");
                String name = rs.getString("name");
                String provider = rs.getString("provider");
                String accountNumber = rs.getString("account_number");
                double ratePerKwh = rs.getDouble("rate_per_kwh");
                double meterReading = rs.getDouble("meter_reading");
                
                Electricity electricity = new Electricity(name, provider, accountNumber, ratePerKwh);
                electricity.setMeterReading(meterReading);
                electricity.setId(id);  // Ensure the ID is set
                electricityList.add(electricity);
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return electricityList;
    }
    
    public List<Electricity> getElectricityByUserId(String userId) {
        List<Electricity> electricityList = new ArrayList<>();
        String sql = "SELECT * FROM electricity WHERE user_id = ?";
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, userId);
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                String id = rs.getString("id");
                String name = rs.getString("name");
                String provider = rs.getString("provider");
                String accountNumber = rs.getString("account_number");
                double ratePerKwh = rs.getDouble("rate_per_kwh");
                double meterReading = rs.getDouble("meter_reading");
                
                Electricity electricity = new Electricity(name, provider, accountNumber, ratePerKwh);
                electricity.setMeterReading(meterReading);
                electricity.setId(id);  // Ensure the ID is set
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
                historyManager.saveReadingHistory(utilityId, "electricity", newReading);
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
                return historyManager.getPreviousReading(utilityId, "electricity");
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return previousReading;
    }
    
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
                    historyManager.saveReadingHistory(utilityId, "electricity", electricity.getMeterReading());
                }
                
                return true;
            }
            
            return false;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean deleteElectricity(String accountNumber) {
        String sql = "DELETE FROM electricity WHERE account_number = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, accountNumber);
            int rowsAffected = stmt.executeUpdate();
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
                electricity.setId(id);  // Ensure the ID is set
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
                String id = rs.getString("id");
                String name = rs.getString("name");
                String provider = rs.getString("provider");
                double ratePerKwh = rs.getDouble("rate_per_kwh");
                double meterReading = rs.getDouble("meter_reading");
                
                Electricity electricity = new Electricity(name, provider, accountNumber, ratePerKwh);
                electricity.setMeterReading(meterReading);
                electricity.setId(id);  // Ensure the ID is set
                return electricity;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return null;
    }
    
    // Primary method for adding electricity with a user ID
    public void addElectricity(Electricity electricity, String userId) {
        saveElectricity(electricity, userId);
    }
    
    // Backwards compatibility method - assigns a default or null user ID
    public void addElectricity(Electricity electricity) {
        // Log a warning that this method doesn't associate with a user
        System.out.println("WARNING: Adding electricity without user ID association");
        
        // Use null for user_id or a default value (like "system")
        String userId = null; // or "system" if you prefer a default value
        saveElectricity(electricity, userId);
    }
}