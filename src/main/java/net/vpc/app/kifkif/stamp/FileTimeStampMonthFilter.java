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
public class FileTimeStampMonthFilter extends AbstractFileStampFilter {
    private transient Calendar c=Calendar.getInstance();

    public FileTimeStampMonthFilter() {
    }

    protected Filestamp createFilestamp(File file, KifKif kifkif) {
        if(c==null){
            c=Calendar.getInstance();
        }
        long lastModified = file.lastModified();
        c.setTimeInMillis(lastModified);
        DefaultFilestamp s = new DefaultFilestamp(String.valueOf(c.get(Calendar.MONTH))+";"+String.valueOf(c.get(Calendar.YEAR)));
        s.setLastModified(lastModified);
        return s;
    }

    public String toString() {
        return "FileTimeStampMonthFilter";
    }
}
