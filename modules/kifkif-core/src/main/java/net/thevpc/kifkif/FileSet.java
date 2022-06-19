package net.thevpc.kifkif;

import java.io.File;
import java.io.Serializable;
import java.util.Iterator;

public interface FileSet extends Serializable {
    EstimateIterator<File> iterate(KifKif kifkif);
    boolean contains(File file, KifKif kifkif);
}
