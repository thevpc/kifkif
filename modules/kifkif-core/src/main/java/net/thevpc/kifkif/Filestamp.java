package net.thevpc.kifkif;

import java.io.Serializable;



/**
 * User: taha
 * Date: 22 dec. 2004
 * Time: 18:54:57
 */
public interface Filestamp extends Serializable {
    public Filestamp combine(Filestamp previousLevelStamp);

    public String getFileName();

    public long getFileSize();

    long getLastModified();
}
