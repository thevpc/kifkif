package net.vpc.app.kifkif;

import java.io.Serializable;

import net.vpc.common.prs.xml.XmlSerializable;


/**
 * User: taha
 * Date: 22 dec. 2004
 * Time: 18:54:57
 */
public interface Filestamp extends Serializable, XmlSerializable {
    public Filestamp combine(Filestamp previousLevelStamp);

    public String getFileName();

    public long getFileSize();

    long getLastModified();
}
