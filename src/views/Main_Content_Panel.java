package views;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

/**
 * Main content panel that serves as the container for the application's
 * main interface after user authentication.
 */
public class Main_Content_Panel {
    private JPanel panel;
    private CardLayout cardLayout;
    private JPanel contentArea;
    private Main_Frame parentFrame;
    
    // Constants for card layout
    private static final String WELCOME_CARD = "WELCOME_CARD";
    private static final String ELECTRICITY_CARD = "ELECTRICITY_CARD";
    private static final String GAS_CARD = "GAS_CARD"; 
    private static final String WATER_CARD = "WATER_CARD";
    private static final String SUBSCRIPTION_CARD = "SUBSCRIPTION_CARD";
    private static final String SUMMARY_CARD = "SUMMARY_CARD";
    
    /**
     * Constructor for the Main_Content_Panel
     * @param parentFrame The parent Main_Frame that contains this panel
     */
    public Main_Content_Panel(Main_Frame parentFrame) {
        this.parentFrame = parentFrame;
        initializePanel();
    }
    
    /**
     * Initialize the panel and its components
     */
    private void initializePanel() {
        panel = new JPanel(new BorderLayout());
        
        // Create header panel
        JPanel headerPanel = createHeaderPanel();
        panel.add(headerPanel, BorderLayout.NORTH);
        
        // Create content area with card layout
        cardLayout = new CardLayout();
        contentArea = new JPanel(cardLayout);
        panel.add(contentArea, BorderLayout.CENTER);
        
        // Initialize all cards/panels
        initializeContentCards();
    }
    
    /**
     * Creates the header panel with title and user info
     * @return The header panel
     */
    private JPanel createHeaderPanel() {
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(new Color(240, 240, 240));
        headerPanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        
        // Add title label
        JLabel titleLabel = new JLabel("House Utility Management System");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
        headerPanel.add(titleLabel, BorderLayout.WEST);
        
        // Add user information and logout on the right
        JPanel userPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        userPanel.setOpaque(false);
        
        JLabel userLabel = new JLabel("Welcome, User");
        userPanel.add(userLabel);
        
        JButton logoutButton = new JButton("Logout");
        logoutButton.addActionListener(e -> handleLogout());
        userPanel.add(logoutButton);
        
        headerPanel.add(userPanel, BorderLayout.EAST);
        
        return headerPanel;
    }
    
    /**
     * Initialize all the content cards for the main area
     */
    private void initializeContentCards() {
        // These would normally be initialized from the parent frame
        // but for now we'll create placeholders
        JPanel welcomeCard = createPlaceholderPanel("Welcome Panel");
        JPanel electricityCard = createPlaceholderPanel("Electricity Panel");
        JPanel gasCard = createPlaceholderPanel("Gas Panel");
        JPanel waterCard = createPlaceholderPanel("Water Panel");
        JPanel subscriptionCard = createPlaceholderPanel("Subscription Panel");
        JPanel summaryCard = createPlaceholderPanel("Summary Panel");
        
        // Add all cards to content area
        contentArea.add(welcomeCard, WELCOME_CARD);
        contentArea.add(electricityCard, ELECTRICITY_CARD);
        contentArea.add(gasCard, GAS_CARD);
        contentArea.add(waterCard, WATER_CARD);
        contentArea.add(subscriptionCard, SUBSCRIPTION_CARD);
        contentArea.add(summaryCard, SUMMARY_CARD);
        
        // Show welcome card by default
        cardLayout.show(contentArea, WELCOME_CARD);
    }
    
    /**
     * Creates a placeholder panel for demonstration
     * @param panelName The name of the panel
     * @return A simple panel with the name displayed
     */
    private JPanel createPlaceholderPanel(String panelName) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        JLabel label = new JLabel(panelName);
        label.setFont(new Font("Arial", Font.BOLD, 18));
        label.setHorizontalAlignment(SwingConstants.CENTER);
        
