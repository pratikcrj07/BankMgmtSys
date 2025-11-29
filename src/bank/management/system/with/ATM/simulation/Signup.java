package bank.management.system.with.ATM.simulation;

import com.toedter.calendar.JDateChooser;
import com.formdev.flatlaf.FlatLightLaf;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.sql.ResultSet;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class Signup extends JFrame implements ActionListener {

    JRadioButton r1, r2, r3, M1, M2, M3;
    JButton next, back;

    JTextField textName, textFname, textEmail, textAdd, textCity, textPostal, textState;
    JDateChooser dateChooser;

    String formNo;

    public Signup() {
        //  Set FlatLaf look and feel
        try {
            UIManager.setLookAndFeel(new FlatLightLaf());
        } catch (Exception ex) {
            System.err.println("FlatLaf initialization failed");
        }

        super.setTitle("APPLICATION FORM");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1336, 768);
        setLocationRelativeTo(null);
        setResizable(false);

        JPanel mainPanel = new JPanel(new GridBagLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;
                g2.setPaint(new GradientPaint(0, 0, new Color(240, 248, 255), 0, getHeight(), new Color(220, 235, 255)));
                g2.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        setContentPane(mainPanel);

        JPanel card = new JPanel(new GridBagLayout());
        card.setBackground(new Color(255, 255, 255, 240));
        card.setBorder(new CompoundBorder(new LineBorder(new Color(200, 220, 255), 2, true),
                new EmptyBorder(40, 60, 40, 60)));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.weightx = 1.0;
        gbc.insets = new Insets(10, 10, 10, 10);

        formNo = generateFormNo();

        ImageIcon i1 = new ImageIcon(ClassLoader.getSystemResource("icon/bank.png"));
        Image i2 = i1.getImage().getScaledInstance(70, 70, Image.SCALE_SMOOTH);
        JLabel logoLabel = new JLabel(new ImageIcon(i2));
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 1;
        card.add(logoLabel, gbc);

        JLabel formNoLabel = new JLabel("APPLICATION FORM NO: " + formNo);
        formNoLabel.setFont(new Font("Segoe UI", Font.BOLD, 28));
        formNoLabel.setForeground(new Color(0, 51, 102));
        gbc.gridx = 1; gbc.gridy = 0;
        card.add(formNoLabel, gbc);

        JLabel pageLabel = new JLabel("Page 1");
        pageLabel.setFont(new Font("Segoe UI", Font.PLAIN, 18));
        pageLabel.setForeground(new Color(0, 51, 102));
        gbc.gridx = 1; gbc.gridy = 1;
        card.add(pageLabel, gbc);

        int row = 2;
        addLabel(card, gbc, "Name :", 0, row); textName = addField(card, gbc, 1, row++);

        addLabel(card, gbc, "Father's Name :", 0, row); textFname = addField(card, gbc, 1, row++);

        addLabel(card, gbc, "Gender :", 0, row);
        JPanel genderPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0)); genderPanel.setOpaque(false);
        r1 = new JRadioButton("Male"); r2 = new JRadioButton("Female"); r3 = new JRadioButton("Others");
        styleRadio(r1); styleRadio(r2); styleRadio(r3);
        ButtonGroup g = new ButtonGroup(); g.add(r1); g.add(r2); g.add(r3);
        genderPanel.add(r1); genderPanel.add(r2); genderPanel.add(r3);
        gbc.gridx = 1; gbc.gridy = row++;
        card.add(genderPanel, gbc);

        addLabel(card, gbc, "Date Of Birth :", 0, row);
        dateChooser = new JDateChooser();
        dateChooser.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        dateChooser.setPreferredSize(new Dimension(250, 30));
        dateChooser.getDateEditor().setEnabled(false);

        dateChooser.getDateEditor().addPropertyChangeListener(evt -> {
            if ("date".equals(evt.getPropertyName())) {
                Date selected = dateChooser.getDate();
                if (selected != null) {
                    Calendar cal = Calendar.getInstance();
                    cal.add(Calendar.YEAR, -16);
                    if (selected.after(new Date()) || selected.after(cal.getTime())) {
                        JOptionPane.showMessageDialog(Signup.this,
                                "Invalid DOB! Minimum age is 16 year.",
                                "DOB Error",
                                JOptionPane.WARNING_MESSAGE);
                        dateChooser.setDate(null);
                    }
                }
            }
        });

        gbc.gridx = 1; gbc.gridy = row++;
        card.add(dateChooser, gbc);

        addLabel(card, gbc, "Email Address :", 0, row); textEmail = addField(card, gbc, 1, row++);

        addLabel(card, gbc, "Marital Status :", 0, row);
        JPanel maritalPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0)); maritalPanel.setOpaque(false);
        M1 = new JRadioButton("Married"); M2 = new JRadioButton("Unmarried"); M3 = new JRadioButton("Others");
        styleRadio(M1); styleRadio(M2); styleRadio(M3);
        ButtonGroup mg = new ButtonGroup(); mg.add(M1); mg.add(M2); mg.add(M3);
        maritalPanel.add(M1); maritalPanel.add(M2); maritalPanel.add(M3);
        gbc.gridx = 1; gbc.gridy = row++;
        card.add(maritalPanel, gbc);

        addLabel(card, gbc, "Address :", 0, row); textAdd = addField(card, gbc, 1, row++);
        addLabel(card, gbc, "City :", 0, row); textCity = addField(card, gbc, 1, row++);
        addLabel(card, gbc, "Postal Code :", 0, row); textPostal = addField(card, gbc, 1, row++);
        addLabel(card, gbc, "Province :", 0, row); textState = addField(card, gbc, 1, row++);

        //  Buttons Panel with Back + Next
        next = new JButton("Next");
        next.setFont(new Font("Segoe UI", Font.BOLD, 14));
        next.setBackground(new Color(0, 102, 204));
        next.setForeground(Color.WHITE);
        next.setFocusPainted(false);
        next.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        next.setBorder(new EmptyBorder(8, 20, 8, 20));
        next.addActionListener(this);

        back = new JButton("Back to Login");
        back.setFont(new Font("Segoe UI", Font.BOLD, 14));
        back.setBackground(new Color(150, 0, 0));
        back.setForeground(Color.WHITE);
        back.setFocusPainted(false);
        back.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        back.setBorder(new EmptyBorder(8, 20, 8, 20));
        back.addActionListener(e -> {
            this.setVisible(false);
            new Login(); //  Replace with your actual Login class
        });

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        btnPanel.setOpaque(false);
        btnPanel.add(back);
        btnPanel.add(next);

        gbc.gridx = 1; gbc.gridy = row; gbc.anchor = GridBagConstraints.LINE_END;
        card.add(btnPanel, gbc);

        mainPanel.add(card);
        setVisible(true);
    }

    private void styleRadio(JRadioButton r) {
        r.setFont(new Font("Segoe UI", Font.PLAIN, 14)); r.setOpaque(false);
    }

    private void addLabel(JPanel panel, GridBagConstraints gbc, String text, int x, int y) {
        JLabel l = new JLabel(text);
        l.setFont(new Font("Segoe UI", Font.BOLD, 16));
        l.setForeground(new Color(0, 51, 102));
        gbc.gridx = x; gbc.gridy = y; panel.add(l, gbc);
    }

    private JTextField addField(JPanel panel, GridBagConstraints gbc, int x, int y) {
        JTextField t = new JTextField();
        t.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        t.setPreferredSize(new Dimension(250, 30));
        t.setBorder(new CompoundBorder(new LineBorder(new Color(200, 220, 255), 1, true),
                new EmptyBorder(5, 8, 5, 8)));
        gbc.gridx = x; gbc.gridy = y; panel.add(t, gbc);
        return t;
    }

    private String generateFormNo() {
        String newFormNo = "0001";
        try {
            Conn c = new Conn();
            ResultSet rs = c.s.executeQuery("SELECT MAX(CAST(formno AS UNSIGNED)) FROM signup");
            if (rs.next()) {
                int max = rs.getInt(1);
                if (max > 0) newFormNo = String.format("%04d", max + 1);
            }
        } catch (Exception e) { e.printStackTrace(); }
        return newFormNo;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        String name = textName.getText().trim();
        String fname = textFname.getText().trim();
        String gender = r1.isSelected() ? "Male" : r2.isSelected() ? "Female" : r3.isSelected() ? "Others" : "";
        String dob = dateChooser.getDate() != null ? new SimpleDateFormat("yyyy-MM-dd").format(dateChooser.getDate()) : "";
        String email = textEmail.getText().trim();
        String marital = M1.isSelected() ? "Married" : M2.isSelected() ? "Unmarried" : M3.isSelected() ? "Others" : "";
        String address = textAdd.getText().trim();
        String city = textCity.getText().trim();
        String postal = textPostal.getText().trim();
        String province = textState.getText().trim();

        if (name.isEmpty() || fname.isEmpty() || gender.isEmpty() || dob.isEmpty() || email.isEmpty()
                || marital.isEmpty() || address.isEmpty() || city.isEmpty() || postal.isEmpty() || province.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please fill all fields.", "Validation Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (!name.matches("^[a-zA-Z\\s]+$") || !fname.matches("^[a-zA-Z\\s]+$")) {
            JOptionPane.showMessageDialog(this, "Name must contain only letters and spaces.", "Validation Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (!email.matches("^[\\w.-]+@[\\w.-]+\\.\\w{2,}$")) {
            JOptionPane.showMessageDialog(this, "Invalid email format.", "Validation Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (!postal.matches("^\\d{4,10}$")) {
            JOptionPane.showMessageDialog(this, "Postal code must be 4-10 digits.", "Validation Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            Conn c = new Conn();
            String query = "INSERT INTO signup (formno, name, father_name, dob, gender, email, marital_status, address, city, postal_code, province) " +
                    "VALUES ('" + formNo + "', '" + name + "', '" + fname + "', '" + dob + "', '" + gender + "', '" +
                    email + "', '" + marital + "', '" + address + "', '" + city + "', '" + postal + "', '" + province + "')";
            c.s.executeUpdate(query);

            JOptionPane.showMessageDialog(this, "Details saved. Proceeding to next page.");
            this.setVisible(false);
            new Signup2(formNo);

        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Database error occurred.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(Signup::new);
    }
}
