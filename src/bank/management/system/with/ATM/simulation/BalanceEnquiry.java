package bank.management.system.with.ATM.simulation;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.ResultSet;

public class BalanceEnquiry extends JFrame implements ActionListener {

    String cardNumber;
    JLabel balanceLabel;
    JButton backBtn;

    BalanceEnquiry(String cardNumber) {
        this.cardNumber = cardNumber;

        ImageIcon i1 = new ImageIcon(ClassLoader.getSystemResource("icon/atm2.png"));
        Image i2 = i1.getImage().getScaledInstance(1550, 830, Image.SCALE_DEFAULT);
        JLabel bg = new JLabel(new ImageIcon(i2));
        bg.setBounds(0, 0, 1550, 830);
        add(bg);

        JLabel label1 = new JLabel("Your Current Balance is Rs:");
        label1.setForeground(Color.WHITE);
        label1.setFont(new Font("System", Font.BOLD, 16));
        label1.setBounds(430, 180, 700, 35);
        bg.add(label1);

        balanceLabel = new JLabel();
        balanceLabel.setForeground(Color.WHITE);
        balanceLabel.setFont(new Font("System", Font.BOLD, 16));
        balanceLabel.setBounds(430, 220, 400, 35);
        bg.add(balanceLabel);

        backBtn = new JButton("BACK");
        backBtn.setBounds(700, 406, 150, 35);
        backBtn.setBackground(new Color(65, 125, 128));
        backBtn.setForeground(Color.WHITE);
        backBtn.addActionListener(this);
        bg.add(backBtn);

        double balance = 0;
        try {
            Conn c = new Conn();
            ResultSet rs = c.s.executeQuery(
                    "SELECT SUM(CASE WHEN txn_type = 'Deposit' THEN amount ELSE -amount END) AS balance " +
                            "FROM transactions WHERE card_number = '" + cardNumber + "'"
            );
            if (rs.next()) {
                balance = rs.getDouble("balance");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        balanceLabel.setText(String.format("%.2f", balance));

        setLayout(null);
        setSize(1550, 1080);
        setLocation(0, 0);
        setVisible(true);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        setVisible(false);
        new main_Class(cardNumber);
    }

    public static void main(String[] args) {
        new BalanceEnquiry("cardNumber");
    }
}