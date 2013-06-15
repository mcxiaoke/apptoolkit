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

package com.mcxiaoke.shell.utils;

import android.content.Context;
import android.util.Log;
import com.mcxiaoke.shell.model.fso.AID;
import com.mcxiaoke.shell.model.fso.BlockDevice;
import com.mcxiaoke.shell.model.fso.CharacterDevice;
import com.mcxiaoke.shell.model.fso.Directory;
import com.mcxiaoke.shell.model.fso.DomainSocket;
import com.mcxiaoke.shell.model.fso.FileSystemObject;
import com.mcxiaoke.shell.model.fso.Group;
import com.mcxiaoke.shell.model.fso.NamedPipe;
import com.mcxiaoke.shell.model.fso.Permissions;
import com.mcxiaoke.shell.model.fso.RegularFile;
import com.mcxiaoke.shell.model.fso.Symlink;
import com.mcxiaoke.shell.model.fso.SystemFile;
import com.mcxiaoke.shell.model.fso.User;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.util.Date;
import java.util.List;

/**
 * A helper class with useful methods for deal with files.
 */
public final class FileHelper {

    private static final String TAG = "FileHelper"; //$NON-NLS-1$

    /**
     * Special extension for compressed tar files
     */
    private static final String[] COMPRESSED_TAR =
            {
                    "tar.gz", "tar.bz2", "tar.lzma" //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
            };

    /**
     * The root directory.
     *
     * @hide
     */
    public static final String ROOT_DIRECTORY = "/";  //$NON-NLS-1$

    /**
     * The parent directory string.
     *
     * @hide
     */
    public static final String PARENT_DIRECTORY = "..";  //$NON-NLS-1$

    /**
     * The current directory string.
     *
     * @hide
     */
    public static final String CURRENT_DIRECTORY = ".";  //$NON-NLS-1$

    /**
     * The administrator user.
     *
     * @hide
     */
    public static final String USER_ROOT = "root";  //$NON-NLS-1$

    /**
     * The newline string.
     *
     * @hide
     */
    public static final String NEWLINE = System.getProperty("line.separator"); //$NON-NLS-1$

    // The date/time formats objects
    /**
     * @hide
     */
    public final static Object DATETIME_SYNC = new Object();
    /**
     * @hide
     */
    public static boolean sReloadDateTimeFormats = true;
    private static String sDateTimeFormatOrder = null;
    private static DateFormat sDateFormat = null;
    private static DateFormat sTimeFormat = null;

    /**
     * Constructor of <code>FileHelper</code>.
     */
    private FileHelper() {
        super();
    }

    /**
     * Method that check if a file is a symbolic link.
     *
     * @param file File to check
     * @return boolean If file is a symbolic link
     * @throws java.io.IOException If real file couldn't be checked
     */
    public static boolean isSymlink(File file) throws IOException {
        return file.getAbsolutePath().compareTo(file.getCanonicalPath()) != 0;
    }

    /**
     * Method that resolves a symbolic link to the real file or directory.
     *
     * @param file File to check
     * @return File The real file or directory
     * @throws java.io.IOException If real file couldn't be resolved
     */
    public static File resolveSymlink(File file) throws IOException {
        return file.getCanonicalFile();
    }

    /**
     * Method that returns a more human readable of the size
     * of a file system object.
     *
     * @param fso File system object
     * @return String The human readable size (void if fso don't supports size)
     */
    public static String getHumanReadableSize(FileSystemObject fso) {
        //Only if has size
        if (fso instanceof Directory) {
            return ""; //$NON-NLS-1$
        }
        if (hasSymlinkRef(fso)) {
            if (isSymlinkRefDirectory(fso)) {
                return ""; //$NON-NLS-1$
            }
            return getHumanReadableSize(((Symlink) fso).getLinkRef().getSize());
        }
        return getHumanReadableSize(fso.getSize());
    }

