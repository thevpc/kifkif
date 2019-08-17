package net.vpc.kifkif.stamp;

import java.io.File;

import net.vpc.kifkif.Filestamp;
import net.vpc.kifkif.KifKif;

/**
 * User: taha
 * Date: 22 dec. 2004
 * Time: 19:07:06
 */
public class FileTimeStampFilter extends AbstractFileStampFilter {
    public FileTimeStampFilter() {
    }

    protected Filestamp createFilestamp(File file, KifKif kifkif) {
        long lastModified = file.lastModified();
        DefaultFilestamp s = new DefaultFilestamp(String.valueOf(lastModified));
        s.setLastModified(lastModified);
        return s;
    }

    public String toString() {
        return "FileTimeStampFilter";
    }
}
