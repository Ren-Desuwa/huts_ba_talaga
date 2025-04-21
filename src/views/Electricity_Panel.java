package views;

import javax.swing.*;
import java.awt.*;
import java.sql.Connection;
import java.util.*;
import models.Electricity;
import database.Database_Manager;
import database.Electricity_Manager;

public class Electricity_Panel implements Utility_Panel {
    private JPanel electricityPanel;
    private Main_Frame parentFrame;
    private java.util.List<Electricity> electricityAccounts;
    private Map<String, Double> previousElectricityReadings;
    private Electricity_Manager electricityManager;
    
    public Electricity_Panel(Main_Frame parentFrame, Map<String, Double> previousReadings) {
        this.parentFrame = parentFrame;
        this.previousElectricityReadings = previousReadings;
        Connection connection = Database_Manager.getInstance().getConnection();
        this.electricityManager = new Electricity_Manager(connection);
        
        // Initialize the panel
        electricityPanel = new JPanel(new BorderLayout());
        electricityPanel.setBackground(new Color(240, 240, 240));
        
        refreshPanel();
    }
    
    @Override
    public JPanel getPanel() {
        return electricityPanel;
    }
    
    @Override
    public void refreshPanel() {
        // Clear the panel
        electricityPanel.removeAll();
        
        // Fetch current data
        electricityAccounts = electricityManager.getAllElectricity();
        
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
        
        electricityPanel.revalidate();
        electricityPanel.repaint();
    }
    
    private void addElectricityAccount() {
        // Create a dialog for adding a new electricity account
        JDialog dialog = new JDialog(parentFrame, "Add Electricity Account", true);
        dialog.setSize(400, 300);
        dialog.setLocationRelativeTo(parentFrame);
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
                
                // Save to database
                electricityAccounts = electricityManager.getAllElectricity();
                previousElectricityReadings.put(accountNumber, reading);
                
                dialog.dispose();
                refreshPanel(); // Refresh the panel
                
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
            JOptionPane.showMessageDialog(parentFrame, 
                "No electricity accounts found.", 
                "No Accounts", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        
        // Create a dialog for selecting an account and updating its reading
        JDialog dialog = new JDialog(parentFrame, "Update Electricity Reading", true);
        dialog.setSize(400, 200);
        dialog.setLocationRelativeTo(parentFrame);
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
                
                // Update in database
                electricityAccounts = electricityManager.getAllElectricity();
                
                dialog.dispose();
                refreshPanel(); // Refresh the panel
                
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
            JOptionPane.showMessageDialog(parentFrame, 
                "No electricity accounts found.", 
                "No Accounts", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        
        // Create a dialog for selecting an account and showing the bill
        JDialog dialog = new JDialog(parentFrame, "Electricity Bill Calculation", true);
        dialog.setSize(500, 400);
        dialog.setLocationRelativeTo(parentFrame);
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
            double previousReading = previousElectricityReadings.getOrDefault(selected.getAccountNumber(), 0.0);
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
    
    public java.util.List<Electricity> getElectricityAccounts() {
        return electricityAccounts;
    }
    
    public Map<String, Double> getPreviousReadings() {
        return previousElectricityReadings;
    }
}
