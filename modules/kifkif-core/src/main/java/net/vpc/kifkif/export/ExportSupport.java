package net.vpc.kifkif.export;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Map;

import net.vpc.kifkif.SearchData;

/**
 * @author vpc
 * Date: 12 janv. 2005
 * Time: 19:41:17
 */
public interface ExportSupport {
    String KKW_PROPERTY = "Kkw";
    String FILE_PROPERTY = "File";

    String getName();

    boolean export(SearchData searchData, OutputStream stream, Map<String, Object> properties) throws ExportException, IOException;
}
