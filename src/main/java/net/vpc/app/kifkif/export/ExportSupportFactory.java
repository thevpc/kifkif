package net.vpc.app.kifkif.export;

import java.util.Vector;

/**
 * @author vpc
 * Date: 16 janv. 2005
 * Time: 16:36:26
 */
public class ExportSupportFactory {

    private static Vector<ExportSupport> exportSupports = new Vector<ExportSupport>();

    static {
        registerExportSupport(new TextExportSupport());
        registerExportSupport(new XmlExportSupport());
    }

    public static ExportSupport[] getAvailableExportSupport() {
        return exportSupports.toArray(new ExportSupport[exportSupports.size()]);
    }

    public static void registerExportSupport(ExportSupport support) {
        exportSupports.add(support);
    }

    public static void unregisterExportSupport(ExportSupport support) {
        exportSupports.remove(support);
    }
}
