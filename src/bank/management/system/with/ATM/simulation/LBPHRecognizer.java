package bank.management.system.with.ATM.simulation;

import org.bytedeco.javacpp.IntPointer;
import org.bytedeco.opencv.global.opencv_core;
import org.bytedeco.opencv.global.opencv_imgcodecs;
import org.bytedeco.opencv.global.opencv_imgproc;
import org.bytedeco.opencv.opencv_core.Mat;
import org.bytedeco.opencv.opencv_core.MatVector;
import org.bytedeco.opencv.opencv_core.Size;
import org.bytedeco.opencv.opencv_face.LBPHFaceRecognizer;

import javax.swing.*;
import java.io.File;
import java.nio.file.Paths;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class LBPHRecognizer extends SwingWorker<Void, Integer> {

    private final String trainingDir;
    private final String formno;
    private final JProgressBar progressBar;
    private final JLabel statusLabel;

    private static final String MODEL_SAVE_DIR = "C:\\Users\\pratik_icy\\IdeaProjects\\Bank Mgmt System with ATM simulation\\trained_models\\";

    public LBPHRecognizer(String formno, JProgressBar progressBar, JLabel statusLabel) {
        this.formno = formno;
        this.trainingDir = "face_data/" + formno;
        this.progressBar = progressBar;
        this.statusLabel = statusLabel;
    }

    @Override
    protected Void doInBackground() throws Exception {
        publish(0);
        statusLabel.setText(" Training started...");

        File dir = new File(trainingDir);
        if (!dir.exists() || !dir.isDirectory()) {
            statusLabel.setText(" Training directory not found: " + trainingDir);
            return null;
        }

        File[] imageFiles = dir.listFiles((d, name) -> name.toLowerCase().matches(".*\\.(jpg|png)$"));
        if (imageFiles == null || imageFiles.length < 50) {
            statusLabel.setText("Not enough images to train (need 50). Found: " + (imageFiles != null ? imageFiles.length : 0));
            return null;
        }

        List<Mat> images = new ArrayList<>();
        List<Integer> labels = new ArrayList<>();

        for (int i = 0; i < imageFiles.length; i++) {
            Mat img = opencv_imgcodecs.imread(imageFiles[i].getAbsolutePath(), opencv_imgcodecs.IMREAD_GRAYSCALE);
            if (img.empty()) {
                System.err.println(" Could not read: " + imageFiles[i].getAbsolutePath());
                continue;
            }
            opencv_imgproc.resize(img, img, new Size(160, 160));
            images.add(img);
            labels.add(1);
            publish((int) (((i + 1) / (double) imageFiles.length) * 80));
        }

        if (images.isEmpty()) {
            statusLabel.setText(" No valid images found for training.");
            return null;
        }

        LBPHFaceRecognizer recognizer = LBPHFaceRecognizer.create();

        MatVector matVector = new MatVector(images.size());
        for (int i = 0; i < images.size(); i++) matVector.put(i, images.get(i));

        Mat labelsMat = new Mat(labels.size(), 1, opencv_core.CV_32SC1);
        IntPointer labelsPtr = new IntPointer(labelsMat.data());
        for (int i = 0; i < labels.size(); i++) labelsPtr.put(i, labels.get(i));

        statusLabel.setText("⚙️ Training face model...");
        recognizer.train(matVector, labelsMat);
        publish(90);

        File modelDir = new File(MODEL_SAVE_DIR);
        if (!modelDir.exists()) modelDir.mkdirs();

        String modelPath = Paths.get(MODEL_SAVE_DIR, formno + "_model.yml").toString();
        recognizer.save(modelPath);

        updateModelPathInDatabase(formno, modelPath);

        for (Mat img : images) img.release();
        labelsMat.release();
        recognizer.close();

        publish(100);
        return null;
    }

    private void updateModelPathInDatabase(String formno, String modelPath) {
        String dbUrl = "jdbc:mysql://localhost:3306/bank_system";
        String dbUser = "root";
        String dbPass = "root";

        try (Connection conn = DriverManager.getConnection(dbUrl, dbUser, dbPass)) {
            String query = "UPDATE signup SET face_model_path = ? WHERE formno = ?";
            try (PreparedStatement ps = conn.prepareStatement(query)) {
                ps.setString(1, modelPath);
                ps.setString(2, formno);

                int rows = ps.executeUpdate();
                if (rows > 0) {
                    statusLabel.setText(" Model saved & DB updated.");
                    System.out.println(" Model saved & DB updated for formno: " + formno);
                } else {
                    statusLabel.setText(" Form number not found in DB.");
                    System.err.println(" Form number not found: " + formno);
                }
            }
        } catch (SQLException e) {
            statusLabel.setText(" DB error: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @Override
    protected void process(List<Integer> chunks) {
        for (int progress : chunks) progressBar.setValue(progress);
    }

    @Override
    protected void done() {
        try {
            get();
            statusLabel.setText(" Face model training complete!");
        } catch (Exception e) {
            statusLabel.setText(" Training failed: " + e.getMessage());
            e.printStackTrace();
        } finally {
            progressBar.setValue(100);
        }
    }
}
