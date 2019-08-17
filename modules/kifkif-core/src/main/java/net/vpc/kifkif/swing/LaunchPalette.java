package net.vpc.kifkif.swing;

import net.vpc.common.swings.SwingUtilities3;

import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.IOException;
import java.util.StringTokenizer;

import javax.swing.AbstractAction;
import javax.swing.JOptionPane;
import javax.swing.JToolBar;

/**
 * @author vpc
 *         Date: 21 janv. 2005
 *         Time: 17:57:14
 */
class LaunchPalette extends DefaultPalette {
    private AbstractAction launchAction;

    private AbstractAction launchParentAction;
    private AbstractAction copyClipboardAction;
    private AbstractAction diffAction;

    public LaunchPalette(Kkw kkwInstance) {
        super(kkwInstance);

        addAction(launchAction = new AbstractAction2("resultOpenInShellButton") {
            public void actionPerformed(ActionEvent e) {
                File file = kkw.getResultTree().getSelectedFile();
                if (file != null) {
                    actionOpen(file);
                }
            }
        });

        addAction(launchParentAction = new AbstractAction2("resultOpenParentInShellButton") {
            public void actionPerformed(ActionEvent e) {
                File file = kkw.getResultTree().getSelectedFile();
                if (file != null) {
                    file = file.getParentFile();
                    if (file != null) {
                        actionOpen(file);
                    }
                }
            }
        });
        addAction(copyClipboardAction = new AbstractAction2("resultCopyPathClipboard") {
            public void actionPerformed(ActionEvent e) {
                File file = kkw.getResultTree().getSelectedFile();
                if (file != null) {
                    Clipboard cp = Toolkit.getDefaultToolkit().getSystemClipboard();
                    try {
                        cp.setContents(new StringSelection(String.valueOf(file.getCanonicalPath())),null);
                    } catch (IOException e1) {
                        cp.setContents(new StringSelection(String.valueOf(file.getAbsolutePath())),null);
                    }
                }
            }
        });
        addAction(diffAction = new AbstractAction2("resultDiff") {
            public void actionPerformed(ActionEvent e) {
                final File[] selectedFiles = kkw.getResultTree().getSelectedFiles();
                actionDiff(selectedFiles);
            }
        });
        onChange();
    }

    public void prepareAllComponents() {
//        kkw.getResourcesSwingHelper().prepareButton(resultOpenInShell);
    }

    public void installPalette(JToolBar jToolBar) {
        jToolBar.addSeparator();
        kkw.getResultTree().getPopupMenu().add(launchAction);
        kkw.getResultTree().getPopupMenu().add(launchParentAction);
        kkw.getResultTree().getPopupMenu().add(copyClipboardAction);
        kkw.getResultTree().getPopupMenu().add(diffAction);
    }

    protected void onChange() {
        final int length = kkw.getResultTree().getSelectedFiles().length;
//        resultOpenInShell.setEnabled(someSelection);
        final boolean notprocessing = !kkw.isProcessing();
        launchAction.setEnabled(length==1 && notprocessing);
        launchParentAction.setEnabled(length==1 && notprocessing);
        copyClipboardAction.setEnabled(length==1 && notprocessing);
        diffAction.setEnabled((length == 2 || length == 3) && notprocessing);
    }

    private void actionOpen(File file) {
        String cmd = kkw.getConfiguration().getString(KkwOptionDialog.OPTION_NATIVE_OPEN_CMD);
        while (true) {
            if (cmd == null || cmd.length() == 0) {
                cmd = SwingUtilities3.getDefaultNativeOpenFileCommand();
            }
            StringTokenizer st = new StringTokenizer(cmd);
            String[] cmdarray = new String[st.countTokens()];
            for (int i = 0; st.hasMoreTokens(); i++) {
                cmdarray[i] = st.nextToken();
                if (cmdarray[i].equals("%f")) {
                    cmdarray[i] = file.getAbsolutePath();
                }
            }
            try {
                Runtime.getRuntime().exec(cmdarray);
                kkw.getConfiguration().setString(KkwOptionDialog.OPTION_NATIVE_OPEN_CMD, cmd);
                return;
            } catch (IOException e1) {
                String c = JOptionPane.showInputDialog(kkw.getMainPanel(),
                        kkw.getResources().get("openFileCmdError"),
                        cmd);
                if (c == null) {
                    return;
                }
                cmd = c;
            }
        }
    }

    private void actionDiff(File[] file) {
        if(file.length!=2 && file.length!=3){
            JOptionPane.showMessageDialog(kkw.getMainPanel(),
                        kkw.getResources().get("diffFileCmdError"),
                    "Error",JOptionPane.ERROR_MESSAGE);
            return;
        }
        String cmd2 = kkw.getConfiguration().getString(KkwOptionDialog.OPTION_NATIVE_DIFF2_CMD);
        if(cmd2==null){
            cmd2="/usr/bin/meld %1 %2";
        }
        String cmd3 = kkw.getConfiguration().getString(KkwOptionDialog.OPTION_NATIVE_DIFF2_CMD);
        if(cmd3==null){
            cmd3="/usr/bin/meld %1 %2 %3";
        }
        String cmd=file.length==2?cmd2:cmd3;
        while (true) {
            for(int j=1;j<=file.length;j++){
                if (cmd.indexOf("%"+j)<0) {
                    cmd+= (" "+"%"+j);
                }
            }
            StringTokenizer st = new StringTokenizer(cmd);
            String[] cmdarray = new String[st.countTokens()];
            for (int i = 0; st.hasMoreTokens(); i++) {
                cmdarray[i] = st.nextToken();
                for(int j=1;j<=file.length;j++){
                    if (cmdarray[i].equals("%"+j)) {
                        cmdarray[i] = file[j-1].getAbsolutePath();
                    }
                }
            }
            try {
                Runtime.getRuntime().exec(cmdarray);
                return;
            } catch (IOException e1) {
                String c = JOptionPane.showInputDialog(kkw.getMainPanel(),
                        kkw.getResources().get("diffFileCmdError"),
                        cmd);
                if (c == null) {
                    return;
                }
                cmd = c;
            }
        }
    }
}
