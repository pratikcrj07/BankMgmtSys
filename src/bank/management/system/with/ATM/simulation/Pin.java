package bank.management.system.with.ATM.simulation;

import com.formdev.flatlaf.FlatLightLaf;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class Pin extends JFrame implements ActionListener {
    JButton changeBtn, backBtn;
    JPasswordField currentPinField, newPinField, reEnterPinField;
    String cardNumber;

    public Pin(String cardNumber) {
        this.cardNumber = cardNumber;

        setTitle("Change PIN");
        setSize(600, 450);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setResizable(false);

        // Background panel with custom color (you can change)
        JPanel panel = new JPanel();
        panel.setBackground(new Color(20, 63, 88));
        panel.setLayout(null);
        add(panel);

        JLabel titleLabel = new JLabel("CHANGE YOUR PIN");
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        titleLabel.setBounds(180, 30, 300, 40);
        panel.add(titleLabel);

        // Current PIN
        JLabel currentPinLabel = new JLabel("Current PIN:");
        currentPinLabel.setForeground(Color.WHITE);
        currentPinLabel.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        currentPinLabel.setBounds(80, 100, 120, 30);
        panel.add(currentPinLabel);

        currentPinField = new JPasswordField();
        currentPinField.setBounds(220, 100, 250, 30);
        currentPinField.setFont(new Font("Segoe UI", Font.BOLD, 18));
        panel.add(currentPinField);

        // New PIN
        JLabel newPinLabel = new JLabel("New PIN:");
        newPinLabel.setForeground(Color.WHITE);
        newPinLabel.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        newPinLabel.setBounds(80, 150, 120, 30);
        panel.add(newPinLabel);

        newPinField = new JPasswordField();
        newPinField.setBounds(220, 150, 250, 30);
        newPinField.setFont(new Font("Segoe UI", Font.BOLD, 18));
        panel.add(newPinField);

        // Re-enter New PIN
        JLabel reEnterPinLabel = new JLabel("Re-Enter New PIN:");
        reEnterPinLabel.setForeground(Color.WHITE);
        reEnterPinLabel.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        reEnterPinLabel.setBounds(80, 200, 150, 30);
        panel.add(reEnterPinLabel);

        reEnterPinField = new JPasswordField();
        reEnterPinField.setBounds(220, 200, 250, 30);
        reEnterPinField.setFont(new Font("Segoe UI", Font.BOLD, 18));
        panel.add(reEnterPinField);

        // Change Button
        changeBtn = new JButton("CHANGE");
        styleButton(changeBtn);
        changeBtn.setBounds(220, 270, 120, 40);
        changeBtn.addActionListener(this);
        panel.add(changeBtn);

        // Back Button
        backBtn = new JButton("BACK");
        styleButton(backBtn);
        backBtn.setBounds(350, 270, 120, 40);
        backBtn.addActionListener(this);
        panel.add(backBtn);

        setVisible(true);
    }

    private void styleButton(JButton btn) {
        btn.setForeground(Color.WHITE);
        btn.setBackground(new Color(0, 120, 215));
        btn.setFont(new Font("Segoe UI", Font.BOLD, 16));
        btn.setFocusPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setBorder(BorderFactory.createLineBorder(new Color(0, 100, 179), 1, true));
    }

    @Override
    public void actionPerformed(ActionEvent ae) {
        if (ae.getSource() == changeBtn) {
            String currentPin = new String(currentPinField.getPassword()).trim();
            String newPin = new String(newPinField.getPassword()).trim();
            String rePin = new String(reEnterPinField.getPassword()).trim();

            if (currentPin.isEmpty() || newPin.isEmpty() || rePin.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please fill in all fields.");
                return;
            }

            if (!newPin.matches("\\d{4}")) {
                JOptionPane.showMessageDialog(this, "New PIN must be exactly 4 digits.");
                return;
            }

            if (!newPin.equals(rePin)) {
                JOptionPane.showMessageDialog(this, "New PIN entries do not match.");
                return;
            }

            try {
                Conn c = new Conn();

                // Verify current PIN from DB
                String query = "SELECT * FROM signup WHERE card_number = ? AND pin_number = ?";
                PreparedStatement pst = c.c.prepareStatement(query);
                pst.setString(1, cardNumber);
                pst.setString(2, currentPin);
                ResultSet rs = pst.executeQuery();

                if (!rs.next()) {
                    JOptionPane.showMessageDialog(this, "Current PIN is incorrect.");
                    pst.close();
                    return;
                }
                pst.close();

                // Update PIN
                String updateQuery = "UPDATE signup SET pin_number = ? WHERE card_number = ?";
                PreparedStatement ps = c.c.prepareStatement(updateQuery);
                ps.setString(1, newPin);
                ps.setString(2, cardNumber);
                int rowsUpdated = ps.executeUpdate();
                ps.close();

                if (rowsUpdated > 0) {
                    JOptionPane.showMessageDialog(this, "PIN changed successfully!");
                    setVisible(false);
                    new main_Class(cardNumber);
                } else {
                    JOptionPane.showMessageDialog(this, "Error: PIN not changed.");
                }

            } catch (Exception ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this, "Error changing PIN: " + ex.getMessage());
            }
        } else if (ae.getSource() == backBtn) {
            setVisible(false);
            new main_Class(cardNumber);
        }
    }

    public static void main(String[] args) {
        // Apply FlatLaf for modern UI
        try {
            UIManager.setLookAndFeel(new FlatLightLaf());
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        SwingUtilities.invokeLater(() -> new Pin("1234567890")); // Example card number
    }
}
