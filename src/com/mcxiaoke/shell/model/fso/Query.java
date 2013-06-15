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

import android.text.TextUtils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * A class that restrict the number of queries that can
 * be made to the application search system.
 */
public class Query implements Serializable {

    private static final long serialVersionUID = 3485374541081012723L;

    //IMP! This need to be sync which the command_list.xml resource
    //to have the same slots as the filled for the find command
    private static final int SLOTS_COUNT = 5;

    private final String[] mQUERIES = new String[SLOTS_COUNT];

    /**
     * Constructor of <code>Query</code>.
     */
    public Query() {
        super();
    }

    /**
     * Method that returns the value of an slot.
     *
     * @param slot The slot number
     * @return String The text of the query at the slot
     */
    public String getSlot(int slot) {
        return this.mQUERIES[slot];
    }

    /**
     * Method that sets the value of an slot.
     *
     * @param query The text of the query at the slot
     * @param slot The slot number
     * @return Query The query reference
     */
    public Query setSlot(String query, int slot) {
        this.mQUERIES[slot] = query;
        return this;
    }

    /**
     * Method that return the number of slots.
     *
     * @return int The number of slots
     */
    public int getSlotsCount() {
        return this.mQUERIES.length;
    }

    /**
     * Method that fill all the available slots (filled from the minimum
     * to the maximum slot).
     *
     * @param queries The queries which fill the slots
     * @return Query The query reference
     */
    public Query fillSlots(List<String> queries) {
        int cc = queries.size();
        for (int i = 0; i < cc; i++) {
            if (i > this.mQUERIES.length) {
                break;
            }
            this.mQUERIES[i] = queries.get(i);
        }
        return this;
    }

    /**
     * Method that returns the list of queries.
     *
     * @return List<String> The list of queries
     */
    public List<String> getQueries() {
        List<String> queries = new ArrayList<String>(getSlotsCount());
        int cc = this.mQUERIES.length;
        for (int i = 0; i < cc; i++) {
            if (this.mQUERIES[i] != null && this.mQUERIES[i].length() > 0) {
                queries.add(this.mQUERIES[i]);
            }
        }
        return queries;
    }

    /**
     * Method that returns the terms of the query in a single string separated by ", " string.
     *
     * @return String The terms of the query
     */
    public String getTerms() {
        String terms = TextUtils.join(", ", getQueries().toArray(new String[]{})); //$NON-NLS-1$;
        if (terms.endsWith(", ")) { //$NON-NLS-1$;
            terms = ""; //$NON-NLS-1$;
        }
        return terms;
    }
}
