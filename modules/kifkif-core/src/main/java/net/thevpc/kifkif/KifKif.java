package net.thevpc.kifkif;

import java.io.*;
import java.util.*;

import net.thevpc.kifkif.stamp.DefaultFilestamp;
import net.thevpc.nuts.NSession;
import net.thevpc.nuts.time.NChronometer;
import net.thevpc.nuts.time.NProgressMonitor;
import net.thevpc.nuts.time.NProgressMonitors;
import net.thevpc.nuts.util.NEnumSet;
import net.thevpc.nuts.util.NMsg;

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
public class KifKif implements Serializable, Cloneable {

    private static final int FILE_SET = 0;
    private static final int FILE_FILTER_NAME = 1;
    private static final int FILE_FILTER_INDEX = 2;
    private static final int FOLDER_FILTER_NAME = 3;
    private static final int FOLDER_FILTER_INDEX = 4;
    private static final int INIT_FILES_COUNT = 5;
    private static final int INIT_FOLDERS_COUNT = 6;
    private static final int FILES_COUNT = 7;
    private static final int FOLDERS_COUNT = 8;
    private static final int DUP_FILES_COUNT = 9;
    private static final int DUP_FOLDERS_COUNT = 10;
    private static final int INDEX = 11;

    private FileStampFilterList fileStampFilterList;
    private FileContentComparator fileContentComparator;

    private FileStampFilterList folderStampFilterList;
    private FileContentComparator folderContentComparator;
    private FileFilter globalFileFilter;


    private List<FileSet> includedFileSet = new ArrayList<FileSet>();


    private NEnumSet<FileMode> diffFileMode = NEnumSet.noneOf(FileMode.class);
    private boolean caseInsensitiveNames = true;
    private boolean showFolderDuplicates = true;
    private boolean showFileDuplicates = true;


    private transient Hashtable<Filestamp, DuplicateList> tempFileDuplicatesMap = new Hashtable<Filestamp, DuplicateList>();
    private transient Hashtable<File, Filestamp> tempFileToStampMap = new Hashtable<File, Filestamp>();
    private transient Hashtable<Filestamp, DuplicateList> tempFolderDuplicatesMap = new Hashtable<Filestamp, DuplicateList>();
    private transient Hashtable<File, Filestamp> tempFolderToStampMap = new Hashtable<File, Filestamp>();
    private transient SearchStatistics tempStatistics = new SearchStatistics();

    private SearchData searchData;
    private NProgressMonitors mons;

    /**
     * Simple Constructor
     * No initialization done.
     */
    public KifKif() {
        this.mons = NProgressMonitors.of();
    }

    /**
     * simple way for configuring search options
     *
     * @param fileMode is one of the constants in FileDiffFactory
     */
    public KifKif(FileMode[] fileMode) {
        setFileMode(fileMode);
    }

    /**
     * @param fileStampFilterList     filstamp list for files filtering
     * @param fileContentComparator   file content comparator
     * @param folderStampFilterList   filstamp list for folders filtering
     * @param folderContentComparator folder conent comparator
     */
    public KifKif(FileStampFilterList fileStampFilterList, FileContentComparator fileContentComparator, FileStampFilterList folderStampFilterList, FileContentComparator folderContentComparator) {
        this.fileStampFilterList = fileStampFilterList;
        this.fileContentComparator = fileContentComparator;
        this.folderStampFilterList = folderStampFilterList;
        this.folderContentComparator = folderContentComparator;
    }

    /**
     * add some Folder (or even a file) to the list of files to check.
     * When adding a Folder, all subfolders and sub files are added (recursive)
     *
     * @param fileSet fileSet
     */
    public void addIncludedFileSet(FileSet fileSet) {
        addIncludedFileSet(fileSet, -1);
    }

    public void addExcludedFiles(File[] fileSets) {
        final ArrayList<File> excludedFolders = ((DefaultFileFilter) getGlobalFileFilter()).getExcludedFolders();
        excludedFolders.addAll(Arrays.asList(fileSets));
    }

    public void addIncludedFileSets(FileSet[] fileSets) {
        addIncludedFileSets(fileSets, -1);
    }

    /**
     * add some Folder (or even a file) to the list of files to check.
     * When adding a Folder, all subfolders and sub files are added (recursive)
     *
     * @param fileSet fileSet
     * @param index   index
     */
    public void addIncludedFileSet(FileSet fileSet, int index) {
        if (fileSet != null && !includedFileSet.contains(fileSet)) {
            if (index >= 0 && index < includedFileSet.size()) {
                includedFileSet.add(index, fileSet);
            } else {
                includedFileSet.add(fileSet);
            }
        }
    }