    /**
     * Method that returns a more human readable of a size in bytes.
     *
     * @param size The size in bytes
     * @return String The human readable size
     */
    public static String getHumanReadableSize(long size) {
        final String format = "%d %s"; //$NON-NLS-1$
        final String[] magnitude = {"B", "KB", "MB", "GB"};

        long aux = size;
        int cc = magnitude.length;
        for (int i = 0; i < cc; i++) {
            long s = aux / 1024;
            if (aux < 1024) {
                return String.format(format, Long.valueOf(aux), magnitude[i]);
            }
            aux = s;
        }
        return String.format(
                format, Long.valueOf(aux), magnitude[magnitude.length - 1]);
    }

    /**
     * Method that returns if the file system object if the root directory.
     *
     * @param fso The file system object to check
     * @return boolean if the file system object if the root directory
     */
    public static boolean isRootDirectory(FileSystemObject fso) {
        if (fso.getName() == null) return true;
        return fso.getName().compareTo(FileHelper.ROOT_DIRECTORY) == 0;
    }

    /**
     * Method that returns if the folder if the root directory.
     *
     * @param folder The folder
     * @return boolean if the folder if the root directory
     */
    public static boolean isRootDirectory(String folder) {
        if (folder == null) return true;
        return isRootDirectory(new File(folder));
    }

    /**
     * Method that returns if the folder if the root directory.
     *
     * @param folder The folder
     * @return boolean if the folder if the root directory
     */
    public static boolean isRootDirectory(File folder) {
        if (folder.getPath() == null) return true;
        return folder.getPath().compareTo(FileHelper.ROOT_DIRECTORY) == 0;
    }

    /**
     * Method that returns if the parent file system object if the root directory.
     *
     * @param fso The parent file system object to check
     * @return boolean if the parent file system object if the root directory
     */
    public static boolean isParentRootDirectory(FileSystemObject fso) {
        if (fso.getParent() == null) return true;
        return fso.getParent().compareTo(FileHelper.ROOT_DIRECTORY) == 0;
    }

    /**
     * Method that returns the name without the extension of a file system object.
     *
     * @param fso The file system object
     * @return The name without the extension of the file system object.
     */
    public static String getName(FileSystemObject fso) {
        return getName(fso.getName());
    }

    /**
     * Method that returns the name without the extension of a file system object.
     *
     * @param name The name of file system object
     * @return The name without the extension of the file system object.
     */
    public static String getName(String name) {
        String ext = getExtension(name);
        if (ext == null) return name;
        return name.substring(0, name.length() - ext.length() - 1);
    }

    /**
     * Method that returns the extension of a file system object.
     *
     * @param fso The file system object
     * @return The extension of the file system object, or <code>null</code>
     *         if <code>fso</code> has no extension.
     */
    public static String getExtension(FileSystemObject fso) {
        return getExtension(fso.getName());
    }

    /**
     * Method that returns the extension of a file system object.
     *
     * @param name The name of file system object
     * @return The extension of the file system object, or <code>null</code>
     *         if <code>fso</code> has no extension.
     */
    public static String getExtension(String name) {
        final char dot = '.';
        int pos = name.lastIndexOf(dot);
        if (pos == -1 || pos == 0) { // Hidden files doesn't have extensions
            return null;
        }

        // Exceptions to the general extraction method
        int cc = COMPRESSED_TAR.length;
        for (int i = 0; i < cc; i++) {
            if (name.endsWith("." + COMPRESSED_TAR[i])) { //$NON-NLS-1$
                return COMPRESSED_TAR[i];
            }
        }

        // General extraction method
        return name.substring(pos + 1);
    }

    /**
     * Method that returns the parent directory of a file/folder
     *
     * @param path The file/folder
     * @return String The parent directory
     */
    public static String getParentDir(String path) {
        return getParentDir(new File(path));
    }

    /**
     * Method that returns the parent directory of a file/folder
     *
     * @param path The file/folder
     * @return String The parent directory
     */
    public static String getParentDir(File path) {
        String parent = path.getParent();
        if (parent == null && path.getAbsolutePath().compareTo(FileHelper.ROOT_DIRECTORY) != 0) {
            parent = FileHelper.ROOT_DIRECTORY;
        }
        return parent;
    }

