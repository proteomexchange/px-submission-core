package uk.ac.ebi.pride.data.validation;

import uk.ac.ebi.pride.data.model.*;
import uk.ac.ebi.pride.data.util.Constant;
import uk.ac.ebi.pride.data.util.MassSpecFileType;
import uk.ac.ebi.pride.data.util.SubmissionType;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.regex.Matcher;

/**
 * This class is used to validate input fields
 * <p/>
 *
 * @author Rui Wang
 * @version $Id$
 */
public class SubmissionValidator {


    private SubmissionValidator() {
    }

    /**
     * Validate a submission object
     *
     * @param submission given submission object
     * @return boolean true means valid
     */
    public static boolean isValidSubmission(Submission submission) {
        return isValidContact(submission.getContact()) && isValidMetaData(submission.getMetaData()) && isValidFileMappings(submission.getDataFiles(), submission.getMetaData().getSubmissionType());
    }

    /**
     * Validate contact
     *
     * @param contact given contact
     * @return boolean true means valid
     */
    public static boolean isValidContact(Contact contact) {
        if (contact == null) {
            return false;
        } else {
            return isValidName(contact.getName()) &&
                    isValidEmail(contact.getEmail()) &&
                    isValidAffiliation(contact.getAffiliation()) &&
                    isValidPrideUserName(contact.getUserName());
        }
    }

    /**
     * Validate meta data
     *
     * @param metaData given metadata
     * @return boolean true means valid
     */
    public static boolean isValidMetaData(MetaData metaData) {
        if (metaData == null) {
            return false;
        } else if (SubmissionType.SUPPORTED.equals(metaData.getSubmissionType())) {
            return isValidExperimentTitle(metaData.getTitle()) &&
                    isValidExperimentDescription(metaData.getDescription()) &&
                    isValidKeywords(metaData.getKeywords()) &&
                    isValidPubMedIds(metaData.getPubmedIds()) &&
                    isValidResubmissionPxAccessions(metaData.getResubmissionPxAccession()) &&
                    isValidReanalysisPxAccessions(metaData.getReanalysisAccessions());
        } else {
            return isValidExperimentTitle(metaData.getTitle()) &&
                    isValidExperimentDescription(metaData.getDescription()) &&
                    isValidKeywords(metaData.getKeywords()) &&
                    isValidComment(metaData.getComment()) &&
                    isValidSpecies(metaData.getSpecies()) &&
                    isValidInstruments(metaData.getInstruments()) &&
                    isValidModifications(metaData.getModifications()) &&
                    isValidPubMedIds(metaData.getPubmedIds()) &&
                    isValidReanalysisPxAccessions(metaData.getReanalysisAccessions());
        }
    }

    /**
     * Validate data file
     *
     * @param dataFile data file
     * @return boolean true means data file is valid
     */
    public static boolean isValidDataFile(DataFile dataFile) {
        if (dataFile == null) {
            return false;
        }

        if (dataFile.isFile()) {
            // check whether file exist
            File actualFile = dataFile.getFile();
            return actualFile.isFile() && actualFile.exists() && actualFile.canRead() && actualFile.length() > 0;
        } else {
            // todo: some URL validation need to be done here
            return true;
        }
    }

    /**
     * Validate file mappings
     *
     * @param dataFiles a list of file mappings
     * @return boolean true means valid
     */
    public static boolean isValidFileMappings(List<DataFile> dataFiles, SubmissionType submissionType) {
        if (dataFiles == null) {
            return false;
        } else {
            boolean validMappings = true;
            boolean validPath = true;
            boolean resultPresent = SubmissionType.RAW_ONLY.equals(submissionType);
            boolean rawFilePresent = false;
            for (DataFile dataFile : dataFiles) {
                if ((SubmissionType.SUPPORTED.equals(submissionType) && MassSpecFileType.RESULT.equals(dataFile.getFileType())) ||
                        (SubmissionType.UNSUPPORTED.equals(submissionType) && MassSpecFileType.SEARCH.equals(dataFile.getFileType()))) {
                    resultPresent = true;
                    if (dataFile.getFileMappings().size() == 0) {
                        validMappings = false;
                    }
                } else if (rawFilePresent || MassSpecFileType.RAW.equals(dataFile.getFileType())) {
                    rawFilePresent = true;
                }

                // check whether the path is valid
                File file = dataFile.getFile();
                if (file == null) {
                    if (dataFile.getUrl() == null) {
                        validPath = false;
                    }
                } else if (!file.isFile()) {
                    validPath = false;
                }
            }
            return validMappings && validPath && rawFilePresent && resultPresent;
        }
    }

