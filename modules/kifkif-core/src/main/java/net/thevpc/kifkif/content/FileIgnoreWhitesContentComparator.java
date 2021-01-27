package net.thevpc.kifkif.content;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import net.thevpc.kifkif.KifKif;

/**
 * Byte by byte comparator for file contents
 * User: taha
 * Date: 22 dec. 2004
 * Time: 19:32:23
 */
public class FileIgnoreWhitesContentComparator extends AbstractFileContentComparator {
    public boolean compareFileContent(KifKif kifkif, File file1, File file2) {
        FileReader is1 = null;
        FileReader is2 = null;
        try {
            is1 = new FileReader(file1);
            is2 = new FileReader(file2);
            while (true) {
                int r1;
                int r2;
                while((r1=is1.read())!=-1 && Character.isWhitespace(r1)){
                    //
                }
                while((r2=is2.read())!=-1 && Character.isWhitespace(r2)){
                    //
                }
                if (r1 == r2) {
                    if (r1 == -1) {
                        return true;
                    }
                } else {
                    System.out.println("Not the same :\n\t" + file1.length() + " : " + file1 + "\n\t" + file2.length() + " : " + file2);
                    return false;
                }
            }
        } catch (IOException e) {
            //
        } finally {
            if (is1 != null) {
                try {
                    is1.close();
                } catch (IOException e) {
                    //
                }
            }
            if (is2 != null) {
                try {
                    is2.close();
                } catch (IOException e) {
                    //
                }
            }
        }
        System.out.println("Not the same " + file1 + " ---- " + file2);
        return false; // never reached
    }

}
