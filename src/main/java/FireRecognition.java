import org.opencv.core.*;
import org.opencv.highgui.HighGui;
import org.opencv.imgproc.Imgproc;
import org.opencv.videoio.VideoCapture;

import java.util.ArrayList;
import java.util.List;

public class FireRecognition {
    static {
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
    }

    private final Mat frame = new Mat();
    private final Mat hsv = new Mat();
    private final Mat blur = new Mat();
    private final Mat mask = new Mat();
    private final Mat output = new Mat();
    private final Mat firstFrame = new Mat();
    private final Mat gray = new Mat();
    private final Mat frameDiff = new Mat();
    private final Mat thresh = new Mat();

    private boolean detectionByColor() {
        VideoCapture fire_video = new VideoCapture("Slow Motion Fire Background  Fire Backgrounds.mp4");
        int nrRed = 0;
        if (fire_video.isOpened()) {
            while (true) {
                fire_video.read(frame);
                if (frame.empty()) break;

                Imgproc.GaussianBlur(frame, blur, new Size(21, 21), 0);
                Imgproc.cvtColor(blur, hsv, Imgproc.COLOR_BGR2HSV);

                Scalar upper = new Scalar(18, 50, 50);
                Scalar lower = new Scalar(35, 255, 255);

                Core.inRange(hsv, upper, lower, mask);
                Core.bitwise_and(frame, hsv, output, mask);
                nrRed = Core.countNonZero(mask);
                // HighGui.imshow("Fire Recognition", frame);
                // HighGui.imshow("hsv", hsv);
                // HighGui.imshow("blur", blur);
                ImgProcHelper.resizeImage(frame);
                ImgProcHelper.resizeImage(output);

                if (nrRed > 100) {
                    ImgProcHelper.addText(frame, "FLAME RECOGNIZED");
                   // System.out.println("Fire detected");
                }
                HighGui.imshow("Frame", frame);
                HighGui.imshow("output", output);
                int key = HighGui.waitKey(20);
                if (key == 27)
                    break;
            }
        }
        HighGui.destroyAllWindows();
        return nrRed > 100;
    }

    private boolean detectionByMovement() {

        List<MatOfPoint> cnts = new ArrayList<MatOfPoint>();
        VideoCapture fire_video = new VideoCapture("Slow Motion Fire Background  Fire Backgrounds.mp4");
        if (fire_video.isOpened()) {
            while (true) {
                fire_video.read(frame);
                if (frame.empty()) {
                    break;
                }

                fire_video.read(frame);
                //convert to grayscale and set the first frame
                Imgproc.cvtColor(frame, firstFrame, Imgproc.COLOR_BGR2GRAY);
                Imgproc.GaussianBlur(firstFrame, firstFrame, new Size(21, 21), 0);

                while (fire_video.read(frame)) {
                    //convert to grayscale
                    Imgproc.cvtColor(frame, gray, Imgproc.COLOR_BGR2GRAY);
                    Imgproc.GaussianBlur(gray, gray, new Size(21, 21), 0);

                    //compute difference between first frame and current frame
                    Core.absdiff(firstFrame, gray, frameDiff);
                    Imgproc.threshold(frameDiff, thresh, 25, 255, Imgproc.THRESH_BINARY);

                    Imgproc.erode(thresh, thresh, new Mat(), new Point(-1, -1), 2);
                    Imgproc.findContours(thresh, cnts, new Mat(), Imgproc.RETR_EXTERNAL, Imgproc.CHAIN_APPROX_SIMPLE);

                    for (MatOfPoint cnt : cnts) {
                        if (Imgproc.contourArea(cnt) < 500) {
                            continue;
                        }
                       // HighGui.imshow("threshold",thresh);
                        return true;
                    }
                }
                int key = HighGui.waitKey(20);
                if (key == 27)
                    break;
            }
        }
        return false;
    }

    public void showFire() {
        if (detectionByMovement()) {
            if (detectionByColor()) {
                System.out.println("Flame detected");
            } else {
                System.out.println("No colour detected");
            }
        } else {
            System.out.println("No movement detected");
        }
    }

    public static void main(String[] args) {
        FireRecognition fire = new FireRecognition();
        fire.showFire();
    }
}

