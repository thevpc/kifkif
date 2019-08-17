package net.vpc.app.kifkif;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.io.Serializable;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import net.vpc.app.kifkif.stamp.DefaultFilestamp;
import net.vpc.common.swings.util.TaskMonitor;
import net.vpc.common.prs.xml.XmlSerializable;
import net.vpc.common.prs.xml.XmlSerializationException;
import net.vpc.common.prs.xml.XmlSerializer;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * Kikif Engine
 * Duplicates are searched using incremental comparaison using one stamp at a time.
 * For example when using name and size filestamps, all files having the same name are groupes
 * toghether. than, singleton groups are ignored. The step after iterates the same operation
 * in each of the remaining groups but using the second filestamp stamp so that we find
 * files having both names and sises the same. at so on.
 * <BR>User: taha
 * <BR>Date: 5 dec. 2004
 * <BR>Time: 17:18:45
 */
public class KifKif implements Serializable, XmlSerializable,Cloneable {

    private FilestampFilterList filestampFilterList;
    private FileContentComparator fileContentComparator;

    private FilestampFilterList folderstampFilterList;
    private FileContentComparator folderContentComparator;
    private FileFilter globalFileFilter;


    private List<FileSet> includedFileSet = new ArrayList<FileSet>();


    private int diffFileMode = FileDiffFactory.UNKNOWN;
    private int diffFolderMode = FileDiffFactory.UNKNOWN;


    private boolean caseInsensitiveNames = true;
    private boolean showFolderDuplicates = true;
    private boolean showFileDuplicates = true;


    private transient Hashtable<Filestamp, DuplicateList> tempFileDuplicatesMap = new Hashtable<Filestamp, DuplicateList>();
    private transient Hashtable<File, Filestamp> tempFileToStampMap = new Hashtable<File, Filestamp>();
    private transient Hashtable<Filestamp, DuplicateList> tempFolderDuplicatesMap = new Hashtable<Filestamp, DuplicateList>();
    private transient Hashtable<File, Filestamp> tempFolderToStampMap = new Hashtable<File, Filestamp>();
    private transient SearchStatistics tempStatistics = new SearchStatistics();

    /**
     * Simple Sonstructor
     * No initialization done.
     */
    public KifKif() {

    }

    /**
     * simple way for cofiguring search options
     *
     * @param fileMode   is one of the constants in FileDiffFactory
     * @param folderMode is one of the constants in FileDiffFactory
     */
    public KifKif(int fileMode, int folderMode) {
        setDiffFileMode(fileMode);
        setDiffFolderMode(folderMode);
    }

    /**
     * @param filestampFilterList     filstamp list for files filtering
     * @param fileContentComparator   file content comparator
     * @param folderstampFilterList   filstamp list for folders filtering
     * @param folderContentComparator folder conent comparator
     */
    public KifKif(FilestampFilterList filestampFilterList, FileContentComparator fileContentComparator, FilestampFilterList folderstampFilterList, FileContentComparator folderContentComparator) {
        this.filestampFilterList = filestampFilterList;
        this.fileContentComparator = fileContentComparator;
        this.folderstampFilterList = folderstampFilterList;
        this.folderContentComparator = folderContentComparator;
    }

    /**
     * add some Folder (or even a file) to the list of files to chek.
     * When adding a Folder, all subfolders and sub files are added (recursive)
     *
     * @param fileSet
     */
    public void addIncludedFileSet(FileSet fileSet) {
        if (fileSet != null && !includedFileSet.contains(fileSet)) {
            includedFileSet.add(fileSet);
        }
    }

    /**
     * remove already selected file
     *
     * @param index
     */
    public void removeIncludedFileSet(int index) {
        includedFileSet.remove(index);
    }

    /**
     * remove already selected file
     *
     * @param file
     */
    public void removeIncludedFileSet(FileSet file) {
        includedFileSet.remove(file);
    }

    /**
     * returns all selected files
     *
     * @return included file sets
     */
    public FileSet[] getIncludedFileSets() {
        return includedFileSet.toArray(new FileSet[includedFileSet.size()]);
    }

    /**
     * returns all selected files
     *
     * @return FileSet at the specified index
     */
    public FileSet getIncludedFileSet(int index) {
        return includedFileSet.get(index);
    }

    /**
     * returns all selected files
     *
     * @return fileSets count
     */
    public int getIncludedFileSetsCount() {
        return includedFileSet.size();
    }

    /**
     * returns all selected files
     *
     */
    public void clearIncludedFileSets() {
        includedFileSet.clear();
    }

