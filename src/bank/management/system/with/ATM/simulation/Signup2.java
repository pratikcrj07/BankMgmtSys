package bank.management.system.with.ATM.simulation;

import org.opencv.core.*;
import org.opencv.core.Point;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.objdetect.CascadeClassifier;
import org.opencv.videoio.VideoCapture;
import org.opencv.imgproc.Imgproc;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.io.File;
import java.sql.Connection;
import java.sql.PreparedStatement;

public class Signup2 extends JFrame implements ActionListener {

    static {
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
    }

    JComboBox<String> religionBox, incomeBox, occupationBox, educationBox;
    JTextField textCitizenship, textPhone;
    JButton next, capture, back;
    JLabel facePreview;

    String formno;
    String faceImagePath = null;

    public Signup2(String formno) {
        super("APPLICATION FORM - PAGE 2");
        this.formno = formno;

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1336, 768);
        setLocationRelativeTo(null);
        setResizable(false);

        JPanel mainPanel = new JPanel(new GridBagLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;
                g2.setPaint(new GradientPaint(0, 0, new Color(222, 255, 228), 0, getHeight(), new Color(200, 235, 210)));
                g2.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        setContentPane(mainPanel);

        JPanel card = new JPanel(new GridBagLayout());
        card.setBackground(new Color(255, 255, 255, 240));
        card.setBorder(new CompoundBorder(new LineBorder(new Color(180, 210, 190), 2, true),
                new EmptyBorder(40, 60, 40, 60)));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.weightx = 1.0;
        gbc.insets = new Insets(15, 15, 15, 15);

        ImageIcon i1 = new ImageIcon(ClassLoader.getSystemResource("icon/bank.png"));
        Image i2 = i1.getImage().getScaledInstance(70, 70, Image.SCALE_SMOOTH);
        JLabel logoLabel = new JLabel(new ImageIcon(i2));
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 1;
        card.add(logoLabel, gbc);

        JLabel formNoLabel = new JLabel("APPLICATION FORM NO: " + formno);
        formNoLabel.setFont(new Font("Segoe UI", Font.BOLD, 28));
        formNoLabel.setForeground(new Color(0, 51, 102));
        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        card.add(formNoLabel, gbc);

        JLabel pageLabel = new JLabel("Page 2");
        pageLabel.setFont(new Font("Segoe UI", Font.PLAIN, 20));
        pageLabel.setForeground(new Color(0, 51, 102));
        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.gridwidth = 1;
        card.add(pageLabel, gbc);

        int row = 2;

        addLabel(card, gbc, "Religion:", 0, row);
        String[] religions = {"-- Select --", "Hindu", "Buddhist", "Christian", "Muslim", "Others"};
        religionBox = new JComboBox<>(religions);
        styleComboBox(religionBox);
        addComboBox(card, religionBox, gbc, 1, row++);

        addLabel(card, gbc, "Income:", 0, row);
        String[] incomes = {"-- Select --", "No Income", "< Rs.1,00,000", "Rs.1,00,000 - Rs.5,00,000", "> Rs.5,00,000"};
        incomeBox = new JComboBox<>(incomes);
        styleComboBox(incomeBox);
        addComboBox(card, incomeBox, gbc, 1, row++);

        addLabel(card, gbc, "Occupation:", 0, row);
        String[] occupations = {"-- Select --", "Student", "Salaried", "Self-Employed", "Retired", "Others"};
        occupationBox = new JComboBox<>(occupations);
        styleComboBox(occupationBox);
        addComboBox(card, occupationBox, gbc, 1, row++);

        addLabel(card, gbc, "Education:", 0, row);
        String[] educations = {"-- Select --", "High School", "Bachelor's", "Master's", "PhD", "Others"};
        educationBox = new JComboBox<>(educations);
        styleComboBox(educationBox);
        addComboBox(card, educationBox, gbc, 1, row++);

        addLabel(card, gbc, "Citizenship No:", 0, row);
        textCitizenship = new JTextField();
        styleTextField(textCitizenship);
        addField(card, textCitizenship, gbc, 1, row++);

        addLabel(card, gbc, "Phone No:", 0, row);
        textPhone = new JTextField();
        styleTextField(textPhone);
        addField(card, textPhone, gbc, 1, row++);

        GridBagConstraints faceGbc = new GridBagConstraints();
        faceGbc.fill = GridBagConstraints.NONE;
        faceGbc.anchor = GridBagConstraints.NORTH;
        faceGbc.insets = new Insets(15, 30, 15, 15);

        facePreview = new JLabel("Preview", SwingConstants.CENTER);
        facePreview.setPreferredSize(new Dimension(320, 240));
        facePreview.setBorder(new LineBorder(new Color(0, 51, 102), 2, true));
        facePreview.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        faceGbc.gridx = 2;
        faceGbc.gridy = 2;
        faceGbc.gridheight = 4;
        card.add(facePreview, faceGbc);

        capture = new JButton("Open Face Capture");
        styleButton(capture);
        capture.addActionListener(e -> openFaceCaptureDialog());
        faceGbc.gridy = 6;
        card.add(capture, faceGbc);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 0));
        buttonPanel.setOpaque(false);

        back = new JButton("Previous");
        styleButton(back);
        back.addActionListener(ev -> {
            setVisible(false);
            new Signup();
        });
        buttonPanel.add(back);

        next = new JButton("Submit");
        styleButton(next);
        next.addActionListener(this);
        buttonPanel.add(next);

        gbc.gridx = 1;
        gbc.gridy = row;
        gbc.anchor = GridBagConstraints.LINE_END;
        card.add(buttonPanel, gbc);

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.CENTER;
        mainPanel.add(card, gbc);

        setVisible(true);
    }

    private void openFaceCaptureDialog() {
        JDialog dialog = new JDialog(this, "Face Capture", true);
        dialog.setSize(640, 480);
        dialog.setLocationRelativeTo(this);

        JLabel videoLabel = new JLabel();
        dialog.add(videoLabel);

        dialog.addKeyListener(new KeyAdapter() {
            volatile boolean capturing = true;

            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_SPACE || e.getKeyCode() == KeyEvent.VK_ENTER) {
                    capturing = false;
                }
            }

            {
                new Thread(() -> {
                    CascadeClassifier faceDetector = new CascadeClassifier("C:\\Users\\pratik_icy\\IdeaProjects\\OcvTest\\src\\resource\\haarcascade_frontalface_default.xml");
                    VideoCapture camera = new VideoCapture(0);

                    if (!camera.isOpened()) {
                        JOptionPane.showMessageDialog(dialog, " Cannot open webcam.");
                        dialog.dispose();
                        return;
                    }

                    Mat frame = new Mat();
                    Rect[] lastFaces = new Rect[0];

                    while (capturing) {
                        if (!camera.read(frame) || frame.empty()) continue;

                        MatOfRect faces = new MatOfRect();
                        faceDetector.detectMultiScale(frame, faces);

                        for (Rect rect : faces.toArray()) {
                            Imgproc.rectangle(frame, new Point(rect.x, rect.y),
                                    new Point(rect.x + rect.width, rect.y + rect.height),
                                    new Scalar(0, 255, 0), 3);
                        }

                        lastFaces = faces.toArray();

                        BufferedImage img = matToBufferedImage(frame);
                        videoLabel.setIcon(new ImageIcon(img));

                        try {
                            Thread.sleep(30);
                        } catch (InterruptedException ex) {
                            ex.printStackTrace();
                        }
                    }

                    if (lastFaces.length > 0) {
                        Rect rect = lastFaces[0];
                        Mat face = new Mat(frame, rect);

                        File dir = new File("images");
                        if (!dir.exists()) dir.mkdirs();

                        faceImagePath = "C:\\Users\\pratik_icy\\IdeaProjects\\Bank Mgmt System with ATM simulation\\images/face_" + formno + ".png";
                        Imgcodecs.imwrite(faceImagePath, face);

                        facePreview.setIcon(new ImageIcon(matToBufferedImage(face)));
                        facePreview.setText("");

                        JOptionPane.showMessageDialog(dialog, " Face captured and saved!");
                    } else {
                        JOptionPane.showMessageDialog(dialog, " No face captured.");
                    }

                    camera.release();
                    dialog.dispose();
                }).start();
            }
        });

        dialog.setFocusable(true);
        dialog.setVisible(true);
    }

    public static BufferedImage matToBufferedImage(Mat mat) {
        int type = BufferedImage.TYPE_BYTE_GRAY;
        if (mat.channels() > 1) {
            type = BufferedImage.TYPE_3BYTE_BGR;
        }
        int bufferSize = mat.channels() * mat.cols() * mat.rows();
        byte[] b = new byte[bufferSize];
        mat.get(0, 0, b);
        BufferedImage image = new BufferedImage(mat.cols(), mat.rows(), type);
        final byte[] targetPixels = ((DataBufferByte) image.getRaster().getDataBuffer()).getData();
        System.arraycopy(b, 0, targetPixels, 0, b.length);
        return image;
    }

    private void styleButton(JButton btn) {
        btn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btn.setBackground(new Color(0, 102, 204));
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setBorder(new EmptyBorder(8, 20, 8, 20));
    }

    private void styleComboBox(JComboBox<String> box) {
        box.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        box.setPreferredSize(new Dimension(250, 30));
    }

    private void styleTextField(JTextField field) {
        field.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        field.setPreferredSize(new Dimension(250, 30));
        field.setBorder(new CompoundBorder(new LineBorder(new Color(200, 220, 255), 1, true),
                new EmptyBorder(5, 8, 5, 8)));
    }

    private void addLabel(JPanel panel, GridBagConstraints gbc, String text, int x, int y) {
        JLabel l = new JLabel(text);
        l.setFont(new Font("Segoe UI", Font.BOLD, 16));
        l.setForeground(new Color(0, 51, 102));
        gbc.gridx = x;
        gbc.gridy = y;
        panel.add(l, gbc);
    }

    private void addComboBox(JPanel panel, JComboBox<String> comboBox, GridBagConstraints gbc, int x, int y) {
        gbc.gridx = x;
        gbc.gridy = y;
        panel.add(comboBox, gbc);
    }

    private void addField(JPanel panel, JTextField field, GridBagConstraints gbc, int x, int y) {
        gbc.gridx = x;
        gbc.gridy = y;
        panel.add(field, gbc);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        String religion = (String) religionBox.getSelectedItem();
        String income = (String) incomeBox.getSelectedItem();
        String occupation = (String) occupationBox.getSelectedItem();
        String education = (String) educationBox.getSelectedItem();
        String citizenship = textCitizenship.getText().trim();
        String phone = textPhone.getText().trim();

        if (religion.equals("-- Select --") || income.equals("-- Select --") ||
                occupation.equals("-- Select --") || education.equals("-- Select --") ||
                citizenship.isEmpty() || phone.isEmpty() || faceImagePath == null) {
            JOptionPane.showMessageDialog(this, "✘ All fields + face must be filled.");
            return;
        }

        try {
            Conn c = new Conn();
            String sql = "UPDATE signup SET religion=?, income=?, occupation=?, education=?, citizenship=?, phone=?, face_image_path=? WHERE formno=?";
            PreparedStatement ps = c.c.prepareStatement(sql);
            ps.setString(1, religion);
            ps.setString(2, income);
            ps.setString(3, occupation);
            ps.setString(4, education);
            ps.setString(5, citizenship);
            ps.setString(6, phone);
            ps.setString(7, faceImagePath);
            ps.setString(8, formno);

            ps.executeUpdate();
            ps.close();

            JOptionPane.showMessageDialog(this, "✔ Data + Face Path saved.");
            setVisible(false);
            new Signup3(formno);

        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "✘ DB error: " + ex.getMessage());
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new Signup2("formno"));
    }
}
