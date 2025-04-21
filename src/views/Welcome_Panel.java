package views;

import javax.swing.*;
import java.awt.*;
import java.util.List;
import models.*;
import database.*;

public class Welcome_Panel implements Utility_Panel {
    private JPanel welcomePanel;
    private Main_Frame parentFrame;
    private Database_Manager dbManager;
    private Water_Manager waterManager;
    private Gas_Manager gasManager;
    private Electricity_Manager electricityManager;
    private Subscription_Manager subscriptionManager;
    
    
    
    public Welcome_Panel(Main_Frame parentFrame, Database_Manager dbManager) {
        this.parentFrame = parentFrame;
        this.dbManager = dbManager;
        
        // Initialize the panel
        welcomePanel = new JPanel(new BorderLayout());
        welcomePanel.setBackground(new Color(240, 240, 240));
        
        refreshPanel();
    }
    
    @Override
    public JPanel getPanel() {
        return welcomePanel;
    }
    
    @Override
    public void refreshPanel() {
        // Clear existing content
        welcomePanel.removeAll();
        
        // Get data from database
        List<Electricity> electricityAccounts = electricityManager.getAllElectricity();
        List<Gas> gasAccounts = gasManager.getAllGas();
        List<Water> waterAccounts = waterManager.getAllWater();
        List<Subscription> subscriptions = subscriptionManager.getAllSubscriptions();
        
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
        
        welcomePanel.revalidate();
        welcomePanel.repaint();
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
}