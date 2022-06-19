package net.thevpc.kifkif.swing;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Stack;

import javax.swing.*;
import javax.swing.event.TreeModelEvent;
import javax.swing.event.TreeModelListener;

import net.thevpc.common.swing.label.MemoryUseIconTray;
import net.thevpc.common.time.DatePart;
import net.thevpc.common.time.TimeDuration;
import net.thevpc.nuts.NutsMessage;
import net.thevpc.nuts.NutsSession;
import net.thevpc.nuts.util.NutsProgressEventType;
import net.thevpc.nuts.util.NutsProgressHandler;
import net.thevpc.nuts.util.NutsProgressHandlerEvent;
import net.thevpc.nuts.util.NutsProgressMonitorModel;

/**
 * @author vpc Date: 19 janv. 2005 Time: 16:36:41
 */
class StatusbarTaskMonitor implements NutsProgressHandler {

    private JComponent box;
    private JProgressBar progressBar = new JProgressBar(JProgressBar.HORIZONTAL);
    private JLabel percentLabel = new JLabel();
    private JButton extraLabel = new JButton();
    private JLabel chronoLabel = new JLabel();
    private MemoryUseIconTray memoryUseIconTray = new MemoryUseIconTray(true);
    private Kkw kkw;
    private Timer timer;
    private ActionListener timerAction;
    private long startTime;
    private long endTime;
    private PropertyChangeSupport support;
    public static final String PROPERTY_STAT_CHANGED = "PROPERTY_STAT_CHANGED";
    private double currentProgress;
    private NutsMessage currentMessage;

