package bank.management.system.with.ATM.simulation;

import com.formdev.flatlaf.FlatIntelliJLaf;
import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.io.File;
import java.sql.*;

public class AdminDashboard extends JFrame {
    private JPanel cardListPanel, userDetailPanel;
    private JTextField[] editableFields;
    private String[] editableColumns = {
            "name", "father_name", "dob", "gender", "email", "marital_status", "address",
            "city", "postal_code", "province", "religion", "income", "occupation",
            "education", "citizenship", "phone", "account_type", "pin_number"
    };
    private String currentCardNumber;

    public AdminDashboard() {
        FlatIntelliJLaf.setup();
        setTitle("Admin Dashboard – ATM Simulation");
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        add(buildLeftPanel(), BorderLayout.WEST);
        add(buildCenterPanel(), BorderLayout.CENTER);

        loadCardNumbers();
        setVisible(true);
    }

    private JPanel buildLeftPanel() {
        JPanel left = new JPanel(new BorderLayout());
        left.setBackground(new Color(33, 45, 62));
        left.setPreferredSize(new Dimension(280, getHeight()));

        JLabel header = new JLabel("Admin Dashboard for ATM Simulation", SwingConstants.CENTER);
        header.setForeground(Color.WHITE);
        header.setFont(new Font("Segoe UI", Font.BOLD, 14));
        header.setBorder(new EmptyBorder(20, 10, 10, 10));

        JLabel userLbl = new JLabel("Admin User", SwingConstants.CENTER);
        userLbl.setForeground(Color.LIGHT_GRAY);
        userLbl.setFont(new Font("Segoe UI", Font.PLAIN, 12));

        JPanel topBox = new JPanel();
        topBox.setLayout(new BoxLayout(topBox, BoxLayout.Y_AXIS));
        topBox.setBackground(new Color(45, 60, 80));
        topBox.setBorder(BorderFactory.createLineBorder(Color.WHITE));
        topBox.add(header);
        topBox.add(userLbl);
        topBox.add(Box.createVerticalStrut(10));

        JLabel accTitle = new JLabel("Account Numbers", SwingConstants.CENTER);
        accTitle.setForeground(Color.WHITE);
        accTitle.setFont(new Font("Segoe UI", Font.BOLD, 13));
        accTitle.setBorder(new EmptyBorder(10, 0, 10, 0));

        cardListPanel = new JPanel();
        cardListPanel.setLayout(new BoxLayout(cardListPanel, BoxLayout.Y_AXIS));
        cardListPanel.setBackground(new Color(33, 45, 62));

        JScrollPane scroll = new JScrollPane(cardListPanel);
        scroll.setBorder(null);
        scroll.setPreferredSize(new Dimension(280, 400)); // Scrollable height
        scroll.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        scroll.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);

        JPanel centerBox = new JPanel(new BorderLayout());
        centerBox.setBackground(new Color(33, 45, 62));
        centerBox.add(accTitle, BorderLayout.NORTH);
        centerBox.add(scroll, BorderLayout.CENTER);

        left.add(topBox, BorderLayout.NORTH);
        left.add(centerBox, BorderLayout.CENTER);

