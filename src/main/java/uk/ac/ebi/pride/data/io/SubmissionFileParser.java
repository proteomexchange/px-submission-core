package uk.ac.ebi.pride.data.io;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.ac.ebi.pride.archive.dataprovider.file.ProjectFileType;
import uk.ac.ebi.pride.archive.dataprovider.project.SubmissionType;
import uk.ac.ebi.pride.data.exception.SubmissionFileException;
import uk.ac.ebi.pride.data.model.*;
import uk.ac.ebi.pride.data.util.Constant;
import uk.ac.ebi.pride.data.util.ExperimentalFactorUtil;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;

/**
 * Parser for reading a submission file
 *
 * @author Rui Wang
 * @version $Id$
 */
public class SubmissionFileParser {
    private static final Logger logger = LoggerFactory.getLogger(SubmissionFileParser.class);

    /**
     * Minimum number of entries in metadata entry line
     */
    private static final int MIN_METADATA_ENTRIES = 3;
    /**
     * Minimum number of entries in file mapping entry line
     */
    private static final int MIN_FILE_MAPPING_ENTRIES = 3;
    /**
     * Minimum number of entries in sample metadata entry line
     */
    private static final int MIN_SAMPLE_METADATA_ENTRIES = 4;


    /**
     * private constructor to avoid creating an new instance
     */
    private SubmissionFileParser() {
    }

    /**
     * Parse an input file into a submission object
     *
     * @param file input submission file
     * @return Submission  submission object
     * @throws uk.ac.ebi.pride.data.exception.SubmissionFileException
     *          exception while reading submission file
     */
    public static Submission parse(File file) throws SubmissionFileException {
        Submission submission = new Submission();
        parse(submission, file);
        return submission;
    }

