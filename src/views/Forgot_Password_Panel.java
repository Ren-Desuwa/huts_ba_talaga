package views;

import database.Database_Manager;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class Forgot_Password_Panel extends JPanel {
    private final Database_Manager dbManager;
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

    /**
     * Creates new Forgot Password Panel
     */
    public Forgot_Password_Panel(Main_Frame mainFrame, Database_Manager dbManager) {
        this.dbManager = dbManager;
        this.mainFrame = mainFrame;
        initComponents();
    }

    private void initComponents() {
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

        setBackground(new Color(35, 50, 90));
        setMinimumSize(new Dimension(900, 410));
        setPreferredSize(new Dimension(900, 410));
        setLayout(null);

        jlbl_ForgotPassword.setFont(new Font("Segoe UI", 0, 24));
        jlbl_ForgotPassword.setForeground(new Color(23, 22, 22));
        jlbl_ForgotPassword.setHorizontalAlignment(SwingConstants.CENTER);
        jlbl_ForgotPassword.setText("Reset Password");
        jlbl_ForgotPassword.setBounds(400, 60, 200, 32);
        add(jlbl_ForgotPassword);

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
        jtf_Username.setBounds(410, 120, 180, 25);
        add(jtf_Username);

        jlbl_Username.setForeground(new Color(23, 22, 22));
        jlbl_Username.setText("Username");
        jlbl_Username.setBounds(320, 120, 80, 25);
        add(jlbl_Username);

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
        jpf_NewPassword.setBounds(410, 170, 180, 25);
        add(jpf_NewPassword);

        jlbl_NewPassword.setForeground(new Color(23, 22, 22));
        jlbl_NewPassword.setText("New Password");
        jlbl_NewPassword.setBounds(320, 170, 100, 25);
        add(jlbl_NewPassword);

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
        jpf_ConfirmPassword.setBounds(410, 220, 180, 25);
        add(jpf_ConfirmPassword);

        jlbl_ConfirmPassword.setForeground(new Color(23, 22, 22));
        jlbl_ConfirmPassword.setText("Confirm Password");
        jlbl_ConfirmPassword.setBounds(300, 220, 110, 25);
        add(jlbl_ConfirmPassword);

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
        jbtn_ResetPassword.setBounds(400, 270, 150, 32);
        add(jbtn_ResetPassword);

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
        jlbl_BackToLogin.setBounds(430, 320, 100, 16);
        add(jlbl_BackToLogin);

        jlbl_Account_Icon.setIcon(new ImageIcon(getClass().getResource("/assets/icon/AccountBlack.png")));
        jlbl_Account_Icon.setBounds(370, 60, 32, 32);
        add(jlbl_Account_Icon);

        jlbl_Background.setIcon(new ImageIcon(getClass().getResource("/assets/image/background(900x410).png")));
        jlbl_Background.setBounds(0, 0, 910, 410);
        add(jlbl_Background);
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
            if (!dbManager.userExists(username)) {
                JOptionPane.showMessageDialog(this, "Username does not exist", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            // Update password
            boolean success = dbManager.updatePassword(username, newPassword);
            
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
    }
}
