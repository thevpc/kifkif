package net.thevpc.kifkif.swing;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.util.ArrayList;
import java.util.Vector;

import javax.swing.event.TreeModelEvent;
import javax.swing.event.TreeModelListener;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;

import net.thevpc.kifkif.DuplicateList;
import net.thevpc.kifkif.DuplicateListRemovedEvent;
import net.thevpc.kifkif.FileAddedEvent;
import net.thevpc.kifkif.FileRemovedEvent;
import net.thevpc.kifkif.SearchData;

/**
 * User: taha
 * Date: 12 janv. 2005
 * Time: 12:24:01
 */
public class DuplicateListTreeModel implements TreeModel {
    private SearchData searchData;
    private static final Object rootNode = "root";
    public static final String SEARCH_DATA_CHANGED = "SEARCH_DATA_CHANGED";
    private Vector<TreeModelListener> listeners = new Vector<TreeModelListener>(3);
    private PropertyChangeListener SELECTED_FILES_CHANGED_listener = new PropertyChangeListener() {
        public void propertyChange(PropertyChangeEvent evt) {
            selectedFilesChanged();
        }
    };

    private PropertyChangeListener DUPLICATES_CHANGED_listener = new PropertyChangeListener() {
        public void propertyChange(PropertyChangeEvent evt) {
            duplicatesChanged();
        }
    };

    private PropertyChangeListener FILE_ADDED_listener = new PropertyChangeListener() {
        public void propertyChange(PropertyChangeEvent evt) {
            duplicateFileInserted((FileAddedEvent) evt.getNewValue());
        }
    };

    private PropertyChangeListener FILE_REMOVED_listener = new PropertyChangeListener() {
        public void propertyChange(PropertyChangeEvent evt) {
            duplicateFileRemoved((FileRemovedEvent) evt.getNewValue());
        }
    };

    private PropertyChangeListener LIST_REMOVED_listener = new PropertyChangeListener() {
        public void propertyChange(PropertyChangeEvent evt) {
            duplicateListRemoved((DuplicateListRemovedEvent) evt.getNewValue());
        }
    };

    public DuplicateListTreeModel(SearchData searchData) {
        setSearchData(searchData);
    }

    public Object getRoot() {
        return rootNode;
    }

    public void setSearchData(SearchData searchData) {
        if (this.searchData != null) {
            this.searchData.removeSelectedFilesChangeListener(SELECTED_FILES_CHANGED_listener);
            this.searchData.removeDuplicatesChangeListener(DUPLICATES_CHANGED_listener);
            this.searchData.removeFileAddedChangeListener(FILE_ADDED_listener);
            this.searchData.removeFileRemovedChangeListener(FILE_REMOVED_listener);
            this.searchData.removeListRemovedChangeListener(LIST_REMOVED_listener);
        }
//        SearchData oldSearchData = this.searchData;
        this.searchData = searchData;
        this.searchData.addSelectedFilesChangeListener(SELECTED_FILES_CHANGED_listener);
        this.searchData.addFileAddedChangeListener(FILE_ADDED_listener);
        this.searchData.addFileRemovedChangeListener(FILE_REMOVED_listener);
        this.searchData.addListRemovedChangeListener(LIST_REMOVED_listener);
        this.searchData.addDuplicatesChangeListener(DUPLICATES_CHANGED_listener);
        duplicatesChanged();
    }

    public Object getChild(Object parent, int index) {
        return parent == rootNode ?
                (Object) searchData.getDuplicateLists().get(index)
                : ((parent instanceof DuplicateList) ?
                ((DuplicateList) parent).getFile(index)
                : null);
    }

    public int getChildCount(Object parent) {
        return parent == rootNode ?
                searchData.getDuplicateLists().size()
                : ((parent instanceof DuplicateList) ?
                ((DuplicateList) parent).getFileCount()
                : 0);
    }

    public boolean isLeaf(Object node) {
        return node instanceof File;
    }

    public void valueForPathChanged(TreePath path, Object newValue) {
        // nothing to do
    }

    public int getIndexOfChild(Object parent, Object child) {
        return parent == rootNode ?
                searchData.getDuplicateLists().indexOf(child)
                : ((parent instanceof DuplicateList) ?
                new ArrayList<File>(((DuplicateList) parent).getFiles()).indexOf(child)
                : -1);

    }

    public void addTreeModelListener(TreeModelListener l) {
        listeners.add(l);
    }

    public void removeTreeModelListener(TreeModelListener l) {
        listeners.remove(l);
    }

    public SearchData getSearchData() {
        return searchData;
    }

    private void selectedFilesChanged() {
        TreeModelEvent e = null;
        for (TreeModelListener treeModelListener : listeners) {
            if (e == null) {
                e = new TreeModelEvent(this, new Object[]{rootNode});
            }
            treeModelListener.treeNodesChanged(e);
        }
    }

    private void duplicatesChanged() {
        TreeModelEvent e = null;
        for (TreeModelListener treeModelListener : listeners) {
            if (e == null) {
                e = new TreeModelEvent(this, new Object[]{rootNode});
            }
            treeModelListener.treeStructureChanged(e);
        }
    }

    private void duplicateListRemoved(DuplicateListRemovedEvent event) {
        TreeModelEvent e = null;
        for (TreeModelListener treeModelListener : listeners) {
            if (e == null) {
                e = new TreeModelEvent(this, new Object[]{rootNode},new int[]{event.index},new Object[]{event.list});
            }
            treeModelListener.treeNodesRemoved(e);
        }
    }
    private void duplicateFileRemoved(FileRemovedEvent event) {
        TreeModelEvent e = null;
        for (TreeModelListener treeModelListener : listeners) {
            if (e == null) {
                e = new TreeModelEvent(this, new Object[]{rootNode,event.list},new int[]{event.index},new Object[]{event.file});
            }
            treeModelListener.treeNodesRemoved(e);
        }
    }
    private void duplicateFileInserted(FileAddedEvent event) {
        TreeModelEvent e = null;
        for (TreeModelListener treeModelListener : listeners) {
            if (e == null) {
                e = new TreeModelEvent(this, new Object[]{rootNode,event.list},new int[]{event.index},new Object[]{event.file});
            }
            treeModelListener.treeNodesInserted(e);
        }
    }
}
