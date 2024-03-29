package net.thevpc.kifkif;

import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;

import net.thevpc.kifkif.util.FixedEstimateIterator;
import net.thevpc.kifkif.util.FolderVisitorEstimateIterator;

/**
 * @author vpc
 * Date: 12 janv. 2005
 * Time: 19:54:24
 */
public class DefaultFileSet implements FileSet{
    private File root;
    private FileFilter fileFilter;

    public DefaultFileSet() {

    }

    public DefaultFileSet(File root) {
        this(root,null);
    }

    public DefaultFileSet(File root, FileFilter fileFilter) {
        this.root = root;
        this.fileFilter = fileFilter;
        if (root.isFile() && fileFilter != null) {
            throw new RuntimeException("Unable to add File with File Filter");
        }
    }

    public EstimateIterator<File> iterate(final KifKif kifkif) {
        final FileFilter compoundFilter=new FileFilter() {
            public boolean accept(File pathname) {
                FileFilter globalFileFilter = kifkif.getGlobalFileFilter();
                return
                        (globalFileFilter==null || globalFileFilter.accept(pathname))
                        &&
                        (fileFilter==null || fileFilter.accept(pathname))
                        ;
            }
        };
        if (root.isFile()) {
            ArrayList<File> a = new ArrayList<File>();
            a.add(root);
            return new FixedEstimateIterator<File>(a);
        } else {
            return new FolderVisitorEstimateIterator(this, compoundFilter);
        }
    }

    public boolean contains(File file, KifKif kifkif) {
        FileFilter globalFileFilter = kifkif.getGlobalFileFilter();
        if(globalFileFilter !=null && !globalFileFilter.accept(file)){
            return false;
        }
        if (fileFilter == null) {
            if (root.isFile()) {
                return file.equals(root);
            } else {
                return file.getPath().replace("\\", "/").startsWith(root.getPath().replace("\\", "/") + "/");
            }
        } else {
            if (fileFilter.accept(file)) {
                if (root.isFile()) {
                    return file.equals(root);
                } else {
                    return file.getPath().replace("\\", "/").startsWith(root.getPath().replace("\\", "/") + "/");
                }
            }
        }
        return false;
    }

    public FileFilter getFileFilter() {
        return fileFilter;
    }

    public void setFileFilter(FileFilter fileFilter) {
        this.fileFilter = fileFilter;
    }

    public String toString() {
        return root.toString();
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (toString().equals(obj.toString()) && obj instanceof DefaultFileSet) {
            DefaultFileSet f = (DefaultFileSet) obj;
            return root.equals(f.root) && (this.fileFilter == f.fileFilter || (this.fileFilter != null && this.fileFilter.equals(f.fileFilter)));
        }
        return false;
    }

    public int hashCode() {
        return toString().hashCode();
    }

    public File getRoot() {
        return root;
    }

    public void setRoot(File root) {
        this.root = root;
    }

}
