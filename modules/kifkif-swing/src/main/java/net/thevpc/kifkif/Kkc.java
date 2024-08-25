package net.thevpc.kifkif;

import java.io.File;
import java.util.*;

import net.thevpc.common.prs.log.LoggerProvider;

import net.thevpc.kifkif.swing.export.ExportSupport;
import net.thevpc.kifkif.swing.export.TextExportSupport;
import net.thevpc.kifkif.swing.Kkw;
import net.thevpc.common.prs.messageset.MessageSet;
import net.thevpc.nuts.*;
import net.thevpc.nuts.cmdline.NArg;
import net.thevpc.nuts.cmdline.NCmdLine;
import net.thevpc.nuts.cmdline.NCmdLineContext;
import net.thevpc.nuts.cmdline.NCmdLineRunner;
import net.thevpc.nuts.time.NProgressMonitor;
import net.thevpc.nuts.time.NProgressMonitors;
import net.thevpc.nuts.util.NLiteral;
import net.thevpc.nuts.util.NMsg;

/**
 * Kikif Console
 * User: taha
 * Date: 5 janv. 2005
 * Time: 21:02:48
 */
public final class Kkc implements NApplication {
    public Kkc() {

    }

    public static void main(String[] args) {
        new Kkc().run(args);
    }

    @Override
    public void run(NSession session) {
        Kkc kkc = new Kkc();
        session.runAppCmdLine(new NCmdLineRunner() {
            Options options = new Options();

            @Override
            public boolean nextOption(NArg option, NCmdLine cmdLine, NCmdLineContext context) {
                switch (option.key()) {
                    case "-c":
                    case "--console": {
                        NArg a = cmdLine.nextFlag().get();
                        if (a.isActive()) {
                            options.console = a.getBooleanValue().get();
                        }
                        return true;
                    }
                    case "-o":
                    case "--output": {
                        NArg a = cmdLine.nextEntry().get();
                        if (a.isActive()) {
                            options.file=(a.getStringValue().get());
                        }
                        return true;
                    }
                    case "-i":
                    case "--ignore-case": {
                        NArg a = cmdLine.nextFlag().get();
                        if (a.isActive()) {
                            options.insensitive = a.getBooleanValue().get();
                        }
                        return true;
                    }
                    case "-m":
                    case "--monitor": {
                        NArg a = cmdLine.nextEntry().get();
                        if (a.isActive()) {
                            options.monitor=a.getStringValue().get();
                        }
                        return true;
                    }
                    case "--fc":
                    case "--file-content": {
                        processFlag(cmdLine, FileMode.FILE_CONTENT);
                        return true;
                    }
                    case "--dc":
                    case "--dir-content": {
                        processFlag(cmdLine, FileMode.FOLDER_CONTENT);
                        return true;
                    }
                    case "--fh":
                    case "--file-checksum": {
                        processFlag(cmdLine, FileMode.FILE_STAMP);
                        return true;
                    }
                    case "--dh":
                    case "--dir-checksum": {
                        processFlag(cmdLine, FileMode.FOLDER_STAMP);
                        return true;
                    }
                    case "--ft":
                    case "--file-time": {
                        processFlag(cmdLine, FileMode.FILE_TIME);
                        return true;
                    }
                    case "--dt":
                    case "--dir-time": {
                        processFlag(cmdLine, FileMode.FOLDER_TIME);
                        return true;
                    }

                    case "--fs":
                    case "--file-size": {
                        processFlag(cmdLine, FileMode.FILE_SIZE);
                        return true;
                    }
                    case "--ds":
                    case "--dir-size": {
                        processFlag(cmdLine, FileMode.FOLDER_SIZE);
                        return true;
                    }

                    case "--fn":
                    case "--file-name": {
                        processFlag(cmdLine, FileMode.FILE_NAME);
                        return true;
                    }
                    case "--dn":
                    case "--dir-name": {
                        processFlag(cmdLine, FileMode.FOLDER_NAME);
                        return true;
                    }
                    case "-1":
                    case "--default-1": {
                        NArg a = cmdLine.nextFlag().get();
                        if (a.isActive()) {
                            if (a.getBooleanValue().get()) {
                                options.diffFileOption.add(FileMode.FILE_NAME);
                                options.diffFileOption.add(FileMode.FILE_SIZE);
                                options.diffFileOption.add(FileMode.FILE_CONTENT);
                                options.diffFileOption.add(FileMode.FOLDER_NAME);
                                options.diffFileOption.add(FileMode.FOLDER_SIZE);
                                options.diffFileOption.add(FileMode.FOLDER_CONTENT);
                            }
                        }

                        return true;
                    }
                    case "-2":
                    case "--default-2": {
                        NArg a = cmdLine.nextFlag().get();
                        if (a.isActive()) {
                            if (a.getBooleanValue().get()) {
                                options.diffFileOption.add(FileMode.FILE_NAME);
                                options.diffFileOption.add(FileMode.FILE_SIZE);
                                options.diffFileOption.add(FileMode.FILE_STAMP);
                                options.diffFileOption.add(FileMode.FOLDER_NAME);
                                options.diffFileOption.add(FileMode.FOLDER_SIZE);
                                options.diffFileOption.add(FileMode.FOLDER_STAMP);
                            }
                        }
                        return true;
                    }
                    case "--include": {
                        NArg a = cmdLine.nextEntry().get();
                        if (a.isActive()) {
                            options.includedFileSets.add(a.getStringValue().get());
                        }
                        return true;
                    }
                    case "--exclude": {
                        NArg a = cmdLine.nextEntry().get();
                        if (a.isActive()) {
                            options.excludedFileSets.add(a.getStringValue().get());
                        }
                        return true;
                    }
                }
                return false;
            }

            private void processFlag(NCmdLine commandline, FileMode flag) {
                NArg a = commandline.nextFlag().get();
                if (a.isActive()) {
                    if (a.getBooleanValue().get()) {
                        options.diffFileOption.add(flag);
                    } else {
                        options.diffFileOption.remove(flag);
                    }
                }
            }

            @Override
            public boolean nextNonOption(NArg nonOption, NCmdLine cmdLine, NCmdLineContext context) {
                options.includedFileSets.add(cmdLine.nextEntry().get().getStringValue().get());
                return true;
            }

            @Override
            public void run(NCmdLine cmdLine, NCmdLineContext context) {
                if (options.console == null || !options.console) {
                    Kkw w = new Kkw(session);
                    w.showFrame();
                } else {
                    KifKif kifKif = new KifKif(options.diffFileOption.toArray(new FileMode[0]), session);
                    kifKif.setCaseInsensitiveNames(options.insensitive);
                    for (String param : options.includedFileSets) {
                        kifKif.addIncludedFileSet(new DefaultFileSet(new File(param)));
                    }
                    kifKif.addExcludedFiles(options.excludedFileSets.stream().map(File::new).toArray(File[]::new));
                    HashMap<String, Object> properties = new HashMap<String, Object>();
                    properties.put(ExportSupport.FILE_PROPERTY, options.file);
                    MessageSet resources = new MessageSet(LoggerProvider.DEFAULT);
                    resources.addBundle("net.thevpc.kifkif.lang.Kifkif");
                    NProgressMonitor taskMonitor = createMon(options.monitor, session);
                    SearchData fileDuplicates = kifKif.findDuplicates(taskMonitor);
                    fileDuplicates.setSelectedDuplicatesAuto();
                    TextExportSupport textExportSupport = new TextExportSupport();
                    try {
                        textExportSupport.export(fileDuplicates, options.file == null ? System.out : null, properties, session);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        });
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

    private NProgressMonitor createMon(String value, NSession session) {
        NProgressMonitors m = NProgressMonitors.of(session);

        if (value == null || value.isEmpty()) {
            return m.ofSilent();
        } else if (NLiteral.of(value).isBoolean()) {
            return NLiteral.of(value).asBoolean().get() ?
                    m.ofLogger(500) : m.ofSilent();
        } else if (value.equals("always")) {
            return m.ofLogger();
        } else if (value.equals("fast")) {
            return m.ofLogger(300);
        } else if (value.equals("medium")) {
            return m.ofLogger(1000);
        } else if (value.equals("slow")) {
            return m.ofLogger(6000);
        } else if (value.equals("never")) {
            return m.ofSilent();
        } else if (value.matches("\\d{1,6}")) {
            return m.ofLogger(Integer.parseInt(value));
        } else {
            throw new NIllegalArgumentException(session, NMsg.ofC("Unknown monitor %s", value));
        }
    }

}
