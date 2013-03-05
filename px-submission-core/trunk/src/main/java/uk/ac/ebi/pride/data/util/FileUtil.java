package uk.ac.ebi.pride.data.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Utility class for file access
 *
 * @author Rui Wang
 * @version $Id$
 */
public class FileUtil {
    private FileUtil() {
    }

    /**
     * Get the extension of a file or a folder
     *
     * @param file given file or folder
     * @return String  extension
     */
    public static String getExtension(File file) {
        if (file.isFile()) {
            return getFileExtension(file);
        } else {
            return getFolderExtension(file);
        }
    }

    /**
     * Get file extension for a given file
     *
     * @param file given file
     * @return String  file extension
     */
    public static String getFileExtension(File file) {
        String fileName = file.getName();
        int mid = fileName.lastIndexOf(Constant.DOT);
        String fileNameExt = null;
        if (mid > 0) {
            fileNameExt = fileName.substring(mid + 1, fileName.length()).toLowerCase();
        }

        return fileNameExt;
    }

    public static String getFolderExtension(File folder) {
        // todo : implement
        return null;
    }

    public static String getNameWithoutExtension(File file) {
        // todo : implement
        return null;
    }

    public static String getFileNameWithoutExtension(File file) {
        // todo : implement
        return null;
    }

    public static String getFolderName(File folder) {
        // todo : implement
        return null;
    }

    /**
     * Check whether a given file is a binary file
     *
     * @param file given file object
     * @return boolean true means binary file
     * @throws java.io.IOException exception while reading the given file
     */
    public static boolean isBinaryFile(File file) throws IOException {
        InputStream in = null;
        try {
            in = new FileInputStream(file);
            int size = file.length() > 500 ? 500 : new Long(file.length()).intValue();
            byte[] bytes = new byte[size];

            in.read(bytes, 0, bytes.length);
            short bin = 0;
            for (byte thisByte : bytes) {
                char it = (char) thisByte;
                if (!Character.isWhitespace(it) && !Character.isISOControl(it)) {
                    bin++;
                }

                if (bin >= 5) {
                    return true;
                }
            }
        } finally {
            if (in != null) {
                in.close();
            }
        }

        return false;
    }
}
