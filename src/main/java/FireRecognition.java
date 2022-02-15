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
    private final Mat frameDelta = new Mat();
    private final Mat thresh = new Mat();

    private boolean detectionByColor() {
        VideoCapture fire_video = new VideoCapture("firevideo.mp4");
        int noRed = 0;
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
                noRed = Core.countNonZero(mask);
                // HighGui.imshow("Fire Recognition", frame);
                // HighGui.imshow("hsv", hsv);
                // HighGui.imshow("blur", blur);
                ImgProcHelper.resizeImage(frame);
                ImgProcHelper.resizeImage(output);

                if (noRed > 20) {
                    ImgProcHelper.addText(output, "FLAME RECOGNIZED");
                    System.out.println("Fire detected");
                }
                HighGui.imshow("Frame", frame);
                HighGui.imshow("output", output);
                int key = HighGui.waitKey(20);
                if (key == 27)
                    break;
            }
        }
        HighGui.destroyAllWindows();
        return noRed > 20;
    }

    private boolean detectionByMovement() {

        List<MatOfPoint> cnts = new ArrayList<MatOfPoint>();
        VideoCapture camera = new VideoCapture("firevideo.mp4");
        if (camera.isOpened()) {
            while (true) {
                camera.read(frame);
                if (frame.empty()) {
                    break;
                }

                try {
                    Thread.sleep(3000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                camera.read(frame);
                //convert to grayscale and set the first frame
                Imgproc.cvtColor(frame, firstFrame, Imgproc.COLOR_BGR2GRAY);
                Imgproc.GaussianBlur(firstFrame, firstFrame, new Size(21, 21), 0);

                while (camera.read(frame)) {
                    //convert to grayscale
                    Imgproc.cvtColor(frame, gray, Imgproc.COLOR_BGR2GRAY);
                    Imgproc.GaussianBlur(gray, gray, new Size(21, 21), 0);

                    //compute difference between first frame and current frame
                    Core.absdiff(firstFrame, gray, frameDelta);
                    Imgproc.threshold(frameDelta, thresh, 25, 255, Imgproc.THRESH_BINARY);

                    Imgproc.dilate(thresh, thresh, new Mat(), new Point(-1, -1), 2);
                    Imgproc.findContours(thresh, cnts, new Mat(), Imgproc.RETR_EXTERNAL, Imgproc.CHAIN_APPROX_SIMPLE);

                    for (MatOfPoint cnt : cnts) {
                        if (Imgproc.contourArea(cnt) < 500) {
                            continue;
                        }

                        return true;
                    }
                }
            }
        }
        return false;
    }

    public void showFire() {
        if (detectionByMovement()) {
            if (detectionByColor()) {
                System.out.println("Fireee");
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

