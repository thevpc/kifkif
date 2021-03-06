package net.thevpc.kifkif.stamp;

import java.io.File;

import net.thevpc.kifkif.Filestamp;
import net.thevpc.kifkif.KifKif;

/**
 * User: taha
 * Date: 22 dec. 2004
 * Time: 19:07:06
 */
public class FileNameStampFilter extends AbstractFileStampFilter {
    public FileNameStampFilter() {
    }

    protected Filestamp createFilestamp(File file, KifKif kifkif) {
        String name = file.getName();
        if (kifkif.isCaseInsensitiveNames()) {
            name = name.toLowerCase();
        }
        DefaultFilestamp s = new DefaultFilestamp(name);
        s.setFileName(name);
        return s;
    }

    public String toString() {
        return "FileNameStampFilter";
    }
}
