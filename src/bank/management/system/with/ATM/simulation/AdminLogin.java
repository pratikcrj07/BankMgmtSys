package bank.management.system.with.ATM.simulation;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

public class AdminLogin extends JFrame implements ActionListener {
    JTextField userField;
    JPasswordField pinField;
    JButton loginBtn, backBtn;

    public AdminLogin() {
        super("Admin Dashboard Login");

        setLayout(null);
        setSize(900, 500);
        setLocation(450, 200);
        setResizable(false);

        // Bank Logo
        ImageIcon bankIcon = new ImageIcon(ClassLoader.getSystemResource("icon/bank.png"));
        Image bankImg = bankIcon.getImage().getScaledInstance(100, 100, Image.SCALE_DEFAULT);
        JLabel labelBankLogo = new JLabel(new ImageIcon(bankImg));
        labelBankLogo.setBounds(350, 10, 100, 100);
        add(labelBankLogo);

        // Title
        JLabel labelTitle = new JLabel("ADMIN LOGIN PANEL");
        labelTitle.setForeground(Color.WHITE);
        labelTitle.setFont(new Font("AvantGarde", Font.BOLD, 38));
        labelTitle.setBounds(200, 125, 500, 40);
        add(labelTitle);

        // Username
        JLabel userLabel = new JLabel("Username:");
        userLabel.setFont(new Font("Raleway", Font.BOLD, 28));
        userLabel.setForeground(Color.WHITE);
        userLabel.setBounds(100, 200, 200, 30);
        add(userLabel);

        userField = new JTextField();
        userField.setBounds(300, 200, 300, 30);
        userField.setFont(new Font("Arial", Font.BOLD, 14));
        add(userField);

        // PIN
        JLabel pinLabel = new JLabel("PIN:");
        pinLabel.setFont(new Font("Raleway", Font.BOLD, 28));
        pinLabel.setForeground(Color.WHITE);
        pinLabel.setBounds(100, 260, 200, 30);
        add(pinLabel);

        pinField = new JPasswordField();
        pinField.setBounds(300, 260, 300, 30);
        pinField.setFont(new Font("Arial", Font.BOLD, 14));
        add(pinField);

        // Login Button
        loginBtn = new JButton("LOGIN");
        loginBtn.setFont(new Font("Arial", Font.BOLD, 14));
        loginBtn.setForeground(Color.WHITE);
        loginBtn.setBackground(Color.BLACK);
        loginBtn.setBounds(300, 320, 130, 30);
        loginBtn.addActionListener(this);
        add(loginBtn);

        // Back Button
        backBtn = new JButton("BACK");
        backBtn.setFont(new Font("Arial", Font.BOLD, 14));
        backBtn.setForeground(Color.WHITE);
        backBtn.setBackground(Color.GRAY);
        backBtn.setBounds(470, 320, 130, 30);
        backBtn.addActionListener(this);
        add(backBtn);

        // Background
        ImageIcon bgIcon = new ImageIcon(ClassLoader.getSystemResource("icon/backbg.png"));
        Image bg = bgIcon.getImage().getScaledInstance(900, 500, Image.SCALE_DEFAULT);
        JLabel background = new JLabel(new ImageIcon(bg));
        background.setBounds(0, 0, 900, 500);
        add(background);

        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setVisible(true);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == loginBtn) {
            String user = userField.getText().trim();
            String pin = new String(pinField.getPassword()).trim();

            try {
                Conn c = new Conn();
                PreparedStatement pst = c.c.prepareStatement("SELECT * FROM admin WHERE username=? AND pin=?");
                pst.setString(1, user);
                pst.setString(2, pin);
                ResultSet rs = pst.executeQuery();

                if (rs.next()) {
                    setVisible(false);
                    new AdminDashboard();  // <-- you should create this class
                } else {
                    JOptionPane.showMessageDialog(this, "Invalid admin credentials");
                }
            } catch (Exception ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this, "Database error");
            }

        } else if (e.getSource() == backBtn) {
            this.dispose();
            new Login();
        }
    }

    public static void main(String[] args) {
        new AdminLogin();
    }
}
