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
 * The base class for Android security identifiers objects.
 *
 * @see User
 * @see Group
 */
public class AID implements Serializable, Comparable<AID> {

    private static final long serialVersionUID = 5603819450513225452L;

    private final int mId;
    private final String mName;

    /**
     * Constructor of <code>AID</code>.
     *
     * @param id The identifier of the security identifier object
     * @param name The name  of the security identifier object
     */
    public AID(int id, String name) {
        super();
        this.mId = id;
        this.mName = name;
    }

    /**
     * Method that returns the identifier of the security identifier object.
     *
     * @return int The identifier of the security identifier object
     */
    public int getId() {
        return this.mId;
    }

    /**
     * Method that returns the name  of the security identifier object.
     *
     * @return String The name  of the security identifier object
     */
    public String getName() {
        return this.mName;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int compareTo(AID another) {
        if (this.mId < another.mId) {
            return -1;
        }
        if (this.mId > another.mId) {
            return 1;
        }
        if (this.mId == -1) {
            return this.mName.compareTo(another.mName);
        }
        return 0;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + this.mId;
        result = prime * result + ((this.mName == null) ? 0 : this.mName.hashCode());
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
        AID other = (AID) obj;
        if (this.mId != other.mId) {
            return false;
        }
        if (this.mName == null) {
            if (other.mName != null) {
                return false;
            }
        } else if (!this.mName.equals(other.mName)) {
            return false;
        }
        return true;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return "AID [id=" + this.mId + ", name=" //$NON-NLS-1$ //$NON-NLS-2$
               + this.mName + "]"; //$NON-NLS-1$
    }
}
