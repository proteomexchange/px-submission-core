package uk.ac.ebi.pride.data.io;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.ac.ebi.pride.data.exception.SubmissionFileException;
import uk.ac.ebi.pride.data.model.*;
import uk.ac.ebi.pride.data.util.Constant;
import uk.ac.ebi.pride.data.util.SubmissionType;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

/**
 * Writer for writing a submission file
 *
 * @author Rui Wang
 * @version $Id$
 */
public class SubmissionFileWriter {
    private static final Logger logger = LoggerFactory.getLogger(SubmissionFileWriter.class);

    private SubmissionFileWriter() {
    }

    /**
     * Write a submission object to a given output file
     *
     * @param submission submission object
     * @param file       output file
     * @throws uk.ac.ebi.pride.data.exception.SubmissionFileException
     *          exception while writing a submission output file
     */
    public static void write(Submission submission, File file) throws SubmissionFileException {
        PrintWriter writer = null;
        try {
            // create writer
            writer = new PrintWriter(file, "UTF-8");

            // write contact
            writeContact(writer, submission.getContact());

            // write meta data
            writeMetaData(writer, submission.getMetaData());
            writer.println();

            // write file mappings
            writeFileMappings(writer, submission.getDataFiles());
            writer.flush();

        } catch (IOException e) {
            String msg = "Error while writing submission file: " + file.getAbsolutePath();
            logger.error(msg, e);
            throw new SubmissionFileException(msg, e);
        } finally {
            if (writer != null) {
                writer.close();
            }
        }
    }


    /**
     * Write contact info to output
     */
    private static void writeContact(PrintWriter writer, Contact contact) {
        writeMetaData(writer, Constant.NAME, contact.getName());
        writeMetaData(writer, Constant.EMAIL, contact.getEmail());
        writeMetaData(writer, Constant.AFFILIATION, contact.getAffiliation());
        writeMetaData(writer, Constant.PRIDE_LOGIN, contact.getUserName());
    }

    /**
     * Write metadata info to output
     */
    private static void writeMetaData(PrintWriter writer, MetaData metaData) {
        // title
        writeMetaData(writer, Constant.EXPERIMENT_TITLE, metaData.getTitle());
        // description
        writeMetaData(writer, Constant.EXPERIMENT_DESC, metaData.getDescription());
        // keywords
        writeMetaData(writer, Constant.KEYWORDS, metaData.getKeywords());
        // supported or unsupported
        writeMetaData(writer, Constant.SUBMISSION_TYPE, metaData.getSubmissionType().toString());

        if (!SubmissionType.SUPPORTED.equals(metaData.getSubmissionType())) {
            // comment
            writeMetaData(writer, Constant.COMMENT, metaData.getComment());
            // species
            writeMetaData(writer, Constant.SPECIES, metaData.getSpecies(), false);
            // instrument
            List<List<CvParam>> instruments = metaData.getInstruments();
            for (List<CvParam> instrument : instruments) {
                writeMetaData(writer, Constant.INSTRUMENT, instrument, true);
            }
            // modification
            writeMetaData(writer, Constant.MODIFICATION, metaData.getModifications(), false);

            // additional
            if (metaData.hasAdditional()) {
                writeMetaData(writer, Constant.ADDITIONAL, metaData.getAdditional(), false);
            }
        }

        // pubmed ids
        if (metaData.hasPubmedIds()) {
            writeMetaData(writer, Constant.PUBMED_ID, metaData.getPubmedIds(), false);
        }

        // resubmission
        if (metaData.isResubmission()) {
            writeMetaData(writer, Constant.RESUBMISSION_PX_ACCESSION, metaData.getResubmissionPxAccession());
        }

        // reanalysis
        if (metaData.hasReanalysisPxAccessions()) {
            writeMetaData(writer, Constant.REANALYSIS_PX_ACCESSION, metaData.getReanalysisAccessions(), false);
        }
    }

    /**
     * Write file mappings to output
     */
    private static void writeFileMappings(PrintWriter writer, List<DataFile> dataFiles) {
        writeFileMappingHeader(writer, hasPrideAccession(dataFiles));
        for (DataFile dataFile : dataFiles) {
            writeFileMapping(writer, dataFile);
        }
    }

    private static boolean hasPrideAccession(List<DataFile> dataFiles) {
        for (DataFile dataFile : dataFiles) {
            if (dataFile.getPrideAccession() != null) {
                return true;
            }
        }
        return false;
    }

    /**
     * Write an entry from MetaData
     *
     * @param writer print writer
     * @param type   metadata type
     * @param value  metadata string value
     */
    private static void writeMetaData(PrintWriter writer, String type, String value) {
        writer.println(Constant.METADATA_HEADER + Constant.TAB + type + Constant.TAB + cleanString(value));
    }

