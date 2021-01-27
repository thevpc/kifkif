package net.thevpc.kifkif;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;


/**
 * User: taha
 * Date: 22 dec. 2004
 * Time: 18:52:28
 */
public class FilestampFilterList implements Serializable {
    private ArrayList<FilestampFilter> filters = new ArrayList<FilestampFilter>(3);

    public FilestampFilterList() {
    }

    public FilestampFilterList(FilestampFilter ... filters) {
        this.filters.addAll(Arrays.asList(filters));
    }

    public Filestamp getFirstFilterId(File file, KifKif kifKif) {
        return filters.get(0).getFilestamp(file, null, kifKif);
    }

//    public net.thevpc.kifkif.Filestamp getFilterId(File file, int level) {
//        return level==0?getFirstFilterId(file):
//        getNextFilterSig(file, getFilterId(file, level-1), level);
//    }

    public Filestamp getNextFilterSig(File file, Filestamp latestFilterStamp, int level, KifKif kifkif) {
        return filters.get(level).getFilestamp(file, latestFilterStamp, kifkif);
    }

    public int getMaxLevels() {
        return filters.size();
    }

    public FilestampFilter getFilestampFilter(int level) {
        return filters.get(level);
    }
}
