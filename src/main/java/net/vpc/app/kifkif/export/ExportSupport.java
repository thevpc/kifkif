package net.vpc.app.kifkif.export;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Map;

import net.vpc.app.kifkif.SearchData;

/**
 * @author vpc
 * Date: 12 janv. 2005
 * Time: 19:41:17
 */
public interface ExportSupport {
    public static final String KKW_PROPERTY = "Kkw";
    public static final String FILE_PROPERTY = "File";

    public String getName();
    public boolean export(SearchData searchData, OutputStream stream, Map<String, Object> properties) throws ExportException, IOException;
}
