package net.vpc.app.kifkif;

import java.io.File;
import java.io.Serializable;
import java.util.Iterator;

import net.vpc.common.prs.xml.XmlSerializable;

/**
 * @author vpc
 * Date: 12 janv. 2005
 * Time: 19:41:17
 */
public interface FileSet extends Serializable, XmlSerializable {
    public Iterator<File> iterate(KifKif kifkif);

    public boolean contains(File file, KifKif kifkif);
}
