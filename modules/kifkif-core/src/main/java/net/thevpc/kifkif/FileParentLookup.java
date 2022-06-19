package net.thevpc.kifkif;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;

public class FileParentLookup {
    private static class Info implements Comparable<Info>{
        String s;
        File f;

        public Info(File f) {
            this.f = f;
            try {
                s=f.getCanonicalPath();
            } catch (IOException e) {
                s=f.getAbsolutePath();
            }
            String v = s.replace("\\", "/");
            if(!v.endsWith("/")){
                s=s+"/";
            }
        }

        @Override
        public int compareTo(Info o) {
            return s.compareTo(o.s);
        }
    }

    private Info[] parents;
    public FileParentLookup(Collection<File> tempFolderToStampMap){
        parents =
                tempFolderToStampMap
                        .stream().map(Info::new).sorted().toArray(Info[]::new);
    }
    public boolean containsParentOf(File f){
        return findParentOf(f)!=null;
    }
    public File findParentOf(File f){
        int e = Arrays.binarySearch(parents, new Info(f));
        if(e<0){
            e=-(e+1);
            if(e>0) {
                e=e-1;
                Info t = parents[e];
                if (f.getParent().startsWith(t.s)) {
                    return t.f;
                }
            }
        }
        return null;
    }
}
