/*
 * This file is part of the RootKit Project: http://code.google.com/p/RootKit/
 * Copyright (c) 2012 Stephen Erickson, Chris Ravenscroft, Dominik Schuermann, Adam Shanks
 * This code is dual-licensed under the terms of the Apache License Version 2.0 and
 * the terms of the General Public License (GPL) Version 2.
 * You may use this code according to either of these licenses as is most appropriate
 * for your project on a case-by-case basis.
 * The terms of each license can be found in the root directory of this project's repository as well
 * as at:
 * * http://www.apache.org/licenses/LICENSE-2.0
 * * http://www.gnu.org/licenses/gpl-2.0.txt
 * Unless required by applicable law or agreed to in writing, software
 * distributed under these Licenses is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See each License for the specific language governing permissions and
 * limitations under that License.
 */

package com.mcxiaoke.shell.utils;

import com.mcxiaoke.shell.Shell;
import com.mcxiaoke.shell.model.Mount;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.LineNumberReader;
import java.util.ArrayList;
import java.util.List;

/**
 * The remounter.
 *
 * @author Chris Jiang
 */
public class Remounter {
    private static final String MOUNT_FILE = "/proc/mounts";

    public static boolean remount(String file, String mountType) {

        if (file.endsWith("/") && !file.equals("/")) {
            file = file.substring(0, file.lastIndexOf("/"));
        }

        boolean foundMount = false;

        List<Mount> mounts = getMounts();
        if (mounts == null || mounts.isEmpty()) {
            return false;
        }

        while (!foundMount) {
            for (Mount mount : mounts) {
                if (file.equals(mount.getMountPoint().toString())) {
                    foundMount = true;
                    break;
                }
            }
            if (!foundMount) {
                file = (new File(file).getParent()).toString();
            }
        }

        Mount mountPoint = getMountPoint(file);

        final boolean isMountMode = mountPoint.getFlags().contains(mountType.toLowerCase());

        if (!isMountMode) {
            String mountPath = mountPoint.getDevice().getAbsolutePath()
                    + " " + mountPoint.getMountPoint().getAbsolutePath();
            String mountCommand = "";
            if (Shell.Helper.hasMount()) {
                mountCommand = "mount -o remount,"
                        + mountType.toLowerCase() + " " + mountPath;
            } else if (Shell.Helper.hasBusyBox()) {
                mountCommand = "busybox mount -o remount," + mountType.toLowerCase()
                        + " " + mountPath;
            } else {
                mountCommand = "toolbox mount -o remount,"
                        + mountType.toLowerCase() + " " + mountPath;
            }

            try {
                List<String> output = Shell.SU.run(mountCommand);
            } catch (IOException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        mountPoint = getMountPoint(file);

        if (mountPoint.getFlags().contains(mountType.toLowerCase())) {
            return true;
        } else {
            return false;
        }
    }

    private static Mount getMountPoint(String file) {

        List<Mount> mounts = getMounts();
        if (mounts == null || mounts.isEmpty()) {
            return null;
        }

        for (File path = new File(file); path != null; ) {
            for (Mount mount : mounts) {
                if (mount.getMountPoint().equals(path)) {
                    return mount;
                }
            }
        }

        return null;
    }

    private static List<Mount> getMounts() {

        LineNumberReader lnr = null;

        try {
            lnr = new LineNumberReader(new FileReader(MOUNT_FILE));
        } catch (FileNotFoundException e1) {
            e1.printStackTrace();
            return null;
        }

        String line;
        ArrayList<Mount> mounts = new ArrayList<Mount>();
        try {
            while ((line = lnr.readLine()) != null) {
                String[] fields = line.split(" ");
                mounts.add(new Mount(new File(fields[0]), new File(fields[1]), fields[2], fields[3]));
            }
            lnr.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return mounts;
    }

    /**
     * This will tell you how the specified mount is mounted. rw, ro, etc...
     * <p/>
     *
     * @param path mount you want to check
     * @return <code>String</code> What the mount is mounted as.
     * @throws Exception if we cannot determine how the mount is mounted.
     */
    public static String getMountedAs(String path) throws Exception {
        List<Mount> mounts = getMounts();
        if (mounts != null) {
            for (Mount mount : mounts) {
                if (path.contains(mount.getMountPoint().getAbsolutePath())) {
//                    RootTools.log((String) mount.getFlags().toArray()[0]);
                    return (String) mount.getFlags().toArray()[0];
                }
            }

            throw new Exception();
        } else {
            throw new Exception();
        }
    }
}
