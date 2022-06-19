package net.thevpc.kifkif;

import net.thevpc.kifkif.stamp.*;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;


/**
 * User: taha
 * Date: 22 dec. 2004
 * Time: 18:52:28
 */
public class FileStampFilterList implements Serializable {
    protected ArrayList<FilestampFilter> filters = new ArrayList<FilestampFilter>(3);

    private void condAdd(FilestampFilter s) {
        if (s != null && !filters.contains(s)) {
            filters.add(s);
        }
    }

    public FileStampFilterList() {
    }

    public FileStampFilterList(FileMode... all) {
        addFileFilters(all);
    }

    public FileStampFilterList(FilestampFilter... filters) {
        this.filters.addAll(Arrays.asList(filters));
    }

    public Filestamp getFirstFilterId(File file, KifKif kifKif) {
        return filters.get(0).getFilestamp(file, null, kifKif);
    }

//    public net.thevpc.kifkif.Filestamp getFilterId(File file, int level) {
//        return level==0?getFirstFilterId(file):
//        getNextFilterSig(file, getFilterId(file, level-1), level);
//    }

    public Filestamp getNextFilterSig(File file, Filestamp latestFilterStamp, int level, KifKif kifkif) {
        return filters.get(level).getFilestamp(file, latestFilterStamp, kifkif);
    }

    public int getMaxLevels() {
        return filters.size();
    }

    public FilestampFilter getFilestampFilter(int level) {
        return filters.get(level);
    }

    public void addFolderFilter(FileMode s) {
        if (s != null) {
            switch (s) {
                case FOLDER_NAME: {
                    condAdd(new FileNameStampFilter());
                    break;
                }
//                case FOLDER_EXTENSION: {
//                    condAdd(new FileExtensionStampFilter());
//                    break;
//                }
                case FOLDER_SIZE: {
                    condAdd(new FolderSizeStampFilter());
                    break;
                }
//                case FOLDER_STAMP_MD5: {
//                    condAdd(new FileContentMessageDigestStampFilter("MD5"));
//                    break;
//                }
//                case FOLDER_STAMP_SHA: {
//                    condAdd(new FileContentMessageDigestStampFilter("SHA-1"));
//                    break;
//                }
                case FOLDER_STAMP: {
                    condAdd(new FolderContentStampFilter());
                    break;
                }
                case FOLDER_TIME: {
                    condAdd(new FileTimeStampFilter());
                    break;
                }
                case FOLDER_TIME_HOUR: {
                    condAdd(new FileTimeStampHourFilter());
                    break;
                }
                case FOLDER_TIME_DAY: {
                    condAdd(new FileTimeStampDayFilter());
                    break;
                }
                case FOLDER_TIME_WEEK: {
                    condAdd(new FileTimeStampWeekFilter());
                    break;
                }
                case FOLDER_TIME_MONTH: {
                    condAdd(new FileTimeStampMonthFilter());
                    break;
                }
                case FOLDER_TIME_YEAR: {
                    condAdd(new FileTimeStampYearFilter());
                    break;
                }
            }
        }
    }
    public void addFileStamp(FileMode s) {
        if (s != null) {
            switch (s) {
                case FILE_NAME: {
                    condAdd(new FileNameStampFilter());
                    break;
                }
                case FILE_EXTENSION: {
                    condAdd(new FileExtensionStampFilter());
                    break;
                }
                case FILE_SIZE: {
                    condAdd(new FileSizeStampFilter());
                    break;
                }
                case FILE_STAMP_MD5: {
                    condAdd(new FileContentMessageDigestStampFilter("MD5"));
                    break;
                }
                case FILE_STAMP_SHA: {
                    condAdd(new FileContentMessageDigestStampFilter("SHA-1"));
                    break;
                }
                case FILE_STAMP: {
                    condAdd(new FileContentStampFilter());
                    break;
                }
                case FILE_TIME: {
                    condAdd(new FileTimeStampFilter());
                    break;
                }
                case FILE_TIME_HOUR: {
                    condAdd(new FileTimeStampHourFilter());
                    break;
                }
                case FILE_TIME_DAY: {
                    condAdd(new FileTimeStampDayFilter());
                    break;
                }
                case FILE_TIME_WEEK: {
                    condAdd(new FileTimeStampWeekFilter());
                    break;
                }
                case FILE_TIME_MONTH: {
                    condAdd(new FileTimeStampMonthFilter());
                    break;
                }
                case FILE_TIME_YEAR: {
                    condAdd(new FileTimeStampYearFilter());
                    break;
                }
            }
        }
    }

    public FileStampFilterList addFileFilters(FileMode... all) {
        if (all != null) {
            for (FileMode fileMode : all) {
                addFileStamp(fileMode);
            }
        }
        return this;
    }

    public FileStampFilterList addFolderFilters(FileMode... all) {
        if (all != null) {
            for (FileMode fileMode : all) {
                addFolderFilter(fileMode);
            }
        }
        return this;
    }

}
