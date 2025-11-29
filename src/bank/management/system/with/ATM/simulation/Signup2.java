package bank.management.system.with.ATM.simulation;

import com.formdev.flatlaf.FlatLightLaf;
import org.bytedeco.opencv.global.opencv_core;
import org.bytedeco.opencv.global.opencv_imgcodecs;
import org.bytedeco.opencv.global.opencv_imgproc;
import org.bytedeco.opencv.opencv_core.*;
import org.bytedeco.opencv.opencv_face.LBPHFaceRecognizer;
import org.bytedeco.opencv.opencv_objdetect.CascadeClassifier;
import org.bytedeco.opencv.opencv_videoio.VideoCapture;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.io.File;
import java.nio.IntBuffer;
import java.sql.*;
import java.util.ArrayList;

public class Signup2 extends JFrame implements ActionListener {

    JComboBox<String> religionBox, incomeBox, occupationBox, educationBox;
    JTextField textCitizenship, textPhone;
    JButton btnOpenCamera, btnCaptureFace, next, back;
    JLabel facePreview, captureStatusLabel;
    JProgressBar progressBar;

    String formno;
    String firstCapturedFacePath = null;
    String faceModelPath = null;

    VideoCapture camera;
    volatile boolean cameraActive = false;
    volatile boolean autoCapturing = false;
    Thread cameraThread, autoCaptureThread;

    int capturedCount = 0;
    final int maxCaptures = 50;

    final String CASCADE_PATH = "src/haarcascade_frontalface_default.xml";

    Connection conn;

    private static final String DB_URL = "jdbc:mysql://localhost:3306/bank_system";
    private static final String DB_USER = "root";  // Change accordingly
    private static final String DB_PASSWORD = "root";  // Change accordingly

