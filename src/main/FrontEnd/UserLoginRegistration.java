package main.FrontEnd;

import main.Database.Database;
import main.Enums.UserType;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class UserLoginRegistration {
    private JFrame frame;
    public void run() {
        frame = new JFrame("User Login & Registration");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(800, 600);

        JTabbedPane tabbedPane = new JTabbedPane();
        JPanel loginPanel = createLoginPanel();
        JPanel registrationPanel = createRegistrationPanel();

        tabbedPane.addTab("Login", loginPanel);
        tabbedPane.addTab("Register", registrationPanel);
        frame.add(tabbedPane);

        frame.setVisible(true);
    }

    private JPanel createLoginPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(4, 4, 4, 4);

        JLabel nameLabel = new JLabel("Name:");
        JTextField nameField = new JTextField(20);
        JLabel passwordLabel = new JLabel("Password:");
        JPasswordField passwordField = new JPasswordField(20);
        JButton loginButton = new JButton("Login");

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
                if (Database.checkLogin(name, password)) {
                    JOptionPane.showMessageDialog(null, "Login successful!");
                    switchToAccountPage(name);
                } else {
                    JOptionPane.showMessageDialog(null, "Login failed. Please check your credentials.");
                }
            }
        });

        return panel;
    }

    private JPanel createRegistrationPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
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

}
