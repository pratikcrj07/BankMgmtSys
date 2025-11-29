package bank.management.system.with.ATM.simulation;

import com.formdev.flatlaf.FlatIntelliJLaf;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.io.File;
import java.sql.*;

public class Details extends JFrame {

    private JPanel leftDetailsPanel;
    private JPanel rightDetailsPanel;
    private JLabel photoLabel;

    public Details(String cardNumber) {
        super("User Details");

        // FlatLaf look and feel
        try {
            UIManager.setLookAndFeel(new FlatIntelliJLaf());
        } catch (Exception e) {
            e.printStackTrace();
        }

        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // === Top Panel ===
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBackground(new Color(225, 245, 254));
        topPanel.setBorder(new EmptyBorder(20, 20, 0, 20));

        // User photo section
        photoLabel = new JLabel("No Photo", SwingConstants.CENTER);
        photoLabel.setPreferredSize(new Dimension(160, 180));
        photoLabel.setBorder(BorderFactory.createLineBorder(new Color(30, 136, 229), 2));
        photoLabel.setOpaque(true);
        photoLabel.setBackground(Color.WHITE);
        photoLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));

        topPanel.add(photoLabel, BorderLayout.WEST);

        JLabel titleLabel = new JLabel("User Account Details", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 28));
        titleLabel.setForeground(new Color(13, 71, 161));
        titleLabel.setBorder(new EmptyBorder(0, 20, 0, 20));
        topPanel.add(titleLabel, BorderLayout.CENTER);

        add(topPanel, BorderLayout.NORTH);

        // === Center Details Section (2 columns) ===
        JPanel centerPanel = new JPanel(new GridLayout(1, 2, 30, 0));
        centerPanel.setBorder(new EmptyBorder(30, 40, 30, 40));
        centerPanel.setBackground(new Color(245, 252, 255));

        leftDetailsPanel = createRoundedPanel();
        rightDetailsPanel = createRoundedPanel();

        centerPanel.add(leftDetailsPanel);
        centerPanel.add(rightDetailsPanel);

        add(centerPanel, BorderLayout.CENTER);

        // === Bottom Panel with Back Button ===
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        bottomPanel.setBackground(new Color(225, 245, 254));

        JButton backBtn = new JButton("← Back to Main");
        backBtn.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        backBtn.setFocusPainted(false);
        backBtn.setBackground(new Color(30, 136, 229));
        backBtn.setForeground(Color.WHITE);
        backBtn.setPreferredSize(new Dimension(180, 40));
        bottomPanel.add(backBtn);

        add(bottomPanel, BorderLayout.SOUTH);

        backBtn.addActionListener(e -> {
            setVisible(false);
            new main_Class(cardNumber).setVisible(true);
        });

        fetchAndDisplay(cardNumber);
        setVisible(true);
    }

    private JPanel createRoundedPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setOpaque(false);
        panel.setBorder(new CompoundBorder(
                new LineBorder(new Color(200, 200, 200), 1, true),
                new EmptyBorder(20, 20, 20, 20)
        ));
        panel.setBackground(Color.WHITE);
        panel.setOpaque(true);
        return panel;
    }

    private void addDetailRow(JPanel panel, String label, String value) {
        JPanel row = new JPanel(new BorderLayout());
        row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        row.setOpaque(false);

        JLabel fieldLabel = new JLabel(label + ": ");
        fieldLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));

        JLabel fieldValue = new JLabel(value);
        fieldValue.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        fieldValue.setForeground(new Color(33, 33, 33));

        row.add(fieldLabel, BorderLayout.WEST);
        row.add(fieldValue, BorderLayout.CENTER);

        panel.add(row);
        panel.add(Box.createVerticalStrut(10));
    }

    private void fetchAndDisplay(String cardNumber) {
        String sql = "SELECT * FROM signup WHERE card_number = ?";
        try (Connection conn = DriverManager.getConnection(
                "jdbc:mysql://localhost:3306/bank_system", "root", "root");
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, cardNumber);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                // Load image if exists
                String imgPath = rs.getString("face_image_path");
                if (imgPath != null && !imgPath.isEmpty()) {
                    ImageIcon ic = new ImageIcon(ImageIO.read(new File(imgPath)));
                    Image img = ic.getImage().getScaledInstance(160, 180, Image.SCALE_SMOOTH);
                    photoLabel.setText("");
                    photoLabel.setIcon(new ImageIcon(img));
                }

                // Left Column
                addDetailRow(leftDetailsPanel, "Form No", rs.getString("formno"));
                addDetailRow(leftDetailsPanel, "Name", rs.getString("name"));
                addDetailRow(leftDetailsPanel, "Father's Name", rs.getString("father_name"));
                addDetailRow(leftDetailsPanel, "DOB", rs.getString("dob"));
                addDetailRow(leftDetailsPanel, "Gender", rs.getString("gender"));
                addDetailRow(leftDetailsPanel, "Email", rs.getString("email"));
                addDetailRow(leftDetailsPanel, "Marital Status", rs.getString("marital_status"));
                addDetailRow(leftDetailsPanel, "Phone", rs.getString("phone"));
                addDetailRow(leftDetailsPanel, "Citizenship No", rs.getString("citizenship"));

                // Right Column
                addDetailRow(rightDetailsPanel, "Address", rs.getString("address"));
                addDetailRow(rightDetailsPanel, "City", rs.getString("city"));
                addDetailRow(rightDetailsPanel, "Postal Code", rs.getString("postal_code"));
                addDetailRow(rightDetailsPanel, "Province", rs.getString("province"));
                addDetailRow(rightDetailsPanel, "Religion", rs.getString("religion"));
                addDetailRow(rightDetailsPanel, "Income", rs.getString("income"));
                addDetailRow(rightDetailsPanel, "Occupation", rs.getString("occupation"));
                addDetailRow(rightDetailsPanel, "Education", rs.getString("education"));
                addDetailRow(rightDetailsPanel, "Account Type", rs.getString("account_type"));
                addDetailRow(rightDetailsPanel, "PIN Number", rs.getString("pin_number"));
            } else {
                JOptionPane.showMessageDialog(this, "No user found with card number: " + cardNumber);
            }

        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error fetching user data:\n" + ex.getMessage());
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new Details("")); // Replace with a valid card number
    }
}
