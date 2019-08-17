package net.vpc.app.kifkif.stamp;

import java.io.File;
import java.util.Calendar;

import net.vpc.app.kifkif.Filestamp;
import net.vpc.app.kifkif.KifKif;

/**
 * User: taha
 * Date: 22 dec. 2004
 * Time: 19:07:06
 */
public class FileTimeStampHourFilter extends AbstractFileStampFilter {
    private transient Calendar c=Calendar.getInstance();

    public FileTimeStampHourFilter() {
    }

    protected Filestamp createFilestamp(File file, KifKif kifkif) {
        if(c==null){
            c=Calendar.getInstance();
        }
        long lastModified = file.lastModified();
        c.setTimeInMillis(lastModified);
        c.set(Calendar.MINUTE,0);
        c.set(Calendar.SECOND,0);
        c.set(Calendar.MILLISECOND,0);
        DefaultFilestamp s = new DefaultFilestamp(String.valueOf(c.getTimeInMillis()));
        s.setLastModified(lastModified);
        return s;
    }

    public String toString() {
        return "FileTimeStampHourFilter";
    }
}
