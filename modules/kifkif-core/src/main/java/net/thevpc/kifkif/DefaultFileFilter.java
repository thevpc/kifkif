package net.thevpc.kifkif;

import java.io.File;
import java.io.FileFilter;
import java.io.Serializable;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.regex.Pattern;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * @author vpc
 *         Date: 12 janv. 2005
 *         Time: 20:04:48
 */
public class DefaultFileFilter implements FileFilter, Serializable {
    private File root;
    private ArrayList<File> excludedFolders = new ArrayList<File>();
    transient private Pattern filePattern;
    transient private Pattern folderPattern;
    private String userFilePattern;
    private String userFolderPattern;
    private boolean simpleRegexp;
    private boolean caseInsensitive;
    private boolean includeHidden;
    private boolean userFilePatternIsNegated;
    private boolean userFolderPatternIsNegated;

    private long minFileSize=-1;
    private long maxFileSize=-1;

    private long minFileLastModifiedTime=-1;
    private long maxFileLastModifiedTime=-1;
    private int duringLastCount=-1;
    private int duringLastCalendarType=-1;

    public DefaultFileFilter() {
        rebuild();
    }

    /**
     * if regexp starts with 'userFilePattern:' it a regexp else its inshell Like
     *
     * @param root
     * @param userFilePattern
     * @param userFolderPattern
     * @param simpleRegexp      : is set than expression is likely to be understood as DOS like expression (use of * and ?)
     */
    public DefaultFileFilter(File root, String userFilePattern, String userFolderPattern, boolean simpleRegexp, boolean caseSensitive, boolean includeHidden, boolean userFilePatternIsNegated, boolean userFolderPatternIsNegated) {
        this.root = root;
        this.userFilePattern = userFilePattern;
        this.userFolderPattern = userFolderPattern;
        this.includeHidden = includeHidden;
        this.caseInsensitive = caseSensitive;
        this.simpleRegexp = simpleRegexp;
        this.userFilePatternIsNegated = userFilePatternIsNegated;
        this.userFolderPatternIsNegated = userFolderPatternIsNegated;
        rebuild();
    }

    public boolean accept(File pathname) {
        if ((root == null) || pathname.getPath().replace("\\", "/").startsWith(root.getPath().replace("\\", "/") + "/"))
        {
            if (includeHidden || !pathname.isHidden()) {
                if (pathname.isFile()) {
                    long len = pathname.length();
                    long time = pathname.lastModified();
                    if (minFileSize > 0) {
                        if (len < minFileSize) {
                            return false;
                        }
                    }
                    if (maxFileSize >= 0) {
                        if (len > maxFileSize) {
                            return false;
                        }
                    }
                    if (minFileLastModifiedTime > 0) {
                        if (time < minFileLastModifiedTime) {
                            return false;
                        }
                    }
                    if (maxFileLastModifiedTime > 0) {
                        if (time > maxFileLastModifiedTime) {
                            return false;
                        }
                    }
                    if (filePattern != null) {
                        boolean value = filePattern.matcher(pathname.getName()).matches();
                        return userFilePatternIsNegated != value;
                    }
                    return true;
                }
                if (pathname.isDirectory()) {
                    if (folderPattern == null) {
                        return true;
                    }
                    boolean value = folderPattern.matcher(pathname.getName()).matches();
                    return userFolderPatternIsNegated != value;
                }
            }
        }
        return false;
    }

//    public String toString() {
//        return userFilePattern == null ? "<?>" : userFilePattern;
//    }

    public boolean equals(Object obj) {
        //TODO some fields are not verified
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        DefaultFileFilter f = (DefaultFileFilter) obj;
        return
                (this.root == f.root || (this.root != null && this.root.equals(f.root)))
                        &&
                        (this.userFilePattern == f.userFilePattern || (this.userFilePattern != null && this.userFilePattern.equals(f.userFilePattern)))
                ;
    }

    public int hashCode() {
        int h = 0;
        if (root != null) {
            h = 31 * h + root.hashCode();
        }
        if (excludedFolders != null) {
            h = 31 * h + excludedFolders.hashCode();
        }
        if (userFilePattern != null) {
            h = 31 * h + userFilePattern.hashCode();
        }
        if (userFolderPattern != null) {
            h = 31 * h + userFolderPattern.hashCode();
        }
        h = 31 * h + (simpleRegexp ? 1 : 0);
        h = 31 * h + (caseInsensitive ? 1 : 0);
        h = 31 * h + (includeHidden ? 1 : 0);
        h = 31 * h + (userFilePatternIsNegated ? 1 : 0);
        return h;
    }

    public String getUserFilePattern() {
        return userFilePattern;
    }

