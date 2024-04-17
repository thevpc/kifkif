package net.thevpc.kifkif.swing.export;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Map;

import net.thevpc.kifkif.SearchData;
import net.thevpc.nuts.NSession;

/**
 * @author vpc
 * Date: 12 janv. 2005
 * Time: 19:41:17
 */
public interface ExportSupport {
    public static final String KKW_PROPERTY = "Kkw";
    public static final String FILE_PROPERTY = "File";

    public String getName();
    public boolean export(SearchData searchData, OutputStream stream, Map<String, Object> properties, NSession session) throws ExportException, IOException;
}