        return left;
    }

    private JScrollPane buildCenterPanel() {
        userDetailPanel = new JPanel();
        userDetailPanel.setLayout(new BoxLayout(userDetailPanel, BoxLayout.Y_AXIS));
        userDetailPanel.setBackground(Color.WHITE);
        userDetailPanel.setBorder(new EmptyBorder(30, 30, 30, 30));

        JScrollPane scrollPane = new JScrollPane(userDetailPanel);
        scrollPane.setBorder(null);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        return scrollPane;
    }

    private void loadCardNumbers() {
        cardListPanel.removeAll();
        try (Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/bank_system", "root", "root");
             Statement stmt = con.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT card_number FROM signup WHERE card_number IS NOT NULL")) {  // <-- changed here

            while (rs.next()) {
                String c = rs.getString("card_number");
                JButton btn = new JButton(c);
                btn.setMaximumSize(new Dimension(250, 36));
                btn.setBackground(new Color(55, 75, 95));
                btn.setForeground(Color.WHITE);
                btn.setFont(new Font("Segoe UI", Font.BOLD, 14));
                btn.setFocusPainted(false);
                btn.addActionListener(e -> showUserDetails(c));
                cardListPanel.add(Box.createVerticalStrut(8));
                cardListPanel.add(btn);
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error loading cards: " + e.getMessage());
        }
        cardListPanel.revalidate();
        cardListPanel.repaint();
    }

    private void showUserDetails(String card) {
        currentCardNumber = card;
        userDetailPanel.removeAll();
        editableFields = new JTextField[editableColumns.length];

        try (Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/bank_system", "root", "root");
             PreparedStatement pst = con.prepareStatement("SELECT * FROM signup WHERE card_number = ?")) {
            pst.setString(1, card);
            ResultSet rs = pst.executeQuery();
            if (rs.next()) {
                JPanel top = new JPanel(new BorderLayout(20, 10));
                top.setBackground(Color.WHITE);

                // Photo section
                JPanel right = new JPanel();
                right.setBackground(Color.WHITE);
                right.setLayout(new BoxLayout(right, BoxLayout.Y_AXIS));

                JLabel photo = new JLabel();
                photo.setPreferredSize(new Dimension(160, 180));
                photo.setBorder(BorderFactory.createLineBorder(Color.GRAY));
                String path = rs.getString("face_image_path");
                if (path != null && new File(path).exists()) {
                    ImageIcon ic = new ImageIcon(ImageIO.read(new File(path))
                            .getScaledInstance(160, 180, Image.SCALE_SMOOTH));
                    photo.setIcon(ic);
                } else {
                    photo.setText("No Image");
                    photo.setHorizontalAlignment(SwingConstants.CENTER);
                }
                right.add(photo);
                right.add(Box.createVerticalStrut(20));

                JButton exit = createButton("Exit", new Color(180, 0, 0));
                exit.setAlignmentX(Component.CENTER_ALIGNMENT);
                exit.addActionListener(e -> {
                    dispose();
                    new Login();
                });
                right.add(exit);

                // Info section
                JPanel info = new JPanel();
                info.setBackground(Color.WHITE);
                info.setLayout(new BoxLayout(info, BoxLayout.Y_AXIS));
                addField(info, "Card Number", card, false);
                for (int i = 0; i < editableColumns.length; i++) {
                    editableFields[i] = addField(info, capitalize(editableColumns[i]), rs.getString(editableColumns[i]), true);
                }

                top.add(info, BorderLayout.CENTER);
                top.add(right, BorderLayout.EAST);
                userDetailPanel.add(top);
                userDetailPanel.add(Box.createVerticalStrut(25));

                // Buttons
                JPanel btns = new JPanel(new FlowLayout(FlowLayout.CENTER, 30, 5));
                btns.setBackground(Color.WHITE);
                JButton upd = createButton("Update", new Color(0, 123, 255));
                JButton del = createButton("Delete", Color.RED);
                JButton ref = createButton("Refresh", Color.GRAY);

                upd.addActionListener(e -> updateUser());
                del.addActionListener(e -> deleteUser(card));
                ref.addActionListener(e -> showUserDetails(card));

                btns.add(upd);
                btns.add(del);
                btns.add(ref);
                userDetailPanel.add(btns);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error fetch: " + e.getMessage());
        }
        userDetailPanel.revalidate();
        userDetailPanel.repaint();
    }

    private JButton createButton(String text, Color color) {
        JButton btn = new JButton(text);
        btn.setBackground(color);
        btn.setForeground(Color.WHITE);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btn.setPreferredSize(new Dimension(140, 40));
        btn.setMaximumSize(new Dimension(140, 40));
        btn.setFocusPainted(false);
        return btn;
    }

    private JTextField addField(JPanel p, String label, String val, boolean edit) {
        JPanel row = new JPanel(new BorderLayout(10, 5));
        row.setBackground(Color.WHITE);
        JLabel l = new JLabel(label + ":");
        l.setPreferredSize(new Dimension(130, 26));
        l.setFont(new Font("Segoe UI", Font.BOLD, 13));
        JTextField tf = new JTextField(val != null ? val : "");
        tf.setEditable(edit);
        tf.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        row.add(l, BorderLayout.WEST);
        row.add(tf, BorderLayout.CENTER);
        p.add(row);
        p.add(Box.createVerticalStrut(6));
        return tf;
    }

    private void updateUser() {
        try (Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/bank_system", "root", "root")) {
            StringBuilder sql = new StringBuilder("UPDATE signup SET ");
            for (int i = 0; i < editableColumns.length; i++) {
                sql.append(editableColumns[i]).append("=?");
                if (i < editableColumns.length - 1) sql.append(", ");
            }
            sql.append(" WHERE card_number=?");
            PreparedStatement pst = con.prepareStatement(sql.toString());
            for (int i = 0; i < editableFields.length; i++) {
                pst.setString(i + 1, editableFields[i].getText().trim());
            }
            pst.setString(editableFields.length + 1, currentCardNumber);
            int r = pst.executeUpdate();
            JOptionPane.showMessageDialog(this, r > 0 ? "Updated" : "Not updated");
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Update error: " + e.getMessage());
        }
    }

    private void deleteUser(String card) {
        int c = JOptionPane.showConfirmDialog(this, "Delete user?", "Confirm", JOptionPane.YES_NO_OPTION);
        if (c != JOptionPane.YES_OPTION) return;
        try (Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/bank_system", "root", "root");
             PreparedStatement pst = con.prepareStatement("DELETE FROM signup WHERE card_number=?")) {
            pst.setString(1, card);
            pst.executeUpdate();
            JOptionPane.showMessageDialog(this, "Deleted");
            loadCardNumbers();
            userDetailPanel.removeAll();
            userDetailPanel.revalidate();
            userDetailPanel.repaint();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Delete error: " + e.getMessage());
        }
    }

    private String capitalize(String s) {
        if (s == null || s.isEmpty()) return s;
        return Character.toUpperCase(s.charAt(0)) + s.substring(1).replace("_", " ");
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(AdminDashboard::new);
    }
}