    /**
     * performs first filtering and creates first groups
     *
     * @param file
     */
    private void registerFile(File file) {
//        try {
//            file = file.getCanonicalFile();
//        } catch (IOException e) {
//        }
        if (file.isFile()) {
            if (tempFileToStampMap.containsKey(file)) {
                return;
            }
//            System.out.println("register "+file);
            tempStatistics.sourceFilesCount++;
            Filestamp filestamp = filestampFilterList.getNextFilterSig(file, null, 0, this);
            DuplicateList duplicateList = tempFileDuplicatesMap.get(filestamp);
            if (duplicateList == null) {
                duplicateList = new DuplicateList(filestamp);
                tempFileDuplicatesMap.put(filestamp, duplicateList);
            }
            duplicateList.addFile(file);
            tempFileToStampMap.put(file, filestamp);
        } else if (isFindFolderDuplicates() && file.isDirectory()) {
            if (tempFolderToStampMap.containsKey(file)) {
                return;
            }
//            System.out.println("register "+file);
            tempStatistics.sourceFoldersCount++;
            if (folderstampFilterList != null) {
                Filestamp folderStamp = folderstampFilterList.getNextFilterSig(file, null, 0, this);
                DuplicateList duplicateList = tempFolderDuplicatesMap.get(folderStamp);
                if (duplicateList == null) {
                    duplicateList = new DuplicateList(folderStamp);
                    tempFolderDuplicatesMap.put(folderStamp, duplicateList);
                }
                duplicateList.addFile(file);
                tempFolderToStampMap.put(file, folderStamp);
            }
        }
    }

    /**
     * remove folder that has no twin
     *
     * @param f
     */
    private void unregisterFolder(File f) {
        //File folder = f;
//        log("unregisterFolder?(" + folder + ")", true);
        //f = f.getCanonicalFile();
        while (f != null) {
            Filestamp foldeStamp = tempFolderToStampMap.get(f);
            if (foldeStamp != null) {
                tempFolderToStampMap.remove(f);
                DuplicateList duplicateList = tempFolderDuplicatesMap.get(foldeStamp);
                if (duplicateList != null) {
                    duplicateList.removeFile(f);
                    //                log("unregisterFolder?(" + folder + ") -> " + f, true);
                    if (duplicateList.getFileCount() == 0) {
                        tempFolderDuplicatesMap.remove(foldeStamp);
                    }
                }
                f = f.getParentFile();
            } else {
                f = null;
            }
        }
//        System.out.println("unregisterFolder?("+f+") out");
    }

    /**
     * remove all duplicate lists that does not contain more than a single file
     */
    private void removeSingletons() {
        for (Iterator<DuplicateList> i = tempFileDuplicatesMap.values().iterator(); i.hasNext();) {
            DuplicateList duplicateList = i.next();
            int c = duplicateList.getFileCount();
            if (c == 1) {
                File f = duplicateList.getFile(0);
//                log("removeSingletonFile(" + f + ")", true);
                i.remove();
                unregisterFolder(f.getParentFile());
            } else if (c == 0) {
                System.err.println("problem removeSingletons<tempFileDuplicatesMap> : " + duplicateList.getFilestamp());
            }
        }
        int count = -1;
        while (true) {
            ArrayList<File> toRemove = new ArrayList<File>();
            for (DuplicateList duplicateList : tempFolderDuplicatesMap.values()) {
                int c = duplicateList.getFileCount();
                if (c == 1) {
                    File f = duplicateList.getFile(0);
                    toRemove.add(f);
                }
            }
            int newCount = toRemove.size();
            if (newCount > 0 && newCount != count) {
                count = newCount;
                for (File file : toRemove) {
                    unregisterFolder(file);
                }
            } else if (newCount == count) {
                System.out.println("Problem");
                break;
            } else {
                break;
            }
        }
    }

    /**
     * when looking for file duplicates, ignore file duplicates
     * if they are already included in some duplcate folders
     *
     * @throws java.io.IOException
     */
    private void removeExpandedFiles() throws IOException {

        for (Iterator<DuplicateList> i = tempFolderDuplicatesMap.values().iterator(); i.hasNext();) {
            DuplicateList dsimilitude = i.next();
            for (Iterator<File> j = dsimilitude.getFiles().iterator(); j.hasNext();) {
                File folder = j.next();
                boolean remove = false;
                for (File parentFolder : tempFolderToStampMap.keySet()) {
                    if (
                            folder.getCanonicalPath().startsWith(parentFolder.getCanonicalPath() + "/")
                                    ||
                                    folder.getCanonicalPath().startsWith(parentFolder.getCanonicalPath() + "\\")
                            ) {
                        remove = true;
                        break;
                    }
                }
                if (remove) {
                    tempFolderToStampMap.remove(folder);
                    j.remove();
                }

            }
            if (dsimilitude.getFileCount() == 0) {
                i.remove();
            }
        }
        for (DuplicateList folderDuplicates : tempFolderDuplicatesMap.values()) {
            for (File folder : folderDuplicates.getFiles()) {
                for (Iterator<DuplicateList> s = tempFileDuplicatesMap.values().iterator(); s.hasNext();) {
                    DuplicateList fileDuplicates = s.next();
//                    boolean isIncludedInCurrentFolder = true;
                    for (Iterator<File> k = fileDuplicates.getFiles().iterator(); k.hasNext();) {
                        File file = k.next();
                        if (
                                file.getCanonicalPath().startsWith(folder.getCanonicalPath() + "/")
                                        ||
                                        file.getCanonicalPath().startsWith(folder.getCanonicalPath() + "\\")
                                ) {
                            k.remove();
                            tempFileToStampMap.remove(file);
//                            isIncludedInCurrentFolder = true;
                        }
                    }
                    if (fileDuplicates.getFileCount() == 0) {
                        s.remove();
                    }
                }
            }
        }

    }

