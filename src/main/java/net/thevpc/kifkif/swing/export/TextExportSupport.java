package net.thevpc.kifkif.swing.export;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.Collection;
import java.util.Map;

import javax.swing.JFileChooser;

import net.thevpc.kifkif.DuplicateList;
import net.thevpc.kifkif.SearchData;
import net.thevpc.kifkif.swing.Kkw;
import net.thevpc.common.swing.util.Chronometer;

/**
 * @author vpc
 * Date: 16 janv. 2005
 * Time: 16:19:06
 */
public class TextExportSupport implements ExportSupport {

    public TextExportSupport() {
    }

    public String getName(){
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
            String old=kkw.getConfiguration().getString("TextExportSupport.selected");
            if(old!=null){
                chooser.setSelectedFile(new File(old));
            }
            if (searchData.getDuplicateLists().size() > 0 && JFileChooser.APPROVE_OPTION == chooser.showSaveDialog(kkw == null ? null : kkw.getMainPanel())) {
                File f = chooser.getSelectedFile();
                if (f.getName().indexOf('.') < 0) {
                    f = new File(f.getPath() + ".txt");
                }
                try {
                    kkw.getConfiguration().setString("TextExportSupport.selected",f.getCanonicalPath());
                } catch (IOException e) {
                    kkw.getConfiguration().setString("TextExportSupport.selected",f.getAbsolutePath());
                }
                out = new PrintStream(f);
                shouldCloseStream = true;
            }
        }

        if (out == null) {
            return false;
        }

        Collection<File> selected = searchData.getSelectedDuplicateFiles();
        Collection<DuplicateList> duplicateLists = searchData.getDuplicateLists();

        try {
            int folderGroups = 1;
            int fileGroups = 1;
            for (DuplicateList duplicateList : duplicateLists) {
                if (duplicateList.isFolderList()) {
                    out.println("Duplicate Folder Group #" + folderGroups);
                    folderGroups++;
                } else {
                    out.println("Duplicate File Group #" + fileGroups);
                    fileGroups++;
                }
                for (File file1 : duplicateList.getFiles()) {
                    String suffix = "";
                    if (selected != null && selected.contains(file1)) {
                        suffix = " (*)";
                    }
                    out.println("\t" + file1.getPath() + suffix);
                }
            }
            String elapsedTime = Chronometer.formatPeriodShort(searchData.getStatistics().getStatsElapsedTime(),Chronometer.DatePart.s);
            out.printf("-------------------\n" +
                    "STATISTICS:\n" +
                    " Exec time : %s\n" +
                    " Source : %d folder(s) and %d file(s)\n" +
                    " Folder Groups : %d ; File Groups : %d\n" +
                    " Total Duplicates  : %d\n" +
                    " Folder Duplicates : %d\n" +
                    " File Duplicates   : %d\n" +
                    "\n" +
                    "N.B. files with (*) are duplicate and could safely be deleted\n" +
                    "N.B. files with (**) are already included in duplicate folders and could safely be deleted",
                    String.valueOf(elapsedTime),
                    searchData.getStatistics().getSourceFoldersCount(),
                    searchData.getStatistics().getSourceFilesCount(),
                    folderGroups,
                    fileGroups,
                    (searchData.getStatsSelectedFileDuplicatesCount() + searchData.getStatsSelectedFolderDuplicatesCount()),
                    searchData.getStatsSelectedFolderDuplicatesCount(),
                    searchData.getStatsSelectedFileDuplicatesCount());
            return true;
        } finally {
            if (shouldCloseStream) {
                out.close();
            }
        }

    }
}
