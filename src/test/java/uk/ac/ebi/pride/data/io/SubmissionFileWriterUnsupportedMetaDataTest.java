package uk.ac.ebi.pride.data.io;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import uk.ac.ebi.pride.data.model.CvParam;
import uk.ac.ebi.pride.data.model.ProjectMetaData;
import uk.ac.ebi.pride.data.model.Submission;
import uk.ac.ebi.pride.archive.dataprovider.project.SubmissionType;

import java.io.File;
import java.net.URL;
import java.util.Set;

import static junit.framework.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Test submission file writer with unsupported result files
 *
 * @author Rui Wang
 * @version $Id$
 */
public class SubmissionFileWriterUnsupportedMetaDataTest {
    @Rule
    public TemporaryFolder temporaryFolder = new TemporaryFolder();

    private Submission submission;

    private File newSubmissionFile;

    @Before
    public void setUp() throws Exception {
        URL url = SubmissionFileWriterSupportedMetaDataTest.class.getClassLoader().getResource("partialSubmissionFile.px");
        submission = SubmissionFileParser.parse(new File(url.toURI()));
        newSubmissionFile = new File(temporaryFolder.getRoot().getAbsolutePath() + File.separator + "submission.px");
    }

    @Test
    public void tabShouldbeReplacedWithSpace() throws Exception {
        ProjectMetaData projectMetaData = submission.getProjectMetaData();
        projectMetaData.setProjectDescription("An experiment about\t\thuman proteome");
        SubmissionFileWriter.write(submission, newSubmissionFile);
        Submission newSubmission = SubmissionFileParser.parse(newSubmissionFile);
        assertEquals("An experiment about  human proteome", newSubmission.getProjectMetaData().getProjectDescription());
    }

    @Test
    public void lineSeparatorShouldbeReplacedByComma() throws Exception {
        ProjectMetaData projectMetaData = submission.getProjectMetaData();
        projectMetaData.setProjectDescription("An experiment about\n\nhuman proteome");
        SubmissionFileWriter.write(submission, newSubmissionFile);
        Submission newSubmission = SubmissionFileParser.parse(newSubmissionFile);
        assertEquals("An experiment about  human proteome", newSubmission.getProjectMetaData().getProjectDescription());
    }

    @Test
    public void supportedIsFalse() throws Exception {
        SubmissionFileWriter.write(submission, newSubmissionFile);
        Submission newSubmission = SubmissionFileParser.parse(newSubmissionFile);
        assertEquals(SubmissionType.PARTIAL, newSubmission.getProjectMetaData().getSubmissionType());
    }


    @Test
    public void commentIsCorrect() throws Exception {
        SubmissionFileWriter.write(submission, newSubmissionFile);
        Submission newSubmission = SubmissionFileParser.parse(newSubmissionFile);
        assertEquals("cannot convert using the PRIDE Converter", newSubmission.getProjectMetaData().getReasonForPartialSubmission());
    }

    @Test
    public void testSpeciesAreCorrect() throws Exception {
        SubmissionFileWriter.write(submission, newSubmissionFile);
        Submission newSubmission = SubmissionFileParser.parse(newSubmissionFile);
        assertEquals(2, newSubmission.getProjectMetaData().getSpecies().size());
        Set<CvParam> species = submission.getProjectMetaData().getSpecies();
        boolean hasHuman = CollectionTestUtils.findCvParamAccession(species, "9606");
        assertTrue(hasHuman);
    }

    @Test
    public void testInstrumentIsCorrect() throws Exception {
        SubmissionFileWriter.write(submission, newSubmissionFile);
        Submission newSubmission = SubmissionFileParser.parse(newSubmissionFile);
        assertEquals(1, newSubmission.getProjectMetaData().getInstruments().size());
        Set<CvParam> instruments = submission.getProjectMetaData().getInstruments();
        boolean hasInstrument = CollectionTestUtils.findCvParamAccession(instruments, "MS:1000447");
        assertTrue(hasInstrument);
    }

    @Test
    public void testModificationIsCorrect() throws Exception {
        SubmissionFileWriter.write(submission, newSubmissionFile);
        Submission newSubmission = SubmissionFileParser.parse(newSubmissionFile);
        assertEquals(2, newSubmission.getProjectMetaData().getModifications().size());
        assertEquals("MOD:00198", newSubmission.getProjectMetaData().getModifications().iterator().next().getAccession());
    }

    @Test
    public void testAdditionalIsCorrect() throws Exception {
        SubmissionFileWriter.write(submission, newSubmissionFile);
        Submission newSubmission = SubmissionFileParser.parse(newSubmissionFile);
        assertEquals(1, newSubmission.getProjectMetaData().getAdditional().size());
        boolean hasAdditionalParamValue = CollectionTestUtils.findParamValue(submission.getProjectMetaData().getAdditional(), "additional param value");
        assertTrue(hasAdditionalParamValue);
    }
}