    /**
     * add some Folder (or even a file) to the list of files to check.
     * When adding a Folder, all subfolders and sub files are added (recursive)
     *
     * @param fileSets fileSets
     * @param index    index
     */
    public void addIncludedFileSets(FileSet[] fileSets, int index) {
        if (fileSets != null) {
            fileSets = Arrays.stream(fileSets).filter(x -> x != null && !includedFileSet.contains(x)).toArray(FileSet[]::new);
            if (fileSets.length > 0) {
                if (index >= 0 && index < includedFileSet.size()) {
                    includedFileSet.addAll(index, Arrays.asList(fileSets));
                } else {
                    includedFileSet.addAll(Arrays.asList(fileSets));
                }
            }
        }
    }

    /**
     * remove already selected file
     *
     * @param index index
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
            Filestamp filestamp = fileStampFilterList.getNextFilterSig(file, null, 0, this);
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
            if (folderStampFilterList != null) {
                Filestamp folderStamp = folderStampFilterList.getNextFilterSig(file, null, 0, this);
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
    private void removeSingletons(NProgressMonitor progressMonitor) {
        chrono("removeSingletons", () -> {
            NProgressMonitor[] split = progressMonitor.split(1, 1);
            NProgressMonitor s1 = split[0].incremental(tempFileDuplicatesMap.size());
            for (Iterator<DuplicateList> i = tempFileDuplicatesMap.values().iterator(); i.hasNext(); ) {
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
                s1.inc();
            }
            s1.complete();

            NProgressMonitor s2 = split[1].incremental(tempFolderDuplicatesMap.size());
            int count = -1;
            while (true) {
                s2.setProgress(0);
                ArrayList<File> toRemove = new ArrayList<File>();
                for (DuplicateList duplicateList : tempFolderDuplicatesMap.values()) {
                    int c = duplicateList.getFileCount();
                    if (c == 1) {
                        File f = duplicateList.getFile(0);
                        toRemove.add(f);
                    }
                    s2.inc();
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
            s2.complete();
            progressMonitor.complete();
        });
    }


    /**
     * when looking for file duplicates, ignore file duplicates
     * if they are already included in some duplcate folders
     *
     */
    private void removeExpandedFiles(NProgressMonitor progressMonitor) {
        FileParentLookup fl = new FileParentLookup(tempFolderToStampMap.keySet());
        NProgressMonitor[] split = progressMonitor.split(5, 10);
        NProgressMonitor s1 = split[0].incremental(tempFolderDuplicatesMap.size());
        for (Iterator<DuplicateList> i = tempFolderDuplicatesMap.values().iterator(); i.hasNext(); ) {
            DuplicateList folderDuplicates = i.next();
            for (Iterator<File> j = folderDuplicates.getFiles().iterator(); j.hasNext(); ) {
                File folder = j.next();
                if (fl.containsParentOf(folder)) {
                    tempFolderToStampMap.remove(folder);
                    j.remove();
                }
            }
            if (folderDuplicates.getFileCount() == 0) {
                i.remove();
            }
            s1.inc();
        }
        s1.complete();

        NProgressMonitor s2 = split[1].incremental(tempFolderDuplicatesMap.size() * tempFileDuplicatesMap.size());
        for (Iterator<DuplicateList> i = tempFolderDuplicatesMap.values().iterator(); i.hasNext(); ) {
            DuplicateList folderDuplicates = i.next();
            fl = new FileParentLookup(folderDuplicates.getFiles());
            for (Iterator<DuplicateList> s = tempFileDuplicatesMap.values().iterator(); s.hasNext(); ) {
                DuplicateList fileDuplicates = s.next();
                for (Iterator<File> k = fileDuplicates.getFiles().iterator(); k.hasNext(); ) {
                    File file = k.next();
                    if (fl.containsParentOf(file)) {
                        k.remove();
                        tempFileToStampMap.remove(file);
                    }
                }
                if (fileDuplicates.getFileCount() == 0) {
                    s.remove();
                }
                s2.inc();
            }
        }
        s2.complete();
    }