    /**
     * Method that evaluates if a path is relative.
     *
     * @param src The path to check
     * @return boolean If a path is relative
     */
    public static boolean isRelativePath(String src) {
        if (src.startsWith(CURRENT_DIRECTORY + File.separator)) {
            return true;
        }
        if (src.startsWith(PARENT_DIRECTORY + File.separator)) {
            return true;
        }
        if (src.indexOf(File.separator + CURRENT_DIRECTORY + File.separator) != -1) {
            return true;
        }
        if (src.indexOf(File.separator + PARENT_DIRECTORY + File.separator) != -1) {
            return true;
        }
        if (!src.startsWith(ROOT_DIRECTORY)) {
            return true;
        }
        return false;
    }

    /**
     * Method that check if the file system object is a {@link Symlink} and
     * has a link reference.
     *
     * @param fso The file system object to check
     * @return boolean If file system object the has a link reference
     */
    public static boolean hasSymlinkRef(FileSystemObject fso) {
        if (fso instanceof Symlink) {
            return ((Symlink) fso).getLinkRef() != null;
        }
        return false;
    }

    /**
     * Method that check if the file system object is a {@link Symlink} and
     * the link reference is a directory.
     *
     * @param fso The file system object to check
     * @return boolean If file system object the link reference is a directory
     */
    public static boolean isSymlinkRefDirectory(FileSystemObject fso) {
        if (!hasSymlinkRef(fso)) {
            return false;
        }
        return ((Symlink) fso).getLinkRef() instanceof Directory;
    }

    /**
     * Method that check if the file system object is a {@link Symlink} and
     * the link reference is a system file.
     *
     * @param fso The file system object to check
     * @return boolean If file system object the link reference is a system file
     */
    public static boolean isSymlinkRefSystemFile(FileSystemObject fso) {
        if (!hasSymlinkRef(fso)) {
            return false;
        }
        return ((Symlink) fso).getLinkRef() instanceof SystemFile;
    }

    /**
     * Method that check if the file system object is a {@link Symlink} and
     * the link reference is a block device.
     *
     * @param fso The file system object to check
     * @return boolean If file system object the link reference is a block device
     */
    public static boolean isSymlinkRefBlockDevice(FileSystemObject fso) {
        if (!hasSymlinkRef(fso)) {
            return false;
        }
        return ((Symlink) fso).getLinkRef() instanceof BlockDevice;
    }

    /**
     * Method that check if the file system object is a {@link Symlink} and
     * the link reference is a character device.
     *
     * @param fso The file system object to check
     * @return boolean If file system object the link reference is a character device
     */
    public static boolean isSymlinkRefCharacterDevice(FileSystemObject fso) {
        if (!hasSymlinkRef(fso)) {
            return false;
        }
        return ((Symlink) fso).getLinkRef() instanceof CharacterDevice;
    }

    /**
     * Method that check if the file system object is a {@link Symlink} and
     * the link reference is a named pipe.
     *
     * @param fso The file system object to check
     * @return boolean If file system object the link reference is a named pipe
     */
    public static boolean isSymlinkRefNamedPipe(FileSystemObject fso) {
        if (!hasSymlinkRef(fso)) {
            return false;
        }
        return ((Symlink) fso).getLinkRef() instanceof NamedPipe;
    }

    /**
     * Method that check if the file system object is a {@link Symlink} and
     * the link reference is a domain socket.
     *
     * @param fso The file system object to check
     * @return boolean If file system object the link reference is a domain socket
     */
    public static boolean isSymlinkRefDomainSocket(FileSystemObject fso) {
        if (!hasSymlinkRef(fso)) {
            return false;
        }
        return ((Symlink) fso).getLinkRef() instanceof DomainSocket;
    }

    /**
     * Method that checks if a file system object is a directory (real o symlink).
     *
     * @param fso The file system object to check
     * @return boolean If file system object is a directory
     */
    public static boolean isDirectory(FileSystemObject fso) {
        if (fso instanceof Directory) {
            return true;
        }
        if (isSymlinkRefDirectory(fso)) {
            return true;
        }
        return false;
    }

