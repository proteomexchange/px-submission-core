package uk.ac.ebi.pride.data.validation;

import com.google.common.base.CharMatcher;
import org.apache.commons.lang3.StringUtils;
import uk.ac.ebi.pride.data.model.*;
import uk.ac.ebi.pride.data.util.Constant;
import uk.ac.ebi.pride.archive.dataprovider.file.ProjectFileType;
import uk.ac.ebi.pride.archive.dataprovider.project.SubmissionType;

import java.io.File;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;

/**
 * This class is used to validate input fields
 * <p/>
 *
 * @author Rui Wang
 * @version $Id$
 *          <p/>
 */
public final class SubmissionValidator {

  private SubmissionValidator() {
  }


  /**
   * Full validation, also checks the existence and access permissions of the data files
   */
  public static ValidationReport validateSubmission(Submission submission) {
    ValidationReport report = validateSubmissionSyntax(submission);
    report.combine(validateDataFiles(submission.getDataFiles()));
    return report;
  }

  /**
   * Validate only submission file schema, this doesn't check the existence and access permissions of the
   * data files
   */
  public static ValidationReport validateSubmissionSyntax(Submission submission) {
    ValidationReport report = new ValidationReport();

    report.combine(validateProjectMetaData(submission.getProjectMetaData()))
        .combine(validateFileMappings(submission))
        .combine(validateSampleMetaData(submission, false));

    return report;
  }

  /**
   * Validate project metadata
   */
  public static ValidationReport validateProjectMetaData(ProjectMetaData projectMetaData) {
    ValidationReport report = new ValidationReport();
    if (projectMetaData == null) {
      report.addMessage(new ValidationMessage(ValidationMessage.Type.ERROR, "Project metadata cannot be empty"));
    } else {
      SubmissionType submissionType = projectMetaData.getSubmissionType();
      report.combine(validateContact(projectMetaData.getSubmitterContact()))
          .combine(validateContact(projectMetaData.getLabHeadContact()))
          .combine(validateProjectTile(projectMetaData.getProjectTitle()))
          .combine(validateProjectDescription(projectMetaData.getProjectDescription()))
          .combine(validateProjectTags(projectMetaData.getProjectTags()))
          .combine(validateSampleProcessingProtocol(projectMetaData.getSampleProcessingProtocol()))
          .combine(validateDataProcessingProtocol(projectMetaData.getDataProcessingProtocol()))
          .combine(validateOtherOmicsLink(projectMetaData.getOtherOmicsLink()))
          .combine(validateExperimentMethods(projectMetaData.getMassSpecExperimentMethods()))
          .combine(validateKeywords(projectMetaData.getKeywords()))
          .combine(validatePubmedIds(projectMetaData.getPubmedIds()))
          .combine(validateDois(projectMetaData.getDois()))
          .combine(validateResubmissionPxAccession(projectMetaData.getResubmissionPxAccession()))
          .combine(validateReanalysisPxAccessions(projectMetaData.getReanalysisAccessions()))
          .combine(validateRpxdOriginalAccessions(projectMetaData.getRpxdOriginalAccessions()))
          .combine(validateAdditional(projectMetaData.getAdditional()))
          .combine(validateSpecies(projectMetaData.getSpecies()))
          .combine(validateTissues(projectMetaData.getTissues()))
          .combine(validateCellTypes(projectMetaData.getCellTypes()))
          .combine(validateDiseases(projectMetaData.getDiseases()))
          .combine(validateInstruments(projectMetaData.getInstruments()))
          .combine(validateModifications(projectMetaData.getModifications(), submissionType))
          .combine(validateQuantifications(projectMetaData.getQuantifications()))
          .combine(validateReasonForPartialSubmission(projectMetaData.getReasonForPartialSubmission(), submissionType));
    }
    return report;
  }

