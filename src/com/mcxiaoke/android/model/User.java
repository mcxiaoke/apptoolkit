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
 * A class that represents a user of the operating system.
 */
public class User extends AID {

    private static final long serialVersionUID = 8250909336356908786L;

    /**
     * Constructor of <code>User</code>.
     *
     * @param uid The user identifier
     * @param name The user name
     */
    public User(int uid, String name) {
        super(uid, name);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return "User [" + super.toString() + "]"; //$NON-NLS-1$ //$NON-NLS-2$
    }

}
