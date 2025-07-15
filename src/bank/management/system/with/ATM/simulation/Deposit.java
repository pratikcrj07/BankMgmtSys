package bank.management.system.with.ATM.simulation;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.sql.PreparedStatement;

public class Deposit extends JFrame implements ActionListener {
    String cardNumber;    // Use card number here directly
    TextField amountField;

    JButton depositBtn, backBtn;

    // Constructor now takes cardNumber directly
    public Deposit(String cardNumber) {
        this.cardNumber = cardNumber;

        setTitle("ATM Deposit");
        ImageIcon i1 = new ImageIcon(ClassLoader.getSystemResource("icon/atm2.png"));
        Image i2 = i1.getImage().getScaledInstance(1550, 830, Image.SCALE_DEFAULT);
        ImageIcon i3 = new ImageIcon(i2);
        JLabel bg = new JLabel(i3);
        bg.setBounds(0, 0, 1550, 830);
        add(bg);

        JLabel label1 = new JLabel("ENTER AMOUNT YOU WANT TO DEPOSIT");
        label1.setForeground(Color.WHITE);
        label1.setFont(new Font("System", Font.BOLD, 16));
        label1.setBounds(460, 180, 400, 35);
        bg.add(label1);

        amountField = new TextField();
        amountField.setBackground(new Color(65, 125, 128));
        amountField.setForeground(Color.WHITE);
        amountField.setBounds(460, 230, 320, 25);
        amountField.setFont(new Font("Raleway", Font.BOLD, 22));
        bg.add(amountField);

        depositBtn = new JButton("DEPOSIT");
        depositBtn.setBounds(700, 362, 150, 35);
        depositBtn.setBackground(new Color(65, 125, 128));
        depositBtn.setForeground(Color.WHITE);
        depositBtn.addActionListener(this);
        bg.add(depositBtn);

        backBtn = new JButton("BACK");
        backBtn.setBounds(700, 406, 150, 35);
        backBtn.setBackground(new Color(65, 125, 128));
        backBtn.setForeground(Color.WHITE);
        backBtn.addActionListener(this);
        bg.add(backBtn);

        setLayout(null);
        setSize(1550, 1080);
        setLocation(0, 0);
        setVisible(true);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        try {
            if (e.getSource() == depositBtn) {
                String amountStr = amountField.getText().trim();

                if (amountStr.isEmpty()) {
                    JOptionPane.showMessageDialog(null, "⚠️ Please enter the amount you want to deposit");
                    return;
                }

                double amount;
                try {
                    amount = Double.parseDouble(amountStr);
                    if (amount <= 0) {
                        JOptionPane.showMessageDialog(null, "⚠️ Amount must be positive");
                        return;
                    }
                } catch (NumberFormatException nfe) {
                    JOptionPane.showMessageDialog(null, "⚠️ Please enter a valid numeric amount");
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

                JOptionPane.showMessageDialog(null, "✅ Rs. " + amount + " Deposited Successfully");

                setVisible(false);
                new main_Class(cardNumber); // Pass cardNumber, NOT formno!

            } else if (e.getSource() == backBtn) {
                setVisible(false);
                new main_Class(cardNumber); // Pass cardNumber, NOT formno!
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(null, " Error: " + ex.getMessage());
        }
    }

    public static void main(String[] args) {
        new Deposit("cardNumber"); // Example card number for test
    }
}
