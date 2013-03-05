package uk.ac.ebi.pride.data.io;

import org.junit.Before;
import org.junit.Test;
import uk.ac.ebi.pride.data.model.Submission;
import uk.ac.ebi.pride.data.validation.SubmissionValidator;

import java.io.File;
import java.net.URL;

import static org.junit.Assert.assertEquals;

/**
 * Test submission validator
 *
 * @author Rui Wang
 * @version $Id$
 */
public class SubmissionValidatorTest {
    private File inputFile;

    @Before
    public void setUp() throws Exception {
        URL url = SubmissionFileParser.class.getClassLoader().getResource("submissionFileUnsupported.px");
        if (url == null) {
            throw new IllegalStateException("no file for input found!");
        }
        inputFile = new File(url.toURI());

    }

    @Test
    public void metaDataIsValid() throws Exception {
        Submission submission = SubmissionFileParser.parse(inputFile);
        assertEquals(true, SubmissionValidator.isValidMetaData(submission.getMetaData()));
    }

    @Test
    public void speciesMustBeNEWT() throws Exception {
        Submission submission = SubmissionFileParser.parse(inputFile);
        submission.getMetaData().getSpecies().get(0).setCvLabel("TEST");
        assertEquals(false, SubmissionValidator.isValidMetaData(submission.getMetaData()));
    }

    @Test
    public void modificatonMustBeEitherPSIMODorUniMod() throws Exception {
        Submission submission = SubmissionFileParser.parse(inputFile);
        submission.getMetaData().getModifications().get(0).setCvLabel("TEST");
        assertEquals(false, SubmissionValidator.isValidMetaData(submission.getMetaData()));
    }

    @Test
    public void fileMappingsMustBeValid() throws Exception {
        Submission submission = SubmissionFileParser.parse(inputFile);
        assertEquals(false, SubmissionValidator.isValidFileMappings(submission.getDataFiles(), submission.getMetaData().getSubmissionType()));
    }
}