  /**
   * Validate file mappings
   * NOTE: here we only make sure that a file is indeed a file, not a folder
   */
  public static ValidationReport validateFileMappings(Submission submission) {
    ValidationReport report = new ValidationReport();
    List<DataFile> dataFiles = submission.getDataFiles();
    SubmissionType submissionType = submission.getProjectMetaData().getSubmissionType();
    if (dataFiles == null) {
      report.addMessage(new ValidationMessage(ValidationMessage.Type.ERROR, "Data files cannot be empty"));
    } else {
      boolean resultPresent = SubmissionType.RAW.equals(submissionType);
      boolean searchPresent = false;
      boolean rawFilePresent = false;
      for (DataFile dataFile : dataFiles) {
        if ((SubmissionType.COMPLETE.equals(submissionType) && ProjectFileType.RESULT.equals(dataFile.getFileType())) ||
            (SubmissionType.PARTIAL.equals(submissionType) && ProjectFileType.SEARCH.equals(dataFile.getFileType()))) {
          resultPresent = true;
          searchPresent = true;
          if (dataFile.getFileMappings().size() == 0) {
            report.addMessage(new ValidationMessage(dataFile, ValidationMessage.Type.ERROR, "No file mapping detected for file: " + dataFile.getFileId()));
          }
        } else if ((SubmissionType.PRIDE.equals(submissionType) && ProjectFileType.RESULT.equals(dataFile.getFileType()))) {
          resultPresent = true;
          searchPresent = true;
          rawFilePresent = true;
        } else if (rawFilePresent || ProjectFileType.RAW.equals(dataFile.getFileType())) {
          rawFilePresent = true;
        }
      }
      if (!rawFilePresent) {
        report.addMessage(new ValidationMessage(ValidationMessage.Type.ERROR, "Raw files not found"));
      }
      if (!resultPresent) {
        report.addMessage(new ValidationMessage(ValidationMessage.Type.ERROR, "Result files not found"));
      }
      if (!searchPresent) {
        report.addMessage(new ValidationMessage(ValidationMessage.Type.ERROR, "Search files not found"));
      }
    }
    if (!report.hasError() && !report.hasWarning()) {
      report.addMessage(new ValidationMessage(ValidationMessage.Type.SUCCESS, "Data files are valid"));
    }
    return report;
  }

  /**
   * Validate sample metadata
   */
  public static ValidationReport validateSampleMetaData(Submission submission, boolean experimentalFactorOptional) {
    ValidationReport report = new ValidationReport();
    List<DataFile> dataFiles = submission.getDataFiles();
    for (DataFile dataFile : dataFiles) {
      SampleMetaData sampleMetaData = dataFile.getSampleMetaData();
      if (ProjectFileType.RESULT.equals(dataFile.getFileType())) {
        report.combine(validateSampleMetaDataEntry(dataFile, experimentalFactorOptional));
      } else if (sampleMetaData != null) {
        report.addMessage(new ValidationMessage(ValidationMessage.Type.ERROR, "None result file should not contain sample metadata, file Id: " + dataFile.getFileId()));
      }
    }
    if (!report.hasError() && !report.hasWarning()) {
      report.addMessage(new ValidationMessage(ValidationMessage.Type.SUCCESS, "Sample metadata is valid"));
    }
    return report;
  }

  /**
   * Validates a single sample metadata entry
   * @param dataFile input data file.
   * @param experimentalFactorOptional true if the experimental factor is optional, false otherwise.
   * @return the validation report about sample metadata.
   */
  public static ValidationReport validateSampleMetaDataEntry(DataFile dataFile, boolean experimentalFactorOptional) {
    ValidationReport report = new ValidationReport();
    if (dataFile.getFileType().equals(ProjectFileType.RESULT)) {
      SampleMetaData sampleMetaDataEntry = dataFile.getSampleMetaData();
      report.combine(validateSpecies(sampleMetaDataEntry.getMetaData(SampleMetaData.Type.SPECIES)))
          .combine(validateTissues(sampleMetaDataEntry.getMetaData(SampleMetaData.Type.TISSUE)))
          .combine(validateCellTypes(sampleMetaDataEntry.getMetaData(SampleMetaData.Type.CELL_TYPE)))
          .combine(validateDiseases(sampleMetaDataEntry.getMetaData(SampleMetaData.Type.DISEASE)))
          .combine(validateInstruments(sampleMetaDataEntry.getMetaData(SampleMetaData.Type.INSTRUMENT)))
          .combine(validateQuantifications(sampleMetaDataEntry.getMetaData(SampleMetaData.Type.QUANTIFICATION_METHOD)));
      Set<CvParam> experimentalFactor = sampleMetaDataEntry.getMetaData(SampleMetaData.Type.EXPERIMENTAL_FACTOR);
      if (experimentalFactor == null || experimentalFactor.isEmpty()) {
        if (!experimentalFactorOptional) {
          report.addMessage(new ValidationMessage(ValidationMessage.Type.ERROR, "Experimental factor cannot be empty: " + dataFile.getFileId()));
        }
      } else {
        report.combine(validateExperimentalFactor(sampleMetaDataEntry.getMetaData(SampleMetaData.Type.EXPERIMENTAL_FACTOR).iterator().next().getValue()));
      }
    } else {
      report.addMessage(new ValidationMessage(ValidationMessage.Type.ERROR, "Sample metadata entry must have a matching result file: " + dataFile.getFileId()));
    }
    if (!report.hasError() && !report.hasWarning()) {
      report.addMessage(new ValidationMessage(ValidationMessage.Type.SUCCESS, "Sample metadata entry is valid: " + dataFile.getFileId()));
    }
    return report;
  }

