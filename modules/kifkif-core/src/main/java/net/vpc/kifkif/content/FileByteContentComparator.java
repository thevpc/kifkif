package net.vpc.kifkif.content;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import net.vpc.kifkif.KifKif;

/**
 * Byte by byte comparator for file contents
 * User: taha
 * Date: 22 dec. 2004
 * Time: 19:32:23
 */
public class FileByteContentComparator extends AbstractFileContentComparator {
    public boolean compareFileContent(KifKif kifkif, File file1, File file2) {
        FileInputStream is1 = null;
        FileInputStream is2 = null;
        try {
            is1 = new FileInputStream(file1);
            is2 = new FileInputStream(file2);
            byte[] buffer1=new byte[1024];
            byte[] buffer2=new byte[1024];
            while (true) {
                int r1 = is1.read(buffer1);
                int r2 = is2.read(buffer2);
                if (r1 == r2) {
                    if (r1 == -1) {
                        return true;
                    } else {
                        for (int i = 0; i < r1; i++) {
                            byte b1 = buffer1[i];
                            byte b2 = buffer2[i];
                            if(b1!=b2){
                                return false;
                            }
                        }
                    }
                } else {
                    System.out.println("Not the same :\n\t"+file1.length()+" : "+file1+"\n\t"+file2.length()+" : "+file2);
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
        System.out.println("Not the same "+file1+" ---- "+file2);
        return false; // never reached
    }

}
