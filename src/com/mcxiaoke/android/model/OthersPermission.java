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

/**
 * A class for represents a others permissions.
 *
 * @see Permission
 */
public class OthersPermission extends Permission {

    private static final long serialVersionUID = -8993601349099495256L;

    /**
     * @hide
     */
    public static final char STICKY_E = 't';
    /**
     * @hide
     */
    public static final char STICKY = 'T';

    private boolean mStickybit;

    /**
     * Constructor of <code>OthersPermission</code>.
     *
     * @param read If the object can be read
     * @param write If the object can be written
     * @param execute If the object can be executed
     */
    public OthersPermission(boolean read, boolean write, boolean execute) {
        super(read, write, execute);
        this.mStickybit = false;
    }

    /**
     * Constructor of <code>OthersPermission</code>.
     *
     * @param read If the object can be read
     * @param write If the object can be written
     * @param execute If the object can be executed
     * @param stickybit If the object has the sticky bit active
     */
    public OthersPermission(boolean read, boolean write, boolean execute, boolean stickybit) {
        super(read, write, execute);
        this.mStickybit = stickybit;
    }

    /**
     * Method that returns if the object has the sticky bit active.
     *
     * @return boolean If the object has the sticky bit active
     */
    public boolean isStickybit() {
        return this.mStickybit;
    }

    /**
     * Method that sets if the object has the sticky bit active.
     *
     * @param stickybit If the object has the sticky bit active
     */
    public void setStickybit(boolean stickybit) {
        this.mStickybit = stickybit;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = super.hashCode();
        result = prime * result + (this.mStickybit ? 1231 : 1237);
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
        if (!super.equals(obj)) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        OthersPermission other = (OthersPermission) obj;
        if (this.mStickybit != other.mStickybit) {
            return false;
        }
        return true;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return "OthersPermission [stickybit=" + this.mStickybit //$NON-NLS-1$
                + ", permission=" + super.toString() + "]"; //$NON-NLS-1$//$NON-NLS-2$
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toRawString() {
        StringBuilder p = new StringBuilder();
        p.append(isRead() ? READ : UNASIGNED);
        p.append(isWrite() ? WRITE : UNASIGNED);
        if (isStickybit()) {
            p.append(isExecute() ? STICKY_E : STICKY);
        } else {
            p.append(isExecute() ? EXECUTE : UNASIGNED);
        }
        return p.toString();
    }

}
