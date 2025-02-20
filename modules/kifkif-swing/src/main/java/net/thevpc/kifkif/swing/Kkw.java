package net.thevpc.kifkif.swing;

import net.thevpc.common.swing.SwingUtilities3;
import net.thevpc.common.swing.layout.GridBagLayoutSupport;
import net.thevpc.common.swing.plaf.JPlafMenu;
import net.thevpc.kifkif.swing.export.ExportSupport;
import net.thevpc.kifkif.swing.export.ExportSupportFactory;
import net.thevpc.common.prs.ResourceSetHolder;
import net.thevpc.common.prs.iconset.IconSet;
import net.thevpc.common.prs.iconset.IconSetDescriptor;
import net.thevpc.common.prs.iconset.IconSetManager;
import net.thevpc.common.swing.iconset.JIconSetMenu;
import net.thevpc.common.swing.messageset.JLocaleMenu;
import net.thevpc.common.prs.messageset.MessageSet;
import net.thevpc.common.prs.messageset.MessageSetManager;
import net.thevpc.common.prs.xml.XmlUtils;

import javax.swing.*;
import javax.swing.filechooser.FileFilter;
import java.awt.*;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Paths;
import java.text.DateFormat;
import java.text.ParseException;
import java.util.*;

import net.thevpc.kifkif.DefaultFileFilter;
import net.thevpc.kifkif.DefaultFileSet;
import net.thevpc.kifkif.FileMode;
import net.thevpc.kifkif.FileSet;
import net.thevpc.kifkif.KifKif;
import net.thevpc.kifkif.KifKifVersion;
import net.thevpc.kifkif.SearchData;
import net.thevpc.kifkif.SearchStatistics;
import net.thevpc.common.prs.log.LoggerProvider;
import net.thevpc.common.prs.locale.LocaleManager;
import net.thevpc.common.swing.prs.ComponentResourcesUpdater;
import net.thevpc.common.swing.prs.PRSManager;
import net.thevpc.nuts.NApp;
import net.thevpc.nuts.NSession;
import net.thevpc.nuts.time.NProgressMonitors;
import net.thevpc.nuts.util.NEnumSet;
import net.thevpc.swing.plaf.UIPlafManager;

/**
 * User: taha
 * Date: 12 janv. 2005
 * Time: 12:46:52
 */
public class Kkw implements ResourceSetHolder {
    public static String DEFAULT_ICONSET_NAME = "kifkif-iconset-default";
    public static final Locale BOOT_LOCALE = Locale.getDefault();
    public static final String FILE_EXTENSION_FILTER = ".kks";
    public static final String FILE_EXTENSION_RESULT = ".kkr";

    public FileFilter filterFilter = new FileFilter() {
        public boolean accept(File f) {
            return f.isDirectory() || f.getName().toLowerCase().endsWith(FILE_EXTENSION_FILTER);
        }

        public String getDescription() {
            return getResources().get("FILE_EXTENSION_FILTER");
        }
    };
    public FileFilter resultFilter = new FileFilter() {
        public boolean accept(File f) {
            return f.isDirectory() || f.getName().toLowerCase().endsWith(FILE_EXTENSION_RESULT);
        }

        public String getDescription() {
            return getResources().get("FILE_EXTENSION_RESULT");
        }
    };
    public static final String PROPERTY_PROCESSING = "PROPERTY_PROCESSING";
    public DateFormat dateFormat = DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT);
    private KifKif kifKif;
    private JTable selectedFolders = null;
    //    private JTable excludedFolders = null;
    private MessageSet resources = null;

    private JComboBox filterFileNameCombo;
    private JTextField filterFileNameText;

    private JComboBox filterFolderNameCombo;
    private JTextField filterFolderNameText;

    private JButton startButton;

    private JButton stopButton;

    private StatusbarTaskMonitor statusbar;
    private JButton addIncludeFolderButton;
    private JButton removeIncludeFolderButton;
    private JButton addExcludedFolderButton;

    private JTextField statSourceFolderCountText;
    private JTextField statSourceFileCountText;
    private JTextField statSelectionFolderCountText;
    private JTextField statSelectionFileCountText;
    private JTextField statSelectionSizeCountText;
    private JTextField statSelectionTimeStartText;
    private JTextField statSelectionTimeElapsedText;

