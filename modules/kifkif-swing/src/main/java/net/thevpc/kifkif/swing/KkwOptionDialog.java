package net.thevpc.kifkif.swing;

import net.thevpc.common.swing.layout.DumbGridBagLayout;
import net.thevpc.common.swing.layout.GridBagLayoutSupport;
import net.thevpc.common.swing.prs.PRSManager;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.IOException;

/**
 * @author : vpc
 * @creationtime 7 janv. 2006 09:35:45
 */
public class KkwOptionDialog {
    public static final String OPTION_NATIVE_OPEN_CMD = "OPTION_NATIVE_OPEN_CMD";
    public static final String OPTION_NATIVE_DIFF2_CMD = "OPTION_NATIVE_DIFF2_CMD";
    public static final String OPTION_NATIVE_DIFF3_CMD = "OPTION_NATIVE_DIFF3_CMD";
    public static final String OPTION_CLEAR_RESULT_ON_NEW_SEARCH = "OPTION_CLEAR_RESULT_ON_NEW_SEARCH";
    public static final String OPTION_AUTO_MARK_FILES_TO_DELETE = "OPTION_AUTO_MARK_FILES_TO_DELETE";
    public static final String OPTION_REGULAR_EXPRESSIONS = "OPTION_REGULAR_EXPRESSIONS";
    public static final String OPTION_INSENSITIVE_NAMES = "OPTION_INSENSITIVE_NAMES";
    JLabel nativeCommandLabel;
    JTextField nativeCommandText;
    JCheckBox clearResultOnNewSearchCheck;
    JCheckBox autoMarkFilesToDeleteCheck;
    private JCheckBox optionRegexpCheck;
    private JCheckBox optionInsensitiveCheck;
    JButton browseCommand;
    Kkw kkw;

    public KkwOptionDialog(Kkw kkw) {
        this.kkw = kkw;
        nativeCommandLabel = PRSManager.createLabel("nativeCommandLabel");
        nativeCommandText = new JTextField("");
        optionInsensitiveCheck = PRSManager.createCheck("optionInsensitiveCheck", true);
        optionRegexpCheck = PRSManager.createCheck("optionRegexpCheck", false);
        clearResultOnNewSearchCheck = PRSManager.createCheck("clearResultOnNewSearchCheck", true);
        autoMarkFilesToDeleteCheck = PRSManager.createCheck("autoMarkFilesToDeleteCheck", true);
        browseCommand = PRSManager.createButton("nativeCommandButton");
        browseCommand.setMargin(new Insets(0, 0, 0, 0));
        browseCommand.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                doBrowse();
            }
        });
        nativeCommandText.addPropertyChangeListener("enabled", new PropertyChangeListener() {
            public void propertyChange(PropertyChangeEvent evt) {
                browseCommand.setEnabled(nativeCommandText.isEnabled());
            }
        });
    }

    public void showDialog() {
        String cmd = kkw.getConfiguration() == null ? null : kkw.getConfiguration().getString(OPTION_NATIVE_OPEN_CMD);
        clearResultOnNewSearchCheck.setSelected(kkw.getConfiguration().getBoolean(OPTION_CLEAR_RESULT_ON_NEW_SEARCH, true));
        autoMarkFilesToDeleteCheck.setSelected(kkw.getConfiguration().getBoolean(OPTION_AUTO_MARK_FILES_TO_DELETE, true));
        autoMarkFilesToDeleteCheck.setSelected(kkw.getConfiguration().getBoolean(OPTION_AUTO_MARK_FILES_TO_DELETE, true));
        optionInsensitiveCheck.setSelected(kkw.getConfiguration().getBoolean(OPTION_INSENSITIVE_NAMES, false));
        optionRegexpCheck.setSelected(kkw.getConfiguration().getBoolean(OPTION_REGULAR_EXPRESSIONS, false));
        nativeCommandText.setText(cmd);
//        PRSManager.prepareLabel(nativeCommandLabel);
//        PRSManager.prepareLabel(nativeCommandLabel);
//        PRSManager.prepareButton(browseCommand);
//        PRSManager.prepareButton(clearResultOnNewSearchCheck);
//        PRSManager.prepareButton(autoMarkFilesToDeleteCheck);
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagLayoutSupport s = new GridBagLayoutSupport(
                "[<fml+    ]:[fmt+==  ]:[<fmb    ]\n"+
                        "[<am+=     :          :         ]\n"+
                        "[<croo+=   :          :         ]\n"+
                        "[<oreg+=   :          :         ]\n"+
                        "[<oinse+=   :          :         ]\n"
        );
        s.setInsets(".*",new Insets(5,5,5,5));
        panel.add(nativeCommandLabel, s.getConstraints("fml"));
        panel.add(nativeCommandText, s.getConstraints("fmt"));
        panel.add(browseCommand, s.getConstraints("fmb"));
        panel.add(autoMarkFilesToDeleteCheck, s.getConstraints("am"));
        panel.add(clearResultOnNewSearchCheck, s.getConstraints("croo"));
        panel.add(optionRegexpCheck, s.getConstraints("oreg"));
        panel.add(optionInsensitiveCheck, s.getConstraints("oinse"));
        PRSManager.update(panel, kkw);
        if (JOptionPane.OK_OPTION == JOptionPane.showConfirmDialog(kkw.getMainPanel(), panel, kkw.getResources().get("optionsMenu"), JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE)) {
            kkw.getConfiguration().setString(OPTION_NATIVE_OPEN_CMD, nativeCommandText.getText());
            kkw.getConfiguration().setBoolean(OPTION_CLEAR_RESULT_ON_NEW_SEARCH, clearResultOnNewSearchCheck.isSelected());
            kkw.getConfiguration().setBoolean(OPTION_AUTO_MARK_FILES_TO_DELETE, autoMarkFilesToDeleteCheck.isSelected());
            kkw.getConfiguration().setBoolean(OPTION_REGULAR_EXPRESSIONS, optionRegexpCheck.isSelected());
            kkw.getConfiguration().setBoolean(OPTION_INSENSITIVE_NAMES, optionInsensitiveCheck.isSelected());
        }
    }

    private void doBrowse() {
        JFileChooser jfc = new JFileChooser();
        int ret = jfc.showOpenDialog(browseCommand);
        if (ret == JFileChooser.APPROVE_OPTION) {
            try {
                nativeCommandText.setText(jfc.getSelectedFile().getCanonicalPath());
            } catch (IOException e1) {
                nativeCommandText.setText(jfc.getSelectedFile().getAbsolutePath());
            }
        }
    }
}
