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
 * A class for represents a group permissions.
 *
 * @see Permission
 */
public class GroupPermission extends Permission {

    private static final long serialVersionUID = 5261938461035756626L;

    /**
     * @hide
     */
    public static final char SETGID_E = 's';
    /**
     * @hide
     */
    public static final char SETGID = 'S';

    private boolean mSetGid;

    /**
     * Constructor of <code>GroupPermission</code>.
     *
     * @param read If the object can be read
     * @param write If the object can be written
     * @param execute If the object can be executed
     */
    public GroupPermission(boolean read, boolean write, boolean execute) {
        super(read, write, execute);
        this.mSetGid = false;
    }

    /**
     * Constructor of <code>GroupPermission</code>.
     *
     * @param read If the object can be read
     * @param write If the object can be written
     * @param execute If the object can be executed
     * @param setgid If the object has the setgid bit active
     */
    public GroupPermission(boolean read, boolean write, boolean execute, boolean setgid) {
        super(read, write, execute);
        this.mSetGid = setgid;
    }

    /**
     * Method that returns if the object has the setgid bit active.
     *
     * @return boolean If the object has the setgid bit active
     */
    public boolean isSetGID() {
        return this.mSetGid;
    }

    /**
     * Method that sets if the object has the setgid bit active.
     *
     * @param setgid If the object has the setgid bit active
     */
    public void setSetGID(boolean setgid) {
        this.mSetGid = setgid;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = super.hashCode();
        result = prime * result + (this.mSetGid ? 1231 : 1237);
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
        GroupPermission other = (GroupPermission) obj;
        if (this.mSetGid != other.mSetGid) {
            return false;
        }
        return true;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return "GroupPermission [setgid=" + this.mSetGid  //$NON-NLS-1$
                + ", permission=" + super.toString() + "]";  //$NON-NLS-1$//$NON-NLS-2$
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toRawString() {
        StringBuilder p = new StringBuilder();
        p.append(isRead() ? READ : UNASIGNED);
        p.append(isWrite() ? WRITE : UNASIGNED);
        if (isSetGID()) {
            p.append(isExecute() ? SETGID_E : SETGID);
        } else {
            p.append(isExecute() ? EXECUTE : UNASIGNED);
        }
        return p.toString();
    }

}
