package net.thevpc.kifkif;

import java.io.File;
import java.util.*;
import java.util.stream.Collectors;

import net.thevpc.common.prs.log.LoggerProvider;

import net.thevpc.kifkif.swing.export.ExportSupport;
import net.thevpc.kifkif.swing.export.TextExportSupport;
import net.thevpc.kifkif.swing.Configuration;
import net.thevpc.kifkif.swing.Kkw;
import net.thevpc.common.prs.messageset.MessageSet;
import net.thevpc.nuts.*;
import net.thevpc.nuts.cmdline.NutsArgument;
import net.thevpc.nuts.cmdline.NutsCommandLine;
import net.thevpc.nuts.util.NutsProgressMonitor;
import net.thevpc.nuts.util.NutsProgressMonitors;

/**
 * Kikif Console
 * User: taha
 * Date: 5 janv. 2005
 * Time: 21:02:48
 */
public final class Kkc implements NutsApplication {
    public Kkc() {

    }

    public static void main(String[] args) {
        new Kkc().run(args);
    }

    @Override
    public void run(NutsApplicationContext applicationContext) {
        Kkc kkc = new Kkc();
        applicationContext.processCommandLine(new NutsAppCmdProcessor() {
            Options options = new Options();

            @Override
            public boolean onCmdNextOption(NutsArgument option, NutsCommandLine commandline, NutsApplicationContext context) {
                switch (option.key()) {
                    case "-c":
                    case "--console": {
                        NutsArgument a = commandline.nextBoolean().get();
                        if (a.isActive()) {
                            options.console = a.getBooleanValue().get();
                        }
                        return true;
                    }
                    case "-o":
                    case "--output": {
                        NutsArgument a = commandline.nextString().get();
                        if (a.isActive()) {
                            options.file=(a.getStringValue().get());
                        }
                        return true;
                    }
                    case "-i":
                    case "--ignore-case": {
                        NutsArgument a = commandline.nextBoolean().get();
                        if (a.isActive()) {
                            options.insensitive = a.getBooleanValue().get();
                        }
                        return true;
                    }
                    case "-m":
                    case "--monitor": {
                        NutsArgument a = commandline.nextString().get();
                        if (a.isActive()) {
                            options.monitor=a.getStringValue().get();
                        }
                        return true;
                    }
                    case "--fc":
                    case "--file-content": {
                        processFlag(commandline, FileMode.FILE_CONTENT);
                        return true;
                    }
                    case "--dc":
                    case "--dir-content": {
                        processFlag(commandline, FileMode.FOLDER_CONTENT);
                        return true;
                    }
                    case "--fh":
                    case "--file-checksum": {
                        processFlag(commandline, FileMode.FILE_STAMP);
                        return true;
                    }
                    case "--dh":
                    case "--dir-checksum": {
                        processFlag(commandline, FileMode.FOLDER_STAMP);
                        return true;
                    }
                    case "--ft":
                    case "--file-time": {
                        processFlag(commandline, FileMode.FILE_TIME);
                        return true;
                    }
                    case "--dt":
                    case "--dir-time": {
                        processFlag(commandline, FileMode.FOLDER_TIME);
                        return true;
                    }

                    case "--fs":
                    case "--file-size": {
                        processFlag(commandline, FileMode.FILE_SIZE);
                        return true;
                    }
                    case "--ds":
                    case "--dir-size": {
                        processFlag(commandline, FileMode.FOLDER_SIZE);
                        return true;
                    }

                    case "--fn":
                    case "--file-name": {
                        processFlag(commandline, FileMode.FILE_NAME);
                        return true;
                    }
                    case "--dn":
                    case "--dir-name": {
                        processFlag(commandline, FileMode.FOLDER_NAME);
                        return true;
                    }
                    case "-1":
                    case "--default-1": {
                        NutsArgument a = commandline.nextBoolean().get();
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
                        NutsArgument a = commandline.nextBoolean().get();
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
                        NutsArgument a = commandline.nextString().get();
                        if (a.isActive()) {
                            options.includedFileSets.add(a.getStringValue().get());
                        }
                        return true;
                    }
                    case "--exclude": {
                        NutsArgument a = commandline.nextString().get();
                        if (a.isActive()) {
                            options.excludedFileSets.add(a.getStringValue().get());
                        }
                        return true;
                    }
                }
                return false;
            }

            private void processFlag(NutsCommandLine commandline, FileMode flag) {
                NutsArgument a = commandline.nextBoolean().get();
                if (a.isActive()) {
                    if (a.getBooleanValue().get()) {
                        options.diffFileOption.add(flag);
                    } else {
                        options.diffFileOption.remove(flag);
                    }
                }
            }

            @Override
            public boolean onCmdNextNonOption(NutsArgument nonOption, NutsCommandLine commandline, NutsApplicationContext context) {
                options.includedFileSets.add(commandline.nextStringValueLiteral().get());
                return true;
            }

            @Override
            public void onCmdExec(NutsCommandLine nutsCommandLine, NutsApplicationContext nutsApplicationContext) {
                if (options.console == null || !options.console) {
                    Kkw w = new Kkw(applicationContext);
                    w.showFrame();
                } else {
                    KifKif kifKif = new KifKif(options.diffFileOption.toArray(new FileMode[0]), applicationContext.getSession());
                    kifKif.setCaseInsensitiveNames(options.insensitive);
                    for (String param : options.includedFileSets) {
                        kifKif.addIncludedFileSet(new DefaultFileSet(new File(param)));
                    }
                    kifKif.addExcludedFiles(options.excludedFileSets.stream().map(File::new).toArray(File[]::new));
                    HashMap<String, Object> properties = new HashMap<String, Object>();
                    properties.put(ExportSupport.FILE_PROPERTY, options.file);
                    MessageSet resources = new MessageSet(LoggerProvider.DEFAULT);
                    resources.addBundle("net.thevpc.kifkif.lang.Kifkif");
                    NutsProgressMonitor taskMonitor = createMon(options.monitor, applicationContext.getSession());
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

    private NutsProgressMonitor createMon(String value, NutsSession session) {
        NutsProgressMonitors m = NutsProgressMonitors.of(session);

        if (value == null || value.isEmpty()) {
            return m.ofSilent();
        } else if (NutsValue.of(value).isBoolean()) {
            return NutsValue.of(value).asBoolean().get() ?
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
            throw new NutsIllegalArgumentException(session, NutsMessage.ofCstyle("Unknown monitor %s", value));
        }
    }

}