    private String toRegexp(String dosLike) {

        StringBuilder sb = new StringBuilder();
        int len = dosLike.length();
        for (int i = 0; i < len; i++) {
            char c = dosLike.charAt(i);
            switch (c) {
                case '$' :
                case '\\' :
                case '^' :
                case '!' :
                case '+' :
                case '[' :
                case ']' :
                case '{' :
                case '}' :
                case '.' : {
                    sb.append("\\");
                    sb.append(c);
                    break;
                }
                case '*' : {
                    sb.append(".*");
                    break;
                }
                case '?' : {
                    sb.append(".");
                    break;
                }
                case ';' : {
                    sb.append("|");
                    break;
                }
                default : {
                    sb.append(c);
                }
            }
        }
        return sb.toString();
    }

    public void rebuild() {
        int mode = caseInsensitive ? Pattern.CASE_INSENSITIVE : 0;
        if (simpleRegexp) {
            this.filePattern = userFilePattern == null ? null : Pattern.compile(toRegexp(userFilePattern), mode);
            this.folderPattern = userFolderPattern == null ? null : Pattern.compile(toRegexp(userFolderPattern), mode);
        } else {
            this.filePattern = userFilePattern == null ? null : Pattern.compile(userFilePattern, mode);
            this.folderPattern = userFolderPattern == null ? null : Pattern.compile(userFolderPattern, mode);
        }
        if (duringLastCalendarType >= 0) {
            Calendar c = Calendar.getInstance();
            c.setTime(new Date());
            c.add(duringLastCalendarType, -duringLastCount);
            this.minFileLastModifiedTime = c.getTimeInMillis();
        }

    }

    public boolean isSimpleRegexp() {
        return simpleRegexp;
    }

    public void setSimpleRegexp(boolean simpleRegexp) {
        this.simpleRegexp = simpleRegexp;
        rebuild();
    }

    public boolean isCaseInsensitive() {
        return caseInsensitive;
    }

    public void setCaseInsensitive(boolean caseInsensitive) {
        this.caseInsensitive = caseInsensitive;
        rebuild();
    }

    public String getUserFolderPattern() {
        return userFolderPattern;
    }

    public void setUserFolderPattern(String userFolderPattern) {
        this.userFolderPattern = userFolderPattern;
        rebuild();
    }

    public void setUserFilePattern(String userFilePattern) {
        this.userFilePattern = userFilePattern;
        rebuild();
    }

    public ArrayList<File> getExcludedFolders() {
        return excludedFolders;
    }

    public void setExcludedFolders(ArrayList<File> excludedFolders) {
        this.excludedFolders = excludedFolders;
    }

    public boolean isUserFilePatternIsNegated() {
        return userFilePatternIsNegated;
    }

    public void setUserFilePatternIsNegated(boolean userFilePatternIsNegated) {
        this.userFilePatternIsNegated = userFilePatternIsNegated;
    }

    public boolean isUserFolderPatternIsNegated() {
        return userFolderPatternIsNegated;
    }

    public void setUserFolderPatternIsNegated(boolean userFolderPatternIsNegated) {
        this.userFolderPatternIsNegated = userFolderPatternIsNegated;
    }

    public File getRoot() {
        return root;
    }

    public void setRoot(File root) {
        this.root = root;
    }

    public long getMinFileSize() {
        return minFileSize;
    }

    public void setMinFileSize(long minFileSize) {
        this.minFileSize = minFileSize;
    }

    public long getMaxFileSize() {
        return maxFileSize;
    }

    public void setMaxFileSize(long maxFileSize) {
        this.maxFileSize = maxFileSize;
    }

    public long getMinFileLastModifiedTime() {
        return minFileLastModifiedTime;
    }

    public void setMinFileLastModifiedTime(long minFileLastModifiedTime) {
        this.minFileLastModifiedTime = minFileLastModifiedTime;
        duringLastCount = 0;
        duringLastCalendarType = -1;
    }

    public long getMaxFileLastModifiedTime() {
        return maxFileLastModifiedTime;
    }

    public void setMaxFileLastModifiedTime(long maxFileLastModifiedTime) {
        this.maxFileLastModifiedTime = maxFileLastModifiedTime;
        duringLastCount = 0;
        duringLastCalendarType = -1;
    }

    public void setMinFileLastModifiedTimeDuringLast(int count, int calendarType) {
        duringLastCount = count;
        duringLastCalendarType = calendarType;
    }

    public boolean isIncludeHidden() {
        return includeHidden;
    }

    public void setIncludeHidden(boolean includeHidden) {
        this.includeHidden = includeHidden;
    }

    public int getDuringLastCount() {
        return duringLastCount;
    }

    public int getDuringLastCalendarType() {
        return duringLastCalendarType;
    }
}
