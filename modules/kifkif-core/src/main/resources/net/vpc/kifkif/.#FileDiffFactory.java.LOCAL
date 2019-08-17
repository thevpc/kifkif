package net.vpc.kifkif;

import java.util.ArrayList;
import java.util.Collection;

import net.vpc.kifkif.content.FileByteContentComparator;
import net.vpc.kifkif.content.FileIgnoreWhitesContentComparator;
import net.vpc.kifkif.content.FileIgnoreWhitesInsensitiveContentComparator;
import net.vpc.kifkif.content.FileInsensitiveCharContentComparator;
import net.vpc.kifkif.content.FolderContentComparator;
import net.vpc.kifkif.stamp.FileContentMessageDigestStampFilter;
import net.vpc.kifkif.stamp.FileContentStampFilter;
import net.vpc.kifkif.stamp.FileExtensionStampFilter;
import net.vpc.kifkif.stamp.FileNameStampFilter;
import net.vpc.kifkif.stamp.FileSizeStampFilter;
import net.vpc.kifkif.stamp.FileTimeStampDayFilter;
import net.vpc.kifkif.stamp.FileTimeStampFilter;
import net.vpc.kifkif.stamp.FileTimeStampHourFilter;
import net.vpc.kifkif.stamp.FileTimeStampMonthFilter;
import net.vpc.kifkif.stamp.FileTimeStampWeekFilter;
import net.vpc.kifkif.stamp.FileTimeStampYearFilter;
import net.vpc.kifkif.stamp.FolderContentStampFilter;
import net.vpc.kifkif.stamp.FolderSizeStampFilter;

/**
 * utiliy Class for Filtering mode consants
 * User: taha
 * Date: 24 dec. 2004
 * Time: 16:13:28
 */
public final class FileDiffFactory {

    private FileDiffFactory() {
    }

    public static final int UNKNOWN = 1024 * 1024;

    private static final int FILE_BASE = 1;

    private static final int FOLDER_BASE = 1;

    /**
     * Filestamp for sensitive Filename
     */
    @SuppressWarnings({"PointlessArithmeticExpression"})
    public static final int FILE_NAME = FILE_BASE <<0;

    /**
     * Filestamp for file size
     */
    @SuppressWarnings({"PointlessArithmeticExpression"})
    public static final int FILE_EXTENSION = FILE_BASE <<1;

    /**
     * Filestamp for file size
     */
    @SuppressWarnings({"PointlessArithmeticExpression"})
    public static final int FILE_SIZE = FILE_BASE <<2;



    /**
     * Filesamp for File content (content hashcode), slow
     */
    @SuppressWarnings({"PointlessArithmeticExpression"})
    public static final int FILE_STAMP = FILE_BASE <<6;

    @SuppressWarnings({"PointlessArithmeticExpression"})
    public static final int FILE_STAMP_MD5 = 2*FILE_STAMP;

    @SuppressWarnings({"PointlessArithmeticExpression"})
    public static final int FILE_STAMP_SHA = 3*FILE_STAMP;

    /**
     * Filesamp for File content (content hashcode), slow
     */
    @SuppressWarnings({"PointlessArithmeticExpression"})
    public static final int FILE_STAMP_MASK = 3*FILE_STAMP;

    /**
     * Filesamp for File lastModified time
     */
    @SuppressWarnings({"PointlessArithmeticExpression"})
    public static final int FILE_TIME = FILE_BASE <<10;
    @SuppressWarnings({"PointlessArithmeticExpression"})
    public static final int FILE_TIME_HOUR = 2*FILE_TIME;
    @SuppressWarnings({"PointlessArithmeticExpression"})
    public static final int FILE_TIME_DAY = 3*FILE_TIME;
    @SuppressWarnings({"PointlessArithmeticExpression"})
    public static final int FILE_TIME_WEEK = 4*FILE_TIME;
    @SuppressWarnings({"PointlessArithmeticExpression"})
    public static final int FILE_TIME_MONTH = 5*FILE_TIME;
    @SuppressWarnings({"PointlessArithmeticExpression"})
    public static final int FILE_TIME_YEAR = 6*FILE_TIME;

