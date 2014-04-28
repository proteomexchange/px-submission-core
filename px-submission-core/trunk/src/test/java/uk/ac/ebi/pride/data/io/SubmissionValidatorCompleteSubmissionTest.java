package uk.ac.ebi.pride.data.io;

import org.junit.Before;
import org.junit.Test;
import uk.ac.ebi.pride.data.model.CvParam;
import uk.ac.ebi.pride.data.model.Submission;
import uk.ac.ebi.pride.data.validation.SubmissionValidator;
import uk.ac.ebi.pride.data.validation.ValidationReport;
import uk.ac.ebi.pride.prider.dataprovider.project.SubmissionType;

import java.io.File;
import java.net.URL;
import java.util.Set;

import static org.junit.Assert.assertEquals;

/**
 * @author Rui Wang
 * @version $Id$
 */
public class SubmissionValidatorCompleteSubmissionTest {
    private File inputFile;

    @Before
    public void setUp() throws Exception {
        URL url = SubmissionFileParser.class.getClassLoader().getResource("submissionFile.px");
        if (url == null) {
            throw new IllegalStateException("no file for input found!");
        }
        inputFile = new File(url.toURI());

    }

    @Test
    public void metaDataIsValid() throws Exception {
        Submission submission = SubmissionFileParser.parse(inputFile);
        ValidationReport report = SubmissionValidator.validateProjectMetaData(submission.getProjectMetaData());
        assertEquals(true, report.hasSuccess());
        assertEquals(false, report.hasError());
    }

    @Test
    public void fileMappingsAreValid() throws Exception {
        Submission submission = SubmissionFileParser.parse(inputFile);
        ValidationReport report = SubmissionValidator.validateFileMappings(submission);
        assertEquals(true, report.hasSuccess());
        assertEquals(false, report.hasError());
    }

    @Test
    public void sampleMetadataIsValid() throws Exception {
        Submission submission = SubmissionFileParser.parse(inputFile);
        ValidationReport report = SubmissionValidator.validateSampleMetaData(submission, false);
        assertEquals(true, report.hasSuccess());
        assertEquals(false, report.hasError());
    }

    @Test
    public void modificationIsOptional() throws Exception {
        Submission submission = SubmissionFileParser.parse(inputFile);
        Set<CvParam> modifications = submission.getProjectMetaData().getModifications();
        modifications.clear();
        SubmissionType submissionType = submission.getProjectMetaData().getSubmissionType();
        ValidationReport validationReport = SubmissionValidator.validateModifications(modifications, submissionType);
        assertEquals(false, validationReport.hasError());
        assertEquals(false, validationReport.hasSuccess());
        assertEquals(true, validationReport.hasWarning());
    }


}