  /**
   * Validate contact
   */
  public static ValidationReport validateContact(Contact contact) {
    ValidationReport report = new ValidationReport();
    if (contact == null) {
      report.addMessage(new ValidationMessage(ValidationMessage.Type.ERROR, "Contact cannot be empty"));
    } else {
      report.combine(validateName(contact.getName()))
          .combine(validateEmail(contact.getEmail()))
          .combine(validateAffiliation(contact.getAffiliation()))
          .combine(validateUserName(contact.getUserName()));
    }
    return report;
  }

  /**
   * Validate submitter name
   */
  public static ValidationReport validateName(String name) {
    ValidationReport report = new ValidationReport();
    if (noneEmptyString(name)) {
      report.addMessage(new ValidationMessage(ValidationMessage.Type.SUCCESS, "Submitter name is valid: " + name));
    } else {
      report.addMessage(new ValidationMessage(ValidationMessage.Type.ERROR, "Submitter name is empty"));
    }
    return report;
  }

  /**
   * Validate email address
   */
  public static ValidationReport validateEmail(String email) {
    ValidationReport report = new ValidationReport();
    if (email == null) {
      report.addMessage(new ValidationMessage(ValidationMessage.Type.ERROR, "Email address cannot be null"));
    } else {
      Matcher m = Constant.EMAIL_PATTERN.matcher(email);
      if (m.matches()) {
        report.addMessage(new ValidationMessage(ValidationMessage.Type.SUCCESS, "Email address is valid"));
      } else {
        report.addMessage(new ValidationMessage(ValidationMessage.Type.ERROR, "Email address is invalid: " + email));
      }
    }

    return report;
  }

  /**
   * Validate affiliation
   */
  public static ValidationReport validateAffiliation(String affiliation) {
    ValidationReport report = new ValidationReport();
    if (noneEmptyString(affiliation)) {
      report.addMessage(new ValidationMessage(ValidationMessage.Type.SUCCESS, "Affiliation is valid"));
    } else {
      report.addMessage(new ValidationMessage(ValidationMessage.Type.ERROR, "Affiliation cannot be empty"));
    }
    return report;
  }

  private static ValidationReport validateProjectTags(Set<String> projectTags) {
    ValidationReport report = new ValidationReport();
    for (String projectTag : projectTags) {
      if (noneEmptyString(projectTag)) {
        report.addMessage(new ValidationMessage(ValidationMessage.Type.SUCCESS, "Project tag is valid: " + projectTag));
      } else {
        report.addMessage(new ValidationMessage(ValidationMessage.Type.ERROR, "Project tag cannot be empty"));
      }
    }
    return report;
  }

  /**
   * Validate user name
   */
  public static ValidationReport validateUserName(String userName) {
    ValidationReport report = new ValidationReport();
    if (userName != null) {
      if (noneEmptyString(userName)) {
        report.addMessage(new ValidationMessage(ValidationMessage.Type.SUCCESS, "User name is valid"));
      } else {
        report.addMessage(new ValidationMessage(ValidationMessage.Type.ERROR, "User anme cannot be empty"));
      }
    }
    return report;
  }

  /**
   * Validate project title
   */
  public static ValidationReport validateProjectTile(String title) {
    ValidationReport report = new ValidationReport();
    if (isValidShortString(title)) {
      report.addMessage(new ValidationMessage(ValidationMessage.Type.SUCCESS, "Project title is valid"));
    } else {
      report.addMessage(new ValidationMessage(ValidationMessage.Type.ERROR, "Project title must be less than " + Constant.MAXIMUM_SHORT_STRING_LENGTH + " characters"));
    }
    return report;
  }

  /**
   * Validate project description
   */
  public static ValidationReport validateProjectDescription(String projectDesc) {
    ValidationReport report = new ValidationReport();
    if (isValidLongString(projectDesc)) {
      report.addMessage(new ValidationMessage(ValidationMessage.Type.SUCCESS, "Project description is valid"));
    } else {
      report.addMessage(new ValidationMessage(ValidationMessage.Type.ERROR, "Project description must be both more than " + Constant.MINIMUM_STRING_LENGTH + " and less than " + Constant.MAXIMUM_LONG_STRING_LENGTH + " characters"));
    }
    return report;
  }

