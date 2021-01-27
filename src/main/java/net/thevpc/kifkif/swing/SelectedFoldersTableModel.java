package net.thevpc.kifkif.swing;

import net.thevpc.kifkif.DefaultFileFilter;
import net.thevpc.kifkif.DefaultFileSet;
import net.thevpc.kifkif.FileSet;
import net.thevpc.kifkif.KifKif;

import javax.swing.table.AbstractTableModel;
import java.io.File;

/**
 * @author vpc
 *         Date: 13 janv. 2005
 *         Time: 00:06:34
 */
class SelectedFoldersTableModel extends AbstractTableModel {

    private KifKif kifKif;
    private Kkw kkw;

    public SelectedFoldersTableModel(Kkw kkw, KifKif kifKif) {
        this.kifKif = kifKif;
        this.kkw = kkw;
    }

    public int getIncludedRowCount() {
        return kifKif.getIncludedFileSetsCount();
    }

    public int getExcludedRowCount() {
        return (kifKif == null || kifKif.getGlobalFileFilter() == null) ? 0 : ((DefaultFileFilter) kifKif.getGlobalFileFilter()).getExcludedFolders().size();
    }

    public int getRowCount() {
        return kifKif.getIncludedFileSetsCount() + getExcludedRowCount();
    }

    public int getColumnCount() {
        return 2;
    }

    public Object getValueAt(int rowIndex, int columnIndex) {
        int r = rowIndex - getIncludedRowCount();
        return r < 0 ? getIncludedValueAt(rowIndex, columnIndex) : getExcludedValueAt(r, columnIndex);
    }

    public Object getExcludedValueAt(int rowIndex, int columnIndex) {
        switch (columnIndex) {
            case 0: {
                File fs = ((DefaultFileFilter) kifKif.getGlobalFileFilter()).getExcludedFolders().get(rowIndex);
                return fs.toString();
            }
            case 1: {
                return kkw.getResources().get("Exclude");
            }
        }
        return null;
    }

    public Object getIncludedValueAt(int rowIndex, int columnIndex) {
        switch (columnIndex) {
            case 0: {
                FileSet fs = kifKif.getIncludedFileSet(rowIndex);
                if (fs instanceof DefaultFileSet) {
                    return ((DefaultFileSet) fs).getRoot();
                }
                return fs.toString();
            }
            case 1: {
                return kkw.getResources().get("Include");
            }
        }
        return null;
    }

    public String getColumnName(int columnIndex) {
        return kkw.getResources().get("includeSelectedFoldersTable.column["+columnIndex+"]", "?", true);
    }

    public Class<?> getColumnClass(int columnIndex) {
        return String.class;
    }

    public FileSet getFileSet(int index) {
        return kifKif.getIncludedFileSet(index);
    }

    public void remove(int i) {
        if (i >= 0 && i < getRowCount()) {
            int r = i - getIncludedRowCount();
            if (r < 0) {
                kifKif.removeIncludedFileSet(i);
            } else {
                ((DefaultFileFilter) kifKif.getGlobalFileFilter()).getExcludedFolders().remove(r);
            }
            fireTableDataChanged();
        }
    }

}
