package views;

import database.Database_Manager;
import database.User_Manager;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class Forgot_Password_Panel extends JPanel {
    private final User_Manager userManager;
    private final Main_Frame mainFrame;

    // UI Components
    private JLabel jlbl_ForgotPassword;
    private JTextField jtf_Username;
    private JLabel jlbl_Username;
    private JButton jbtn_ResetPassword;
    private JPasswordField jpf_NewPassword;
    private JPasswordField jpf_ConfirmPassword;
    private JLabel jlbl_NewPassword;
    private JLabel jlbl_ConfirmPassword;
    private JLabel jlbl_BackToLogin;
    private JLabel jlbl_Account_Icon;
    private JLabel jlbl_Background;
    
    // For responsiveness
    private JPanel contentPanel;

    // Form dimensions
    private final int LABEL_WIDTH = 110;
    private final int FIELD_WIDTH = 180;
    private final int ROW_HEIGHT = 25;
    private final int VERTICAL_GAP = 50;

    /**
     * Creates new Forgot Password Panel
     */
    public Forgot_Password_Panel(Main_Frame mainFrame, User_Manager userManager) {
        this.userManager = userManager;
        this.mainFrame = mainFrame;
        initComponents();
    }

    private void initComponents() {
        // Set layout for main panel
        setBackground(new Color(35, 50, 90));
        setLayout(new BorderLayout());
        
        // Initialize content panel that will hold all components
        contentPanel = new JPanel();
        contentPanel.setOpaque(false);
        contentPanel.setLayout(null);
        
        jlbl_ForgotPassword = new JLabel();
        jtf_Username = new JTextField();
        jlbl_Username = new JLabel();
        jbtn_ResetPassword = new JButton();
        jpf_NewPassword = new JPasswordField();
        jpf_ConfirmPassword = new JPasswordField();
        jlbl_NewPassword = new JLabel();
        jlbl_ConfirmPassword = new JLabel();
        jlbl_BackToLogin = new JLabel();
        jlbl_Account_Icon = new JLabel();
        jlbl_Background = new JLabel();

        // Setup the form title
        jlbl_ForgotPassword.setFont(new Font("Segoe UI", 0, 24));
        jlbl_ForgotPassword.setForeground(new Color(23, 22, 22));
        jlbl_ForgotPassword.setHorizontalAlignment(SwingConstants.CENTER);
        jlbl_ForgotPassword.setText("Reset Password");
        contentPanel.add(jlbl_ForgotPassword);

        // Username field
        jtf_Username.setForeground(new Color(23, 22, 22));
        jtf_Username.setText("Enter Username");
        jtf_Username.addFocusListener(new FocusAdapter() {
            public void focusGained(FocusEvent evt) {
                if ("Enter Username".equals(jtf_Username.getText())) {
                    jtf_Username.setText("");
                }
            }
            public void focusLost(FocusEvent evt) {
                if ("".equals(jtf_Username.getText())) {
                    jtf_Username.setText("Enter Username");
                }
            }
        });
        contentPanel.add(jtf_Username);

        jlbl_Username.setForeground(new Color(23, 22, 22));
        jlbl_Username.setText("Username");
        contentPanel.add(jlbl_Username);

        // New Password field
        jpf_NewPassword.setForeground(new Color(23, 22, 22));
        jpf_NewPassword.setText("Enter New Password");
        jpf_NewPassword.setEchoChar((char) 0);
        jpf_NewPassword.addFocusListener(new FocusAdapter() {
            public void focusGained(FocusEvent evt) {
                if ("Enter New Password".equals(new String(jpf_NewPassword.getPassword()))) {
                    jpf_NewPassword.setEchoChar('\u2022');
                    jpf_NewPassword.setText("");
                }
            }
            public void focusLost(FocusEvent evt) {
                if (new String(jpf_NewPassword.getPassword()).isEmpty()) {
                    jpf_NewPassword.setEchoChar((char) 0);
                    jpf_NewPassword.setText("Enter New Password");
                }
            }
        });
        contentPanel.add(jpf_NewPassword);

        jlbl_NewPassword.setForeground(new Color(23, 22, 22));
        jlbl_NewPassword.setText("New Password");
        contentPanel.add(jlbl_NewPassword);

        // Confirm Password field
        jpf_ConfirmPassword.setForeground(new Color(23, 22, 22));
        jpf_ConfirmPassword.setText("Confirm New Password");
        jpf_ConfirmPassword.setEchoChar((char) 0);
        jpf_ConfirmPassword.addFocusListener(new FocusAdapter() {
            public void focusGained(FocusEvent evt) {
                if ("Confirm New Password".equals(new String(jpf_ConfirmPassword.getPassword()))) {
                    jpf_ConfirmPassword.setEchoChar('\u2022');
                    jpf_ConfirmPassword.setText("");
                }
            }
            public void focusLost(FocusEvent evt) {
                if (new String(jpf_ConfirmPassword.getPassword()).isEmpty()) {
                    jpf_ConfirmPassword.setEchoChar((char) 0);
                    jpf_ConfirmPassword.setText("Confirm New Password");
                }
            }
        });
        contentPanel.add(jpf_ConfirmPassword);

        jlbl_ConfirmPassword.setForeground(new Color(23, 22, 22));
        jlbl_ConfirmPassword.setText("Confirm Password");
        contentPanel.add(jlbl_ConfirmPassword);

        // Reset Password button
        jbtn_ResetPassword.setBackground(new Color(226, 149, 90));
        jbtn_ResetPassword.setFont(new Font("Segoe UI", 0, 18));
        jbtn_ResetPassword.setForeground(new Color(255, 255, 255));
        jbtn_ResetPassword.setText("Reset Password");
        jbtn_ResetPassword.setCursor(new Cursor(Cursor.HAND_CURSOR));
        jbtn_ResetPassword.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                jbtn_ResetPasswordActionPerformed(evt);
            }
        });
        contentPanel.add(jbtn_ResetPassword);

        // Back to Login link
        jlbl_BackToLogin.setForeground(new Color(23, 22, 22));
        jlbl_BackToLogin.setText("Back to Login");
        jlbl_BackToLogin.setCursor(new Cursor(Cursor.HAND_CURSOR));
        jlbl_BackToLogin.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent evt) {
                jlbl_BackToLoginMouseClicked(evt);
            }
            public void mouseEntered(MouseEvent evt) {
                jlbl_BackToLogin.setText("<html><u>Back to Login</u></html>");
            }
            public void mouseExited(MouseEvent evt) {
                jlbl_BackToLogin.setText("Back to Login");
            }
        });
        contentPanel.add(jlbl_BackToLogin);

        jlbl_Account_Icon.setIcon(new ImageIcon(getClass().getResource("/assets/icon/AccountBlack.png")));
        contentPanel.add(jlbl_Account_Icon);

        jlbl_Background.setIcon(new ImageIcon(getClass().getResource("/assets/image/background(900x410).png")));
        contentPanel.add(jlbl_Background);
        
        // Add content panel to main panel
        add(contentPanel, BorderLayout.CENTER);
        
        // Initialize component positions
        resizeComponents();
        
        // Add component listener to handle resizing
        addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                resizeComponents();
            }
        });
    }
    
    private void resizeComponents() {
        int width = getWidth();
        int height = getHeight();
        
        // Calculate center positions
        int centerX = width / 2;
        int totalFormWidth = LABEL_WIDTH + FIELD_WIDTH + 10; // 10px gap between label and field
        
        // Starting Y position
        int currentY = 60;
        
        // Position title and icon
        jlbl_ForgotPassword.setBounds(centerX - 100, currentY, 200, 32);
        jlbl_Account_Icon.setBounds(centerX - 130, currentY, 32, 32);
        
        currentY += 60; // Move to first row
        
        // Position username row
        int labelX = centerX - (totalFormWidth / 2);
        int fieldX = labelX + LABEL_WIDTH + 10;
        
        jlbl_Username.setBounds(labelX, currentY, LABEL_WIDTH, ROW_HEIGHT);
        jtf_Username.setBounds(fieldX, currentY, FIELD_WIDTH, ROW_HEIGHT);
        
        currentY += VERTICAL_GAP; // Move to next row
        
        // Position new password row
        jlbl_NewPassword.setBounds(labelX, currentY, LABEL_WIDTH, ROW_HEIGHT);
        jpf_NewPassword.setBounds(fieldX, currentY, FIELD_WIDTH, ROW_HEIGHT);
        
        currentY += VERTICAL_GAP; // Move to next row
        
        // Position confirm password row
        jlbl_ConfirmPassword.setBounds(labelX, currentY, LABEL_WIDTH, ROW_HEIGHT);
        jpf_ConfirmPassword.setBounds(fieldX, currentY, FIELD_WIDTH, ROW_HEIGHT);
        
        currentY += VERTICAL_GAP; // Move to button row
        
        // Position reset button (centered)
        int buttonWidth = 150;
        jbtn_ResetPassword.setBounds(centerX - (buttonWidth / 2), currentY, buttonWidth, 32);
        
        currentY += 40; // Move to back to login link
        
        // Position back to login link (centered)
        int linkWidth = 100;
        jlbl_BackToLogin.setBounds(centerX - (linkWidth / 2), currentY, linkWidth, 16);
        
        // Background - stretch to fit panel size
        jlbl_Background.setBounds(0, 0, width, height);
    }

    private void jbtn_ResetPasswordActionPerformed(ActionEvent evt) {
        String username = jtf_Username.getText().trim();
        String newPassword = new String(jpf_NewPassword.getPassword()).trim();
        String confirmPassword = new String(jpf_ConfirmPassword.getPassword()).trim();
        
        // Validate input
        if (username.equals("Enter Username") || username.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter your username", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        if (newPassword.equals("Enter New Password") || newPassword.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter a new password", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        if (confirmPassword.equals("Confirm New Password") || confirmPassword.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please confirm your new password", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        if (!newPassword.equals(confirmPassword)) {
            JOptionPane.showMessageDialog(this, "Passwords do not match", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        try {
            // Check if username exists
            if (!userManager.userExists(username)) {
                JOptionPane.showMessageDialog(this, "Username does not exist", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            // Update password
            boolean success = userManager.updateUserPassword(username, newPassword);
            
            if (success) {
                JOptionPane.showMessageDialog(this, "Password reset successful! Please log in with your new password.", 
                        "Success", JOptionPane.INFORMATION_MESSAGE);
                mainFrame.showLoginPanel();
            } else {
                JOptionPane.showMessageDialog(this, "Failed to reset password", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "An error occurred while resetting password", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void jlbl_BackToLoginMouseClicked(MouseEvent evt) {
        mainFrame.showLoginPanel();
    }
    
    public JPanel getPanel() {
        return this;
    }
    
    public void refreshPanel() {
        // Reset fields
        jtf_Username.setText("Enter Username");
        jpf_NewPassword.setText("Enter New Password");
        jpf_NewPassword.setEchoChar((char) 0);
        jpf_ConfirmPassword.setText("Confirm New Password");
        jpf_ConfirmPassword.setEchoChar((char) 0);
        
        // Adjust component positions for current size
        resizeComponents();
    }
}