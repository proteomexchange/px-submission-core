package uk.ac.ebi.pride.data.util;

import java.io.*;
import java.util.zip.GZIPInputStream;
import java.util.zip.ZipInputStream;

/**
 * Different mass spec file format, also include support methods for detecting the format
 *
 * @author Rui Wang
 * @version $Id$
 */
public enum MassSpecFileFormat {
    MGF("mgf", true, MassSpecFileType.PEAK, false),
    MZML("mzml", true, MassSpecFileType.RAW, false),
    MZIDENTML("mzid", true, MassSpecFileType.SEARCH, false),    // this is temporarily set to search type
    PRIDE("xml", true, MassSpecFileType.RESULT, true),
    DAT("dat", true, MassSpecFileType.SEARCH, false),
    XTANDEM("xml", true, MassSpecFileType.SEARCH, false),
    PKL("pkl", true, MassSpecFileType.PEAK, false),
    PKL_SPO("spo", true, MassSpecFileType.SEARCH, false),
    SEQUEST_DTA("dta", true, MassSpecFileType.PEAK, false),
    SEQUEST_OUT("out", true, MassSpecFileType.SEARCH, false),
    OMSSA_OMX("omx", true, MassSpecFileType.SEARCH, false),
    MSGF("msgf", true, MassSpecFileType.SEARCH, false),
    SPECTRAST("xls", true, MassSpecFileType.SEARCH, false),
    CRUX("txt", true, MassSpecFileType.SEARCH, false),
    PEPTIDE_PROPHET("pepxml", true, MassSpecFileType.SEARCH, false),
    PROTEIN_PROPHET("protxml", true, MassSpecFileType.SEARCH, false),
    VEMS_PKX("pkx", true, MassSpecFileType.RAW, false),
    MS2("ms2", true, MassSpecFileType.PEAK, false),
    MZDATA("mzdata", true, MassSpecFileType.RAW, false),
    MZXML("mzxml", true, MassSpecFileType.RAW, false),
    BRUKER_BAF("baf", true, MassSpecFileType.RAW, false),
    BRUKER_FID("fid", true, MassSpecFileType.RAW, false),
    BRUKER_YEP("yep", true, MassSpecFileType.RAW, false),
    ABI_WIFF("wiff", true, MassSpecFileType.RAW, false),
    RAW("raw", true, MassSpecFileType.RAW, false),
    AGILENT_MASSHUNTER_RAW("d", false, MassSpecFileType.RAW, false);


    private String fileExtension;
    private boolean fileFormat;
    private MassSpecFileType fileType;
    private boolean supported;

    private MassSpecFileFormat(String fileExtension, boolean fileFormat, MassSpecFileType fileType, boolean supported) {
        this.fileExtension = fileExtension;
        this.fileFormat = fileFormat;
        this.fileType = fileType;
        this.supported = supported;
    }

    public String getFileExtension() {
        return fileExtension;
    }

    public boolean isFileFormat() {
        return fileFormat;
    }

    public MassSpecFileType getFileType() {
        return fileType;
    }

    public boolean isSupported() {
        return supported;
    }

    /**
     * Check whether a folder is a recognized mass spec data folder
     *
     * @param folder input folder
     * @return boolean true means mass spec data folder
     */
    public static boolean isMassSpecDataFolder(File folder) {
        boolean msFolder = false;

        if (folder.isDirectory()) {
            MassSpecFileFormat format = checkFormat(folder);
            if (format != null) {
                msFolder = true;
            }
        }

        return msFolder;
    }

    /**
     * Get mass spec file type
     *
     * @param file given file
     * @return MassSpecFileType    mass spec file type
     */
    public static MassSpecFileType getType(File file) {
        MassSpecFileFormat format = checkFormat(file);

        return format == null ? MassSpecFileType.OTHER : format.getFileType();
    }

    /**
     * Detect mass spec file format
     *
     * @param file input file
     * @return MassSpecFileFormat  mass spec file format
     */
    public static MassSpecFileFormat checkFormat(File file) {
        MassSpecFileFormat format = null;

        boolean isFile = file.isFile();

        String ext = FileUtil.getExtension(file);

        if (ext != null) {
            if ("xml".equals(ext.toLowerCase())) {
                // read file content to detect the type
                format = checkXmlFormat(file);
            } else if ("zip".equals(ext.toLowerCase())) {
                format = checkZippedFileFormat(file);
            } else if ("gz".equals(ext.toLowerCase())) {
                format = checkGzippedFileFormat(file);
            } else if ("txt".equals(ext.toLowerCase())) {
                format = null;
            } else if ("xls".equals(ext.toLowerCase())) {
                format = null;
            } else {
                format = checkFormat(ext, isFile);
            }
        }

        return format;
    }

