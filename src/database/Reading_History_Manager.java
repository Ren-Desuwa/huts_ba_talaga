package database;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Manages meter reading history database operations
 */
public class Reading_History_Manager {
    private Connection connection;
    
    public Reading_History_Manager(Connection connection) {
        this.connection = connection;
    }
    
    public void saveReadingHistory(String utilityId, String utilityType, double readingValue) {
        String id = UUID.randomUUID().toString();
        String sql = "INSERT INTO reading_history (id, utility_id, utility_type, reading_date, reading_value) VALUES (?, ?, ?, ?, ?)";
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, id);
            pstmt.setString(2, utilityId);
            pstmt.setString(3, utilityType);
            pstmt.setString(4, LocalDate.now().toString());
            pstmt.setDouble(5, readingValue);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    public double getPreviousReading(String utilityId, String utilityType) {
        double previousReading = 0.0;
        String sql = "SELECT reading_value FROM reading_history WHERE utility_id = ? AND utility_type = ? ORDER BY reading_date DESC LIMIT 2";
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, utilityId);
            pstmt.setString(2, utilityType);
            ResultSet rs = pstmt.executeQuery();
            
            // Skip the most recent reading
            if (rs.next() && rs.next()) {
                previousReading = rs.getDouble("reading_value");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return previousReading;
    }
    
    public List<Double> getReadingHistory(String utilityId, String utilityType) {
        List<Double> readingHistory = new ArrayList<>();
        String sql = "SELECT reading_value FROM reading_history WHERE utility_id = ? AND utility_type = ? ORDER BY reading_date ASC";
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, utilityId);
            pstmt.setString(2, utilityType);
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                readingHistory.add(rs.getDouble("reading_value"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return readingHistory;
    }
    
    public List<Double> getReadingHistoryByDateRange(String utilityId, String utilityType, LocalDate startDate, LocalDate endDate) {
        List<Double> readingHistory = new ArrayList<>();
        String sql = "SELECT reading_value FROM reading_history WHERE utility_id = ? AND utility_type = ? AND reading_date >= ? AND reading_date <= ? ORDER BY reading_date ASC";
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, utilityId);
            pstmt.setString(2, utilityType);
            pstmt.setString(3, startDate.toString());
            pstmt.setString(4, endDate.toString());
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                readingHistory.add(rs.getDouble("reading_value"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return readingHistory;
    }
    
    public double getLatestReading(String utilityId, String utilityType) {
        double latestReading = 0.0;
        String sql = "SELECT reading_value FROM reading_history WHERE utility_id = ? AND utility_type = ? ORDER BY reading_date DESC LIMIT 1";
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, utilityId);
            pstmt.setString(2, utilityType);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                latestReading = rs.getDouble("reading_value");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return latestReading;
    }
    
    public double getConsumptionBetweenDates(String utilityId, String utilityType, LocalDate startDate, LocalDate endDate) {
        double consumption = 0.0;
        String sql = "SELECT reading_value, reading_date FROM reading_history WHERE utility_id = ? AND utility_type = ? AND reading_date >= ? AND reading_date <= ? ORDER BY reading_date ASC";
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, utilityId);
            pstmt.setString(2, utilityType);
            pstmt.setString(3, startDate.toString());
            pstmt.setString(4, endDate.toString());
            ResultSet rs = pstmt.executeQuery();
            
            Double firstReading = null;
            Double lastReading = null;
            
            while (rs.next()) {
                double reading = rs.getDouble("reading_value");
                if (firstReading == null) {
                    firstReading = reading;
                }
                lastReading = reading;
            }
            
            if (firstReading != null && lastReading != null) {
                consumption = lastReading - firstReading;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return consumption > 0 ? consumption : 0.0;
    }
    
    public boolean deleteReadingHistory(String utilityId) {
        String sql = "DELETE FROM reading_history WHERE utility_id = ?";
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, utilityId);
            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}