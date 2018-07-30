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
            // File reader
            reader = new BufferedReader(new FileReader(file));
            // each line from the file
            String line;
            // store all metadata
            List<String[]> metadata = new ArrayList<String[]>();
            // store file mapping headers
            String[] fileMappingHeaders = null;
            // store all file mappings
            List<String[]> fileMappings = new ArrayList<String[]>();
            // store sample metadata headers
            String[] sampleMetadataHeaders = null;
            // store sample metadata
            List<String[]> sampleMetadata = new ArrayList<String[]>();


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

                    // check whether it is a valid file format
                    if (!isMetaData && !isFileMapping && !isSampleMetadata) {
                        String msg = "Unrecognised submission file section: " + file.getAbsolutePath();
                        logger.error(msg);
                        throw new SubmissionFileException(msg);
                    }

                    // process the file content
                    if (isMetaData) {
                        // metadata
                        if (length >= MIN_METADATA_ENTRIES) {
                            metadata.add(parts);
                        } else {
                            String msg = "The MetaData section of the submission file must have three tab-separated parts: " + line;
                            throw new SubmissionFileException(msg);
                        }
                    } else if (isFileMapping) {
                        // file mappings
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
                        // sample metadata
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

            // parse project metadata
            parseProjectMetadata(submission, metadata);

            // parse file mappings
            parseFileMapping(submission, fileMappingHeaders, fileMappings);

            // parse sample metadata
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

        // get metadata
        ProjectMetaData projectMetaData = submission.getProjectMetaData();
        projectMetaData.setSubmitterContact(new Contact());

        for (String[] entry : metadata) {
            // save meta data
            String type = entry[1];
            String value = entry[2].trim();
            Contact submitterContact = projectMetaData.getSubmitterContact();
            Contact labHeadContact = projectMetaData.getLabHeadContact();
            if (Constant.SUBMITTER_NAME.equalsIgnoreCase(type) || Constant.LEGACY_SUBMITTER_NAME.equalsIgnoreCase(type)) {
                // first name
                submitterContact.setName(value);
            } else if (Constant.SUBMITTER_EMAIL.equalsIgnoreCase(type) || Constant.LEGACY_SUBMITTER_EMAIL.equalsIgnoreCase(type)) {
                // email
                submitterContact.setEmail(value);
            } else if (Constant.SUBMITTER_AFFILIATION.equalsIgnoreCase(type) || Constant.LEGACY_SUBMITTER_AFFILIATION.equalsIgnoreCase(type)) {
                // affiliation
                submitterContact.setAffiliation(value);
            } else if (Constant.SUBMITTER_USER_NAME.equalsIgnoreCase(type) || Constant.LEGACY_SUBMITTER_USER_NAME.equalsIgnoreCase(type)) {
                // pride user
                submitterContact.setUserName(value);
            } else if (Constant.LAB_HEAD_NAME.equalsIgnoreCase(type)) {
                // first name
                labHeadContact.setName(value);
            } else if (Constant.LAB_HEAD_EMAIL.equalsIgnoreCase(type)) {
                // email
                labHeadContact.setEmail(value);
            } else if (Constant.LAB_HEAD_AFFILIATION.equalsIgnoreCase(type)) {
                // affiliation
                labHeadContact.setAffiliation(value);
            } else if (Constant.SUBMITTER_USER_NAME.equalsIgnoreCase(type) || Constant.LEGACY_SUBMITTER_USER_NAME.equalsIgnoreCase(type)) {
                // pride user
                submitterContact.setUserName(value);
            } else if (Constant.PROJECT_TITLE.equalsIgnoreCase(type)  || Constant.LEGACY_PROJECT_TITLE.equalsIgnoreCase(type)) {
                // title
                projectMetaData.setProjectTitle(value);
            } else if (Constant.PROJECT_DESC.equalsIgnoreCase(type) || Constant.LEGACY_PROJECT_DESC.equalsIgnoreCase(type)) {
                // experiment description
                projectMetaData.setProjectDescription(value);
            } else if (Constant.PROJECT_TAG.equalsIgnoreCase(type)) {
                // species
                projectMetaData.addProjectTags(value);
            } else if (Constant.KEYWORDS.equalsIgnoreCase(type)) {
                // keywords
                projectMetaData.setKeywords(value);
            } else if (Constant.SAMPLE_PROCESSING_PROTOCOL.equalsIgnoreCase(type)) {
                // sample processing protocol
                projectMetaData.setSampleProcessingProtocol(value);
            } else if (Constant.DATA_PROCESSING_PROTOCOL.equalsIgnoreCase(type)) {
                // data processing protocol
                projectMetaData.setDataProcessingProtocol(value);
            } else if (Constant.OTHER_OMICS_LINK.equalsIgnoreCase(type)) {
                // other omics link
                projectMetaData.setOtherOmicsLink(value);
            } else if (Constant.EXPERIMENT_TYPE.equalsIgnoreCase(type)) {
                // experiment type
                projectMetaData.addMassSpecExperimentMethods(createCvParam(value));
            } else if (Constant.SUBMISSION_TYPE.equalsIgnoreCase(type) || Constant.LEGACY_SUBMISSION_TYPE.equalsIgnoreCase(type)) {
                // submission type
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
                // reason for partial submission
                projectMetaData.setReasonForPartialSubmission(value);
            } else if (Constant.SPECIES.equalsIgnoreCase(type)) {
                // species
                projectMetaData.addSpecies(createCvParam(value));
            } else if (Constant.TISSUE.equalsIgnoreCase(type)) {
                // species
                projectMetaData.addTissues(createCvParam(value));
            } else if (Constant.CELL_TYPE.equalsIgnoreCase(type)) {
                // species
                projectMetaData.addCellTypes(createCvParam(value));
            } else if (Constant.DISEASE.equalsIgnoreCase(type)) {
                // species
                projectMetaData.addDiseases(createCvParam(value));
            } else if (Constant.INSTRUMENT.equalsIgnoreCase(type)) {
                // instrument
                projectMetaData.addInstruments(createCvParam(value));
            } else if (Constant.MODIFICATION.equalsIgnoreCase(type)) {
                // modification
                projectMetaData.addModifications(createCvParam(value));
            } else if (Constant.QUANTIFICATION.equalsIgnoreCase(type)) {
                // modification
                projectMetaData.addQuantifications(createCvParam(value));
            } else if (Constant.ADDITIONAL.equalsIgnoreCase(type)) {
                // additional
                projectMetaData.addAdditional(createParam(value));
            } else if (Constant.PUBMED_ID.equalsIgnoreCase(type)) {
                // pubmed ids
                projectMetaData.addPubmedIds(value);
            } else if (Constant.DOI.equalsIgnoreCase(type)) {
                // DOIs
                projectMetaData.addDois(value);
            }else if (Constant.RESUBMISSION_PX_ACCESSION.equalsIgnoreCase(type)) {
                projectMetaData.setResubmissionPxAccession(value);
            } else if (Constant.REANALYSIS_PX_ACCESSION.equalsIgnoreCase(type)) {
                projectMetaData.addReanalysisPxAccessions(value);
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
        // create all the objects and mappings

        // map to store the mapping between file id and data file object
        Map<Integer, DataFile> fileMap = new LinkedHashMap<Integer, DataFile>();

        // map to store the mapping between files
        Map<Integer, List<Integer>> idMap = new HashMap<Integer, List<Integer>>();

        // get the index of all the values
        int idIndex = -1, typeIndex = -1, pathIndex = -1, mappingIndex = -1, prideAccIndex = -1, urlIndex = -1;
        for (int i = 0; i < headers.length; i++) {
            String header = headers[i].trim();
            if (Constant.FILE_ID.equalsIgnoreCase(header)) {
                // file id
                idIndex = i;
            } else if (Constant.FILE_TYPE.equalsIgnoreCase(header)) {
                // file type
                typeIndex = i;
            } else if (Constant.FILE_PATH.equalsIgnoreCase(header)) {
                // file path
                pathIndex = i;
            } else if (Constant.FILE_MAPPING.equalsIgnoreCase(header)) {
                // file mapping
                mappingIndex = i;
            } else if (Constant.PRIDE_ACCESSION.equalsIgnoreCase(header)) {
                // pride accession
                prideAccIndex = i;
            }  else if (Constant.URL.equalsIgnoreCase(header)) {
                // url
                urlIndex = i;
            }
        }

        // create all data file objects
        for (String[] entry : entries) {
            // validate the file id
            String idStr = entry[idIndex].trim();

            int id = Integer.parseInt(idStr);

            // file or url object
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

            // validate the file type
            String fileType = entry[typeIndex].trim();
            ProjectFileType type = ProjectFileType.fromString(fileType);
            if (type == null) {
                throw new SubmissionFileException("Invalid file type: " + fileType);
            }

            // pride accession, this is optional
            String prideAccession = null;
            if (prideAccIndex != -1 && entry.length > prideAccIndex) {
                prideAccession = entry[prideAccIndex].trim();
            }

            // create data file object
            DataFile dataFile = new DataFile(id, file, url, type, new ArrayList<DataFile>(), prideAccession);
            fileMap.put(id, dataFile);

            // mappings
            if (entry.length > mappingIndex) {
                String mappingStr = entry[mappingIndex].trim();
                if (mappingStr.length() > 0) {
                    String[] parts = mappingStr.split(Constant.COMMA);
                    List<Integer> idList = new ArrayList<Integer>();
                    idMap.put(id, idList);
                    for (String part : parts) {
                        if (isNonNegativeInteger(part)) {
                            idList.add(new Integer(part.trim()));
                        } else {
                            throw new SubmissionFileException("Invalid file id, must be none negative integer: " + part);
                        }
                    }
                }
            }
        }


        // populate all the file mappings
        for (Integer id : idMap.keySet()) {
            DataFile dataFile = fileMap.get(id);
            List<Integer> idMappings = idMap.get(id);
            for (Integer idMapping : idMappings) {
                DataFile mappedDataFile = fileMap.get(idMapping);
                if (mappedDataFile != null) {
                    dataFile.addFileMapping(mappedDataFile);
                } else {
                    throw new SubmissionFileException("Invalid file id, it must related to valid data file: " + idMapping);
                }
            }
        }

        // looking for cyclic referencing  issues
        isCyclicReferencesExists(idMap);

        // add all the data files
        submission.addDataFiles(fileMap.values());
    }

    /**
     * This method checks if there are cyclic referencing in the px summary file.
     *
     * Cyclic referencing means, for example, File ID 1 mapped to ID 2, then for File ID 2, it was being mapped to ID 1.
     * This shouldn't be the case, files shouldn't be related to each other, because then it causes a cyclic relationship.
     * cyclic dependency may course problems when generating the README.txt file (during publication)
     * @param idMap a map with file ID and their related file IDs
     * @return Boolean value - true if a cyclic dependency found
     */
    private static boolean isCyclicReferencesExists(Map<Integer, List<Integer>> idMap){
        boolean isCyclicRefFound = false;
        try {
            for (Map.Entry<Integer, List<Integer>> file : idMap.entrySet())
            {
                for (Integer reference : file.getValue()) {
                    if(idMap.containsKey(reference)) {
                        if(idMap.get(reference).contains(file.getKey())) {
                            isCyclicRefFound = true;
                            StringBuilder errorMsg = new StringBuilder();
                            errorMsg.append("Cyclic Reference found at file " + file.getKey() + " and " +  reference + "\n");
                            errorMsg.append(file.getKey() + "--->" + file.getValue().toString() + "\n");
                            errorMsg.append(reference +  "--->" + idMap.get(reference).toString() + "\n");
                            throw new Exception(errorMsg.toString());
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return isCyclicRefFound;
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

        // trim the string
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
        // create all data file objects
        for (String[] entry : entries) {
            // create a new sample metadata
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
        System.out.println("Submission summary file parsed successfully");
    }
}