        panel.add(label, BorderLayout.CENTER);
        return panel;
    }
    
    /**
     * Handle logout action
     */
    private void handleLogout() {
        int choice = JOptionPane.showConfirmDialog(
            panel,
            "Are you sure you want to logout?",
            "Confirm Logout",
            JOptionPane.YES_NO_OPTION
        );
        
        if (choice == JOptionPane.YES_OPTION) {
            // Logic to handle logout would go here
            // This would typically involve switching back to the login panel
            JOptionPane.showMessageDialog(panel, "Logged out successfully!");
        }
    }
    
    /**
     * Set the current user display name
     * @param username The username to display
     */
    public void setCurrentUser(String username) {
        JPanel headerPanel = (JPanel) panel.getComponent(0);
        JPanel userPanel = (JPanel) headerPanel.getComponent(1);
        JLabel userLabel = (JLabel) userPanel.getComponent(0);
        userLabel.setText("Welcome, " + username);
    }
    
    /**
     * Shows the welcome card
     */
    public void showWelcomeCard() {
        cardLayout.show(contentArea, WELCOME_CARD);
    }
    
    /**
     * Shows the electricity card
     */
    public void showElectricityCard() {
        cardLayout.show(contentArea, ELECTRICITY_CARD);
    }
    
    /**
     * Shows the gas card
     */
    public void showGasCard() {
        cardLayout.show(contentArea, GAS_CARD);
    }
    
    /**
     * Shows the water card
     */
    public void showWaterCard() {
        cardLayout.show(contentArea, WATER_CARD);
    }
    
    /**
     * Shows the subscription card
     */
    public void showSubscriptionCard() {
        cardLayout.show(contentArea, SUBSCRIPTION_CARD);
    }
    
    /**
     * Shows the summary card
     */
    public void showSummaryCard() {
        cardLayout.show(contentArea, SUMMARY_CARD);
    }
    
    /**
     * Gets the main panel
     * @return The main JPanel
     */
    public JPanel getPanel() {
        return panel;
    }
    
    /**
     * Updates the panel content when data changes
     */
    public void refreshPanel() {
        // Refresh all sub-panels or data as needed
    }
    
    /**
     * Set specific panels for each card
     * @param welcomePanel The welcome panel
     */
    public void setWelcomePanel(JPanel welcomePanel) {
        contentArea.remove(0); // Remove the placeholder
        contentArea.add(welcomePanel, WELCOME_CARD);
    }
    
    /**
     * Set the electricity panel
     * @param electricityPanel The electricity panel
     */
    public void setElectricityPanel(JPanel electricityPanel) {
        contentArea.remove(1); // Remove the placeholder
        contentArea.add(electricityPanel, ELECTRICITY_CARD);
    }
    
    /**
     * Set the gas panel
     * @param gasPanel The gas panel
     */
    public void setGasPanel(JPanel gasPanel) {
        contentArea.remove(2); // Remove the placeholder
        contentArea.add(gasPanel, GAS_CARD);
    }
    
    /**
     * Set the water panel
     * @param waterPanel The water panel
     */
    public void setWaterPanel(JPanel waterPanel) {
        contentArea.remove(3); // Remove the placeholder
        contentArea.add(waterPanel, WATER_CARD);
    }
    
    /**
     * Set the subscription panel
     * @param subscriptionPanel The subscription panel
     */
    public void setSubscriptionPanel(JPanel subscriptionPanel) {
        contentArea.remove(4); // Remove the placeholder
        contentArea.add(subscriptionPanel, SUBSCRIPTION_CARD);
    }
    
    /**
     * Set the summary panel
     * @param summaryPanel The summary panel
     */
    public void setSummaryPanel(JPanel summaryPanel) {
        contentArea.remove(5); // Remove the placeholder
        contentArea.add(summaryPanel, SUMMARY_CARD);
    }
}