    /**
     * Validate file mappings and return a list of validation messages
     *
     * @param dataFiles a list of data files
     * @param submissionType    px submission type
     * @return  a list of submission validation mesage
     */
    public static List<SubmissionValidationMessage> validateFileMappings(List<DataFile> dataFiles, SubmissionType submissionType) {
        List<SubmissionValidationMessage> submissionValidationMessages = new ArrayList<SubmissionValidationMessage>();

        if (dataFiles == null) {
            // when datafiles are empty
            SubmissionValidationMessage noDataFiles = new SubmissionValidationMessage(SubmissionValidationMessage.Type.ERROR, "Data files are not present");
            submissionValidationMessages.add(noDataFiles);
        } else {
            boolean validPath = true;
            boolean resultPresent = SubmissionType.RAW_ONLY.equals(submissionType);
            boolean rawFilePresent = false;

            for (DataFile dataFile : dataFiles) {
                if ((SubmissionType.SUPPORTED.equals(submissionType) && MassSpecFileType.RESULT.equals(dataFile.getFileType())) ||
                        (SubmissionType.UNSUPPORTED.equals(submissionType) && MassSpecFileType.SEARCH.equals(dataFile.getFileType()))) {
                    resultPresent = true;
                    if (dataFile.getFileMappings().size() == 0) {
                        SubmissionValidationMessage message = new SubmissionValidationMessage(dataFile, SubmissionValidationMessage.Type.ERROR, "No file mapping detected for file: " + dataFile.getId());
                        submissionValidationMessages.add(message);
                    }
                } else if (rawFilePresent || MassSpecFileType.RAW.equals(dataFile.getFileType())) {
                    rawFilePresent = true;
                }

                // check whether the path is valid
                File file = dataFile.getFile();
                if (file == null) {
                    if (dataFile.getUrl() == null) {
                        validPath = false;
                    }
                } else if (!file.isFile()) {
                    validPath = false;
                }

                if (!validPath) {
                    SubmissionValidationMessage message = new SubmissionValidationMessage(dataFile, SubmissionValidationMessage.Type.ERROR, "Path is not valid for file: " + dataFile.getId());
                    submissionValidationMessages.add(message);
                }
            }

            if (!rawFilePresent) {
                SubmissionValidationMessage message = new SubmissionValidationMessage(SubmissionValidationMessage.Type.ERROR, "Raw files not found");
                submissionValidationMessages.add(message);
            }

            if (!resultPresent) {
                SubmissionValidationMessage message = new SubmissionValidationMessage(SubmissionValidationMessage.Type.ERROR, "Result files not found");
                submissionValidationMessages.add(message);
            }
        }

        return submissionValidationMessages;
    }

    /**
     * Validate first name
     *
     * @param name name
     * @return boolean true means valid
     */
    public static boolean isValidName(String name) {
        return noneEmptyString(name);
    }

    /**
     * Validate email
     *
     * @param email email address
     * @return boolean true means valid
     */
    public static boolean isValidEmail(String email) {
        Matcher m = Constant.EMAIL_PATTERN.matcher(email);
        return m.matches();
    }

    /**
     * Validate affiliation
     *
     * @param affiliation affiliation
     * @return boolean true means valid
     */
    public static boolean isValidAffiliation(String affiliation) {
        return noneEmptyString(affiliation);
    }

    /**
     * Validate experiment title
     *
     * @param title experiment title
     * @return boolean true means valid
     */
    public static boolean isValidExperimentTitle(String title) {
        return noneEmptyString(title);
    }

    /**
     * Validate experiment description
     *
     * @param desc experiment description
     * @return boolean true means valid
     */
    public static boolean isValidExperimentDescription(String desc) {
        return noneEmptyString(desc) && desc.length() > 20;
    }

