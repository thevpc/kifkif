package net.thevpc.kifkif;

import java.io.Serializable;

public class SearchStatistics implements Serializable {
    int duplicateFileSelectionCount = -1;
    int duplicateFolderSelectionCount = -1;
    int duplicateFileGroupsCount = -1;
    int duplicateFolderGroupsCount = -1;

    int sourceFilesCount = 0;
    int sourceFoldersCount = 0;
    long startTime = 0;
    long endTime = 0;

    public SearchStatistics(){
        startTime = System.currentTimeMillis();
        endTime = 0;
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

    public long getStartTime() {
        return startTime;
    }

    public void setStartTime(long startTime) {
        this.startTime = startTime;
    }

    public long getStatsElapsedTime() {
        return endTime-startTime;
    }

    public long getEndTime() {
        return endTime;
    }

    public void setEndTime(long endTime) {
        this.endTime = endTime;
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