    public StatusbarTaskMonitor() {
        support = new PropertyChangeSupport(this);
        JPanel b = new JPanel(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(3, 3, 3, 3);
        c.gridx = 0;
        c.gridy = 0;
        c.gridwidth = 4;
        c.gridheight = 1;
        c.weightx = 1;
        c.weighty = 1;
        c.fill = GridBagConstraints.BOTH;
        b.add(progressBar, c);

        c.gridx = 5;
        c.gridy = 0;
        c.gridwidth = 1;
        c.gridheight = 1;
        c.weightx = 0;
        c.weighty = 0;
        c.fill = GridBagConstraints.BOTH;
        b.add(percentLabel, c);

        c.gridx = 6;
        c.gridy = 0;
        c.gridwidth = 1;
        c.gridheight = 1;
        c.weightx = 0;
        c.weighty = 0;
        c.fill = GridBagConstraints.BOTH;
        b.add(chronoLabel, c);

        c.gridx = 7;
        c.gridy = 0;
        c.gridwidth = 1;
        c.gridheight = 1;
        c.weightx = 0;
        c.weighty = 0;
        c.fill = GridBagConstraints.BOTH;
        b.add(extraLabel, c);

        c.gridx = 8;
        c.gridy = 0;
        c.gridwidth = 1;
        c.gridheight = 1;
        c.weightx = 0;
        c.weighty = 0;
        c.fill = GridBagConstraints.BOTH;
        b.add(memoryUseIconTray, c);

        this.progressBar.setStringPainted(true);
        this.progressBar.setString("");

        box = b;

        percentLabel.setMinimumSize(new Dimension(80, 10));
        percentLabel.setPreferredSize(new Dimension(80, 10));
        percentLabel.setBorder(BorderFactory.createEtchedBorder());
        percentLabel.setHorizontalTextPosition(SwingConstants.CENTER);
        percentLabel.setHorizontalAlignment(SwingConstants.CENTER);

        extraLabel.setMinimumSize(new Dimension(80, 10));
        extraLabel.setPreferredSize(new Dimension(80, 10));
        extraLabel.setBorder(BorderFactory.createEtchedBorder());
        extraLabel.setHorizontalTextPosition(SwingConstants.CENTER);
        extraLabel.setHorizontalAlignment(SwingConstants.CENTER);
        extraLabel.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                extraType++;
                if (extraType > 4) {
                    extraType = 1;
                }
                updateExtra();
            }
        });

        chronoLabel.setMinimumSize(new Dimension(80, 10));
        chronoLabel.setPreferredSize(new Dimension(80, 10));
        chronoLabel.setBorder(BorderFactory.createEtchedBorder());
        chronoLabel.setHorizontalTextPosition(SwingConstants.CENTER);
        chronoLabel.setHorizontalAlignment(SwingConstants.CENTER);
        timerAction = new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                long p = ((timer == null) ? endTime : System.currentTimeMillis()) - startTime;
                chronoLabel.setText(TimeDuration.ofMillis(p).formatShort(DatePart.SECOND));
            }
        };
    }

    public void setMax(long max) {
        progressBar.setMaximum((int) max);
    }

    public long getMax() {
        return progressBar.getMaximum();
    }

    @Override
    public void onEvent(NutsProgressHandlerEvent event) {
        this.currentMessage = event.getModel().getMessage();
        this.currentProgress = event.getModel().getProgress();
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                int value = progressBar.getValue();
                long index = (int) (currentProgress * progressBar.getMaximum());
                String percentString = String.valueOf((((long) (currentProgress * 10000)) / 100.0) + "%");
                int newValue = (int) index;
                progressBar.setValue(newValue);
                percentLabel.setText(percentString + " " + (currentMessage == null ? "" : currentMessage.toString()));
                support.firePropertyChange(PROPERTY_STAT_CHANGED, Boolean.FALSE, Boolean.TRUE);
                if (value != newValue) {
                    System.out.println(newValue);
                }
            }
        });
    }

    public long getIndex() {
        return progressBar.getValue();
    }

    //    @Override
    public void setStopped(boolean stopped) {
//        super.setStopped(stopped);
//        if (isIndeterminate()) {
//            progressBar.setIndeterminate(stopped);
//        }
        if (stopped && timer != null) {
            Timer t = timer;
            timer = null;
            t.stop();
            endTime = System.currentTimeMillis();
            timerAction.actionPerformed(null);
        } else if (!stopped && timer == null) {
            timer = new Timer(1000, timerAction);
            timer.start();
            startTime = System.currentTimeMillis();
            timerAction.actionPerformed(null);
        }
    }

    public void setKkw(Kkw kkw) {
        this.kkw = kkw;
        kkw.getResultTree().getModel().addTreeModelListener(new TreeModelListener() {
            public void treeNodesChanged(TreeModelEvent e) {
                recalculate();
            }

            public void treeNodesInserted(TreeModelEvent e) {
            }

            public void treeNodesRemoved(TreeModelEvent e) {
            }

            public void treeStructureChanged(TreeModelEvent e) {
                recalculate();
            }
        });
        kkw.getResultTree().addPropertyChangeListener(JTree.TREE_MODEL_PROPERTY,
                new PropertyChangeListener() {
                    public void propertyChange(PropertyChangeEvent evt) {
                        recalculate();
                    }
                });
        recalculate();
    }

    public Component getComponent() {
        return box;
    }

    public long getProcessTime() {
        return endTime - startTime;
    }

    public long getStartTime() {
        return startTime;
    }

    private int extraSelectedFiles;
    private int extraSelectedFolders;
    private int extraSelectedAll;
    private int extraSizeAll;
    public static final int EXTRA_SELECTED_COUNT_FILES = 1;
    public static final int EXTRA_SELECTED_COUNT_FOLDERS = 2;
    public static final int EXTRA_SELECTED_COUNT_ALL = 3;
    public static final int EXTRA_SELECTED_SIZE_ALL = 4;
    int extraType = EXTRA_SELECTED_SIZE_ALL;
    private boolean mustRecalculate;
    private boolean isRecalculate;

    private void updateExtra() {
        switch (extraType) {
            case EXTRA_SELECTED_COUNT_FILES: {
                extraLabel.setText(extraSelectedFiles < 0 ? "..."
                        : kkw.getResources().get2("extra.countFiles", extraSelectedFiles));
                extraLabel.setToolTipText(kkw.getResources().get("extra.countFiles.tooltip"));
                break;
            }
            case EXTRA_SELECTED_COUNT_FOLDERS: {
                extraLabel.setText(extraSelectedFolders < 0 ? "..."
                        : kkw.getResources().get2("extra.countFolders", extraSelectedFolders));
                extraLabel.setToolTipText(kkw.getResources().get("extra.countFolders.tooltip"));
                break;
            }
            case EXTRA_SELECTED_COUNT_ALL: {
                extraLabel.setText(extraSelectedAll < 0 ? "..."
                        : kkw.getResources().get2("extra.countAll", extraSelectedAll));
                extraLabel.setToolTipText(kkw.getResources().get("extra.countAll.tooltip"));
                break;
            }
            case EXTRA_SELECTED_SIZE_ALL: {
                extraLabel.setText(extraSizeAll < 0 ? "..." : kkw.getFileSizeString(extraSizeAll));
                extraLabel.setToolTipText(kkw.getResources().get("extra.sizeAll.tooltip"));
                break;
            }
        }
        support.firePropertyChange(PROPERTY_STAT_CHANGED, Boolean.FALSE, Boolean.TRUE);
//        System.out.println(">> "+extraLabel.getText()+" : "+extraSelectedFiles+"/"+extraSelectedFolders+"/"+extraSelectedAll+"/"+extraSizeAll);
    }

    public void recalculate() {
        mustRecalculate = true;
        if (isRecalculate) {
            return;
        }
        isRecalculate = true;
        new Thread() {
            @Override
            public void run() {
                try {
                    extraSelectedFiles = -1;
                    extraSelectedFolders = -1;
                    extraSelectedAll = -1;
                    extraSizeAll = -1;
                    while (mustRecalculate) {
                        doIt();
                    }
                } finally {
                    isRecalculate = false;
//                    System.out.println("recalculated successfully");
                }
            }

            private void doIt() {
//                System.out.println(">> doIt");
                mustRecalculate = false;
                DuplicateListTree resultTree = kkw.getResultTree();
                Collection<File> selectedFiles = new ArrayList<File>(resultTree.getSearchData().getSelectedDuplicateFiles());
                extraSelectedFiles = 0;
                extraSelectedFolders = 0;
                extraSelectedAll = 0;
                extraSizeAll = 0;
                for (File file : selectedFiles) {
                    if (mustRecalculate) {
                        return;
                    }
                    if (file.isFile()) {
                        extraSelectedFiles++;
                        extraSelectedAll++;
                        extraSizeAll += file.length();
                        updateExtra();
                    } else {
                        extraSelectedFolders++;
                        extraSelectedAll++;
                        updateExtra();
                        Stack<File> stack = new Stack<File>();
                        updateExtra();
                        stack.push(file);
                        while (!stack.isEmpty()) {
                            if (mustRecalculate) {
                                return;
                            }
                            File f = stack.pop();
                            if (f.isFile()) {
                                extraSizeAll += f.length();
                                updateExtra();
                            } else if (f.isDirectory()) {
                                File[] c = f.listFiles();
                                if (c != null) {
                                    for (File f2 : c) {
                                        if (mustRecalculate) {
                                            return;
                                        }
                                        stack.push(f2);
                                    }
                                }
                            }
                        }
                    }
                }
                updateExtra();
            }
        }.start();
    }

    public void addPropertyChageListener(String property, PropertyChangeListener listener) {
        support.addPropertyChangeListener(property, listener);
    }

    public int getExtraSelectedFiles() {
        return extraSelectedFiles;
    }

    public int getExtraSelectedFolders() {
        return extraSelectedFolders;
    }

    public int getExtraSelectedAll() {
        return extraSelectedAll;
    }

    public int getExtraSizeAll() {
        return extraSizeAll;
    }

    public String getPercentText() {
        return percentLabel.getText();
    }

    public String getChronoText() {
        return chronoLabel.getText();
    }
}
