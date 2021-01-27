package net.thevpc.kifkif.stamp;

import java.io.File;

import net.thevpc.kifkif.Filestamp;
import net.thevpc.kifkif.FilestampFilter;
import net.thevpc.kifkif.KifKif;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * Te default Fitler
 * User: taha
 * Date: 22 dec. 2004
 * Time: 19:02:11
 */
public abstract class AbstractFileStampFilter implements FilestampFilter {

    public AbstractFileStampFilter() {
    }

    public Filestamp getFilestamp(File file, Filestamp previousLevelStamp, KifKif kifkif) {
        Filestamp i = createFilestamp(file, kifkif);
        return previousLevelStamp == null ? i : i.combine(previousLevelStamp);
    }

    /**
     * create Filestamp for the given file
     *
     * @param file
     * @param kifkif
     * @return Filestamp
     */
    protected abstract Filestamp createFilestamp(File file, KifKif kifkif);
}
