package database;

import models.Gas;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Manages gas-related database operations
 */
public class Gas_Manager {
    private Connection connection;
    private Reading_History_Manager historyManager;
    
    public Gas_Manager(Connection connection) {
        this.connection = connection;
        this.historyManager = new Reading_History_Manager(connection);
    }
    
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
            historyManager.saveReadingHistory(id, "gas", gas.getMeterReading());
            
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
                historyManager.saveReadingHistory(utilityId, "gas", newReading);
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
                return historyManager.getPreviousReading(utilityId, "gas");
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return previousReading;
    }
    
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
                    historyManager.saveReadingHistory(utilityId, "gas", gas.getMeterReading());
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
}