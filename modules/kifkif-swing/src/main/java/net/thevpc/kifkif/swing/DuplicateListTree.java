package net.thevpc.kifkif.swing;

import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;

import javax.swing.Icon;
import javax.swing.JPopupMenu;
import javax.swing.JTree;
import javax.swing.SwingUtilities;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.TreePath;

import net.thevpc.kifkif.DuplicateList;
import net.thevpc.kifkif.Filestamp;
import net.thevpc.kifkif.SearchData;

/**
 * User: taha
 * Date: 12 janv. 2005
 * Time: 12:34:53
 */
public class DuplicateListTree extends JTree {
    private boolean showFilestamp = true;
    transient private Kkw kkw;
    transient JPopupMenu popupMenu;

    public DuplicateListTree(Kkw kkw) {
        super(new DuplicateListTreeModel(new SearchData()));
//        getModel().addTreeModelListener(new TreeModelListener() {
//            public void treeNodesChanged(TreeModelEvent e) {
//                firePropertyChange("SelectedFilesForRemoval", null, selectedFilesForRemoval);
//            }
//
//            public void treeNodesInserted(TreeModelEvent e) {
//                firePropertyChange("SelectedFilesForRemoval", null, selectedFilesForRemoval);
//            }
//
//            public void treeNodesRemoved(TreeModelEvent e) {
//                firePropertyChange("SelectedFilesForRemoval", null, selectedFilesForRemoval);
//            }
//
//            public void treeStructureChanged(TreeModelEvent e) {
//                firePropertyChange("SelectedFilesForRemoval", null, selectedFilesForRemoval);
//            }
//        });
        this.kkw = kkw;
        this.popupMenu = createPopupMenu();
        setRootVisible(false);
        this.setCellRenderer(new DuplicateRenderer());
        this.addMouseListener(new MouseListener() {
            public void mouseClicked(MouseEvent e) {
                if (SwingUtilities.isRightMouseButton(e) && e.getClickCount() == 1) {
                    popupMenu.show(DuplicateListTree.this, e.getX(), e.getY());
                } else if (SwingUtilities.isLeftMouseButton(e) && e.getClickCount() == 2) {
                    switchSelectedFiles();
                }
            }

            public void mousePressed(MouseEvent e) {
                //nothing
            }

            public void mouseReleased(MouseEvent e) {
                //nothing
            }

            public void mouseEntered(MouseEvent e) {
                //nothing
            }

            public void mouseExited(MouseEvent e) {
                //nothing
            }
        });
    }

    private JPopupMenu createPopupMenu() {
        return new JPopupMenu();
    }

    public JPopupMenu getPopupMenu() {
        return popupMenu;
    }

    public void switchSelectedFiles() {
        File[] f = getSelectedFiles();
        for (File file : f) {
            getSearchData().invertSelection(file);
        }
    }

    public File getSelectedFile() {
        TreePath p = getSelectionPath();
        if (p != null) {
            Object o = p.getLastPathComponent();
            if (o instanceof File) {
                return (File) o;
            }
        }
        return null;
    }

    public File[] getSelectedFiles() {
        ArrayList<File> f = new ArrayList<File>();
        TreePath[] p = getSelectionPaths();
        if (p != null) {
            for (TreePath treePath : p) {
                Object o = treePath.getLastPathComponent();
                if (o instanceof File) {
                    f.add((File) o);
                }
            }
        }
        return f.toArray(new File[f.size()]);
    }

    public DuplicateList findListByFile(File file) {
        for (DuplicateList duplicateList : getModel().getSearchData().getDuplicateLists()) {
            if (duplicateList.getFiles().contains(file)) {
                return duplicateList;
            }
        }
        return null;
    }

    public void hideSelectedFiles() {
        TreePath[] p = getSelectionPaths();
        if (p != null) {
            for (TreePath treePath : p) {
                Object o = treePath.getLastPathComponent();
                if (o instanceof File) {
                    DuplicateList l = (DuplicateList) treePath.getParentPath().getLastPathComponent();
                    l.removeFile((File) o);
                } else if (o instanceof DuplicateList) {
                    getModel().getSearchData().removeList((DuplicateList) o);
                }
            }
        }
    }

