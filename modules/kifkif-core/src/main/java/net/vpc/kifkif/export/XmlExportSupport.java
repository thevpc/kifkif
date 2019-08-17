package net.vpc.kifkif.export;

import net.vpc.common.prs.xml.XmlUtils;
import net.vpc.kifkif.SearchData;
import net.vpc.kifkif.swing.Kkw;

import javax.swing.*;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.Map;

/**
 * @author vpc
 *         Date: 16 janv. 2005
 *         Time: 16:19:06
 */
public class XmlExportSupport implements ExportSupport {

    public XmlExportSupport() {
    }

    public String getName() {
        return getClass().getSimpleName();
    }

    public boolean export(SearchData searchData, OutputStream stream, Map<String, Object> properties) throws ExportException, IOException {
        Kkw kkw = ((properties == null) ? (Kkw) null : (Kkw) properties.get(ExportSupport.KKW_PROPERTY));
        File outFile = ((properties == null) ? (File) null : (File) properties.get(ExportSupport.FILE_PROPERTY));
        PrintStream out = null;
        boolean shouldCloseStream = false;
        if (stream == null && outFile != null) {
            out = new PrintStream(outFile);
            shouldCloseStream = true;
        } else if (stream != null) {
            if (stream instanceof PrintStream) {
                out = (PrintStream) stream;
            } else {
                out = new PrintStream(stream);
                shouldCloseStream = true;
            }
        } else {
            JFileChooser chooser = new JFileChooser();
            String old = kkw.getConfiguration().getString("TextExportSupport.selected");
            if (old != null) {
                chooser.setSelectedFile(new File(old));
            }
            if (searchData.getDuplicateLists().size() > 0 && JFileChooser.APPROVE_OPTION == chooser.showSaveDialog(kkw == null ? null : kkw.getMainPanel())) {
                File f = chooser.getSelectedFile();
                if (f.getName().indexOf('.') < 0) {
                    f = new File(f.getPath() + ".xml");
                }
                try {
                    kkw.getConfiguration().setString("TextExportSupport.selected", f.getCanonicalPath());
                } catch (IOException e) {
                    kkw.getConfiguration().setString("TextExportSupport.selected", f.getAbsolutePath());
                }
                out = new PrintStream(f);
                shouldCloseStream = true;
            }
        }

        if (out == null) {
            return false;
        }

        try {
            XmlUtils.objectToXml(searchData, out, null, null);
            return true;
        } finally {
            if (shouldCloseStream) {
                out.close();
            }
        }

    }
}