//    private JLabel nativeCommandLabel;
//    private JTextField nativeCommandText;

    private JLabel statsSrcFoldersCountLabel;
    private JLabel statsSrcFilesCountLabel;
    private JLabel statsSelectionFoldersCountLabel;
    private JLabel statsSelectionFilesCountLabel;
    private JLabel statsSelectionSizeLabel;
    private JLabel statsTimeStartLabel;
    private JLabel statsTimeElapsedLabel;
    private JLabel statsSourceLabel;
    private JLabel statsSelectionLabel;
    private JLabel statsTimeLabel;

    /**
     * file name
     */
    private JCheckBox optionFileNameCheck;
    private JCheckBox optionFileExtensionCheck;
    /**
     * file size
     */
    private JCheckBox optionFileSizeCheck;
    /**
     * file checksum
     */
    private JCheckBox optionFileChecksumCheck;
    private JComboBox optionFileStampCombo;

    private JCheckBox optionFileContentInsensitiveCheck;
    private JCheckBox optionFileContentIgnoreWhitesCheck;
    private JComboBox optionFileTimeCombo;
    private JComboBox optionFolderTimeCombo;

    /**
     * file content
     */
    private JCheckBox optionFileContentCheck;
    /**
     * file time
     */
    private JCheckBox optionFileTimeCheck;

    private JCheckBox optionFolderNameCheck;
    private JCheckBox optionFolderSizeCheck;
    private JCheckBox optionFolderChecksumCheck;
    private JCheckBox optionFolderContentCheck;
    private JCheckBox optionFolderTimeCheck;

    private JCheckBox filterFileNameCheck;
    private JCheckBox filterFolderNameCheck;
    private JCheckBox filterFileSizeCheck;
    private JCheckBox filterFileHiddenCheck;
    private JCheckBox filterFileSizeGreaterCheck;
    private JComboBox filterFileSizeGreaterCombo;
    private JCheckBox filterFileSizeLessCheck;
    private JComboBox filterFileSizeLessCombo;
    private JCheckBox filterFileTimeCheck;
    private JRadioButton filterFileTimeBetweenCheck;
    private JLabel filterFileTimeAndNameLabel;
    private JRadioButton filterFileTimeDuringCheck;

    private JTextField filterFileSizeGreaterText;
    private JTextField filterFileSizeLessText;
    private JTextField filterFileTimeBetweenText;
    private JTextField filterFileTimeAndText;
    private JTextField filterFileTimeDuringText;
    private JComboBox filterFileTimeDuringCombo;


    private DuplicateListTree resultTree;
    private Thread currentProcessingThread = null;
    private JPanel mainPanel;
    private JFrame mainFrame;
    private JMenuBar menuBar;
    private JMenu fileMenu;
    private JMenuItem optionsMenu;
    private JMenuItem optionsMenuItem;
    private JMenuItem newSearchMenuItem;
    private JMenu exportMenu;
    private JPlafMenu plafMenu;
    private JLocaleMenu langMenu;
    private JIconSetMenu iconMenu;
    private JMenu helpMenu;
    private JMenuItem aboutMenuItem;

    private JMenuItem saveFilterMenuItem;
    private JMenuItem loadFilterMenuItem;

    private JMenuItem saveResultMenuItem;
    private JMenuItem loadResultMenuItem;

    private JMenuItem quitMenuItem;
    private JTabbedPane optionTabbedPane;
    private JTabbedPane resultTabbedPane;

    private Locale DEFAULT_LOCALE = Locale.getDefault();
    private Locale currentLocale = Locale.getDefault();

    private ExpandCollapsePalette expandCollapsePalette;
    private SortPalette sortPalette;
    private MarkForDeletionPalette markForDeletionPalette;
    private DeletePalette deletePalette;
    private LaunchPalette launchPalette;
    private Configuration configuration;
    private boolean processing;
    private Collection<Action> actions = new ArrayList<Action>();
    private transient PropertyChangeSupport support;

    public Kkw() {
        NSession session = NSession.of();
        SwingUtilities.invokeLater(()->session.getWorkspace().setSharedInstance());

        String locale1 = session.getLocale().orNull();
        if (locale1 != null) {
            Locale locale = new Locale(locale1);
            Locale.setDefault(locale);
            LocaleManager.getInstance().setLocale(locale);
        }
        this.kifKif = new KifKif();
        UIPlafManager.getCurrentManager().apply("FlatDark");
        LocaleManager.getInstance().registerLocale(BOOT_LOCALE);
        LocaleManager.getInstance().registerLocale(Locale.ENGLISH);
        LocaleManager.getInstance().registerLocale(Locale.FRENCH);
        LocaleManager.getInstance().registerLocale(Locale.ITALIAN);
        LocaleManager.getInstance().registerLocale(new Locale("ar"));
        try {
            configuration = new Configuration(NApp.of().getConfFolder().resolve("kkw.xml"), true);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (configuration != null) {
            Locale locale = Configuration.getLocaleFromString(configuration.getString("java.util.Locale"));
            if (locale != null) {
                Locale.setDefault(locale);
            }
            String plafClassName = configuration.getString("PlafClassName");
            if (plafClassName != null) {
                try {
                    UIManager.setLookAndFeel(plafClassName);
                } catch (Exception e) {
                    //
                }
            }
        }
        PRSManager.registerIconSet(new IconSetDescriptor("net.thevpc.kifkif.iconset.kifkif-iconset-default", null, null), LoggerProvider.DEFAULT);
//        IconSetManager.loadAvailableIconSets("net.thevpc.kifkif.iconset", "kifkif-iconset-", new File("lib/iconsets"), DEFAULT_ICONSET_NAME);
        try {
            IconSetManager.loadAvailableIconSets(new File("lib/iconsets").toURI().toURL(), getClass().getClassLoader(), null, null, LoggerProvider.DEFAULT);
        } catch (IOException e) {
            e.printStackTrace();
        }
        IconSetManager.setIconSet(DEFAULT_ICONSET_NAME);
        //
        try {
            MessageSetManager.loadAvailableMessageSets(new File("lib/messagesets").toURI().toURL(), getClass().getClassLoader(), null, null);
        } catch (IOException e) {
            e.printStackTrace();
        }
        kifKif.setGlobalFileFilter(new DefaultFileFilter());
        resources = new MessageSet(LoggerProvider.DEFAULT);
        resources.addBundle("net.thevpc.kifkif.messageset.Kifkif");
        resultTree = new DuplicateListTree(this);
        startButton = PRSManager.createButton("startButton");
        startButton.putClientProperty("showText", Boolean.TRUE);
        stopButton = PRSManager.createButton("stopButton");
        stopButton.putClientProperty("showText", Boolean.TRUE);
        statusbar = new StatusbarTaskMonitor();

        statsSrcFoldersCountLabel = PRSManager.createLabel("statsSrcFoldersCountLabel");
        statsSrcFilesCountLabel = PRSManager.createLabel("statsSrcFilesCountLabel");
        statsSelectionFoldersCountLabel = PRSManager.createLabel("statsSelectionFoldersCountLabel");
        statsSelectionFilesCountLabel = PRSManager.createLabel("statsSelectionFilesCountLabel");
        statsSelectionSizeLabel = PRSManager.createLabel("statsSelectionSizeLabel");
        statsTimeStartLabel = PRSManager.createLabel("statsTimeStartLabel");
        statsTimeElapsedLabel = PRSManager.createLabel("statsTimeElapsedLabel");
        statsSourceLabel = PRSManager.createLabel("statsSourceLabel");
        statsSelectionLabel = PRSManager.createLabel("statsSelectionLabel");
        statsTimeLabel = PRSManager.createLabel("statsTimeLabel");

        addIncludeFolderButton = PRSManager.createButton("addIncludeFolderButton");
        removeIncludeFolderButton = PRSManager.createButton("removeIncludeFolderButton");
        addExcludedFolderButton = PRSManager.createButton("addExcludedFolderButton");
        filterFileNameCombo = PRSManager.createCombo("filterFileNameCombo", "filterFileFolderNameComboValues", new Object[]{Boolean.TRUE, Boolean.FALSE}, 0);
        filterFileNameText = new JTextField("*.jpg;*.gif;*.png");
        filterFolderNameCombo = PRSManager.createCombo("filterFolderNameCombo", "filterFileFolderNameComboValues", new Object[]{Boolean.TRUE, Boolean.FALSE}, 1);

        statSourceFolderCountText = new JTextField();
        statSourceFileCountText = new JTextField();
        statSelectionFolderCountText = new JTextField();
        statSelectionFileCountText = new JTextField();
        statSelectionSizeCountText = new JTextField();
        statSelectionTimeStartText = new JTextField();
        statSelectionTimeElapsedText = new JTextField();

        filterFolderNameText = new JTextField("CVS;REPOSITORY");
        optionFileNameCheck = PRSManager.createCheck("optionFileNameCheck", false);
        optionFileExtensionCheck = PRSManager.createCheck("optionFileExtensionCheck", false);
        optionFileSizeCheck = PRSManager.createCheck("optionFileSizeCheck", true);
        optionFileChecksumCheck = PRSManager.createCheck("optionFileChecksumCheck", true);
        optionFileContentCheck = PRSManager.createCheck("optionFileContentCheck", true);
        optionFileTimeCheck = PRSManager.createCheck("optionFileTimeCheck", false);

        optionFolderNameCheck = PRSManager.createCheck("optionFolderNameCheck", false);
        optionFolderSizeCheck = PRSManager.createCheck("optionFolderSizeCheck", true);
        optionFolderChecksumCheck = PRSManager.createCheck("optionFolderChecksumCheck", false);
        optionFolderContentCheck = PRSManager.createCheck("optionFolderContentCheck", true);
        optionFolderTimeCheck = PRSManager.createCheck("optionFolderTimeCheck", false);

        optionFileContentInsensitiveCheck = PRSManager.createCheck("optionFileContentInsensitiveCheck", false);
        optionFileContentIgnoreWhitesCheck = PRSManager.createCheck("optionFileContentIgnoreWhitesCheck", false);
        optionFileTimeCombo = PRSManager.createCombo("optionFileTimeCombo", "optionFileFolderTimeComboValues", new Object[]{"E", "H", "D", "W", "M", "Y"}, 0);
        optionFolderTimeCombo = PRSManager.createCombo("optionFolderTimeCombo", "optionFileFolderTimeComboValues", new Object[]{"E", "H", "D", "W", "M", "Y"}, 0);

        optionFileStampCombo = PRSManager.createCombo("optionFileStampCombo", "optionFileFolderStampComboValues", new Object[]{"DEFAULT", "SHA-1", "MD5"}, 0);
        filterFileNameCheck = PRSManager.createCheck("filterFileNameCheck", false);
        filterFolderNameCheck = PRSManager.createCheck("filterFolderNameCheck", false);
        filterFileSizeCheck = PRSManager.createCheck("filterFileSizeCheck", false);
        filterFileHiddenCheck = PRSManager.createCheck("filterFileHiddenCheck", false);
        filterFileSizeGreaterCheck = PRSManager.createCheck("filterFileSizeGreaterCheck", false);
        filterFileSizeGreaterCombo = PRSManager.createCombo("filterFileSizeGreaterCombo", "filterFileSizeComboValues", new Object[]{0, 1, 2, 3}, 0);
        filterFileSizeLessCheck = PRSManager.createCheck("filterFileSizeLessCheck", false);
        filterFileSizeLessCombo = PRSManager.createCombo("filterFileSizeLessCombo", "filterFileSizeComboValues", new Object[]{0, 1, 2, 3}, 0);
        filterFileTimeCheck = PRSManager.createCheck("filterFileTimeCheck", false);
        filterFileTimeBetweenCheck = PRSManager.createRadio("filterFileTimeBetweenCheck", true);
        filterFileTimeAndNameLabel = PRSManager.createLabel("filterFileTimeAndNameLabel");
        filterFileTimeDuringCheck = PRSManager.createRadio("filterFileTimeDuringCheck", false);

        filterFileSizeGreaterText = new JTextField("0");
        filterFileSizeLessText = new JTextField("1000");
        filterFileSizeLessText.setMinimumSize(new Dimension(100, 4));
        filterFileTimeBetweenText = new JTextField(dateFormat.format(new Date()));
        filterFileTimeAndText = new JTextField(dateFormat.format(new Date()));
        filterFileTimeDuringText = new JTextField("1");
        filterFileTimeDuringCombo = PRSManager.createCombo("filterFileTimeDuringCombo", "filterFileTimeDuringComboValues", new Object[]{"MINUTE", "HOUR", "DAY", "WEEK", "MONTH", "YEAR"}, 0);
        this.statusbar.setKkw(this);

        sortPalette = new SortPalette(this);
        expandCollapsePalette = new ExpandCollapsePalette(this);
        markForDeletionPalette = new MarkForDeletionPalette(this);
        deletePalette = new DeletePalette(this);
        launchPalette = new LaunchPalette(this);

        actions.addAll(sortPalette.getActions());
        actions.addAll(expandCollapsePalette.getActions());
        actions.addAll(markForDeletionPalette.getActions());
        actions.addAll(deletePalette.getActions());
        actions.addAll(launchPalette.getActions());
    }

    public JFrame getMainFrame() {
        if (mainPanel == null) {
            mainFrame = new JFrame(KifKifVersion.PRODUCT_NAME + " v" + KifKifVersion.PRODUCT_VERSION);
            mainFrame.add(getMainPanel());
            mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            mainFrame.setIconImage(((ImageIcon) getIconSet().getIcon("kifkif")).getImage());
            mainFrame.pack();
        }
        return mainFrame;
    }

    public JPanel getMainPanel() {
        if (mainPanel == null) {
            mainPanel = new JPanel(new BorderLayout());
            mainPanel.add(getMenuBar(), BorderLayout.PAGE_START);

            JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
            splitPane.setOneTouchExpandable(true);
            JComponent sourcePanel = createSourcePanel();
            splitPane.add(sourcePanel);
            JComponent resultPanel = createResultPanel();
            splitPane.add(resultPanel);
            splitPane.setOneTouchExpandable(true);
            splitPane.setResizeWeight(0.2f);

            mainPanel.add(splitPane, BorderLayout.CENTER);
            mainPanel.add(statusbar.getComponent(), BorderLayout.PAGE_END);
            init();
            updateResources();
            sourcePanel.setPreferredSize(sourcePanel.getMinimumSize());
//            resultPanel.setPreferredSize(new Dimension(300,200));
        }
        return mainPanel;
    }

    private JMenuBar getMenuBar() {
        if (menuBar == null) {
            menuBar = new JMenuBar();
            fileMenu = PRSManager.createMenu("fileMenu");
            optionsMenu = PRSManager.createMenu("optionsMenu");

            langMenu = new JLocaleMenu(BOOT_LOCALE);
            PRSManager.addSupport(langMenu, "langMenu");
            langMenu.addLocaleSelectedListener(new PropertyChangeListener() {
                public void propertyChange(PropertyChangeEvent evt) {
                    reloadResourceBundle((Locale) evt.getNewValue());
                }
            });
            iconMenu = new JIconSetMenu(DEFAULT_ICONSET_NAME, null);
            iconMenu.addIconSetChangeListener(new PropertyChangeListener() {
                public void propertyChange(PropertyChangeEvent evt) {
                    try {
                        PRSManager.setComponentIconSet(getMainPanel(), (String) evt.getNewValue());
                        updateResources();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });


            optionsMenuItem = PRSManager.createMenuItem("optionsMenuItem");
            newSearchMenuItem = PRSManager.createMenuItem("newSearchMenuItem");
            saveFilterMenuItem = PRSManager.createMenuItem("saveFilterMenuItem");
            loadFilterMenuItem = PRSManager.createMenuItem("loadFilterMenuItem");
            saveResultMenuItem = PRSManager.createMenuItem("saveResultMenuItem");
            loadResultMenuItem = PRSManager.createMenuItem("loadResultMenuItem");
            exportMenu = PRSManager.createMenu("exportMenu");
            quitMenuItem = PRSManager.createMenuItem("quitMenuItem");
            helpMenu = PRSManager.createMenu("helpMenu");
            aboutMenuItem = PRSManager.createMenuItem("aboutMenuItem");
            plafMenu = new JPlafMenu(this.getMainPanel());

            menuBar.add(fileMenu);
            menuBar.add(optionsMenu);
            menuBar.add(helpMenu);

            fileMenu.add(newSearchMenuItem);
            fileMenu.addSeparator();
            fileMenu.add(saveFilterMenuItem);
            fileMenu.add(loadFilterMenuItem);
            fileMenu.addSeparator();
            fileMenu.add(saveResultMenuItem);
            fileMenu.add(loadResultMenuItem);
            fileMenu.addSeparator();
            fileMenu.add(exportMenu);
            fileMenu.addSeparator();
            fileMenu.add(quitMenuItem);

            optionsMenu.add(optionsMenuItem);
            optionsMenu.add(langMenu);
            optionsMenu.add(plafMenu);
            optionsMenu.add(iconMenu);

            helpMenu.add(aboutMenuItem);


            plafMenu.addLookAndFeelChangeListener(new PropertyChangeListener() {
                public void propertyChange(PropertyChangeEvent evt) {
                    if (configuration != null) {
                        configuration.setString("PlafClassName", (String) evt.getNewValue());
                    }
                }
            });

            optionsMenuItem.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    showOptions();
                }
            });
            newSearchMenuItem.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    newSearch();
                }
            });
            saveFilterMenuItem.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    saveFilter();
                }
            });
            loadFilterMenuItem.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    loadFilter();
                }
            });
            saveResultMenuItem.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    saveResult();
                }
            });
            loadResultMenuItem.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    loadResult();
                }
            });

            ExportSupport[] supports = ExportSupportFactory.getAvailableExportSupport();
            for (ExportSupport exportSupport : supports) {
                JMenuItem exportItem = PRSManager.createMenuItem(exportSupport.getName());
                exportMenu.add(exportItem);
                exportItem.putClientProperty("ExportSupport", exportSupport);
                exportItem.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        try {
                            ExportSupport exportSupport = (ExportSupport) ((JComponent) e.getSource()).getClientProperty("ExportSupport");
                            HashMap<String, Object> hashMap = new HashMap<String, Object>();
                            hashMap.put(ExportSupport.KKW_PROPERTY, Kkw.this);
                            exportSupport.export(getResultTree().getSearchData(), null, hashMap);
                        } catch (Exception e1) {
                            JOptionPane.showMessageDialog(getMainPanel(),
                                    getResources().get2("msg.ResultExport.Exception", e1.getMessage()),
                                    getResources().get("msg.warning"),
                                    JOptionPane.ERROR_MESSAGE);
                        }
                    }
                });

            }


            quitMenuItem.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    System.exit(0);
                }
            });

            aboutMenuItem.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    showAbout();
                }
            });
        }
        return menuBar;
    }

    private void showAbout() {
        JOptionPane.showMessageDialog(getMainPanel(), new AboutPanel(), "About", JOptionPane.INFORMATION_MESSAGE);
    }

    private JComponent createSourcePanel() {
        optionTabbedPane = new JTabbedPane();
        PRSManager.addSupport(optionTabbedPane, "optionTabbedPane"/*, new ComponentResourcesUpdater() {
            public void update(JComponent comp, String id, MessageSet resources, IconSet iconSet) {
                JTabbedPane p = (JTabbedPane) comp;
                if (resources != null) {
                    for (int i = 0; i < p.getTabCount(); i++) {
                        p.setTitleAt(i, resources.get(id + "[" + i + "]"));
                    }
                }
            }
        }*/);

        JPanel p0 = createSearchIntoFoldersPanel();
        p0.setBorder(PRSManager.createBorder("SourceFolders"));
        optionTabbedPane.add("", p0);
//        optionTabbedPane.add("", );
        optionTabbedPane.add("", createFilterOptionsPanel());
        optionTabbedPane.add("", createFileOptionsPanel());
        optionTabbedPane.add("", createFolderOptionsPanel());

        JPanel toolbar = new JPanel(new GridBagLayout());
        GridBagLayoutSupport s = new GridBagLayoutSupport(
                "[A=$+][A ][A  ][A  ][B- ][C- ]\n" +
                        ""
        );
        s.setInsets("B;C", new Insets(3, 3, 3, 3));
        toolbar.add(Box.createHorizontalGlue(), s.getConstraints("A"));
        toolbar.add(startButton, s.getConstraints("B"));
        toolbar.add(stopButton, s.getConstraints("C"));

        stopButton.setEnabled(false);
        stopButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (currentProcessingThread != null) {
                    try {
                        currentProcessingThread.stop();
                    } catch (Throwable e1) {
                        //
                    }
                    currentProcessingThread = null;
                    setProcessing(false);
                }
            }
        });
        startButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                startSearch();
            }
        });

        JPanel p = new JPanel(new BorderLayout());
        p.add(optionTabbedPane, BorderLayout.CENTER);
        p.add(toolbar, BorderLayout.PAGE_END);
        return p;


    }

    private void setKifkif(KifKif newKifkif) {
        NEnumSet<FileMode> fileOption = newKifkif.getDiffFileMode();
        boolean caseSensitiveNames = newKifkif.isCaseInsensitiveNames();

//        optionInsensitiveCheck.setSelected(caseSensitiveNames);

        optionFileNameCheck.setSelected(fileOption.contains(FileMode.FILE_NAME));
        optionFileExtensionCheck.setSelected(fileOption.contains(FileMode.FILE_EXTENSION));
        optionFileSizeCheck.setSelected(fileOption.contains(FileMode.FILE_SIZE));
        optionFileChecksumCheck.setSelected(fileOption.containsAny(FileMode.FILE_STAMP_MASK, FileMode.FILE_STAMP_MD5, FileMode.FILE_STAMP_SHA));
        optionFileStampCombo.setSelectedItem(
                fileOption.containsAny(FileMode.FILE_STAMP, FileMode.FILE_STAMP_MASK) ? "DEFAULT" :
                        fileOption.containsAll(FileMode.FILE_STAMP_SHA, FileMode.FILE_STAMP_MASK) ? "SHA-1" :
                                fileOption.containsAny(FileMode.FILE_STAMP_MD5, FileMode.FILE_STAMP_MASK) ? "MD5" :
                                        "DEFAULT"
        );
        optionFileContentCheck.setSelected(fileOption.containsAny(FileMode.FILE_CONTENT_MASK, FileMode.FILE_CONTENT_W, FileMode.FILE_CONTENT_WI, FileMode.FILE_CONTENT_I));
        optionFileContentIgnoreWhitesCheck.setSelected(fileOption.containsAny(FileMode.FILE_CONTENT_W, FileMode.FILE_CONTENT_MASK) || fileOption.containsAny(FileMode.FILE_CONTENT_WI, FileMode.FILE_CONTENT_MASK));
        optionFileContentInsensitiveCheck.setSelected(fileOption.containsAny(FileMode.FILE_CONTENT_I, FileMode.FILE_CONTENT_MASK) || fileOption.containsAny(FileMode.FILE_CONTENT_WI, FileMode.FILE_CONTENT_MASK));

        optionFileTimeCheck.setSelected(fileOption.containsAny(FileMode.FILE_TIME_MASK, FileMode.FILE_TIME_HOUR, FileMode.FILE_TIME_DAY, FileMode.FILE_TIME_WEEK, FileMode.FILE_TIME_MONTH, FileMode.FILE_TIME_YEAR));
        optionFileTimeCombo.setSelectedItem(
                fileOption.containsAny(FileMode.FILE_TIME, FileMode.FILE_TIME_MASK) ? "E" :
                        fileOption.containsAny(FileMode.FILE_TIME_HOUR, FileMode.FILE_TIME_MASK) ? "H" :
                                fileOption.containsAny(FileMode.FILE_TIME_DAY, FileMode.FILE_TIME_MASK) ? "D" :
                                        fileOption.containsAny(FileMode.FILE_TIME_WEEK, FileMode.FILE_TIME_MASK) ? "W" :
                                                fileOption.containsAny(FileMode.FILE_TIME_MONTH, FileMode.FILE_TIME_MASK) ? "M" :
                                                        fileOption.containsAny(FileMode.FILE_TIME_YEAR, FileMode.FILE_TIME_MASK) ? "Y" :
                                                                "E"
        );


        optionFolderSizeCheck.setSelected(fileOption.containsAny(FileMode.FOLDER_SIZE));
        optionFolderNameCheck.setSelected(fileOption.containsAny(FileMode.FOLDER_NAME));
        optionFolderChecksumCheck.setSelected(fileOption.containsAny(FileMode.FOLDER_STAMP));
        optionFolderTimeCheck.setSelected(fileOption.containsAny(FileMode.FOLDER_TIME_MASK, FileMode.FOLDER_TIME_HOUR, FileMode.FOLDER_TIME_DAY, FileMode.FOLDER_TIME_WEEK, FileMode.FOLDER_TIME_MONTH, FileMode.FOLDER_TIME_YEAR));
        optionFolderContentCheck.setSelected(fileOption.containsAny(FileMode.FOLDER_CONTENT));
        optionFolderTimeCombo.setSelectedItem(
                fileOption.containsAny(FileMode.FOLDER_TIME, FileMode.FOLDER_TIME_MASK) ? "E" :
                        fileOption.containsAny(FileMode.FOLDER_TIME_HOUR, FileMode.FOLDER_TIME_MASK) ? "H" :
                                fileOption.containsAny(FileMode.FOLDER_TIME_DAY, FileMode.FOLDER_TIME_MASK) ? "D" :
                                        fileOption.containsAny(FileMode.FOLDER_TIME_WEEK, FileMode.FOLDER_TIME_MASK) ? "W" :
                                                fileOption.containsAny(FileMode.FOLDER_TIME_MONTH, FileMode.FOLDER_TIME_MASK) ? "M" :
                                                        fileOption.containsAny(FileMode.FOLDER_TIME_YEAR, FileMode.FOLDER_TIME_MASK) ? "Y" :
                                                                "E"
        );
        DefaultFileFilter allFoldersFilter = null;
        try {
            allFoldersFilter = (DefaultFileFilter) newKifkif.getGlobalFileFilter();
        } catch (ClassCastException e) {
            //
        }
        if (allFoldersFilter == null) {
            allFoldersFilter = new DefaultFileFilter();
        }
        kifKif.setGlobalFileFilter(allFoldersFilter);
        filterFolderNameCheck.setSelected(allFoldersFilter != null && allFoldersFilter.getUserFolderPattern() != null);
        filterFolderNameText.setText(allFoldersFilter != null ? allFoldersFilter.getUserFolderPattern() : null);
        filterFileNameCheck.setSelected(allFoldersFilter != null && allFoldersFilter.getUserFilePattern() != null);
        filterFileNameText.setText(allFoldersFilter != null ? allFoldersFilter.getUserFilePattern() : null);
        filterFileSizeCheck.setSelected(
                allFoldersFilter != null
                        && (allFoldersFilter.getMinFileSize() >= 0 && allFoldersFilter.getMaxFileSize() >= 0));
        filterFileSizeGreaterCheck.setSelected(allFoldersFilter != null && allFoldersFilter.getMinFileSize() >= 0);
        filterFileSizeLessCheck.setSelected(allFoldersFilter != null && allFoldersFilter.getMaxFileSize() >= 0);
        Object[] lessValue = resolveSize(allFoldersFilter != null && allFoldersFilter.getMaxFileSize() >= 0 ? allFoldersFilter.getMaxFileSize() : 0);
        Object[] greaterValue = resolveSize(allFoldersFilter != null && allFoldersFilter.getMinFileSize() >= 0 ? allFoldersFilter.getMinFileSize() : 0);

        filterFileSizeGreaterText.setText(String.valueOf(greaterValue[0]));
        filterFileSizeGreaterCombo.setSelectedItem(greaterValue[1]);
        filterFileSizeLessText.setText(String.valueOf(lessValue[0]));
        filterFileSizeLessCombo.setSelectedItem(lessValue[1]);

        filterFileHiddenCheck.setSelected(allFoldersFilter.isIncludeHidden());
        filterFileNameCombo.setSelectedItem(!allFoldersFilter.isUserFilePatternIsNegated());


        filterFileTimeCheck.setSelected(
                allFoldersFilter.getDuringLastCalendarType() >= 0
                        ||
                        allFoldersFilter.getMinFileLastModifiedTime() >= 0
                        ||
                        allFoldersFilter.getMaxFileLastModifiedTime() >= 0
        );

        filterFileTimeDuringText.setText(allFoldersFilter.getDuringLastCalendarType() >= 0 ? String.valueOf(allFoldersFilter.getDuringLastCount()) : "");
        filterFileTimeDuringCheck.setSelected(allFoldersFilter.getDuringLastCalendarType() > 0);
        filterFileTimeDuringCombo.setSelectedItem(
                allFoldersFilter.getDuringLastCalendarType() == Calendar.MINUTE ? "MINUTE" :
                        allFoldersFilter.getDuringLastCalendarType() == Calendar.HOUR_OF_DAY ? "HOUR" :
                                allFoldersFilter.getDuringLastCalendarType() == Calendar.DAY_OF_YEAR ? "DAY" :
                                        allFoldersFilter.getDuringLastCalendarType() == Calendar.WEEK_OF_YEAR ? "WEEK" :
                                                allFoldersFilter.getDuringLastCalendarType() == Calendar.MONTH ? "MONTH" :
                                                        allFoldersFilter.getDuringLastCalendarType() == Calendar.YEAR ? "YEAR" :
                                                                "MINUTE"
        );
        filterFileTimeBetweenCheck.setSelected(allFoldersFilter.getDuringLastCalendarType() <= 0);
        filterFileTimeBetweenText.setText(dateFormat.format(new Date(allFoldersFilter.getMinFileLastModifiedTime())));
        filterFileTimeAndText.setText(dateFormat.format(new Date(allFoldersFilter.getMaxFileLastModifiedTime())));

        kifKif.clearIncludedFileSets();
        for (FileSet fileSet : newKifkif.getIncludedFileSets()) {
            kifKif.addIncludedFileSet(fileSet);
        }
        SelectedFoldersTableModel imodel = (SelectedFoldersTableModel) selectedFolders.getModel();
        imodel.fireTableDataChanged();
        updateDependencies();
        if (getConfiguration().getBoolean(KkwOptionDialog.OPTION_CLEAR_RESULT_ON_NEW_SEARCH, true)) {
            getResultTree().clear();
        }

//        updateModelFromView();
    }

    public Object[] resolveSize(long bytes) {
        if ((bytes % (1024 * 1024 * 1024)) == 0) {
            return new Object[]{((bytes / (1024 * 1024 * 1024))), (3)};
        } else if ((bytes % (1024 * 1024)) == 0) {
            return new Object[]{((bytes / (1024 * 1024))), (2)};
        } else if ((bytes % (1024)) == 0) {
            return new Object[]{((bytes / (1024))), (1)};
        } else {
            return new Object[]{((bytes)), (0)};
        }
    }

    private void updateModelFromView() {
        NEnumSet<FileMode> fileOption = NEnumSet.noneOf(FileMode.class);
        fileOption = fileOption.add((optionFileNameCheck.isSelected()) ? FileMode.FILE_NAME : null);
        fileOption = fileOption.add((optionFileExtensionCheck.isSelected()) ? FileMode.FILE_EXTENSION : null);
        fileOption = fileOption.add((optionFileSizeCheck.isSelected()) ? FileMode.FILE_SIZE : null);
        fileOption = fileOption.add((optionFileChecksumCheck.isSelected()) ? ("SHA-1".equals(optionFileStampCombo.getSelectedItem()) ? FileMode.FILE_STAMP_SHA : "MD5".equals(optionFileStampCombo.getSelectedItem()) ? FileMode.FILE_STAMP_MD5 : FileMode.FILE_STAMP) : null);
        String ft = (String) optionFileTimeCombo.getSelectedItem();
        fileOption = fileOption.add((optionFileTimeCheck.isSelected()) ? ("E".equals(ft) ? FileMode.FILE_TIME : "H".equals(ft) ? FileMode.FILE_TIME_HOUR : "D".equals(ft) ? FileMode.FILE_TIME_DAY : "W".equals(ft) ? FileMode.FILE_TIME_WEEK : "M".equals(ft) ? FileMode.FILE_TIME_MONTH : "Y".equals(ft) ? FileMode.FILE_TIME_YEAR : FileMode.FILE_TIME) : null);

        fileOption = fileOption.add((optionFileContentCheck.isEnabled() && optionFileContentCheck.isSelected()) ?
                (optionFileContentIgnoreWhitesCheck.isEnabled() && optionFileContentIgnoreWhitesCheck.isSelected() && optionFileContentInsensitiveCheck.isEnabled() && optionFileContentInsensitiveCheck.isSelected()) ? FileMode.FILE_CONTENT_WI :
                        (optionFileContentIgnoreWhitesCheck.isEnabled() && optionFileContentIgnoreWhitesCheck.isSelected()) ? FileMode.FILE_CONTENT_W :
                                (optionFileContentInsensitiveCheck.isEnabled() && optionFileContentInsensitiveCheck.isSelected()) ? FileMode.FILE_CONTENT_I :
                                        FileMode.FILE_CONTENT :
                null);

        fileOption = fileOption.add((optionFolderNameCheck.isSelected()) ? FileMode.FOLDER_NAME : null);
        fileOption = fileOption.add((optionFolderSizeCheck.isSelected()) ? FileMode.FOLDER_SIZE : null);
        fileOption = fileOption.add((optionFolderChecksumCheck.isSelected()) ? FileMode.FOLDER_STAMP : null);

        ft = (String) optionFolderTimeCombo.getSelectedItem();
        fileOption = fileOption.add((optionFolderTimeCheck.isSelected()) ?
                (
                        "E".equals(ft) ? FileMode.FOLDER_TIME :
                                "H".equals(ft) ? FileMode.FOLDER_TIME_HOUR :
                                        "D".equals(ft) ? FileMode.FOLDER_TIME_DAY :
                                                "W".equals(ft) ? FileMode.FOLDER_TIME_WEEK :
                                                        "M".equals(ft) ? FileMode.FOLDER_TIME_MONTH :
                                                                "Y".equals(ft) ? FileMode.FOLDER_TIME_YEAR :
                                                                        FileMode.FOLDER_TIME
                ) : null);
        fileOption = fileOption.add((optionFolderContentCheck.isSelected()) ? FileMode.FOLDER_CONTENT : null);
        if (fileOption.isEmpty()) {
            fileOption = fileOption.addAll(
                    FileMode.FILE_NAME, FileMode.FOLDER_NAME
                    , FileMode.FILE_SIZE
                    , FileMode.FILE_CONTENT
            );
        }
        kifKif.setFileMode(fileOption.toArray());
        boolean optionCaseInsensitiveValue = getConfiguration().getBoolean(KkwOptionDialog.OPTION_INSENSITIVE_NAMES, false);
        boolean optionRegexpValue = getConfiguration().getBoolean(KkwOptionDialog.OPTION_REGULAR_EXPRESSIONS, false);
        kifKif.setCaseInsensitiveNames(optionCaseInsensitiveValue);
        DefaultFileFilter allFoldersFilter = null;
        try {
            allFoldersFilter = (DefaultFileFilter) kifKif.getGlobalFileFilter();
        } catch (ClassCastException e) {
            //
        }
        if (allFoldersFilter == null) {
            allFoldersFilter = new DefaultFileFilter();
            kifKif.setGlobalFileFilter(allFoldersFilter);
        }
        allFoldersFilter.setSimpleRegexp(!optionRegexpValue);
        allFoldersFilter.setCaseInsensitive(optionCaseInsensitiveValue);
        allFoldersFilter.setUserFolderPattern(filterFolderNameCheck.isSelected() && filterFolderNameCheck.isEnabled() ? filterFolderNameText.getText() : null);
        allFoldersFilter.setUserFilePattern(filterFileNameCheck.isSelected() && filterFileNameCheck.isEnabled() ? filterFileNameText.getText() : null);
        allFoldersFilter.setMinFileSize(filterFileSizeGreaterCheck.isSelected() && filterFileSizeGreaterCheck.isEnabled() ? (long) (Integer.valueOf(filterFileSizeGreaterText.getText()) * (Math.pow(1024, (Integer) filterFileSizeGreaterCombo.getSelectedItem()))) : -1);
        allFoldersFilter.setMaxFileSize(filterFileSizeLessCheck.isSelected() && filterFileSizeLessCheck.isEnabled() ? (long) (Integer.valueOf(filterFileSizeLessText.getText()) * (Math.pow(1024, (Integer) filterFileSizeLessCombo.getSelectedItem()))) : -1);
        allFoldersFilter.setIncludeHidden(filterFileHiddenCheck.isSelected());
        allFoldersFilter.setUserFilePatternIsNegated(!(Boolean) filterFileNameCombo.getSelectedItem());
        allFoldersFilter.setUserFolderPatternIsNegated(!(Boolean) filterFolderNameCombo.getSelectedItem());
        allFoldersFilter.setMinFileLastModifiedTime(-1);
        allFoldersFilter.setMaxFileLastModifiedTime(-1);
        if (filterFileTimeDuringCheck.isSelected() && filterFileTimeDuringCheck.isEnabled()) {
            String type = (String) filterFileTimeDuringCombo.getSelectedItem();
            allFoldersFilter.setMinFileLastModifiedTimeDuringLast(
                    Integer.parseInt(filterFileTimeDuringText.getText()),
                    "MINUTE".equals(type) ? Calendar.MINUTE :
                            "HOUR".equals(type) ? Calendar.HOUR_OF_DAY :
                                    "DAY".equals(type) ? Calendar.DAY_OF_YEAR :
                                            "WEEK".equals(type) ? Calendar.WEEK_OF_YEAR :
                                                    "MONTH".equals(type) ? Calendar.MONTH :
                                                            "YEAR".equals(type) ? Calendar.YEAR :
                                                                    Calendar.MILLISECOND
            );
        } else if (filterFileTimeBetweenCheck.isSelected() && filterFileTimeBetweenCheck.isEnabled()) {
            if (filterFileTimeBetweenText.getText().length() > 0) {
                try {
                    allFoldersFilter.setMinFileLastModifiedTime(dateFormat.parse(filterFileTimeBetweenText.getText()).getTime());
                } catch (ParseException e) {
                    throw new RuntimeException(e);
                }
            }
            if (filterFileTimeAndText.getText().length() > 0) {
                try {
                    allFoldersFilter.setMaxFileLastModifiedTime(dateFormat.parse(filterFileTimeAndText.getText()).getTime());
                } catch (ParseException e) {
                    throw new RuntimeException(e);
                }
            }
        }
        FileSet[] includedFileSets = kifKif.getIncludedFileSets();
        for (FileSet fileSet : includedFileSets) {
            if (fileSet instanceof DefaultFileSet) {
                ((DefaultFileSet) fileSet).setFileFilter(allFoldersFilter);
            }
        }
    }

    private void startSearch() {
        try {
            updateModelFromView();
            currentProcessingThread = new Thread() {
                @Override
                public void run() {
                    doSearchProcess();
                }
            };
            currentProcessingThread.start();
        } catch (Throwable e) {
            JOptionPane.showMessageDialog(mainFrame, e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public void doSearchProcess() {
        if (kifKif.getIncludedFileSetsCount() == 0) {
            return;
        }
        setProcessing(true);
        try {
            resultTree.clear();
            SearchData searchData = kifKif.findDuplicates(NProgressMonitors.of().of(statusbar));
            if (getConfiguration().getBoolean(KkwOptionDialog.OPTION_AUTO_MARK_FILES_TO_DELETE, false)) {
                searchData.setSelectedDuplicatesAuto();
            }
            resultTree.setSearchData(searchData);
        } finally {
            setProcessing(false);
            currentProcessingThread = null;
        }
    }

    private void setProcessing(boolean onHold) {
        if (onHold == processing) {
            return;
        }
        processing = onHold;
        updateDependencies();
        if (support != null) {
            support.firePropertyChange(PROPERTY_PROCESSING, !processing, processing);
        }
    }


    private JComponent createResultPanel() {
        JPanel resultTreePanel = new JPanel(new BorderLayout());
        JScrollPane scrollPane = new JScrollPane(resultTree);
        scrollPane.setPreferredSize(new Dimension(200, 200));
        resultTreePanel.add(scrollPane, BorderLayout.CENTER);
        JToolBar bar = new JToolBar();
        bar.setFloatable(false);
        expandCollapsePalette.installPalette(bar);
        sortPalette.installPalette(bar);
        markForDeletionPalette.installPalette(bar);
        deletePalette.installPalette(bar);
        launchPalette.installPalette(bar);
        bar.addSeparator();
        resultTreePanel.add(bar, BorderLayout.PAGE_START);


        resultTabbedPane = new JTabbedPane();
        resultTabbedPane.add("", resultTreePanel);
        resultTabbedPane.add("", createStatsPanel());
        PRSManager.addSupport(resultTabbedPane, "resultTabbedPane", new ComponentResourcesUpdater() {
            public void update(JComponent comp, String id, MessageSet resources, IconSet iconSet) {
                JTabbedPane p = (JTabbedPane) comp;
                if (resources != null) {
                    for (int i = 0; i < p.getTabCount(); i++) {
                        p.setTitleAt(i, resources.get(id + "[" + i + "]"));
                    }
                }
            }
        });

        return resultTabbedPane;
    }

    private JPanel createStatsPanel() {
        JPanel resultStatsPanel = new JPanel(new GridBagLayout());
        GridBagLayoutSupport s = new GridBagLayoutSupport(
                "[<^H1+=][H1 ][H1 ][H1 ]\n" +
                        "[<L1+=][L1 ][L1 ][L1 ]\n" +
                        "[ <A  ][+B=][<C ][+D=]\n" +
                        "[<H2+=][H2 ][H2 ][H2 ]\n" +
                        "[<L2+=][L2 ][L2 ][L2 ]\n" +
                        "[ <E  ][+F=][<G ][+H=]\n" +
                        "[ <I  ][+J=]\n" +
                        "[<H3+=][H3 ][H3 ][H3 ]\n" +
                        "[<L3+=][L3 ][L3 ][L3 ]\n" +
                        "[<K ][+L=][<M ][+N=]\n" +
                        ""
        );
        s.setInsets("A;E;I;K", new Insets(5, 10, 5, 5));
        s.setInsets("B;C;D;F;G;H;J;L;M;N", new Insets(5, 5, 5, 5));
        s.setInsets("H1;H2;H3", new Insets(5, 5, 5, 5));

        resultStatsPanel.add(statsSrcFoldersCountLabel, s.getConstraints("A"));
        resultStatsPanel.add(statSourceFolderCountText, s.getConstraints("B"));
        resultStatsPanel.add(statsSrcFilesCountLabel, s.getConstraints("C"));
        resultStatsPanel.add(statSourceFileCountText, s.getConstraints("D"));
        resultStatsPanel.add(statsSelectionFoldersCountLabel, s.getConstraints("E"));
        resultStatsPanel.add(statSelectionFolderCountText, s.getConstraints("F"));
        resultStatsPanel.add(statsSelectionFilesCountLabel, s.getConstraints("G"));
        resultStatsPanel.add(statSelectionFileCountText, s.getConstraints("H"));
        resultStatsPanel.add(statsSelectionSizeLabel, s.getConstraints("I"));
        resultStatsPanel.add(statSelectionSizeCountText, s.getConstraints("J"));
        resultStatsPanel.add(statsTimeStartLabel, s.getConstraints("K"));
        resultStatsPanel.add(statSelectionTimeStartText, s.getConstraints("L"));
        resultStatsPanel.add(statsTimeElapsedLabel, s.getConstraints("M"));
        resultStatsPanel.add(statSelectionTimeElapsedText, s.getConstraints("N"));
        resultStatsPanel.add(createLineComponent(), s.getConstraints("L1"));
        resultStatsPanel.add(createLineComponent(), s.getConstraints("L2"));
        resultStatsPanel.add(createLineComponent(), s.getConstraints("L3"));
        resultStatsPanel.add(statsSourceLabel, s.getConstraints("H1"));
        resultStatsPanel.add(statsSelectionLabel, s.getConstraints("H2"));
        resultStatsPanel.add(statsTimeLabel, s.getConstraints("H3"));
        return resultStatsPanel;
    }

    private JComponent createLineComponent() {
        JLabel jLabel = new JLabel("");
        jLabel.setBorder(BorderFactory.createEtchedBorder());
        return jLabel;
    }

    private void statsChanged() {
        SearchStatistics statistics = getResultTree().getSearchData().getStatistics();
        statSourceFolderCountText.setText(String.valueOf(statistics.getSourceFoldersCount()));
        statSourceFileCountText.setText(String.valueOf(statistics.getSourceFilesCount()));
        statSelectionFolderCountText.setText(getResources().get2("extra.countFolders", statusbar.getExtraSelectedFolders()));
        statSelectionFileCountText.setText(getResources().get2("extra.countFiles", statusbar.getExtraSelectedFiles()));
        statSelectionSizeCountText.setText(getFileSizeString(statusbar.getExtraSizeAll()));
        statSelectionTimeStartText.setText(dateFormat.format(new Date(statusbar.getStartTime())));
        statSelectionTimeElapsedText.setText(statusbar.getChronoText());
    }

    private JPanel createSearchIntoFoldersPanel() {
        selectedFolders = new JTable();
        JScrollPane pane = new JScrollPane(selectedFolders);
        selectedFolders.setDropMode(DropMode.ON_OR_INSERT_ROWS);
        selectedFolders.setTransferHandler(new TransferHandler() {

            public boolean canImport(TransferSupport support) {
                // for the demo, we'll only support drops (not clipboard paste)
                if (!support.isDrop()) {
                    return false;
                }

                // we only import Strings
                if (!support.isDataFlavorSupported(DataFlavor.stringFlavor)) {
                    return false;
                }

                return true;
            }

            public boolean importData(TransferSupport support) {
                // if we can't handle the import, say so
                if (!canImport(support)) {
                    return false;
                }

                // fetch the drop location
                JTable.DropLocation dl = (JTable.DropLocation) support
                        .getDropLocation();

                int row = dl.getRow();

                // fetch the data and bail if this fails
                String data;
                try {
                    data = (String) support.getTransferable().getTransferData(
                            DataFlavor.stringFlavor);
                } catch (UnsupportedFlavorException e) {
                    return false;
                } catch (IOException e) {
                    return false;
                }


                addIncludeFiles(Arrays.stream(data.split("\n")).filter(x -> x.length() > 0).toArray(String[]::new), row);

                String[] rowData = data.split(",");
                //tableModel.insertRow(row, rowData);

                Rectangle rect = selectedFolders.getCellRect(row, 0, false);
                if (rect != null) {
                    selectedFolders.scrollRectToVisible(rect);
                }

                // demo stuff - remove for blog
//                model.removeAllElements();
//                model.insertElementAt(getNextString(count++), 0);
                // end demo stuff

                return true;
            }
        });
//        selectedFolders.setDropTarget(new DropTarget() {
//            @Override
//            public synchronized void drop(DropTargetDropEvent dtde) {
//                Point point = dtde.getLocation();
//                int column = selectedFolders.columnAtPoint(point);
//                int row = selectedFolders.rowAtPoint(point);
//                // handle drop inside current table
//                super.drop(dtde);
//            }
//        });
        pane.setTransferHandler(new TransferHandler() {

            public boolean canImport(TransferSupport support) {
                // for the demo, we'll only support drops (not clipboard paste)
                if (!support.isDrop()) {
                    return false;
                }

                // we only import Strings
                if (!support.isDataFlavorSupported(DataFlavor.stringFlavor)) {
                    return false;
                }

                return true;
            }

            public boolean importData(TransferSupport support) {
                // if we can't handle the import, say so
                if (!canImport(support)) {
                    return false;
                }

//                // fetch the drop location
//                JTable.DropLocation dl = (JTable.DropLocation) support
//                        .getDropLocation();
//
//                int row = dl.getRow();

                // fetch the data and bail if this fails
                String data;
                try {
                    data = (String) support.getTransferable().getTransferData(
                            DataFlavor.stringFlavor);
                } catch (UnsupportedFlavorException e) {
                    return false;
                } catch (IOException e) {
                    return false;
                }
                addIncludeFiles(Arrays.stream(data.split("\n")).filter(x -> x.length() > 0).toArray(String[]::new), -1);
                return true;
            }
        });
//        pane.setDropTarget(new DropTarget(){
//            @Override
//            public synchronized void drop(DropTargetDropEvent dtde) {
//                // handle drop outside current table (e.g. add row)
//                String data;
//                try {
//                    data = (String) dtde.getTransferable().getTransferData(
//                            DataFlavor.stringFlavor);
//                } catch (Exception e) {
//                    return;
//                }
//                System.out.println(data);
//
//                super.drop(dtde);
//            }
//        });
        selectedFolders.setModel(new SelectedFoldersTableModel(this, kifKif));
        JPanel p = new JPanel(new BorderLayout());
        pane.setPreferredSize(new Dimension(300, 200));
        p.add(pane, BorderLayout.CENTER);
        JToolBar bar = new JToolBar(JToolBar.VERTICAL);
        bar.setFloatable(false);
        bar.add(addIncludeFolderButton);
        bar.add(addExcludedFolderButton);
        bar.add(removeIncludeFolderButton);
        p.add(bar, BorderLayout.LINE_END);

        addIncludeFolderButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onAddIncludedFolderButtonClicked();
            }
        });

        addExcludedFolderButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onExcludedFolderButtonClicked();
            }
        });

        removeIncludeFolderButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                //FileSet f = (FileSet) selectedFolders.getSelectedValue();
                int i = selectedFolders.getSelectedRow();
                SelectedFoldersTableModel model = (SelectedFoldersTableModel) selectedFolders.getModel();
                model.remove(i);
            }
        });
        return p;
    }

    private void onAddIncludedFolderButtonClicked() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
        fileChooser.setMultiSelectionEnabled(true);
        String old = configuration.getString("LatestFolder");
        File oldFile = old == null ? null : new File(old);
        if (oldFile != null && oldFile.exists()) {
            fileChooser.setSelectedFile(oldFile);
            fileChooser.setCurrentDirectory(new File(old).getParentFile());
        }
        fileChooser.setLocale(currentLocale);
        if (JFileChooser.APPROVE_OPTION == fileChooser.showOpenDialog(mainPanel)) {
            File[] selectedFiles = fileChooser.getSelectedFiles();
            addIncludeFiles(selectedFiles, -1);
        }
    }

    private void onExcludedFolderButtonClicked() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
        fileChooser.setMultiSelectionEnabled(true);
        String old = configuration.getString("LatestFolder");
        File oldFile = old == null ? null : new File(old);
        if (oldFile != null && oldFile.exists()) {
            fileChooser.setSelectedFile(oldFile);
            fileChooser.setCurrentDirectory(new File(old).getParentFile());
        }
        fileChooser.setLocale(currentLocale);
        if (JFileChooser.APPROVE_OPTION == fileChooser.showOpenDialog(mainPanel)) {
            File[] selectedFiles = fileChooser.getSelectedFiles();
            if (selectedFiles.length > 0) {
                if (selectedFiles.length > 0) {
                    configuration.setString("LatestFolder", selectedFiles[0].getPath());
                    final ArrayList<File> excludedFolders = ((DefaultFileFilter) kifKif.getGlobalFileFilter()).getExcludedFolders();
                    excludedFolders.addAll(Arrays.asList(selectedFiles));
                    SelectedFoldersTableModel model = (SelectedFoldersTableModel) selectedFolders.getModel();
                    model.fireTableDataChanged();
                }
            }
        }
    }

    private void addIncludeFiles(String[] selectedFiles, int row) {
        File[] selections = Arrays.stream(selectedFiles == null ? new String[0] : selectedFiles)
                .filter(x -> x != null && x.length() > 0)
                .map(x -> {
                    if (x.startsWith("file:")) {
                        try {
                            return Paths.get(new URL(x).toURI()).toFile();
                        } catch (Exception e) {
                            return null;
                        }
                    } else {
                        return Paths.get(x).toFile();
                    }
                })
                .filter(Objects::nonNull)
                .toArray(File[]::new);
        addIncludeFiles(selections, row);
    }

    private void addIncludeFiles(File[] selectedFiles, int row) {
        if (selectedFiles != null && selectedFiles.length > 0) {
            configuration.setString("LatestFolder", selectedFiles[0].getPath());
            kifKif.addIncludedFileSets(Arrays.stream(selectedFiles).map(DefaultFileSet::new).toArray(FileSet[]::new), row);
            SelectedFoldersTableModel model = (SelectedFoldersTableModel) selectedFolders.getModel();
            model.fireTableDataChanged();
        }
    }

    private JComponent createFilterOptionsPanel() {

        JPanel b0 = new JPanel(new GridBagLayout());
        GridBagLayoutSupport s = new GridBagLayoutSupport("" +
                "[A<+   :     ]:[B+  ]:[C+=  :      :      :     ]\n" +
                "[D<+   :     ]:[E+  ]:[F+=  :      :      :     ]\n" +
                "[G<+=  :      :      :      :      :      :     ]\n" +
                "[H<+=  :      :      :      :      :      :     ]\n" +
                "[02   ]:[ I<+]:[J+= ]:[K<  ]:[L<+]:[M+== ]:[N<  ]\n" +
//                "\n" +
//                "[ I<+= :     ]:[J+=  :      :      :     ]:[K<  ]\n" +
//                "[ L<+= :     ]:[M+=  :      :      :     ]:[N<  ]\n" +
                "[O<+=  :             :      :      :      :     ]\n" +
                "[ P<+= :     ]:[Q+=  :     ]:[R   ]:[S+=  :     ]\n" +
                "[ T<+= :     ]:[U+=  :      :      :     ]:[V<  ]\n" +
                "[0$$$$ :             :      :      :      :     ]\n" +
                ""
        );

        s.setInsets("A;B;C", new Insets(10, 5, 5, 5));
        s.setInsets("E;F;D;J;K;M;N;Q;R;S;U;V;H;G;O", new Insets(5, 5, 5, 5));
        s.setInsets("I;L;P;T", new Insets(5, 20, 5, 5));
        s.setInsets("0", new Insets(5, 20, 20, 5));

        b0.add(filterFolderNameCheck, s.getConstraints("A"));
        b0.add(filterFolderNameCombo, s.getConstraints("B"));
        b0.add(filterFolderNameText, s.getConstraints("C"));
        b0.add(filterFileNameCheck, s.getConstraints("D"));
        b0.add(filterFileNameCombo, s.getConstraints("E"));
        b0.add(filterFileNameText, s.getConstraints("F"));
        b0.add(filterFileHiddenCheck, s.getConstraints("G"));
        b0.add(filterFileSizeCheck, s.getConstraints("H"));
        b0.add(filterFileSizeGreaterCheck, s.getConstraints("I"));
        b0.add(filterFileSizeGreaterText, s.getConstraints("J"));
        b0.add(filterFileSizeGreaterCombo, s.getConstraints("K"));
        b0.add(filterFileSizeLessCheck, s.getConstraints("L"));
        b0.add(filterFileSizeLessText, s.getConstraints("M"));
        b0.add(filterFileSizeLessCombo, s.getConstraints("N"));

        b0.add(filterFileTimeCheck, s.getConstraints("O"));
        b0.add(filterFileTimeBetweenCheck, s.getConstraints("P"));
        b0.add(filterFileTimeBetweenText, s.getConstraints("Q"));
        b0.add(filterFileTimeAndNameLabel, s.getConstraints("R"));
        b0.add(filterFileTimeAndText, s.getConstraints("S"));
        b0.add(filterFileTimeDuringCheck, s.getConstraints("T"));
        b0.add(filterFileTimeDuringText, s.getConstraints("U"));
        b0.add(filterFileTimeDuringCombo, s.getConstraints("V"));
        b0.add(Box.createVerticalGlue(), s.getConstraints("0"));
        b0.add(Box.createHorizontalGlue(), s.getConstraints("02"));

        b0.setBorder(PRSManager.createBorder("optionFileFilterTitledBorder"));

        return b0;
    }

    private JComponent createFolderOptionsPanel() {
        JPanel folderOptionsVerticalPanel = new JPanel(new GridBagLayout());
        GridBagLayoutSupport s = new GridBagLayoutSupport(
                "[Dn+=<^ :      ][00=========]\n" +
                        "[Ds+=<  :      ][00         ]\n" +
                        "[Dt+=< ]:[<Dtc ][00         ]\n" +
                        "[Dg+=<  :      ][00         ]\n" +
                        "[Dc+=<  :      ][00         ]\n" +
                        "[0$$$$  :      ][00         ]\n"
        );
        s.setInsets(".*", new Insets(5, 5, 5, 5));
        s.setInsets("Dn", new Insets(10, 5, 5, 5));
        folderOptionsVerticalPanel.setBorder(PRSManager.createBorder("optionFolderChecksBorder"));
        folderOptionsVerticalPanel.add(optionFolderNameCheck, s.getConstraints("Dn"));
        folderOptionsVerticalPanel.add(optionFolderSizeCheck, s.getConstraints("Ds"));
        folderOptionsVerticalPanel.add(optionFolderTimeCheck, s.getConstraints("Dt"));
        folderOptionsVerticalPanel.add(optionFolderChecksumCheck, s.getConstraints("Dg"));
        folderOptionsVerticalPanel.add(optionFolderContentCheck, s.getConstraints("Dc"));
        folderOptionsVerticalPanel.add(optionFolderTimeCombo, s.getConstraints("Dtc"));
        folderOptionsVerticalPanel.add(Box.createVerticalGlue(), s.getConstraints("0"));
        folderOptionsVerticalPanel.add(Box.createHorizontalGlue(), s.getConstraints("00"));
        return folderOptionsVerticalPanel;
    }

    private JComponent createFileOptionsPanel() {
        JPanel fileOptionsVerticalPanel = new JPanel(new GridBagLayout());
        GridBagLayoutSupport s = new GridBagLayoutSupport(
                "[Fn+=<    ]:[<+   Fe ][00=========]\n" +
                        "[Fs+=<     :         ][00         ]\n" +
                        "[Ft+=<    ]:[<+   Ftc][00         ]\n" +
                        "[Fg +=<   ]:[<+   Fgc][00         ]\n" +
                        "[Fc+=<     :         ][00         ]\n" +
                        "[  Fcci+=< :         ][00         ]\n" +
                        "[  Fciw+=< :         ][00         ]\n" +
                        "[0$$$$     :         ][00         ]\n"
        );
        s.setInsets(".*", new Insets(5, 5, 5, 5));
        s.setInsets("Fn", new Insets(10, 5, 5, 5));
        s.setInsets("Fcci;Fciw", new Insets(5, 20, 5, 5));
        fileOptionsVerticalPanel.setBorder(PRSManager.createBorder("optionFileChecksBorder"));
        fileOptionsVerticalPanel.add(optionFileNameCheck, s.getConstraints("Fn"));
        fileOptionsVerticalPanel.add(optionFileExtensionCheck, s.getConstraints("Fe"));
        fileOptionsVerticalPanel.add(optionFileSizeCheck, s.getConstraints("Fs"));
        fileOptionsVerticalPanel.add(optionFileTimeCheck, s.getConstraints("Ft"));
        fileOptionsVerticalPanel.add(optionFileChecksumCheck, s.getConstraints("Fg"));
        fileOptionsVerticalPanel.add(optionFileStampCombo, s.getConstraints("Fgc"));
        fileOptionsVerticalPanel.add(optionFileContentCheck, s.getConstraints("Fc"));
        fileOptionsVerticalPanel.add(optionFileTimeCombo, s.getConstraints("Ftc"));

        fileOptionsVerticalPanel.add(optionFileContentInsensitiveCheck, s.getConstraints("Fcci"));
        fileOptionsVerticalPanel.add(optionFileContentIgnoreWhitesCheck, s.getConstraints("Fciw"));
        fileOptionsVerticalPanel.add(Box.createVerticalGlue(), s.getConstraints("0"));
        fileOptionsVerticalPanel.add(Box.createHorizontalGlue(), s.getConstraints("00"));

        return fileOptionsVerticalPanel;
    }

    public void reloadResourceBundle(Locale locale) {
        currentLocale = locale == null ? DEFAULT_LOCALE : locale;
        Locale.setDefault(currentLocale);
        resources.setLocale(currentLocale);
        resources.addBundle("net.thevpc.kifkif.messageset.Kifkif");
        updateResources();
        if (configuration != null) {
            configuration.setString("java.util.Locale", currentLocale.toString());
        }
    }

    private void init() {
        ButtonGroup lastmodifiedGroup = new ButtonGroup();
        lastmodifiedGroup.add(filterFileTimeBetweenCheck);
        lastmodifiedGroup.add(filterFileTimeDuringCheck);
        ItemListener dependeciesUpdater = new ItemListener() {
            public void itemStateChanged(ItemEvent e) {
                updateDependencies();
            }
        };
        filterFolderNameCheck.addItemListener(dependeciesUpdater);
        filterFileNameCheck.addItemListener(dependeciesUpdater);
        filterFileSizeCheck.addItemListener(dependeciesUpdater);
        filterFileSizeGreaterCheck.addItemListener(dependeciesUpdater);
        filterFileSizeLessCheck.addItemListener(dependeciesUpdater);
        filterFileTimeCheck.addItemListener(dependeciesUpdater);
        filterFileTimeBetweenCheck.addItemListener(dependeciesUpdater);
        filterFileTimeDuringCheck.addItemListener(dependeciesUpdater);
        updateDependencies();

        statusbar.addPropertyChageListener(StatusbarTaskMonitor.PROPERTY_STAT_CHANGED, new PropertyChangeListener() {
            public void propertyChange(PropertyChangeEvent evt) {
                statsChanged();
            }
        });

        ItemListener fileItemListener = new ItemListener() {
            public void itemStateChanged(ItemEvent e) {
                JCheckBox cb = (JCheckBox) e.getSource();
                if (!cb.isSelected()) {
                    if (!optionFileNameCheck.isSelected() && !optionFileExtensionCheck.isSelected() && !optionFileSizeCheck.isSelected() && !optionFileChecksumCheck.isSelected() && !optionFileTimeCheck.isSelected()) {
                        cb.setSelected(true);
                    }
                }
                updateDependencies();
            }
        };
        optionFileExtensionCheck.addItemListener(fileItemListener);
        optionFileNameCheck.addItemListener(fileItemListener);
        optionFileSizeCheck.addItemListener(fileItemListener);
        optionFileTimeCheck.addItemListener(fileItemListener);
        optionFileContentCheck.addItemListener(fileItemListener);
        optionFileChecksumCheck.addItemListener(fileItemListener);
        ItemListener dependenciesUpdaterItemListener = new ItemListener() {
            public void itemStateChanged(ItemEvent e) {
                updateDependencies();
            }
        };

        optionFolderChecksumCheck.addItemListener(dependenciesUpdaterItemListener);
        optionFolderTimeCheck.addItemListener(dependenciesUpdaterItemListener);

        statSourceFolderCountText.setEditable(false);
        statSourceFileCountText.setEditable(false);
        statSelectionFolderCountText.setEditable(false);
        statSelectionFileCountText.setEditable(false);
        statSelectionSizeCountText.setEditable(false);
        statSelectionTimeStartText.setEditable(false);
        statSelectionTimeElapsedText.setEditable(false);
    }

    private void updateDependencies() {
        statusbar.setStopped(!processing);
        startButton.setEnabled(!processing);
        stopButton.setEnabled(processing);
        addIncludeFolderButton.setEnabled(!processing);
        removeIncludeFolderButton.setEnabled(!processing);
        addExcludedFolderButton.setEnabled(!processing);
        optionFileNameCheck.setEnabled(!processing);
        optionFileExtensionCheck.setEnabled(!processing);
        optionFileSizeCheck.setEnabled(!processing);
        optionFileChecksumCheck.setEnabled(!processing);
        optionFileContentCheck.setEnabled(!processing);
        optionFileTimeCheck.setEnabled(!processing);
        optionFolderNameCheck.setEnabled(!processing);
        optionFolderSizeCheck.setEnabled(!processing);
        optionFolderChecksumCheck.setEnabled(!processing);
        optionFolderTimeCheck.setEnabled(!processing);
        optionFolderContentCheck.setEnabled(!processing);

        filterFolderNameCombo.setEnabled(!processing);
        filterFolderNameText.setEnabled(!processing);
        filterFileNameCombo.setEnabled(!processing);
        filterFileNameText.setEnabled(!processing);

        filterFileNameCheck.setEnabled(!processing);
        filterFolderNameCheck.setEnabled(!processing);
        filterFileSizeCheck.setEnabled(!processing);
        filterFileHiddenCheck.setEnabled(!processing);
        filterFileSizeGreaterCheck.setEnabled(!processing);
        filterFileSizeGreaterCombo.setEnabled(!processing);
        filterFileSizeLessCheck.setEnabled(!processing);
        filterFileSizeLessCombo.setEnabled(!processing);
        filterFileTimeCheck.setEnabled(!processing);
        filterFileTimeBetweenCheck.setEnabled(!processing);
        filterFileTimeAndNameLabel.setEnabled(!processing);
        filterFileTimeDuringCheck.setEnabled(!processing);
        filterFileSizeGreaterText.setEnabled(!processing);
        filterFileSizeLessText.setEnabled(!processing);
        filterFileTimeBetweenText.setEnabled(!processing);
        filterFileTimeAndText.setEnabled(!processing);
        filterFileTimeDuringText.setEnabled(!processing);
        filterFileTimeDuringCombo.setEnabled(!processing);

        filterFolderNameCombo.setEnabled(!processing && filterFolderNameCheck.isSelected() && filterFolderNameCheck.isEnabled());
        filterFolderNameText.setEnabled(!processing && filterFolderNameCheck.isSelected() && filterFolderNameCheck.isEnabled());
        filterFileNameCombo.setEnabled(!processing && filterFileNameCheck.isSelected() && filterFileNameCheck.isEnabled());
        filterFileNameText.setEnabled(!processing && filterFileNameCheck.isSelected() && filterFileNameCheck.isEnabled());
        filterFileSizeGreaterCheck.setEnabled(!processing && filterFileSizeCheck.isSelected() && filterFileSizeCheck.isEnabled());
        filterFileSizeLessCheck.setEnabled(!processing && filterFileSizeCheck.isSelected() && filterFileSizeCheck.isEnabled());

        filterFileSizeGreaterText.setEnabled(!processing && filterFileSizeGreaterCheck.isSelected() && filterFileSizeGreaterCheck.isEnabled());
        filterFileSizeGreaterCombo.setEnabled(!processing && filterFileSizeGreaterCheck.isSelected() && filterFileSizeGreaterCheck.isEnabled());
        filterFileSizeLessText.setEnabled(!processing && filterFileSizeLessCheck.isSelected() && filterFileSizeLessCheck.isEnabled());
        filterFileSizeLessCombo.setEnabled(!processing && filterFileSizeLessCheck.isSelected() && filterFileSizeLessCheck.isEnabled());

        filterFileTimeBetweenCheck.setEnabled(!processing && filterFileTimeCheck.isSelected() && filterFileTimeCheck.isEnabled());
        filterFileTimeDuringCheck.setEnabled(!processing && filterFileTimeCheck.isSelected() && filterFileTimeCheck.isEnabled());

        filterFileTimeBetweenText.setEnabled(!processing && filterFileTimeBetweenCheck.isSelected() && filterFileTimeBetweenCheck.isEnabled());
        filterFileTimeAndText.setEnabled(!processing && filterFileTimeBetweenCheck.isSelected() && filterFileTimeBetweenCheck.isEnabled());
        filterFileTimeAndText.setEnabled(!processing && filterFileTimeBetweenCheck.isSelected() && filterFileTimeBetweenCheck.isEnabled());


        filterFileTimeDuringCombo.setEnabled(!processing && filterFileTimeDuringCheck.isSelected() && filterFileTimeDuringCheck.isEnabled());
        filterFileTimeDuringText.setEnabled(!processing && filterFileTimeDuringCheck.isSelected() && filterFileTimeDuringCheck.isEnabled());

        optionFileStampCombo.setEnabled(!processing && optionFileChecksumCheck.isSelected() && optionFileChecksumCheck.isEnabled());
        optionFileContentInsensitiveCheck.setEnabled(optionFileContentCheck.isEnabled() && optionFileContentCheck.isSelected());
        optionFileContentIgnoreWhitesCheck.setEnabled(optionFileContentCheck.isEnabled() && optionFileContentCheck.isSelected());
        optionFileTimeCombo.setEnabled(optionFileTimeCheck.isEnabled() && optionFileTimeCheck.isSelected());
        optionFolderTimeCombo.setEnabled(optionFolderTimeCheck.isEnabled() && optionFolderTimeCheck.isSelected());
    }

    private void updateResources() {
        PRSManager.applyOrientation(getMainPanel());
        PRSManager.applyOrientation(getMainFrame());
        PRSManager.update(actions, getMainPanel(), resources, getIconSet());
        PRSManager.update(actions, getResultTree().getPopupMenu(), resources, getIconSet());
    }

    public MessageSet getResources() {
        return resources;
    }

    public IconSet getIconSet() {
        return IconSetManager.getIconSet();
    }

    public DuplicateListTree getResultTree() {
        return resultTree;
    }

    public KifKif getKifKif() {
        return kifKif;
    }

    public Configuration getConfiguration() {
        return configuration;
    }

    public String getFileSizeString(long size) {
        int type;
        long value;
        if (size == 0) {
            type = 0;
            value = size;
        } else if (size < 1024) {
            type = 1;
            value = size;
        } else if (size < 1024 * 1024) {
            double d = ((double) size) / 1024.0;
            type = 2;
            value = (long) d;
        } else {
            double d = ((double) size) / (1024.0 * 1024.0);
            type = 3;
            value = (long) d;
        }
        return resources.get2("fileSize", value, type);
    }

    public JPlafMenu getPlafMenu() {
        return plafMenu;
    }

    public JMenu getLangMenu() {
        return langMenu;
    }

    public DateFormat getDateFormat() {
        return dateFormat;
    }

    public boolean isProcessing() {
        return processing;
    }

    public void addPropertyChangeListener(String property, PropertyChangeListener l) {
        if (support == null) {
            support = new PropertyChangeSupport(this);
        }
        support.addPropertyChangeListener(property, l);
    }

    public void showOptions() {
        new KkwOptionDialog(this).showDialog();
    }

    public void saveFilter() {
        try {
            updateModelFromView();
            JFileChooser chooser = new JFileChooser();
            chooser.addChoosableFileFilter(filterFilter);
            chooser.setFileFilter(filterFilter);
            String old = getConfiguration().getString("SaveLoadFilter.selected");
            if (old != null) {
                chooser.setSelectedFile(new File(old));
            }

            if (JFileChooser.APPROVE_OPTION == chooser.showSaveDialog(getMainPanel())) {
                File f = chooser.getSelectedFile();
                if (!filterFilter.accept(f)) {
                    f = new File(f.getPath() + FILE_EXTENSION_FILTER);
                }
                try {
                    getConfiguration().setString("SaveLoadFilter.selected", f.getCanonicalPath());
                } catch (IOException e) {
                    getConfiguration().setString("SaveLoadFilter.selected", f.getAbsolutePath());
                }
                XmlUtils.objectToXml(kifKif, f, null, null);
            }
        } catch (Throwable e) {
            JOptionPane.showMessageDialog(mainFrame, e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public void saveResult() {
        try {
            JFileChooser chooser = new JFileChooser();
            chooser.addChoosableFileFilter(resultFilter);
            chooser.setFileFilter(resultFilter);
            String old = getConfiguration().getString("SaveLoadResult.selected");
            if (old != null) {
                chooser.setSelectedFile(new File(old));
            }

            if (JFileChooser.APPROVE_OPTION == chooser.showSaveDialog(getMainPanel())) {
                File f = chooser.getSelectedFile();
                if (!resultFilter.accept(f)) {
                    f = new File(f.getPath() + FILE_EXTENSION_RESULT);
                }
                try {
                    getConfiguration().setString("SaveLoadResult.selected", f.getCanonicalPath());
                } catch (IOException e) {
                    getConfiguration().setString("SaveLoadResult.selected", f.getAbsolutePath());
                }
                XmlUtils.objectToXml(getResultTree().getSearchData(), f, null, null);
            }
        } catch (Throwable e) {
            JOptionPane.showMessageDialog(mainFrame, e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public void loadFilter() {
        try {
            JFileChooser chooser = new JFileChooser();
            chooser.addChoosableFileFilter(filterFilter);
            chooser.setFileFilter(filterFilter);
            String old = getConfiguration().getString("SaveLoadFilter.selected");
            if (old != null) {
                chooser.setSelectedFile(new File(old));
            }

            if (JFileChooser.APPROVE_OPTION == chooser.showOpenDialog(getMainPanel())) {
                File f = chooser.getSelectedFile();
                try {
                    getConfiguration().setString("SaveLoadFilter.selected", f.getCanonicalPath());
                } catch (IOException e) {
                    getConfiguration().setString("SaveLoadFilter.selected", f.getAbsolutePath());
                }
                setKifkif((KifKif) XmlUtils.xmlToObject(f, null, null));
            }
        } catch (Throwable e) {
            JOptionPane.showMessageDialog(mainFrame, e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public void newSearch() {
        try {
            setKifkif(new KifKif());
        } catch (Throwable e) {
            JOptionPane.showMessageDialog(mainFrame, e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public void loadResult() {
        try {
            JFileChooser chooser = new JFileChooser();
            chooser.addChoosableFileFilter(resultFilter);
            chooser.setFileFilter(resultFilter);
            String old = getConfiguration().getString("SaveLoadResult.selected");
            if (old != null) {
                chooser.setSelectedFile(new File(old));
            }

            if (JFileChooser.APPROVE_OPTION == chooser.showOpenDialog(getMainPanel())) {
                File f = chooser.getSelectedFile();
                try {
                    getConfiguration().setString("SaveLoadResult.selected", f.getCanonicalPath());
                } catch (IOException e) {
                    getConfiguration().setString("SaveLoadResult.selected", f.getAbsolutePath());
                }
                SearchData d = (SearchData) XmlUtils.xmlToObject(f, null, null);
                getResultTree().setSearchData(d);
                if (d.getKifkif() != null) {
                    setKifkif(d.getKifkif());
                }
            }
        } catch (Throwable e) {
            JOptionPane.showMessageDialog(mainFrame, e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public MessageSet getMessageSet() {
        return resources;
    }

    public void showFrame() {
        getMainFrame().setLocation(SwingUtilities3.getScreenCentredPosition(getMainFrame()));
        getMainFrame().setVisible(true);
    }
}