    /**
     * searches for duplicates in the selected files according to the specified filters and compaators
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
    public SearchData findDuplicates(NProgressMonitor taskMonitor) {
        taskMonitor = NProgressMonitors.of().of(taskMonitor);
        tempFileDuplicatesMap = new Hashtable<Filestamp, DuplicateList>();
        tempFileToStampMap = new Hashtable<File, Filestamp>();
        tempFolderDuplicatesMap = new Hashtable<Filestamp, DuplicateList>();
        tempFolderToStampMap = new Hashtable<File, Filestamp>();
        tempStatistics = new SearchStatistics();

        final Object[] PROGRESS_PARAMS = new Object[15];
        taskMonitor.reset();
        taskMonitor.start();
        NProgressMonitor[] mons = taskMonitor.split(1, 3, 6, 3, 3, 1);
        processInit(PROGRESS_PARAMS, mons[0]);
        processFileTimestamps(PROGRESS_PARAMS, mons[1]);
        processFileContents(PROGRESS_PARAMS, mons[2]);
        processFolderTimestamps(PROGRESS_PARAMS, mons[3]);
        processFolderContents(PROGRESS_PARAMS, mons[4]);
        processFinalize(PROGRESS_PARAMS, mons[5]);
        taskMonitor.complete();
        return searchData;
    }

    private void chrono(String name, Runnable r) {
        NOut.println(NMsg.ofC("start %s", name));
        NChronometer c = NChronometer.startNow();
        r.run();
        c.stop();
        NOut.println(NMsg.ofC("%s %s", name, c));
    }

    private void processFinalize(Object[] progress_params, NProgressMonitor taskMonitor) {
        chrono("processFinalize", () -> {
            removeSingletons(taskMonitor);
            ArrayList<DuplicateList> v = new ArrayList<DuplicateList>();
            if (isFindFolderDuplicates()) {
                v.addAll((tempFolderDuplicatesMap.values()));
            }
            if (isFindFileDuplicates()) {
                v.addAll((tempFileDuplicatesMap.values()));
            }
            tempStatistics.endTimeMillis = System.currentTimeMillis();
            searchData = new SearchData(v);
            searchData.setStatistics(tempStatistics);
            searchData.setKifkif(this);
            tempFileDuplicatesMap = null;
            tempFileToStampMap = null;
            tempFolderDuplicatesMap = null;
            tempFolderToStampMap = null;
            tempStatistics = null;
            taskMonitor.complete();
        });
    }

    private void processFolderContents(Object[] PROGRESS_PARAMS, NProgressMonitor taskMonitor) {
        chrono("processFolderContents", () -> {
            if (folderContentComparator != null) {
                NProgressMonitor[] split = taskMonitor.split(5, 2, 1);
                ArrayList<DuplicateList> allNewSimiltudes = new ArrayList<DuplicateList>();
                PROGRESS_PARAMS[FILES_COUNT] = tempFileToStampMap.size();
                PROGRESS_PARAMS[FOLDERS_COUNT] = tempFolderToStampMap.size();
                PROGRESS_PARAMS[DUP_FILES_COUNT] = tempFileDuplicatesMap.size();
                PROGRESS_PARAMS[DUP_FOLDERS_COUNT] = tempFolderDuplicatesMap.size();
                final NProgressMonitor next = split[0].incremental(tempFolderDuplicatesMap.size());
                int index = 0;
                for (Iterator<DuplicateList> it = tempFolderDuplicatesMap.values().iterator(); it.hasNext(); ) {
                    PROGRESS_PARAMS[FILES_COUNT] = tempFileToStampMap.size();
                    PROGRESS_PARAMS[FOLDERS_COUNT] = tempFolderToStampMap.size();
                    PROGRESS_PARAMS[DUP_FILES_COUNT] = tempFileDuplicatesMap.size();
                    PROGRESS_PARAMS[DUP_FOLDERS_COUNT] = tempFolderDuplicatesMap.size();
                    PROGRESS_PARAMS[INDEX] = ++index;
                    next.inc(NMsg.ofC("FolderContentItem %s", Arrays.asList(PROGRESS_PARAMS)));
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
                split[0].complete();
                removeSingletons(split[1]);
                removeExpandedFiles(split[2]);
//                taskMonitor.stepOut();
//                System.out.println(">> dc.index "+taskMonitor.getIndex());
            }
            taskMonitor.complete();
        });
    }

    private void processFolderTimestamps(Object[] PROGRESS_PARAMS, NProgressMonitor taskMonitor) {
        chrono("processFolderTimestamps", () -> {
            // ---------------  FOLDERS  -----------------------
            if (folderStampFilterList != null) {
                int dmax = folderStampFilterList.getMaxLevels();
                NProgressMonitor[] split = taskMonitor.split(dmax - 1);
                for (int d = 1; d < dmax; d++) {
                    NProgressMonitor[] split2 = split[d - 1].split(5, 2);
                    Hashtable<Filestamp, DuplicateList> newHashtable = new Hashtable<Filestamp, DuplicateList>();
                    PROGRESS_PARAMS[FOLDER_FILTER_INDEX] = d;
                    FilestampFilter filestampFilter = folderStampFilterList.getFilestampFilter(d);
                    PROGRESS_PARAMS[FOLDER_FILTER_NAME] = filestampFilter == null ? "" : filestampFilter.toString();
                    PROGRESS_PARAMS[FILES_COUNT] = tempFileToStampMap.size();
                    PROGRESS_PARAMS[FOLDERS_COUNT] = tempFolderToStampMap.size();
                    PROGRESS_PARAMS[DUP_FILES_COUNT] = tempFileDuplicatesMap.size();
                    PROGRESS_PARAMS[DUP_FOLDERS_COUNT] = tempFolderDuplicatesMap.size();
                    final NProgressMonitor next = split2[0].incremental(tempFolderDuplicatesMap.size());
                    int index = 0;
                    for (DuplicateList duplicateList : tempFolderDuplicatesMap.values()) {
                        PROGRESS_PARAMS[FILES_COUNT] = tempFileToStampMap.size();
                        PROGRESS_PARAMS[FOLDERS_COUNT] = tempFolderToStampMap.size();
                        PROGRESS_PARAMS[DUP_FILES_COUNT] = tempFileDuplicatesMap.size();
                        PROGRESS_PARAMS[DUP_FOLDERS_COUNT] = tempFolderDuplicatesMap.size();
                        PROGRESS_PARAMS[INDEX] = ++index;
                        next.inc(NMsg.ofC("FolderstampItem %s", Arrays.asList(PROGRESS_PARAMS)));
                        for (File file : duplicateList.getFiles()) {
                            Filestamp filestamp = folderStampFilterList.getNextFilterSig(file, duplicateList.getFilestamp(), d, this);
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
                    split2[0].complete();
                    removeSingletons(split2[1]);
//                    taskMonitor.stepOut();
//                    System.out.println(">> ds.index "+taskMonitor.getIndex());
                }
            }
            taskMonitor.complete();
        });
    }

    private void processInit(Object[] PROGRESS_PARAMS, NProgressMonitor taskMonitor) {
        chrono("processInit", () -> {
            NProgressMonitor[] split = taskMonitor.split(5, 2);
            FileSetList li = new FileSetList(includedFileSet);
            PROGRESS_PARAMS[FILE_SET] = li;
            PROGRESS_PARAMS[FILE_FILTER_NAME] = (fileStampFilterList == null ? "" : String.valueOf(fileStampFilterList.getFilestampFilter(0)));
            PROGRESS_PARAMS[FILE_FILTER_INDEX] = 0;
            PROGRESS_PARAMS[FOLDER_FILTER_NAME] = (folderStampFilterList == null ? "" : String.valueOf(folderStampFilterList.getFilestampFilter(0)));
            PROGRESS_PARAMS[FOLDER_FILTER_INDEX] = 0;

            PROGRESS_PARAMS[INIT_FILES_COUNT] = tempStatistics.sourceFilesCount;
            PROGRESS_PARAMS[INIT_FOLDERS_COUNT] = tempStatistics.sourceFoldersCount;
//                taskMonitor.inc();
            for (EstimateIterator<File> i = li.iterate(this); i.hasNext(); ) {
                File file = i.next();
                PROGRESS_PARAMS[INIT_FILES_COUNT] = tempStatistics.sourceFilesCount;
                PROGRESS_PARAMS[INIT_FOLDERS_COUNT] = tempStatistics.sourceFoldersCount;
//                    taskMonitor.inc("InitItem", PROGRESS_PARAMS);
                registerFile(file);
                split[0].setProgress(i.progressRatio(), NMsg.ofC("InitItem %s", Arrays.asList(PROGRESS_PARAMS)));
            }
            if (tempStatistics.sourceFilesCount == 0 && tempStatistics.sourceFoldersCount == 0) {
                throw new IllegalArgumentException("Not Valid File set found");
            }
            split[0].complete();
            removeSingletons(split[1]);
            taskMonitor.complete(NMsg.ofC("InitItem %s", Arrays.asList(PROGRESS_PARAMS)));
        });
    }

    private void processFileContents(Object[] PROGRESS_PARAMS, NProgressMonitor taskMonitor) {
        chrono("processFileContents", () -> {
            if (fileContentComparator != null) {
                NProgressMonitor[] split = taskMonitor.split(5, 1, 2);

                ArrayList<DuplicateList> allNewSimilitudes = new ArrayList<DuplicateList>();
                PROGRESS_PARAMS[FILES_COUNT] = tempFileToStampMap.size();
                PROGRESS_PARAMS[FOLDERS_COUNT] = tempFolderToStampMap.size();
                PROGRESS_PARAMS[DUP_FILES_COUNT] = tempFileDuplicatesMap.size();
                PROGRESS_PARAMS[DUP_FOLDERS_COUNT] = tempFolderDuplicatesMap.size();
                final NProgressMonitor next = split[0].incremental(tempFileDuplicatesMap.size());
                int index = 0;
                for (Iterator<DuplicateList> it = tempFileDuplicatesMap.values().iterator(); it.hasNext(); ) {
                    PROGRESS_PARAMS[FILES_COUNT] = tempFileToStampMap.size();
                    PROGRESS_PARAMS[FOLDERS_COUNT] = tempFolderToStampMap.size();
                    PROGRESS_PARAMS[DUP_FILES_COUNT] = tempFileDuplicatesMap.size();
                    PROGRESS_PARAMS[DUP_FOLDERS_COUNT] = tempFolderDuplicatesMap.size();
                    PROGRESS_PARAMS[INDEX] = ++index;
                    next.inc(NMsg.ofC("FileContentItem %s", Arrays.asList(PROGRESS_PARAMS)));
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
                            allNewSimilitudes.add(newDuplicateList);
                        }
                    }
                }
                split[0].complete();
                NProgressMonitor s1 = split[1].incremental(allNewSimilitudes.size());
                for (DuplicateList duplicateList : allNewSimilitudes) {
//                    System.out.println(">add "+duplicateList);
                    tempFileDuplicatesMap.put(duplicateList.getFilestamp(), duplicateList);
                    for (File file : duplicateList.getFiles()) {
                        tempFileToStampMap.put(file, duplicateList.getFilestamp());
                    }
                    s1.inc();
                }
                s1.complete();

                removeSingletons(split[2]);
//                taskMonitor.stepOut();
//                System.out.println(">> fc.index "+taskMonitor.getIndex());
            }
            taskMonitor.complete(NMsg.ofC("processFileContents %s", Arrays.asList(PROGRESS_PARAMS)));
        });
    }

    private void processFileTimestamps(Object[] PROGRESS_PARAMS, NProgressMonitor taskMonitor) {
        chrono("processFileTimestamps", () -> {
            int max = fileStampFilterList.getMaxLevels();
            if (max == 0) {
                taskMonitor.complete();
                return;
            }
            if (max < 0) {
                throw new IllegalArgumentException("Not Valid File stamp found");
            }
            NProgressMonitor[] split = taskMonitor.split(max - 1);
            for (int d = 1; d < max; d++) {
                NProgressMonitor[] split2 = split[d - 1].split(5, 2);
                NProgressMonitor s0 = split2[0].incremental(tempFileDuplicatesMap.size());
                Hashtable<Filestamp, DuplicateList> newHashtable = new Hashtable<Filestamp, DuplicateList>();
                PROGRESS_PARAMS[FILE_FILTER_INDEX] = d;
                PROGRESS_PARAMS[FILE_FILTER_NAME] = fileStampFilterList.getFilestampFilter(d).toString();
                PROGRESS_PARAMS[FILES_COUNT] = tempFileToStampMap.size();
                PROGRESS_PARAMS[FOLDERS_COUNT] = tempFolderToStampMap.size();
                PROGRESS_PARAMS[DUP_FILES_COUNT] = tempFileDuplicatesMap.size();
                PROGRESS_PARAMS[DUP_FOLDERS_COUNT] = tempFolderDuplicatesMap.size();

                int index = 0;
                for (DuplicateList duplicateList : tempFileDuplicatesMap.values()) {
                    PROGRESS_PARAMS[FILES_COUNT] = tempFileToStampMap.size();
                    PROGRESS_PARAMS[FOLDERS_COUNT] = tempFolderToStampMap.size();
                    PROGRESS_PARAMS[DUP_FILES_COUNT] = tempFileDuplicatesMap.size();
                    PROGRESS_PARAMS[DUP_FOLDERS_COUNT] = tempFolderDuplicatesMap.size();
                    PROGRESS_PARAMS[INDEX] = ++index;
                    s0.inc(NMsg.ofC("FilestampItem %s", Arrays.asList(PROGRESS_PARAMS)));
                    for (File file : duplicateList.getFiles()) {
                        Filestamp filestamp = fileStampFilterList.getNextFilterSig(file, duplicateList.getFilestamp(), d, this);
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
                s0.complete();
                removeSingletons(split2[1]);
            }
            taskMonitor.complete();
        });
    }


    /**
     * @param f1 f1
     * @param f2 f2
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
        this.diffFileMode = NEnumSet.noneOf(FileMode.class);
        this.folderContentComparator = folderContentComparator;
    }

    /**
     * list of the filestamps used for comparing folders
     *
     * @return list of the filestamps used for comparing folders
     */
    public FileStampFilterList getFolderStampFilterList() {
        return folderStampFilterList;
    }

