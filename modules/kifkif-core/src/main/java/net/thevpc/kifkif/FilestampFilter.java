package net.thevpc.kifkif;

import java.io.File;


/**
 * User: taha
 * Date: 22 dec. 2004
 * Time: 18:52:18
 */
public interface FilestampFilter  {
    /**
     * create Filestamp for the given file.
     * Usually, timestamps are dependant thats why the previousLevelStamp is given
     *
     * @param file
     * @param previousLevelStamp
     * @param kifkif
     * @return stamps
     */
    Filestamp getFilestamp(File file, Filestamp previousLevelStamp, KifKif kifkif);
}