    /**
     * Detect file format by detecting file extension
     *
     * @param ext    extension of a given file or folder
     * @param isFile whether input is a file
     * @return MassSpecFileFormat  mass spec file format
     */
    private static MassSpecFileFormat checkFormat(String ext, boolean isFile) {
        for (MassSpecFileFormat value : values()) {
            if (value.getFileExtension().equals(ext) && (value.isFileFormat() == isFile)) {
                return value;
            }
        }

        return null;
    }

    /**
     * Detect the file format of a xml file
     *
     * @param file xml file
     * @return MassSpecFileFormat  mass spec file format
     */
    private static MassSpecFileFormat checkXmlFormat(File file) {
        MassSpecFileFormat format = null;
        BufferedReader reader = null;

        try {
            reader = new BufferedReader(new FileReader(file));
            // read first ten lines
            StringBuilder content = new StringBuilder();
            for (int i = 0; i < 10; i++) {
                content.append(reader.readLine());
            }
            // detect format
            format = detectFormat(content.toString());
        } catch (IOException ioe) {
            //do nothing here
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    // do nothing there
                }
            }
        }

        return format;
    }

    /**
     * Check the file format of a zip file
     *
     * @param file zipped file
     * @return mass spec file format
     */
    private static MassSpecFileFormat checkZippedFileFormat(File file) {
        MassSpecFileFormat format = null;

        ZipInputStream zipInputStream = null;
        try {
            FileInputStream fileInputStream = new FileInputStream(file);
            zipInputStream = new ZipInputStream(new BufferedInputStream(fileInputStream));

            if (zipInputStream.getNextEntry() != null) {
                // reading buffer size
                int BUFFER = 1048;
                byte data[] = new byte[BUFFER];

                zipInputStream.read(data, 0, BUFFER);

                // convert byte array to string
                String content = new String(data);
                format = detectFormat(content);
            }
        } catch (FileNotFoundException e) {
            // do nothing here
        } catch (IOException e) {
            // do nothing here
        } finally {
            try {
                if (zipInputStream != null) {
                    zipInputStream.close();
                }
            } catch (IOException e) {
                // do nothing here
            }
        }

        return format;
    }


    /**
     * Check the file format of a gzipped file
     *
     * @param file gzipped file
     * @return mass spec file format
     */
    private static MassSpecFileFormat checkGzippedFileFormat(File file) {
        MassSpecFileFormat format = null;

        GZIPInputStream gzipInputStream = null;

        try {
            FileInputStream fileInputStream = new FileInputStream(file);
            gzipInputStream = new GZIPInputStream(new BufferedInputStream(fileInputStream));

            // reading buffer size
            int BUFFER = 1048;
            byte data[] = new byte[BUFFER];

            gzipInputStream.read(data, 0, BUFFER);

            // convert byte array to string
            String content = new String(data);
            format = detectFormat(content);

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (gzipInputStream != null) {
                    gzipInputStream.close();
                }
            } catch (IOException e) {
                // do nothing here
            }
        }

        return format;
    }

    /**
     * Detect the format based on a given string
     *
     * @param content input content
     * @return mass spec file format
     */
    private static MassSpecFileFormat detectFormat(String content) {
        MassSpecFileFormat format = null;

        if (MassSpecFileRegx.PRIDE_XML_PATTERN.matcher(content).find()) {
            format = PRIDE;
        } else if (MassSpecFileRegx.MZML_PATTERN.matcher(content).find()) {
            format = MZML;
        } else if (MassSpecFileRegx.MZIDENTML_PATTERN.matcher(content).find()) {
            format = MZIDENTML;
        } else if (MassSpecFileRegx.MZXML_PATTERN.matcher(content).find()) {
            format = MZXML;
        } else if (MassSpecFileRegx.MZDATA_PATTERN.matcher(content).find()) {
            format = MZDATA;
        }

        return format;
    }
}
