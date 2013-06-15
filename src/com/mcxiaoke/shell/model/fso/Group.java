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

/**
 * A class that represents a group of the operating system.
 */
public class Group extends AID {

    private static final long serialVersionUID = -6087834824505714560L;

    /**
     * Constructor of <code>Group</code>.
     *
     * @param gid The group identifier
     * @param name The group name
     */
    public Group(int gid, String name) {
        super(gid, name);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return "Group [" + super.toString() + "]"; //$NON-NLS-1$ //$NON-NLS-2$
    }

}