    /**
     * Method that checks if a file system object is a system file (real o symlink).
     *
     * @param fso The file system object to check
     * @return boolean If file system object is a system file
     */
    public static boolean isSystemFile(FileSystemObject fso) {
        if (fso instanceof SystemFile) {
            return true;
        }
        if (isSymlinkRefSystemFile(fso)) {
            return true;
        }
        return false;
    }

    /**
     * Method that returns the real reference of a file system object
     * (the reference file system object if the file system object is a symlink.
     * Otherwise the same reference).
     *
     * @param fso The file system object to check
     * @return FileSystemObject The real file system object reference
     */
    public static FileSystemObject getReference(FileSystemObject fso) {
        if (hasSymlinkRef(fso)) {
            return ((Symlink) fso).getLinkRef();
        }
        return fso;
    }

    /**
     * Method that add to the path the trailing slash
     *
     * @param path The path
     * @return String The path with the trailing slash
     */
    public static String addTrailingSlash(String path) {
        if (path == null) return null;
        return path.endsWith(File.separator) ? path : path + File.separator;
    }

    /**
     * Method that cleans the path and removes the trailing slash
     *
     * @param path The path to clean
     * @return String The path without the trailing slash
     */
    public static String removeTrailingSlash(String path) {
        if (path == null) return null;
        if (path.trim().compareTo(ROOT_DIRECTORY) == 0) return path;
        if (path.endsWith(File.separator)) {
            return path.substring(0, path.length() - 1);
        }
        return path;
    }

    /**
     * Method that creates a new name based on the name of the {@link FileSystemObject}
     * that is not current used by the filesystem.
     *
     * @param ctx           The current context
     * @param files         The list of files of the current directory
     * @param attemptedName The attempted name
     * @param regexp        The resource of the regular expression to create the new name
     * @return String The new non-existing name
     */
    public static String createNonExistingName(
            final Context ctx, final List<FileSystemObject> files,
            final String attemptedName, int regexp) {
        // Find a non-exiting name
        String newName = attemptedName;
        if (!isNameExists(files, newName)) return newName;
        do {
            String name = FileHelper.getName(newName);
            String ext = FileHelper.getExtension(newName);
            if (ext == null) {
                ext = ""; //$NON-NLS-1$
            } else {
                ext = String.format(".%s", ext); //$NON-NLS-1$
            }
            newName = ctx.getString(regexp, name, ext);
        } while (isNameExists(files, newName));
        return newName;
    }

    /**
     * Method that checks if a name exists in the current directory.
     *
     * @param files The list of files of the current directory
     * @param name  The name to check
     * @return boolean Indicate if the name exists in the current directory
     */
    public static boolean isNameExists(List<FileSystemObject> files, String name) {
        //Verify if the name exists in the current file list
        int cc = files.size();
        for (int i = 0; i < cc; i++) {
            FileSystemObject fso = files.get(i);
            if (fso.getName().compareTo(name) == 0) {
                return true;
            }
        }
        return false;
    }

    /**
     * Method that converts an absolute path to a relative path
     *
     * @param path       The absolute path to convert
     * @param relativeTo The absolute path from which make path relative to (a folder)
     * @return String The relative path
     */
    public static String toRelativePath(String path, String relativeTo) {
        // Normalize the paths
        File f1 = new File(path);
        File f2 = new File(relativeTo);
        String s1 = f1.getAbsolutePath();
        String s2 = f2.getAbsolutePath();
        if (!s2.endsWith(File.separator)) {
            s2 = s2 + File.separator;
        }

        // If s2 contains s1 then the relative is replace of the start of the path
        if (s1.startsWith(s2)) {
            return s1.substring(s2.length());
        }

        StringBuffer relative = new StringBuffer();
        do {
            File f3 = new File(s2);
            relative.append(String.format("..%s", File.separator)); //$NON-NLS-1$
            s2 = f3.getParent() + File.separator;
        } while (!s1.startsWith(s2) && !s1.startsWith(new File(s2).getAbsolutePath()));
        s2 = new File(s2).getAbsolutePath();
        return relative.toString() + s1.substring(s2.length());
    }

