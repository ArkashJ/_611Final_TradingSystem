package main.FrontEnd;

import main.Database.Database;
import main.Enums.UserType;

import javax.swing.*;
import javax.swing.plaf.basic.BasicTabbedPaneUI;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

/**
* @Description: This class is used to create a User Login & Registration Page
* This class is part of the main.FrontEnd package and provides an interface for the user to login or register.
* The interface allows the user to login with their username and password, or register a new account.
* The user can also choose to login as an admin.
* Dependencies: main.Database.Database, main.Enums.UserType, java.sql.* and javax.swing.*
 */

public class UserLoginRegistration {
    private JFrame frame;
    private Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
    public void run() {
        frame = new JFrame("User Login & Registration");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(screenSize.width, screenSize.height);

        JTabbedPane tabbedPane = new JTabbedPane();
        JPanel loginPanel = createLoginPanel();
        JPanel registrationPanel = createRegistrationPanel();

        tabbedPane.addTab("Login", loginPanel);
        tabbedPane.addTab("Register", registrationPanel);

        // Create a custom TabbedPaneUI to modify the tab names
        tabbedPane.setUI(new BasicTabbedPaneUI() {
            @Override
            protected void paintText(Graphics g, int tabPlacement, Font font, FontMetrics metrics,
                                     int tabIndex, String title, Rectangle textRect, boolean isSelected) {
                Font italicFont = font.deriveFont(Font.ITALIC, 15); // Set the font style and size
                g.setFont(italicFont);
                super.paintText(g, tabPlacement, italicFont, metrics, tabIndex, title, textRect, isSelected);
            }
        });

        frame.add(tabbedPane);
        frame.setVisible(true);
    }



    private JPanel createLoginPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(Color.CYAN); // Set the background color to blue

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(4, 4, 4, 4);

        JLabel nameLabel = new JLabel("Name:");
        JTextField nameField = new JTextField(20);
        JLabel passwordLabel = new JLabel("Password:");
        JPasswordField passwordField = new JPasswordField(20);
        JButton loginButton = new JButton("Login");

        // Set the font size to 20 for labels, text fields, and button
        Font font = new Font(Font.SANS_SERIF, Font.BOLD, 20);
        nameLabel.setFont(font);
        nameField.setFont(font);
        passwordLabel.setFont(font);
        passwordField.setFont(font);
        loginButton.setFont(font);

        gbc.gridx = 0;
        gbc.gridy = 0;
        panel.add(nameLabel, gbc);
        gbc.gridx = 1;
        panel.add(nameField, gbc);
        gbc.gridx = 0;
        gbc.gridy = 1;
        panel.add(passwordLabel, gbc);
        gbc.gridx = 1;
        panel.add(passwordField, gbc);
        gbc.gridx = 1;
        gbc.gridy = 2;
        panel.add(loginButton, gbc);

        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String name = nameField.getText();
                String password = String.valueOf(passwordField.getPassword());
                UserType userType = Database.checkLogin(name, password);
                if (userType != null) {
                    JOptionPane.showMessageDialog(null, "Login successful!");
                    switch (userType) {
                        case ADMIN:
                            switchToManagerPage(name);
                            break;
                        case USER:
                            switchToAccountPage(name);
                            break;
                    }
                } else {
                    JOptionPane.showMessageDialog(null, "Login failed. Please check your credentials.");
                }
            }
        });

        return panel;
    }


    private JPanel createRegistrationPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(Color.PINK); // Set the background color to blue

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(4, 4, 4, 4);

        JLabel nameLabel = new JLabel("Name:");
        JTextField nameField = new JTextField(20);
        JLabel passwordLabel = new JLabel("Password:");
        JPasswordField passwordField = new JPasswordField(20);
        JLabel roleLabel = new JLabel("Role:");
        JComboBox<String> roleComboBox = new JComboBox<>(new String[]{"Admin", "User"});
        JLabel accountNumberLabel = new JLabel("Account Number:");
        JTextField accountNumberField = new JTextField(20);
        JButton registerButton = new JButton("Register");

        // Set the font size to 20 for labels, text fields, combo box, and button
        Font font = new Font(Font.SANS_SERIF, Font.BOLD, 20);
        nameLabel.setFont(font);
        nameField.setFont(font);
        passwordLabel.setFont(font);
        passwordField.setFont(font);
        roleLabel.setFont(font);
        roleComboBox.setFont(font);
        accountNumberLabel.setFont(font);
        accountNumberField.setFont(font);
        registerButton.setFont(font);

        gbc.gridx = 0;
        gbc.gridy = 0;
        panel.add(nameLabel, gbc);
        gbc.gridx = 1;
        panel.add(nameField, gbc);
        gbc.gridx = 0;
        gbc.gridy = 1;
        panel.add(passwordLabel, gbc);
        gbc.gridx = 1;
        panel.add(passwordField, gbc);
        gbc.gridx = 0;
        gbc.gridy = 2;
        panel.add(roleLabel, gbc);
        gbc.gridx = 1;
        panel.add(roleComboBox, gbc);
        gbc.gridx = 0;
        gbc.gridy = 3;
        panel.add(accountNumberLabel, gbc);
        gbc.gridx = 1;
        panel.add(accountNumberField, gbc);
        gbc.gridx = 1;
        gbc.gridy = 4;
        panel.add(registerButton, gbc);

        registerButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String name = nameField.getText();
                String password = String.valueOf(passwordField.getPassword());
                UserType role = UserType.valueOf(((String) roleComboBox.getSelectedItem()).toUpperCase());
                int accountNumber = Integer.parseInt(accountNumberField.getText());
                if (Database.registerUser(name, password, role, accountNumber)) {
                    JOptionPane.showMessageDialog(null, "Registration successful!");
                } else {
                    JOptionPane.showMessageDialog(null, "Registration failed. Please try again.");
                }
            }
        });

        return panel;
    }


    public void switchToAccountPage(String ownerName) {
        frame.dispose();
        new AccountPage(ownerName,this).run();
    }
    public void switchToManagerPage(String adminName) {
        frame.dispose();
        new ManagerPage(adminName).run();
    }
}

