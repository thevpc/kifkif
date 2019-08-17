package net.vpc.app.kifkif.stamp;

import java.io.File;

import net.vpc.app.kifkif.Filestamp;
import net.vpc.app.kifkif.KifKif;

/**
 * User: taha
 * Date: 22 dec. 2004
 * Time: 19:07:06
 */
public class FileExtensionStampFilter extends AbstractFileStampFilter {
    public FileExtensionStampFilter() {
    }

    protected Filestamp createFilestamp(File file, KifKif kifkif) {
        String name = file.getName();
        if (kifkif.isCaseInsensitiveNames()) {
            name = name.toLowerCase();
        }
        int i = name.lastIndexOf('.');
        String ext = (i < 0 || i == (name.length() - 1)) ? "" : name.substring(i + 1);
        return new DefaultFilestamp(ext);
    }

    public String toString() {
        return "FileSizeStampFilter";
    }
}