    /**
     * Validate a collection of keywords
     *
     * @param keywords a collection of keywords
     * @return boolean true means valid
     */
    public static boolean isValidKeywords(String keywords) {
        return noneEmptyString(keywords);
    }

    /**
     * Validate unsupported comment
     *
     * @param comment user comment
     * @return boolean true means valid
     */
    public static boolean isValidComment(String comment) {
        return noneEmptyString(comment);
    }

    /**
     * Validate species
     *
     * @param species a list of species
     * @return boolean true means valid
     */
    public static boolean isValidSpecies(List<CvParam> species) {
        if (species == null || species.isEmpty()) {
            return false;
        } else {
            for (CvParam sp : species) {
                if (!Constant.NEWT.equals(sp.getCvLabel())) {
                    return false;
                }
            }
            return true;
        }
    }

    /**
     * Validate instruments
     *
     * @param instruments a list of instruments
     * @return boolean true means valid
     */
    public static boolean isValidInstruments(List<List<CvParam>> instruments) {
        if (instruments == null || instruments.isEmpty()) {
            return false;
        } else {
            for (List<CvParam> instrument : instruments) {
                if (instrument.size() == 0) {
                    return false;
                }
            }
            return true;
        }
    }

    /**
     * Validate modifications
     *
     * @param modifications a list of modifications
     * @return boolean true means valid
     */
    public static boolean isValidModifications(List<CvParam> modifications) {
        if (modifications == null || modifications.isEmpty()) {
            return false;
        } else {
            for (CvParam mod : modifications) {
                String cvLabel = mod.getCvLabel();
                if (!Constant.PSI_MOD.equals(cvLabel) && !Constant.UNIMOD.equals(cvLabel) && !Constant.PRIDE.equals(cvLabel)) {
                    return false;
                }

                if (Constant.PRIDE.equals(cvLabel) && Constant.NO_MOD_PRIDE_ACCESSION.equals(mod.getAccession()) && modifications.size() > 1) {
                    return false;
                }
            }
        }

        return true;
    }

    /**
     * Validate a collection pubmed ids
     *
     * @param ids pubmed ids
     * @return boolean true means valid
     */
    public static boolean isValidPubMedIds(Collection<String> ids) {
        boolean valid = true;
        if (ids != null) {
            for (String id : ids) {
                valid = valid && isValidPubMedId(id);
            }
        }
        return valid;
    }

    /**
     * Validate pubmed id
     *
     * @param id pubmed id
     * @return boolean true means valid
     */
    public static boolean isValidPubMedId(String id) {
        Matcher m = Constant.PUBMED_PATTERN.matcher(id);
        return m.matches();
    }

    /**
     * @param px
     * @return
     */
    public static boolean isValidResubmissionPxAccessions(String px) {
        boolean valid = true;

        if (px != null) {
            valid = isValidPxAccession(px);
        }

        return valid;
    }

    /**
     * Validate a collection of px accessions
     *
     * @param pxs a collection of px accessions
     * @return boolean true means valid
     */
    public static boolean isValidReanalysisPxAccessions(Collection<String> pxs) {
        boolean valid = true;
        if (pxs != null) {
            for (String px : pxs) {
                valid = valid && isValidPxAccession(px);
            }
        }
        return valid;
    }

    /**
     * Validate px accession
     *
     * @param px px accession
     * @return boolean true means valid
     */
    public static boolean isValidPxAccession(String px) {
        Matcher pxMatcher = Constant.PX_PATTERN.matcher(px);
        Matcher pxTestMatcher = Constant.PX_TEST_PATTERN.matcher(px);
        return pxMatcher.matches() || pxTestMatcher.matches();
    }

    /**
     * Validate pride user name
     *
     * @param username pride user name
     * @return boolean true means valid
     */
    public static boolean isValidPrideUserName(String username) {
        return noneEmptyString(username);
    }

    /**
     * Validate password
     *
     * @param password given password
     * @return boolean true means valid
     */
    public static boolean isValidPassword(char[] password) {
        return password != null && password.length > 0;
    }

    /**
     * Check whether a given string is empty or null
     *
     * @param str input string
     * @return boolean true means empty or null
     */
    private static boolean noneEmptyString(String str) {
        return str != null && str.trim().length() != 0;
    }
}
