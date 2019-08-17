
import net.vpc.kifkif.*;
import net.vpc.kifkif.export.TextExportSupport;
import net.vpc.kifkif.export.ExportException;

import java.io.IOException;
import java.io.File;
import java.util.List;
import java.util.Iterator;

/**
 * Using FileDiffFactory modes
 * Exporting to the System.out in ext mode
 * @author vpc
 */
public class Example01 {
    public static void main(String[] args) {
        KifKif kifKif=new KifKif();

        // searching only File duplicates by name (case sensitive search)
        int diffMode=
                FileDiffFactory.FILE_CONTENT
                |
                FileDiffFactory.FILE_NAME;

        kifKif.setDiffFileMode(diffMode);
        kifKif.setDiffFolderMode(diffMode);

        // starting search
        kifKif.addIncludedFileSet(new DefaultFileSet(new File(".")));
        SearchData searchData = kifKif.findFileDuplicates();


        System.out.println("************************************* ");
        System.out.println("** Personnalized Result Formatting ** ");
        System.out.println("************************************* ");
        List<DuplicateList> duplicateLists = searchData.getDuplicateLists();
        for (DuplicateList duplicateList : duplicateLists) {
            if(duplicateList.isFolderList()){
                System.out.println("[Folder Duplicates]");
            }else{
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
            textExportSupport.export(searchData,System.out, null);
        } catch (ExportException e) {
            System.out.println("Export Exception : ");
            e.printStackTrace();
        } catch (IOException e) {
            System.out.println("In/Out Exception : ");
            e.printStackTrace();
        }
    }
}
