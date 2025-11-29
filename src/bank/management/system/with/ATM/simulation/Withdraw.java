package bank.management.system.with.ATM.simulation;

import com.formdev.flatlaf.FlatLightLaf;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Withdraw extends JFrame implements ActionListener {
    String cardNumber;
    JTextField amountField;
    JButton withdrawBtn, backBtn;

    public Withdraw(String cardNumber) {
        this.cardNumber = cardNumber;

        setTitle("ATM Withdraw");

        // Background image
        ImageIcon i1 = new ImageIcon(ClassLoader.getSystemResource("icon/atm2.png"));
        Image i2 = i1.getImage().getScaledInstance(1550, 830, Image.SCALE_SMOOTH);
        JLabel bg = new JLabel(new ImageIcon(i2));
        bg.setBounds(0, 0, 1550, 830);
        add(bg);

        // Labels
        JLabel label1 = new JLabel("MAXIMUM WITHDRAWAL IS RS.10,000");
        label1.setForeground(Color.WHITE);
        label1.setFont(new Font("Segoe UI", Font.BOLD, 16));
        label1.setBounds(460, 180, 700, 35);
        bg.add(label1);

        JLabel label2 = new JLabel("PLEASE ENTER YOUR AMOUNT");
        label2.setForeground(Color.WHITE);
        label2.setFont(new Font("Segoe UI", Font.BOLD, 16));
        label2.setBounds(460, 220, 400, 35);
        bg.add(label2);

        // Amount field
        amountField = new JTextField();
        amountField.setBackground(new Color(65, 125, 128));
        amountField.setForeground(Color.WHITE);
        amountField.setBounds(460, 260, 320, 35);
        amountField.setFont(new Font("Segoe UI", Font.BOLD, 22));
        amountField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(45, 95, 98), 2, true),
                BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));
        bg.add(amountField);

        // Withdraw button
        withdrawBtn = new JButton("WITHDRAW");
        styleButton(withdrawBtn, new Color(0, 102, 204));
        withdrawBtn.setBounds(700, 362, 150, 40);
        withdrawBtn.addActionListener(this);
        bg.add(withdrawBtn);

        // Back button
        backBtn = new JButton("BACK");
        styleButton(backBtn, new Color(100, 100, 100));
        backBtn.setBounds(700, 406, 150, 40);
        backBtn.addActionListener(this);
        bg.add(backBtn);

        setLayout(null);
        setSize(1550, 1080);
        setLocation(0, 0);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setVisible(true);
    }

    private void styleButton(JButton button, Color bgColor) {
        button.setForeground(Color.WHITE);
        button.setBackground(bgColor);
        button.setFont(new Font("Segoe UI", Font.BOLD, 16));
        button.setFocusPainted(false);
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        button.setBorder(BorderFactory.createLineBorder(bgColor.darker(), 1, true));
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        try {
            if (e.getSource() == withdrawBtn) {
                String amountStr = amountField.getText().trim();

                if (amountStr.isEmpty()) {
                    JOptionPane.showMessageDialog(this, "Please enter the amount you want to withdraw");
                    return;
                }

                double amount;
                try {
                    amount = Double.parseDouble(amountStr);
                    if (amount <= 500 || amount > 100000) {
                        JOptionPane.showMessageDialog(this, "Amount must be between Rs. 500 and Rs. 100000");
                        return;
                    }
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(this, "Please enter a valid numeric amount");
                    return;
                }

                Conn c = new Conn();
                ResultSet rs = c.s.executeQuery(
                        "SELECT SUM(CASE WHEN txn_type='Deposit' THEN amount ELSE -amount END) AS balance " +
                                "FROM transactions WHERE card_number = '" + cardNumber + "'"
                );

                double balance = 0;
                if (rs.next()) {
                    balance = rs.getDouble("balance");
                }

                if (balance < amount) {
                    JOptionPane.showMessageDialog(this, "Insufficient Balance! Current balance: Rs. " + balance);
                    return;
                }

                String sql = "INSERT INTO transactions (card_number, txn_date, txn_type, amount) VALUES (?, ?, ?, ?)";
                PreparedStatement ps = c.c.prepareStatement(sql);

                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                String now = sdf.format(new Date());

                ps.setString(1, cardNumber);
                ps.setString(2, now);
                ps.setString(3, "Withdrawl");
                ps.setDouble(4, amount);

                ps.executeUpdate();
                ps.close();

                JOptionPane.showMessageDialog(this, "Rs. " + amount + " Debited Successfully");

                setVisible(false);
                new main_Class(cardNumber);

            } else if (e.getSource() == backBtn) {
                setVisible(false);
                new main_Class(cardNumber);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
        }
    }

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(new FlatLightLaf());
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        SwingUtilities.invokeLater(() -> new Withdraw(""));
    }
}
