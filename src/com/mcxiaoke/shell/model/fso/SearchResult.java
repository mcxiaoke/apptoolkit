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

package com.mcxiaoke.shell.model.fso;

import java.io.Serializable;

/**
 * An class that represent a result of a search.
 */
public class SearchResult implements Serializable, Comparable<SearchResult> {

    private static final long serialVersionUID = 3212483213561244650L;

    private double mRelevance;
    private FileSystemObject mFso;

    /**
     * The maximum relevance.
     */
    public static final int MAX_RELEVANCE = 10;

    /**
     * Constructor of <code>SearchResult</code>.
     *
     * @param relevance The relevance of the search
     * @param fso The file system object found
     */
    public SearchResult(double relevance, FileSystemObject fso) {
        super();
        this.mRelevance = relevance;
        this.mFso = fso;
    }

    /**
     * Method that returns the relevance of the file system object found.<br />
     * <br />
     * This relevance goes from 0 (min) to 10 (max)
     *
     * @return double The relevance of the file system object found
     */
    public double getRelevance() {
        return this.mRelevance;
    }

    /**
     * Method that sets the relevance of the file system object found.<br />
     * <br />
     * This relevance goes from 0 (min) to 10 (max)
     *
     * @param relevance The relevance of the file system object found
     */
    public void setRelevance(double relevance) {
        this.mRelevance = relevance;
    }

    /**
     * Method that returns the file system object found.
     *
     * @return FileSystemObject The file system object found
     */
    public FileSystemObject getFso() {
        return this.mFso;
    }

    /**
     * Method that sets the file system object found.
     *
     * @param fso The file system object found
     */
    public void setFso(FileSystemObject fso) {
        this.mFso = fso;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((this.mFso == null) ? 0 : this.mFso.hashCode());
        long temp;
        temp = Double.doubleToLongBits(this.mRelevance);
        result = prime * result + (int) (temp ^ (temp >>> 32));
        return result;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        SearchResult other = (SearchResult) obj;
        if (this.mFso == null) {
            if (other.mFso != null) {
                return false;
            }
        } else if (!this.mFso.equals(other.mFso)) {
            return false;
        }
        if (Double.doubleToLongBits(this.mRelevance) != Double.doubleToLongBits(other.mRelevance)) {
            return false;
        }
        return true;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int compareTo(SearchResult another) {
        if (this.mRelevance != another.mRelevance) {
            return Double.valueOf(this.mRelevance).compareTo(
                                        Double.valueOf(another.mRelevance)) * -1;
        }
        return this.mFso.compareTo(another.mFso);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return "SearchResult [relevance=" + this.mRelevance //$NON-NLS-1$
                + ", fso=" + this.mFso + "]";  //$NON-NLS-1$//$NON-NLS-2$
    }

}
