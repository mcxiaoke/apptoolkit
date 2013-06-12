package com.mcxiaoke.appmanager.compress;

import android.util.Log;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public class Decompress {
    private String zipFile;
    private String location;

    public Decompress(String zipFile, String location) {
        this.zipFile = zipFile;
        this.location = location;
    }

    public void unzip() {
        try {

            File fSourceZip = new File(zipFile);
            String zipPath = zipFile.substring(0, zipFile.length() - 4);
            //File temp = new File(zipPath);
            //temp.mkdir();
            Log.v("Decompress", zipPath + " created");

			/*
             * Extract entries while creating required sub-directories
			 */
            ZipFile zipFile = new ZipFile(fSourceZip);
            Enumeration<?> e = zipFile.entries();

            while (e.hasMoreElements()) {
                ZipEntry entry = (ZipEntry) e.nextElement();
                File destinationFilePath = new File(location, entry.getName());

                // create directories if required.
                destinationFilePath.getParentFile().mkdirs();

                // if the entry is directory, leave it. Otherwise extract it.
                if (entry.isDirectory()) {
                    continue;
                } else {
                    Log.v("Decompress", "Unzipping " + entry.getName());

					/*
					 * Get the InputStream for current entry of the zip file
					 * using
					 * 
					 * InputStream getInputStream(Entry entry) method.
					 */
                    BufferedInputStream bis = new BufferedInputStream(
                            zipFile.getInputStream(entry));

                    int b;
                    byte buffer[] = new byte[1024];

					/*
					 * read the current entry from the zip file, extract it and
					 * write the extracted file.
					 */
                    FileOutputStream fos = new FileOutputStream(destinationFilePath);
                    BufferedOutputStream bos = new BufferedOutputStream(fos,
                            1024);

                    while ((b = bis.read(buffer, 0, 1024)) != -1) {
                        bos.write(buffer, 0, b);
                    }

                    // flush the output stream and close it.
                    bos.flush();
                    bos.close();

                    // close the input stream.
                    bis.close();
                }
            }
        } catch (IOException ioe) {
            Log.e("Decompress", "unzip", ioe);
        }
    }
}
