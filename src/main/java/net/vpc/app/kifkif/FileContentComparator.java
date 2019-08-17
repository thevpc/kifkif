package net.vpc.app.kifkif;

import java.io.File;
import java.io.Serializable;

import net.vpc.common.prs.xml.XmlSerializable;

/**
 * User: taha
 * Date: 22 dec. 2004
 * Time: 19:32:58
 */
public interface FileContentComparator extends Serializable, XmlSerializable {
    boolean compareFileContent(KifKif kifkif, File file1, File file2);
}
