package bank.management.system.with.ATM.simulation;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.PreparedStatement;

public class Pin extends JFrame implements ActionListener {
    JButton changeBtn, backBtn;
    JPasswordField newPinField, reEnterPinField;
    String cardNumber;

    public Pin(String cardNumber) {
        this.cardNumber = cardNumber;

        ImageIcon i1 = new ImageIcon(ClassLoader.getSystemResource("icon/atm2.png"));
        Image i2 = i1.getImage().getScaledInstance(1550, 830, Image.SCALE_DEFAULT);
        JLabel background = new JLabel(new ImageIcon(i2));
        background.setBounds(0, 0, 1550, 830);
        add(background);

        JLabel titleLabel = new JLabel("CHANGE YOUR PIN");
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setFont(new Font("System", Font.BOLD, 16));
        titleLabel.setBounds(430, 180, 400, 35);
        background.add(titleLabel);

        JLabel newPinLabel = new JLabel("New PIN:");
        newPinLabel.setForeground(Color.WHITE);
        newPinLabel.setFont(new Font("System", Font.BOLD, 16));
        newPinLabel.setBounds(430, 220, 150, 35);
        background.add(newPinLabel);

        newPinField = new JPasswordField();
        newPinField.setBackground(new Color(65, 125, 128));
        newPinField.setForeground(Color.WHITE);
        newPinField.setBounds(600, 220, 180, 25);
        newPinField.setFont(new Font("Raleway", Font.BOLD, 22));
        background.add(newPinField);

        JLabel reEnterPinLabel = new JLabel("Re-Enter New PIN:");
        reEnterPinLabel.setForeground(Color.WHITE);
        reEnterPinLabel.setFont(new Font("System", Font.BOLD, 16));
        reEnterPinLabel.setBounds(430, 260, 400, 35);
        background.add(reEnterPinLabel);

        reEnterPinField = new JPasswordField();
        reEnterPinField.setBackground(new Color(65, 125, 128));
        reEnterPinField.setForeground(Color.WHITE);
        reEnterPinField.setBounds(600, 265, 180, 25);
        reEnterPinField.setFont(new Font("Raleway", Font.BOLD, 22));
        background.add(reEnterPinField);

        changeBtn = new JButton("CHANGE");
        changeBtn.setBounds(700, 362, 150, 35);
        changeBtn.setBackground(new Color(65, 125, 128));
        changeBtn.setForeground(Color.WHITE);
        changeBtn.addActionListener(this);
        background.add(changeBtn);

        backBtn = new JButton("BACK");
        backBtn.setBounds(700, 406, 150, 35);
        backBtn.setBackground(new Color(65, 125, 128));
        backBtn.setForeground(Color.WHITE);
        backBtn.addActionListener(this);
        background.add(backBtn);

        setSize(1550, 1080);
        setLayout(null);
        setLocation(0, 0);
        setVisible(true);
    }

    @Override
    public void actionPerformed(ActionEvent ae) {
        String newPin = new String(newPinField.getPassword()).trim();
        String rePin = new String(reEnterPinField.getPassword()).trim();

        if (!newPin.equals(rePin)) {
            JOptionPane.showMessageDialog(null, "❌ Entered PIN does not match.");
            return;
        }
        if (newPin.isEmpty() || rePin.isEmpty()) {
            JOptionPane.showMessageDialog(null, "❌ PIN fields cannot be empty.");
            return;
        }
        if (!newPin.matches("\\d{4}")) {
            JOptionPane.showMessageDialog(null, "❌ PIN must be exactly 4 digits.");
            return;
        }

        if (ae.getSource() == changeBtn) {
            try {
                Conn c = new Conn();

                String sql = "UPDATE signup SET pin_number = ? WHERE card_number = ?";
                PreparedStatement ps = c.c.prepareStatement(sql);
                ps.setString(1, newPin);
                ps.setString(2, cardNumber);

                int rowsUpdated = ps.executeUpdate();

                if (rowsUpdated > 0) {
                    JOptionPane.showMessageDialog(null, "✅ PIN changed successfully!");
                    setVisible(false);
                    new main_Class(cardNumber); // Keep same cardnumber, updated PIN is in DB.
                } else {
                    JOptionPane.showMessageDialog(null, "❌ Card Number did not match any record. PIN not updated.");
                }

                ps.close();

            } catch (Exception ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(null, "❌ Error changing PIN. Check your database connection and query.");
            }
        } else if (ae.getSource() == backBtn) {
            setVisible(false);
            new main_Class(cardNumber);
        }
    }

    public static void main(String[] args) {
        new Pin("cardNumber"); // For testing only — pass a valid card number
    }
}
