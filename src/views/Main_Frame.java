package views;

import models.*;
import javax.swing.*;

import database.Database_Manager;

import java.awt.*;
import java.awt.List;
import java.awt.event.*;
import java.util.*;

public class Main_Frame extends JFrame {
    // In-memory storage
    private static java.util.List<Electricity> electricityAccounts = new ArrayList<>();
    private static java.util.List<Gas> gasAccounts = new ArrayList<>();
    private static java.util.List<Water> waterAccounts = new ArrayList<>();
    private static java.util.List<Subscription> subscriptions = new ArrayList<>();
    
    // Store previous readings for bill calculation
    private static Map<String, Double> previousElectricityReadings = new HashMap<>();
    private static Map<String, Double> previousGasReadings = new HashMap<>();
    private static Map<String, Double> previousWaterReadings = new HashMap<>();
    
 // UI components
    private JPanel mainPanel;
    private JPanel menuPanel;
    private JPanel contentPanel;
    
 // Database manager instance
    private Database_Manager dbManager;
    
    public Main_Frame() {
        // Initialize database manager
        dbManager = Database_Manager.getInstance();
        
        // Set up frame properties
        setTitle("House Utility Management System");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        
        // Create main panel with BorderLayout
        mainPanel = new JPanel(new BorderLayout());
        
        // Create menu panel on the left
        menuPanel = new JPanel();
        menuPanel.setLayout(new BoxLayout(menuPanel, BoxLayout.Y_AXIS));
        menuPanel.setBackground(new Color(60, 63, 65));
        menuPanel.setPreferredSize(new Dimension(200, getHeight()));
        
        // Create content panel for the right
        contentPanel = new JPanel(new BorderLayout());
        contentPanel.setBackground(new Color(240, 240, 240));
        
        // Add welcome panel to content area
        showWelcomePanel();
        
        // Create menu buttons
        addMenuButtons();
        
        // Add panels to the main frame
        mainPanel.add(menuPanel, BorderLayout.WEST);
        mainPanel.add(contentPanel, BorderLayout.CENTER);
        
        // Add main panel to frame
        add(mainPanel);
        
        // Add sample data
        addSampleData();
        
        // Add window listener to close database connection when application closes
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                dbManager.closeConnection();
            }
        });
    }

    private void addMenuButtons() {
        JButton dashboardBtn = createMenuButton("Dashboard");
        JButton electricityBtn = createMenuButton("Electricity");
        JButton gasBtn = createMenuButton("Gas");
        JButton waterBtn = createMenuButton("Water");
        JButton subscriptionsBtn = createMenuButton("Subscriptions");
        JButton summaryBtn = createMenuButton("Summary");
        
        // Add action listeners
        dashboardBtn.addActionListener(e -> showWelcomePanel());
        electricityBtn.addActionListener(e -> showElectricityPanel());
        gasBtn.addActionListener(e -> showGasPanel());
        waterBtn.addActionListener(e -> showWaterPanel());
        subscriptionsBtn.addActionListener(e -> showSubscriptionsPanel());
        summaryBtn.addActionListener(e -> showSummaryPanel());
        
        // Add buttons to menu panel
        menuPanel.add(Box.createRigidArea(new Dimension(0, 20)));
        menuPanel.add(createTitleLabel("HUMS"));
        menuPanel.add(Box.createRigidArea(new Dimension(0, 20)));
        menuPanel.add(dashboardBtn);
        menuPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        menuPanel.add(electricityBtn);
        menuPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        menuPanel.add(gasBtn);
        menuPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        menuPanel.add(waterBtn);
        menuPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        menuPanel.add(subscriptionsBtn);
        menuPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        menuPanel.add(summaryBtn);
        menuPanel.add(Box.createVerticalGlue());
    }

    private JButton createMenuButton(String text) {
        JButton button = new JButton(text);
        button.setAlignmentX(Component.CENTER_ALIGNMENT);
        button.setMaximumSize(new Dimension(180, 40));
        button.setFocusPainted(false);
        button.setBackground(new Color(80, 87, 94));
        button.setForeground(Color.WHITE);
        button.setBorderPainted(false);
        button.setFont(new Font("Arial", Font.PLAIN, 14));
        
        // Add hover effect
        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                button.setBackground(new Color(100, 107, 114));
            }
            
            @Override
            public void mouseExited(MouseEvent e) {
                button.setBackground(new Color(80, 87, 94));
            }
        });
        
        return button;
    }
    
	private JLabel createTitleLabel(String text) {
        JLabel label = new JLabel(text);
        label.setAlignmentX(Component.CENTER_ALIGNMENT);
        label.setForeground(Color.WHITE);
        label.setFont(new Font("Arial", Font.BOLD, 24));
        return label;
    }
    
	private void showWelcomePanel() {
        // Clear existing content
        contentPanel.removeAll();
        
        // Get data from database
        List<Electricity> electricityAccounts = dbManager.getAllElectricity();
        List<Gas> gasAccounts = dbManager.getAllGas();
        List<Water> waterAccounts = dbManager.getAllWater();
        List<Subscription> subscriptions = dbManager.getAllSubscriptions();
        
        // Create welcome panel
        JPanel welcomePanel = new JPanel(new BorderLayout());
        welcomePanel.setBackground(new Color(240, 240, 240));
        
        JLabel welcomeLabel = new JLabel("Welcome to House Utility Management System");
        welcomeLabel.setFont(new Font("Arial", Font.BOLD, 24));
        welcomeLabel.setHorizontalAlignment(JLabel.CENTER);
        welcomeLabel.setBorder(BorderFactory.createEmptyBorder(30, 0, 30, 0));
        
        JPanel statsPanel = new JPanel(new GridLayout(2, 2, 20, 20));
        statsPanel.setBackground(new Color(240, 240, 240));
        statsPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        statsPanel.add(createStatPanel("Electricity Accounts", electricityAccounts.size(), new Color(52, 152, 219)));
        statsPanel.add(createStatPanel("Gas Accounts", gasAccounts.size(), new Color(155, 89, 182)));
        statsPanel.add(createStatPanel("Water Accounts", waterAccounts.size(), new Color(46, 204, 113)));
        statsPanel.add(createStatPanel("Active Subscriptions", subscriptions.size(), new Color(230, 126, 34)));
        
        welcomePanel.add(welcomeLabel, BorderLayout.NORTH);
        welcomePanel.add(statsPanel, BorderLayout.CENTER);
        
        // Add welcome panel to content panel
        contentPanel.add(welcomePanel, BorderLayout.CENTER);
        
        // Refresh UI
        contentPanel.revalidate();
        contentPanel.repaint();
    }
    
    private JPanel createStatPanel(String title, int count, Color color) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createLineBorder(color, 2));
        
        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 16));
        titleLabel.setHorizontalAlignment(JLabel.CENTER);
        titleLabel.setBorder(BorderFactory.createEmptyBorder(10, 0, 5, 0));
        
        JLabel countLabel = new JLabel(String.valueOf(count));
        countLabel.setFont(new Font("Arial", Font.BOLD, 48));
        countLabel.setForeground(color);
        countLabel.setHorizontalAlignment(JLabel.CENTER);
        
        panel.add(titleLabel, BorderLayout.NORTH);
        panel.add(countLabel, BorderLayout.CENTER);
        
        return panel;
    }
    
    private void showElectricityPanel() {
        // Clear existing content
        contentPanel.removeAll();
        
        // Create electricity panel
        JPanel electricityPanel = new JPanel(new BorderLayout());
        electricityPanel.setBackground(new Color(240, 240, 240));
        
        // Add title
        JLabel titleLabel = new JLabel("Electricity Management");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
        titleLabel.setHorizontalAlignment(JLabel.CENTER);
        titleLabel.setBorder(BorderFactory.createEmptyBorder(15, 0, 15, 0));
        
        // Create buttons panel
        JPanel buttonsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        buttonsPanel.setBackground(new Color(240, 240, 240));
        buttonsPanel.setBorder(BorderFactory.createEmptyBorder(0, 10, 10, 10));
        
        JButton addButton = new JButton("Add Account");
        JButton updateButton = new JButton("Update Reading");
        JButton calculateButton = new JButton("Calculate Bill");
        
        addButton.addActionListener(e -> addElectricityAccount());
        updateButton.addActionListener(e -> updateElectricityReading());
        calculateButton.addActionListener(e -> calculateElectricityBill());
        
        buttonsPanel.add(addButton);
        buttonsPanel.add(updateButton);
        buttonsPanel.add(calculateButton);
        
        // Create table for data
        JPanel tablePanel = new JPanel(new BorderLayout());
        tablePanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        String[] columnNames = {"Name", "Provider", "Account Number", "Current Reading", "Rate ($/kWh)"};
        Object[][] data = new Object[electricityAccounts.size()][5];
        
        for (int i = 0; i < electricityAccounts.size(); i++) {
            Electricity account = electricityAccounts.get(i);
            data[i][0] = account.getName();
            data[i][1] = account.getProvider();
            data[i][2] = account.getAccountNumber();
            data[i][3] = account.getMeterReading();
            data[i][4] = account.getRatePerKwh();
        }
        
        JTable table = new JTable(data, columnNames);
        JScrollPane scrollPane = new JScrollPane(table);
        tablePanel.add(scrollPane, BorderLayout.CENTER);
        
        // Add components to electricity panel
        electricityPanel.add(titleLabel, BorderLayout.NORTH);
        electricityPanel.add(buttonsPanel, BorderLayout.SOUTH);
        electricityPanel.add(tablePanel, BorderLayout.CENTER);
        
        // Add electricity panel to content panel
        contentPanel.add(electricityPanel, BorderLayout.CENTER);
        
        // Refresh UI
        contentPanel.revalidate();
        contentPanel.repaint();
    }
    
    private void addElectricityAccount() {
        // Create a dialog for adding a new electricity account
        JDialog dialog = new JDialog(this, "Add Electricity Account", true);
        dialog.setSize(400, 300);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new BorderLayout());
        
        JPanel formPanel = new JPanel(new GridLayout(5, 2, 10, 10));
        formPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        JLabel nameLabel = new JLabel("Account Name:");
        JTextField nameField = new JTextField(20);
        
        JLabel providerLabel = new JLabel("Provider:");
        JTextField providerField = new JTextField(20);
        
        JLabel accountLabel = new JLabel("Account Number:");
        JTextField accountField = new JTextField(20);
        
        JLabel rateLabel = new JLabel("Rate per kWh ($):");
        JTextField rateField = new JTextField(20);
        
        JLabel readingLabel = new JLabel("Initial Reading (kWh):");
        JTextField readingField = new JTextField(20);
        
        formPanel.add(nameLabel);
        formPanel.add(nameField);
        formPanel.add(providerLabel);
        formPanel.add(providerField);
        formPanel.add(accountLabel);
        formPanel.add(accountField);
        formPanel.add(rateLabel);
        formPanel.add(rateField);
        formPanel.add(readingLabel);
        formPanel.add(readingField);
        
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton cancelButton = new JButton("Cancel");
        JButton saveButton = new JButton("Save");
        
        cancelButton.addActionListener(e -> dialog.dispose());
        saveButton.addActionListener(e -> {
            try {
                String name = nameField.getText();
                String provider = providerField.getText();
                String accountNumber = accountField.getText();
                double rate = Double.parseDouble(rateField.getText());
                double reading = Double.parseDouble(readingField.getText());
                
                Electricity electricity = new Electricity(name, provider, accountNumber, rate);
                electricity.setMeterReading(reading);
                electricityAccounts.add(electricity);
                previousElectricityReadings.put(accountNumber, reading);
                
                dialog.dispose();
                showElectricityPanel(); // Refresh the panel
                
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(dialog, 
                    "Please enter valid numbers for rate and reading.", 
                    "Input Error", JOptionPane.ERROR_MESSAGE);
            }
        });
        
        buttonPanel.add(cancelButton);
        buttonPanel.add(saveButton);
        
        dialog.add(formPanel, BorderLayout.CENTER);
        dialog.add(buttonPanel, BorderLayout.SOUTH);
        
        dialog.setVisible(true);
    }
    
    private void updateElectricityReading() {
        if (electricityAccounts.isEmpty()) {
            JOptionPane.showMessageDialog(this, 
                "No electricity accounts found.", 
                "No Accounts", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        
        // Create a dialog for selecting an account and updating its reading
        JDialog dialog = new JDialog(this, "Update Electricity Reading", true);
        dialog.setSize(400, 200);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new BorderLayout());
        
        JPanel formPanel = new JPanel(new GridLayout(2, 2, 10, 10));
        formPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        JLabel accountLabel = new JLabel("Select Account:");
        JComboBox<String> accountCombo = new JComboBox<>();
        
        for (Electricity account : electricityAccounts) {
            accountCombo.addItem(account.getName() + " (" + account.getAccountNumber() + ")");
        }
        
        JLabel readingLabel = new JLabel("New Reading (kWh):");
        JTextField readingField = new JTextField(20);
        
        formPanel.add(accountLabel);
        formPanel.add(accountCombo);
        formPanel.add(readingLabel);
        formPanel.add(readingField);
        
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton cancelButton = new JButton("Cancel");
        JButton saveButton = new JButton("Save");
        
        cancelButton.addActionListener(e -> dialog.dispose());
        saveButton.addActionListener(e -> {
            try {
                int index = accountCombo.getSelectedIndex();
                double newReading = Double.parseDouble(readingField.getText());
                
                Electricity selected = electricityAccounts.get(index);
                previousElectricityReadings.put(selected.getAccountNumber(), selected.getMeterReading());
                selected.setMeterReading(newReading);
                
                dialog.dispose();
                showElectricityPanel(); // Refresh the panel
                
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(dialog, 
                    "Please enter a valid number for the reading.", 
                    "Input Error", JOptionPane.ERROR_MESSAGE);
            }
        });
        
        buttonPanel.add(cancelButton);
        buttonPanel.add(saveButton);
        
        dialog.add(formPanel, BorderLayout.CENTER);
        dialog.add(buttonPanel, BorderLayout.SOUTH);
        
        dialog.setVisible(true);
    }
    
    private void calculateElectricityBill() {
        if (electricityAccounts.isEmpty()) {
            JOptionPane.showMessageDialog(this, 
                "No electricity accounts found.", 
                "No Accounts", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        
        // Create a dialog for selecting an account and showing the bill
        JDialog dialog = new JDialog(this, "Electricity Bill Calculation", true);
        dialog.setSize(500, 400);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new BorderLayout());
        
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        topPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        
        JLabel accountLabel = new JLabel("Select Account:");
        JComboBox<String> accountCombo = new JComboBox<>();
        
        for (Electricity account : electricityAccounts) {
            accountCombo.addItem(account.getName() + " (" + account.getAccountNumber() + ")");
        }
        
        JButton calculateButton = new JButton("Calculate");
        
        topPanel.add(accountLabel);
        topPanel.add(accountCombo);
        topPanel.add(calculateButton);
        
        JPanel resultPanel = new JPanel(new BorderLayout());
        resultPanel.setBorder(BorderFactory.createEmptyBorder(0, 15, 15, 15));
        
        JTextArea resultArea = new JTextArea();
        resultArea.setEditable(false);
        resultArea.setFont(new Font("Monospaced", Font.PLAIN, 14));
        JScrollPane scrollPane = new JScrollPane(resultArea);
        
        resultPanel.add(scrollPane, BorderLayout.CENTER);
        
        calculateButton.addActionListener(e -> {
            int index = accountCombo.getSelectedIndex();
            Electricity selected = electricityAccounts.get(index);
            double previousReading = previousElectricityReadings.get(selected.getAccountNumber());
            double bill = selected.calculateBill(previousReading);
            double consumption = selected.getMeterReading() - previousReading;
            
            StringBuilder sb = new StringBuilder();
            sb.append("Bill Calculation:\n\n");
            sb.append("Account: ").append(selected.getName()).append("\n");
            sb.append("Provider: ").append(selected.getProvider()).append("\n");
            sb.append("Account Number: ").append(selected.getAccountNumber()).append("\n\n");
            sb.append("Previous Reading: ").append(previousReading).append(" kWh\n");
            sb.append("Current Reading: ").append(selected.getMeterReading()).append(" kWh\n");
            sb.append("Consumption: ").append(consumption).append(" kWh\n\n");
            sb.append("Rate: $").append(selected.getRatePerKwh()).append(" per kWh\n");
            sb.append("Total Bill: $").append(String.format("%.2f", bill)).append("\n");
            
            resultArea.setText(sb.toString());
        });
        
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton closeButton = new JButton("Close");
        closeButton.addActionListener(e -> dialog.dispose());
        buttonPanel.add(closeButton);
        
        dialog.add(topPanel, BorderLayout.NORTH);
        dialog.add(resultPanel, BorderLayout.CENTER);
        dialog.add(buttonPanel, BorderLayout.SOUTH);
        
        dialog.setVisible(true);
    }
    
    private void showGasPanel() {
        // Similar to electricity panel
        JOptionPane.showMessageDialog(this, 
            "Gas Management panel would be implemented similarly to Electricity", 
            "Information", JOptionPane.INFORMATION_MESSAGE);
    }
    
    private void showWaterPanel() {
        // Similar to electricity panel
        JOptionPane.showMessageDialog(this, 
            "Water Management panel would be implemented similarly to Electricity", 
            "Information", JOptionPane.INFORMATION_MESSAGE);
    }
    
    private void showSubscriptionsPanel() {
        // Clear existing content
        contentPanel.removeAll();
        
        // Create subscriptions panel
        JPanel subscriptionsPanel = new JPanel(new BorderLayout());
        subscriptionsPanel.setBackground(new Color(240, 240, 240));
        
        // Add title
        JLabel titleLabel = new JLabel("Subscriptions Management");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
        titleLabel.setHorizontalAlignment(JLabel.CENTER);
        titleLabel.setBorder(BorderFactory.createEmptyBorder(15, 0, 15, 0));
        
        // Create buttons panel
        JPanel buttonsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        buttonsPanel.setBackground(new Color(240, 240, 240));
        buttonsPanel.setBorder(BorderFactory.createEmptyBorder(0, 10, 10, 10));
        
        JButton addButton = new JButton("Add Subscription");
        JButton upcomingButton = new JButton("View Upcoming Payments");
        
        addButton.addActionListener(e -> addSubscription());
        upcomingButton.addActionListener(e -> viewUpcomingPayments());
        
        buttonsPanel.add(addButton);
        buttonsPanel.add(upcomingButton);
        
        // Create table for data
        JPanel tablePanel = new JPanel(new BorderLayout());
        tablePanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        String[] columnNames = {"Name", "Provider", "Type", "Monthly Cost", "Next Billing Date"};
        Object[][] data = new Object[subscriptions.size()][5];
        
        for (int i = 0; i < subscriptions.size(); i++) {
            Subscription sub = subscriptions.get(i);
            data[i][0] = sub.getName();
            data[i][1] = sub.getProvider();
            data[i][2] = sub.getType();
            data[i][3] = String.format("$%.2f", sub.getMonthlyCost());
            data[i][4] = sub.getNextBillingDate();
        }
        
        JTable table = new JTable(data, columnNames);
        JScrollPane scrollPane = new JScrollPane(table);
        tablePanel.add(scrollPane, BorderLayout.CENTER);
        
        // Add components to subscriptions panel
        subscriptionsPanel.add(titleLabel, BorderLayout.NORTH);
        subscriptionsPanel.add(buttonsPanel, BorderLayout.SOUTH);
        subscriptionsPanel.add(tablePanel, BorderLayout.CENTER);
        
        // Add subscriptions panel to content panel
        contentPanel.add(subscriptionsPanel, BorderLayout.CENTER);
        
        // Refresh UI
        contentPanel.revalidate();
        contentPanel.repaint();
    }
    
    private void addSubscription() {
        // Create a dialog for adding a new subscription
        JDialog dialog = new JDialog(this, "Add Subscription", true);
        dialog.setSize(400, 300);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new BorderLayout());
        
        JPanel formPanel = new JPanel(new GridLayout(5, 2, 10, 10));
        formPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        JLabel nameLabel = new JLabel("Subscription Name:");
        JTextField nameField = new JTextField(20);
        
        JLabel providerLabel = new JLabel("Provider:");
        JTextField providerField = new JTextField(20);
        
        JLabel accountLabel = new JLabel("Account Number:");
        JTextField accountField = new JTextField(20);
        
        JLabel typeLabel = new JLabel("Type:");
        JComboBox<SubscriptionType> typeCombo = new JComboBox<>(SubscriptionType.values());
        
        JLabel costLabel = new JLabel("Monthly Cost ($):");
        JTextField costField = new JTextField(20);
        
        formPanel.add(nameLabel);
        formPanel.add(nameField);
        formPanel.add(providerLabel);
        formPanel.add(providerField);
        formPanel.add(accountLabel);
        formPanel.add(accountField);
        formPanel.add(typeLabel);
        formPanel.add(typeCombo);
        formPanel.add(costLabel);
        formPanel.add(costField);
        
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton cancelButton = new JButton("Cancel");
        JButton saveButton = new JButton("Save");
        
        cancelButton.addActionListener(e -> dialog.dispose());
        saveButton.addActionListener(e -> {
            try {
                String name = nameField.getText();
                String provider = providerField.getText();
                String accountNumber = accountField.getText();
                SubscriptionType type = (SubscriptionType) typeCombo.getSelectedItem();
                double cost = Double.parseDouble(costField.getText());
                
                Subscription subscription = new Subscription(name, provider, accountNumber, type, cost);
                subscriptions.add(subscription);
                
                dialog.dispose();
                showSubscriptionsPanel(); // Refresh the panel
                
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(dialog, 
                    "Please enter a valid number for the monthly cost.", 
                    "Input Error", JOptionPane.ERROR_MESSAGE);
            }
        });
        
        buttonPanel.add(cancelButton);
        buttonPanel.add(saveButton);
        
        dialog.add(formPanel, BorderLayout.CENTER);
        dialog.add(buttonPanel, BorderLayout.SOUTH);
        
        dialog.setVisible(true);
    }
    
    private void viewUpcomingPayments() {
        // Create a dialog to show upcoming payments
        JDialog dialog = new JDialog(this, "Upcoming Subscription Payments", true);
        dialog.setSize(500, 400);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new BorderLayout());
        
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        topPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        
        JLabel titleLabel = new JLabel("Upcoming Payments (Next 30 Days):");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 16));
        
        topPanel.add(titleLabel);
        
        JPanel resultPanel = new JPanel(new BorderLayout());
        resultPanel.setBorder(BorderFactory.createEmptyBorder(0, 15, 15, 15));
        
        JTextArea resultArea = new JTextArea();
        resultArea.setEditable(false);
        resultArea.setFont(new Font("Monospaced", Font.PLAIN, 14));
        JScrollPane scrollPane = new JScrollPane(resultArea);
        
        resultPanel.add(scrollPane, BorderLayout.CENTER);
        
        // Calculate upcoming payments
        StringBuilder sb = new StringBuilder();
        java.time.LocalDate today = java.time.LocalDate.now();
        java.time.LocalDate oneMonthFromNow = today.plusMonths(1);
        
        boolean found = false;
        for (Subscription sub : subscriptions) {
            if (sub.getNextBillingDate().isBefore(oneMonthFromNow)) {
                sb.append(sub.getName()).append(" ($").append(String.format("%.2f", sub.getMonthlyCost())).append(")\n");
                sb.append("  Due on: ").append(sub.getNextBillingDate()).append("\n");
                long daysUntilBilling = today.until(sub.getNextBillingDate()).getDays();
                sb.append("  Days until billing: ").append(daysUntilBilling).append("\n\n");
                found = true;
            }
        }
        
        if (!found) {
            sb.append("No upcoming payments in the next 30 days.");
        }
        
        resultArea.setText(sb.toString());
        
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton closeButton = new JButton("Close");
        closeButton.addActionListener(e -> dialog.dispose());
        buttonPanel.add(closeButton);
        
        dialog.add(topPanel, BorderLayout.NORTH);
        dialog.add(resultPanel, BorderLayout.CENTER);
        dialog.add(buttonPanel, BorderLayout.SOUTH);
        
        dialog.setVisible(true);
    }
    
    private void addSampleData() {
        // Check if we already have data in the database
        List<Electricity> electricityAccounts = dbManager.getAllElectricity();
        List<Gas> gasAccounts = dbManager.getAllGas();
        List<Water> waterAccounts = dbManager.getAllWater();
        List<Subscription> subscriptions = dbManager.getAllSubscriptions();
        
        // Only add sample data if no data exists
        if (electricityAccounts.isEmpty() && gasAccounts.isEmpty() && waterAccounts.isEmpty() && subscriptions.isEmpty()) {
            // Add sample electricity account
            Electricity electricity = new Electricity("Home Electricity", "Power Company", "EL-12345", 0.12);
            electricity.setMeterReading(1000.0);
            dbManager.saveElectricity(electricity);
            
            // Add sample gas account
            Gas gas = new Gas("Home Gas", "Gas Company", "GS-67890", 0.85);
            gas.setMeterReading(500.0);
            dbManager.saveGas(gas);
            
            // Add sample water account
            Water water = new Water("Home Water", "Water Company", "WT-54321", 2.5);
            water.setMeterReading(150.0);
            dbManager.saveWater(water);
            
            // Add sample subscriptions
            Subscription internet = new Subscription("Home Internet", "ISP Provider", "NET-123", SubscriptionType.INTERNET, 49.99);
            dbManager.saveSubscription(internet);
            
            Subscription streaming = new Subscription("Movie Streaming", "StreamFlix", "STR-456", SubscriptionType.STREAMING, 14.99);
            dbManager.saveSubscription(streaming);
            
            Subscription mobile = new Subscription("Mobile Phone", "TeleCom", "PHN-789", SubscriptionType.PHONE, 39.99);
            dbManager.saveSubscription(mobile);
            
            JOptionPane.showMessageDialog(this, 
                "Sample data has been added to the database.", 
                "Sample Data", JOptionPane.INFORMATION_MESSAGE);
        }
    }
}
