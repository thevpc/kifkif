package net.vpc.kifkif.swing;

import net.vpc.common.swings.DumbGridBagLayout;
import net.vpc.common.swings.prs.PRSManager;

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
        JPanel panel = new JPanel(
                new DumbGridBagLayout()
                        .addLine("[<fml+    ]:[fmt+==  ]:[<fmb    ]")
                        .addLine("[<am+=     :          :         ]")
                        .addLine("[<croo+=   :          :         ]")
                        .addLine("[<oreg+=   :          :         ]")
                        .addLine("[<oinse+=   :          :         ]")
        );
        panel.add(nativeCommandLabel, "fml");
        panel.add(nativeCommandText, "fmt");
        panel.add(browseCommand, "fmb");
        panel.add(autoMarkFilesToDeleteCheck, "am");
        panel.add(clearResultOnNewSearchCheck, "croo");
        panel.add(optionRegexpCheck, "oreg");
        panel.add(optionInsensitiveCheck, "oinse");
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
