package views;

import database.Database_Manager;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class Sign_Up_Panel extends JPanel {
    private final Database_Manager dbManager;
    private final Main_Frame mainFrame;

    // UI Components
    private JLabel jlbl_SignUp;
    private JButton jbtn_SignUp;
    private JTextField jtf_Username;
    private JPasswordField jpf_Password;
    private JPasswordField jpf_ConfirmPassword;
    private JTextField jtf_Email;
    private JTextField jtf_FullName;
    private JLabel jlbl_Password;
    private JLabel jlbl_ConfirmPassword;
    private JLabel jlbl_Username;
    private JLabel jlbl_Email;
    private JLabel jlbl_FullName;
    private JLabel jlbl_Login;
    private JLabel jlbl_Account_Icon;
    private JLabel jlbl_Background;

    /**
     * Creates new Sign Up Panel
     */
    public Sign_Up_Panel(Main_Frame mainFrame, Database_Manager dbManager) {
        this.dbManager = dbManager;
        this.mainFrame = mainFrame;
        initComponents();
    }

    private void initComponents() {
        jlbl_SignUp = new JLabel();
        jbtn_SignUp = new JButton();
        jtf_Username = new JTextField();
        jpf_Password = new JPasswordField();
        jpf_ConfirmPassword = new JPasswordField();
        jtf_Email = new JTextField();
        jtf_FullName = new JTextField();
        jlbl_Password = new JLabel();
        jlbl_ConfirmPassword = new JLabel();
        jlbl_Username = new JLabel();
        jlbl_Email = new JLabel();
        jlbl_FullName = new JLabel();
        jlbl_Login = new JLabel();
        jlbl_Account_Icon = new JLabel();
        jlbl_Background = new JLabel();

        setBackground(new Color(35, 50, 90));
        setMinimumSize(new Dimension(900, 410));
        setPreferredSize(new Dimension(900, 410));
        setLayout(null);

        jlbl_SignUp.setFont(new Font("Segoe UI", 0, 24));
        jlbl_SignUp.setForeground(new Color(23, 22, 22));
        jlbl_SignUp.setHorizontalAlignment(SwingConstants.CENTER);
        jlbl_SignUp.setText("Sign Up");
        jlbl_SignUp.setBounds(450, 40, 100, 32);
        add(jlbl_SignUp);

        jbtn_SignUp.setBackground(new Color(226, 149, 90));
        jbtn_SignUp.setFont(new Font("Segoe UI", 0, 18));
        jbtn_SignUp.setForeground(new Color(255, 255, 255));
        jbtn_SignUp.setText("Sign Up");
        jbtn_SignUp.setCursor(new Cursor(Cursor.HAND_CURSOR));
        jbtn_SignUp.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                jbtn_SignUpActionPerformed(evt);
            }
        });
        jbtn_SignUp.setBounds(420, 330, 100, 32);
        add(jbtn_SignUp);

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
        jtf_Username.setBounds(410, 100, 180, 25);
        add(jtf_Username);

        jlbl_Username.setForeground(new Color(23, 22, 22));
        jlbl_Username.setText("Username");
        jlbl_Username.setBounds(330, 100, 70, 25);
        add(jlbl_Username);

        // Email field
        jtf_Email.setForeground(new Color(23, 22, 22));
        jtf_Email.setText("Enter Email");
        jtf_Email.addFocusListener(new FocusAdapter() {
            public void focusGained(FocusEvent evt) {
                if ("Enter Email".equals(jtf_Email.getText())) {
                    jtf_Email.setText("");
                }
            }
            public void focusLost(FocusEvent evt) {
                if ("".equals(jtf_Email.getText())) {
                    jtf_Email.setText("Enter Email");
                }
            }
        });
        jtf_Email.setBounds(410, 140, 180, 25);
        add(jtf_Email);

        jlbl_Email.setForeground(new Color(23, 22, 22));
        jlbl_Email.setText("Email");
        jlbl_Email.setBounds(330, 140, 70, 25);
        add(jlbl_Email);

        // Full Name field
        jtf_FullName.setForeground(new Color(23, 22, 22));
        jtf_FullName.setText("Enter Full Name");
        jtf_FullName.addFocusListener(new FocusAdapter() {
            public void focusGained(FocusEvent evt) {
                if ("Enter Full Name".equals(jtf_FullName.getText())) {
                    jtf_FullName.setText("");
                }
            }
            public void focusLost(FocusEvent evt) {
                if ("".equals(jtf_FullName.getText())) {
                    jtf_FullName.setText("Enter Full Name");
                }
            }
        });
        jtf_FullName.setBounds(410, 180, 180, 25);
        add(jtf_FullName);

        jlbl_FullName.setForeground(new Color(23, 22, 22));
        jlbl_FullName.setText("Full Name");
        jlbl_FullName.setBounds(330, 180, 70, 25);
        add(jlbl_FullName);

        // Password field
        jpf_Password.setForeground(new Color(23, 22, 22));
        jpf_Password.setText("Enter Password");
        jpf_Password.setEchoChar((char) 0);
        jpf_Password.addFocusListener(new FocusAdapter() {
            public void focusGained(FocusEvent evt) {
                if ("Enter Password".equals(new String(jpf_Password.getPassword()))) {
                    jpf_Password.setEchoChar('\u2022');
                    jpf_Password.setText("");
                }
            }
            public void focusLost(FocusEvent evt) {
                if (new String(jpf_Password.getPassword()).isEmpty()) {
                    jpf_Password.setEchoChar((char) 0);
                    jpf_Password.setText("Enter Password");
                }
            }
        });
        jpf_Password.setBounds(410, 220, 180, 25);
        add(jpf_Password);

        jlbl_Password.setForeground(new Color(23, 22, 22));
        jlbl_Password.setText("Password");
        jlbl_Password.setBounds(330, 220, 70, 25);
        add(jlbl_Password);

        // Confirm Password field
        jpf_ConfirmPassword.setForeground(new Color(23, 22, 22));
        jpf_ConfirmPassword.setText("Confirm Password");
        jpf_ConfirmPassword.setEchoChar((char) 0);
        jpf_ConfirmPassword.addFocusListener(new FocusAdapter() {
            public void focusGained(FocusEvent evt) {
                if ("Confirm Password".equals(new String(jpf_ConfirmPassword.getPassword()))) {
                    jpf_ConfirmPassword.setEchoChar('\u2022');
                    jpf_ConfirmPassword.setText("");
                }
            }
            public void focusLost(FocusEvent evt) {
                if (new String(jpf_ConfirmPassword.getPassword()).isEmpty()) {
                    jpf_ConfirmPassword.setEchoChar((char) 0);
                    jpf_ConfirmPassword.setText("Confirm Password");
                }
            }
        });
        jpf_ConfirmPassword.setBounds(410, 260, 180, 25);
        add(jpf_ConfirmPassword);

        jlbl_ConfirmPassword.setForeground(new Color(23, 22, 22));
        jlbl_ConfirmPassword.setText("Confirm Password");
        jlbl_ConfirmPassword.setBounds(290, 260, 110, 25);
        add(jlbl_ConfirmPassword);

        // Already have an account? Login link
        jlbl_Login.setForeground(new Color(23, 22, 22));
        jlbl_Login.setText("Already have an account? Login");
        jlbl_Login.setCursor(new Cursor(Cursor.HAND_CURSOR));
        jlbl_Login.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent evt) {
                jlbl_LoginMouseClicked(evt);
            }
            public void mouseEntered(MouseEvent evt) {
                jlbl_Login.setText("<html><u>Already have an account? Login</u></html>");
            }
            public void mouseExited(MouseEvent evt) {
                jlbl_Login.setText("Already have an account? Login");
            }
        });
        jlbl_Login.setBounds(370, 370, 200, 16);
        add(jlbl_Login);

        jlbl_Account_Icon.setIcon(new ImageIcon(getClass().getResource("/assets/icon/AccountBlack.png")));
        jlbl_Account_Icon.setBounds(410, 40, 32, 32);
        add(jlbl_Account_Icon);

        jlbl_Background.setIcon(new ImageIcon(getClass().getResource("/assets/image/background(900x410).png")));
        jlbl_Background.setBounds(0, 0, 910, 410);
        add(jlbl_Background);
    }

    private void jbtn_SignUpActionPerformed(ActionEvent evt) {
        String username = jtf_Username.getText().trim();
        String email = jtf_Email.getText().trim();
        String fullName = jtf_FullName.getText().trim();
        String password = new String(jpf_Password.getPassword()).trim();
        String confirmPassword = new String(jpf_ConfirmPassword.getPassword()).trim();
        
        // Validate input
        if (username.equals("Enter Username") || username.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter a username", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        if (email.equals("Enter Email") || email.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter an email", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        if (fullName.equals("Enter Full Name") || fullName.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter your full name", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        if (password.equals("Enter Password") || password.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter a password", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        if (confirmPassword.equals("Confirm Password") || confirmPassword.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please confirm your password", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        if (!password.equals(confirmPassword)) {
            JOptionPane.showMessageDialog(this, "Passwords do not match", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        try {
            // Check if username already exists
            if (dbManager.userExists(username)) {
                JOptionPane.showMessageDialog(this, "Username already exists", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            // Register the user
            boolean success = dbManager.registerUser(username, password, email, fullName);
            
            if (success) {
                JOptionPane.showMessageDialog(this, "Registration successful! Please log in.", "Success", JOptionPane.INFORMATION_MESSAGE);
                mainFrame.showLoginPanel();
            } else {
                JOptionPane.showMessageDialog(this, "Failed to register user", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "An error occurred during registration", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void jlbl_LoginMouseClicked(MouseEvent evt) {
        mainFrame.showLoginPanel();
    }
    
    public JPanel getPanel() {
        return this;
    }
    
    public void refreshPanel() {
        // Reset fields
        jtf_Username.setText("Enter Username");
        jtf_Email.setText("Enter Email");
        jtf_FullName.setText("Enter Full Name");
        jpf_Password.setText("Enter Password");
        jpf_Password.setEchoChar((char) 0);
        jpf_ConfirmPassword.setText("Confirm Password");
        jpf_ConfirmPassword.setEchoChar((char) 0);
    }
}