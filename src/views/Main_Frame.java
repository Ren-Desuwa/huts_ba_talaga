package views;

import models.*;
import javax.swing.*;
import database.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;

public class Main_Frame extends JFrame {
    // Maps to store previous readings for bill calculations
    private Map<String, Double> previousElectricityReadings = new HashMap<>();
    private Map<String, Double> previousGasReadings = new HashMap<>();
    private Map<String, Double> previousWaterReadings = new HashMap<>();
    
    // UI components
    private JPanel mainPanel;
    private JPanel menuPanel;
    private JPanel contentPanel;
    private Login_Panel loginPanel;
    private Sign_Up_Panel signUpPanel;
    private Forgot_Password_Panel forgotPasswordPanel;
    private Main_Content_Panel mainContentPanel;
    private final CardLayout cardLayout;
    
    // Panel managers
    private Welcome_Panel welcomePanel;
    private Electricity_Panel electricityPanel;
    private Gas_Panel gasPanel;
    private Water_Panel waterPanel;
    private Subscription_Panel subscriptionPanel;
    private Summary_Panel summaryPanel;
    
    // Database manager instance
    private Database_Manager dbManager;
    private Subscription_Manager subscriptionManager;
    private Electricity_Manager electricityManager;
    private Gas_Manager gasManager;
    private Water_Manager waterManager;
    
    
    private static final String LOGIN_PANEL = "LOGIN_PANEL";
    private static final String SIGNUP_PANEL = "SIGNUP_PANEL";
    private static final String FORGOT_PASSWORD_PANEL = "FORGOT_PASSWORD_PANEL";
    private static final String MAIN_CONTENT_PANEL = "MAIN_CONTENT_PANEL";
    
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
        
        // Initialize panel managers
        welcomePanel = new Welcome_Panel(this, dbManager);
        electricityPanel = new Electricity_Panel(this, previousElectricityReadings);
        gasPanel = new Gas_Panel(this, previousGasReadings);
        waterPanel = new Water_Panel(this, previousWaterReadings);
        subscriptionPanel = new Subscription_Panel(this);
        summaryPanel = new Summary_Panel(this, previousElectricityReadings, previousGasReadings, previousWaterReadings);
        
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
    
    // Panel display methods
    private void showWelcomePanel() {
        contentPanel.removeAll();
        welcomePanel.refreshPanel();
        contentPanel.add(welcomePanel.getPanel(), BorderLayout.CENTER);
        contentPanel.revalidate();
        contentPanel.repaint();
    }
    
    private void showElectricityPanel() {
        contentPanel.removeAll();
        electricityPanel.refreshPanel();
        contentPanel.add(electricityPanel.getPanel(), BorderLayout.CENTER);
        contentPanel.revalidate();
        contentPanel.repaint();
    }
    
    private void showGasPanel() {
        contentPanel.removeAll();
        gasPanel.refreshPanel();
        contentPanel.add(gasPanel.getPanel(), BorderLayout.CENTER);
        contentPanel.revalidate();
        contentPanel.repaint();
    }
    
    private void showWaterPanel() {
        contentPanel.removeAll();
        waterPanel.refreshPanel();
        contentPanel.add(waterPanel.getPanel(), BorderLayout.CENTER);
        contentPanel.revalidate();
        contentPanel.repaint();
    }
    
    private void showSubscriptionsPanel() {
        contentPanel.removeAll();
        subscriptionPanel.refreshPanel();
        contentPanel.add(subscriptionPanel.getPanel(), BorderLayout.CENTER);
        contentPanel.revalidate();
        contentPanel.repaint();
    }
    
    private void showSummaryPanel() {
        contentPanel.removeAll();
        summaryPanel.refreshPanel();
        contentPanel.add(summaryPanel.getPanel(), BorderLayout.CENTER);
        contentPanel.revalidate();
        contentPanel.repaint();
    }
    
    private void addSampleData() {
        // Check if we already have data in the database
        java.util.List<Electricity> electricityAccounts = electricityManager.getAllElectricity();
        java.util.List<Gas> gasAccounts = gasManager.getAllGas();
        java.util.List<Water> waterAccounts = waterManager.getAllWater();
        java.util.List<Subscription> subscriptions = subscriptionManager.getAllSubscriptions();
        
        // Only add sample data if no data exists
        if (electricityAccounts.isEmpty() && gasAccounts.isEmpty() && waterAccounts.isEmpty() && subscriptions.isEmpty()) {
            // Add sample electricity account
            Electricity electricity = new Electricity("Home Electricity", "Power Company", "EL-12345", 0.12);
            electricity.setMeterReading(1000.0);
            electricityManager.saveElectricity(electricity);
            previousElectricityReadings.put(electricity.getAccountNumber(), electricity.getMeterReading());
            
            // Add sample gas account
            Gas gas = new Gas("Home Gas", "Gas Company", "GS-67890", 0.85);
            gas.setMeterReading(500.0);
            gasManager.saveGas(gas);
            previousGasReadings.put(gas.getAccountNumber(), gas.getMeterReading());
            
            // Add sample water account
            Water water = new Water("Home Water", "Water Company", "WT-54321", 2.5);
            water.setMeterReading(150.0);
            waterManager.saveWater(water);
            previousWaterReadings.put(water.getAccountNumber(), water.getMeterReading());
            
            // Add sample subscriptions
            Subscription internet = new Subscription("Home Internet", "ISP Provider", "NET-123", SubscriptionType.INTERNET, 49.99);
            subscriptionManager.saveSubscription(internet);
            
            Subscription streaming = new Subscription("Movie Streaming", "StreamFlix", "STR-456", SubscriptionType.STREAMING, 14.99);
            subscriptionManager.saveSubscription(streaming);
            
            Subscription mobile = new Subscription("Mobile Phone", "TeleCom", "PHN-789", SubscriptionType.PHONE, 39.99);
            subscriptionManager.saveSubscription(mobile);
            
            JOptionPane.showMessageDialog(this, 
                "Sample data has been added to the database.", 
                "Sample Data", JOptionPane.INFORMATION_MESSAGE);
        }
    }
    
    // Accessor methods that might be needed by panels
    public Database_Manager getDbManager() {
        return dbManager;
    }
}