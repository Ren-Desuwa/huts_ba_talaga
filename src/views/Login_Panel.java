package views;

import models.*;
import javax.swing.*;

import database.User_Manager;

import java.awt.*;
import java.awt.event.*;
import java.util.*;

public class Login_Panel extends JPanel {
    private final User_Manager userManager;
    private final Main_Frame mainFrame;

    // UI Components
    private JLabel jlbl_Login;
    private JButton jbtn_Login;
    private JTextField jtf_Username;
    private JPasswordField jpf_Password;
    private JLabel jlbl_Password;
    private JLabel jlbl_Username;
    private JLabel jlbl_Forgot_Password;
    private JLabel jlbl_Sign_Up;
    private JLabel jlbl_Account_Icon;
    private JLabel jlbl_Background;

    /**
     * Creates new Login Panel
     */
    public Login_Panel(Main_Frame mainFrame, User_Manager userManager) {
        this.userManager = userManager;
        this.mainFrame = mainFrame;
        initComponents();
    }

    private void initComponents() {
        jlbl_Login = new JLabel();
        jbtn_Login = new JButton();
        jtf_Username = new JTextField();
        jpf_Password = new JPasswordField();
        jlbl_Password = new JLabel();
        jlbl_Username = new JLabel();
        jlbl_Forgot_Password = new JLabel();
        jlbl_Sign_Up = new JLabel();
        jlbl_Account_Icon = new JLabel();
        jlbl_Background = new JLabel();

        setBackground(new Color(35, 50, 90));
        setMinimumSize(new Dimension(900, 410));
        setPreferredSize(new Dimension(900, 410));
        addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent evt) {
                formMouseClicked(evt);
            }
        });
        
        // Using null layout for absolute positioning (similar to AbsoluteLayout)
        setLayout(null);

        // Set up UI components with setBounds instead of AbsoluteConstraints
        jlbl_Login.setBackground(new Color(255, 255, 255));
        jlbl_Login.setFont(new Font("Segoe UI", 0, 24));
        jlbl_Login.setForeground(new Color(23, 22, 22));
        jlbl_Login.setHorizontalAlignment(SwingConstants.CENTER);
        jlbl_Login.setText("Log In");
        jlbl_Login.setToolTipText("");
        jlbl_Login.setBounds(450, 60, 70, 32);
        add(jlbl_Login);

        jbtn_Login.setBackground(new Color(226, 149, 90));
        jbtn_Login.setFont(new Font("Segoe UI", 0, 18));
        jbtn_Login.setForeground(new Color(255, 255, 255));
        jbtn_Login.setText("Log In");
        jbtn_Login.setCursor(new Cursor(Cursor.HAND_CURSOR));
        jbtn_Login.setMaximumSize(new Dimension(93, 32));
        jbtn_Login.setMinimumSize(new Dimension(93, 32));
        jbtn_Login.setPreferredSize(new Dimension(93, 32));
        jbtn_Login.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                jbtn_LoginActionPerformed(evt);
            }
        });
        jbtn_Login.setBounds(420, 300, 93, 32);
        add(jbtn_Login);

        jtf_Username.setBackground(new Color(255, 255, 255));
        jtf_Username.setForeground(new Color(23, 22, 22));
        jtf_Username.setText("Enter Username");
        jtf_Username.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                jtf_UsernameFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                jtf_UsernameFocusLost(evt);
            }
        });
        jtf_Username.setBounds(410, 150, 120, 25);
        add(jtf_Username);

        jpf_Password.setForeground(new Color(23, 22, 22));
        jpf_Password.setText("Enter Password");
        jpf_Password.setBackground(new Color(255, 255, 255));
        jpf_Password.setEchoChar((char) 0);
        jpf_Password.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                jpf_PasswordFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                jpf_PasswordFocusLost(evt);
            }
        });
        jpf_Password.setBounds(410, 220, 120, 25);
        add(jpf_Password);

        jlbl_Password.setForeground(new Color(23, 22, 22));
        jlbl_Password.setText("Password");
        jlbl_Password.setBounds(390, 200, 70, 16);
        add(jlbl_Password);

        jlbl_Username.setForeground(new Color(23, 22, 22));
        jlbl_Username.setText("Username");
        jlbl_Username.setBounds(390, 130, 70, 16);
        add(jlbl_Username);

        jlbl_Forgot_Password.setForeground(new Color(23, 22, 22));
        jlbl_Forgot_Password.setText("Forgot Password?");
        jlbl_Forgot_Password.setToolTipText("");
        jlbl_Forgot_Password.setCursor(new Cursor(Cursor.HAND_CURSOR));
        jlbl_Forgot_Password.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent evt) {
                jlbl_Forgot_PasswordMouseClicked(evt);
            }
            public void mouseEntered(MouseEvent evt) {
                jlbl_Forgot_Password_Mouse_Hover(evt);
            }
            public void mouseExited(MouseEvent evt) {
                jlbl_Forgot_Password_Mouse_Exited(evt);
            }
        });
        jlbl_Forgot_Password.setBounds(420, 260, 120, 16);
        add(jlbl_Forgot_Password);

        jlbl_Sign_Up.setForeground(new Color(23, 22, 22));
        jlbl_Sign_Up.setText("Sign Up");
        jlbl_Sign_Up.setToolTipText("");
        jlbl_Sign_Up.setCursor(new Cursor(Cursor.HAND_CURSOR));
        jlbl_Sign_Up.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent evt) {
                jlbl_Sign_UpMouseClicked(evt);
            }
            public void mouseEntered(MouseEvent evt) {
                jlbl_Sign_UpMouseEntered(evt);
            }
            public void mouseExited(MouseEvent evt) {
                jlbl_Sign_UpMouseExited(evt);
            }
        });
        jlbl_Sign_Up.setBounds(450, 340, 50, 16);
        add(jlbl_Sign_Up);

        jlbl_Account_Icon.setBackground(new Color(23, 22, 22));
        jlbl_Account_Icon.setIcon(new ImageIcon(getClass().getResource("/assets/icon/AccountBlack.png")));
        jlbl_Account_Icon.setBounds(410, 60, 32, 32);
        add(jlbl_Account_Icon);

        jlbl_Background.setIcon(new ImageIcon(getClass().getResource("/assets/image/background(900x410).png")));
        jlbl_Background.setBounds(0, 0, 910, 410);
        add(jlbl_Background);
    }

    private void jbtn_LoginActionPerformed(ActionEvent evt) {
        String username = jtf_Username.getText().trim();
        String password = new String(jpf_Password.getPassword()).trim();

        try {
            boolean isAuthenticated = userManager.authenticateUser(username, password);

            if (isAuthenticated) {
                JOptionPane.showMessageDialog(this, "Login successful!");
                // Show the main application window
                mainFrame.();
            } else {
                // Invalid credentials
                JOptionPane.showMessageDialog(this, "Invalid username or password.", "Login Failed", JOptionPane.ERROR_MESSAGE);
            }
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "An error occurred while trying to log in.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void jtf_UsernameFocusGained(java.awt.event.FocusEvent evt) {
        if ("Enter Username".equals(jtf_Username.getText())) {
            jtf_Username.setText("");
        }
    }

    private void jtf_UsernameFocusLost(java.awt.event.FocusEvent evt) {
        if ("".equals(jtf_Username.getText())) {
            jtf_Username.setText("Enter Username");
        }
    }

    private void jpf_PasswordFocusGained(java.awt.event.FocusEvent evt) {
        if ("Enter Password".equals(new String(jpf_Password.getPassword()))) {
            jpf_Password.setEchoChar('\u2022');
            jpf_Password.setText("");
        }
    }

    private void jpf_PasswordFocusLost(java.awt.event.FocusEvent evt) {
        if (new String(jpf_Password.getPassword()).isEmpty()) {
            jpf_Password.setEchoChar((char) 0);
            jpf_Password.setText("Enter Password");
        }
    }

    private void jlbl_Forgot_Password_Mouse_Hover(java.awt.event.MouseEvent evt) {
        jlbl_Forgot_Password.setText("<html><u>Forgot Password?</u></html>");
    }

    private void jlbl_Forgot_Password_Mouse_Exited(java.awt.event.MouseEvent evt) {
        jlbl_Forgot_Password.setText("Forgot Password?");
    }

    private void jlbl_Sign_UpMouseEntered(java.awt.event.MouseEvent evt) {
        jlbl_Sign_Up.setText("<html><u>Sign Up</u></html>");
    }

    private void jlbl_Sign_UpMouseExited(java.awt.event.MouseEvent evt) {
        jlbl_Sign_Up.setText("Sign Up");
    }

    private void jlbl_Sign_UpMouseClicked(java.awt.event.MouseEvent evt) {
        // Show sign up panel
        mainFrame.showSignUpPanel();
    }

    private void jlbl_Forgot_PasswordMouseClicked(java.awt.event.MouseEvent evt) {
        // Show forgot password panel
        mainFrame.showForgotPasswordPanel();
    }

    private void formMouseClicked(java.awt.event.MouseEvent evt) {
        // Optional: Add code for when form is clicked
    }
    
    public JPanel getPanel() {
        return this;
    }
    
    public void refreshPanel() {
        // Reset fields if needed
        jtf_Username.setText("Enter Username");
        jpf_Password.setText("Enter Password");
        jpf_Password.setEchoChar((char) 0);
    }
}