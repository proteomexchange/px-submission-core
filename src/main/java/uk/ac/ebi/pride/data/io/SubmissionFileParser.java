package uk.ac.ebi.pride.data.io;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.ac.ebi.pride.data.exception.SubmissionFileException;
import uk.ac.ebi.pride.data.model.*;
import uk.ac.ebi.pride.data.util.Constant;
import uk.ac.ebi.pride.data.util.MassSpecFileType;
import uk.ac.ebi.pride.data.util.SubmissionType;
import uk.ac.ebi.pride.util.NumberUtilities;

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

                    // check whether it is a valid file format
                    if (!isMetaData && !isFileMapping) {
                        String msg = "Unrecognised submission file section, it is neither metadata nor file mapping: " + file.getAbsolutePath();
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
                    } else {
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
                    }
                }
            }

            // parse meta data
            parseMetadata(submission, metadata);

            // parse file mappings
            parseFileMapping(submission, fileMappingHeaders, fileMappings);

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
    private static void parseMetadata(Submission submission, List<String[]> metadata) {

        // get contact
        Contact contact = submission.getContact();
        // get metadata
        MetaData metaData = submission.getMetaData();

        for (String[] entry : metadata) {
            // save meta data
            String type = entry[1];
            String value = entry[2].trim();
            if (Constant.NAME.equals(type)) {
                // first name
                contact.setName(value);
            } else if (Constant.EMAIL.equals(type)) {
                // email
                contact.setEmail(value);
            } else if (Constant.AFFILIATION.equals(type)) {
                // affiliation
                contact.setAffiliation(value);
            } else if (Constant.EXPERIMENT_TITLE.equals(type)) {
                // title
                metaData.setTitle(value);
            } else if (Constant.EXPERIMENT_DESC.equals(type)) {
                // experiment description
                metaData.setDescription(value);
            } else if (Constant.KEYWORDS.equals(type)) {
                // keywords
                metaData.setKeywords(value);
            } else if (Constant.SUBMISSION_TYPE.equals(type)) {
                // submission type
                if (value.toLowerCase().equals(SubmissionType.SUPPORTED.toString().toLowerCase())) {
                    metaData.setSubmissionType(SubmissionType.SUPPORTED);
                } else if (value.toLowerCase().equals(SubmissionType.UNSUPPORTED.toString().toLowerCase())) {
                    metaData.setSubmissionType(SubmissionType.UNSUPPORTED);
                } else if (value.toLowerCase().equals(SubmissionType.RAW_ONLY.toString().toLowerCase())) {
                    metaData.setSubmissionType(SubmissionType.RAW_ONLY);
                }
            } else if (Constant.COMMENT.equals(type)) {
                // comment
                metaData.setComment(value);
            } else if (Constant.SPECIES.equals(type)) {
                // supported
                metaData.addSpecies(createCvParam(value));
            } else if (Constant.INSTRUMENT.equals(type)) {
                // instrument
                List<CvParam> instrument = parseInstrument(value);
                metaData.addInstrument(instrument);
            } else if (Constant.MODIFICATION.equals(type)) {
                // modification
                metaData.addModification(createCvParam(value));
            } else if (Constant.ADDITIONAL.equals(type)) {
                // additional
                metaData.addAdditional(createCvParam(value));
            } else if (Constant.PUBMED_ID.equals(type)) {
                // pubmed ids
                metaData.addPubmedId(value);
            } else if (Constant.RESUBMISSION_PX_ACCESSION.equals(type)) {
                metaData.setResubmissionPxAccession(value);
            } else if (Constant.REANALYSIS_PX_ACCESSION.equals(type)) {
                // PX accessions
                metaData.addReanalysisPxAccession(value);
            } else if (Constant.PRIDE_LOGIN.equals(type)) {
                // pride login
                contact.setUserName(value);
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
        // get file mappings
        List<DataFile> dataFiles = submission.getDataFiles();

        // create all the objects and mappings

        // map to store the mapping between file id and data file object
        Map<Integer, DataFile> fileMap = new LinkedHashMap<Integer, DataFile>();

        // map to store the mapping between files
        Map<Integer, List<Integer>> idMap = new HashMap<Integer, List<Integer>>();

        // get the index of all the values
        int idIndex = -1, typeIndex = -1, pathIndex = -1, mappingIndex = -1, prideAccIndex = -1;
        for (int i = 0; i < headers.length; i++) {
            String header = headers[i].trim();
            if (Constant.FILE_ID.equals(header)) {
                // file id
                idIndex = i;
            } else if (Constant.FILE_TYPE.equals(header)) {
                // file type
                typeIndex = i;
            } else if (Constant.FILE_PATH.equals(header)) {
                // file path
                pathIndex = i;
            } else if (Constant.FILE_MAPPING.equals(header)) {
                // file mapping
                mappingIndex = i;
            } else if (Constant.PRIDE_ACCESSION.equals(header)) {
                // pride accession
                prideAccIndex = i;
            }
        }

        // create all data file objects
        for (String[] entry : entries) {
            // validate the file id
            String idStr = entry[idIndex].trim();
            if (!NumberUtilities.isNonNegativeInteger(idStr)) {
                throw new SubmissionFileException("Invalid file id, must be none negative integer: " + idStr);
            }
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
            }

            // validate the file type
            String fileType = entry[typeIndex].trim();
            MassSpecFileType type;
            if (Constant.RESULT_FILE_TYPE.equals(fileType)) {
                type = MassSpecFileType.RESULT;
            } else if (Constant.RAW_FILE_TYPE.equals(fileType)) {
                type = MassSpecFileType.RAW;
            } else if (Constant.SEARCH_ENGINE_FILE_TYPE.equals(fileType)) {
                type = MassSpecFileType.SEARCH;
            } else if (Constant.PEAK_LIST_FILE_TYPE.equals(fileType)) {
                type = MassSpecFileType.PEAK;
            } else if (Constant.OTHER_FILE_TYPE.equals(fileType)) {
                type = MassSpecFileType.OTHER;
            } else {
                throw new SubmissionFileException("Invalid file type: " + fileType);
            }

            // pride accession, this is optional
            String prideAccession = null;
            if (prideAccIndex != -1 && entry.length>prideAccIndex) {
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
                        if (NumberUtilities.isNonNegativeInteger(part)) {
                            idList.add(new Integer(part));
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

        // add all the data files
        dataFiles.addAll(fileMap.values());
    }


    /**
     * Parse all the cv params for an instrument
     */
    private static List<CvParam> parseInstrument(String str) {
        String parts[] = str.split(Constant.COMMA + "\\" + Constant.CV_START);
        List<CvParam> instrument = new ArrayList<CvParam>();
        instrument.add(createCvParam(parts[0]));

        if (parts.length > 1) {
            for (int i = 1; i < parts.length; i++) {
                String part = parts[i];
                instrument.add(createCvParam(Constant.CV_START + part));
            }
        }

        return instrument;
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
}