    /**
     * Method that creates a {@link FileSystemObject} from a {@link java.io.File}
     *
     * @param file The file or folder reference
     * @return FileSystemObject The file system object reference
     */
    public static FileSystemObject createFileSystemObject(File file) {
        try {
            // The user and group name of the files. In ChRoot, aosp give restrict access to
            // this user and group.
            final String USER = "system"; //$NON-NLS-1$
            final String GROUP = "sdcard_r"; //$NON-NLS-1$
            final String PERMISSIONS = "----rwxr-x"; //$NON-NLS-1$

            // The user and group name of the files. In ChRoot, aosp give restrict access to
            // this user and group. This applies for permission also. This has no really much
            // interest if we not allow to change the permissions
            AID userAID = AIDHelper.getAIDFromName(USER);
            AID groupAID = AIDHelper.getAIDFromName(GROUP);
            User user = new User(userAID.getId(), userAID.getName());
            Group group = new Group(groupAID.getId(), groupAID.getName());
            Permissions perm = Permissions.fromRawString(PERMISSIONS);

            // Build a directory?
            Date lastModified = new Date(file.lastModified());
            if (file.isDirectory()) {
                return
                        new Directory(
                                file.getName(),
                                file.getParent(),
                                user, group, perm,
                                lastModified, lastModified, lastModified); // The only date we have
            }

            // Build a regular file
            return
                    new RegularFile(
                            file.getName(),
                            file.getParent(),
                            user, group, perm,
                            file.length(),
                            lastModified, lastModified, lastModified); // The only date we have
        } catch (Exception e) {
            Log.e(TAG, "Exception retrieving the fso", e); //$NON-NLS-1$
        }
        return null;
    }


    /**
     * Method that copies a file
     *
     * @param src        The source file
     * @param dst        The destination file
     * @param bufferSize The buffer size for the operation
     * @return boolean If the operation complete successfully
     */
    public static boolean bufferedCopy(final File src, final File dst, int bufferSize) {
        BufferedInputStream bis = null;
        BufferedOutputStream bos = null;
        try {
            bis = new BufferedInputStream(new FileInputStream(src), bufferSize);
            bos = new BufferedOutputStream(new FileOutputStream(dst), bufferSize);
            int read = 0;
            byte[] data = new byte[bufferSize];
            while ((read = bis.read(data, 0, bufferSize)) != -1) {
                bos.write(data, 0, read);
            }
            return true;

        } catch (Throwable e) {
            Log.e(TAG,
                    String.format(TAG, "Failed to copy from %s to %d", src, dst), e); //$NON-NLS-1$
            return false;
        } finally {
            try {
                if (bis != null) {
                    bis.close();
                }
            } catch (Throwable e) {/**NON BLOCK**/}
            try {
                if (bos != null) {
                    bos.close();
                }
            } catch (Throwable e) {/**NON BLOCK**/}
        }
    }

    /**
     * Method that deletes a folder recursively
     *
     * @param folder The folder to delete
     * @return boolean If the folder was deleted
     */
    public static boolean deleteFolder(File folder) {
        File[] files = folder.listFiles();
        if (files != null) {
            for (int i = 0; i < files.length; i++) {
                if (files[i].isDirectory()) {
                    if (!deleteFolder(files[i])) {
                        return false;
                    }
                } else {
                    if (!files[i].delete()) {
                        return false;
                    }
                }
            }
        }
        return folder.delete();
    }

    /**
     * Method that returns the canonical/absolute path of the path.<br/>
     * This method performs path resolution
     *
     * @param path The path to convert
     * @return String The canonical/absolute path
     */
    public static String getAbsPath(String path) {
        try {
            return new File(path).getCanonicalPath();
        } catch (Exception e) {
            return new File(path).getAbsolutePath();
        }
    }

    /**
     * Method that returns the .nomedia file
     *
     * @param fso The folder that contains the .nomedia file
     * @return File The .nomedia file
     */
    public static File getNoMediaFile(FileSystemObject fso) {
        File file = null;
        try {
            file = new File(fso.getFullPath()).getCanonicalFile();
        } catch (Exception e) {
            file = new File(fso.getFullPath()).getAbsoluteFile();
        }
        return new File(file, ".nomedia").getAbsoluteFile(); //$NON-NLS-1$
    }
}
