package net.thevpc.kifkif;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;


/**
 * User: taha
 * Date: 24 dec. 2004
 * Time: 16:47:39
 */
public class DuplicateList implements Serializable {
    public static final String FILE_REMOVED = "FILE_REMOVED";
    public static final String FILE_ADDED = "FILE_ADDED";

    public static Comparator<DuplicateList> FILE_NAME_COMPARATOR = new Comparator<DuplicateList>() {
        public int compare(DuplicateList duplicateList, DuplicateList duplicateList1) {
            if (duplicateList.isFolderList() != duplicateList1.isFolderList()) {
                if (duplicateList.isFolderList()) {
                    return -1;
                } else {
                    return 1;
                }
            }
            String n1 = duplicateList.getFilestamp().getFileName();
            String n2 = duplicateList1.getFilestamp().getFileName();
            if (n1 == null) {
                n1 = "";
            }
            if (n2 == null) {
                n2 = "";
            }
            return n1.compareTo(n2);
        }
    };
    public static Comparator<DuplicateList> FILE_SIZE_COMPARATOR = new Comparator<DuplicateList>() {
        public int compare(DuplicateList duplicateList, DuplicateList duplicateList1) {
            if (duplicateList.isFolderList() != duplicateList1.isFolderList()) {
                if (duplicateList.isFolderList()) {
                    return -1;
                } else {
                    return 1;
                }
            }
            long l1 = duplicateList.getFilestamp().getFileSize();
            long l2 = duplicateList1.getFilestamp().getFileSize();
            return (l1 == l2) ? 0 : (l1 > l2) ? 1 : -1;
        }
    };
    public static Comparator<DuplicateList> DUPLICATE_COUNT_COMPARATOR = new Comparator<DuplicateList>() {
        public int compare(DuplicateList duplicateList, DuplicateList duplicateList1) {
            if (duplicateList.isFolderList() != duplicateList1.isFolderList()) {
                if (duplicateList.isFolderList()) {
                    return -1;
                } else {
                    return 1;
                }
            }
            int l1 = duplicateList.getFileCount();
            int l2 = duplicateList1.getFileCount();
            return (l1 == l2) ? 0 : (l1 > l2) ? 1 : -1;
        }
    };

    private Filestamp filestamp;
    private ArrayList<File> files = new ArrayList<File>(3);
    private transient PropertyChangeSupport support;

    public DuplicateList() {

    }

    public DuplicateList(Filestamp stamp) {
        this.filestamp = stamp;
    }

    public String toString() {
        return String.valueOf(filestamp) + String.valueOf(files);
    }

    public List<File> getFiles() {
        return files;
    }

    public int size() {
        return files.size();
    }

    public void removeFile(File file) {
        int i = files.indexOf(file);
        if (i >= 0) {
            files.remove(file);
            if (support != null) {
                support.firePropertyChange(FILE_REMOVED, null, new FileRemovedEvent(this, file, i));
            }
        }
    }

    public void addFile(File file) {
        files.add(file);
        if (support != null) {
            support.firePropertyChange(FILE_ADDED, null, new FileAddedEvent(this, file, files.size() - 1));
        }
    }

    public void addAllFiles(Collection<File> files) {
        for (File file : files) {
            addFile(file);
        }
    }

    public File getFile(int i) {
        return files.get(i);
    }

    public int getFileCount() {
        return files.size();
    }

    public Filestamp getFilestamp() {
        return filestamp;
    }

    public boolean isFolderList() {
        return files.size() > 0 && files.get(0).isDirectory();
    }

    public boolean isFileList() {
        return files.size() > 0 && files.get(0).isFile();
    }

    public void setFiles(ArrayList<File> files) {
        this.files = files;
        //TODO what to fire here?
    }

    public static final class FileNameComparator implements Comparator<DuplicateList> {
        private boolean reverse;
        private boolean foldersFirst;

        public FileNameComparator(boolean reverse, boolean foldersFirst) {
            this.reverse = reverse;
            this.foldersFirst = foldersFirst;
        }

        public int compare(DuplicateList duplicateList, DuplicateList duplicateList1) {
            if (foldersFirst) {
                if (duplicateList.isFolderList() != duplicateList1.isFolderList()) {
                    if (duplicateList.isFolderList()) {
                        return -1;
                    } else {
                        return 1;
                    }
                }
            }
            String n1 = duplicateList.getFilestamp().getFileName();
            String n2 = duplicateList1.getFilestamp().getFileName();
            if (n1 == null) {
                n1 = "";
            }
            if (n2 == null) {
                n2 = "";
            }
            int value = n1.compareTo(n2);
            return reverse ? -value : value;
        }
    }

    public static final class FileSizeComparator implements Comparator<DuplicateList> {
        private boolean reverse;
        private boolean foldersFirst;

        public FileSizeComparator(boolean reverse, boolean foldersFirst) {
            this.reverse = reverse;
            this.foldersFirst = foldersFirst;
        }

        public int compare(DuplicateList duplicateList, DuplicateList duplicateList1) {
            if (foldersFirst) {
                if (duplicateList.isFolderList() != duplicateList1.isFolderList()) {
                    if (duplicateList.isFolderList()) {
                        return -1;
                    } else {
                        return 1;
                    }
                }
            }
            long l1 = duplicateList.getFilestamp().getFileSize();
            long l2 = duplicateList1.getFilestamp().getFileSize();
            int value = (l1 == l2) ? 0 : (l1 > l2) ? 1 : -1;
            return reverse ? -value : value;
        }
    }

    public static final class DuplicatesCountComparator implements Comparator<DuplicateList> {
        private boolean reverse;
        private boolean foldersFirst;

        public DuplicatesCountComparator(boolean reverse, boolean foldersFirst) {
            this.reverse = reverse;
            this.foldersFirst = foldersFirst;
        }

        public int compare(DuplicateList duplicateList, DuplicateList duplicateList1) {
            if (foldersFirst) {
                if (duplicateList.isFolderList() != duplicateList1.isFolderList()) {
                    if (duplicateList.isFolderList()) {
                        return -1;
                    } else {
                        return 1;
                    }
                }
            }
            int l1 = duplicateList.getFileCount();
            int l2 = duplicateList1.getFileCount();
            int value = (l1 == l2) ? 0 : (l1 > l2) ? 1 : -1;
            return reverse ? -value : value;
        }
    }

    public void addFileRemovedPropertyListener(PropertyChangeListener listener) {
        if (support == null) {
            support = new PropertyChangeSupport(this);
        }
        support.addPropertyChangeListener(FILE_REMOVED, listener);
    }

    public void addFileAddedPropertyListener(PropertyChangeListener listener) {
        if (support == null) {
            support = new PropertyChangeSupport(this);
        }
        support.addPropertyChangeListener(FILE_ADDED, listener);
    }

    public void setFilestamp(Filestamp filestamp) {
        this.filestamp = filestamp;
    }

}
