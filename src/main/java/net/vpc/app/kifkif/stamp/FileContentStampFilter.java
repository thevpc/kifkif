package net.vpc.app.kifkif.stamp;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import net.vpc.app.kifkif.Filestamp;
import net.vpc.app.kifkif.KifKif;

/**
 * User: taha
 * Date: 22 dec. 2004
 * Time: 19:07:06
 */
public class FileContentStampFilter extends AbstractFileStampFilter {
    public FileContentStampFilter() {
    }

    protected Filestamp createFilestamp(File file, KifKif kifkif) {
        FileInputStream is = null;
        int x = 0;
        int y = 0;
        try {
            is = new FileInputStream(file);
            x = 0;
            y = 0;
            int r;
            byte[] buffer=new byte[1024];
            while ((r = is.read(buffer)) != -1) {
                for (int i = 0; i < r; i++) {
                    byte b = buffer[i];
                    x = 31 * x + b;
                    y = 17 * y + b;
                }
            }
        } catch (IOException e) {
            //
        } finally {
            if (is != null) {
                try {
                    is.close();
                } catch (IOException e) {
                    //
                }
            }
        }
        return new DefaultFilestamp(String.valueOf(x) + ";" + String.valueOf(y));
    }

    public String toString() {
        return "File Content Checksum comparator";
    }
}