  /**
   * Validate sample processing protocol
   */
  public static ValidationReport validateSampleProcessingProtocol(String sampleProtocol) {
    ValidationReport report = new ValidationReport();
    if (isValidLongString(sampleProtocol)) {
      report.addMessage(new ValidationMessage(ValidationMessage.Type.SUCCESS, "Sample processing protocol is valid"));
    } else {
      report.addMessage(new ValidationMessage(ValidationMessage.Type.ERROR, "Sample processing protocol must be both more than " + Constant.MINIMUM_STRING_LENGTH + " and less than " + Constant.MAXIMUM_LONG_STRING_LENGTH + " characters"));
    }
    return report;
  }

  /**
   * Validate data processing protocol
   */
  public static ValidationReport validateDataProcessingProtocol(String dataProtocol) {
    ValidationReport report = new ValidationReport();
    if (isValidLongString(dataProtocol)) {
      report.addMessage(new ValidationMessage(ValidationMessage.Type.SUCCESS, "Data processing protocol is valid"));
    } else {
      report.addMessage(new ValidationMessage(ValidationMessage.Type.ERROR, "Data processing protocol must be both more than " + Constant.MINIMUM_STRING_LENGTH + " and less than " + Constant.MAXIMUM_LONG_STRING_LENGTH + " characters"));
    }
    return report;
  }

  /**
   * Validate other omics link
   */
  public static ValidationReport validateOtherOmicsLink(String otherOmicsLink) {
    ValidationReport report = new ValidationReport();
    if (otherOmicsLink == null || "".equals(otherOmicsLink.trim()) || isValidMediumString(otherOmicsLink)) {
      report.addMessage(new ValidationMessage(ValidationMessage.Type.SUCCESS, "Other omics link is valid"));
    } else {
      report.addMessage(new ValidationMessage(ValidationMessage.Type.ERROR, "Other omics link must be less than " + Constant.MAXIMUM_MEDIUM_STRING_LENGTH + " characters"));
    }
    return report;
  }

  /**
   * Validate experiment methods
   */
  public static ValidationReport validateExperimentMethods(Set<CvParam> expMethods) {
    ValidationReport report = new ValidationReport();
    if (expMethods == null || expMethods.isEmpty()) {
      report.addMessage(new ValidationMessage(ValidationMessage.Type.ERROR, "Experiment methods cannot be empty"));
    } else {
      for (CvParam experimentMethod : expMethods) {
        if (!Constant.PRIDE.equalsIgnoreCase(experimentMethod.getCvLabel()) && !Constant.MS.equalsIgnoreCase(experimentMethod.getCvLabel())) {
          report.addMessage(new ValidationMessage(ValidationMessage.Type.ERROR, "Experiment methods must be defined using a PRIDE CV term: " + experimentMethod.getAccession()));
        }
      }
    }
    if (!report.hasError() && !report.hasWarning()) {
      report.addMessage(new ValidationMessage(ValidationMessage.Type.SUCCESS, "Experiment methods are valid"));
    }
    return report;
  }

  /**
   * Validate keywords
   */
  public static ValidationReport validateKeywords(String keywords) {
    ValidationReport report = new ValidationReport();
    if (noneEmptyString(keywords)) {
      report.addMessage(new ValidationMessage(ValidationMessage.Type.SUCCESS, "Keywords are valid"));
    } else {
      report.addMessage(new ValidationMessage(ValidationMessage.Type.ERROR, "Keywords cannot be empty"));
    }
    return report;
  }

  /**
   * Validate PubMed ids
   */
  public static ValidationReport validatePubmedIds(Set<String> pubmedIds) {
    ValidationReport report = new ValidationReport();
    if (pubmedIds != null) {
      for (String pubmedId : pubmedIds) {
        report.combine(validatePubMedId(pubmedId));
      }
    }
    return report;
  }

  /**
   * Validate DOIs
   */
  public static ValidationReport validateDois(Set<String> dois) {
    ValidationReport report = new ValidationReport();
    if (dois != null) {
      for (String doi : dois) {
        report.combine(validateDoi(doi));
      }
    }

    return report;
  }

