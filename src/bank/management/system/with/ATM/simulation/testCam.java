package bank.management.system.with.ATM.simulation;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.videoio.VideoCapture;
import org.opencv.highgui.HighGui;

public class testCam {
    static { System.loadLibrary(Core.NATIVE_LIBRARY_NAME); }

    public static void main(String[] args) {
        VideoCapture cam = new VideoCapture(0);
        if (!cam.isOpened()) {
            System.out.println("❌ Camera not opening");
            return;
        }
        Mat frame = new Mat();
        while (true) {
            if (cam.read(frame)) {
                HighGui.imshow("Webcam", frame);
                if (HighGui.waitKey(30) >= 0) break;
            }
        }
        cam.release();
    }
}
