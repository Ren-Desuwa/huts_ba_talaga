package database;

import models.Electricity;
import models.ElectricityBill;
import java.sql.*;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
    
    // NEW METHODS FOR ELECTRICITY_PANEL
    
    // Add an electricity bill directly (for the panel's bill add functionality)
    public boolean addElectricityBill(String userId, String date, double amount, double kwhUsed, String notes) {
        try {
            // Format date properly
            LocalDate billDate = LocalDate.parse(date);
            
            // Create a unique ID for the bill
            String billId = UUID.randomUUID().toString();
            
            // Get the first electricity account for this user (simplification)
            List<Electricity> accounts = getElectricityByUserId(userId);
            if (accounts.isEmpty()) {
                // If no electricity account exists, create one
                Electricity electricity = new Electricity("Default Account", "Default Provider", 
                                                        "ACC" + System.currentTimeMillis(), amount / (kwhUsed > 0 ? kwhUsed : 100));
                electricity.setMeterReading(kwhUsed);
                addElectricity(electricity, userId);
                
                // Get the ID we just created
                String utilityId = getElectricityByAccountNumber(electricity.getAccountNumber()).getId();
                
                // Insert the bill
                insertBill(billId, utilityId, userId, amount, kwhUsed, billDate);
                
                // Update statistics
                updateMonthlyStatistics(userId, billDate, kwhUsed, amount);
                
                return true;
            } else {
                // Use the first electricity account
                Electricity electricity = accounts.get(0);
                
                // Insert the bill
                insertBill(billId, electricity.getId(), userId, amount, kwhUsed, billDate);
                
                // If kWh used is provided, update the meter reading
                if (kwhUsed > 0) {
                    // Get the current reading
                    double currentReading = electricity.getMeterReading();
                    
                    // Update to reflect additional usage
                    electricity.setMeterReading(currentReading + kwhUsed);
                    updateElectricity(electricity);
                }
                
                // Update statistics
                updateMonthlyStatistics(userId, billDate, kwhUsed, amount);
                
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    
    // Private helper method to insert a bill record
    private void insertBill(String billId, String utilityId, String userId, double amount, double consumption, LocalDate billDate) throws SQLException {
        String sql = "INSERT INTO bill (id, utility_id, utility_type, user_id, amount, consumption, issue_date, due_date, is_paid) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        
        // Calculate due date (30 days after issue date)
        LocalDate dueDate = billDate.plusDays(30);
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, billId);
            pstmt.setString(2, utilityId);
            pstmt.setString(3, "electricity");
            pstmt.setString(4, userId);
            pstmt.setDouble(5, amount);
            pstmt.setDouble(6, consumption);
            pstmt.setString(7, billDate.toString());
            pstmt.setString(8, dueDate.toString());
            pstmt.setInt(9, 0); // Not paid by default
            pstmt.executeUpdate();
        }
    }
    
    // Get all electricity bills for a user
    public List<ElectricityBill> getBillsByUserId(String userId) {
        List<ElectricityBill> bills = new ArrayList<>();
        
        String sql = "SELECT b.*, e.name, e.account_number FROM bill b " +
                    "INNER JOIN electricity e ON b.utility_id = e.id " +
                    "WHERE b.user_id = ? AND b.utility_type = 'electricity' " +
                    "ORDER BY b.issue_date DESC";
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, userId);
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                String billId = rs.getString("id");
                String utilityId = rs.getString("utility_id");
                double amount = rs.getDouble("amount");
                double consumption = rs.getDouble("consumption");
                LocalDate issueDate = LocalDate.parse(rs.getString("issue_date"));
                LocalDate dueDate = LocalDate.parse(rs.getString("due_date"));
                boolean isPaid = rs.getInt("is_paid") == 1;
                String accountName = rs.getString("name");
                String accountNumber = rs.getString("account_number");
                
                ElectricityBill bill = new ElectricityBill(billId, utilityId, userId, amount, consumption, issueDate);	
                bill.setDueDate(dueDate);
                bill.setPaid(isPaid);
                bill.setAccountName(accountName);
                bill.setAccountNumber(accountNumber);
                
                bills.add(bill);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return bills;
    }
    
    // Get monthly usage data for the histogram in the Electricity Panel
    public Map<String, Double> getMonthlyUsageData(String userId, int year) {
        Map<String, Double> monthlyData = new HashMap<>();
        
        // Initialize with all months
        for (int i = 1; i <= 12; i++) {
            YearMonth ym = YearMonth.of(year, i);
            String monthName = ym.getMonth().toString();
            monthlyData.put(monthName, 0.0);
        }
        
        String sql = "SELECT strftime('%m', issue_date) as month, SUM(consumption) as total_kwh " +
                    "FROM bill WHERE user_id = ? AND utility_type = 'electricity' " +
                    "AND strftime('%Y', issue_date) = ? " +
                    "GROUP BY strftime('%m', issue_date)";
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, userId);
            pstmt.setString(2, String.valueOf(year));
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                int monthNum = Integer.parseInt(rs.getString("month"));
                double totalKwh = rs.getDouble("total_kwh");
                
                YearMonth ym = YearMonth.of(year, monthNum);
                String monthName = ym.getMonth().toString();
                monthlyData.put(monthName, totalKwh);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return monthlyData;
    }
    
    // Get the year-to-date statistics for electricity usage
    public double getTotalYearlyUsage(String userId, int year) {
        String sql = "SELECT SUM(consumption) as total_kwh FROM bill " +
                    "WHERE user_id = ? AND utility_type = 'electricity' " +
                    "AND strftime('%Y', issue_date) = ?";
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, userId);
            pstmt.setString(2, String.valueOf(year));
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return rs.getDouble("total_kwh");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return 0.0;
    }
    
    // Get the total cost for the year
    public double getTotalYearlyCost(String userId, int year) {
        String sql = "SELECT SUM(amount) as total_cost FROM bill " +
                    "WHERE user_id = ? AND utility_type = 'electricity' " +
                    "AND strftime('%Y', issue_date) = ?";
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, userId);
            pstmt.setString(2, String.valueOf(year));
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return rs.getDouble("total_cost");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return 0.0;
    }
    
    // Get the average monthly cost
    public double getAverageMonthlyBill(String userId, int year) {
        String sql = "SELECT AVG(monthly_total) as avg_monthly " +
                    "FROM (" +
                    "  SELECT strftime('%m', issue_date) as month, SUM(amount) as monthly_total " +
                    "  FROM bill WHERE user_id = ? AND utility_type = 'electricity' " +
                    "  AND strftime('%Y', issue_date) = ? " +
                    "  GROUP BY strftime('%m', issue_date)" +
                    ")";
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, userId);
            pstmt.setString(2, String.valueOf(year));
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return rs.getDouble("avg_monthly");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return 0.0;
    }
    
    // Update monthly statistics table (for performance optimization)
    private void updateMonthlyStatistics(String userId, LocalDate date, double kwhUsed, double amount) {
        int year = date.getYear();
        int month = date.getMonthValue();
        
        // Check if entry exists
        String checkSql = "SELECT id FROM electricity_stats WHERE user_id = ? AND year = ? AND month = ?";
        
        try (PreparedStatement checkStmt = connection.prepareStatement(checkSql)) {
            checkStmt.setString(1, userId);
            checkStmt.setInt(2, year);
            checkStmt.setInt(3, month);
            ResultSet rs = checkStmt.executeQuery();
            
            if (rs.next()) {
                // Update existing entry
                String updateSql = "UPDATE electricity_stats SET total_kwh = total_kwh + ?, total_cost = total_cost + ? " +
                                 "WHERE user_id = ? AND year = ? AND month = ?";
                
                try (PreparedStatement updateStmt = connection.prepareStatement(updateSql)) {
                    updateStmt.setDouble(1, kwhUsed);
                    updateStmt.setDouble(2, amount);
                    updateStmt.setString(3, userId);
                    updateStmt.setInt(4, year);
                    updateStmt.setInt(5, month);
                    updateStmt.executeUpdate();
                }
            } else {
                // Create new entry
            	String insertSql = "INSERT INTO electricity_stats (id, user_id, year, month, total_kwh, total_cost) VALUES (?, ?, ?, ?, ?, ?)";
                
                try (PreparedStatement insertStmt = connection.prepareStatement(insertSql)) {
                    String statId = UUID.randomUUID().toString();
                    insertStmt.setString(1, statId);
                    insertStmt.setString(2, userId);
                    insertStmt.setInt(3, year);
                    insertStmt.setInt(4, month);
                    insertStmt.setDouble(5, kwhUsed);
                    insertStmt.setDouble(6, amount);
                    insertStmt.executeUpdate();
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    // Get trending data for electricity consumption
    public Map<String, Double> getMonthlyTrend(String userId, int year) {
        Map<String, Double> trendData = new LinkedHashMap<>();
        DateTimeFormatter monthFormatter = DateTimeFormatter.ofPattern("MMMM");
        
        // Calculate the percentage for each month compared to the highest month
        Map<String, Double> monthlyUsage = getMonthlyUsageData(userId, year);
        
        // Find the highest month
        double maxUsage = 0.0;
        for (Double usage : monthlyUsage.values()) {
            if (usage > maxUsage) {
                maxUsage = usage;
            }
        }
        
        // Calculate percentage for each month
        for (int i = 1; i <= 12; i++) {
            YearMonth ym = YearMonth.of(year, i);
            String monthName = ym.getMonth().getDisplayName(java.time.format.TextStyle.FULL, java.util.Locale.getDefault());
            double usage = monthlyUsage.getOrDefault(ym.getMonth().toString(), 0.0);
            
            // Calculate percentage (avoid divide by zero)
            double percentage = (maxUsage > 0) ? (usage / maxUsage) * 100.0 : 0.0;
            trendData.put(monthName, Math.round(percentage * 10) / 10.0); // Round to one decimal place
        }
        
        return trendData;
    }
    
    // Mark an electricity bill as paid
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
    
    // Get billing history statistics - number of bills by status
    public Map<String, Integer> getBillingStatsByUserId(String userId) {
        Map<String, Integer> stats = new HashMap<>();
        stats.put("paid", 0);
        stats.put("unpaid", 0);
        stats.put("overdue", 0);
        
        String sql = "SELECT COUNT(*) as count, is_paid, " +
                    "CASE WHEN due_date < date('now') AND is_paid = 0 THEN 1 ELSE 0 END as is_overdue " +
                    "FROM bill WHERE user_id = ? AND utility_type = 'electricity' " +
                    "GROUP BY is_paid, is_overdue";
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, userId);
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                int count = rs.getInt("count");
                boolean isPaid = rs.getInt("is_paid") == 1;
                boolean isOverdue = rs.getInt("is_overdue") == 1;
                
                if (isPaid) {
                    stats.put("paid", count);
                } else if (isOverdue) {
                    stats.put("overdue", count);
                } else {
                    stats.put("unpaid", count);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return stats;
    }
    
    // Get the most recent bill for a specific user
    public ElectricityBill getMostRecentBill(String userId) {
        String sql = "SELECT b.*, e.name, e.account_number FROM bill b " +
                    "INNER JOIN electricity e ON b.utility_id = e.id " +
                    "WHERE b.user_id = ? AND b.utility_type = 'electricity' " +
                    "ORDER BY b.issue_date DESC LIMIT 1";
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, userId);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                String billId = rs.getString("id");
                String utilityId = rs.getString("utility_id");
                double amount = rs.getDouble("amount");
                double consumption = rs.getDouble("consumption");
                LocalDate issueDate = LocalDate.parse(rs.getString("issue_date"));
                LocalDate dueDate = LocalDate.parse(rs.getString("due_date"));
                boolean isPaid = rs.getInt("is_paid") == 1;
                String accountName = rs.getString("name");
                String accountNumber = rs.getString("account_number");
                
                ElectricityBill bill = new ElectricityBill(billId, utilityId, userId, amount, consumption, issueDate);
                bill.setDueDate(dueDate);
                bill.setPaid(isPaid);
                bill.setAccountName(accountName);
                bill.setAccountNumber(accountNumber);
                
                return bill;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return null;
    }
    
    // Get all bills that are overdue
    public List<ElectricityBill> getOverdueBills(String userId) {
        List<ElectricityBill> overdueBills = new ArrayList<>();
        String sql = "SELECT b.*, e.name, e.account_number FROM bill b " +
                    "INNER JOIN electricity e ON b.utility_id = e.id " +
                    "WHERE b.user_id = ? AND b.utility_type = 'electricity' " +
                    "AND b.is_paid = 0 AND b.due_date < date('now') " +
                    "ORDER BY b.due_date ASC";
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, userId);
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                String billId = rs.getString("id");
                String utilityId = rs.getString("utility_id");
                double amount = rs.getDouble("amount");
                double consumption = rs.getDouble("consumption");
                LocalDate issueDate = LocalDate.parse(rs.getString("issue_date"));
                LocalDate dueDate = LocalDate.parse(rs.getString("due_date"));
                String accountName = rs.getString("name");
                String accountNumber = rs.getString("account_number");
                
                ElectricityBill bill = new ElectricityBill(billId, utilityId, userId, amount, consumption, issueDate);
                bill.setDueDate(dueDate);
                bill.setPaid(false);
                bill.setAccountName(accountName);
                bill.setAccountNumber(accountNumber);
                
                overdueBills.add(bill);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return overdueBills;
    }
    
    // Calculate predictions for future consumption based on historical usage
    public Map<String, Double> predictFutureConsumption(String userId, int months) {
        Map<String, Double> predictions = new LinkedHashMap<>();
        LocalDate currentDate = LocalDate.now();
        
        // Get historical usage data for the past year
        Map<String, Double> historicalData = new HashMap<>();
        String sql = "SELECT strftime('%Y-%m', issue_date) as month, AVG(consumption) as avg_consumption " +
                    "FROM bill WHERE user_id = ? AND utility_type = 'electricity' " +
                    "GROUP BY strftime('%Y-%m', issue_date) " +
                    "ORDER BY month DESC LIMIT 12";
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, userId);
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                String month = rs.getString("month");
                double consumption = rs.getDouble("avg_consumption");
                historicalData.put(month, consumption);
            }
            
            // If we have historical data, use it for predictions
            if (!historicalData.isEmpty()) {
                // Calculate average monthly consumption
                double sum = 0.0;
                for (Double value : historicalData.values()) {
                    sum += value;
                }
                double avgConsumption = sum / historicalData.size();
                
                // Generate predictions for future months
                for (int i = 1; i <= months; i++) {
                    YearMonth futureMonth = YearMonth.from(currentDate).plusMonths(i);
                    String monthName = futureMonth.getMonth().getDisplayName(java.time.format.TextStyle.FULL, java.util.Locale.getDefault());
                    predictions.put(monthName + " " + futureMonth.getYear(), avgConsumption);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return predictions;
    }
    
    // Calculate cost projections based on historical usage and current rates
    public Map<String, Double> projectCosts(String userId, int months) {
        Map<String, Double> projections = new LinkedHashMap<>();
        
        // Get consumption projections
        Map<String, Double> consumptionProjections = predictFutureConsumption(userId, months);
        
        // Get average rate per kWh for this user's accounts
        double avgRate = getAverageRateForUser(userId);
        
        // Calculate cost projections
        for (Map.Entry<String, Double> entry : consumptionProjections.entrySet()) {
            double projectedCost = entry.getValue() * avgRate;
            projections.put(entry.getKey(), projectedCost);
        }
        
        return projections;
    }
    
    // Helper method to get average electricity rate for a user
    private double getAverageRateForUser(String userId) {
        double avgRate = 0.0;
        
        String sql = "SELECT AVG(rate_per_kwh) as avg_rate FROM electricity WHERE user_id = ?";
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, userId);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                avgRate = rs.getDouble("avg_rate");
            }
            
            // If no rates found, use a default value
            if (avgRate <= 0) {
                avgRate = 0.12; // Default rate per kWh
            }
        } catch (SQLException e) {
            e.printStackTrace();
            avgRate = 0.12; // Default in case of error
        }
        
        return avgRate;
    }
    
    // Export electricity consumption data to CSV format
    public String exportConsumptionDataToCSV(String userId, int year) {
        StringBuilder csv = new StringBuilder();
        
        // Add CSV header
        csv.append("Month,Consumption (kWh),Cost ($)\n");
        
        // Get monthly data
        String sql = "SELECT strftime('%m', issue_date) as month, " +
                    "SUM(consumption) as total_consumption, " +
                    "SUM(amount) as total_cost " +
                    "FROM bill WHERE user_id = ? AND utility_type = 'electricity' " +
                    "AND strftime('%Y', issue_date) = ? " +
                    "GROUP BY strftime('%m', issue_date) " +
                    "ORDER BY month ASC";
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, userId);
            pstmt.setString(2, String.valueOf(year));
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                int monthNum = Integer.parseInt(rs.getString("month"));
                double consumption = rs.getDouble("total_consumption");
                double cost = rs.getDouble("total_cost");
                
                YearMonth ym = YearMonth.of(year, monthNum);
                String monthName = ym.getMonth().getDisplayName(java.time.format.TextStyle.FULL, java.util.Locale.getDefault());
                
                csv.append(String.format("%s,%,.2f,%,.2f\n", monthName, consumption, cost));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return csv.toString();
    }
    
    // Compare usage with similar households in the region (mocked data for demo)
    public Map<String, Object> compareWithNeighbors(String userId, int month, int year) {
        Map<String, Object> comparisonData = new HashMap<>();
        
        // Get user's consumption for specified month
        double userConsumption = getUserConsumptionForMonth(userId, month, year);
        
        // Mock data for neighborhood average (this would come from a real regional database)
        double neighborhoodAvg = estimateNeighborhoodAverage(month, year);
        
        // Calculate percentile (for demo purposes)
        int percentile = (userConsumption < neighborhoodAvg) ? 
                         (int)(100 * (userConsumption / neighborhoodAvg)) : 
                         100 - (int)(20 * (userConsumption / neighborhoodAvg));
        
        // Limit percentile to valid range
        percentile = Math.max(1, Math.min(99, percentile));
        
        // Add data to response
        comparisonData.put("userConsumption", userConsumption);
        comparisonData.put("neighborhoodAverage", neighborhoodAvg);
        comparisonData.put("percentile", percentile);
        comparisonData.put("isBelowAverage", userConsumption < neighborhoodAvg);
        
        return comparisonData;
    }
    
    // Helper method to get user's consumption for a specific month
    private double getUserConsumptionForMonth(String userId, int month, int year) {
        String sql = "SELECT SUM(consumption) as total_consumption FROM bill " +
                    "WHERE user_id = ? AND utility_type = 'electricity' " +
                    "AND strftime('%m', issue_date) = ? AND strftime('%Y', issue_date) = ?";
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, userId);
            pstmt.setString(2, String.format("%02d", month));
            pstmt.setString(3, String.valueOf(year));
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return rs.getDouble("total_consumption");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return 0.0;
    }
    
    // Mock method to estimate neighborhood average (would use real data in production)
    private double estimateNeighborhoodAverage(int month, int year) {
        // Seasonal variations in a mock dataset
        double[] monthlyAverages = {
            750, 680, 630, 580, 520, 500, // Jan-Jun
            520, 540, 560, 600, 650, 720  // Jul-Dec
        };
        
        return monthlyAverages[month - 1]; // Adjust for 0-based index
    }
}