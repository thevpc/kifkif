package net.thevpc.kifkif.swing;

import java.io.File;

import javax.swing.table.AbstractTableModel;

import net.thevpc.kifkif.DefaultFileFilter;
import net.thevpc.kifkif.KifKif;

/**
 * @author vpc
 *         Date: 13 janv. 2005
 *         Time: 00:06:34
 */
class ExcludedFoldersTableModel extends AbstractTableModel {

    private Kkw kkw;
    private KifKif kifkif;

    public ExcludedFoldersTableModel(Kkw kkw, KifKif kifkif) {
        this.kkw = kkw;
        this.kifkif = kifkif;
    }

    public int getRowCount() {
        return (kifkif==null || kifkif.getGlobalFileFilter()==null)? 0 : ((DefaultFileFilter) kifkif.getGlobalFileFilter()).getExcludedFolders().size();
    }

    public int getColumnCount() {
        return 1;
    }

    public Object getValueAt(int rowIndex, int columnIndex) {
        File fs = ((DefaultFileFilter) kifkif.getGlobalFileFilter()).getExcludedFolders().get(rowIndex);
        return fs.toString();
    }

    public String getColumnName(int columnIndex) {
        return kkw.getResources().get("excludeSelectedFoldersTable.column[0]", "Exclude", true);
    }

    public Class<?> getColumnClass(int columnIndex) {
        return String.class;
    }

    public File getExcludedFolder(int index) {
        return ((DefaultFileFilter) kifkif.getGlobalFileFilter()).getExcludedFolders().get(index);
    }
}
