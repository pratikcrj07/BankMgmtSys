package bank.management.system.with.ATM.simulation;

import com.formdev.flatlaf.FlatDarkLaf;
import org.bytedeco.opencv.global.opencv_core;
import org.bytedeco.opencv.global.opencv_imgproc;
import org.bytedeco.opencv.opencv_core.*;
import org.bytedeco.opencv.opencv_core.Point;
import org.bytedeco.opencv.opencv_face.LBPHFaceRecognizer;
import org.bytedeco.opencv.opencv_objdetect.CascadeClassifier;
import org.bytedeco.opencv.opencv_videoio.VideoCapture;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.sql.*;

public class Login extends JFrame implements ActionListener {

    JLabel labelTitle, labelCardNo, labelPin;
    JTextField textFieldCardNo;
    JPasswordField passwordFieldPin;
    JButton btnSignIn, btnClear, btnSignUp, btnAdminLogin;
    JButton btnStartCamera;
    JLabel faceLabel;

    volatile boolean cameraRunning = false;
    VideoCapture camera;
    CascadeClassifier faceDetector;
    Mat currentFrame;

    public Login() {
        super("Bank Management System With ATM Simulation");
        FlatDarkLaf.setup();
        UIManager.put("Button.arc", 15);
        UIManager.put("Component.arc", 15);
        UIManager.put("ProgressBar.arc", 10);

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
        btnSignIn.setBackground(Color.BLACK); // all buttons black
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

        btnAdminLogin = new JButton("ADMIN LOGIN");
        btnAdminLogin.setFont(new Font("Arial", Font.BOLD, 14));
        btnAdminLogin.setForeground(Color.WHITE);
        btnAdminLogin.setBackground(Color.BLACK); // changed from gray to black
        btnAdminLogin.setBounds(200, 390, 300, 30);
        btnAdminLogin.addActionListener(this);
        add(btnAdminLogin);

        faceLabel = new JLabel("Camera Preview");
        faceLabel.setHorizontalAlignment(SwingConstants.CENTER);
        faceLabel.setBorder(BorderFactory.createLineBorder(Color.WHITE));
        faceLabel.setBounds(600, 50, 250, 250); // decreased size
        add(faceLabel);

        btnStartCamera = new JButton("Start Camera");
        btnStartCamera.setFont(new Font("Arial", Font.BOLD, 16));
        btnStartCamera.setForeground(Color.WHITE);
        btnStartCamera.setBackground(Color.BLACK);
        btnStartCamera.setBounds(650, 310, 130, 40); // moved up from 370 to 310
        btnStartCamera.addActionListener(this);
        add(btnStartCamera);

        ImageIcon bgIcon = new ImageIcon(ClassLoader.getSystemResource("icon/backbg.png"));
        Image bg = bgIcon.getImage().getScaledInstance(900, 500, Image.SCALE_DEFAULT);
        JLabel background = new JLabel(new ImageIcon(bg));
        background.setBounds(0, 0, 900, 500);
        add(background);

        faceDetector = new CascadeClassifier("src/haarcascade_frontalface_default.xml");

        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setVisible(true);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        Object src = e.getSource();
        if (src == btnSignIn) {
            signIn();
        } else if (src == btnClear) {
            textFieldCardNo.setText("");
            passwordFieldPin.setText("");
        } else if (src == btnSignUp) {
            setVisible(false);
            new Signup();
        } else if (src == btnAdminLogin) {
            setVisible(false);
            new AdminLogin();
        } else if (src == btnStartCamera) {
            if (!cameraRunning) {
                startCameraAndRecognize();
            }
        }
    }

    private void startCameraAndRecognize() {
        camera = new VideoCapture(0);
        if (!camera.isOpened()) {
            JOptionPane.showMessageDialog(this, "Webcam not accessible.");
            return;
        }
        cameraRunning = true;

        new Thread(() -> {
            currentFrame = new Mat();

            while (cameraRunning) {
                if (!camera.read(currentFrame) || currentFrame.empty()) continue;

                RectVector faces = new RectVector();
                faceDetector.detectMultiScale(currentFrame, faces);

                boolean recognized = false;

                for (int i = 0; i < faces.size(); i++) {
                    Rect rect = faces.get(i);
                    // Draw rectangle around face
                    opencv_imgproc.rectangle(currentFrame,
                            new Point(rect.x(), rect.y()),
                            new Point(rect.x() + rect.width(), rect.y() + rect.height()),
                            new Scalar(0, 255, 0, 0), 2, opencv_imgproc.LINE_8, 0);

                    Mat face = new Mat(currentFrame, rect);
                    opencv_imgproc.cvtColor(face, face, opencv_imgproc.COLOR_BGR2GRAY);

                    try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/bank_system", "root", "root")) {
                        String query = "SELECT card_number, pin_number, name, face_model_path FROM signup WHERE face_model_path IS NOT NULL";
                        PreparedStatement ps = conn.prepareStatement(query);
                        ResultSet rs = ps.executeQuery();

                        while (rs.next()) {
                            String modelPath = rs.getString("face_model_path");
                            String card = rs.getString("card_number");
                            String pin = rs.getString("pin_number");
                            String name = rs.getString("name");

                            LBPHFaceRecognizer recognizer = LBPHFaceRecognizer.create();
                            recognizer.read(modelPath);

                            int[] label = new int[1];
                            double[] confidence = new double[1];
                            recognizer.predict(face, label, confidence);

                            recognizer.close();

                            if (confidence[0] < 70) {
                                recognized = true;

                                cameraRunning = false;
                                camera.release();

                                SwingUtilities.invokeLater(() -> {
                                    JOptionPane.showMessageDialog(this,
                                            "Welcome " + name + "!\nFace recognized.");
                                    textFieldCardNo.setText(card);
                                    passwordFieldPin.setText(pin);

                                    setVisible(false);
                                    new main_Class(card);
                                });
                                break;
                            }
                        }
                        rs.close();
                        ps.close();
                    } catch (SQLException ex) {
                        ex.printStackTrace();
                    }

                    if (recognized) break;
                }


                faceLabel.setIcon(new ImageIcon(matToBufferedImage(currentFrame)));

                if (recognized) break;

                try {
                    Thread.sleep(10);
                } catch (InterruptedException ignored) {
                }
            }

            if (camera.isOpened()) {
                camera.release();
            }
        }).start();
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
            ResultSet rs = c.s.executeQuery("SELECT * FROM signup WHERE card_number = '" + cardNo + "' AND pin_number = '" + pin + "'");

            if (rs.next()) {
                setVisible(false);
                new main_Class(cardNo);
            } else {
                JOptionPane.showMessageDialog(this, "Incorrect Card No or PIN");
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private BufferedImage matToBufferedImage(Mat mat) {
        int type = BufferedImage.TYPE_BYTE_GRAY;
        if (mat.channels() > 1) type = BufferedImage.TYPE_3BYTE_BGR;
        int bufferSize = mat.channels() * mat.cols() * mat.rows();
        byte[] b = new byte[bufferSize];
        mat.data().get(b);
        BufferedImage image = new BufferedImage(mat.cols(), mat.rows(), type);
        final byte[] targetPixels = ((DataBufferByte) image.getRaster().getDataBuffer()).getData();
        System.arraycopy(b, 0, targetPixels, 0, b.length);
        return image;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(Login::new);
    }
}