  /**
   * Validate resubmission px accession
   */
  public static ValidationReport validateResubmissionPxAccession(String px) {
    ValidationReport report = new ValidationReport();
    if (px != null) {
      if (isValidProjectAccession(px)) {
        report.addMessage(new ValidationMessage(ValidationMessage.Type.SUCCESS, "Resubmission PX accession is valid: " + px));
      } else {
        report.addMessage(new ValidationMessage(ValidationMessage.Type.ERROR, "Resubmission PX accession is invalid: " + px));
      }
    }
    return report;
  }

  /**
   * Validate reanalysis px accessions
   */
  public static ValidationReport validateReanalysisPxAccessions(Collection<String> pxdAccessions) {
    ValidationReport report = new ValidationReport();
    if (pxdAccessions != null) {
      for (String pxdAccession : pxdAccessions) {
        if (isValidProjectAccession(pxdAccession)) {
          report.addMessage(new ValidationMessage(ValidationMessage.Type.SUCCESS, "Reanalysis PX accession is valid: " + pxdAccession));
        } else {
          report.addMessage(new ValidationMessage(ValidationMessage.Type.ERROR, "Reanalysis PX accession is invalid: " + pxdAccession));
        }
      }
    }
    return report;
  }

  /**
   * Validate RPXD original px accessions
   */
  public static ValidationReport validateRpxdOriginalAccessions(Collection<String> pxdAccessions) {
    return validateReanalysisPxAccessions(pxdAccessions);
  }

  /**
   * Validate reason for partial submission
   */
  public static ValidationReport validateReasonForPartialSubmission(String reason, SubmissionType submissionType) {
    ValidationReport report = new ValidationReport();
    if (reason != null) {
      if (submissionType.equals(SubmissionType.PARTIAL)) {
        if (isValidMediumString(reason)) {
          report.addMessage(new ValidationMessage(ValidationMessage.Type.SUCCESS, "Reason for partial submission is valid"));
        } else {
          report.addMessage(new ValidationMessage(ValidationMessage.Type.ERROR, "Reason for partial submission must be less than " + Constant.MAXIMUM_MEDIUM_STRING_LENGTH + " characters"));
        }
      } else {
        report.addMessage(new ValidationMessage(ValidationMessage.Type.ERROR, "Reason for partial submission is only allowed for partial submissions"));
      }
    }
    return report;
  }

  /**
   * Validate species
   */
  public static ValidationReport validateSpecies(Set<? extends Param> species) {
    ValidationReport report = new ValidationReport();
    if (species == null || species.isEmpty()) {
      report.addMessage(new ValidationMessage(ValidationMessage.Type.ERROR, "Species cannot be empty"));
    } else {
      for (Param sp : species) {
        CvParam spCvParam = (CvParam) sp;
        if (!Constant.NEWT.equalsIgnoreCase(spCvParam.getCvLabel())) {
          report.addMessage(new ValidationMessage(ValidationMessage.Type.ERROR, "Species must be defined using NEWT ontology: " + spCvParam.getAccession()));
        }
      }
    }
    if (!report.hasError() && !report.hasWarning()) {
      report.addMessage(new ValidationMessage(ValidationMessage.Type.SUCCESS, "Species are valid"));
    }
    return report;
  }

  /**
   * Validate tissues
   */
  public static ValidationReport validateTissues(Set<? extends Param> tissues) {
    ValidationReport report = new ValidationReport();
    if (tissues == null || tissues.isEmpty()) {
      report.addMessage(new ValidationMessage(ValidationMessage.Type.ERROR, "Tissues cannot be empty"));
    } else {
      for (Param ts : tissues) {
        CvParam tsCvParam = (CvParam) ts;
        String cvLabel = tsCvParam.getCvLabel();
        if (!Constant.BTO.equalsIgnoreCase(cvLabel) && !Constant.PRIDE.equalsIgnoreCase(cvLabel)) {
          report.addMessage(new ValidationMessage(ValidationMessage.Type.ERROR, "Tissues must be defined using BTO ontology: " + tsCvParam.getAccession()));
        }
      }
    }
    if (!report.hasError() && !report.hasWarning()) {
      report.addMessage(new ValidationMessage(ValidationMessage.Type.SUCCESS, "Tissues are valid"));
    }
    return report;
  }

