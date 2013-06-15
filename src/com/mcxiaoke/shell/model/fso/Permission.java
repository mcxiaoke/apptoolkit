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
 * A class for represents permissions.<br/>
 * <br/>
 * Actual permissions refers to Unix permissions:
 * <ul>
 *  <li>read</li>
 *  <li>write</li>
 *  <li>execute</li>
 * </ul>
 */
public abstract class Permission implements Serializable {

    private static final long serialVersionUID = 5775987092282897912L;

    /**
     * @hide
     */
    public static final char UNASIGNED = '-';
    /**
     * @hide
     */
    public static final char READ = 'r';
    /**
     * @hide
     */
    public static final char WRITE = 'w';
    /**
     * @hide
     */
    public static final char EXECUTE = 'x';

    private boolean mRead;
    private boolean mWrite;
    private boolean mExecute;

    /**
     * Constructor of <code>Permission</code>.
     *
     * @param read If the object can be read
     * @param write If the object can be written
     * @param execute If the object can be executed
     */
    public Permission(boolean read, boolean write, boolean execute) {
        super();
        this.mRead = read;
        this.mWrite = write;
        this.mExecute = execute;
    }

    /**
     * Method that returns if the object can be read.
     *
     * @return boolean If the object can be read
     */
    public boolean isRead() {
        return this.mRead;
    }

    /**
     * Method that sets if the object can be read.
     *
     * @param read If the object can be read
     */
    public void setRead(boolean read) {
        this.mRead = read;
    }

    /**
     * Method that returns if the object can be written.
     *
     * @return boolean If the object can be written
     */
    public boolean isWrite() {
        return this.mWrite;
    }

    /**
     * Method that sets if the object can be written.
     *
     * @param write If the object can be written
     */
    public void setWrite(boolean write) {
        this.mWrite = write;
    }

    /**
     * Method that returns if the object can be executed.
     *
     * @return boolean If the object can be executed
     */
    public boolean isExecute() {
        return this.mExecute;
    }

    /**
     * Method that sets if the object can be executed.
     *
     * @param execute If the object can be executed
     */
    public void setExecute(boolean execute) {
        this.mExecute = execute;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + (this.mExecute ? 1231 : 1237);
        result = prime * result + (this.mRead ? 1231 : 1237);
        result = prime * result + (this.mWrite ? 1231 : 1237);
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
        Permission other = (Permission) obj;
        if (this.mExecute != other.mExecute) {
            return false;
        }
        if (this.mRead != other.mRead) {
            return false;
        }
        if (this.mWrite != other.mWrite) {
            return false;
        }
        return true;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return "Permission [read=" + this.mRead + ", write=" + //$NON-NLS-1$ //$NON-NLS-2$
                 this.mWrite +   ", execute=" + this.mExecute + "]"; //$NON-NLS-1$ //$NON-NLS-2$
    }

    /**
     * Method that returns a string representation of the permissions
     * conforming with the unix style (rwx).
     *
     * @return String The string representation of the permissions
     */
    public abstract String toRawString();

}
