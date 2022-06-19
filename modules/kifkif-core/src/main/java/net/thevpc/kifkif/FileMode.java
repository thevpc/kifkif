package net.thevpc.kifkif;

import net.thevpc.nuts.util.NutsEnumSet;

public enum FileMode {
    /**
     * Filestamp for sensitive Filename
     */
    FILE_NAME,

    /**
     * Filestamp for file size
     */
    FILE_EXTENSION,

    /**
     * Filestamp for file size
     */
    FILE_SIZE,


    /**
     * Filesamp for File content (content hashcode), slow
     */
    FILE_STAMP,

    FILE_STAMP_MD5,

    FILE_STAMP_SHA,

    FILE_STAMP_MASK,

    FILE_TIME,
    FILE_TIME_HOUR,
    FILE_TIME_DAY,
    FILE_TIME_WEEK,
    FILE_TIME_MONTH,
    FILE_TIME_YEAR,
    FILE_TIME_MASK,


    /**
     * Folder content comparaison (Files names are compared)
     */
    FILE_CONTENT,

    FILE_CONTENT_I,

    FILE_CONTENT_W,


    FILE_CONTENT_WI,


    FILE_CONTENT_MASK,

    /**
     * Filestamp for sensitive filename
     */

    FOLDER_NAME,
    /**
     * Filestamp for file size (list count)
     */

    FOLDER_SIZE,
    /**
     * Filesamp for Folder list (list string hashcode), slow
     */

    FOLDER_STAMP,
    /**
     * Folder content comparaison (Files names are compared)
     */

    FOLDER_CONTENT,

    /**
     * Filesamp for File lastModified time
     */

    FOLDER_TIME,
    FOLDER_TIME_HOUR,
    FOLDER_TIME_DAY,
    FOLDER_TIME_WEEK,
    FOLDER_TIME_MONTH,
    FOLDER_TIME_YEAR,
    FOLDER_TIME_MASK;

    public NutsEnumSet<FileMode> add(FileMode other) {
        return NutsEnumSet.of(this).add(other);
    }

    public NutsEnumSet<FileMode> addAll(FileMode... other) {
        return NutsEnumSet.of(this).addAll(other);
    }

    public NutsEnumSet<FileMode> addAll(NutsEnumSet<FileMode> other) {
        return NutsEnumSet.of(this).addAll(other);
    }
}