  /**
   * Validate cell types
   */
  public static ValidationReport validateCellTypes(Set<? extends Param> cellTypes) {
    ValidationReport report = new ValidationReport();
    if (cellTypes == null || cellTypes.isEmpty()) {
      report.addMessage(new ValidationMessage(ValidationMessage.Type.WARNING, "Cell types are empty"));
    } else {
      for (Param ct : cellTypes) {
        CvParam ctCvParam = (CvParam) ct;
        if (!Constant.CL.equalsIgnoreCase(ctCvParam.getCvLabel())) {
          report.addMessage(new ValidationMessage(ValidationMessage.Type.ERROR, "Cell type must be defined using CL ontology: " + ctCvParam.getAccession()));
        }
      }
    }
    if (!report.hasError() && !report.hasWarning()) {
      report.addMessage(new ValidationMessage(ValidationMessage.Type.SUCCESS, "Cell types are valid"));
    }
    return report;
  }

  /**
   * Validate diseases
   */
  public static ValidationReport validateDiseases(Set<? extends Param> diseases) {
    ValidationReport report = new ValidationReport();
    if (diseases == null || diseases.isEmpty()) {
      report.addMessage(new ValidationMessage(ValidationMessage.Type.WARNING, "Diseases are empty"));
    } else {
      for (Param de : diseases) {
        CvParam deCvParam = (CvParam) de;
        if (!Constant.DOID.equalsIgnoreCase(deCvParam.getCvLabel())) {
          report.addMessage(new ValidationMessage(ValidationMessage.Type.ERROR, "Disease must be defined using DOID ontology: " + deCvParam.getAccession()));
        }
      }
    }
    if (!report.hasError() && !report.hasWarning()) {
      report.addMessage(new ValidationMessage(ValidationMessage.Type.SUCCESS, "Disease are valid"));
    }
    return report;
  }

  /**
   * Validate instruments
   */
  public static ValidationReport validateInstruments(Set<? extends Param> instruments) {
    ValidationReport report = new ValidationReport();
    if (instruments == null || instruments.isEmpty()) {
      report.addMessage(new ValidationMessage(ValidationMessage.Type.ERROR, "Instruments cannot be empty"));
    } else {
      for (Param instrument : instruments) {
        CvParam instrumentCvParam = (CvParam) instrument;
        if (!Constant.MS.equalsIgnoreCase(instrumentCvParam.getCvLabel()) && !Constant.PRIDE.equalsIgnoreCase(instrumentCvParam.getCvLabel())) {
          report.addMessage(new ValidationMessage(ValidationMessage.Type.ERROR, "Instrument must be defined using either MS or PRIDE ontology: " + instrumentCvParam.getAccession()));
        }
      }
    }
    if (!report.hasError() && !report.hasWarning()) {
      report.addMessage(new ValidationMessage(ValidationMessage.Type.SUCCESS, "Instruments are valid"));
    }
    return report;
  }

  /**
   * Validate modifications
   */
  public static ValidationReport validateModifications(Set<CvParam> mods, SubmissionType submissionType) {
    ValidationReport report = new ValidationReport();
    if (mods == null || mods.isEmpty()) {
      report.addMessage(new ValidationMessage(submissionType.equals(SubmissionType.PARTIAL) ? ValidationMessage.Type.ERROR : ValidationMessage.Type.WARNING, "Modifications cannot be empty"));
    } else {
      for (CvParam mod : mods) {
        String cvLabel = mod.getCvLabel();
        if (!Constant.PSI_MOD.equalsIgnoreCase(cvLabel) && !Constant.UNIMOD.equalsIgnoreCase(cvLabel) && !Constant.PRIDE.equalsIgnoreCase(cvLabel)
            && !(Constant.MS.equalsIgnoreCase(cvLabel) && "MS:1001460".equalsIgnoreCase(mod.getAccession()))) {
          report.addMessage(new ValidationMessage(ValidationMessage.Type.ERROR, "Modification must be defined using PSI MOD, UNIMOD or PRIDE: " + mod.getAccession()));
        }
        if (Constant.PRIDE.equalsIgnoreCase(cvLabel) && Constant.NO_MOD_PRIDE_ACCESSION.equalsIgnoreCase(mod.getAccession()) && mods.size() > 1) {
          report.addMessage(new ValidationMessage(ValidationMessage.Type.ERROR, "None modification must be a single CV term defined using PRIDE ontology: " + mod.getAccession()));
        }
      }
    }
    if (!report.hasError() && !report.hasWarning()) {
      report.addMessage(new ValidationMessage(ValidationMessage.Type.SUCCESS, "Modifications are valid"));
    }
    return report;
  }

