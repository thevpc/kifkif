package net.vpc.app.kifkif;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.File;
import java.io.Serializable;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.TreeSet;

import net.vpc.common.prs.xml.XmlSerializable;
import net.vpc.common.prs.xml.XmlSerializationException;
import net.vpc.common.prs.xml.XmlSerializer;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * @author vpc
 *         Date: 16 janv. 2005
 *         Time: 16:19:50
 */
public class SearchData implements Serializable, XmlSerializable {
    public static final String DUPLICATE_LIST_REMOVED = "DUPLICATE_LIST_REMOVED";
    public static final String FILE_REMOVED = DuplicateList.FILE_REMOVED;
    public static final String FILE_ADDED = DuplicateList.FILE_ADDED;
    public static final String SELECTED_FILES_CHANGED = "SELECTED_FILES_CHANGED";
    public static final String DUPLICATES_CHANGED = "DUPLICATES_CHANGED";
    private transient PropertyChangeSupport support;
    private transient ListDispatcher listDispatcher = new ListDispatcher();

    private SearchStatistics statistics= new SearchStatistics();

    private List<DuplicateList> duplicateLists;
    private Collection<File> selectedDuplicateFiles;
    private KifKif kifkif;

    private class ListDispatcher implements PropertyChangeListener {
        public void propertyChange(PropertyChangeEvent evt) {
            DuplicateList duplicateList = (DuplicateList) evt.getSource();
            if (FILE_REMOVED.equals(evt.getPropertyName())) {
                if (duplicateList.size() == 0) {
                    removeList(duplicateList);
                    return;
                }
            }
            if (support != null) {
                support.firePropertyChange(evt.getPropertyName(), null, evt.getNewValue());
            }
        }
    }


    public SearchData() {
        this(new ArrayList<DuplicateList>());
    }

    public SearchData(List<DuplicateList> duplicateLists) {
        setSelectedDuplicateFiles(new TreeSet<File>());
        setDuplicateLists(duplicateLists);
    }

    public List<DuplicateList> getDuplicateLists() {
        return duplicateLists;
    }

    public void setDuplicateLists(List<DuplicateList> duplicateLists) {
        this.duplicateLists = new ArrayList<DuplicateList>(duplicateLists);
        for (DuplicateList duplicateList : duplicateLists) {
            if (listDispatcher == null) {
                listDispatcher = new ListDispatcher();
            }
            duplicateList.addFileAddedPropertyListener(listDispatcher);
            duplicateList.addFileRemovedPropertyListener(listDispatcher);
        }
    }

    public Collection<File> getSelectedDuplicateFiles() {
        return selectedDuplicateFiles;
    }

    public void setSelectedDuplicateFiles(Collection<File> selectedDuplicateFiles) {
        this.selectedDuplicateFiles = selectedDuplicateFiles;
        fireSelectedFilesChanged();
    }

    public void setSelectedDuplicatesAuto() {
        Comparator<File> defaultcomparator = new Comparator<File>() {
            public int compare(File o1, File o2) {
                return
                        o1.lastModified() > o2.lastModified() ? 1 :
                                o1.lastModified() < o2.lastModified() ? -1 :
                                        0;
            }
        };
        for (DuplicateList duplicateList : duplicateLists) {
            if (duplicateList.getFileCount() == 1) {
                selectedDuplicateFiles.add(duplicateList.getFile(0));
            } else {
                boolean first = true;
                List<File> files = new ArrayList<File>(duplicateList.getFiles());
                Collections.sort(files, defaultcomparator);
                for (File file : files) {
                    if (first) {
                        first = false;
                    } else {
                        selectedDuplicateFiles.add(file);
                    }
                }
            }
        }
        fireSelectedFilesChanged();
    }

    public void setSelectedDuplicatesNone() {
        selectedDuplicateFiles.clear();
        fireSelectedFilesChanged();
    }

    public void setSelectedDuplicatesByFolderAncestor(File folder, boolean selectedWhenAncestor) {
        for (DuplicateList duplicateList : duplicateLists) {
            if (duplicateList.getFileCount() > 1) {
                ArrayList<File> noRemove = new ArrayList<File>();
                ArrayList<File> doRemove = new ArrayList<File>();
                ArrayList<File> alreadySelected = new ArrayList<File>();
                for (File file : duplicateList.getFiles()) {
                    String c = file.getPath().replace('\\', '/');
                    String p = folder.getPath().replace('\\', '/') + '/';
                    if (c.startsWith(p)) {
                        if (selectedWhenAncestor) {
                            doRemove.add(file);
                        } else {
                            noRemove.add(file);
                        }
                    } else {
                        if (selectedWhenAncestor) {
                            noRemove.add(file);
                        } else {
                            doRemove.add(file);
                        }
                    }
                    if (selectedDuplicateFiles.contains(file)) {
                        alreadySelected.add(file);
                    }
                }
                if (doRemove.size() > 0) {
                    selectedDuplicateFiles.addAll(doRemove);
                    if (noRemove.size() == 0) {
                        selectedDuplicateFiles.remove(doRemove.get(0));
                    }
                } else {
                    if (alreadySelected.size() == 0) {
//                        selectedDuplicateFiles.add(noRemove.get(noRemove.size() - 1));
                    }
                }
                selectedDuplicateFiles.removeAll(noRemove);
            } else {
                if (!selectedDuplicateFiles.contains(duplicateList.getFile(0))) {
                    selectedDuplicateFiles.add(duplicateList.getFile(0));
                }
            }
        }
        fireSelectedFilesChanged();
    }

