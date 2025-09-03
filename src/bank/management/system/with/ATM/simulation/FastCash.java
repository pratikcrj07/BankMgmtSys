package bank.management.system.with.ATM.simulation;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.ResultSet;
import java.text.SimpleDateFormat;
import java.util.Date;

public class FastCash extends JFrame implements ActionListener {

    JButton b1, b2, b3, b4, b5, b6, b7;
    String cardNumber;

    FastCash(String cardNumber) {
        this.cardNumber = cardNumber;

        ImageIcon i1 = new ImageIcon(ClassLoader.getSystemResource("icon/atm2.png"));
        Image i2 = i1.getImage().getScaledInstance(1550, 830, Image.SCALE_DEFAULT);
        JLabel bg = new JLabel(new ImageIcon(i2));
        bg.setBounds(0, 0, 1550, 830);
        add(bg);

        JLabel label = new JLabel("SELECT WITHDRAWAL AMOUNT");
        label.setBounds(445, 180, 700, 35);
        label.setForeground(Color.WHITE);
        label.setFont(new Font("System", Font.BOLD, 23));
        bg.add(label);

        b1 = new JButton("Rs. 100");
        b1.setForeground(Color.WHITE);
        b1.setBackground(new Color(65, 125, 128));
        b1.setBounds(410, 274, 150, 35);
        b1.addActionListener(this);
        bg.add(b1);

        b2 = new JButton("Rs. 500");
        b2.setForeground(Color.WHITE);
        b2.setBackground(new Color(65, 125, 128));
        b2.setBounds(700, 274, 150, 35);
        b2.addActionListener(this);
        bg.add(b2);

        b3 = new JButton("Rs. 1000");
        b3.setForeground(Color.WHITE);
        b3.setBackground(new Color(65, 125, 128));
        b3.setBounds(410, 318, 150, 35);
        b3.addActionListener(this);
        bg.add(b3);

        b4 = new JButton("Rs. 2000");
        b4.setForeground(Color.WHITE);
        b4.setBackground(new Color(65, 125, 128));
        b4.setBounds(700, 318, 150, 35);
        b4.addActionListener(this);
        bg.add(b4);

        b5 = new JButton("Rs. 5000");
        b5.setForeground(Color.WHITE);
        b5.setBackground(new Color(65, 125, 128));
        b5.setBounds(410, 362, 150, 35);
        b5.addActionListener(this);
        bg.add(b5);

        b6 = new JButton("Rs. 10000");
        b6.setForeground(Color.WHITE);
        b6.setBackground(new Color(65, 125, 128));
        b6.setBounds(700, 362, 150, 35);
        b6.addActionListener(this);
        bg.add(b6);

        b7 = new JButton("BACK");
        b7.setForeground(Color.WHITE);
        b7.setBackground(new Color(65, 125, 128));
        b7.setBounds(700, 406, 150, 35);
        b7.addActionListener(this);
        bg.add(b7);

        setLayout(null);
        setSize(1550, 1080);
        setLocation(0, 0);
        setVisible(true);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == b7) {
            setVisible(false);
            new main_Class(cardNumber);
        } else {
            String amountStr = ((JButton) e.getSource()).getText().substring(4).trim();
            double amount = Double.parseDouble(amountStr);

            try {
                Conn c = new Conn();

                // Calculate balance
                ResultSet rs = c.s.executeQuery(
                        "SELECT SUM(CASE WHEN txn_type = 'Deposit' THEN amount ELSE -amount END) AS balance " +
                                "FROM transactions WHERE card_number = '" + cardNumber + "'"
                );

                double balance = 0;
                if (rs.next()) {
                    balance = rs.getDouble("balance");
                }

                if (balance < amount) {
                    JOptionPane.showMessageDialog(null, " Insufficient Balance");
                    return;
                }

                // Insert withdrawal
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                String now = sdf.format(new Date());

                String sql = "INSERT INTO transactions (card_number, txn_date, txn_type, amount) VALUES ('"
                        + cardNumber + "', '" + now + "', 'Withdrawl', '" + amount + "')";
                c.s.executeUpdate(sql);

                JOptionPane.showMessageDialog(null, " Rs. " + amount + " Debited Successfully");
                setVisible(false);
                new main_Class(cardNumber);

            } catch (Exception ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(null, "Error: " + ex.getMessage());
            }
        }
    }

    public static void main(String[] args) {
        new FastCash("cardNumber");
    }
}
