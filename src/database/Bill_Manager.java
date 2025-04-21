package database;

import models.Bill;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Manages bill-related database operations
 */
public class Bill_Manager {
    private Connection connection;
    
    public Bill_Manager(Connection connection) {
        this.connection = connection;
    }
    
    public String saveBill(Bill bill) {
        String id = UUID.randomUUID().toString();
        String sql = "INSERT INTO bill (id, utility_id, amount, consumption, issue_date, due_date, is_paid, paid_date) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, id);
            pstmt.setString(2, bill.getUtilityId());
            pstmt.setDouble(3, bill.getAmount());
            pstmt.setDouble(4, bill.getConsumption());
            pstmt.setString(5, bill.getIssueDate().toString());
            pstmt.setString(6, bill.getDueDate().toString());
            pstmt.setInt(7, bill.isPaid() ? 1 : 0);
            pstmt.setString(8, bill.getPaidDate() != null ? bill.getPaidDate().toString() : null);
            pstmt.executeUpdate();
            return id;
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }
    
    public List<Bill> getAllBills() {
        List<Bill> billList = new ArrayList<>();
        String sql = "SELECT * FROM bill";
        
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                Bill bill = extractBillFromResultSet(rs);
                billList.add(bill);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return billList;
    }
    
    public List<Bill> getBillsByUtilityId(String utilityId) {
        List<Bill> billList = new ArrayList<>();
        String sql = "SELECT * FROM bill WHERE utility_id = ?";
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, utilityId);
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                Bill bill = extractBillFromResultSet(rs);
                billList.add(bill);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return billList;
    }
    
    public Bill getBillById(String id) {
        String sql = "SELECT * FROM bill WHERE id = ?";
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, id);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return extractBillFromResultSet(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return null;
    }
    
    public boolean updateBill(Bill bill) {
        String sql = "UPDATE bill SET amount = ?, consumption = ?, issue_date = ?, due_date = ?, is_paid = ?, paid_date = ? WHERE id = ?";
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setDouble(1, bill.getAmount());
            pstmt.setDouble(2, bill.getConsumption());
            pstmt.setString(3, bill.getIssueDate().toString());
            pstmt.setString(4, bill.getDueDate().toString());
            pstmt.setInt(5, bill.isPaid() ? 1 : 0);
            pstmt.setString(6, bill.getPaidDate() != null ? bill.getPaidDate().toString() : null);
            pstmt.setString(7, bill.getId());
            
            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    public boolean markBillAsPaid(String billId) {
        String sql = "UPDATE bill SET is_paid = 1, paid_date = ? WHERE id = ?";
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, LocalDate.now().toString());
            pstmt.setString(2, billId);
            
            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    public boolean deleteBill(String id) {
        String sql = "DELETE FROM bill WHERE id = ?";
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, id);
            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    public List<Bill> getUnpaidBills() {
        List<Bill> unpaidBills = new ArrayList<>();
        String sql = "SELECT * FROM bill WHERE is_paid = 0";
        
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                Bill bill = extractBillFromResultSet(rs);
                unpaidBills.add(bill);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return unpaidBills;
    }
    
    public List<Bill> getBillsByDateRange(LocalDate startDate, LocalDate endDate) {
        List<Bill> billsInRange = new ArrayList<>();
        String sql = "SELECT * FROM bill WHERE issue_date >= ? AND issue_date <= ?";
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, startDate.toString());
            pstmt.setString(2, endDate.toString());
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                Bill bill = extractBillFromResultSet(rs);
                billsInRange.add(bill);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return billsInRange;
    }
    
    private Bill extractBillFromResultSet(ResultSet rs) throws SQLException {
        String id = rs.getString("id");
        String utilityId = rs.getString("utility_id");
        double amount = rs.getDouble("amount");
        double consumption = rs.getDouble("consumption");
        LocalDate issueDate = LocalDate.parse(rs.getString("issue_date"));
        LocalDate dueDate = LocalDate.parse(rs.getString("due_date"));
        boolean isPaid = rs.getInt("is_paid") == 1;
        String paidDateStr = rs.getString("paid_date");
        LocalDate paidDate = paidDateStr != null ? LocalDate.parse(paidDateStr) : null;
        
        Bill bill = new Bill(id, utilityId, amount, consumption, issueDate, dueDate);
        bill.setPaid(isPaid);
        bill.setPaidDate(paidDate);
        return bill;
    }
}