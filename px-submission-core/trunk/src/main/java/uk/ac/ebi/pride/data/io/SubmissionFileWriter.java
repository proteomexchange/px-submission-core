package uk.ac.ebi.pride.data.io;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.ac.ebi.pride.data.exception.SubmissionFileException;
import uk.ac.ebi.pride.data.model.*;
import uk.ac.ebi.pride.data.util.Constant;
import uk.ac.ebi.pride.archive.dataprovider.file.ProjectFileType;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Collection;
import java.util.List;

/**
 * Writer for writing a submission file
 *
 * @author Rui Wang
 * @version $Id$
 */
public final class SubmissionFileWriter {

    private static final Logger logger = LoggerFactory.getLogger(SubmissionFileWriter.class);

    private SubmissionFileWriter() {
    }

    /**
     * Write a submission object to a given output file
     *
     * @param submission submission object
     * @param file       output file
     * @throws uk.ac.ebi.pride.data.exception.SubmissionFileException exception while writing a submission output file
     */
    public static void write(Submission submission, File file) throws SubmissionFileException {
        PrintWriter writer = null;
        try {
            // create writer
            writer = new PrintWriter(file, "UTF-8");

            // write general project meta data
            ProjectMetaData projectMetaData = submission.getProjectMetaData();
            writeGeneralProjectMetaData(writer, projectMetaData);
            writeSampleProjectMetaData(writer, projectMetaData);
            writer.println();

            // write file mappings
            writeFileMappings(writer, submission.getDataFiles());
            writer.println();

            // write sample metadata
            if (!submission.getProjectMetaData().isPartialSubmission()) {
                writeSampleMetaData(writer, submission.getDataFiles());
            }
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
     * Write general project level metadata to output
     */
    private static void writeGeneralProjectMetaData(PrintWriter writer, ProjectMetaData projectMetaData) {
        // submitter
        writeProjectMetaData(writer, Constant.SUBMITTER_NAME, projectMetaData.getSubmitterContact().getName());
        writeProjectMetaData(writer, Constant.SUBMITTER_EMAIL, projectMetaData.getSubmitterContact().getEmail());
        writeProjectMetaData(writer, Constant.SUBMITTER_AFFILIATION, projectMetaData.getSubmitterContact().getAffiliation());
        writeProjectMetaData(writer, Constant.SUBMITTER_USER_NAME, projectMetaData.getSubmitterContact().getUserName());

        // lab head
        Contact labHeadContact = projectMetaData.getLabHeadContact();
        if (labHeadContact != null) {
            writeProjectMetaData(writer, Constant.LAB_HEAD_NAME, labHeadContact.getName());
            writeProjectMetaData(writer, Constant.LAB_HEAD_EMAIL, labHeadContact.getEmail());
            writeProjectMetaData(writer, Constant.LAB_HEAD_AFFILIATION, labHeadContact.getAffiliation());
        }

        // title
        writeProjectMetaData(writer, Constant.PROJECT_TITLE, projectMetaData.getProjectTitle());

        // project description
        writeProjectMetaData(writer, Constant.PROJECT_DESC, projectMetaData.getProjectDescription());

        // project tag
        writeProjectMetaData(writer, Constant.PROJECT_TAG, projectMetaData.getProjectTags(), false);

        // keywords
        writeProjectMetaData(writer, Constant.KEYWORDS, projectMetaData.getKeywords());

        // sample processing protocol
        writeProjectMetaData(writer, Constant.SAMPLE_PROCESSING_PROTOCOL, projectMetaData.getSampleProcessingProtocol());

        // data processing protocol
        writeProjectMetaData(writer, Constant.DATA_PROCESSING_PROTOCOL, projectMetaData.getDataProcessingProtocol());

        // other omics link
        if (projectMetaData.hasOtherOmicsLink()) {
            writeProjectMetaData(writer, Constant.OTHER_OMICS_LINK, projectMetaData.getOtherOmicsLink());
        }

        // experiment type
        writeProjectMetaData(writer, Constant.EXPERIMENT_TYPE, projectMetaData.getMassSpecExperimentMethods(), false);

        // complete or partial
        writeProjectMetaData(writer, Constant.SUBMISSION_TYPE, projectMetaData.getSubmissionType().toString());

        // pubmed ids
        if (projectMetaData.hasPubmedIds()) {
            writeProjectMetaData(writer, Constant.PUBMED_ID, projectMetaData.getPubmedIds(), false);
        }

        // resubmission
        if (projectMetaData.isResubmission()) {
            writeProjectMetaData(writer, Constant.RESUBMISSION_PX_ACCESSION, projectMetaData.getResubmissionPxAccession());
        }

        // reanalysis
        if (projectMetaData.hasReanalysisPxAccessions()) {
            writeProjectMetaData(writer, Constant.REANALYSIS_PX_ACCESSION, projectMetaData.getReanalysisAccessions(), false);
        }

        // additional
        if (projectMetaData.hasAdditional()) {
            writeProjectMetaData(writer, Constant.ADDITIONAL, projectMetaData.getAdditional(), false);
        }
    }


    /**
     * Write project level metadata for partial submission
     */
    private static void writeSampleProjectMetaData(PrintWriter writer, ProjectMetaData projectMetaData) {
        // reason for partial submission
        writeProjectMetaData(writer, Constant.REASON_FOR_PARTIAL, projectMetaData.getReasonForPartialSubmission());

        // species
        writeProjectMetaData(writer, Constant.SPECIES, projectMetaData.getSpecies(), false);

        // tissue
        writeProjectMetaData(writer, Constant.TISSUE, projectMetaData.getTissues(), false);

        // cell type
        writeProjectMetaData(writer, Constant.CELL_TYPE, projectMetaData.getCellTypes(), false);

        // disease
        writeProjectMetaData(writer, Constant.DISEASE, projectMetaData.getDiseases(), false);

        // instrument
        writeProjectMetaData(writer, Constant.INSTRUMENT, projectMetaData.getInstruments(), false);

        // modification
        writeProjectMetaData(writer, Constant.MODIFICATION, projectMetaData.getModifications(), false);

        // quantification
        writeProjectMetaData(writer, Constant.QUANTIFICATION, projectMetaData.getQuantifications(), false);
    }

    /**
     * Write file mappings to output
     */
    private static void writeFileMappings(PrintWriter writer, List<DataFile> dataFiles) throws IOException {
        boolean prideAccsHeader = hasPrideAccession(dataFiles);
        boolean urlHeader = hasURL(dataFiles);
        writeFileMappingHeader(writer, prideAccsHeader, urlHeader);
        for (DataFile dataFile : dataFiles) {
            writeFileMapping(writer, dataFile, prideAccsHeader, urlHeader);
        }
    }

    /**
     * Write sample metadata
     */
    private static void writeSampleMetaData(PrintWriter writer, List<DataFile> dataFiles) {
        writeSampleMetaDataHeader(writer);
        for (DataFile dataFile : dataFiles) {
            if (ProjectFileType.RESULT.equals(dataFile.getFileType())) {
                writeSampleMetaDataEntry(writer, dataFile.getFileId(), dataFile.getSampleMetaData());
            }
        }
    }

    /**
     * Write sample metadata header
     */
    private static void writeSampleMetaDataHeader(PrintWriter writer) {
        writer.println(castToString(Constant.TAB, Constant.SAMPLE_METADATA_HEADER, Constant.FILE_ID,
                Constant.SPECIES, Constant.TISSUE,
                Constant.CELL_TYPE, Constant.DISEASE, Constant.MODIFICATION,
                Constant.INSTRUMENT, Constant.QUANTIFICATION,
                Constant.EXPERIMENTAL_FACTOR));
    }

    /**
     * Write a single sample metadata entry
     */
    private static void writeSampleMetaDataEntry(PrintWriter writer, int fileId, SampleMetaData metaData) {
        // species
        String species = "";
        if (metaData.hasMetaData(SampleMetaData.Type.SPECIES)) {
            species = castToString(Constant.COMMA, metaData.getMetaData(SampleMetaData.Type.SPECIES).toArray());
        }

        // tissue
        String tissues = "";
        if (metaData.hasMetaData(SampleMetaData.Type.TISSUE)) {
            tissues = castToString(Constant.COMMA, metaData.getMetaData(SampleMetaData.Type.TISSUE).toArray());
        }

        // cell type
        String cellTypes = "";
        if (metaData.hasMetaData(SampleMetaData.Type.CELL_TYPE)) {
            cellTypes = castToString(Constant.COMMA, metaData.getMetaData(SampleMetaData.Type.CELL_TYPE).toArray());
        }

        // disease
        String diseases = "";
        if (metaData.hasMetaData(SampleMetaData.Type.DISEASE)) {
            diseases = castToString(Constant.COMMA, metaData.getMetaData(SampleMetaData.Type.DISEASE).toArray());
        }

        // modification
        String modification = "";
        if (metaData.hasMetaData(SampleMetaData.Type.MODIFICATION)) {
            modification = castToString(Constant.COMMA, metaData.getMetaData(SampleMetaData.Type.MODIFICATION).toArray());
        }

        // instrument
        String instruments = "";
        if (metaData.hasMetaData(SampleMetaData.Type.INSTRUMENT)) {
            instruments = castToString(Constant.COMMA, metaData.getMetaData(SampleMetaData.Type.INSTRUMENT).toArray());
        }

        // quantification
        String quantifications = "";
        if (metaData.hasMetaData(SampleMetaData.Type.QUANTIFICATION_METHOD)) {
            quantifications = castToString(Constant.COMMA, metaData.getMetaData(SampleMetaData.Type.QUANTIFICATION_METHOD).toArray());
        }

        String experimentalFactor = metaData.getMetaData(SampleMetaData.Type.EXPERIMENTAL_FACTOR).iterator().next().getValue();

        writer.println(castToString(Constant.TAB, Constant.SAMPLE_METADATA_ENTRY,
                fileId, species,
                tissues, cellTypes, diseases, modification,
                instruments, quantifications,
                experimentalFactor));
    }

    private static boolean hasPrideAccession(List<DataFile> dataFiles) {
        for (DataFile dataFile : dataFiles) {
            if (dataFile.getAssayAccession() != null) {
                return true;
            }
        }
        return false;
    }

    private static boolean hasURL(List<DataFile> dataFiles) {
        boolean result = false;
        for (DataFile dataFile : dataFiles) {
            if (dataFile.getUrl() != null && !dataFile.getUrl().toString().trim().isEmpty()) {
                result = true;
                break;
            }
        }
        return result;
    }

    /**
     * Write an entry from MetaData
     *
     * @param writer print writer
     * @param type   metadata type
     * @param value  metadata string value
     */
    private static void writeProjectMetaData(PrintWriter writer, String type, Object value) {
        if (value != null) {
            String cleanedValue = cleanString(value.toString());
            if (cleanedValue.trim().length() != 0) {
                writer.println(castToString(Constant.TAB, Constant.METADATA_HEADER, type, cleanedValue));
            }
        }
    }

    /**
     * Write a list of entries from MetaData
     *
     * @param writer print writer
     * @param type   metadata type
     * @param values a list of metadata values
     */
    private static void writeProjectMetaData(PrintWriter writer, String type, Collection values, boolean combine) {
        if (combine) {
            String combinedValues = castToString(Constant.COMMA, values);
            if (combinedValues.trim().length() != 0) {
                writer.println(castToString(Constant.TAB, Constant.METADATA_HEADER, type, cleanString(combinedValues)));
            }
        } else {
            if (!values.isEmpty()) {
                for (Object value : values) {
                    writer.println(castToString(Constant.TAB, Constant.METADATA_HEADER, type, value.toString()));
                }
            }
        }
    }

    /**
     * Write file mapping header
     *
     * @param writer print writer
     */
    private static void writeFileMappingHeader(PrintWriter writer, boolean hasPrideAccession,  boolean hasURLs) {
        StringBuilder sb = new StringBuilder(castToString(Constant.TAB, Constant.FILE_MAPPING_HEADER, Constant.FILE_ID,
                Constant.FILE_TYPE, Constant.FILE_PATH, Constant.FILE_MAPPING));
        if (hasPrideAccession) {
            sb.append('\t');
            sb.append(Constant.PRIDE_ACCESSION);
        }
        if (hasURLs) {
            sb.append('\t');
            sb.append(Constant.URL);
        }
        writer.println(sb.toString());
    }

    /**
     * Write a file mapping entry to output file
     *
     * @param writer   print writer
     * @param dataFile data file mapping entry
     */
    private static void writeFileMapping(PrintWriter writer, DataFile dataFile, boolean hasPrideAccession,  boolean hasURLs) throws IOException {
        // convert file type
        String type = dataFile.getFileType().name();

        // convert file path
        String path = "";
        String url = "";
        if (dataFile.isFile()) {
            path = dataFile.getFile().getCanonicalPath();
        }
        if (dataFile.isUrl()) {
            url = dataFile.getUrl().toString();
            if (path.isEmpty()) {
                path = dataFile.getUrl().getFile();
            }
        }

        // convert file mappings
        String mappings = "";
        if (dataFile.hasMappings()) {
            for (DataFile file : dataFile.getFileMappings()) {
                mappings += file.getFileId() + Constant.COMMA;
            }
            mappings = mappings.substring(0, mappings.length() - 1);
        }

        // pride accession
        String prideAccession = "";
        if (dataFile.getAssayAccession() != null) {
            prideAccession = dataFile.getAssayAccession();
        }

        StringBuilder sb = new StringBuilder(castToString(Constant.TAB, Constant.FILE_MAPPING_ENTRY, dataFile.getFileId(),
                type, path, mappings));
        if (hasPrideAccession) {
            sb.append('\t');
            sb.append(prideAccession);
        }
        if (hasURLs) {
            sb.append('\t');
            sb.append(url);
        }
        writer.println(sb.toString());
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


    /**
     * Cast a set of objects into a combined string with a given separator
     *
     * @param separator string separator
     * @param values    a set of objects
     * @return combined string
     */
    private static String castToString(String separator, Object... values) {
        StringBuilder builder = new StringBuilder();

        for (Object value : values) {
            builder.append(value.toString());
            builder.append(separator);
        }

        // remove the last the separator
        String str = builder.toString();
        if (values.length > 0) {
            str = str.substring(0, str.length() - separator.length());
        }
        return str;
    }
}