    public Signup2(String formno) {
        super("APPLICATION FORM - PAGE 2");
        this.formno = formno;

        setupDatabase();

        try {
            UIManager.setLookAndFeel(new FlatLightLaf());
        } catch (Exception e) {
            e.printStackTrace();
        }

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setLocationRelativeTo(null);
        setResizable(true);

        JPanel container = new JPanel(new GridBagLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                g.setColor(new Color(220, 235, 255));
                g.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        container.setBorder(BorderFactory.createEmptyBorder(40, 40, 40, 40));
        setContentPane(container);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(12, 12, 12, 12);
        gbc.fill = GridBagConstraints.BOTH;

        JPanel card = new JPanel(new GridBagLayout());
        card.setBackground(new Color(255, 255, 255, 245));
        card.setBorder(new CompoundBorder(new LineBorder(new Color(150, 180, 210), 2, true), new EmptyBorder(25, 30, 25, 30)));

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        container.add(card, gbc);

        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(10, 10, 10, 10);
        c.fill = GridBagConstraints.HORIZONTAL;

        JLabel header = new JLabel("APPLICATION FORM NO: " + formno);
        header.setFont(new Font("Segoe UI", Font.BOLD, 26));
        header.setForeground(new Color(0, 51, 102));
        c.gridx = 0;
        c.gridy = 0;
        c.gridwidth = 2;
        card.add(header, c);

        JLabel pageLabel = new JLabel("Page 2: Personal Details & Face Capture");
        pageLabel.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        pageLabel.setForeground(new Color(0, 51, 102));
        c.gridy = 1;
        card.add(pageLabel, c);

        c.gridwidth = 1;
        c.gridy = 2;

        // Input Panel - Left Side
        JPanel inputPanel = new JPanel(new GridBagLayout());
        inputPanel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(0, 102, 204), 1, true),
                "Personal Details", TitledBorder.LEADING, TitledBorder.TOP,
                new Font("Segoe UI", Font.BOLD, 16), new Color(0, 51, 102)));
        inputPanel.setBackground(Color.white);

        GridBagConstraints ic = new GridBagConstraints();
        ic.insets = new Insets(12, 12, 12, 12);
        ic.fill = GridBagConstraints.HORIZONTAL;
        ic.weightx = 1.0;

        addLabel(inputPanel, ic, "Religion:", 0, 0);
        religionBox = new JComboBox<>(new String[]{"-- Select --", "Hindu", "Buddhist", "Christian", "Muslim", "Others"});
        addComboBox(inputPanel, religionBox, ic, 1, 0);

        addLabel(inputPanel, ic, "Income:", 0, 1);
        incomeBox = new JComboBox<>(new String[]{"-- Select --", "No Income", "< Rs.1,00,000", "Rs.1,00,000 - Rs.5,00,000", "> Rs.5,00,000"});
        addComboBox(inputPanel, incomeBox, ic, 1, 1);

        addLabel(inputPanel, ic, "Occupation:", 0, 2);
        occupationBox = new JComboBox<>(new String[]{"-- Select --", "Student", "Salaried", "Self-Employed", "Retired", "Others"});
        addComboBox(inputPanel, occupationBox, ic, 1, 2);

        addLabel(inputPanel, ic, "Education:", 0, 3);
        educationBox = new JComboBox<>(new String[]{"-- Select --", "High School", "Bachelor's", "Master's", "PhD", "Others"});
        addComboBox(inputPanel, educationBox, ic, 1, 3);

        addLabel(inputPanel, ic, "Citizenship No:", 0, 4);
        textCitizenship = new JTextField();
        addField(inputPanel, textCitizenship, ic, 1, 4);

        addLabel(inputPanel, ic, "Phone No:", 0, 5);
        textPhone = new JTextField();
        addField(inputPanel, textPhone, ic, 1, 5);

        JPanel btnNavPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 0));
        btnNavPanel.setBackground(Color.white);

        back = new JButton("Previous");
        next = new JButton("Submit");
        next.setEnabled(false);

        Dimension navBtnSize = new Dimension(140, 42);
        back.setPreferredSize(navBtnSize);
        next.setPreferredSize(navBtnSize);

        Color btnBlue = new Color(0, 102, 204);
        back.setBackground(btnBlue);
        back.setForeground(Color.white);
        back.setFont(new Font("Segoe UI", Font.BOLD, 16));
        back.setFocusPainted(false);

        next.setBackground(btnBlue);
        next.setForeground(Color.white);
        next.setFont(new Font("Segoe UI", Font.BOLD, 16));
        next.setFocusPainted(false);

        btnNavPanel.add(back);
        btnNavPanel.add(next);

        ic.gridy = 6;
        ic.gridx = 0;
        ic.gridwidth = 2;
        ic.anchor = GridBagConstraints.WEST;
        ic.fill = GridBagConstraints.NONE;
        inputPanel.add(btnNavPanel, ic);

        c.gridx = 0;
        c.gridy = 2;
        c.weightx = 0.55;
        c.weighty = 1.0;
        c.fill = GridBagConstraints.VERTICAL;
        card.add(inputPanel, c);

        // Camera Panel - Right Side
        JPanel cameraPanel = new JPanel(new GridBagLayout());
        cameraPanel.setBackground(Color.white);
        cameraPanel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(0, 102, 204), 1, true),
                "Face Capture", TitledBorder.LEADING, TitledBorder.TOP,
                new Font("Segoe UI", Font.BOLD, 16), new Color(0, 51, 102)));

        GridBagConstraints cc = new GridBagConstraints();
        cc.insets = new Insets(10, 10, 10, 10);
        cc.gridx = 0;
        cc.gridy = 0;
        cc.gridwidth = 2;

        facePreview = new JLabel("Face Preview", SwingConstants.CENTER);
        facePreview.setPreferredSize(new Dimension(280, 280));
        facePreview.setBorder(new LineBorder(new Color(0, 102, 204), 3, true));
        facePreview.setOpaque(true);
        facePreview.setBackground(new Color(230, 240, 255));
        facePreview.setFont(new Font("Segoe UI", Font.ITALIC, 18));
        cameraPanel.add(facePreview, cc);

        cc.gridy = 1;
        cc.gridwidth = 1;

        btnOpenCamera = new JButton("Open Camera");
        btnCaptureFace = new JButton("Capture Face");
        btnCaptureFace.setEnabled(false);

        Dimension btnSize = new Dimension(130, 38);
        btnOpenCamera.setPreferredSize(btnSize);
        btnCaptureFace.setPreferredSize(btnSize);

        btnOpenCamera.setBackground(btnBlue);
        btnOpenCamera.setForeground(Color.white);
        btnOpenCamera.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnOpenCamera.setFocusPainted(false);

        btnCaptureFace.setBackground(btnBlue);
        btnCaptureFace.setForeground(Color.white);
        btnCaptureFace.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnCaptureFace.setFocusPainted(false);

        cameraPanel.add(btnOpenCamera, cc);
        cc.gridx = 1;
        cameraPanel.add(btnCaptureFace, cc);

        cc.gridx = 0;
        cc.gridy = 2;
        cc.gridwidth = 2;

        captureStatusLabel = new JLabel("Camera not started", SwingConstants.CENTER);
        captureStatusLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        captureStatusLabel.setForeground(new Color(0, 51, 102));
        cameraPanel.add(captureStatusLabel, cc);

        cc.gridy = 3;
        progressBar = new JProgressBar(0, maxCaptures);
        progressBar.setValue(0);
        progressBar.setStringPainted(true);
        cameraPanel.add(progressBar, cc);

        c.gridx = 1;
        c.gridy = 2;
        c.weightx = 0.45;
        c.fill = GridBagConstraints.NONE;
        card.add(cameraPanel, c);

        // Action Listeners
        btnOpenCamera.addActionListener(e -> toggleCamera());
        btnCaptureFace.addActionListener(e -> manualCapture());
        next.addActionListener(this);
        back.addActionListener(e -> {
            closeCameraIfOpen();
            this.setVisible(false);
            new Signup();  // Assuming Signup1 is your previous form
        });

        setVisible(true);
    }

    private void setupDatabase() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);

            Statement stmt = conn.createStatement();
            String createTableSQL = "CREATE TABLE IF NOT EXISTS signup (" +
                    "formno VARCHAR(20) PRIMARY KEY," +
                    "religion VARCHAR(50)," +
                    "income VARCHAR(50)," +
                    "occupation VARCHAR(50)," +
                    "education VARCHAR(50)," +
                    "citizenship VARCHAR(50)," +
                    "phone VARCHAR(20)," +
                    "face_image_path VARCHAR(255)," +
                    "face_model_path VARCHAR(255)" +
                    ")";
            stmt.execute(createTableSQL);
            stmt.close();

        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this,
                    "Database connection/setup failed: " + e.getMessage(),
                    "DB Error", JOptionPane.ERROR_MESSAGE);
            System.exit(1);
        }
    }

    private void toggleCamera() {
        if (cameraActive) {
            stopAutoCapture();
            closeCameraIfOpen();
            btnOpenCamera.setText("Open Camera");
            btnCaptureFace.setEnabled(false);
            captureStatusLabel.setText("Camera closed");
            facePreview.setIcon(null);
            facePreview.setText("Face Preview");
            progressBar.setValue(capturedCount);
            return;
        }
        camera = new VideoCapture(0);
        if (!camera.isOpened()) {
            JOptionPane.showMessageDialog(this, "Cannot open camera. Make sure no other application is using it.");
            return;
        }
        cameraActive = true;
        btnOpenCamera.setText("Close Camera");
        btnCaptureFace.setEnabled(true);
        captureStatusLabel.setText("Camera opened - Ready to capture");
        cameraThread = new Thread(this::runCamera);
        cameraThread.start();
    }

    private void runCamera() {
        CascadeClassifier detector = new CascadeClassifier(CASCADE_PATH);
        if (detector.empty()) {
            JOptionPane.showMessageDialog(this, "Failed to load face detector model.");
            cameraActive = false;
            return;
        }

        Mat frame = new Mat();
        while (cameraActive) {
            if (camera.read(frame) && !frame.empty()) {
                Mat displayFrame = frame.clone();
                Mat gray = new Mat();
                opencv_imgproc.cvtColor(displayFrame, gray, opencv_imgproc.COLOR_BGR2GRAY);
                RectVector faces = new RectVector();
                detector.detectMultiScale(gray, faces);

                // Draw green rectangle around each face
                for (int i = 0; i < faces.size(); i++) {
                    Rect r = faces.get(i);
                    opencv_imgproc.rectangle(displayFrame, r, new Scalar(0, 255, 0, 1), 2, opencv_imgproc.LINE_8, 0);
                }

                ImageIcon image = new ImageIcon(matToBufferedImage(displayFrame));
                SwingUtilities.invokeLater(() -> {
                    facePreview.setIcon(image);
                    facePreview.setText(null);
                });

                gray.release();
                displayFrame.release();
            }
            try {
                Thread.sleep(33); // ~30 FPS
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
        detector.close();
    }

    private void manualCapture() {
        if (!cameraActive || camera == null || !camera.isOpened()) {
            JOptionPane.showMessageDialog(this, "Camera is not open.");
            return;
        }
        if (autoCapturing) {
            JOptionPane.showMessageDialog(this, "Auto capturing is in progress. Please wait.");
            return;
        }

        Mat frame = new Mat();
        if (!camera.read(frame) || frame.empty()) {
            JOptionPane.showMessageDialog(this, "Failed to capture frame from camera.");
            return;
        }

        // Detect face (color frame this time)
        Mat colorFrame = frame.clone();
        Mat gray = new Mat();
        opencv_imgproc.cvtColor(frame, gray, opencv_imgproc.COLOR_BGR2GRAY);
        CascadeClassifier detector = new CascadeClassifier(CASCADE_PATH);
        if (detector.empty()) {
            JOptionPane.showMessageDialog(this, "Error: Face detection model not loaded. Cannot capture face.");
            return;
        }

        RectVector faces = new RectVector();
        detector.detectMultiScale(gray, faces);
        detector.close();

        if (faces.size() == 0) {
            JOptionPane.showMessageDialog(this, "No face detected in the frame. Please try again, ensuring your face is clearly visible.");
            gray.release();
            colorFrame.release();
            return;
        }

        if (capturedCount > 0) {
            JOptionPane.showMessageDialog(this, "First face already captured. Starting auto-capture for more images.");
            gray.release();
            colorFrame.release();
            startAutoCapture();
            return;
        }

        // First face: Save color crop (no grayscale conversion)
        Rect faceRect = faces.get(0);
        Mat faceColor = new Mat(colorFrame, faceRect);
        opencv_imgproc.resize(faceColor, faceColor, new Size(160, 160));

        String baseDir = "training_img" + File.separator + formno;
        File dir = new File(baseDir);
        if (!dir.exists()) {
            dir.mkdirs();
        }

        capturedCount++;
        String imgPath = baseDir + File.separator + capturedCount + ".png";
        if (opencv_imgcodecs.imwrite(imgPath, faceColor)) {
            firstCapturedFacePath = imgPath;
            progressBar.setValue(capturedCount);
            captureStatusLabel.setText("Captured 1 / " + maxCaptures + " (First image saved in color)");

            BufferedImage displayImg = matToBufferedImage(faceColor);
            if (displayImg != null) {
                int panelWidth = facePreview.getWidth();
                int panelHeight = facePreview.getHeight();
                Image scaledImage = displayImg.getScaledInstance(panelWidth, panelHeight, Image.SCALE_SMOOTH);
                facePreview.setIcon(new ImageIcon(scaledImage));
                facePreview.setText(null);
            }
            next.setEnabled(true);
            startAutoCapture();
        } else {
            capturedCount--;
            JOptionPane.showMessageDialog(this, "Failed to save captured face image.");
        }

        gray.release();
        colorFrame.release();
        faceColor.release();
    }

    private void startAutoCapture() {
        if (autoCapturing) return;
        autoCapturing = true;
        autoCaptureThread = new Thread(() -> {
            CascadeClassifier detector = new CascadeClassifier(CASCADE_PATH);
            if (detector.empty()) {
                JOptionPane.showMessageDialog(this, "Failed to load face detector model for auto capture.");
                autoCapturing = false;
                return;
            }

            try {
                while (capturedCount < maxCaptures && cameraActive) {
                    Mat frame = new Mat();
                    if (camera.read(frame) && !frame.empty()) {
                        Mat gray = new Mat();
                        opencv_imgproc.cvtColor(frame, gray, opencv_imgproc.COLOR_BGR2GRAY);
                        RectVector faces = new RectVector();
                        detector.detectMultiScale(gray, faces);

                        if (faces.size() > 0) {
                            Rect faceRect = faces.get(0);
                            Mat face = new Mat(gray, faceRect);
                            opencv_imgproc.resize(face, face, new Size(160, 160));

                            String baseDir = "training_img" + File.separator + formno;
                            File dir = new File(baseDir);
                            if (!dir.exists()) {
                                dir.mkdirs();
                            }

                            capturedCount++;
                            String imgPath = baseDir + File.separator + capturedCount + ".png";
                            opencv_imgcodecs.imwrite(imgPath, face);

                            SwingUtilities.invokeLater(() -> {
                                progressBar.setValue(capturedCount);
                                captureStatusLabel.setText("Captured " + capturedCount + " / " + maxCaptures);
                            });

                            face.release();
                        }
                        gray.release();
                        frame.release();

                        if (capturedCount == maxCaptures) {
                            SwingUtilities.invokeLater(() -> JOptionPane.showMessageDialog(this, "Auto capture completed."));
                            break;
                        }
                    }
                    Thread.sleep(200); // Capture every 200 ms approx
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            } finally {
                autoCapturing = false;
                detector.close();
            }
        });
        autoCaptureThread.start();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (!validateForm()) {
            return;
        }
        stopAutoCapture();

        try {
            // TRAIN MODEL FIRST
            trainAndSaveModel();

            // Then save/update database with trained model path
            String sql = "INSERT INTO signup (formno, religion, income, occupation, education, citizenship, phone, face_image_path, face_model_path) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?) " +
                    "ON DUPLICATE KEY UPDATE " +
                    "religion=?, income=?, occupation=?, education=?, citizenship=?, phone=?, face_image_path=?, face_model_path=?";

            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, formno);
            ps.setString(2, (String) religionBox.getSelectedItem());
            ps.setString(3, (String) incomeBox.getSelectedItem());
            ps.setString(4, (String) occupationBox.getSelectedItem());
            ps.setString(5, (String) educationBox.getSelectedItem());
            ps.setString(6, textCitizenship.getText().trim());
            ps.setString(7, textPhone.getText().trim());
            ps.setString(8, firstCapturedFacePath);
            ps.setString(9, faceModelPath);

            ps.setString(10, (String) religionBox.getSelectedItem());
            ps.setString(11, (String) incomeBox.getSelectedItem());
            ps.setString(12, (String) occupationBox.getSelectedItem());
            ps.setString(13, (String) educationBox.getSelectedItem());
            ps.setString(14, textCitizenship.getText().trim());
            ps.setString(15, textPhone.getText().trim());
            ps.setString(16, firstCapturedFacePath);
            ps.setString(17, faceModelPath);

            ps.executeUpdate();
            ps.close();

            JOptionPane.showMessageDialog(this, "Personal details saved successfully to MySQL!\nFace recognition model trained and saved successfully!\nProceeding to next page.");

            next.setEnabled(false);
            closeCameraIfOpen();
            setVisible(false);
            new Signup3(formno);

        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Database error saving data: " + ex.getMessage());
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error during data saving or model training: " + ex.getMessage());
        }
    }

    private void trainAndSaveModel() throws Exception {
        String baseDir = "training_img" + File.separator + formno;
        File dir = new File(baseDir);
        File[] imgFiles = dir.listFiles((d, name) -> name.toLowerCase().endsWith(".png"));

        if (imgFiles == null || imgFiles.length == 0) {
            throw new Exception("No training images found in " + baseDir + "!");
        }

        ArrayList<Mat> images = new ArrayList<>();
        ArrayList<Integer> labels = new ArrayList<>();

        int label;
        try {
            label = Integer.parseInt(formno);
        } catch (NumberFormatException e) {
            throw new Exception("Form number '" + formno + "' is not a valid integer for face recognition training.", e);
        }

        for (File imgFile : imgFiles) {
            Mat img = opencv_imgcodecs.imread(imgFile.getAbsolutePath(), opencv_imgcodecs.IMREAD_GRAYSCALE);
            if (!img.empty()) {
                images.add(img);
                labels.add(label);
            } else {
                System.err.println("Warning: Could not read image file: " + imgFile.getAbsolutePath());
            }
        }

        if (images.size() == 0) {
            throw new Exception("No valid training images loaded after reading from disk!");
        }

        LBPHFaceRecognizer recognizer = LBPHFaceRecognizer.create();
        MatVector imagesVec = new MatVector(images.size());
        Mat labelsMat = new Mat(images.size(), 1, opencv_core.CV_32SC1);
        IntBuffer labelsBuf = labelsMat.createBuffer();

        for (int i = 0; i < images.size(); i++) {
            imagesVec.put(i, images.get(i));
            labelsBuf.put(i, labels.get(i));
        }

        recognizer.train(imagesVec, labelsMat);

        String modelDir = "trained_models";
        File mdir = new File(modelDir);
        if (!mdir.exists()) {
            mdir.mkdirs();
        }

        faceModelPath = modelDir + File.separator + formno + ".yml";
        recognizer.save(faceModelPath);

        // Cleanup
        imagesVec.close();
        labelsMat.close();
        for (Mat img : images) {
            img.close();
        }
        recognizer.close();
    }

    private boolean validateForm() {
        if (religionBox.getSelectedIndex() == 0) {
            JOptionPane.showMessageDialog(this, "Please select your Religion.", "Validation Error", JOptionPane.WARNING_MESSAGE);
            return false;
        }
        if (incomeBox.getSelectedIndex() == 0) {
            JOptionPane.showMessageDialog(this, "Please select your Income Category.", "Validation Error", JOptionPane.WARNING_MESSAGE);
            return false;
        }
        if (occupationBox.getSelectedIndex() == 0) {
            JOptionPane.showMessageDialog(this, "Please select your Occupation.", "Validation Error", JOptionPane.WARNING_MESSAGE);
            return false;
        }
        if (educationBox.getSelectedIndex() == 0) {
            JOptionPane.showMessageDialog(this, "Please select your Education Level.", "Validation Error", JOptionPane.WARNING_MESSAGE);
            return false;
        }
        if (textCitizenship.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter your Citizenship No.", "Validation Error", JOptionPane.WARNING_MESSAGE);
            return false;
        }
        if (!textCitizenship.getText().trim().matches("\\d{10}")) {
            JOptionPane.showMessageDialog(this, "Citizenship No. must be exactly 10 digits.", "Validation Error", JOptionPane.WARNING_MESSAGE);
            return false;
        }
        if (textPhone.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter your Phone No.", "Validation Error", JOptionPane.WARNING_MESSAGE);
            return false;
        }
        if (!textPhone.getText().trim().matches("\\d{10}")) {
            JOptionPane.showMessageDialog(this, "Phone No. must be exactly 10 digits.", "Validation Error", JOptionPane.WARNING_MESSAGE);
            return false;
        }

        if (firstCapturedFacePath == null || !new File(firstCapturedFacePath).exists()) {
            JOptionPane.showMessageDialog(this, "Please capture at least one face image for verification.", "Validation Error", JOptionPane.WARNING_MESSAGE);
            return false;
        }
        return true;
    }

    private BufferedImage matToBufferedImage(Mat mat) {
        if (mat == null || mat.empty()) {
            return null;
        }
        int type = BufferedImage.TYPE_BYTE_GRAY;
        if (mat.channels() > 1) {
            type = BufferedImage.TYPE_3BYTE_BGR;
        }
        int bufferSize = (int) (mat.total() * mat.channels());
        byte[] b = new byte[bufferSize];
        mat.data().get(b);

        BufferedImage image = new BufferedImage(mat.cols(), mat.rows(), type);
        final byte[] targetPixels = ((DataBufferByte) image.getRaster().getDataBuffer()).getData();
        System.arraycopy(b, 0, targetPixels, 0, b.length);
        return image;
    }

    private void addLabel(JPanel panel, GridBagConstraints gbc, String text, int x, int y) {
        gbc.gridx = x;
        gbc.gridy = y;
        JLabel l = new JLabel(text);
        l.setFont(new Font("Segoe UI", Font.BOLD, 16));
        l.setForeground(new Color(0, 51, 102));
        panel.add(l, gbc);
    }

    private void addComboBox(JPanel panel, JComboBox<String> comboBox, GridBagConstraints gbc, int x, int y) {
        gbc.gridx = x;
        gbc.gridy = y;
        comboBox.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        comboBox.setPreferredSize(new Dimension(280, 30));
        comboBox.setBackground(Color.white);
        comboBox.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                JComponent comp = (JComponent) super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                comp.setBorder(BorderFactory.createEmptyBorder(5, 8, 5, 8));
                return comp;
            }
        });
        panel.add(comboBox, gbc);
    }

    private void addField(JPanel panel, JTextField field, GridBagConstraints gbc, int x, int y) {
        gbc.gridx = x;
        gbc.gridy = y;
        field.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        field.setPreferredSize(new Dimension(280, 30));
        field.setBorder(new CompoundBorder(new LineBorder(new Color(170, 190, 225), 1, true), new EmptyBorder(5, 8, 5, 8)));
        panel.add(field, gbc);
    }

    private void stopAutoCapture() {
        autoCapturing = false;
        if (autoCaptureThread != null && autoCaptureThread.isAlive()) {
            autoCaptureThread.interrupt();
            try {
                autoCaptureThread.join();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }

    private void closeCameraIfOpen() {
        stopAutoCapture();
        cameraActive = false;
        if (cameraThread != null) {
            try {
                cameraThread.join(1000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            cameraThread = null;
        }
        if (camera != null && camera.isOpened()) {
            camera.release();
            camera = null;
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new Signup2("")); // Example form number (must be 10 digits)
    }
}
