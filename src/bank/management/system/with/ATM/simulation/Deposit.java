package bank.management.system.with.ATM.simulation;

import com.formdev.flatlaf.FlatLightLaf;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.PreparedStatement;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Deposit extends JFrame implements ActionListener {
    String cardNumber;
    JTextField amountField;

    JButton depositBtn, backBtn;

    public Deposit(String cardNumber) {
        this.cardNumber = cardNumber;

        setTitle("ATM Deposit");

        // Background image
        ImageIcon i1 = new ImageIcon(ClassLoader.getSystemResource("icon/atm2.png"));
        Image i2 = i1.getImage().getScaledInstance(1550, 830, Image.SCALE_SMOOTH);
        ImageIcon i3 = new ImageIcon(i2);
        JLabel bg = new JLabel(i3);
        bg.setBounds(0, 0, 1550, 830);
        add(bg);

        // Label
        JLabel label1 = new JLabel("ENTER AMOUNT YOU WANT TO DEPOSIT");
        label1.setForeground(Color.WHITE);
        label1.setFont(new Font("Segoe UI", Font.BOLD, 16));
        label1.setBounds(460, 180, 400, 35);
        bg.add(label1);

        // Amount field - JTextField styled
        amountField = new JTextField();
        amountField.setBackground(new Color(65, 125, 128));
        amountField.setForeground(Color.WHITE);
        amountField.setBounds(460, 230, 320, 35);
        amountField.setFont(new Font("Segoe UI", Font.BOLD, 22));
        amountField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(45, 95, 98), 2, true),
                BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));
        bg.add(amountField);

        // Deposit button
        depositBtn = new JButton("DEPOSIT");
        styleButton(depositBtn, new Color(0, 102, 204));
        depositBtn.setBounds(700, 362, 150, 40);
        depositBtn.addActionListener(this);
        bg.add(depositBtn);

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
            if (e.getSource() == depositBtn) {
                String amountStr = amountField.getText().trim();

                if (amountStr.isEmpty()) {
                    JOptionPane.showMessageDialog(null, "Please enter the amount you want to deposit");
                    return;
                }

                double amount;
                try {
                    amount = Double.parseDouble(amountStr);
                    if (amount <= 0) {
                        JOptionPane.showMessageDialog(null, "Amount must be positive");
                        return;
                    }
                } catch (NumberFormatException nfe) {
                    JOptionPane.showMessageDialog(null, "Please enter a valid numeric amount");
                    return;
                }

                Conn c = new Conn();
                String sql = "INSERT INTO transactions (card_number, txn_date, txn_type, amount) VALUES (?, ?, ?, ?)";
                PreparedStatement ps = c.c.prepareStatement(sql);

                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                String now = sdf.format(new Date());

                ps.setString(1, cardNumber);
                ps.setString(2, now);
                ps.setString(3, "Deposit");
                ps.setDouble(4, amount);

                ps.executeUpdate();
                ps.close();

                JOptionPane.showMessageDialog(null, "Rs. " + amount + " Deposited Successfully");

                setVisible(false);
                new main_Class(cardNumber);

            } else if (e.getSource() == backBtn) {
                setVisible(false);
                new main_Class(cardNumber);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(null, "Error: " + ex.getMessage());
        }
    }

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(new FlatLightLaf());
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        SwingUtilities.invokeLater(() -> new Deposit("1234567890"));
    }
}