    /**
     * Write an entry from MetaData
     *
     * @param writer print writer
     * @param type   metadata type
     * @param value  metadata boolean value
     */
    private static void writeMetaData(PrintWriter writer, String type, boolean value) {
        writer.println(Constant.METADATA_HEADER + Constant.TAB + type + Constant.TAB + value);
    }

    /**
     * Write an entry from MetaData
     *
     * @param writer print writer
     * @param type   metadata type
     * @param value  metadata controlled vocabulary value
     */
    private static void writeMetaData(PrintWriter writer, String type, CvParam value) {
        writer.println(Constant.METADATA_HEADER + Constant.TAB + type + Constant.TAB + value.toString());
    }

    /**
     * Write a list of entries from MetaData
     *
     * @param writer print writer
     * @param type   metadata type
     * @param values a list of metadata values
     */
    private static void writeMetaData(PrintWriter writer, String type, List values, boolean combine) {
        if (combine) {
            String combined = "";
            for (Object value : values) {
                combined += (value instanceof CvParam ? cvParamToString((CvParam) value) : value.toString()) + Constant.COMMA;
            }
            combined = combined.substring(0, combined.length() - 1);
            writer.println(Constant.METADATA_HEADER + Constant.TAB + type + Constant.TAB + cleanString(combined));
        } else {
            for (Object value : values) {
                writer.println(Constant.METADATA_HEADER + Constant.TAB + type + Constant.TAB + (value instanceof CvParam ? cvParamToString((CvParam) value) : value.toString()));
            }
        }
    }

    /**
     * Write file mapping header
     *
     * @param writer print writer
     */
    private static void writeFileMappingHeader(PrintWriter writer, boolean hasPrideAccession) {
        writer.println(Constant.FILE_MAPPING_HEADER + Constant.TAB + Constant.FILE_ID
                + Constant.TAB + Constant.FILE_TYPE + Constant.TAB + Constant.FILE_PATH
                + Constant.TAB + Constant.FILE_MAPPING + (hasPrideAccession ? (Constant.TAB + Constant.PRIDE_ACCESSION) : ""));
    }

    /**
     * Write a file mapping entry to output file
     *
     * @param writer   print writer
     * @param dataFile data file mapping entry
     */
    private static void writeFileMapping(PrintWriter writer, DataFile dataFile) {
        // convert file type
        String type = null;
        switch (dataFile.getFileType()) {
            case RESULT:
                type = Constant.RESULT_FILE_TYPE;
                break;
            case RAW:
                type = Constant.RAW_FILE_TYPE;
                break;
            case SEARCH:
                type = Constant.SEARCH_ENGINE_FILE_TYPE;
                break;
            case PEAK:
                type = Constant.PEAK_LIST_FILE_TYPE;
                break;
            case OTHER:
                type = Constant.OTHER_FILE_TYPE;
                break;
        }

        // convert file path
        String path = null;
        if (dataFile.isFile()) {
            path = dataFile.getFile().getAbsolutePath();
        } else if (dataFile.isUrl()) {
            path = dataFile.getUrl().toString();
        }

        // convert file mappings
        String mappings = "";
        if (dataFile.hasMappings()) {
            for (DataFile file : dataFile.getFileMappings()) {
                mappings += file.getId() + Constant.COMMA;
            }
            mappings = mappings.substring(0, mappings.length() - 1);
        }

        // pride accession
        String prideAccession = "";
        if (dataFile.getPrideAccession() != null) {
            prideAccession = dataFile.getPrideAccession();
        }

        writer.println(Constant.FILE_MAPPING_ENTRY + Constant.TAB + dataFile.getId()
                + Constant.TAB + type + Constant.TAB + path + Constant.TAB + mappings + Constant.TAB + prideAccession);
    }


    /**
     * convert cv param to string for output
     */
    private static String cvParamToString(CvParam cvParam) {
        return Constant.CV_START + cvParam.getCvLabel() +
                Constant.COMMA + cvParam.getAccession() +
                Constant.COMMA + cvParam.getName() +
                Constant.COMMA + (cvParam.getValue() == null ? "" : cvParam.getValue()) + Constant.CV_END;
    }

    /**
     * Clean input string for output
     *
     * @param content input string
     * @return a formatted string
     */
    private static String cleanString(String content) {
        // replace all the tabs with a single space
        String result = content.replace(Constant.TAB, " ");

        // replace all the windows related line breaks
        result = result.replace(Constant.WIN_LINE_SEPARATOR, " ");

        // replace all linux related line breaks
        result = result.replace(Constant.LINUX_LINE_SEPARATOR, " ");

        // replace all the new lines with a single space
        result = result.replace(Constant.LINE_SEPARATOR, " ");

        return result;
    }
}
