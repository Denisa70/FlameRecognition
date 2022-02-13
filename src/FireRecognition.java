import org.opencv.core.*;
import org.opencv.highgui.HighGui;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import org.opencv.videoio.VideoCapture;

import static org.opencv.imgproc.Imgproc.putText;

public class FireRecognition {

    public static void main(String[] args) throws Exception {
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);

        Mat frame = new Mat();
        Mat hsv = new Mat();
        Mat blur = new Mat();
        Mat mask = new Mat();
        Mat output = new Mat();

        VideoCapture fire_video = new VideoCapture("firevideo.mp4");
        if (fire_video.isOpened()) {
            while (true) {
                fire_video.read(frame);
                if (frame.empty()) break;

                Imgproc.GaussianBlur(frame, blur, new Size(9, 9), 0);
                Imgproc.cvtColor(frame, hsv, Imgproc.COLOR_BGR2HSV);

                Scalar upper = new Scalar(18, 50, 50);
                Scalar lower = new Scalar(35, 255, 255);

                Core.inRange(hsv, upper, lower, mask);
                Core.bitwise_and(frame, hsv, output, mask);
                int noRed = Core.countNonZero(mask);
                // HighGui.imshow("Fire Recognition", frame);
                // HighGui.imshow("hsv", hsv);
                // HighGui.imshow("blur", blur);
                Size size = new Size(frame.cols() * 0.5, frame.rows() * 0.5);
                Imgproc.resize(output, output, size, 0, 0, Imgproc.INTER_AREA);


                if (noRed > 100) {
                    int font = Imgproc.FONT_HERSHEY_SIMPLEX;
                    Scalar color = new Scalar(0, 0, 255);
                    putText(output,
                            "FLAME DETECTED",
                            new Point(output.cols() / 4, output.rows() / 2),
                            Imgproc.FONT_HERSHEY_COMPLEX,
                            1,
                            new Scalar(255, 255, 255));
                    HighGui.imshow("This Frame", output);
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
        System.exit(0);
    }


}

//        if (fire_video.isOpened()) {
//            while (hasNext) {
//                hasNext = fire_video.read(frame);
//                numOfFrames++;
//
//                if (numOfFrames == 1000) {
//                    break;  // calling this will break out of the loop
//                }
//
//                HighGui.imshow("Fire Video", frame);
//                int key = HighGui.waitKey(20);
//                if (key == 27)
//                    break;
//            }

//        JFrame jframe = new JFrame("Title");
//        jframe.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
//        JLabel vidpanel = new JLabel();
//        jframe.setContentPane(vidpanel);
//        jframe.setVisible(true);
//        while(hasNext) {
//            hasNext = fire_video.read(frame);
//            numOfFrames++;
//
//            if(numOfFrames == 100) {
//                break;  // calling this will break out of the loop
//            }
//        } // end while loop

//        while (true) {
//            if (fire_video.read(frame)) {
//
//                ImageIcon image = new ImageIcon(new Mat2BufferedImage(frame));
//                vidpanel.setIcon(image);
//                vidpanel.repaint();
//
//            }
//        }