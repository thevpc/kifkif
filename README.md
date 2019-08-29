# kifkif
Imported projectfrom **java.net**
Kifkif is a new Open Source Localizable Tool for finding out all duplicates of either Files and Folders.
It is designed so that it can be used in different ways:

    As an Extensible Duplicates Finding Library to include in any other project or application
    As a Console Application (Unix command like)
    As a Windowed GUI Application (GUI Front end)

## Main Features

    Find File Duplicates
    Find Hole Folder Duplicates
    Automatic / Interactive Duplicates Removal
    Localization Support : Arabic, English, French and Italian
    Extensible Library
    User Freindly Front end
    Multiple Source FileSet Support (Multiple Folder and Files Selection to searh into)
    File types filter (according to file name extension)
    ... And any other useful feature you'll suggest ;)

## Requirement

Java Runtime Environment(JRE) 1.5 or higher is required for running this tool.

## Installation
to run netbeans launcher you should have a valid **nuts** installation (>=0.5.7)  and a valid java 8 or later installation.
see [nuts wiki](https://github.com/thevpc/nuts/wiki)

then just type to install it:

```
nuts install kifkif
```

## Launching the application
To launch the tool  type :
```
nuts kifkif
```

## Updating netbeans-launcher

```
nuts update kifkif
```


## Code Examples  
(see test folder)
    Example01
    Example02

## Change log
### version 1.2b1 (04/03/2004)

    Improuvements in Console Mode
    Bug fixes

### version 1.1 (24/01/2004)

    Added new Time stampinf Filter
    Added Versatile and deterministic progression
    Added Proress chronometer
    Summary support (Nbr of duplicates, total size of duplicates)
    Added Reverse Order Sorting, Folders first Sorting
    Improvements in File Selection
        Select/Deselect
        Clear selection
        Auto select
        Select by location
        Deselect by location
    Added Italian Localization support : Arabic, English, French and Italian are right now Supported
    Added PL&F support
    Added Shell interaction Support (Open file)
    Added Memory Use Icon Tray
    Lots of GUI Improvements
    Auto Check for updates (In the About Panel)
    Minor bug fixes

### version 1.0 (17/01/2004)

    First stable version
    Duplicates Removal
    Export Plain Text
    Localization support : Arabic, English, French are Supported
    Minor bug fixes

### version 1.0alpha2 (15/01/2005)

    Simple GUI Front End
    File filter support

### version 1.0alpha1 (07/01/2005)

    First release
    Simple Console application

## Features Planned for future versions

    More interactive Remove Duplicates
    Show Duplicates summary (depends on type) plugin for Summary Icon (like in linux) for text, and images
    Tree Popup support (select all but this, select all like this menu items ...etc.)
    Save Result so that you can remove duplicates later in witch case will be prompt for relaunching the search on restart
    Save/load search options
    D&D support
    Export Result to
        Text File
        XML File
        Html File
    Support for more Locales (German, Espaniol)
    Help, more help
    Website, pay some attention on website
    Sound/animation Alarm support
    More content comparators (special pdf, special xls and xml support, case insensitive content,)
    Rewrite Kifkif for earlier targets of JVM (specially 1.1, 1.2, 1.3 & 1.4)
