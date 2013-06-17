/* 
 * This file is part of the RootTools Project: http://code.google.com/p/roottools/
 *  
 * Copyright (c) 2012 Stephen Erickson, Chris Ravenscroft, Dominik Schuermann, Adam Shanks
 *  
 * This code is dual-licensed under the terms of the Apache License Version 2.0 and
 * the terms of the General Public License (GPL) Version 2.
 * You may use this code according to either of these licenses as is most appropriate
 * for your project on a case-by-case basis.
 * 
 * The terms of each license can be found in the root directory of this project's repository as well as at:
 * 
 * * http://www.apache.org/licenses/LICENSE-2.0
 * * http://www.gnu.org/licenses/gpl-2.0.txt
 *  
 * Unless required by applicable law or agreed to in writing, software
 * distributed under these Licenses is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See each License for the specific language governing permissions and
 * limitations under that License.
 */

package com.mcxiaoke.shell.model;

import com.mcxiaoke.shell.Shell;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.LineNumberReader;
import java.util.ArrayList;
import java.util.List;

public class Symlink {
    protected final File file;
    protected final File symlinkPath;

    public Symlink(File file, File path) {
        this.file = file;
        symlinkPath = path;
    }

    public File getFile() {
        return this.file;
    }

    public File getSymlinkPath() {
        return symlinkPath;
    }


    public static ArrayList<Symlink> getSymLinks() throws IOException {
        LineNumberReader lnr = null;
        try {
            lnr = new LineNumberReader(new FileReader(
                    "/data/local/symlinks.txt"));
            String line;
            ArrayList<Symlink> symlink = new ArrayList<Symlink>();
            while ((line = lnr.readLine()) != null) {

//                RootTools.log(line);

                String[] fields = line.split(" ");
                symlink.add(new Symlink(new File(fields[fields.length - 3]), // file
                        new File(fields[fields.length - 1]) // SymlinkPath
                ));
            }
            return symlink;
        } finally {
            // no need to do anything here.
        }
    }

    /**
     * This will return an ArrayList of the class Symlink. The class Symlink contains the following
     * property's: path SymplinkPath
     * <p/>
     * These will provide you with any Symlinks in the given path.
     *
     * @param path path to search for Symlinks.
     * @return <code>ArrayList<Symlink></code> an ArrayList of the class Symlink.
     * @throws Exception if we cannot return the Symlinks.
     */
    public ArrayList<Symlink> getSymlinks(String path) throws Exception {

        // this command needs find
        if (!Shell.hasFind()) {
            throw new Exception("find command not found");
        }

        String findCommand = "find " + path + " -type l -exec ls -l {} \\; > /data/local/symlinks.txt;";
        List<String> outputs = Shell.runAsRoot(findCommand).output;
        ArrayList<Symlink> symlinks = getSymLinks();
        return symlinks;
    }
}