    /**
     * searchs for duplicates in the selected files according to the specified filters and compaators
     *
     * @return list of duplicates
     */
    public SearchData findFileDuplicates() {
        return findDuplicates(null);
    }

    /**
     * searchs for duplicates in the selected files according to the specified filters and compaators
     *
     * @param taskMonitor : the progress monitor
     * @return list of duplicates
     */
    public SearchData findDuplicates(TaskMonitor taskMonitor) {
        if (taskMonitor == null) {
            taskMonitor = TaskMonitor.NONE;
        }
        tempFileDuplicatesMap = new Hashtable<Filestamp, DuplicateList>();
        tempFileToStampMap = new Hashtable<File, Filestamp>();
        tempFolderDuplicatesMap = new Hashtable<Filestamp, DuplicateList>();
        tempFolderToStampMap = new Hashtable<File, Filestamp>();
        tempStatistics = new SearchStatistics();

        final int FILE_SET = 0;
        final int FILE_FILTER_NAME = 1;
        final int FILE_FILTER_INDEX = 2;
        final int FOLDER_FILTER_NAME = 3;
        final int FOLDER_FILTER_INDEX = 4;
        final int INIT_FILES_COUNT = 5;
        final int INIT_FOLDERS_COUNT = 6;
        final int FILES_COUNT = 7;
        final int FOLDERS_COUNT = 8;
        final int DUP_FILES_COUNT = 9;
        final int DUP_FOLDERS_COUNT = 10;
        final int INDEX = 11;
        final Object[] PROGRESS_PARAMS = new Object[15];
        try {
            taskMonitor.setMax(includedFileSet.size()
                    + (filestampFilterList == null ? 0 : filestampFilterList.getMaxLevels() == 0 ? 0 : (filestampFilterList.getMaxLevels() - 1))
                    + (folderstampFilterList == null ? 0 : folderstampFilterList.getMaxLevels() == 0 ? 0 : (folderstampFilterList.getMaxLevels() - 1))
                    + (fileContentComparator == null ? 0 : 1)
                    + (folderContentComparator == null ? 0 : 1));
            for (FileSet fileSet : includedFileSet) {
                PROGRESS_PARAMS[FILE_SET] = fileSet;
                PROGRESS_PARAMS[FILE_FILTER_NAME] = (filestampFilterList == null ? "" : String.valueOf(filestampFilterList.getFilestampFilter(0)));
                PROGRESS_PARAMS[FILE_FILTER_INDEX] = 0;
                PROGRESS_PARAMS[FOLDER_FILTER_NAME] = (folderstampFilterList == null ? "" : String.valueOf(folderstampFilterList.getFilestampFilter(0)));
                PROGRESS_PARAMS[FOLDER_FILTER_INDEX] = 0;

                PROGRESS_PARAMS[INIT_FILES_COUNT] = tempStatistics.sourceFilesCount;
                PROGRESS_PARAMS[INIT_FOLDERS_COUNT] = tempStatistics.sourceFoldersCount;
                taskMonitor.next("Init", PROGRESS_PARAMS);
//                System.out.println(">> init.index "+taskMonitor.getIndex());
                for (Iterator<File> i = fileSet.iterate(this); i.hasNext();) {
                    File file = i.next();
                    PROGRESS_PARAMS[INIT_FILES_COUNT] = tempStatistics.sourceFilesCount;
                    PROGRESS_PARAMS[INIT_FOLDERS_COUNT] = tempStatistics.sourceFoldersCount;
                    taskMonitor.progress("InitItem", PROGRESS_PARAMS);
                    registerFile(file);
                }
            }
            if (tempStatistics.sourceFilesCount == 0 && tempStatistics.sourceFoldersCount == 0) {
                throw new IllegalArgumentException("Not Valid File set found");
            }
            int max = filestampFilterList.getMaxLevels();
            if (max < 0) {
                throw new IllegalArgumentException("Not Valid File stamp found");
            }
            removeSingletons();
//            fileToIdTable = new Hashtable<File, Filestamp>();
//            folderToIdTable = new Hashtable<File, Filestamp>();
            for (int d = 1; d < max; d++) {
                Hashtable<Filestamp, DuplicateList> newHashtable = new Hashtable<Filestamp, DuplicateList>();
                PROGRESS_PARAMS[FILE_FILTER_INDEX] = d;
                PROGRESS_PARAMS[FILE_FILTER_NAME] = filestampFilterList.getFilestampFilter(d).toString();
                PROGRESS_PARAMS[FILES_COUNT] = tempFileToStampMap.size();
                PROGRESS_PARAMS[FOLDERS_COUNT] = tempFolderToStampMap.size();
                PROGRESS_PARAMS[DUP_FILES_COUNT] = tempFileDuplicatesMap.size();
                PROGRESS_PARAMS[DUP_FOLDERS_COUNT] = tempFolderDuplicatesMap.size();
                taskMonitor.stepInto(tempFileDuplicatesMap.size(), "Filestamp", PROGRESS_PARAMS);
                int index = 0;
                for (DuplicateList duplicateList : tempFileDuplicatesMap.values()) {
                    PROGRESS_PARAMS[FILES_COUNT] = tempFileToStampMap.size();
                    PROGRESS_PARAMS[FOLDERS_COUNT] = tempFolderToStampMap.size();
                    PROGRESS_PARAMS[DUP_FILES_COUNT] = tempFileDuplicatesMap.size();
                    PROGRESS_PARAMS[DUP_FOLDERS_COUNT] = tempFolderDuplicatesMap.size();
                    PROGRESS_PARAMS[INDEX] = ++index;
                    taskMonitor.next("FilestampItem", PROGRESS_PARAMS);
                    for (File file : duplicateList.getFiles()) {
                        Filestamp filestamp = filestampFilterList.getNextFilterSig(file, duplicateList.getFilestamp(), d, this);
                        DuplicateList newDuplicateList = newHashtable.get(filestamp);
                        if (newDuplicateList == null) {
                            newDuplicateList = new DuplicateList(filestamp);
                            newHashtable.put(filestamp, newDuplicateList);
                        }
                        tempFileToStampMap.put(file, filestamp);
                        newDuplicateList.addFile(file);
                    }
                }
                tempFileDuplicatesMap = newHashtable;
                removeSingletons();
                taskMonitor.stepOut();
//                System.out.println(">> fs.index "+taskMonitor.getIndex());
            }
            if (fileContentComparator != null) {
                ArrayList<DuplicateList> allNewSimiltudes = new ArrayList<DuplicateList>();
                PROGRESS_PARAMS[FILES_COUNT] = tempFileToStampMap.size();
                PROGRESS_PARAMS[FOLDERS_COUNT] = tempFolderToStampMap.size();
                PROGRESS_PARAMS[DUP_FILES_COUNT] = tempFileDuplicatesMap.size();
                PROGRESS_PARAMS[DUP_FOLDERS_COUNT] = tempFolderDuplicatesMap.size();
                taskMonitor.stepInto(tempFileDuplicatesMap.size(), "FileContent", PROGRESS_PARAMS);
                int index = 0;
                for (Iterator<DuplicateList> it = tempFileDuplicatesMap.values().iterator(); it.hasNext();) {
                    PROGRESS_PARAMS[FILES_COUNT] = tempFileToStampMap.size();
                    PROGRESS_PARAMS[FOLDERS_COUNT] = tempFolderToStampMap.size();
                    PROGRESS_PARAMS[DUP_FILES_COUNT] = tempFileDuplicatesMap.size();
                    PROGRESS_PARAMS[DUP_FOLDERS_COUNT] = tempFolderDuplicatesMap.size();
                    PROGRESS_PARAMS[INDEX] = ++index;
                    taskMonitor.next("FileContentItem", PROGRESS_PARAMS);
                    DuplicateList duplicateList = it.next();
//                    System.out.println(">>>>> "+duplicateList);
                    File[] files = duplicateList.getFiles().toArray(new File[duplicateList.getFileCount()]);
                    ArrayList<File> filesCopies = new ArrayList<File>();
                    Hashtable<File, ArrayList<File>> filesLeaders = new Hashtable<File, ArrayList<File>>();

                    //Hashtable<File, ArrayList<File>> fileHash = new Hashtable<File, ArrayList<File>>();
                    for (File file : files) {
                        boolean found = false;
                        for (Map.Entry<File, ArrayList<File>> entry : filesLeaders.entrySet()) {
                            File leader = entry.getKey();
                            if (
                                    !filesCopies.contains(file)
                                            && fileContentComparator.compareFileContent(this, file, leader)
                                    ) {
                                ArrayList<File> leaderTwins = entry.getValue();
                                leaderTwins.add(file);
                                filesCopies.add(file);
//                                System.out.println(">twin "+files[i]);
                                found = true;
                            }
                        }
                        if (!found) {
                            ArrayList<File> leaderTwins = new ArrayList<File>();
                            leaderTwins.add(file);
                            filesLeaders.put(file, leaderTwins);
//                            System.out.println(">new "+files[i]);
                        } else {
//                            System.out.println(">already "+files[i]);
                        }
                    }
                    //noinspection UnusedAssignment
                    filesCopies = null;
                    // HashSet to remove Duplicates
                    if (filesLeaders.size() > 1) {
//                        System.out.println(">remove "+duplicateList);
                        it.remove();
                        int x = 1;
                        for (Map.Entry<File, ArrayList<File>> entry : filesLeaders.entrySet()) {
                            DuplicateList newDuplicateList = new DuplicateList(duplicateList.getFilestamp().combine(new DefaultFilestamp("<" + (x + 1) + ">")));
                            newDuplicateList.addAllFiles(entry.getValue());
                            allNewSimiltudes.add(newDuplicateList);
                        }
                    }
                }
                for (DuplicateList duplicateList : allNewSimiltudes) {
//                    System.out.println(">add "+duplicateList);
                    tempFileDuplicatesMap.put(duplicateList.getFilestamp(), duplicateList);
                    for (File file : duplicateList.getFiles()) {
                        tempFileToStampMap.put(file, duplicateList.getFilestamp());
                    }
                }
                removeSingletons();
                taskMonitor.stepOut();
//                System.out.println(">> fc.index "+taskMonitor.getIndex());
            }

            // ---------------  FOLDERS  -----------------------
            if (folderstampFilterList != null) {
                int dmax = folderstampFilterList.getMaxLevels();
                for (int d = 1; d < dmax; d++) {
                    //taskMonitor.progress("Folderstamp",new Object[]{d});
                    Hashtable<Filestamp, DuplicateList> newHashtable = new Hashtable<Filestamp, DuplicateList>();
                    PROGRESS_PARAMS[FOLDER_FILTER_INDEX] = d;
                    FilestampFilter filestampFilter = folderstampFilterList.getFilestampFilter(d);
                    PROGRESS_PARAMS[FOLDER_FILTER_NAME] = filestampFilter==null?"":filestampFilter.toString();
                    PROGRESS_PARAMS[FILES_COUNT] = tempFileToStampMap.size();
                    PROGRESS_PARAMS[FOLDERS_COUNT] = tempFolderToStampMap.size();
                    PROGRESS_PARAMS[DUP_FILES_COUNT] = tempFileDuplicatesMap.size();
                    PROGRESS_PARAMS[DUP_FOLDERS_COUNT] = tempFolderDuplicatesMap.size();
                    taskMonitor.stepInto(tempFolderDuplicatesMap.size(), "Folderstamp", PROGRESS_PARAMS);
                    int index = 0;
                    for (DuplicateList duplicateList : tempFolderDuplicatesMap.values()) {
                        PROGRESS_PARAMS[FILES_COUNT] = tempFileToStampMap.size();
                        PROGRESS_PARAMS[FOLDERS_COUNT] = tempFolderToStampMap.size();
                        PROGRESS_PARAMS[DUP_FILES_COUNT] = tempFileDuplicatesMap.size();
                        PROGRESS_PARAMS[DUP_FOLDERS_COUNT] = tempFolderDuplicatesMap.size();
                        PROGRESS_PARAMS[INDEX] = ++index;
                        taskMonitor.next("FolderstampItem", PROGRESS_PARAMS);
                        for (File file : duplicateList.getFiles()) {
                            Filestamp filestamp = folderstampFilterList.getNextFilterSig(file, duplicateList.getFilestamp(), d, this);
                            DuplicateList newDuplicateList = newHashtable.get(filestamp);
                            if (newDuplicateList == null) {
                                newDuplicateList = new DuplicateList(filestamp);
                                newHashtable.put(filestamp, newDuplicateList);
                            }
                            tempFolderToStampMap.put(file, filestamp);
                            newDuplicateList.addFile(file);
                        }
                    }
                    tempFolderDuplicatesMap = newHashtable;
                    removeSingletons();
                    taskMonitor.stepOut();
//                    System.out.println(">> ds.index "+taskMonitor.getIndex());
                }
            }

            if (folderContentComparator != null) {
                ArrayList<DuplicateList> allNewSimiltudes = new ArrayList<DuplicateList>();
                PROGRESS_PARAMS[FILES_COUNT] = tempFileToStampMap.size();
                PROGRESS_PARAMS[FOLDERS_COUNT] = tempFolderToStampMap.size();
                PROGRESS_PARAMS[DUP_FILES_COUNT] = tempFileDuplicatesMap.size();
                PROGRESS_PARAMS[DUP_FOLDERS_COUNT] = tempFolderDuplicatesMap.size();
                taskMonitor.stepInto(tempFolderDuplicatesMap.size(), "FolderContent", PROGRESS_PARAMS);
                int index = 0;
                for (Iterator<DuplicateList> it = tempFolderDuplicatesMap.values().iterator(); it.hasNext();) {
                    PROGRESS_PARAMS[FILES_COUNT] = tempFileToStampMap.size();
                    PROGRESS_PARAMS[FOLDERS_COUNT] = tempFolderToStampMap.size();
                    PROGRESS_PARAMS[DUP_FILES_COUNT] = tempFileDuplicatesMap.size();
                    PROGRESS_PARAMS[DUP_FOLDERS_COUNT] = tempFolderDuplicatesMap.size();
                    PROGRESS_PARAMS[INDEX] = ++index;
                    taskMonitor.next("FolderContentItem", PROGRESS_PARAMS);
                    DuplicateList duplicateList = it.next();
                    File[] files = duplicateList.getFiles().toArray(new File[duplicateList.getFileCount()]);
                    ArrayList<File> filesCopies = new ArrayList<File>();
                    Hashtable<File, ArrayList<File>> filesLeaders = new Hashtable<File, ArrayList<File>>();
                    for (File file : files) {
                        boolean found = false;
                        for (Map.Entry<File, ArrayList<File>> entry : filesLeaders.entrySet()) {
                            File leader = entry.getKey();
                            if (
                                    !filesCopies.contains(file)
                                            && folderContentComparator.compareFileContent(this, file, leader)
                                    ) {
                                ArrayList<File> leaderTwins = entry.getValue();
                                leaderTwins.add(file);
                                filesCopies.add(file);
//                                System.out.println(">twin "+files[i]);
                                found = true;
                            }
                        }
                        if (!found) {
                            ArrayList<File> leaderTwins = new ArrayList<File>();
                            leaderTwins.add(file);
                            filesLeaders.put(file, leaderTwins);
//                            System.out.println(">new "+files[i]);
                        } else {
//                            System.out.println(">already "+files[i]);
                        }
                    }
                    //noinspection UnusedAssignment
                    filesCopies = null;
                    // HashSet to remove Duplicates
                    if (filesLeaders.size() > 1) {
//                        System.out.println(">remove "+duplicateList);
                        it.remove();
                        int x = 1;
                        for (Map.Entry<File, ArrayList<File>> entry : filesLeaders.entrySet()) {
                            DuplicateList newDuplicateList = new DuplicateList(duplicateList.getFilestamp().combine(new DefaultFilestamp("<" + (x + 1) + ">")));
                            newDuplicateList.addAllFiles(entry.getValue());
                            allNewSimiltudes.add(newDuplicateList);
                        }
                    }
                }
                for (DuplicateList duplicateList : allNewSimiltudes) {
                    tempFolderDuplicatesMap.put(duplicateList.getFilestamp(), duplicateList);
                    for (File file : duplicateList.getFiles()) {
                        tempFolderToStampMap.put(file, duplicateList.getFilestamp());
                    }
                }
                removeSingletons();
                removeExpandedFiles();
                taskMonitor.stepOut();
//                System.out.println(">> dc.index "+taskMonitor.getIndex());
            }
            removeSingletons();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }


        ArrayList<DuplicateList> v = new ArrayList<DuplicateList>();
        if (isFindFolderDuplicates()) {
            v.addAll((tempFolderDuplicatesMap.values()));
        }
        if (isFindFileDuplicates()) {
            v.addAll((tempFileDuplicatesMap.values()));
        }
        taskMonitor.progress("Final", PROGRESS_PARAMS);
        //System.out.println(">> final.index "+taskMonitor.getIndex());
        tempStatistics.endTime = System.currentTimeMillis();
        SearchData searchData = new SearchData(v);
        searchData.setStatistics(tempStatistics);
        searchData.setKifkif(this);

        tempFileDuplicatesMap = null;
        tempFileToStampMap = null;
        tempFolderDuplicatesMap = null;
        tempFolderToStampMap = null;
        tempStatistics = null;

        return searchData;
    }


