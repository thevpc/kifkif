package net.thevpc.kifkif;

import java.io.File;
import java.util.*;

import net.thevpc.common.prs.log.LoggerProvider;

import net.thevpc.kifkif.swing.export.ExportSupport;
import net.thevpc.kifkif.swing.export.TextExportSupport;
import net.thevpc.kifkif.swing.Kkw;
import net.thevpc.common.prs.messageset.MessageSet;
import net.thevpc.nuts.app.NAppComplete;
import net.thevpc.nuts.app.NApplication;
import net.thevpc.nuts.app.NApp;
import net.thevpc.nuts.app.NAppRun;
import net.thevpc.nuts.cmdline.NArg;
import net.thevpc.nuts.cmdline.NCmdLine;
import net.thevpc.nuts.mon.NProgressMonitor;
import net.thevpc.nuts.util.NIllegalArgumentException;
import net.thevpc.nuts.util.NLiteral;
import net.thevpc.nuts.text.NMsg;

/**
 * Kikif Console
 * User: taha
 * Date: 5 janv. 2005
 * Time: 21:02:48
 */
@NApp
public final class Kkc  {
    Options options = new Options();
    public Kkc() {

    }

    public static void main(String[] args) {
        NApplication.builder(args).run();
    }

    private NCmdLine parseCmdLine(){
        NCmdLine cmdLine = NApplication.of().cmdLine();
        cmdLine.matcher()
                .when("-c","--console").asFlag(a->options.console = a.booleanValue())
                .when("-i","--ignore-case").asFlag(a->options.insensitive = a.booleanValue())
                .when("-o","--output").asEntry(a->options.file=(a.stringValue()))
                .when("--fc","--file-content").asEntry(a->processFlag(a, FileMode.FILE_CONTENT))
                .when("--dc","--dir-content").asEntry(a->processFlag(a, FileMode.FOLDER_CONTENT))
                .when("--fh","--file-checksum").asEntry(a->processFlag(a, FileMode.FILE_STAMP))
                .when("--dh","--dir-checksum").asEntry(a->processFlag(a, FileMode.FOLDER_STAMP))
                .when("--ft","--file-time").asEntry(a->processFlag(a, FileMode.FILE_TIME))
                .when("--dt","--dir-time").asEntry(a->processFlag(a, FileMode.FOLDER_TIME))
                .when("--fs","--file-size").asEntry(a->processFlag(a, FileMode.FILE_SIZE))
                .when("--ds","--dir-size").asEntry(a->processFlag(a, FileMode.FOLDER_SIZE))
                .when("--fn","--file-name").asEntry(a->processFlag(a, FileMode.FILE_NAME))
                .when("--dn","--dir-name").asEntry(a->processFlag(a, FileMode.FOLDER_NAME))
                .when("--include").asEntry(a-> options.includedFileSets.add(a.stringValue()))
                .when("--exclude").asEntry(a-> options.excludedFileSets.add(a.stringValue()))
                .when("-1","--default-1").asTrueFlag(a->{
                    options.diffFileOption.add(FileMode.FILE_NAME);
                    options.diffFileOption.add(FileMode.FILE_SIZE);
                    options.diffFileOption.add(FileMode.FILE_CONTENT);
                    options.diffFileOption.add(FileMode.FOLDER_NAME);
                    options.diffFileOption.add(FileMode.FOLDER_SIZE);
                    options.diffFileOption.add(FileMode.FOLDER_CONTENT);
                })
                .when("-2","--default-2").asTrueFlag(a->{
                    options.diffFileOption.add(FileMode.FILE_NAME);
                    options.diffFileOption.add(FileMode.FILE_SIZE);
                    options.diffFileOption.add(FileMode.FILE_STAMP);
                    options.diffFileOption.add(FileMode.FOLDER_NAME);
                    options.diffFileOption.add(FileMode.FOLDER_SIZE);
                    options.diffFileOption.add(FileMode.FOLDER_STAMP);
                })
                .whenNonOption().asArg(a->options.includedFileSets.add(a.image()))
                .withDefaults()
                .requireAll();
        return cmdLine;
    }

    @NAppComplete
    public void complete() {
        parseCmdLine().printCompleteResult();
    }
    @NAppRun
    public void run() {
        NCmdLine cmdLine = parseCmdLine();
        if (options.console == null || !options.console) {
            Kkw w = new Kkw();
            w.showFrame();
        } else {
            KifKif kifKif = new KifKif(options.diffFileOption.toArray(new FileMode[0]));
            kifKif.setCaseInsensitiveNames(options.insensitive);
            for (String param : options.includedFileSets) {
                kifKif.addIncludedFileSet(new DefaultFileSet(new File(param)));
            }
            kifKif.addExcludedFiles(options.excludedFileSets.stream().map(File::new).toArray(File[]::new));
            HashMap<String, Object> properties = new HashMap<String, Object>();
            properties.put(ExportSupport.FILE_PROPERTY, options.file);
            MessageSet resources = new MessageSet(LoggerProvider.DEFAULT);
            resources.addBundle("net.thevpc.kifkif.lang.Kifkif");
            NProgressMonitor taskMonitor = createMon(options.monitor);
            SearchData fileDuplicates = kifKif.findDuplicates(taskMonitor);
            fileDuplicates.setSelectedDuplicatesAuto();
            TextExportSupport textExportSupport = new TextExportSupport();
            try {
                textExportSupport.export(fileDuplicates, options.file == null ? System.out : null, properties);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void processFlag(NArg a, FileMode flag) {
        if (a.booleanValue()) {
            options.diffFileOption.add(flag);
        } else {
            options.diffFileOption.remove(flag);
        }
    }

    private static class Options {
        Boolean console;
        Set<FileMode> diffFileOption = new HashSet<>();
        String file;
        boolean insensitive;
        String monitor;
        List<String> includedFileSets = new ArrayList<>();
        List<String> excludedFileSets = new ArrayList<>();
    }

    private NProgressMonitor createMon(String value) {

        if (value == null || value.isEmpty()) {
            return NProgressMonitor.ofSilent();
        } else if (NLiteral.of(value).isBoolean()) {
            return NLiteral.of(value).asBoolean().get() ?
                    NProgressMonitor.ofLogger(500) : NProgressMonitor.ofSilent();
        } else if (value.equals("always")) {
            return NProgressMonitor.ofLogger();
        } else if (value.equals("fast")) {
            return NProgressMonitor.ofLogger(300);
        } else if (value.equals("medium")) {
            return NProgressMonitor.ofLogger(1000);
        } else if (value.equals("slow")) {
            return NProgressMonitor.ofLogger(6000);
        } else if (value.equals("never")) {
            return NProgressMonitor.ofSilent();
        } else if (value.matches("\\d{1,6}")) {
            return NProgressMonitor.ofLogger(Integer.parseInt(value));
        } else {
            throw new NIllegalArgumentException( NMsg.ofC("Unknown monitor %s", value));
        }
    }

}
