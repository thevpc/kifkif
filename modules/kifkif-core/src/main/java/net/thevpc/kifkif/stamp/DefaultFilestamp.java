package net.thevpc.kifkif.stamp;



import net.thevpc.kifkif.Filestamp;

/**
 * User: taha
 * Date: 22 dec. 2004
 * Time: 18:56:10
 */
public class DefaultFilestamp implements Filestamp {
    public static final String SEP = "\n";
    private String id;
    private String fileName;
    private long lastModified = -1;
    private long fileSize = -1;

    public DefaultFilestamp() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public DefaultFilestamp(String id) {
        this.id = id;
    }

    public Filestamp combine(Filestamp previousLevelStamp) {
        DefaultFilestamp i = new DefaultFilestamp(id + SEP + previousLevelStamp.toString());
        if (this.getFileSize() >= 0) {
            i.fileSize = this.getFileSize();
        }
        if (this.getFileName() != null) {
            i.fileName = this.getFileName();
        }
        if (this.getLastModified() > 0) {
            i.lastModified = this.getLastModified();
        }

        if (previousLevelStamp.getFileSize() >= 0) {
            i.fileSize = previousLevelStamp.getFileSize();
        }
        if (previousLevelStamp.getFileName() != null) {
            i.fileName = previousLevelStamp.getFileName();
        }
        if (previousLevelStamp.getLastModified() > 0) {
            i.lastModified = previousLevelStamp.getLastModified();
        }
        return i;
    }

    public String toString() {
        return id.replace(SEP, "<SEP>");
    }

    public int hashCode() {
        return id.hashCode();
    }

    public boolean equals(Object obj) {
        return (obj instanceof DefaultFilestamp) && id.equals(((DefaultFilestamp) obj).id);
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public long getFileSize() {
        return fileSize;
    }

    public void setFileSize(long fileSize) {
        this.fileSize = fileSize;
    }

    public long getLastModified() {
        return lastModified;
    }

    public void setLastModified(long lastModified) {
        this.lastModified = lastModified;
    }

}
