package database;

import models.Subscription;
import models.SubscriptionType; // Add this import
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Manages subscription-related database operations
 */
public class Subscription_Manager {
    private Connection connection;
    
    public Subscription_Manager(Connection connection) {
        this.connection = connection;
    }
    
    public void saveSubscription(Subscription subscription) {
        String id = UUID.randomUUID().toString();
        String sql = "INSERT INTO subscription (id, name, provider, account_number, type, monthly_cost, next_billing_date) VALUES (?, ?, ?, ?, ?, ?, ?)";
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, id);
            pstmt.setString(2, subscription.getName());
            pstmt.setString(3, subscription.getProvider());
            pstmt.setString(4, subscription.getAccountNumber());
            pstmt.setString(5, subscription.getType().toString()); // Changed to get the type as a string
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
                Subscription subscription = extractSubscriptionFromResultSet(rs);
                subscriptionList.add(subscription);
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return subscriptionList;
    }
    
    public List<Subscription> getSubscriptionsByType(SubscriptionType type) { // Changed parameter type
        List<Subscription> subscriptionList = new ArrayList<>();
        String sql = "SELECT * FROM subscription WHERE type = ?";
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, type.toString());
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                Subscription subscription = extractSubscriptionFromResultSet(rs);
                subscriptionList.add(subscription);
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return subscriptionList;
    }
    
    public Subscription getSubscriptionById(String id) {
        String sql = "SELECT * FROM subscription WHERE id = ?";
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, id);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return extractSubscriptionFromResultSet(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return null;
    }
    
    public Subscription getSubscriptionByAccountNumber(String accountNumber) {
        String sql = "SELECT * FROM subscription WHERE account_number = ?";
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, accountNumber);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return extractSubscriptionFromResultSet(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return null;
    }
    
    public boolean updateSubscription(Subscription subscription) {
        String sql = "UPDATE subscription SET name = ?, provider = ?, type = ?, monthly_cost = ?, next_billing_date = ? WHERE account_number = ?";
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, subscription.getName());
            pstmt.setString(2, subscription.getProvider());
            pstmt.setString(3, subscription.getType().toString()); // Changed to get the type as a string
            pstmt.setDouble(4, subscription.getMonthlyCost());
            pstmt.setString(5, subscription.getNextBillingDate().toString());
            pstmt.setString(6, subscription.getAccountNumber());
            
            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
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
    
    public List<Subscription> getDueBillingSubscriptions(int daysThreshold) {
        List<Subscription> dueBillingList = new ArrayList<>();
        LocalDate thresholdDate = LocalDate.now().plusDays(daysThreshold);
        String sql = "SELECT * FROM subscription WHERE next_billing_date <= ?";
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, thresholdDate.toString());
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                Subscription subscription = extractSubscriptionFromResultSet(rs);
                dueBillingList.add(subscription);
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return dueBillingList;
    }
    
    public void updateNextBillingDate(String subscriptionId, LocalDate nextBillingDate) {
        String sql = "UPDATE subscription SET next_billing_date = ? WHERE id = ?";
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, nextBillingDate.toString());
            pstmt.setString(2, subscriptionId);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    private Subscription extractSubscriptionFromResultSet(ResultSet rs) throws SQLException {
        String id = rs.getString("id");
        String name = rs.getString("name");
        String provider = rs.getString("provider");
        String accountNumber = rs.getString("account_number");
        String typeStr = rs.getString("type");
        SubscriptionType type = SubscriptionType.valueOf(typeStr); // Convert string to enum
        double monthlyCost = rs.getDouble("monthly_cost");
        LocalDate nextBillingDate = LocalDate.parse(rs.getString("next_billing_date"));
        
        Subscription subscription = new Subscription(name, provider, accountNumber, type, monthlyCost);
        subscription.setId(id); // Set the ID from the database
        subscription.setNextBillingDate(nextBillingDate);
        return subscription;
    }
}