package imageprocessing;

import org.opencv.core.Core;
import org.opencv.core.Core.MinMaxLocResult;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.highgui.Highgui;
import org.opencv.imgproc.Imgproc;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

public class TemplateMatching {

    // From: https://stackoverflow.com/questions/17001083/opencv-template-matching-example-in-android

    private static final Logger LOG = LoggerFactory.getLogger(TemplateMatching.class);

    protected int method;
    protected double threshold;


    public TemplateMatching(int match_method, double threshold) {
        this.method = match_method;
        this.threshold = threshold;
        nu.pattern.OpenCV.loadLibrary();
    }

    public Map<String, String> isSubImage(String inFile, String templateFile) {
        LOG.info(String.format("Running Template Matching for files %s %s", inFile, templateFile));
        Map<String, String> matchResult = new HashMap<>();

        Mat img = Highgui.imread(inFile);
        Mat templ = Highgui.imread(templateFile);

        // / Create the result matrix
        int resultCols = img.cols() - templ.cols() + 1;
        int resultRows = img.rows() - templ.rows() + 1;
        Mat result = new Mat(resultRows, resultCols, CvType.CV_32FC1);

        // / Do the Matching and Normalize
        Imgproc.matchTemplate(img, templ, result, this.method);
        MinMaxLocResult mmr = Core.minMaxLoc(result);
        double score = mmr.maxVal - mmr.minVal;

        LOG.info(String.format(" Score %s", String.valueOf(score)));

        /* We don't need to normalize
        Core.normalize(result, result, 0, 1, Core.NORM_MINMAX, -1, new Mat());
        LOG.info(" normalized");

        Localizing the best match with minMaxLoc
        mmr = Core.minMaxLoc(result);
        LOG.info(mmr.toString());
        LOG.info(String.valueOf(mmr.minVal));
        LOG.info(String.valueOf(mmr.maxVal));
        *
        */
        Point matchLoc;
        if (this.method == Imgproc.TM_SQDIFF || this.method == Imgproc.TM_SQDIFF_NORMED) {
            matchLoc = mmr.minLoc;
        } else {
            matchLoc = mmr.maxLoc;
        }
        // / Show me what you got
        Core.rectangle(img, matchLoc, new Point(matchLoc.x + templ.cols(),
                matchLoc.y + templ.rows()), new Scalar(0, 255, 0));


        matchResult.put("score", String.valueOf(score));

        if (score > threshold) {
            matchResult.put("match", "t");
            matchResult.put("x", String.valueOf(matchLoc.x));
            matchResult.put("y", String.valueOf(matchLoc.y));
        } else {
            matchResult.put("match", "f");
            matchResult.put("x", "-1");
            matchResult.put("y", "-1");
        }

        LOG.info(matchResult.toString());

        return matchResult;
    }
}