    /**
     * Parse an input file into a pre-existing submission object
     *
     * @param submission given submission object
     * @param file       input submission file
     * @throws uk.ac.ebi.pride.data.exception.SubmissionFileException
     *          exception while reading submission file
     */
    public static void parse(Submission submission, File file) throws SubmissionFileException {
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new FileReader(file));
            String line;
            List<String[]> metadata = new ArrayList<>();
            String[] fileMappingHeaders = null;
            List<String[]> fileMappings = new ArrayList<>();
            String[] sampleMetadataHeaders = null;
            List<String[]> sampleMetadata = new ArrayList<>();
            while ((line = reader.readLine()) != null) {
                if (line.trim().length() > 0) {
                    String[] parts = line.split(Constant.TAB, -1);
                    int length = parts.length;
                    if (length <= 1) {
                        String msg = "The MetaData section of the submission file must have three tab-separated parts: " + line;
                        throw new SubmissionFileException(msg);
                    }
                    // check the content type
                    boolean isMetaData = Constant.METADATA_HEADER.equals(parts[0]);
                    boolean isFileMapping = Constant.FILE_MAPPING_HEADER.equals(parts[0]) || Constant.FILE_MAPPING_ENTRY.equals(parts[0]);
                    boolean isSampleMetadata = Constant.SAMPLE_METADATA_HEADER.equals(parts[0]) || Constant.SAMPLE_METADATA_ENTRY.equals(parts[0]);
                    if (!isMetaData && !isFileMapping && !isSampleMetadata) { // check whether it is a valid file format
                        String msg = "Unrecognised submission file section: " + file.getAbsolutePath();
                        logger.error(msg);
                        throw new SubmissionFileException(msg);
                    }
                    // process the file content
                    if (isMetaData) {
                        if (length >= MIN_METADATA_ENTRIES) {
                            metadata.add(parts);
                        } else {
                            String msg = "The MetaData section of the submission file must have three tab-separated parts: " + line;
                            throw new SubmissionFileException(msg);
                        }
                    } else if (isFileMapping) {
                        if (length >= MIN_FILE_MAPPING_ENTRIES) {
                            if (Constant.FILE_MAPPING_HEADER.equals(parts[0])) {
                                fileMappingHeaders = parts;
                            } else {
                                fileMappings.add(parts);
                            }
                        } else {
                            String msg = "The File Mapping section of the submission file must have three tab-separated parts: " + line;
                            throw new SubmissionFileException(msg);
                        }
                    } else if (isSampleMetadata) {
                        if (length >= MIN_SAMPLE_METADATA_ENTRIES) {
                            if (Constant.SAMPLE_METADATA_HEADER.equals(parts[0])) {
                                sampleMetadataHeaders = parts;
                            } else {
                                sampleMetadata.add(parts);
                            }
                        } else {
                            String msg = "The Sample Metadata section of the submission file must have four tab-separated parts: " + line;
                            throw new SubmissionFileException(msg);
                        }
                    }
                }
            }
            parseProjectMetadata(submission, metadata);
            parseFileMapping(submission, fileMappingHeaders, fileMappings);
            parseSampleMetadata(submission, sampleMetadataHeaders, sampleMetadata);
        } catch (FileNotFoundException e) {
            String msg = "Failed to find submission file: " + file.getAbsolutePath();
            logger.error(msg, e);
            throw new SubmissionFileException(msg, e);
        } catch (IOException e) {
            String msg = "Failed to read from submission file: " + file.getAbsolutePath();
            logger.error(msg, e);
            throw new SubmissionFileException(msg, e);
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    logger.error("Failed to close submission file reader", e);
                }
            }
        }
    }

    /**
     * Parse metadata section
     *
     * @param submission submission object
     * @param metadata   metadata entries
     */
    private static void parseProjectMetadata(Submission submission, List<String[]> metadata) {
        ProjectMetaData projectMetaData = submission.getProjectMetaData();
        projectMetaData.setSubmitterContact(new Contact());
        for (String[] entry : metadata) {
            String type = entry[1];
            String value = entry[2].trim();
            Contact submitterContact = projectMetaData.getSubmitterContact();
            Contact labHeadContact = projectMetaData.getLabHeadContact();
            if (Constant.SUBMITTER_NAME.equalsIgnoreCase(type) || Constant.LEGACY_SUBMITTER_NAME.equalsIgnoreCase(type)) {
                submitterContact.setName(value);
            } else if (Constant.SUBMITTER_EMAIL.equalsIgnoreCase(type) || Constant.LEGACY_SUBMITTER_EMAIL.equalsIgnoreCase(type)) {
                submitterContact.setEmail(value);
            } else if (Constant.SUBMITTER_AFFILIATION.equalsIgnoreCase(type) || Constant.LEGACY_SUBMITTER_AFFILIATION.equalsIgnoreCase(type)) {
                submitterContact.setAffiliation(value);
            } else if (Constant.SUBMITTER_USER_NAME.equalsIgnoreCase(type) || Constant.LEGACY_SUBMITTER_USER_NAME.equalsIgnoreCase(type)) {
                submitterContact.setUserName(value);
            } else if (Constant.LAB_HEAD_NAME.equalsIgnoreCase(type)) {
                labHeadContact.setName(value);
            } else if (Constant.LAB_HEAD_EMAIL.equalsIgnoreCase(type)) {
                labHeadContact.setEmail(value);
            } else if (Constant.LAB_HEAD_AFFILIATION.equalsIgnoreCase(type)) {
                labHeadContact.setAffiliation(value);
            } else if (Constant.PROJECT_TITLE.equalsIgnoreCase(type)  || Constant.LEGACY_PROJECT_TITLE.equalsIgnoreCase(type)) {
                projectMetaData.setProjectTitle(value);
            } else if (Constant.PROJECT_DESC.equalsIgnoreCase(type) || Constant.LEGACY_PROJECT_DESC.equalsIgnoreCase(type)) {
                projectMetaData.setProjectDescription(value);
            } else if (Constant.PROJECT_TAG.equalsIgnoreCase(type)) {
                projectMetaData.addProjectTags(value);
            } else if (Constant.KEYWORDS.equalsIgnoreCase(type)) {
                projectMetaData.setKeywords(value);
            } else if (Constant.SAMPLE_PROCESSING_PROTOCOL.equalsIgnoreCase(type)) {
                projectMetaData.setSampleProcessingProtocol(value);
            } else if (Constant.DATA_PROCESSING_PROTOCOL.equalsIgnoreCase(type)) {
                projectMetaData.setDataProcessingProtocol(value);
            } else if (Constant.OTHER_OMICS_LINK.equalsIgnoreCase(type)) {
                projectMetaData.setOtherOmicsLink(value);
            } else if (Constant.EXPERIMENT_TYPE.equalsIgnoreCase(type)) {
                projectMetaData.addMassSpecExperimentMethods(createCvParam(value));
            } else if (Constant.SUBMISSION_TYPE.equalsIgnoreCase(type) || Constant.LEGACY_SUBMISSION_TYPE.equalsIgnoreCase(type)) {
                if (SubmissionType.COMPLETE.toString().equalsIgnoreCase(value) || Constant.LEGACY_SUPPORTED_SUBMISSION.equalsIgnoreCase(value)) {
                    projectMetaData.setSubmissionType(SubmissionType.COMPLETE);
                } else if (SubmissionType.PARTIAL.toString().equalsIgnoreCase(value) || Constant.LEGACY_UNSUPPORTED_SUBMISSION.equalsIgnoreCase(value)) {
                    projectMetaData.setSubmissionType(SubmissionType.PARTIAL);
                } else if (SubmissionType.RAW.toString().equalsIgnoreCase(value)) {
                    projectMetaData.setSubmissionType(SubmissionType.RAW);
                } else if (SubmissionType.PRIDE.toString().equalsIgnoreCase(value)) {
                    projectMetaData.setSubmissionType(SubmissionType.PRIDE);
                }
            } else if (Constant.REASON_FOR_PARTIAL.equalsIgnoreCase(type) || Constant.LEGACY_REASON_FOR_PARTIAL.equalsIgnoreCase(type)) {
                projectMetaData.setReasonForPartialSubmission(value);
            } else if (Constant.SPECIES.equalsIgnoreCase(type)) {
                projectMetaData.addSpecies(createCvParam(value));
            } else if (Constant.TISSUE.equalsIgnoreCase(type)) {
                projectMetaData.addTissues(createCvParam(value));
            } else if (Constant.CELL_TYPE.equalsIgnoreCase(type)) {
                projectMetaData.addCellTypes(createCvParam(value));
            } else if (Constant.DISEASE.equalsIgnoreCase(type)) {
                projectMetaData.addDiseases(createCvParam(value));
            } else if (Constant.INSTRUMENT.equalsIgnoreCase(type)) {
                projectMetaData.addInstruments(createCvParam(value));
            } else if (Constant.MODIFICATION.equalsIgnoreCase(type)) {
                projectMetaData.addModifications(createCvParam(value));
            } else if (Constant.QUANTIFICATION.equalsIgnoreCase(type)) {
                projectMetaData.addQuantifications(createCvParam(value));
            } else if (Constant.ADDITIONAL.equalsIgnoreCase(type)) {
                projectMetaData.addAdditional(createParam(value));
            } else if (Constant.PUBMED_ID.equalsIgnoreCase(type)) {
                projectMetaData.addPubmedIds(value);
            } else if (Constant.DOI.equalsIgnoreCase(type)) {
                projectMetaData.addDois(value);
            } else if (Constant.RESUBMISSION_PX_ACCESSION.equalsIgnoreCase(type)) {
                projectMetaData.setResubmissionPxAccession(value);
            } else if (Constant.REANALYSIS_PX_ACCESSION.equalsIgnoreCase(type)) {
                projectMetaData.addReanalysisPxAccessions(value);
            } else if (Constant.RPXD_ORIGINAL_ACCESSION.equalsIgnoreCase(type)) {
                projectMetaData.addRpxdOriginalPxAccessions(value);
            }
        }
    }

    /**
     * Parse file mapping
     *
     * @param submission submission object
     * @param headers    file mapping headers
     * @param entries    file mapping entry
     * @throws uk.ac.ebi.pride.data.exception.SubmissionFileException
     *          exception wihle parsing the input file
     */
    private static void parseFileMapping(Submission submission, String[] headers, List<String[]> entries) throws SubmissionFileException {
        Map<Integer, DataFile> fileObjectsMappings = new LinkedHashMap<>();
        Map<Integer, List<Integer>> fileIdMappings = new HashMap<>();
        int idIndex = -1, typeIndex = -1, pathIndex = -1, mappingIndex = -1, prideAccIndex = -1, urlIndex = -1, rpxdOriginalAccessionIndex = -1;
        for (int i = 0; i < headers.length; i++) {
            String header = headers[i].trim();
            if (Constant.FILE_ID.equalsIgnoreCase(header)) {
                idIndex = i;
            } else if (Constant.FILE_TYPE.equalsIgnoreCase(header)) {
                typeIndex = i;
            } else if (Constant.FILE_PATH.equalsIgnoreCase(header)) {
                pathIndex = i;
            } else if (Constant.FILE_MAPPING.equalsIgnoreCase(header)) {
                mappingIndex = i;
            } else if (Constant.PRIDE_ACCESSION.equalsIgnoreCase(header)) {
                prideAccIndex = i;
            }  else if (Constant.URL.equalsIgnoreCase(header)) {
                urlIndex = i;
            } else if (Constant.RPXD_ORIGINAL_ACCESSION.equalsIgnoreCase(header)) {
                rpxdOriginalAccessionIndex = i;
            }
        }
        for (String[] entry : entries) {
            String idStr = entry[idIndex].trim();
            int id = Integer.parseInt(idStr);
            String path = entry[pathIndex].trim();
            URL url = null;
            File file = null;
            try {
                url = new URL(path);
            } catch (MalformedURLException e) {
                path = path.replace("\\", "/");
                file = new File(path);
                try {
                    if (urlIndex > -1 && urlIndex < entry.length && entry[urlIndex]!=null && !entry[urlIndex].trim().isEmpty()) {
                        url = new URL(entry[urlIndex].trim());
                    } } catch (MalformedURLException me) {
                    logger.error("Malformed URL, continuing anyway: " + urlIndex);
                }
            }
            String fileType = entry[typeIndex].trim();
            ProjectFileType type = ProjectFileType.fromString(fileType);
            if (type == null) {
                throw new SubmissionFileException("Invalid file type: " + fileType);
            }
            String prideAccession = null; // optional
            if (prideAccIndex != -1 && prideAccIndex < entry.length) {
                prideAccession = entry[prideAccIndex].trim();
            }
            String rpxdOriginalAccession = null; // optional
            if (rpxdOriginalAccessionIndex > -1 && rpxdOriginalAccessionIndex < entry.length &&
                entry[rpxdOriginalAccessionIndex]!=null && !entry[rpxdOriginalAccessionIndex].trim().isEmpty()) {
                rpxdOriginalAccession = entry[rpxdOriginalAccessionIndex].trim();
                if (!submission.getProjectMetaData().hasRpxdPriginalPxAccession(rpxdOriginalAccession)) {
                    submission.getProjectMetaData().addRpxdOriginalPxAccessions(rpxdOriginalAccession);
                }
            }
            DataFile dataFile = new DataFile(id, file, url, type, new ArrayList<>(), prideAccession, rpxdOriginalAccession);
            fileObjectsMappings.put(id, dataFile);
            if (entry.length > mappingIndex) {
                String mappingStr = entry[mappingIndex].trim();
                if (mappingStr.length() > 0) {
                    String[] parts = mappingStr.split(Constant.COMMA);
                    List<Integer> idList = new ArrayList<>();
                    fileIdMappings.put(id, idList);
                    for (String part : parts) {
                        if (isNonNegativeInteger(part)) {
                            idList.add(new Integer(part.trim()));
                        } else {
                            throw new SubmissionFileException("Invalid file id, must be non-negative integer: " + part);
                        }
                    }
                }
            }
        }
        for (Integer id : fileIdMappings.keySet()) {
            DataFile dataFile = fileObjectsMappings.get(id);
            List<Integer> idMappings = fileIdMappings.get(id);
            for (Integer idMapping : idMappings) {
                DataFile mappedDataFile = fileObjectsMappings.get(idMapping);
                if (mappedDataFile != null) {
                    dataFile.addFileMapping(mappedDataFile);
                } else {
                    throw new SubmissionFileException("Invalid file id, it must related to valid data file: " + idMapping);
                }
            }
        }
        submission.addDataFiles(fileObjectsMappings.values());
    }

    /**
     * Check whether an string is a non-negative integer.
     *
     * @param string input string
     * @return boolean true means it is a non-negative integer.
     */
    private static boolean isNonNegativeInteger(String string) {
        if (isInteger(string)) {
            int i = Integer.parseInt(string.trim());
            return i >= 0;
        } else {
            return false;
        }
    }

    /**
     * Check whether an string is an integer.
     *
     * @param string    input string
     * @return  boolean true means it is an integer
     */
    private static boolean isInteger(String string) {
        if (string == null || string.trim().isEmpty()) {
            return false;
        }
        String trimmedString = string.trim();
        int i = 0;
        if (trimmedString.charAt(0) == '-') {
            if (trimmedString.length() > 1) {
                i++;
            } else {
                return false;
            }
        }
        for (; i < trimmedString.length(); i++) {
            char c = trimmedString.charAt(i);
            if (!Character.isDigit(c)) {
                return false;
            }
        }
        try {
            Integer.parseInt(trimmedString);
        } catch (Exception ex) {
            return false;
        }
        return true;
    }

    /**
     * Parse sample metadata section
     *
     * @param submission submission object
     * @param headers    sample metadata headers
     * @param entries    sample metadata
     */
    private static void parseSampleMetadata(Submission submission, String[] headers, List<String[]> entries) throws SubmissionFileException {
        for (String[] entry : entries) {
            SampleMetaData sampleMetaDataEntry = new SampleMetaData();
            DataFile dataFile = null;
            int fileId = -1;
            for (int i = 0; i < headers.length; i++) {
                String value = entry[i].trim();
                if (value.length() > 0) {
                    String header = headers[i].trim();
                    if (Constant.FILE_ID.equalsIgnoreCase(header)) {
                        fileId = Integer.parseInt(value);
                        dataFile = submission.getDataFileById(fileId);
                    } else if (Constant.SPECIES.equalsIgnoreCase(header)) {
                        sampleMetaDataEntry.setMetaData(SampleMetaData.Type.SPECIES, createMultipleCvParams(value));
                    } else if (Constant.TISSUE.equalsIgnoreCase(header)) {
                        sampleMetaDataEntry.setMetaData(SampleMetaData.Type.TISSUE, createMultipleCvParams(value));
                    } else if (Constant.DISEASE.equalsIgnoreCase(header)) {
                        sampleMetaDataEntry.setMetaData(SampleMetaData.Type.DISEASE, createMultipleCvParams(value));
                    } else if (Constant.CELL_TYPE.equalsIgnoreCase(header)) {
                        sampleMetaDataEntry.setMetaData(SampleMetaData.Type.CELL_TYPE, createMultipleCvParams(value));
                    } else if (Constant.MODIFICATION.equalsIgnoreCase(header)) {
                        sampleMetaDataEntry.setMetaData(SampleMetaData.Type.MODIFICATION, createMultipleCvParams(value));
                    } else if (Constant.INSTRUMENT.equalsIgnoreCase(header)) {
                        sampleMetaDataEntry.setMetaData(SampleMetaData.Type.INSTRUMENT, createMultipleCvParams(value));
                    } else if (Constant.QUANTIFICATION.equalsIgnoreCase(header)) {
                        sampleMetaDataEntry.setMetaData(SampleMetaData.Type.QUANTIFICATION_METHOD, createMultipleCvParams(value));
                    } else if (Constant.EXPERIMENTAL_FACTOR.equalsIgnoreCase(header)) {
                        sampleMetaDataEntry.addMetaData(SampleMetaData.Type.EXPERIMENTAL_FACTOR,
                                ExperimentalFactorUtil.getExperimentalFactorCvParam(value));
                    }
                }
            }
            if (dataFile == null) {
                throw new SubmissionFileException("Failed to find data file for sample metadata, file id: " + fileId);
            }
            dataFile.setSampleMetaData(sampleMetaDataEntry);
        }
    }


    /**
     * Parse all the params
     */
    private static Set<Param> createMultipleParams(String str) {
        String parts[] = str.split(Constant.COMMA + "\\" + Constant.PARAM_START);
        Set<Param> params = new LinkedHashSet<Param>();
        params.add(createParam(parts[0]));
        if (parts.length > 1) {
            for (int i = 1; i < parts.length; i++) {
                String part = parts[i];
                params.add(createParam(Constant.PARAM_START + part));
            }
        }
        return params;
    }


    /**
     * Parse all the cv params
     */
    private static Set<CvParam> createMultipleCvParams(String str) {
        String parts[] = str.split(Constant.COMMA + "\\" + Constant.PARAM_START);
        Set<CvParam> cvParams = new LinkedHashSet<CvParam>();
        cvParams.add(createCvParam(parts[0]));
        if (parts.length > 1) {
            for (int i = 1; i < parts.length; i++) {
                String part = parts[i];
                cvParams.add(createCvParam(Constant.PARAM_START + part));
            }
        }
        return cvParams;
    }

    /**
     * Create a CvParam based on a given string
     */
    private static CvParam createCvParam(String str) {
        str = str.trim();
        str = str.substring(1);
        str = str.substring(0, str.length() - 1);
        String[] parts = str.split(",", -1);
        return new CvParam(parts[0].trim(), parts[1].trim(), parts[2].trim(), ("".equals(parts[3].trim()) ? null : parts[3].trim()));
    }

    /**
     * Create a Param based on a given string
     */
    private static Param createParam(String str) {
        str = str.trim();
        str = str.substring(1);
        str = str.substring(0, str.length() - 1);
        String[] parts = str.split(",", -1);
        if ("".equals(parts[0].trim())) {
            return new Param(parts[2].trim(), ("".equals(parts[3].trim()) ? null : parts[3].trim()));
        } else {
            return new CvParam(parts[0].trim(), parts[1].trim(), parts[2].trim(), ("".equals(parts[3].trim()) ? null : parts[3].trim()));
        }
    }

    public static void main(String[] args) throws SubmissionFileException {
        Submission submission = SubmissionFileParser.parse(new File(args[0]));
        logger.info("Submission summary file parsed successfully: " + submission.toString());
    }
}
