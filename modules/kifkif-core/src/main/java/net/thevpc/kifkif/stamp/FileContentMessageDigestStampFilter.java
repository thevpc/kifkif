package net.thevpc.kifkif.stamp;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.security.MessageDigest;

import net.thevpc.kifkif.Filestamp;
import net.thevpc.kifkif.KifKif;

/**
 * User: taha
 * Date: 22 dec. 2004
 * Time: 19:07:06
 */
public class FileContentMessageDigestStampFilter extends AbstractFileStampFilter {
    public FileContentMessageDigestStampFilter() {
    }

    private String algo;

    public FileContentMessageDigestStampFilter(String algo) {
        this.algo = algo;
    }

    protected Filestamp createFilestamp(File file, KifKif kifkif) {
        FileInputStream is = null;
        MessageDigest messageDigest=null;
        try {
            messageDigest=MessageDigest.getInstance(algo);

            is = new FileInputStream(file);
            int r;
            byte[] buffer=new byte[1024];
            while ((r = is.read(buffer)) != -1) {
                messageDigest.update(buffer,0,r);
            }
        } catch (Exception e) {
            //
            throw new RuntimeException(e);
        } finally {
            if (is != null) {
                try {
                    is.close();
                } catch (IOException e) {
                    //
                }
            }
        }
        return new DefaultFilestamp(new String(messageDigest.digest()));
    }

    public String toString() {
        return "File Content Checksum comparator";
    }

}
