package net.vpc.app.kifkif.stamp;

import java.io.File;

import net.vpc.app.kifkif.Filestamp;
import net.vpc.app.kifkif.KifKif;

/**
 * User: taha
 * Date: 22 dec. 2004
 * Time: 19:07:06
 */
public class FileSizeStampFilter extends AbstractFileStampFilter {
    public FileSizeStampFilter() {
    }

    protected Filestamp createFilestamp(File file, KifKif kifkif) {
        long l = file.length();
        DefaultFilestamp f = new DefaultFilestamp(String.valueOf(l));
        f.setFileSize(l);
        return f;

    }

    public String toString() {
        return "FileSizeStampFilter";
    }
}
