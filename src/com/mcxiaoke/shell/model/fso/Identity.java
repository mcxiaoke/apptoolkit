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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * A class that held the identitys information of an system identity.<br/>
 * <br/>
 * This class contains the sid user, his sid group, and additional sid groups
 * that belongs to the identity.
 */
public class Identity implements Serializable {

    private static final long serialVersionUID = 6274016010810325416L;

    private User mUser;
    private Group mGroup;
    private List<Group> mGroups;

    /**
     * Constructor of <code>Identity</code>.
     *
     * @param user The user associated to the identity
     * @param group The group associated to the identity (the primary group)
     * @param groups Additional groups associated to the identity
     */
    public Identity(User user, Group group, List<Group> groups) {
        super();
        this.mUser = user;
        this.mGroup = group;
        this.mGroups = groups;
    }

    /**
     * Method that returns the user associated to the identity.
     *
     * @return User The user associated to the identity
     */
    public User getUser() {
        return this.mUser;
    }

    /**
     * Method that sets the user associated to the identity.
     *
     * @param user The user associated to the identity
     */
    public void setUser(User user) {
        this.mUser = user;
    }

    /**
     * Method that returns the group associated to the identity (the primary group).
     *
     * @return Group The group associated to the identity (the primary group)
     */
    public Group getGroup() {
        return this.mGroup;
    }

    /**
     * Method that sets the group associated to the identity (the primary group).
     *
     * @param group The group associated to the identity (the primary group)
     */
    public void setGroup(Group group) {
        this.mGroup = group;
    }

    /**
     * Method that returns additional groups associated to the identity.
     *
     * @return List<Group> Additional groups associated to the identity
     */
    public List<Group> getGroups() {
        return new ArrayList<Group>(this.mGroups);
    }

    /**
     * Method that sets additional groups associated to the identity.
     *
     * @param groups Additional groups associated to the identity
     */
    public void setGroups(List<Group> groups) {
        this.mGroups = groups;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((this.mGroup == null) ? 0 : this.mGroup.hashCode());
        result = prime * result + ((this.mGroups == null) ? 0 : this.mGroups.hashCode());
        result = prime * result + ((this.mUser == null) ? 0 : this.mUser.hashCode());
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
        Identity other = (Identity) obj;
        if (this.mGroup == null) {
            if (other.mGroup != null) {
                return false;
            }
        } else if (!this.mGroup.equals(other.mGroup)) {
            return false;
        }
        if (this.mGroups == null) {
            if (other.mGroups != null) {
                return false;
            }
        } else if (!this.mGroups.equals(other.mGroups)) {
            return false;
        }
        if (this.mUser == null) {
            if (other.mUser != null) {
                return false;
            }
        } else if (!this.mUser.equals(other.mUser)) {
            return false;
        }
        return true;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return "Identity [user=" + this.mUser + ", group=" //$NON-NLS-1$//$NON-NLS-2$
                + this.mGroup + ", groups=" +  //$NON-NLS-1$
                Arrays.toString(
                        this.mGroups.toArray(new Group[this.mGroups.size()])) + "]"; //$NON-NLS-1$
    }



}