    public boolean switchSelectedFiles() {
        File[] f = selectedDuplicateFiles.toArray(new File[selectedDuplicateFiles.size()]);
        boolean updated = false;
        for (File file : f) {
            if (selectedDuplicateFiles.contains(file)) {
                selectedDuplicateFiles.remove(file);
            } else {
                selectedDuplicateFiles.add(file);
            }
            updated = true;
        }
        if (updated) {
            fireSelectedFilesChanged();
        }
        return updated;
    }

    public void sortDuplicateLists(Comparator<DuplicateList> comparator) {
        Collections.sort(duplicateLists, comparator);
        if (support != null) {
            support.firePropertyChange(DUPLICATES_CHANGED, Boolean.FALSE, Boolean.TRUE);
        }
    }

    public void addSelectedFilesChangeListener(PropertyChangeListener listener) {
        if (support == null) {
            support = new PropertyChangeSupport(this);
        }
        support.addPropertyChangeListener(SELECTED_FILES_CHANGED, listener);
    }

    public void addFileAddedChangeListener(PropertyChangeListener listener) {
        if (support == null) {
            support = new PropertyChangeSupport(this);
        }
        support.addPropertyChangeListener(FILE_ADDED, listener);
    }

    public void addFileRemovedChangeListener(PropertyChangeListener listener) {
        if (support == null) {
            support = new PropertyChangeSupport(this);
        }
        support.addPropertyChangeListener(FILE_REMOVED, listener);
    }

    public void addListRemovedChangeListener(PropertyChangeListener listener) {
        if (support == null) {
            support = new PropertyChangeSupport(this);
        }
        support.addPropertyChangeListener(DUPLICATE_LIST_REMOVED, listener);
    }

    public void removeSelectedFilesChangeListener(PropertyChangeListener listener) {
        if (support != null) {
            support.removePropertyChangeListener(SELECTED_FILES_CHANGED, listener);
        }
    }

    public void addDuplicatesChangeListener(PropertyChangeListener listener) {
        if (support == null) {
            support = new PropertyChangeSupport(this);
        }
        support.addPropertyChangeListener(DUPLICATES_CHANGED, listener);
    }

    public void removeDuplicatesChangeListener(PropertyChangeListener listener) {
        if (support != null) {
            support.removePropertyChangeListener(DUPLICATES_CHANGED, listener);
        }
    }

    public void removeFileAddedChangeListener(PropertyChangeListener listener) {
        if (support != null) {
            support.removePropertyChangeListener(FILE_ADDED, listener);
        }
    }

    public void removeFileRemovedChangeListener(PropertyChangeListener listener) {
        if (support != null) {
            support.removePropertyChangeListener(FILE_REMOVED, listener);
        }
    }

    public void removeListRemovedChangeListener(PropertyChangeListener listener) {
        if (support != null) {
            support.removePropertyChangeListener(DUPLICATE_LIST_REMOVED, listener);
        }
    }

    public void invertSelection(File file) {
        if (!selectedDuplicateFiles.remove(file)) {
            selectedDuplicateFiles.add(file);
        }
        fireSelectedFilesChanged();
    }

    private void fireSelectedFilesChanged() {
        statistics.duplicateFileSelectionCount = -1;
        statistics.duplicateFolderSelectionCount = -1;
        if (support != null) {
            support.firePropertyChange(SELECTED_FILES_CHANGED, Boolean.FALSE, Boolean.TRUE);
        }
    }

    public int getStatsSelectedFileDuplicatesCount() {
        if (statistics.duplicateFileSelectionCount < 0) {
            statistics.duplicateFileSelectionCount = 0;
            statistics.duplicateFolderSelectionCount = 0;
            for (File file : selectedDuplicateFiles) {
                if (file.isFile()) {
                    statistics.duplicateFileSelectionCount++;
                } else if (file.isDirectory()) {
                    statistics.duplicateFolderSelectionCount++;
                }
            }
        }
        return statistics.duplicateFileSelectionCount;
    }

    public int getStatsSelectedFolderDuplicatesCount() {
        getStatsSelectedFileDuplicatesCount();
        return statistics.duplicateFolderSelectionCount;
    }

    public int getStatsDuplicateFileGroupsCount() {
        if (statistics.duplicateFileGroupsCount < 0) {
            statistics.duplicateFileGroupsCount = 0;
            statistics.duplicateFolderGroupsCount = 0;
            for (DuplicateList duplicateList : duplicateLists) {
                if (duplicateList.isFolderList()) {
                    statistics.duplicateFolderGroupsCount++;
                } else {
                    statistics.duplicateFileGroupsCount++;
                }
            }

        }
        return statistics.duplicateFileGroupsCount;
    }

    public int getStatsDuplicateFolderGroupsCount() {
        getStatsDuplicateFileGroupsCount();
        return statistics.duplicateFolderGroupsCount;
    }

    public void removeList(DuplicateList list) {
        int i = duplicateLists.indexOf(list);
        if (i >= 0) {
            if (selectedDuplicateFiles.removeAll(list.getFiles())) {
                fireSelectedFilesChanged();
            }
            if (duplicateLists.remove(list)) {
                if (support != null) {
                    support.firePropertyChange(DUPLICATE_LIST_REMOVED, null, new DuplicateListRemovedEvent(list, i));
                }
            }
        }
    }

    public SearchStatistics getStatistics() {
        return statistics;
    }

    public void setStatistics(SearchStatistics statistics) {
        this.statistics = statistics;
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

    public KifKif getKifkif() {
        return kifkif;
    }

    public void setKifkif(KifKif kifkif) {
        this.kifkif = kifkif;
    }
}
