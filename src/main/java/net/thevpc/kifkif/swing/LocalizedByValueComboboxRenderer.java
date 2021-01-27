package net.thevpc.kifkif.swing;

import java.awt.Component;

import javax.swing.DefaultListCellRenderer;
import javax.swing.JComboBox;
import javax.swing.JList;

/**
 * @author vpc
 * Date: 12 janv. 2005
 * Time: 19:41:17
 */
public class LocalizedByValueComboboxRenderer extends DefaultListCellRenderer {
    private Kkw kkw;
    private JComboBox combo;

    public LocalizedByValueComboboxRenderer(Kkw kkw, JComboBox combo) {
        this.kkw = kkw;
        this.combo = combo;
    }

    public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
        String valuesKey=(String) combo.getClientProperty("ValuesKey");
        if(valuesKey==null){
            valuesKey=combo.getName();
        }
        String str = kkw.getResources().get(valuesKey + "." + value);
        return super.getListCellRendererComponent(list, str, index, isSelected, cellHasFocus);
    }
}
