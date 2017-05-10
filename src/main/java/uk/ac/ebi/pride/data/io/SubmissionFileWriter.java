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
      writer = new PrintWriter(file, "UTF-8");
      ProjectMetaData projectMetaData = submission.getProjectMetaData();
      writeGeneralProjectMetaData(writer, projectMetaData);
      writeSampleProjectMetaData(writer, projectMetaData);
      writer.println();
      writeFileMappings(writer, submission.getDataFiles());
      writer.println();
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
    writeProjectMetaData(writer, Constant.SUBMITTER_NAME, projectMetaData.getSubmitterContact().getName());
    writeProjectMetaData(writer, Constant.SUBMITTER_EMAIL, projectMetaData.getSubmitterContact().getEmail());
    writeProjectMetaData(writer, Constant.SUBMITTER_AFFILIATION, projectMetaData.getSubmitterContact().getAffiliation());
    writeProjectMetaData(writer, Constant.SUBMITTER_USER_NAME, projectMetaData.getSubmitterContact().getUserName());
    Contact labHeadContact = projectMetaData.getLabHeadContact();
    if (labHeadContact != null) {
      writeProjectMetaData(writer, Constant.LAB_HEAD_NAME, labHeadContact.getName());
      writeProjectMetaData(writer, Constant.LAB_HEAD_EMAIL, labHeadContact.getEmail());
      writeProjectMetaData(writer, Constant.LAB_HEAD_AFFILIATION, labHeadContact.getAffiliation());
    }
    writeProjectMetaData(writer, Constant.PROJECT_TITLE, projectMetaData.getProjectTitle());
    writeProjectMetaData(writer, Constant.PROJECT_DESC, projectMetaData.getProjectDescription());
    writeProjectMetaData(writer, Constant.PROJECT_TAG, projectMetaData.getProjectTags(), false);
    writeProjectMetaData(writer, Constant.KEYWORDS, projectMetaData.getKeywords());
    writeProjectMetaData(writer, Constant.SAMPLE_PROCESSING_PROTOCOL, projectMetaData.getSampleProcessingProtocol());
    writeProjectMetaData(writer, Constant.DATA_PROCESSING_PROTOCOL, projectMetaData.getDataProcessingProtocol());
    if (projectMetaData.hasOtherOmicsLink()) {
      writeProjectMetaData(writer, Constant.OTHER_OMICS_LINK, projectMetaData.getOtherOmicsLink());
    }
    writeProjectMetaData(writer, Constant.EXPERIMENT_TYPE, projectMetaData.getMassSpecExperimentMethods(), false);
    writeProjectMetaData(writer, Constant.SUBMISSION_TYPE, projectMetaData.getSubmissionType().toString());
    if (projectMetaData.hasPubmedIds()) {
      writeProjectMetaData(writer, Constant.PUBMED_ID, projectMetaData.getPubmedIds(), false);
    }
    if (projectMetaData.hasDois()) {
      writeProjectMetaData(writer, Constant.DOI, projectMetaData.getDois(), false);
    }
    if (projectMetaData.isResubmission()) {
      writeProjectMetaData(writer, Constant.RESUBMISSION_PX_ACCESSION, projectMetaData.getResubmissionPxAccession());
    }
    if (projectMetaData.hasReanalysisPxAccessions()) {
      writeProjectMetaData(writer, Constant.REANALYSIS_PX_ACCESSION, projectMetaData.getReanalysisAccessions(), false);
    }
    if (projectMetaData.hasRpxdOriginalPxAccessions()) {
      writeProjectMetaData(writer, Constant.RPXD_ORIGINAL_ACCESSION, projectMetaData.getRpxdOriginalAccessions(), false);
    }
    if (projectMetaData.hasAdditional()) {
      writeProjectMetaData(writer, Constant.ADDITIONAL, projectMetaData.getAdditional(), false);
    }
  }

  /**
   * Write project level metadata for partial submission
   */
  private static void writeSampleProjectMetaData(PrintWriter writer, ProjectMetaData projectMetaData) {
    writeProjectMetaData(writer, Constant.REASON_FOR_PARTIAL, projectMetaData.getReasonForPartialSubmission());
    writeProjectMetaData(writer, Constant.SPECIES, projectMetaData.getSpecies(), false);
    writeProjectMetaData(writer, Constant.TISSUE, projectMetaData.getTissues(), false);
    writeProjectMetaData(writer, Constant.CELL_TYPE, projectMetaData.getCellTypes(), false);
    writeProjectMetaData(writer, Constant.DISEASE, projectMetaData.getDiseases(), false);
    writeProjectMetaData(writer, Constant.INSTRUMENT, projectMetaData.getInstruments(), false);
    writeProjectMetaData(writer, Constant.MODIFICATION, projectMetaData.getModifications(), false);
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
    String species = "";
    if (metaData.hasMetaData(SampleMetaData.Type.SPECIES)) {
      species = castToString(Constant.COMMA, metaData.getMetaData(SampleMetaData.Type.SPECIES).toArray());
    }
    String tissues = "";
    if (metaData.hasMetaData(SampleMetaData.Type.TISSUE)) {
      tissues = castToString(Constant.COMMA, metaData.getMetaData(SampleMetaData.Type.TISSUE).toArray());
    }
    String cellTypes = "";
    if (metaData.hasMetaData(SampleMetaData.Type.CELL_TYPE)) {
      cellTypes = castToString(Constant.COMMA, metaData.getMetaData(SampleMetaData.Type.CELL_TYPE).toArray());
    }
    String diseases = "";
    if (metaData.hasMetaData(SampleMetaData.Type.DISEASE)) {
      diseases = castToString(Constant.COMMA, metaData.getMetaData(SampleMetaData.Type.DISEASE).toArray());
    }
    String modification = "";
    if (metaData.hasMetaData(SampleMetaData.Type.MODIFICATION)) {
      modification = castToString(Constant.COMMA, metaData.getMetaData(SampleMetaData.Type.MODIFICATION).toArray());
    }
    String instruments = "";
    if (metaData.hasMetaData(SampleMetaData.Type.INSTRUMENT)) {
      instruments = castToString(Constant.COMMA, metaData.getMetaData(SampleMetaData.Type.INSTRUMENT).toArray());
    }
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
    for (DataFile dataFile : dataFiles) {
      if (dataFile.isUrl()) {
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
    String type = dataFile.getFileType().name();
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
    StringBuilder mappings = new StringBuilder();
    if (dataFile.hasMappings()) {
      for (DataFile file : dataFile.getFileMappings()) {
        mappings.append(file.getFileId()).append(Constant.COMMA);
      }
      mappings = new StringBuilder(mappings.substring(0, mappings.length() - 1));
    }
    String prideAccession = "";
    if (dataFile.getAssayAccession() != null) {
      prideAccession = dataFile.getAssayAccession();
    }
    StringBuilder sb = new StringBuilder(castToString(Constant.TAB, Constant.FILE_MAPPING_ENTRY, dataFile.getFileId(),
        type, path, mappings.toString()));
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
    String result = content.replace(Constant.TAB, " ");
    result = result.replace(Constant.WIN_LINE_SEPARATOR, " ");
    result = result.replace(Constant.LINUX_LINE_SEPARATOR, " ");
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
    String str = builder.toString();
    if (values.length > 0) {
      str = str.substring(0, str.length() - separator.length());
    }
    return str;
  }
}
