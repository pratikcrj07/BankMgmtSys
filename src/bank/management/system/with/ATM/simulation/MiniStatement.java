package bank.management.system.with.ATM.simulation;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.*;
import java.sql.ResultSet;

public class MiniStatement extends JFrame implements ActionListener {

    String cardNumber;
    JButton closeButton;

    MiniStatement(String cardNumber) {
        this.cardNumber = cardNumber;

        // Frame base settings
        setTitle("Mini Statement");
        setSize(500, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Use a JPanel with BoxLayout
        JPanel contentPane = new JPanel();
        contentPane.setBackground(new Color(245, 250, 255));
        contentPane.setLayout(new BoxLayout(contentPane, BoxLayout.Y_AXIS));
        contentPane.setBorder(new EmptyBorder(20, 20, 20, 20));
        setContentPane(contentPane);

        // Bank title
        JLabel bankLabel = new JLabel("Bank Management System");
        bankLabel.setFont(new Font("Segoe UI", Font.BOLD, 22));
        bankLabel.setForeground(new Color(0, 102, 204));
        bankLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        contentPane.add(bankLabel);

        contentPane.add(Box.createRigidArea(new Dimension(0, 20)));

        // Card number label
        JLabel cardLabel = new JLabel();
        cardLabel.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        cardLabel.setForeground(Color.DARK_GRAY);
        cardLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        contentPane.add(cardLabel);

        // Current balance label
        JLabel balanceLabel = new JLabel();
        balanceLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        balanceLabel.setForeground(new Color(0, 153, 76));
        balanceLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        contentPane.add(balanceLabel);

        contentPane.add(Box.createRigidArea(new Dimension(0, 20)));

        // Transactions area inside scroll pane
        JTextArea transactionsArea = new JTextArea();
        transactionsArea.setFont(new Font("Consolas", Font.PLAIN, 14));
        transactionsArea.setEditable(false);
        transactionsArea.setBackground(new Color(255, 255, 255));
        transactionsArea.setMargin(new Insets(10, 10, 10, 10));

        JScrollPane scrollPane = new JScrollPane(transactionsArea);
        scrollPane.setPreferredSize(new Dimension(440, 350));
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200)));
        contentPane.add(scrollPane);

        contentPane.add(Box.createRigidArea(new Dimension(0, 20)));

        closeButton = new JButton("Close");
        closeButton.setBackground(new Color(0, 102, 204));
        closeButton.setForeground(Color.WHITE);
        closeButton.setFont(new Font("Segoe UI", Font.BOLD, 14));
        closeButton.setFocusPainted(false);
        closeButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        closeButton.addActionListener(this);
        contentPane.add(closeButton);

        // Fetch and set data
        try {
            Conn c = new Conn();

            // Show masked card number
            ResultSet rsCard = c.s.executeQuery("SELECT card_number FROM Transactions WHERE card_number = '" + cardNumber + "' LIMIT 1");
            if (rsCard.next()) {
                String cn = rsCard.getString("card_number");
                cardLabel.setText("Card Number: " + cn.substring(0, 4) + "XXXXXXXX" + cn.substring(12));
            } else {
                cardLabel.setText("Card Number: Not Found");
            }

            // Show transactions ordered by txn_date DESC
            ResultSet rsTxn = c.s.executeQuery(
                    "SELECT * FROM Transactions WHERE card_number = '" + cardNumber + "' ORDER BY txn_date DESC"
            );

            StringBuilder sb = new StringBuilder();
            double balance = 0.0;

            while (rsTxn.next()) {
                String date = rsTxn.getString("txn_date");
                String type = rsTxn.getString("txn_type");
                double amount = Double.parseDouble(rsTxn.getString("amount"));

                sb.append(String.format("%-20s %-12s Rs. %.2f\n", date, type, amount));

                if (type.equalsIgnoreCase("Deposit")) {
                    balance += amount;
                } else {
                    balance -= amount;
                }
            }

            transactionsArea.setText(sb.toString());
            balanceLabel.setText("Current Balance: Rs. " + String.format("%.2f", balance));

        } catch (Exception e) {
            e.printStackTrace();
        }

        setVisible(true);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        dispose();
    }

    public static void main(String[] args) {
        new MiniStatement("cardNumber"); // Replace with your actual test card number
    }
}
