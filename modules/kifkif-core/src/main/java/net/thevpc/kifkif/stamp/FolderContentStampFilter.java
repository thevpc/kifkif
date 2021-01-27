package net.thevpc.kifkif.stamp;

import java.io.File;
import java.util.Arrays;

import net.thevpc.kifkif.Filestamp;
import net.thevpc.kifkif.KifKif;

/**
 * User: taha
 * Date: 22 dec. 2004
 * Time: 19:07:06
 */
public class FolderContentStampFilter extends AbstractFileStampFilter {

    public FolderContentStampFilter() {
    }

    protected Filestamp createFilestamp(File file, KifKif kifkif) {
        String[] s = file.list();
        if (s == null) {
            //file no more exist
            return new DefaultFilestamp("0");
        }
        Arrays.sort(s);
        if (kifkif.isCaseInsensitiveNames()) {
            for (int i = 0; i < s.length; i++) {
                s[i] = s[i].toLowerCase();
            }
        }
        int x = 0;
        int y = 0;
        for (String s1 : s) {
            int r = s1.hashCode();
            x = 31 * x + r;
            y = 17 * y + r;
        }
        return new DefaultFilestamp(String.valueOf(x) + ";" + String.valueOf(y));
    }
}
