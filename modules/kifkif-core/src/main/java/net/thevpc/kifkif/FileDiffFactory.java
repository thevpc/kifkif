package net.thevpc.kifkif;

import java.util.*;

import net.thevpc.kifkif.content.FileByteContentComparator;
import net.thevpc.kifkif.content.FileIgnoreWhitesContentComparator;
import net.thevpc.kifkif.content.FileIgnoreWhitesInsensitiveContentComparator;
import net.thevpc.kifkif.content.FileInsensitiveCharContentComparator;
import net.thevpc.kifkif.content.FolderContentComparator;

/**
 * utiliy Class for Filtering mode consants
 * User: taha
 * Date: 24 dec. 2004
 * Time: 16:13:28
 */
public final class FileDiffFactory {

    private FileDiffFactory() {
    }


    public static FileContentComparator createFileContentComparator(FileMode... mode) {
        Set<FileMode> s = new HashSet<>(Arrays.asList(mode));
        if (s.contains(FileMode.FILE_CONTENT_WI)) {
            return new FileIgnoreWhitesInsensitiveContentComparator();
        }
        if (s.contains(FileMode.FILE_CONTENT_W)) {
            return new FileIgnoreWhitesContentComparator();
        }
        if (s.contains(FileMode.FILE_CONTENT_I)) {
            return new FileInsensitiveCharContentComparator();
        }
        if (s.contains(FileMode.FILE_CONTENT)) {
            return new FileByteContentComparator();
        }
        return null;
    }

    public static FileContentComparator createFolderContentComparator(FileMode... mode) {
        Set<FileMode> s = new HashSet<>(Arrays.asList(mode));
        if (s.contains(FileMode.FOLDER_CONTENT)) {
            return new FolderContentComparator();
        }
        return null;
    }

//    public static void main(String[] args) {
//        System.out.println("Utils.isSet(FILE_TIME|FILE_TIME_MASK) = " + Utils.isSet(FILE_TIME & FILE_TIME_MASK));
//    }
}