    @SuppressWarnings({"PointlessArithmeticExpression"})
    public static final int FILE_TIME_MASK = 7*FILE_TIME;


    /**
     * Folder content comparaison (Files names are compared)
     */

    @SuppressWarnings({"PointlessArithmeticExpression"})
    public static final int FILE_CONTENT = FILE_BASE <<20;

    @SuppressWarnings({"PointlessArithmeticExpression"})
    public static final int FILE_CONTENT_I = 2*FILE_CONTENT;

    @SuppressWarnings({"PointlessArithmeticExpression"})
    public static final int FILE_CONTENT_W = 3*FILE_CONTENT;

    @SuppressWarnings({"PointlessArithmeticExpression"})
    public static final int FILE_CONTENT_WI = 4*FILE_CONTENT;

    @SuppressWarnings({"PointlessArithmeticExpression"})
    public static final int FILE_CONTENT_MASK = 7*FILE_CONTENT;

    /**
     * Filestamp for sensitive filename
     */
    @SuppressWarnings({"PointlessArithmeticExpression"})
    public static final int FOLDER_NAME = FOLDER_BASE <<0;
    /**
     * Filestamp for file size (list count)
     */
    @SuppressWarnings({"PointlessArithmeticExpression"})
    public static final int FOLDER_SIZE = FOLDER_BASE <<1;
    /**
     * Filesamp for Folder list (list string hashcode), slow
     */
    @SuppressWarnings({"PointlessArithmeticExpression"})
    public static final int FOLDER_STAMP = FOLDER_BASE <<2;
    /**
     * Folder content comparaison (Files names are compared)
     */
    @SuppressWarnings({"PointlessArithmeticExpression"})
    public static final int FOLDER_CONTENT = FOLDER_BASE <<3;

    /**
     * Filesamp for File lastModified time
     */
    @SuppressWarnings({"PointlessArithmeticExpression"})
    public static final int FOLDER_TIME = FOLDER_BASE <<5;
    @SuppressWarnings({"PointlessArithmeticExpression"})
    public static final int FOLDER_TIME_HOUR = 2*FOLDER_TIME;
    @SuppressWarnings({"PointlessArithmeticExpression"})
    public static final int FOLDER_TIME_DAY = 3*FOLDER_TIME;
    @SuppressWarnings({"PointlessArithmeticExpression"})
    public static final int FOLDER_TIME_WEEK = 4*FOLDER_TIME;
    @SuppressWarnings({"PointlessArithmeticExpression"})
    public static final int FOLDER_TIME_MONTH = 5*FOLDER_TIME;
    @SuppressWarnings({"PointlessArithmeticExpression"})
    public static final int FOLDER_TIME_YEAR = 6*FOLDER_TIME;

    @SuppressWarnings({"PointlessArithmeticExpression"})
    public static final int FOLDER_TIME_MASK = 7*FOLDER_TIME;

