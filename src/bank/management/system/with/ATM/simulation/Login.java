package bank.management.system.with.ATM.simulation;

import org.opencv.core.*;
import org.opencv.core.Point;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.objdetect.CascadeClassifier;
import org.opencv.videoio.VideoCapture;
import org.opencv.imgproc.Imgproc;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.io.File;
import java.sql.ResultSet;

public class Login extends JFrame implements ActionListener {

    static {
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
    }

    JLabel labelTitle, labelCardNo, labelPin;
    JTextField textFieldCardNo;
    JPasswordField passwordFieldPin;

    JButton btnSignIn, btnClear, btnSignUp;
    JLabel faceLabel;
    JButton btnStartCamera, btnFaceScan;

    volatile boolean cameraRunning = false;
    VideoCapture camera;
    CascadeClassifier faceDetector;
    Mat currentFrame;

    public Login() {
        super("Bank Management System With ATM Simulation");

        setLayout(null);
        setSize(900, 500);
        setLocation(450, 200);
        setResizable(false);

        ImageIcon bankIcon = new ImageIcon(ClassLoader.getSystemResource("icon/bank.png"));
        Image bankImg = bankIcon.getImage().getScaledInstance(100, 100, Image.SCALE_DEFAULT);
        JLabel labelBankLogo = new JLabel(new ImageIcon(bankImg));
        labelBankLogo.setBounds(350, 10, 100, 100);
        add(labelBankLogo);

        ImageIcon cardIcon = new ImageIcon(ClassLoader.getSystemResource("icon/card.png"));
        Image cardImg = cardIcon.getImage().getScaledInstance(100, 100, Image.SCALE_DEFAULT);
        JLabel labelCardIcon = new JLabel(new ImageIcon(cardImg));
        labelCardIcon.setBounds(750, 350, 100, 100);
        add(labelCardIcon);

        labelTitle = new JLabel("WELCOME TO ATM");
        labelTitle.setForeground(Color.WHITE);
        labelTitle.setFont(new Font("AvantGarde", Font.BOLD, 38));
        labelTitle.setBounds(230, 125, 450, 40);
        add(labelTitle);

        labelCardNo = new JLabel("Card No:");
        labelCardNo.setFont(new Font("Raleway", Font.BOLD, 28));
        labelCardNo.setForeground(Color.WHITE);
        labelCardNo.setBounds(50, 190, 200, 30);
        add(labelCardNo);

        textFieldCardNo = new JTextField();
        textFieldCardNo.setBounds(200, 190, 300, 30);
        textFieldCardNo.setFont(new Font("Arial", Font.BOLD, 14));
        add(textFieldCardNo);

        labelPin = new JLabel("PIN:");
        labelPin.setFont(new Font("Raleway", Font.BOLD, 28));
        labelPin.setForeground(Color.WHITE);
        labelPin.setBounds(50, 250, 200, 30);
        add(labelPin);

        passwordFieldPin = new JPasswordField();
        passwordFieldPin.setBounds(200, 250, 300, 30);
        passwordFieldPin.setFont(new Font("Arial", Font.BOLD, 14));
        add(passwordFieldPin);

        btnSignIn = new JButton("SIGN IN");
        btnSignIn.setFont(new Font("Arial", Font.BOLD, 14));
        btnSignIn.setForeground(Color.WHITE);
        btnSignIn.setBackground(Color.BLACK);
        btnSignIn.setBounds(200, 300, 130, 30);
        btnSignIn.addActionListener(this);
        add(btnSignIn);

        btnClear = new JButton("CLEAR");
        btnClear.setFont(new Font("Arial", Font.BOLD, 14));
        btnClear.setForeground(Color.WHITE);
        btnClear.setBackground(Color.BLACK);
        btnClear.setBounds(370, 300, 130, 30);
        btnClear.addActionListener(this);
        add(btnClear);

        btnSignUp = new JButton("SIGN UP");
        btnSignUp.setFont(new Font("Arial", Font.BOLD, 14));
        btnSignUp.setForeground(Color.WHITE);
        btnSignUp.setBackground(Color.BLACK);
        btnSignUp.setBounds(200, 350, 300, 30);
        btnSignUp.addActionListener(this);
        add(btnSignUp);

        faceLabel = new JLabel("No Camera");
        faceLabel.setHorizontalAlignment(SwingConstants.CENTER);
        faceLabel.setBorder(BorderFactory.createLineBorder(Color.WHITE));
        faceLabel.setBounds(570, 120, 280, 210);
        add(faceLabel);

        btnStartCamera = new JButton("Start Camera");
        btnStartCamera.setFont(new Font("Arial", Font.BOLD, 12));
        btnStartCamera.setForeground(Color.WHITE);
        btnStartCamera.setBackground(new Color(0, 120, 215));
        btnStartCamera.setBounds(570, 340, 130, 30);
        btnStartCamera.addActionListener(this);
        add(btnStartCamera);

        btnFaceScan = new JButton("Scan & Login");
        btnFaceScan.setFont(new Font("Arial", Font.BOLD, 12));
        btnFaceScan.setForeground(Color.WHITE);
        btnFaceScan.setBackground(new Color(0, 160, 100));
        btnFaceScan.setBounds(720, 340, 130, 30);
        btnFaceScan.addActionListener(this);
        add(btnFaceScan);

        ImageIcon bgIcon = new ImageIcon(ClassLoader.getSystemResource("icon/backbg.png"));
        Image bg = bgIcon.getImage().getScaledInstance(900, 500, Image.SCALE_DEFAULT);
        JLabel background = new JLabel(new ImageIcon(bg));
        background.setBounds(0, 0, 900, 500);
        add(background);

        faceDetector = new CascadeClassifier("C:\\Users\\pratik_icy\\IdeaProjects\\OcvTest\\src\\resource\\haarcascade_frontalface_default.xml");

        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setVisible(true);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == btnSignIn) {
            signIn();
        } else if (e.getSource() == btnClear) {
            textFieldCardNo.setText("");
            passwordFieldPin.setText("");
        } else if (e.getSource() == btnSignUp) {
            setVisible(false);
            new Signup();
        } else if (e.getSource() == btnStartCamera) {
            startCamera();
        } else if (e.getSource() == btnFaceScan) {
            scanFaceAndLogin();
        }
    }

    private void startCamera() {
        if (cameraRunning) return;

        camera = new VideoCapture(0);
        if (!camera.isOpened()) {
            JOptionPane.showMessageDialog(this, " Cannot open webcam.");
            return;
        }

        cameraRunning = true;

        new Thread(() -> {
            currentFrame = new Mat();
            while (cameraRunning) {
                if (!camera.read(currentFrame) || currentFrame.empty()) continue;

                MatOfRect faces = new MatOfRect();
                faceDetector.detectMultiScale(currentFrame, faces);

                for (Rect rect : faces.toArray()) {
                    Imgproc.rectangle(currentFrame, new Point(rect.x, rect.y),
                            new Point(rect.x + rect.width, rect.y + rect.height),
                            new Scalar(0, 255, 0), 3);
                }

                BufferedImage img = matToBufferedImage(currentFrame);
                faceLabel.setIcon(new ImageIcon(img));

                try {
                    Thread.sleep(30);
                } catch (InterruptedException ex) {
                    ex.printStackTrace();
                }
            }
            camera.release();
        }).start();
    }

    private void scanFaceAndLogin() {
        if (currentFrame == null || currentFrame.empty()) {
            JOptionPane.showMessageDialog(this, "No frame captured. Start camera first.");
            return;
        }

        MatOfRect faces = new MatOfRect();
        faceDetector.detectMultiScale(currentFrame, faces);

        Rect[] detectedFaces = faces.toArray();
        if (detectedFaces.length == 0) {
            JOptionPane.showMessageDialog(this, " No face detected.");
            return;
        }

        Rect faceRect = detectedFaces[0];
        Mat capturedFace = new Mat(currentFrame, faceRect);

        boolean found = false;

        try {
            Conn c = new Conn();
            ResultSet rs = c.s.executeQuery("SELECT * FROM signup");
            while (rs.next()) {
                String storedFacePath = rs.getString("face_image_path");
                String cardNo = rs.getString("card_number");
                String pin = rs.getString("pin_number");
                String name = rs.getString("name");
                String phone = rs.getString("phone");

                if (storedFacePath == null || storedFacePath.isEmpty()) continue;

                File f = new File(storedFacePath);
                if (!f.exists()) {
                    System.out.println(" File not found: " + storedFacePath);
                    continue;
                }

                Mat storedFace = Imgcodecs.imread(storedFacePath, Imgcodecs.IMREAD_GRAYSCALE);
                Imgproc.resize(capturedFace, capturedFace, storedFace.size());

                Mat hist1 = new Mat();
                Mat hist2 = new Mat();

                Imgproc.cvtColor(capturedFace, capturedFace, Imgproc.COLOR_BGR2GRAY);
                Imgproc.calcHist(java.util.Collections.singletonList(capturedFace), new MatOfInt(0), new Mat(), hist1, new MatOfInt(256), new MatOfFloat(0, 256));
                Imgproc.calcHist(java.util.Collections.singletonList(storedFace), new MatOfInt(0), new Mat(), hist2, new MatOfInt(256), new MatOfFloat(0, 256));

                Core.normalize(hist1, hist1);
                Core.normalize(hist2, hist2);

                double score = Imgproc.compareHist(hist1, hist2, Imgproc.CV_COMP_CORREL);

                if (score > 0.4) {
                    textFieldCardNo.setText(cardNo);
                    passwordFieldPin.setText(pin);

                    String maskedPhone = maskString(phone, 3, 3);
                    String maskedCard = maskString(cardNo, 4, 4);

                    String message = "✔ Face Matched!\nWelcome, " + name +
                            "\n Phone: " + maskedPhone +
                            "\n Card: " + maskedCard;

                    JOptionPane.showMessageDialog(this, message);

                    found = true;
                    break;
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        if (found) {
            signIn();
        } else {
            JOptionPane.showMessageDialog(this, " Face not recognized.");
        }
    }

    private String maskString(String original, int startUnmasked, int endUnmasked) {
        if (original == null || original.length() <= (startUnmasked + endUnmasked)) return original;
        String start = original.substring(0, startUnmasked);
        String end = original.substring(original.length() - endUnmasked);
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < original.length() - (startUnmasked + endUnmasked); i++) {
            sb.append("X");
        }
        return start + sb + end;
    }

    private void signIn() {
        try {
            String cardNo = textFieldCardNo.getText().trim();
            String pin = new String(passwordFieldPin.getPassword()).trim();

            if (cardNo.isEmpty() || pin.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please enter Card Number and PIN");
                return;
            }

            Conn c = new Conn();
            String query = "SELECT * FROM signup WHERE card_number = '" + cardNo + "' AND pin_number = '" + pin + "'";
            ResultSet rs = c.s.executeQuery(query);

            if (rs.next()) {
                setVisible(false);
                new main_Class(cardNo);
            } else {
                JOptionPane.showMessageDialog(this, "Incorrect Card Number or PIN");
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "An error occurred during login.");
        }
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

    public static void main(String[] args) {
        new Login();
    }
}