  /**
   * Validate quantifications
   */
  public static ValidationReport validateQuantifications(Set<? extends Param> quantifications) {
    ValidationReport report = new ValidationReport();
    if (quantifications == null || quantifications.isEmpty()) {
      report.addMessage(new ValidationMessage(ValidationMessage.Type.WARNING, "Quantifications are empty"));
    } else {
      for (Param quantification : quantifications) {
        CvParam quantCvParam = (CvParam) quantification;
        if (!Constant.PRIDE.equalsIgnoreCase(quantCvParam.getCvLabel()) && !Constant.MS.equalsIgnoreCase(quantCvParam.getCvLabel())) {
          report.addMessage(new ValidationMessage(ValidationMessage.Type.ERROR, "Quantifications musth be defined using either PRIDE ontology or MS ontology: " + quantCvParam.getAccession()));
        }
      }
    }
    if (!report.hasError() && !report.hasWarning()) {
      report.addMessage(new ValidationMessage(ValidationMessage.Type.SUCCESS, "Quantifications are valid"));
    }
    return report;
  }

  /**
   * Validates experimental factor
   *
   * @param expFactor the input experimental factor.
   * @return the validation report about success or error.
   */
  public static ValidationReport validateExperimentalFactor(String expFactor) {
    ValidationReport report = new ValidationReport();
    if (isValidShortString(expFactor)) {
      report.addMessage(new ValidationMessage(ValidationMessage.Type.SUCCESS, "Experimental factor is valid"));
    } else {
      report.addMessage(new ValidationMessage(ValidationMessage.Type.ERROR, "Experimental factor must be less than " + Constant.MAXIMUM_SHORT_STRING_LENGTH + " characters"));
    }
    return report;
  }

  /**
   * Validate additional
   */
  public static ValidationReport validateAdditional(Set<Param> additional) {
    ValidationReport report = new ValidationReport();
    if (additional == null || additional.isEmpty()) {
      report.addMessage(new ValidationMessage(ValidationMessage.Type.WARNING, "Additional fields are empty"));
    } else {
      report.addMessage(new ValidationMessage(ValidationMessage.Type.SUCCESS, "Additional fields are valid"));
    }
    return report;
  }

  public static ValidationReport validateDataFiles(Collection<DataFile> dataFiles) {
    ValidationReport report = new ValidationReport();
    for (DataFile dataFile : dataFiles) {
      report.combine(validateDataFile(dataFile));
    }
    return report;
  }

  /**
   * Validate data file
   * <p/>
   * NOTE: here we check file existence and file read permission, AND, we also check that the file name is portable
   */
  public static ValidationReport validateDataFile(DataFile dataFile) {
    ValidationReport report = new ValidationReport();
    validateDataFile(dataFile, report);
    return report;
  }

  private static void validateDataFile(DataFile dataFile, ValidationReport report) {
    if (dataFile == null) {
      report.addMessage(new ValidationMessage(ValidationMessage.Type.ERROR, "Data file cannot be empty"));
    } else {
      if (dataFile.isFile()) {
        File actualFile = dataFile.getFile();
        if (!actualFile.isFile()) {
          report.addMessage(new ValidationMessage(ValidationMessage.Type.ERROR, "Data file is not a file: " + actualFile.getAbsolutePath()));
        } else if (!actualFile.exists()) {
          report.addMessage(new ValidationMessage(ValidationMessage.Type.ERROR, "Data file doesn't exist: " + actualFile.getAbsolutePath()));
        } else if (!actualFile.canRead()) {
          report.addMessage(new ValidationMessage(ValidationMessage.Type.ERROR, "No read permission on data file: " + actualFile.getAbsolutePath()));
        } else if (actualFile.length() <= 0) {
          report.addMessage(new ValidationMessage(ValidationMessage.Type.ERROR, "Data file is empty: " + actualFile.getAbsolutePath()));
        }
        // Check that the file name is portable
        if (!CharMatcher.ASCII.matchesAllOf(dataFile.getFileName())) {
          // Not all characters in the file name are part of the ASCII standard
          // NOTE - This artifact is used by the px-submission-tool (and probably no one else), where strings
          // used in messages, specially those used on the GUI, have been externalized to a configuration
          // file, but not getting to the point of "internationalization". In the case of this artifact,
          // pretty much all the strings are hard coded... hurray!
          // WARNING - I know this check is not very strict, and it's not picky in terms of checking that for
          // the file name a subset of the POSIX definition of "portable character set"
          // (https://en.wikipedia.org/wiki/Portable_character_set) has been used in a strict way, but that's
          // because, when you reach this point, it means the file name can be used in a PATH, as the file can
          // be opened and read, i.e. it already is compatible with at least one file system, now we just need
          // to check that the characters that have been used to write the file name, are part of ASCII
          // charset.
          report.addMessage(new ValidationMessage(ValidationMessage.Type.ERROR, "NON-STANDARD CHARSET used in file name '" + actualFile.getName() + "'"));
        }
      } else if (!dataFile.isUrl()) {
        report.addMessage(new ValidationMessage(ValidationMessage.Type.ERROR, "Data file is not a file: " + dataFile.getFileId()));
      } else if (StringUtils.isEmpty(dataFile.getRpxdOriginalAccession())) {
        report.addMessage(new ValidationMessage(ValidationMessage.Type.ERROR, "Data file is not a file: " + dataFile.getFileId()));
      }
      if (!report.hasError() && !report.hasWarning()) {
        report.addMessage(new ValidationMessage(ValidationMessage.Type.SUCCESS, "Data file is valid: " + dataFile.getFileId()));
      }
    }
  }

