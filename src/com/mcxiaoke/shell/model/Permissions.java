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

public class Permissions {
    String type;
    String user;
    String group;
    String other;
    String symlink;
    int permissions;

    public String getSymlink() {
        return this.symlink;
    }

    public String getType() {
        return type;
    }

    public int getPermissions() {
        return this.permissions;
    }

    public String getUserPermissions() {
        return this.user;
    }

    public String getGroupPermissions() {
        return this.group;
    }

    public String getOtherPermissions() {
        return this.other;
    }

    public void setSymlink(String symlink) {
        this.symlink = symlink;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setPermissions(int permissions) {
        this.permissions = permissions;
    }

    public void setUserPermissions(String user) {
        this.user = user;
    }

    public void setGroupPermissions(String group) {
        this.group = group;
    }

    public void setOtherPermissions(String other) {
        this.other = other;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getGroup() {
        return group;
    }

    public void setGroup(String group) {
        this.group = group;
    }

    public String getOther() {
        return other;
    }

    public void setOther(String other) {
        this.other = other;
    }


    public static Permissions getPermissions(String line) {

        String[] lineArray = line.split(" ");
        String rawPermissions = lineArray[0];

        if (rawPermissions.length() == 10
                && (rawPermissions.charAt(0) == '-'
                || rawPermissions.charAt(0) == 'd' || rawPermissions
                .charAt(0) == 'l')
                && (rawPermissions.charAt(1) == '-' || rawPermissions.charAt(1) == 'r')
                && (rawPermissions.charAt(2) == '-' || rawPermissions.charAt(2) == 'w')) {
//            RootTools.log(rawPermissions);

            Permissions permissions = new Permissions();

            permissions.setType(rawPermissions.substring(0, 1));

//            RootTools.log(permissions.getType());

            permissions.setUserPermissions(rawPermissions.substring(1, 4));

//            RootTools.log(permissions.getUserPermissions());

            permissions.setGroupPermissions(rawPermissions.substring(4, 7));

//            RootTools.log(permissions.getGroupPermissions());

            permissions.setOtherPermissions(rawPermissions.substring(7, 10));

//            RootTools.log(permissions.getOtherPermissions());

            StringBuilder finalPermissions = new StringBuilder();
            finalPermissions.append(parseSpecialPermissions(rawPermissions));
            finalPermissions.append(parsePermissions(permissions.getUserPermissions()));
            finalPermissions.append(parsePermissions(permissions.getGroupPermissions()));
            finalPermissions.append(parsePermissions(permissions.getOtherPermissions()));

            permissions.setPermissions(Integer.parseInt(finalPermissions.toString()));

            return permissions;
        }

        return null;
    }

    public static int parsePermissions(String permission) {
        int tmp;
        if (permission.charAt(0) == 'r')
            tmp = 4;
        else
            tmp = 0;

//        RootTools.log("permission " + tmp);
//        RootTools.log("character " + permission.charAt(0));

        if (permission.charAt(1) == 'w')
            tmp += 2;
        else
            tmp += 0;

//        RootTools.log("permission " + tmp);
//        RootTools.log("character " + permission.charAt(1));

        if (permission.charAt(2) == 'x')
            tmp += 1;
        else
            tmp += 0;

//        RootTools.log("permission " + tmp);
//        RootTools.log("character " + permission.charAt(2));

        return tmp;
    }

    public static int parseSpecialPermissions(String permission) {
        int tmp = 0;
        if (permission.charAt(2) == 's')
            tmp += 4;

        if (permission.charAt(5) == 's')
            tmp += 2;

        if (permission.charAt(8) == 't')
            tmp += 1;

//        RootTools.log("special permissions " + tmp);

        return tmp;
    }


}