    /**
     * @param f1
     * @param f2
     * @return true is f1 and f2 are known (according to the actual search process) to be similar
     */
    public boolean quickCompareFiles(File f1, File f2) {
        if (f1.isFile() && f2.isFile()) {
            Filestamp fid1 = tempFileToStampMap.get(f1);
            Filestamp fid2 = tempFileToStampMap.get(f2);
            return fid1 != null && fid1.equals(fid2);
        } else if (f1.isDirectory() && f2.isDirectory()) {
            Filestamp fid1 = tempFolderToStampMap.get(f1);
            Filestamp fid2 = tempFolderToStampMap.get(f2);
            return fid1 != null && fid1.equals(fid2);
        } else {
            return false;
        }
    }

//    public Collection<DuplicateList> dumpTextFileDuplicates(PrintStream out, TaskMonitor taskMonitor) {
//        long start = System.currentTimeMillis();
//        Collection<DuplicateList> similitudes = findDuplicates(taskMonitor);
//        long end = System.currentTimeMillis();
//        double minutes = (end - start) / 60000.0;
//        int i = 1;
//        int fileDuplicates = 0;
//        int folderDuplicates = 0;
//        int fileGroups = 0;
//        int folderGroups = 0;
//        for (DuplicateList duplicateList : similitudes) {
//            boolean fileGroup = duplicateList.getFile(0).isFile();
//            out.println((fileGroup ? "File" : "Folder") + " Group " + i + " : " + (duplicateList.getFileCount() - 1) + " duplicates");
//            if (fileGroup) {
//                fileGroups++;
//            } else {
//                folderGroups++;
//            }
//            if (duplicateList.getFileCount() == 1) {
//                out.println("\t" + KifKif.str(duplicateList.getFile(0)) + " **");
//                if (fileGroup) {
//                    fileDuplicates++;
//                } else {
//                    folderDuplicates++;
//                }
//            } else {
//                boolean first = true;
//                for (File file : duplicateList.getFiles()) {
//                    if (first) {
//                        out.println("\t" + KifKif.str(file));
//                        first = false;
//                    } else {
//                        if (fileGroup) {
//                            fileDuplicates++;
//                        } else {
//                            folderDuplicates++;
//                        }
//                        out.println("\t" + KifKif.str(file) + " *");
//                    }
//                }
//            }
//            i++;
//        }
//        out.printf("-------------------\n" +
//                "STATISTICS:\n" +
//                " Exec time : %s mn\n" +
//                " Source : %d folder(s) and %d file(s)\n" +
//                " Folder Groups : %d ; File Groups : %d\n" +
//                " Total Duplicates  : %d\n" +
//                " Folder Duplicates : %d\n" +
//                " File Duplicates   : %d\n" +
//                "\n" +
//                "N.B. files with (*) are duplicate and could safely be deleted\n" +
//                "N.B. files with (**) are already included in duplicate folders and could safely be deleted",
//                String.valueOf(minutes), sourceFoldersCount, sourceFilesCount, folderGroups, fileGroups, (fileDuplicates + folderDuplicates), folderDuplicates, fileDuplicates);
//        return similitudes;
//    }

