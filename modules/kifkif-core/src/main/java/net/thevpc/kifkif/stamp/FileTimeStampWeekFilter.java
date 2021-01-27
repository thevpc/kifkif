package net.thevpc.kifkif.stamp;

import java.io.File;
import java.util.Calendar;

import net.thevpc.kifkif.Filestamp;
import net.thevpc.kifkif.KifKif;

/**
 * User: taha
 * Date: 22 dec. 2004
 * Time: 19:07:06
 */
public class FileTimeStampWeekFilter extends AbstractFileStampFilter {
    private transient Calendar c=Calendar.getInstance();

    public FileTimeStampWeekFilter() {
    }

    protected Filestamp createFilestamp(File file, KifKif kifkif) {
        if(c==null){
            c=Calendar.getInstance();
        }
        long lastModified = file.lastModified();
        c.setTimeInMillis(lastModified);
        DefaultFilestamp s = new DefaultFilestamp(String.valueOf(c.get(Calendar.WEEK_OF_YEAR))+";"+String.valueOf(c.get(Calendar.YEAR)));
        s.setLastModified(lastModified);
        return s;
    }

    public String toString() {
        return "FileTimeStampWeekFilter";
    }
}
