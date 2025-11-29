package bank.management.system.with.ATM.simulation;

import javax.swing.*;
import javax.swing.border.*;
import com.formdev.flatlaf.themes.FlatMacLightLaf;
import java.awt.*;
import java.awt.event.*;
import java.sql.ResultSet;

public class MiniStatement extends JFrame implements ActionListener {
    String cardNumber;
    JButton closeButton;

    MiniStatement(String cardNumber) {
        this.cardNumber = cardNumber;

        // Set modern FlatLaf theme
        try {
            UIManager.setLookAndFeel(new FlatMacLightLaf());
        } catch (Exception ex) {
            System.err.println("Failed to initialize FlatLaf theme");
        }

        setTitle("Mini Statement - ATM");
        setSize(520, 620);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setUndecorated(false);

        JPanel contentPane = new JPanel();
        contentPane.setBackground(new Color(235, 245, 255));
        contentPane.setLayout(new BorderLayout(15, 15));
        contentPane.setBorder(new EmptyBorder(20, 20, 20, 20));
        setContentPane(contentPane);

        // ------------------ Header Panel ------------------
        JPanel headerPanel = new JPanel();
        headerPanel.setBackground(new Color(0, 102, 204));
        headerPanel.setLayout(new BoxLayout(headerPanel, BoxLayout.Y_AXIS));
        headerPanel.setBorder(new EmptyBorder(10, 10, 10, 10));

        JLabel title = new JLabel("Bank Management System");
        title.setFont(new Font("Segoe UI", Font.BOLD, 24));
        title.setForeground(Color.WHITE);
        title.setAlignmentX(Component.CENTER_ALIGNMENT);
        headerPanel.add(title);

        JLabel subtitle = new JLabel("Mini Statement");
        subtitle.setFont(new Font("Segoe UI", Font.PLAIN, 18));
        subtitle.setForeground(Color.WHITE);
        subtitle.setAlignmentX(Component.CENTER_ALIGNMENT);
        headerPanel.add(subtitle);

        contentPane.add(headerPanel, BorderLayout.NORTH);

        // ------------------ Center Panel ------------------
        JPanel centerPanel = new JPanel();
        centerPanel.setBackground(new Color(250, 250, 255));
        centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));
        centerPanel.setBorder(new CompoundBorder(
                new LineBorder(new Color(200, 200, 255), 1),
                new EmptyBorder(15, 15, 15, 15)
        ));

        JLabel cardLabel = new JLabel("Card Number: Loading...");
        cardLabel.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        cardLabel.setForeground(new Color(60, 60, 60));
        cardLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        centerPanel.add(cardLabel);

        centerPanel.add(Box.createRigidArea(new Dimension(0, 10)));

        JLabel balanceLabel = new JLabel("Balance: Loading...");
        balanceLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        balanceLabel.setForeground(new Color(0, 153, 76));
        balanceLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        centerPanel.add(balanceLabel);

        centerPanel.add(Box.createRigidArea(new Dimension(0, 15)));

        JTextArea transactionsArea = new JTextArea();
        transactionsArea.setFont(new Font("Consolas", Font.PLAIN, 14));
        transactionsArea.setEditable(false);
        transactionsArea.setBackground(new Color(255, 255, 255));
        transactionsArea.setForeground(Color.DARK_GRAY);
        transactionsArea.setMargin(new Insets(10, 10, 10, 10));
        transactionsArea.setBorder(BorderFactory.createLineBorder(new Color(180, 180, 220)));

        JScrollPane scrollPane = new JScrollPane(transactionsArea);
        scrollPane.setPreferredSize(new Dimension(440, 330));
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        centerPanel.add(scrollPane);

        contentPane.add(centerPanel, BorderLayout.CENTER);

        // ------------------ Footer ------------------
        JPanel footerPanel = new JPanel();
        footerPanel.setBackground(new Color(235, 245, 255));

        closeButton = new JButton("Close");
        closeButton.setBackground(new Color(0, 102, 204));
        closeButton.setForeground(Color.WHITE);
        closeButton.setFont(new Font("Segoe UI", Font.BOLD, 14));
        closeButton.setFocusPainted(false);
        closeButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        closeButton.setPreferredSize(new Dimension(120, 35));
        closeButton.addActionListener(this);

        footerPanel.add(closeButton);
        contentPane.add(footerPanel, BorderLayout.SOUTH);

        // ------------------ Data Logic ------------------
        try {
            Conn c = new Conn();

            // Masked card
            ResultSet rsCard = c.s.executeQuery("SELECT card_number FROM Transactions WHERE card_number = '" + cardNumber + "' LIMIT 1");
            if (rsCard.next()) {
                String cn = rsCard.getString("card_number");
                cardLabel.setText("Card Number: " + cn.substring(0, 4) + "XXXXXXXX" + cn.substring(12));
            } else {
                cardLabel.setText("Card Number: Not Found");
            }

            ResultSet rsTxn = c.s.executeQuery(
                    "SELECT * FROM Transactions WHERE card_number = '" + cardNumber + "' ORDER BY txn_date DESC"
            );

            StringBuilder sb = new StringBuilder();
            double balance = 0.0;

            while (rsTxn.next()) {
                String date = rsTxn.getString("txn_date");
                String type = rsTxn.getString("txn_type");
                double amount = Double.parseDouble(rsTxn.getString("amount"));

                sb.append(String.format("%-18s %-10s Rs. %.2f\n", date, type, amount));

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
            JOptionPane.showMessageDialog(this, "Error fetching data.", "Database Error", JOptionPane.ERROR_MESSAGE);
        }

        setVisible(true);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        dispose();
    }

    public static void main(String[] args) {
        new MiniStatement(""); // Replace with your test card
    }
}
