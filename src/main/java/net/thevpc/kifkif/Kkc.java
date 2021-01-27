package net.thevpc.kifkif;

import java.io.File;
import java.util.HashMap;
import java.util.Locale;
import net.thevpc.common.mon.ProgressMonitor;
import net.thevpc.common.mon.ProgressMonitors;
import net.thevpc.common.prs.log.LoggerProvider;

import net.thevpc.common.swing.util.jcmd.CmdOption;
import net.thevpc.common.swing.util.jcmd.CmdParam;
import net.thevpc.common.swing.util.jcmd.JCmdLine;
import net.thevpc.kifkif.swing.export.ExportSupport;
import net.thevpc.kifkif.swing.export.TextExportSupport;
import net.thevpc.kifkif.swing.Configuration;
import net.thevpc.kifkif.swing.Kkw;
import net.thevpc.common.swing.util.LoggerTaskMonitor;
import net.thevpc.common.swing.util.TaskMonitor;
import net.thevpc.common.swing.SwingUtilities3;
import net.thevpc.common.prs.messageset.MessageSet;

/**
 * Kikif Console
 * User: taha
 * Date: 5 janv. 2005
 * Time: 21:02:48
 */
public final class Kkc {
    public static final int ERR_UNSUPPORTED_OUTPUT_TYPE = 100;
    public static final int ERR_UNSUPPORTED_MONITOR = 101;

    public Kkc() {

    }

