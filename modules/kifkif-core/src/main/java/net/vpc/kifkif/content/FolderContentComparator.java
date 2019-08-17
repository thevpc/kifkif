package net.vpc.kifkif.content;

import java.io.File;
import java.util.Arrays;

import net.vpc.kifkif.KifKif;

/**
 * Folder comparator
 * User: taha
 * Date: 22 dec. 2004
 * Time: 19:32:23
 */
public class FolderContentComparator extends AbstractFileContentComparator {
    public FolderContentComparator() {
    }

    public boolean compareFileContent(KifKif kifkif, File folder1, File folder2) {
        File[] fs1 = folder1.listFiles();
        File[] fs2 = folder2.listFiles();
        if (fs1.length == fs2.length) {
            //TODO comparator?
            Arrays.sort(fs1);
            Arrays.sort(fs2);
            for (int i = 0; i < fs1.length; i++) {
                File f1 = fs1[i];
                File f2 = fs2[i];
                if (!kifkif.quickCompareFiles(f1, f2)) {
                    return false;
                }
            }
            return true;
        }
        return false;
    }
}
