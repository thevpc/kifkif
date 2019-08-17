package net.vpc.app.kifkif.swing;

import net.vpc.app.kifkif.DuplicateList;
import net.vpc.common.swings.DumbGridBagLayout;
import net.vpc.common.swings.prs.PRSManager;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

/**
 * @author vpc
 *         Date: 21 janv. 2005
 *         Time: 17:57:14
 */
class DeletePalette extends DefaultPalette {

    private Action resultHideThisAction;
    private Action resultDeleteThisAction;
    private Action resultDeleteSelected;

    public DeletePalette(Kkw kkwInstance) {
        super(kkwInstance);
        addAction(resultHideThisAction = new AbstractAction2("resultHideThisButton") {

            public void actionPerformed(ActionEvent e) {
                kkw.getResultTree().hideSelectedFiles();
            }
        });
        addAction(resultDeleteThisAction = new AbstractAction2("resultDeleteThisButton") {

            public void actionPerformed(ActionEvent e) {
                File[] selectedFiles = kkw.getResultTree().getSelectedFiles();
                showDeleteBatch(Arrays.asList(selectedFiles));
            }
        });
        addAction(resultDeleteSelected = new AbstractAction2("resultDeleteBatchButton") {

            public void actionPerformed(ActionEvent e) {
                showDeleteBatch(kkw.getResultTree().getSearchData().getSelectedDuplicateFiles());
            }
        });

        onChange();
    }

    private void showDeleteBatch(Collection<File> files) {
        files=new ArrayList<File>(files);//copy it to avoid concurrence problems
        if (JOptionPane.YES_OPTION == JOptionPane.showConfirmDialog(getKkw().getMainPanel(),
                kkw.getResources().get2("msg.ResultDeleteSelected.PreWarning", files.size()),
                kkw.getResources().get("msg.warning"),
                JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE)) {
            int deleted = 0;
            boolean ignoreAll = false;
            for (File file : files) {
                if (!deleteFile(file)) {
                    if (!ignoreAll) {
                        JPanel p = new JPanel(
                                new DumbGridBagLayout().addLine("[<L1]").addLine("[>L2]"));
                        JCheckBox ignoreAllCheckBox = new JCheckBox(kkw.getResources().get2("msg.ResultDeleteSelected.IgnoreAll", file), false);
                        p.add(new JLabel(kkw.getResources().get2("msg.ResultDeleteSelected.UnableToDeleteFile", file)), "L1");
                        p.add(ignoreAllCheckBox, "L2");
                        if (JOptionPane.YES_OPTION != JOptionPane.showConfirmDialog(getKkw().getMainPanel(),
                                p,
                                kkw.getResources().get("msg.warning"),
                                JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE)) {
                            break;
                        }
                        if (ignoreAllCheckBox.isSelected()) {
                            ignoreAll = true;
                        }
                    }
                } else {
                    deleted++;
                }
            }
            JOptionPane.showMessageDialog(getKkw().getMainPanel(), kkw.getResources().get2("msg.ResultDeleteSelected.Summary", deleted, files.size()));
        }
    }

    public void installPalette(JToolBar jToolBar) {
        jToolBar.addSeparator();
        PRSManager.addSupport(jToolBar.add(resultDeleteSelected), "resultDeleteBatchButton");
        kkw.getResultTree().getPopupMenu().add(resultHideThisAction);
        kkw.getResultTree().getPopupMenu().add(resultDeleteThisAction);
    }

    protected void onChange() {
        boolean someMarked = kkw.getResultTree().getSearchData().getSelectedDuplicateFiles().size() > 0;
        boolean someSelected = kkw.getResultTree().getSelectedFiles().length > 0;
        resultDeleteSelected.setEnabled(someMarked && !kkw.isProcessing());
        resultDeleteThisAction.setEnabled(someSelected && !kkw.isProcessing());
        resultHideThisAction.setEnabled(someSelected && !kkw.isProcessing());
    }

    private boolean deleteFile(File file) {
        boolean yes = true;
        if (file.isDirectory()) {
            File[] children = file.listFiles();
            for (File child : children) {
                if (!deleteFile(child)) {
                    yes = false;
                }
            }
            if (yes) {
                yes = file.delete();
            }
            if (yes) {
                DuplicateList listByFile = kkw.getResultTree().findListByFile(file);
                if (listByFile != null) {
                    listByFile.removeFile(file);
                }
            }
        } else {
            yes = file.delete();
        }
        DuplicateList listByFile = kkw.getResultTree().findListByFile(file);
        if (listByFile != null) {
            listByFile.removeFile(file);
            if (listByFile.getFileCount() == 1) {
                kkw.getResultTree().getSearchData().removeList(listByFile);
            }
        }
        return yes;
    }
}
