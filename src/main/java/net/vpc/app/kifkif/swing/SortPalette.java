package net.vpc.app.kifkif.swing;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.AbstractButton;
import javax.swing.ButtonGroup;
import javax.swing.JToggleButton;
import javax.swing.JToolBar;

import net.vpc.app.kifkif.DuplicateList;
import net.vpc.common.swings.prs.PRSManager;

/**
 * @author vpc
 * Date: 21 janv. 2005
 * Time: 17:57:14
 */
class SortPalette extends DefaultPalette {
    private JToggleButton resultSortName = new JToggleButton("");
    private JToggleButton resultSortSize = new JToggleButton("");
    private JToggleButton resultSortDup = new JToggleButton("");
    private JToggleButton resultSortReverse = new JToggleButton("");
    private JToggleButton resultSortFolderFirst = new JToggleButton("");

    public SortPalette(Kkw kkwInstance) {
        super(kkwInstance);
        resultSortName = PRSManager.createToggleButton("resultSortNameButton");
        resultSortSize = PRSManager.createToggleButton("resultSortSizeButton");
        resultSortDup = PRSManager.createToggleButton("resultSortDuplicateButton");
        resultSortReverse = PRSManager.createToggleButton("resultSortReverseButton");
        resultSortFolderFirst = PRSManager.createToggleButton("resultSortFolderFirstButton");

        resultSortFolderFirst.setSelected(true);
        ButtonGroup bg = new ButtonGroup();
        bg.add(resultSortName);
        bg.add(resultSortSize);
        bg.add(resultSortDup);
        resultSortName.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (!resultSortName.isSelected() && !resultSortSize.isSelected() && !resultSortDup.isSelected()) {
                    ((AbstractButton) e.getSource()).setSelected(true);
                }
                sortResultTree();
            }
        });
        resultSortSize.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (!resultSortName.isSelected() && !resultSortSize.isSelected() && !resultSortDup.isSelected()) {
                    ((AbstractButton) e.getSource()).setSelected(true);
                }
                sortResultTree();
            }
        });
        resultSortDup.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (!resultSortName.isSelected() && !resultSortSize.isSelected() && !resultSortDup.isSelected()) {
                    ((AbstractButton) e.getSource()).setSelected(true);
                }
                sortResultTree();
            }
        });
        resultSortFolderFirst.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                sortResultTree();
            }
        });
        resultSortReverse.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                sortResultTree();
            }
        });
        onChange();
    }

    public void sortResultTree() {
        if (resultSortName.isSelected()) {
            kkw.getResultTree().sort(new DuplicateList.FileNameComparator(resultSortReverse.isSelected(), resultSortFolderFirst.isSelected()));
        } else if (resultSortDup.isSelected()) {
            kkw.getResultTree().sort(new DuplicateList.DuplicatesCountComparator(resultSortReverse.isSelected(), resultSortFolderFirst.isSelected()));
        } else if (resultSortSize.isSelected()) {
            kkw.getResultTree().sort(new DuplicateList.FileSizeComparator(resultSortReverse.isSelected(), resultSortFolderFirst.isSelected()));
        }
    }

    protected void onProcessingChange(boolean processing) {
        super.onProcessingChange(processing);
        if(!processing){
            sortResultTree();
        }
    }

    public void installPalette(JToolBar jToolBar) {
        jToolBar.addSeparator();
        jToolBar.add(resultSortName);
        jToolBar.add(resultSortSize);
        jToolBar.add(resultSortDup);
        jToolBar.add(resultSortReverse);
        jToolBar.add(resultSortFolderFirst);
    }

    protected void onChange() {
        boolean someSelection = kkw.getResultTree().getSearchData().getDuplicateLists().size() > 0;
        resultSortName.setEnabled(someSelection && !kkw.isProcessing());
        resultSortSize.setEnabled(someSelection  && !kkw.isProcessing());
        resultSortDup.setEnabled(someSelection  && !kkw.isProcessing());
        resultSortReverse.setEnabled(someSelection  && !kkw.isProcessing());
        resultSortFolderFirst.setEnabled(someSelection  && !kkw.isProcessing());
    }

}