  /**
   * Validate pubmed id
   */
  public static ValidationReport validatePubMedId(String id) {
    ValidationReport report = new ValidationReport();
    Matcher m = Constant.PUBMED_PATTERN.matcher(id);
    if (m.matches()) {
      report.addMessage(new ValidationMessage(ValidationMessage.Type.SUCCESS, "PubMed is valid: " + id));
    } else {
      report.addMessage(new ValidationMessage(ValidationMessage.Type.ERROR, "PubMed is invalid: " + id));
    }
    return report;
  }

  /**
   * Validate doi
   */
  public static ValidationReport validateDoi(String doi) {
    ValidationReport report = new ValidationReport();
    Matcher m = Constant.DOI_PATTERN.matcher(doi);
    if (m.lookingAt()) {
      report.addMessage(new ValidationMessage(ValidationMessage.Type.SUCCESS, "DOI is valid: " + doi));
    } else {
      report.addMessage(new ValidationMessage(ValidationMessage.Type.ERROR, "DOI is invalid: " + doi));
    }
    return report;
  }

  public static ValidationReport validatePxAccession(String px) {
    ValidationReport report = new ValidationReport();
    if (isValidProjectAccession(px)) {
      report.addMessage(new ValidationMessage(ValidationMessage.Type.SUCCESS, "ProteomeXchange accession is valid: " + px));
    } else {
      report.addMessage(new ValidationMessage(ValidationMessage.Type.SUCCESS, "ProteomeXchange accession is invalid" + px));
    }
    return report;
  }

  /**
   * Validate px accession
   */
  private static boolean isValidProjectAccession(String accession) {
    Matcher pxMatcher = Constant.PX_PATTERN.matcher(accession);
    Matcher pxTestMatcher = Constant.PX_TEST_PATTERN.matcher(accession);
    Matcher prideMatcher = Constant.PRIDE_PATTERN.matcher(accession);
    Matcher prideTestMatcher = Constant.PRIDE_TEST_PATTERN.matcher(accession);
    Matcher rpxMatcher = Constant.RPX_PATTERN.matcher(accession);
    return pxMatcher.matches() || pxTestMatcher.matches() || prideMatcher.matches() || prideTestMatcher.matches() || rpxMatcher.matches();
  }

  /**
   * Validate password
   */
  public static ValidationReport validatePassword(char[] password) {
    ValidationReport report = new ValidationReport();
    if (password != null && password.length > 0) {
      report.addMessage(new ValidationMessage(ValidationMessage.Type.SUCCESS, "Password is valid"));
    } else {
      report.addMessage(new ValidationMessage(ValidationMessage.Type.ERROR, "Password is invalid"));
    }
    return report;
  }

  /**
   * Validate a short string
   *
   * @param str string to be validated
   * @return boolean true means valid
   */
  private static boolean isValidShortString(String str) {
    return noneEmptyString(str) && str.length() < Constant.MAXIMUM_SHORT_STRING_LENGTH;
  }

  /**
   * Validate a medium string
   *
   * @param str string to be validated
   * @return boolean true means valid
   */
  private static boolean isValidMediumString(String str) {
    return noneEmptyString(str) && str.length() < Constant.MAXIMUM_MEDIUM_STRING_LENGTH;
  }

  /**
   * Validate a long string
   *
   * @param str string to be validated
   * @return boolean true means valid
   */
  private static boolean isValidLongString(String str) {
    return noneEmptyString(str) && str.length() > Constant.MINIMUM_STRING_LENGTH && str.length() < Constant.MAXIMUM_LONG_STRING_LENGTH;
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