    /**
     * updates list of thefilestamps used for comparing folder
     *
     * @param folderFilterList
     */
    public void setFolderStampFilterList(FileStampFilterList folderFilterList) {
        this.diffFileMode = NEnumSet.noneOf(FileMode.class);
        this.folderStampFilterList = folderFilterList;
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
        this.diffFileMode = NEnumSet.noneOf(FileMode.class);
        this.fileContentComparator = fileContentComparator;
    }

    /**
     * list of thefilestamps used for comparing files
     *
     * @return list of thefilestamps used for comparing files
     */
    public FileStampFilterList getFileStampFilterList() {
        return fileStampFilterList;
    }

    public void setFileStampFilterList(FileStampFilterList fileStampFilterList) {
        this.diffFileMode = NEnumSet.noneOf(FileMode.class);
        this.fileStampFilterList = fileStampFilterList;
    }

    /**
     * the used Diff mode or FileDiffFactory.
     * return UNKNOWN if filters or comparators are selected distinctly
     *
     * @return the used Diff mode or FileDiffFactory
     */
    public NEnumSet<FileMode> getDiffFileMode() {
        return diffFileMode;
    }

    public boolean isFindFolderDuplicates() {
        return showFolderDuplicates && (folderStampFilterList != null || folderContentComparator != null);
    }