    public static void main(String[] args) {
        Kkc kkc = new Kkc();
        JCmdLine JCmdLine = new JCmdLine(System.getProperty("user.home") + "/.kifkif/kkc.prp", args);
        JCmdLine.setReference(kkc);
        JCmdLine.setHelpContent("file:/net/vpc/kifkif/KkcHelp.txt");
        if (JCmdLine.size() == 0) {
            Kkw w = new Kkw();
            w.getMainFrame().setLocation(SwingUtilities3.getScreenCentredPosition(w.getMainFrame()));
            w.getMainFrame().setVisible(true);
        } else {
            if ((!JCmdLine.isAnySelectedOption("c", "console"))) {
                JCmdLine.showWinHelp(null, "Kkc");
                System.exit(0);
            } else if (JCmdLine.getParameters().length == 0 || JCmdLine.isHelpNeeded()) {
                JCmdLine.showHelp();
                System.exit(0);
            } else if (JCmdLine.isAnySelectedOption("v", "version")) {
                System.out.println(KifKifVersion.PRODUCT_NAME + " Console (Kkc) v" + KifKifVersion.PRODUCT_VERSION + " by " + KifKifVersion.AUTHOR_NAME + " (" + KifKifVersion.AUTHOR_MAIL + ")");
                System.exit(0);
            }
            int diffFileOption = 0;
            int diffFolderOption = 0;
            String file = JCmdLine.getAnyOptionValue("o", "output");
            String fileType = JCmdLine.getAnyOptionValue("ot", "output-type");
            String language = JCmdLine.getAnyOptionValue("lang", "language");
            boolean insensitve = JCmdLine.isAnySelectedOption("i", "ignorecase");
            //String monitor = CmdLine.getAnyOptionValue("m", "monitor");
            CmdOption monitor = JCmdLine.getAnyOption("m", "monitor");

            if (language != null) {
                Locale.setDefault(Configuration.getLocaleFromString(language));
            }
            if (JCmdLine.isAnySelectedOption("fc", "file-content")) {
                diffFileOption |= FileDiffFactory.FILE_CONTENT;
            }
            if (JCmdLine.isAnySelectedOption("dc", "dir-content")) {
                diffFolderOption |= FileDiffFactory.FOLDER_CONTENT;
            }
            if (JCmdLine.isAnySelectedOption("fn", "file-name")) {
                diffFileOption |= (FileDiffFactory.FILE_NAME);
            }
            if (JCmdLine.isAnySelectedOption("dn", "dir-name")) {
                diffFolderOption |= (FileDiffFactory.FOLDER_NAME);
            }
            if (JCmdLine.isAnySelectedOption("fs", "file-size")) {
                diffFileOption |= FileDiffFactory.FILE_SIZE;
            }
            if (JCmdLine.isAnySelectedOption("ds", "dir-size")) {
                diffFolderOption |= FileDiffFactory.FOLDER_SIZE;
            }
            if (JCmdLine.isAnySelectedOption("ft", "file-time")) {
                diffFileOption |= FileDiffFactory.FILE_TIME;
            }
            if (JCmdLine.isAnySelectedOption("dt", "dir-time")) {
                diffFolderOption |= FileDiffFactory.FOLDER_TIME;
            }
            if (JCmdLine.isAnySelectedOption("fh", "file-checksum")) {
                diffFileOption |= FileDiffFactory.FILE_STAMP;
            }
            if (JCmdLine.isAnySelectedOption("dh", "dir-checksum")) {
                diffFolderOption |= FileDiffFactory.FOLDER_STAMP;
            }

            if (fileType != null) {
                if ("txt".equals(fileType)) {

                } else {
                    System.err.println("[warning] Unsupported output-type '" + fileType + "'");
                    System.exit(ERR_UNSUPPORTED_OUTPUT_TYPE);
                }
            }

            if (
                    diffFileOption == 0
                    ||
                    (JCmdLine.isAnySelectedOption("1", "default-1"))
            ) {
                diffFileOption |= (
                        (FileDiffFactory.FILE_NAME)
                        | FileDiffFactory.FILE_SIZE
                        | FileDiffFactory.FILE_CONTENT
                        );
                diffFolderOption |= (
                          (FileDiffFactory.FOLDER_NAME)
                        | FileDiffFactory.FOLDER_SIZE
                        | FileDiffFactory.FOLDER_CONTENT
                        );
            }
            if (
                    (JCmdLine.isAnySelectedOption("2", "default-2"))
            ) {
                diffFileOption |= (
                        (FileDiffFactory.FILE_NAME)
                        | FileDiffFactory.FILE_SIZE
                        | FileDiffFactory.FILE_STAMP
                        );
                diffFileOption |= (
                          (FileDiffFactory.FOLDER_NAME)
                        | FileDiffFactory.FOLDER_SIZE
                        | FileDiffFactory.FOLDER_STAMP
                        );
            }
            if (JCmdLine.showUnknownOptionsErrors()) {
                System.exit(-1);
            }
            KifKif kifKif = new KifKif(diffFileOption,diffFolderOption);
            kifKif.setCaseInsensitiveNames(insensitve);
            CmdParam[] params = JCmdLine.getParameters();
            for (CmdParam param : params) {
                kifKif.addIncludedFileSet(new DefaultFileSet(new File(param.getValue())));
            }
            HashMap<String, Object> properties = new HashMap<String, Object>();
            properties.put(ExportSupport.FILE_PROPERTY, file);
            MessageSet resources = new MessageSet(LoggerProvider.DEFAULT);
            resources.addBundle("net.thevpc.kifkif.lang.Kifkif");
            ProgressMonitor taskMonitor = null;
            if (monitor == null) {
                taskMonitor = ProgressMonitors.none();
            } else if (monitor.isBoolean()) {
                taskMonitor = monitor.isSelected() ? 
                        ProgressMonitors.createLogMonitor(500) : ProgressMonitors.none();
            } else if (monitor.getValue().equals("always")) {
                taskMonitor = ProgressMonitors.createLogMonitor(0);
            } else if (monitor.getValue().equals("fast")) {
                taskMonitor = ProgressMonitors.createLogMonitor(300);
            } else if (monitor.getValue().equals("medium")) {
                taskMonitor = ProgressMonitors.createLogMonitor(1000);
            } else if (monitor.getValue().equals("slow")) {
                taskMonitor = ProgressMonitors.createLogMonitor(6000);
            } else if (monitor.getValue().equals("never")) {
                taskMonitor = ProgressMonitors.none();
            } else if (monitor.getValue().matches("\\d{1,6}")) {
                taskMonitor = ProgressMonitors.createLogMonitor(Integer.parseInt(monitor.getValue()));
            } else {
                JCmdLine.exitWithError(ERR_UNSUPPORTED_MONITOR, "Unknow monitor " + monitor.getValue());
            }
            SearchData fileDuplicates = kifKif.findDuplicates(taskMonitor);
            fileDuplicates.setSelectedDuplicatesAuto();
            TextExportSupport textExportSupport = new TextExportSupport();
            try {
                textExportSupport.export(fileDuplicates, file == null ? System.out : null, properties);
            } catch (Exception e) {
                e.printStackTrace();
            }
            System.exit(0);
        }
    }

}