    /**
     * file comparator used for comparing folder contents
     *
     * @return file comparator used for comparing folder contents
     */
    public FileContentComparator getFolderContentComparator() {
        return folderContentComparator;
    }

    /**
     * updates the file comparator used for comparing folder contents
     *
     * @param folderContentComparator
     */
    public void setFolderContentComparator(FileContentComparator folderContentComparator) {
        diffFileMode = FileDiffFactory.UNKNOWN;
        diffFolderMode = FileDiffFactory.UNKNOWN;
        this.folderContentComparator = folderContentComparator;
    }

    /**
     * list of thefilestamps used for comparing folders
     *
     * @return list of thefilestamps used for comparing folders
     */
    public FilestampFilterList getFolderstampFilterList() {
        return folderstampFilterList;
    }

    /**
     * updates list of thefilestamps used for comparing folder
     *
     * @param folderstampFilterList
     */
    public void setFolderstampFilterList(FilestampFilterList folderstampFilterList) {
        diffFileMode = FileDiffFactory.UNKNOWN;
        diffFolderMode = FileDiffFactory.UNKNOWN;
        this.folderstampFilterList = folderstampFilterList;
    }

    /**
     * file comparator used for comparing file contents
     */
    public FileContentComparator getFileContentComparator() {
        return fileContentComparator;
    }