    public static FilestampFilterList createFilestampFilterList(int mode) {
        Collection<FilestampFilter> a = new ArrayList<FilestampFilter>();
        if ((mode & FILE_NAME) != 0) {
            a.add(new FileNameStampFilter());
        }
        if ((mode & FILE_EXTENSION) != 0) {
            a.add(new FileExtensionStampFilter());
        }
        if ((mode & FILE_SIZE) != 0) {
            a.add(new FileSizeStampFilter());
        }
        if ((mode & FILE_STAMP_MASK) == FILE_STAMP) {
            a.add(new FileContentStampFilter());
        }else if ((mode & FILE_STAMP_MASK) == FILE_STAMP_MD5) {
            a.add(new FileContentMessageDigestStampFilter("MD5"));
        }else if ((mode & FILE_STAMP_MASK) == FILE_STAMP_SHA) {
            a.add(new FileContentMessageDigestStampFilter("SHA-1"));
        }

        if ((mode & FILE_TIME_MASK) == FILE_TIME) {
            a.add(new FileTimeStampFilter());
        }else if ((mode & FILE_TIME_MASK) == FILE_TIME_HOUR) {
            a.add(new FileTimeStampHourFilter());
        }else if ((mode & FILE_TIME_MASK) == FILE_TIME_DAY) {
            a.add(new FileTimeStampDayFilter());
        }else if ((mode & FILE_TIME_MASK) == FILE_TIME_WEEK) {
            a.add(new FileTimeStampWeekFilter());
        }else if ((mode & FILE_TIME_MASK) == FILE_TIME_MONTH) {
            a.add(new FileTimeStampMonthFilter());
        }else if ((mode & FILE_TIME_MASK) == FILE_TIME_YEAR) {
            a.add(new FileTimeStampYearFilter());
        }

        if (a.size() == 0) {
            return null;
        }
        return new FilestampFilterList(a.toArray(new FilestampFilter[a.size()]));
    }

    public static FilestampFilterList createFolderstampFilterList(int mode) {
        Collection<FilestampFilter> a = new ArrayList<FilestampFilter>();
        if ((mode & FOLDER_NAME) != 0) {
            a.add(new FileNameStampFilter());
        }
        if ((mode & FOLDER_SIZE) != 0) {
            a.add(new FolderSizeStampFilter());
        }
        if ((mode & FOLDER_STAMP) != 0) {
            a.add(new FolderContentStampFilter());
        }
        if ((mode & FOLDER_TIME_MASK) == FOLDER_TIME) {
            a.add(new FileTimeStampFilter());
        }else if ((mode & FOLDER_TIME_MASK) == FOLDER_TIME_HOUR) {
            a.add(new FileTimeStampHourFilter());
        }else if ((mode & FOLDER_TIME_MASK) == FOLDER_TIME_DAY) {
            a.add(new FileTimeStampDayFilter());
        }else if ((mode & FOLDER_TIME_MASK) == FOLDER_TIME_WEEK) {
            a.add(new FileTimeStampWeekFilter());
        }else if ((mode & FOLDER_TIME_MASK) == FOLDER_TIME_MONTH) {
            a.add(new FileTimeStampMonthFilter());
        }else if ((mode & FOLDER_TIME_MASK) == FOLDER_TIME_YEAR) {
            a.add(new FileTimeStampYearFilter());
        }
        if (a.size() == 0) {
            return null;
        }
        return new FilestampFilterList(a.toArray(new FilestampFilter[a.size()]));
    }

    public static FileContentComparator createFileContentComparator(int mode) {
        if ((mode & FILE_CONTENT_MASK) == FILE_CONTENT) {
            return new FileByteContentComparator();
        } else if ((mode & FILE_CONTENT_MASK) == FILE_CONTENT_I) {
            return new FileInsensitiveCharContentComparator();
        } else if ((mode & FILE_CONTENT_MASK) == FILE_CONTENT_W) {
            return new FileIgnoreWhitesContentComparator();
        } else if ((mode & FILE_CONTENT_MASK) == FILE_CONTENT_WI) {
            return new FileIgnoreWhitesInsensitiveContentComparator();
        } else {
            return null;
        }
    }

    public static FileContentComparator createFolderContentComparator(int mode) {
        if ((mode & FOLDER_CONTENT) != 0) {
            //todo should be file insensitive
            return new FolderContentComparator();
        } else {
            return null;
        }
    }

//    public static void main(String[] args) {
//        System.out.println("Utils.isSet(FILE_TIME|FILE_TIME_MASK) = " + Utils.isSet(FILE_TIME & FILE_TIME_MASK));
//    }
}
