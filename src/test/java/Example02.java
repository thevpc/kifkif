
import net.thevpc.kifkif.*;
import net.thevpc.kifkif.content.FileByteContentComparator;
import net.thevpc.kifkif.swing.export.ExportException;
import net.thevpc.kifkif.swing.export.TextExportSupport;
import net.thevpc.kifkif.stamp.FileSizeStampFilter;
import net.thevpc.kifkif.stamp.FolderSizeStampFilter;

import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import net.thevpc.kifkif.stamp.FileNameStampFilter;

/**
 * this example prents two different manners to define File filters
 * used for finding duplicates.
 * The first way is very simple but non extensible and uses predifined filters
 * The second way is a bit harder but it makes-it possible to define your
 * own File Filter (by implementing FileContentComparator or Filestamp filter Interfaces)
 *
 * @author vpc
 */
public class Example02 {
    public static void main(String[] args) {
        KifKif kifKif = new KifKif();

        // ********************************
        // FIRST WAY : fast
        // ********************************

        // searching File duplicates by name (case insensitive search), by size and by content
        kifKif.setDiffFileMode(
                FileDiffFactory.FILE_NAME | FileDiffFactory.FILE_SIZE | FileDiffFactory.FILE_CONTENT
                | FileDiffFactory.FOLDER_NAME | FileDiffFactory.FOLDER_CONTENT);
        kifKif.setCaseInsensitiveNames(false);

        // ********************************
        // SECOND WAY
        // ********************************

        // setting  filestamp filters
        // that will be used before any content comparator
        // to put comparable files in the same group
        kifKif.setFilestampFilterList(new FilestampFilterList(new FilestampFilter[]{
            new FileNameStampFilter(),
            new FileSizeStampFilter()
        }));

        // setting  filecontent filter
        // thatwill be used to compare files belonging to the same filestamp group
        kifKif.setFileContentComparator(new FileByteContentComparator());

        // setting  folserstamp filters
        // that will be used before any content comparator
        // to put comparable folders in the same group
        kifKif.setFolderstampFilterList(new FilestampFilterList(new FilestampFilter[]{
            new FileNameStampFilter(),
            new FolderSizeStampFilter()
        }));

        // setting  foldercontent filter
        kifKif.setFileContentComparator(new FileByteContentComparator());


        // ********************************
        // STARTING SEARCH...
        // ********************************

        // starting search
        SearchData searchData = kifKif.findFileDuplicates();

        List<DuplicateList> duplicateLists = searchData.getDuplicateLists();
        for (DuplicateList duplicateList : duplicateLists) {
            if (duplicateList.isFolderList()) {
                System.out.println("[Folder Duplicates]");
            } else {
                System.out.println("[File Duplicates]");
            }
            for (Iterator<File> i = duplicateList.getFiles().iterator(); i.hasNext();) {
                File file = i.next();
                System.out.println(file.getPath());
            }
        }

        // exporting search data in text format
        System.out.println();
        System.out.println("************************************* ");
        System.out.println("** Text Export Support Formatting  ** ");
        System.out.println("************************************* ");
        TextExportSupport textExportSupport = new TextExportSupport();
        try {
            textExportSupport.export(searchData, System.out, null);
        } catch (ExportException e) {
            System.out.println("Export Exception : ");
            e.printStackTrace();
        } catch (IOException e) {
            System.out.println("In/Out Exception : ");
            e.printStackTrace();
        }
    }
}
