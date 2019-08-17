package net.vpc.kifkif.swing;

import java.awt.HeadlessException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.Action;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JCheckBox;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JToolBar;

import net.vpc.kifkif.DefaultFileSet;
import net.vpc.kifkif.FileSet;
import net.vpc.kifkif.KifKif;

/**
 * @author vpc
 * Date: 21 janv. 2005
 * Time: 17:57:14
 */
class MarkForDeletionPalette extends DefaultPalette {
    private Action resultSelectAction;
    private Action autoSelectAction;
    private Action clearSelectAction;
    private Action autoSelectByLocationAction;

    private Action autoDeselectByLocationAction;


    public MarkForDeletionPalette(Kkw kkwInstance) {
        super(kkwInstance);



        addAction(resultSelectAction=new AbstractAction2("resultToggleMarkButton") {
            public void actionPerformed(ActionEvent e) {
                kkw.getResultTree().switchSelectedFiles();
            }
        });
        addAction(autoSelectAction=new AbstractAction2("resultAutoMarkButton") {
            public void actionPerformed(ActionEvent e) {
                kkw.getResultTree().getSearchData().setSelectedDuplicatesAuto();
            }
        });
        addAction(clearSelectAction=new AbstractAction2("resultClearMarkButton") {
            public void actionPerformed(ActionEvent e) {
                kkw.getResultTree().getSearchData().setSelectedDuplicatesNone();
            }
        });
        addAction(autoSelectByLocationAction=new AbstractAction2("resultAutoMarkByLocationButton") {
            public void actionPerformed(ActionEvent e) {
                SelectPanel selectPanel = new SelectPanel(kkw, kkw.getResources().get("resultAutoMarkByLocationButton"));
                int r = JOptionPane.showOptionDialog(kkw.getMainFrame(), selectPanel,
                        kkw.getResources().get("resultAutoMarkByLocationButton"),
                        JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE, null, null, null);
                if (r == JOptionPane.OK_OPTION) {
                    File folder = selectPanel.getSelectedFile();
                    if (folder != null) {
                        kkw.getResultTree().getSearchData().setSelectedDuplicatesByFolderAncestor(folder, true);
                    }
                }
            }
        });

        addAction(autoDeselectByLocationAction=new AbstractAction2("resultAutoClearMarkByLocationButton") {
            public void actionPerformed(ActionEvent e) {
                SelectPanel selectPanel = new SelectPanel(kkw, kkw.getResources().get("resultAutoClearMarkByLocationButton"));
                int r = JOptionPane.showOptionDialog(kkw.getMainFrame(), selectPanel,
                        kkw.getResources().get("resultAutoClearMarkByLocationButton"),
                        JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE, null, null, null);
                if (r == JOptionPane.OK_OPTION) {
                    File folder = selectPanel.getSelectedFile();
                    if (folder != null) {
                        kkw.getResultTree().getSearchData().setSelectedDuplicatesByFolderAncestor(folder, false);
                    }
                }
            }
        });



//        add(autoSelect);
//        add(clearSelect);
//        add(autoSelectByLocation);
//        add(autoDeselectByLocation);
        onChange();
    }

    public void installPalette(JToolBar jToolBar) {
        jToolBar.addSeparator();
//        jToolBar.add(resultSelect);
        jToolBar.add(autoSelectAction);
        jToolBar.add(clearSelectAction);
        jToolBar.add(autoSelectByLocationAction);
        jToolBar.add(autoDeselectByLocationAction);
        kkw.getResultTree().getPopupMenu().add(resultSelectAction);
    }

    protected void onChange() {
        boolean someSelection = kkw.getResultTree().getSelectedFiles().length > 0;
        boolean someDuplicate = kkw.getResultTree().getSearchData().getDuplicateLists().size() > 0;
//        boolean someFileset = kkw.getKifKif().getIncludedFileSetsCount() > 0;
        resultSelectAction.setEnabled(someSelection  && !kkw.isProcessing());
        autoSelectAction.setEnabled(someDuplicate  && !kkw.isProcessing());
        clearSelectAction.setEnabled(someDuplicate  && !kkw.isProcessing());
        autoSelectByLocationAction.setEnabled(someDuplicate  && !kkw.isProcessing());
        autoDeselectByLocationAction.setEnabled(someDuplicate  && !kkw.isProcessing());
    }

    class SelectPanel extends JPanel {
        private File selectedFile;

        public SelectPanel(Kkw kkw, String label) throws HeadlessException {
            setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));
            add(new JLabel(label));
            KifKif kifKif = kkw.getKifKif();
            ButtonGroup buttonGroup = new ButtonGroup();
            for (final FileSet fileSet : kifKif.getIncludedFileSets()) {
                if (fileSet instanceof DefaultFileSet) {
                    if (selectedFile == null) {
                        selectedFile = ((DefaultFileSet) fileSet).getRoot();
                    }
                    JCheckBox b = new JCheckBox(fileSet.toString());
                    buttonGroup.add(b);
                    b.addActionListener(new ActionListener() {
                        public void actionPerformed(ActionEvent e) {
                            selectedFile = ((DefaultFileSet) fileSet).getRoot();
                        }
                    });
                    add(b);
                }
            }
            JCheckBox b = new JCheckBox("...");
            buttonGroup.add(b);
            b.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    JCheckBox b = (JCheckBox) e.getSource();
                    JFileChooser chooser = new JFileChooser();
                    chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
                    if (selectedFile != null) {
                        File f = selectedFile;
                        if (f.isFile()) {
                            f = f.getParentFile();
                        }
                        chooser.setSelectedFile(f);
                    }
                    int ret = chooser.showOpenDialog(net.vpc.kifkif.swing.MarkForDeletionPalette.SelectPanel.this);
                    if (ret == JFileChooser.APPROVE_OPTION) {
                        selectedFile = chooser.getSelectedFile();
                        b.setText(selectedFile.getPath());
                    }
                }
            });
            add(b);
        }

        public File getSelectedFile() {
            return selectedFile;
        }
    }

}
