package net.thevpc.kifkif.swing;

import java.awt.BorderLayout;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;

import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import net.thevpc.kifkif.KifKifVersion;

/**
 * @author vpc
 * Date: 24 janv. 2005
 * Time: 18:49:49
 */
public class AboutPanel extends JPanel {
    JLabel label = null;

    public AboutPanel() {
        super(new BorderLayout());
        label = new JLabel(getString(null));
        add(label);
        new Thread() {
            public void run() {
                label.setText(getString(latestVersion()));
                JDialog d = (JDialog) SwingUtilities.getAncestorOfClass(JDialog.class, label);
                d.pack();
            }
        }.start();
    }

    private String latestVersion() {
        BufferedReader b = null;
        try {
            URL url = new URL("https://kifkif.dev.java.net/currentVersion.txt");
            b = new BufferedReader(new InputStreamReader(url.openStream()));
            String line = null;
            while ((line = b.readLine()) != null) {
                line = line.trim();
                if (line.startsWith("#")) {
                    continue;
                } else if (line.startsWith("currentVersion=")) {
                    return line.substring("currentVersion=".length()).trim();
                }
            }
            return "";
        } catch (IOException e) {
            System.err.println(e);
            return "";
        } finally {
            if (b != null) {
                try {
                    b.close();
                } catch (IOException e) {
                }
            }
        }
    }

    private String getString(String latestVersion) {
        if (latestVersion != null && latestVersion.length() > 0 && KifKifVersion.PRODUCT_VERSION.compareToIgnoreCase(latestVersion) >= 0) {
            latestVersion = "";
        }
        return "<HTML><CENTER>" +
                "<H1>" + KifKifVersion.PRODUCT_NAME + " v" + KifKifVersion.PRODUCT_VERSION + "</H1>" +
                " <P>by " + KifKifVersion.AUTHOR_NAME + "</P>"
                + "<P> " + KifKifVersion.AUTHOR_MAIL + "</P>"
                + "<P> " + KifKifVersion.PRODUCT_URL + "</P>"
                + (latestVersion == null ? "<P><Font color=blue><I>checking for newest version...</I></Font></P>" : (latestVersion.length() == 0) ? "" : ("<P><Font color=RED><I>Newest version " + latestVersion + " is available</I></Font></P>"))
                + "</CENTER></HTML>";
    }
}