    /**
     * updates the file comparator used for comparing file contents
     *
     * @param fileContentComparator
     */
    public void setFileContentComparator(FileContentComparator fileContentComparator) {
        diffFileMode = FileDiffFactory.UNKNOWN;
        diffFolderMode = FileDiffFactory.UNKNOWN;
        this.fileContentComparator = fileContentComparator;
    }

    /**
     * list of thefilestamps used for comparing files
     *
     * @return list of thefilestamps used for comparing files
     */
    public FilestampFilterList getFilestampFilterList() {
        return filestampFilterList;
    }

    public void setFilestampFilterList(FilestampFilterList filestampFilterList) {
        diffFileMode = FileDiffFactory.UNKNOWN;
        diffFolderMode = FileDiffFactory.UNKNOWN;
        this.filestampFilterList = filestampFilterList;
    }

    /**
     * the used Diff mode or FileDiffFactory.
     * return UNKNOWN if filters or comparators are selected distinctly
     *
     * @return the used Diff mode or FileDiffFactory
     */
    public int getDiffFileMode() {
        return diffFileMode;
    }

    public int getDiffFolderMode() {
        return diffFolderMode;
    }


    public boolean isFindFolderDuplicates() {
        return showFolderDuplicates && (folderstampFilterList != null || folderContentComparator != null);
    }

