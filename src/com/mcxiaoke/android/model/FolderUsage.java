/*
 * Copyright (C) 2012 The CyanogenMod Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.mcxiaoke.android.model;

import android.util.SparseArray;
import com.mcxiaoke.android.util.MimeTypeHelper;

import java.io.Serializable;

/**
 * A class that holds information about the usage of a folder (space and number of files/folders).
 */
public class FolderUsage implements Serializable, Cloneable {

    private static final long serialVersionUID = -8830510087518648692L;

    private final String mFolder;
    private int mNumberOfFolders;
    private int mNumberOfFiles;
    private long mTotalSize;
    private SparseArray<Long> mStatistics;

    /**
     * Constructor of <code>FolderUsage</code>.
     *
     * @param folder The folder of which retrieve the usage
     */
    public FolderUsage(String folder) {
        super();

        // Initialize the class
        this.mFolder = folder;
        this.mNumberOfFolders = 0;
        this.mNumberOfFiles = 0;
        this.mTotalSize = 0;

        // Fill the array of statistics
        MimeTypeHelper.MimeTypeCategory[] categories = MimeTypeHelper.MimeTypeCategory.values();
        this.mStatistics = new SparseArray<Long>(categories.length - 1);
        int cc = categories.length;
        for (int i = 0; i < cc; i++) {
            this.mStatistics.put(categories[i].ordinal(), Long.valueOf(0));
        }
    }

    /**
     * Method that adds 1 folder to the total number of folders.
     */
    public void addFolder() {
        this.mNumberOfFolders++;
    }

    /**
     * Method that adds 1 file to the total number of files.
     */
    public void addFile() {
        this.mNumberOfFiles++;
    }

    /**
     * Method that adds to the total size.
     *
     * @param size The size to add to the total
     */
    public void addSize(long size) {
        this.mTotalSize += size;
    }

    /**
     * Method that add a file to the category
     *
     * @param category The category
     */
    public void addFileToCategory(MimeTypeHelper.MimeTypeCategory category) {
        long count = this.mStatistics.get(category.ordinal()).longValue();
        count++;
        this.mStatistics.put(category.ordinal(), Long.valueOf(count));
    }

    /**
     * Method that returns the folder of which retrieve the usage.
     *
     * @return String The folder of which retrieve the usage
     */
    public String getFolder() {
        return this.mFolder;
    }

    /**
     * Method that returns the total number of folders.
     *
     * @return int The total number of folders
     */
    public int getNumberOfFolders() {
        return this.mNumberOfFolders;
    }

    /**
     * Method that returns the total number of files.
     *
     * @return int The total number of files
     */
    public int getNumberOfFiles() {
        return this.mNumberOfFiles;
    }

    /**
     * Method that returns the total size.
     *
     * @return long The total size
     */
    public long getTotalSize() {
        return this.mTotalSize;
    }

    /**
     * Method sets the total size.
     *
     * @param totalSize The total size
     */
    public void setTotalSize(long totalSize) {
        this.mTotalSize = totalSize;
    }

    /**
     * Method that returns the number of files for a {@link MimeTypeCategory}.
     *
     * @param category The category
     * @return long The number of files for the category
     */
    public long getStatisticsForCategory(MimeTypeHelper.MimeTypeCategory category) {
        return this.mStatistics.get(category.ordinal()).longValue();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((this.mFolder == null) ? 0 : this.mFolder.hashCode());
        result = prime * result + this.mNumberOfFiles;
        result = prime * result + this.mNumberOfFolders;
        result = prime * result
                + ((this.mStatistics == null) ? 0 : this.mStatistics.hashCode());
        result = prime * result + (int) (this.mTotalSize ^ (this.mTotalSize >>> 32));
        return result;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        FolderUsage other = (FolderUsage) obj;
        if (this.mFolder == null) {
            if (other.mFolder != null)
                return false;
        } else if (!this.mFolder.equals(other.mFolder))
            return false;
        if (this.mNumberOfFiles != other.mNumberOfFiles)
            return false;
        if (this.mNumberOfFolders != other.mNumberOfFolders)
            return false;
        if (this.mStatistics == null) {
            if (other.mStatistics != null)
                return false;
        } else if (!this.mStatistics.equals(other.mStatistics))
            return false;
        if (this.mTotalSize != other.mTotalSize)
            return false;
        return true;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Object clone() throws CloneNotSupportedException {
        FolderUsage other = new FolderUsage(this.mFolder);
        other.mNumberOfFolders = this.mNumberOfFolders;
        other.mNumberOfFiles = this.mNumberOfFiles;
        other.mTotalSize = this.mTotalSize;
        other.mStatistics = this.mStatistics.clone();
        return super.clone();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return "FolderUsage [folder=" + this.mFolder + //$NON-NLS-1$
                ", numberOfFolders=" + this.mNumberOfFolders + //$NON-NLS-1$
                ", numberOfFiles=" + this.mNumberOfFiles + //$NON-NLS-1$
                ", totalSize=" + this.mTotalSize + //$NON-NLS-1$
                ", statistics=" + this.mStatistics + "]"; //$NON-NLS-1$//$NON-NLS-2$
    }
}
