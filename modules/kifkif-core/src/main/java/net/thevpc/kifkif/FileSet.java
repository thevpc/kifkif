package net.thevpc.kifkif;

import java.io.File;
import java.io.Serializable;
import java.util.Iterator;


/**
 * @author vpc
 * Date: 12 janv. 2005
 * Time: 19:41:17
 */
public interface FileSet extends Serializable {
    public Iterator<File> iterate(KifKif kifkif);

    public boolean contains(File file, KifKif kifkif);
}
