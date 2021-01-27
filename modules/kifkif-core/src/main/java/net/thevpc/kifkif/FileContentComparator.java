package net.thevpc.kifkif;

import java.io.File;
import java.io.Serializable;


/**
 * User: taha
 * Date: 22 dec. 2004
 * Time: 19:32:58
 */
public interface FileContentComparator extends Serializable {
    boolean compareFileContent(KifKif kifkif, File file1, File file2);
}
