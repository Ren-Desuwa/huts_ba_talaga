package database;

import models.Water;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Manages water-related database operations
 */
public class Water_Manager {
    private Connection connection;
    private Reading_History_Manager historyManager;
    
    public Water_Manager(Connection connection) {
        this.connection = connection;
        this.historyManager = new Reading_History_Manager(connection);
    }
    
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
            historyManager.saveReadingHistory(id, "water", water.getMeterReading());
            
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
                historyManager.saveReadingHistory(utilityId, "water", newReading);
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
                return historyManager.getPreviousReading(utilityId, "water");
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return previousReading;
    }
    
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
                    historyManager.saveReadingHistory(utilityId, "water", water.getMeterReading());
                }
                
                return true;
            }
            
            return false;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

 // Water_Manager.java
    public boolean deleteWater(String accountNumber) {
        String sql = "DELETE FROM water WHERE account_number = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, accountNumber);
            int rowsAffected = stmt.executeUpdate();
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
}