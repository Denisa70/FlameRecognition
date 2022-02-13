import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;

import static org.opencv.imgproc.Imgproc.putText;

public class ImgProcHelper {
    static void addText(Mat mat,String text){
        int font = Imgproc.FONT_HERSHEY_SIMPLEX;
        Scalar color = new Scalar(0, 0, 255);
        putText(mat,
                text,
                new Point(mat.cols() / 4, mat.rows() / 2),
                Imgproc.FONT_HERSHEY_COMPLEX,
                1,
                new Scalar(255, 255, 255));
    }
    static void resizeImage(Mat mat){
        Size size = new Size(mat.cols() * 0.5, mat.rows() * 0.5);
        Imgproc.resize(mat, mat, size, 0, 0, Imgproc.INTER_AREA);
    }
}