    public boolean isFindFileDuplicates() {
        return showFileDuplicates && (filestampFilterList != null || fileContentComparator != null);
    }

    public boolean isShowFolderDuplicates() {
        return showFolderDuplicates;
    }

    public void setShowFolderDuplicates(boolean showFolderDuplicates) {
        this.showFolderDuplicates = showFolderDuplicates;
    }

    public boolean isShowFileDuplicates() {
        return showFileDuplicates;
    }

    public void setShowFileDuplicates(boolean showFileDuplicates) {
        this.showFileDuplicates = showFileDuplicates;
    }

    public boolean isCaseInsensitiveNames() {
        return caseInsensitiveNames;
    }

    public void setCaseInsensitiveNames(boolean caseInsensitiveNames) {
        this.caseInsensitiveNames = caseInsensitiveNames;
    }

    /**
     * updates filters and comparators according to the given mode
     * see FileDiffFactory for available modes
     */
    public void setDiffFileMode(int diffFileMode) {
        this.diffFileMode = diffFileMode;
        if (FileDiffFactory.UNKNOWN != diffFileMode) {
            this.showFileDuplicates = true;
            this.filestampFilterList = FileDiffFactory.createFilestampFilterList(diffFileMode);
            this.fileContentComparator = FileDiffFactory.createFileContentComparator(diffFileMode);
            if (filestampFilterList == null) {
                throw new IllegalStateException("At least a filestamp filter should be set");
            }
        }
    }

