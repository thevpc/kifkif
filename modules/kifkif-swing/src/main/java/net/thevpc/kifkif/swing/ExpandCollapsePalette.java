package net.thevpc.kifkif.swing;

import net.thevpc.common.swing.SwingUtilities3;
import net.thevpc.common.swing.prs.PRSManager;

import java.awt.event.ActionEvent;

import javax.swing.JToolBar;


/**
 * @author vpc
 *         Date: 21 janv. 2005
 *         Time: 17:57:14
 */
class ExpandCollapsePalette extends DefaultPalette {
    private AbstractAction2 resultCollapse;
    private AbstractAction2 resultExpand;

    public ExpandCollapsePalette(Kkw kkwInstance) {
        super(kkwInstance);

        addAction(resultCollapse = new AbstractAction2("resultCollapseButton") {
            public void actionPerformed(ActionEvent e) {
                SwingUtilities3.collapseAll(kkw.getResultTree());
            }
        });
        addAction(resultExpand = new AbstractAction2("resultExpandButton") {
            public void actionPerformed(ActionEvent e) {
                SwingUtilities3.expandAll(kkw.getResultTree());
            }
        });
        onChange();
    }

    public void installPalette(JToolBar jToolBar) {
        jToolBar.addSeparator();
        PRSManager.addSupport(jToolBar.add(resultCollapse),"resultCollapseButton");
        PRSManager.addSupport(jToolBar.add(resultExpand),"resultExpandButton");;
    }

    protected void onChange() {
        boolean someSelection = kkw.getResultTree().getSearchData().getDuplicateLists().size() > 0;
        resultCollapse.setEnabled(someSelection && !kkw.isProcessing());
        resultExpand.setEnabled(someSelection && !kkw.isProcessing());
    }

}
