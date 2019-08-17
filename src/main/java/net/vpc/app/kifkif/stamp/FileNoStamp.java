package net.vpc.app.kifkif.stamp;

import java.io.File;

import net.vpc.app.kifkif.Filestamp;
import net.vpc.app.kifkif.KifKif;

/**
 * User: taha
 * Date: 22 dec. 2004
 * Time: 19:07:06
 */
public class FileNoStamp extends AbstractFileStampFilter {
    public FileNoStamp() {
    }

    protected Filestamp createFilestamp(File file, KifKif kifkif) {
        return new DefaultFilestamp("");
    }
}
