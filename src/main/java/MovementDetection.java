import org.opencv.core.Core;
import org.opencv.core.*;
import org.opencv.highgui.HighGui;
import org.opencv.imgproc.Imgproc;
import org.opencv.videoio.VideoCapture;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class MovementDetection {
    public static void main(String args[]) {
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
        //load library


        Mat frame = new Mat();
        Mat firstFrame = new Mat();
        Mat gray = new Mat();
        Mat frameDelta = new Mat();
        Mat thresh = new Mat();
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

                    for (int i = 0; i < cnts.size(); i++) {
                        if (Imgproc.contourArea(cnts.get(i)) < 500) {
                            continue;
                        }

                        System.out.println("Motion detected!!!");
                        System.exit(0);
                    }
                }

            }
        }
        HighGui.destroyAllWindows();
        System.exit(0);
    }

}
