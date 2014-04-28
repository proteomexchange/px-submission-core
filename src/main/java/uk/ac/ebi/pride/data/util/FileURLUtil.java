package uk.ac.ebi.pride.data.util;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * @author Rui Wang
 * @version $Id$
 */
public class FileURLUtil {

    public static String getFileName(URL url) {
        String fileName = url.getFile();

        int lastSlashIndex = fileName.lastIndexOf("/");
        if (lastSlashIndex >= 0) {
            // Remove path to get pure filename
            fileName = fileName.substring(lastSlashIndex + 1);
        }

        return fileName;
    }

    /**
     * Get file size for file defined via URL.
     *
     * @param url URL The URL for the file to get size for.
     * @return long Size in bytes of file. Returns -1 if connection fails.
     */
    public static long getFileSize(URL url) {
        long fileSize = 0;

        HttpURLConnection conn = null;
        try {
            conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("HEAD");
            conn.getInputStream();
            fileSize = conn.getContentLength();
        } catch (IOException e) {
            fileSize = -1;
        } finally {
            if (conn != null) {
                conn.disconnect();
            }
        }

        return fileSize;
    }
}