    public void setSearchData(SearchData searchData) {
        getModel().setSearchData(searchData);
//        Swings.expandAll(this);
        //firePropertyChange("SelectedFilesForRemoval", null, selectedFilesForRemoval);
    }

    public boolean isShowFilestamp() {
        return showFilestamp;
    }

    public void setShowFilestamp(boolean showFilestamp) {
        this.showFilestamp = showFilestamp;
        invalidate();
        repaint();
    }

    public SearchData getSearchData() {
        return (getModel()).getSearchData();
    }

    @Override
    public DuplicateListTreeModel getModel() {
        return ((DuplicateListTreeModel) super.getModel());
    }

    public void sort(Comparator<DuplicateList> c) {
        getSearchData().sortDuplicateLists(c);
    }

    @Override
    public void updateUI() {
        super.updateUI();
        this.setCellRenderer(new DuplicateRenderer());
    }

    private class DuplicateRenderer extends DefaultTreeCellRenderer {
        @Override
        public Component getTreeCellRendererComponent(JTree tree, Object value, boolean sel, boolean expanded, boolean leaf, int row, boolean hasFocus) {
            Icon icon = null;
            boolean itemSelectedForDeletion=false;
            if (value instanceof DuplicateList) {
                DuplicateList d = (DuplicateList) value;
                if (d.isFolderList()) {
                    icon = DuplicateListTree.this.kkw.getIconSet().getIcon("duplicateListTree.renderer.FolderGroup");
                    if (showFilestamp) {
                        Filestamp stamp = d.getFilestamp();
                        String n = stamp.getFileName();
                        if (n == null) {
                            n = "*";
                        }
                        long s = stamp.getFileSize();
                        value = DuplicateListTree.this.kkw.getResources().get2("duplicateListTree.renderer.FolderGroup.longText",
                                (d.getFileCount() - 1),
                                n,
                                s);
                    } else {
                        value = DuplicateListTree.this.kkw.getResources().get2("duplicateListTree.renderer.FolderGroup.shortText",
                                (d.getFileCount() - 1));

                    }
                } else {
                    icon = DuplicateListTree.this.kkw.getIconSet().getIcon("duplicateListTree.renderer.FileGroup");
                    if (showFilestamp) {
                        Filestamp stamp = d.getFilestamp();
                        String n = stamp.getFileName();
                        if (n == null) {
                            n = "*";
                        }
                        String s = kkw.getFileSizeString(stamp.getFileSize());
                        value = DuplicateListTree.this.kkw.getResources().get2("duplicateListTree.renderer.FileGroup.longText",
                                (d.getFileCount() - 1),
                                n,
                                s);
                    } else {
                        value = DuplicateListTree.this.kkw.getResources().get2("duplicateListTree.renderer.FileGroup.shortText",
                                (d.getFileCount() - 1));

                    }
                }
            } else if (value instanceof File) {
                File fileValue = (File) value;
                try {
                    value = fileValue.getCanonicalPath();
                } catch (IOException e) {
                    value = fileValue.getPath();
                }
                value = DuplicateListTree.this.kkw.getResources().get2("duplicateListTree.renderer.File", value, kkw.getDateFormat().format(new Date(fileValue.lastModified())));
                if (getSearchData().getSelectedDuplicateFiles().contains(fileValue)) {
                    itemSelectedForDeletion=true;
                    icon = DuplicateListTree.this.kkw.getIconSet().getIcon("duplicateListTree.renderer.File.selected");
                } else {
                    icon = DuplicateListTree.this.kkw.getIconSet().getIcon("duplicateListTree.renderer.File.unselected");
                }
            }
            super.getTreeCellRendererComponent(tree, value, sel, expanded, leaf, row, hasFocus);
            if(itemSelectedForDeletion){
                setForeground(Color.RED);
            }
            setIcon(icon);
            return this;
        }
    }

    public void clear(){
        setSearchData(new SearchData());
    }
}