    public boolean isFindFileDuplicates() {
        return showFileDuplicates && (fileStampFilterList != null || fileContentComparator != null);
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
    public void setFileMode(NEnumSet<FileMode> fileModes) {
        setFileMode(fileModes.toArray());
    }

    public void setFileMode(FileMode... fileModes) {
        this.diffFileMode = NEnumSet.of(Arrays.asList(fileModes), FileMode.class);
        if (diffFileMode.isEmpty()) {
            diffFileMode = diffFileMode.add(FileMode.FILE_NAME);
            diffFileMode = diffFileMode.add(FileMode.FILE_SIZE);
            diffFileMode = diffFileMode.add(FileMode.FILE_CONTENT);
            diffFileMode = diffFileMode.add(FileMode.FOLDER_NAME);
            diffFileMode = diffFileMode.add(FileMode.FOLDER_SIZE);
            diffFileMode = diffFileMode.add(FileMode.FOLDER_CONTENT);
        }

        this.showFileDuplicates = !this.diffFileMode.isEmpty();
        this.fileStampFilterList = new FileStampFilterList().addFileFilters(fileModes);
        this.fileContentComparator = FileDiffFactory.createFileContentComparator(fileModes);
        this.folderStampFilterList = new FileStampFilterList().addFolderFilters(fileModes);
        this.folderContentComparator = FileDiffFactory.createFolderContentComparator(fileModes);
        this.showFolderDuplicates = (folderStampFilterList != null || folderContentComparator != null);

        if (fileStampFilterList == null) {
            throw new IllegalStateException("At least a file stamp filter should be set");
        }
        if (folderStampFilterList == null) {
            throw new IllegalStateException("At least a folder stamp filter should be set");
        }
    }

    public FileFilter getGlobalFileFilter() {
        return globalFileFilter;
    }

    public void setGlobalFileFilter(FileFilter globalFileFilter) {
        this.globalFileFilter = globalFileFilter;
    }

    @Override
    protected KifKif clone() {
        try {
            KifKif o = (KifKif) super.clone();
            return o;
        } catch (CloneNotSupportedException ex) {
            throw new RuntimeException(ex);
        }
    }

}
