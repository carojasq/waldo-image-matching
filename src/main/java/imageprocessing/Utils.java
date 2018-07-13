package imageprocessing;

import org.apache.commons.codec.binary.Base64;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

public class Utils {

    public static String base64ImgToTmpFile(String encodedImg) throws IOException {
        byte[] data = Base64.decodeBase64(encodedImg);
        String tmpPath = File.createTempFile("image",".jpg").getAbsolutePath();
        try (OutputStream stream = new FileOutputStream(tmpPath)) {
            stream.write(data);
        }
        return tmpPath;
    }

}
