package net.thevpc.kifkif;

import java.io.Serializable;

public class SearchStatistics implements Serializable {
    int duplicateFileSelectionCount = -1;
    int duplicateFolderSelectionCount = -1;
    int duplicateFileGroupsCount = -1;
    int duplicateFolderGroupsCount = -1;

    int sourceFilesCount = 0;
    int sourceFoldersCount = 0;
    long startTimeMillis = 0;
    long endTimeMillis = 0;

    public SearchStatistics(){
        startTimeMillis = System.currentTimeMillis();
        endTimeMillis = 0;
    }

    public int getSourceFilesCount() {
        return sourceFilesCount;
    }

    public void setSourceFilesCount(int sourceFilesCount) {
        this.sourceFilesCount = sourceFilesCount;
    }

    public int getSourceFoldersCount() {
        return sourceFoldersCount;
    }

    public void setSourceFoldersCount(int sourceFoldersCount) {
        this.sourceFoldersCount = sourceFoldersCount;
    }

    public long getStartTimeMillis() {
        return startTimeMillis;
    }

    public void setStartTimeMillis(long startTimeMillis) {
        this.startTimeMillis = startTimeMillis;
    }

    public long getStatsElapsedTimeMillis() {
        return endTimeMillis - startTimeMillis;
    }

    public long getEndTimeMillis() {
        return endTimeMillis;
    }

    public void setEndTimeMillis(long endTimeMillis) {
        this.endTimeMillis = endTimeMillis;
    }

    public int getDuplicateFileSelectionCount() {
        return duplicateFileSelectionCount;
    }

    public void setDuplicateFileSelectionCount(int duplicateFileSelectionCount) {
        this.duplicateFileSelectionCount = duplicateFileSelectionCount;
    }

    public int getDuplicateFolderSelectionCount() {
        return duplicateFolderSelectionCount;
    }

    public void setDuplicateFolderSelectionCount(int duplicateFolderSelectionCount) {
        this.duplicateFolderSelectionCount = duplicateFolderSelectionCount;
    }

    public int getDuplicateFileGroupsCount() {
        return duplicateFileGroupsCount;
    }

    public void setDuplicateFileGroupsCount(int duplicateFileGroupsCount) {
        this.duplicateFileGroupsCount = duplicateFileGroupsCount;
    }

    public int getDuplicateFolderGroupsCount() {
        return duplicateFolderGroupsCount;
    }

    public void setDuplicateFolderGroupsCount(int duplicateFolderGroupsCount) {
        this.duplicateFolderGroupsCount = duplicateFolderGroupsCount;
    }
}