    /**
     * updates filters and comparators according to the given mode
     * see FileDiffFactory for available modes
     */
    public void setDiffFolderMode(int diffFolderMode) {
        this.diffFolderMode = diffFolderMode;
        if (FileDiffFactory.UNKNOWN != diffFileMode) {
            this.folderstampFilterList = FileDiffFactory.createFolderstampFilterList(diffFolderMode);
            this.folderContentComparator = FileDiffFactory.createFolderContentComparator(diffFolderMode);
            this.showFolderDuplicates = (folderstampFilterList != null || folderContentComparator != null);
            if (filestampFilterList == null) {
                throw new IllegalStateException("At least a filestamp filter should be set");
            }
        }
    }

    public void storeXmlNode(XmlSerializer serializer, Document doc, Element element) {
        try {
            Field[] declaredFields = getClass().getDeclaredFields();
            for (Field field : declaredFields) {
                if (!Modifier.isTransient(field.getModifiers()) && !Modifier.isStatic(field.getModifiers())) {
                    element.appendChild(serializer.createNode(doc, field.getName(), field.get(this)));
                }
            }
        } catch (IllegalAccessException e) {
            throw new XmlSerializationException(e);
        }
    }

    public void loadXmlNode(XmlSerializer serializer, Element element) {
        try {
            Field[] declaredFields = getClass().getDeclaredFields();
            for (Field field : declaredFields) {
                if (!Modifier.isTransient(field.getModifiers()) && !Modifier.isStatic(field.getModifiers())) {
                    Element node = (Element) element.getElementsByTagName(field.getName()).item(0);
                    field.set(this, serializer.createObject(node));
                }
            }
        } catch (IllegalAccessException e) {
            throw new XmlSerializationException(e);
        }
    }

    public FileFilter getGlobalFileFilter() {
        return globalFileFilter;
    }

    public void setGlobalFileFilter(FileFilter globalFileFilter) {
        this.globalFileFilter = globalFileFilter;
    }

    @Override
    protected KifKif clone(){
        try {
            KifKif o = (KifKif) super.clone();
            return o;
        } catch (CloneNotSupportedException ex) {
            throw new RuntimeException(ex);
        }
    }
    
}
