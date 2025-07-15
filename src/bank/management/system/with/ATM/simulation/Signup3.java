package bank.management.system.with.ATM.simulation;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.ResultSet;

public class Signup3 extends JFrame implements ActionListener {

    private JCheckBox savings, current, fixedDeposit;
    private JButton submit, showHidePin, back;
    private JPasswordField pinField;
    private JLabel nameLabel, phoneLabel, emailLabel, cardNumberLabel;
    private JCheckBox declarationCheckbox;

    private String formno;
    private String generatedCardNumber;

    public Signup3(String formno) {
        super("APPLICATION FORM - PAGE 3");
        this.formno = formno;

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1300, 770);
        setLocationRelativeTo(null);
        setResizable(false);

        JPanel mainPanel = new JPanel(new GridBagLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;
                g2.setPaint(new GradientPaint(0, 0, new Color(245, 248, 255),
                        0, getHeight(), new Color(222, 230, 255)));
                g2.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        setContentPane(mainPanel);

        JPanel card = new JPanel(new GridBagLayout());
        card.setBackground(new Color(255, 255, 255, 240));
        card.setBorder(new CompoundBorder(
                new LineBorder(new Color(0, 51, 102), 3, true),
                new EmptyBorder(40, 60, 40, 60)
        ));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridx = 0;
        gbc.weightx = 1.0;

        int row = 0;

        // ✅ Logo + Form No
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 30, 0));
        topPanel.setOpaque(false);

        ImageIcon i1 = new ImageIcon(ClassLoader.getSystemResource("icon/bank.png"));
        Image i2 = i1.getImage().getScaledInstance(70, 70, Image.SCALE_SMOOTH);
        JLabel logoLabel = new JLabel(new ImageIcon(i2));
        topPanel.add(logoLabel);

        JLabel formNoLabel = new JLabel("APPLICATION FORM NO: " + formno);
        formNoLabel.setFont(new Font("Segoe UI", Font.BOLD, 28));
        formNoLabel.setForeground(new Color(0, 51, 102));
        topPanel.add(formNoLabel);

        gbc.gridy = row++;
        gbc.anchor = GridBagConstraints.CENTER;
        card.add(topPanel, gbc);

        // ✅ Page Label
        JLabel pageLabel = new JLabel("Page 3 : CREDENTIALS");
        pageLabel.setFont(new Font("Segoe UI", Font.PLAIN, 20));
        pageLabel.setForeground(new Color(0, 51, 102));
        gbc.gridy = row++;
        gbc.anchor = GridBagConstraints.EAST;
        card.add(pageLabel, gbc);

        // ✅ Personal Details
        JPanel detailsPanel = new JPanel(new GridBagLayout());
        detailsPanel.setBackground(Color.WHITE);
        detailsPanel.setBorder(new CompoundBorder(
                new LineBorder(new Color(0, 51, 102), 2, true),
                new EmptyBorder(20, 30, 20, 30)
        ));

        GridBagConstraints dgbc = new GridBagConstraints();
        dgbc.insets = new Insets(5, 5, 5, 5);
        dgbc.anchor = GridBagConstraints.WEST;

        int drow = 0;

        detailsPanelAdd(detailsPanel, dgbc, drow++, "Name:", nameLabel = createValueLabel());
        detailsPanelAdd(detailsPanel, dgbc, drow++, "Phone:", phoneLabel = createValueLabel());
        detailsPanelAdd(detailsPanel, dgbc, drow++, "Email:", emailLabel = createValueLabel());
        detailsPanelAdd(detailsPanel, dgbc, drow++, "Card Number:", cardNumberLabel = createValueLabel());

        gbc.gridy = row++;
        card.add(detailsPanel, gbc);

        // ✅ Account Type
        JLabel accountLabel = createLabel("Account Type:");
        gbc.gridy = row++;
        gbc.anchor = GridBagConstraints.WEST;
        card.add(accountLabel, gbc);

        JPanel accountPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 0));
        accountPanel.setBackground(Color.WHITE);

        savings = createStyledCheckbox("Savings");
        current = createStyledCheckbox("Current");
        fixedDeposit = createStyledCheckbox("Fixed Deposit");

        ItemListener il = e -> {
            JCheckBox source = (JCheckBox) e.getSource();
            if (source.isSelected()) {
                if (source != savings) savings.setSelected(false);
                if (source != current) current.setSelected(false);
                if (source != fixedDeposit) fixedDeposit.setSelected(false);
            }
        };
        savings.addItemListener(il);
        current.addItemListener(il);
        fixedDeposit.addItemListener(il);

        accountPanel.add(savings);
        accountPanel.add(current);
        accountPanel.add(fixedDeposit);

        gbc.gridy = row++;
        gbc.anchor = GridBagConstraints.CENTER;
        card.add(accountPanel, gbc);

        // ✅ PIN Label
        JLabel pinLabel = createLabel("Set PIN (4-digits):");
        gbc.gridy = row++;
        gbc.anchor = GridBagConstraints.WEST;
        card.add(pinLabel, gbc);

        // ✅ PIN Panel
        JPanel pinPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 1));
        pinPanel.setBackground(Color.WHITE);

        pinField = new JPasswordField(4);
        pinField.setFont(new Font("Segoe UI", Font.PLAIN, 25));
        pinField.setPreferredSize(new Dimension(160, 40));
        pinPanel.add(pinField);

        showHidePin = new JButton("Show");
        styleButton(showHidePin);
        showHidePin.addActionListener(e -> togglePinVisibility());
        pinPanel.add(showHidePin);

        gbc.gridy = row++;
        card.add(pinPanel, gbc);

        // ✅ Declaration
        declarationCheckbox = new JCheckBox("I declare that all details are correct & accept bank terms.");
        declarationCheckbox.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        declarationCheckbox.setBackground(Color.WHITE);
        declarationCheckbox.setFocusPainted(false);

        gbc.gridy = row++;
        gbc.anchor = GridBagConstraints.CENTER;
        card.add(declarationCheckbox, gbc);

        // ✅ Buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 20, 0));
        buttonPanel.setOpaque(false);

        back = new JButton("Previous");
        styleButton(back);
        back.addActionListener(ev -> {
            setVisible(false);
            new Signup2(formno);
        });
        buttonPanel.add(back);

        submit = new JButton("Submit");
        styleButton(submit);
        submit.addActionListener(this);
        buttonPanel.add(submit);

        gbc.gridy = row++;
        card.add(buttonPanel, gbc);

        mainPanel.add(card);

        loadCustomerData();
        generateOrFetchCardNumber();

        setVisible(true);
    }

    private void detailsPanelAdd(JPanel p, GridBagConstraints gbc, int row, String label, JLabel value) {
        gbc.gridx = 0; gbc.gridy = row;
        p.add(createLabel(label), gbc);
        gbc.gridx = 1;
        p.add(value, gbc);
    }

    private JLabel createValueLabel() {
        JLabel label = new JLabel(" ");
        label.setFont(new Font("Segoe UI", Font.PLAIN, 18));
        label.setForeground(Color.BLACK);
        return label;
    }

    private JLabel createLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Segoe UI", Font.BOLD, 18));
        label.setForeground(new Color(0, 51, 102));
        return label;
    }

    private JCheckBox createStyledCheckbox(String text) {
        JCheckBox cb = new JCheckBox(text);
        cb.setFont(new Font("Segoe UI", Font.PLAIN, 18));
        cb.setBackground(Color.WHITE);
        cb.setFocusPainted(false);
        return cb;
    }

    private void togglePinVisibility() {
        if (pinField.getEchoChar() == '\u0000') {
            pinField.setEchoChar('*');
            showHidePin.setText("Show");
        } else {
            pinField.setEchoChar('\u0000');
            showHidePin.setText("Hide");
        }
    }

    private void styleButton(JButton btn) {
        btn.setFont(new Font("Segoe UI", Font.BOLD, 16));
        btn.setBackground(new Color(0, 102, 204));
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setBorder(new EmptyBorder(8, 25, 8, 25));
    }

    private void loadCustomerData() {
        try {
            Conn c = new Conn();
            String query = "SELECT name, phone, email FROM signup WHERE formno = '" + formno + "'";
            ResultSet rs = c.s.executeQuery(query);
            if (rs.next()) {
                nameLabel.setText(rs.getString("name"));
                phoneLabel.setText(rs.getString("phone"));
                emailLabel.setText(rs.getString("email"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void generateOrFetchCardNumber() {
        try {
            Conn c = new Conn();
            String checkQuery = "SELECT card_number FROM signup WHERE formno = '" + formno + "'";
            ResultSet rs = c.s.executeQuery(checkQuery);
            if (rs.next() && rs.getString("card_number") != null) {
                generatedCardNumber = rs.getString("card_number");
            } else {
                String maxQuery = "SELECT MAX(card_number) AS max_card FROM signup";
                ResultSet maxRs = c.s.executeQuery(maxQuery);
                if (maxRs.next() && maxRs.getString("max_card") != null) {
                    long maxCard = Long.parseLong(maxRs.getString("max_card"));
                    generatedCardNumber = String.valueOf(maxCard + 1);
                } else {
                    generatedCardNumber = "1111111111111111";
                }
                c.s.executeUpdate("UPDATE signup SET card_number = '" + generatedCardNumber + "' WHERE formno = '" + formno + "'");
            }
            cardNumberLabel.setText(generatedCardNumber);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void actionPerformed(ActionEvent ae) {
        String accountType = null;
        if (savings.isSelected()) accountType = "Savings";
        else if (current.isSelected()) accountType = "Current";
        else if (fixedDeposit.isSelected()) accountType = "Fixed Deposit";

        String pin = new String(pinField.getPassword()).trim();

        if (accountType == null) {
            JOptionPane.showMessageDialog(this, "✘ Please select an account type.");
        } else if (!pin.matches("\\d{4}")) {
            JOptionPane.showMessageDialog(this, "✘ PIN must be exactly 4 digits.");
        } else if (!declarationCheckbox.isSelected()) {
            JOptionPane.showMessageDialog(this, "✘ Please accept the declaration.");
        } else {
            try {
                Conn c = new Conn();
                String query = "UPDATE signup SET account_type = '" + accountType + "', pin_number = '" + pin + "' WHERE formno = '" + formno + "'";
                c.s.executeUpdate(query);

                JOptionPane.showMessageDialog(this, "✔ Account Created Successfully!");
                setVisible(false);
                new Login();
            } catch (Exception e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(this, "✘ Error occurred while saving data.");
            }
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new Signup3("formno"));
    }
}
