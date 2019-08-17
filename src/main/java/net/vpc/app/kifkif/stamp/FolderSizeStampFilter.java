package net.vpc.app.kifkif.stamp;

import java.io.File;

import net.vpc.app.kifkif.Filestamp;
import net.vpc.app.kifkif.KifKif;

/**
 * User: taha
 * Date: 22 dec. 2004
 * Time: 19:07:06
 */
public class FolderSizeStampFilter extends AbstractFileStampFilter {
    public FolderSizeStampFilter() {
    }

    protected Filestamp createFilestamp(File file, KifKif kifkif) {
        String[] children=file.list();
        long l = children==null?0:children.length; //children==null is folder is not accessible of invalid
        DefaultFilestamp f = new DefaultFilestamp(String.valueOf(l));
        f.setFileSize(l);
        return f;
    }
}
