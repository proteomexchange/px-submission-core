package uk.ac.ebi.pride.data.io;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import uk.ac.ebi.pride.data.model.MetaData;
import uk.ac.ebi.pride.data.model.Submission;
import uk.ac.ebi.pride.data.util.SubmissionType;

import java.io.File;
import java.net.URL;

import static junit.framework.Assert.assertEquals;

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
        URL url = SubmissionFileWriterSupportedMetaDataTest.class.getClassLoader().getResource("submissionFileUnsupported.px");
        submission = SubmissionFileParser.parse(new File(url.toURI()));
        newSubmissionFile = new File(temporaryFolder.getRoot().getAbsolutePath() + File.separator + "submission.px");
    }

    @Test
    public void tabShouldbeReplacedWithSpace() throws Exception {
        MetaData metaData = submission.getMetaData();
        metaData.setDescription("An experiment about\t\thuman proteome");
        SubmissionFileWriter.write(submission, newSubmissionFile);
        Submission newSubmission = SubmissionFileParser.parse(newSubmissionFile);
        assertEquals("An experiment about  human proteome", newSubmission.getMetaData().getDescription());
    }

    @Test
    public void lineSeparatorShouldbeReplacedByComma() throws Exception {
        MetaData metaData = submission.getMetaData();
        metaData.setDescription("An experiment about\n\nhuman proteome");
        SubmissionFileWriter.write(submission, newSubmissionFile);
        Submission newSubmission = SubmissionFileParser.parse(newSubmissionFile);
        assertEquals("An experiment about  human proteome", newSubmission.getMetaData().getDescription());
    }

    @Test
    public void supportedIsFalse() throws Exception {
        SubmissionFileWriter.write(submission, newSubmissionFile);
        Submission newSubmission = SubmissionFileParser.parse(newSubmissionFile);
        assertEquals(SubmissionType.UNSUPPORTED, newSubmission.getMetaData().getSubmissionType());
    }


    @Test
    public void commentIsCorrect() throws Exception {
        SubmissionFileWriter.write(submission, newSubmissionFile);
        Submission newSubmission = SubmissionFileParser.parse(newSubmissionFile);
        assertEquals("cannot convert using the PRIDE Converter", newSubmission.getMetaData().getComment());
    }

    @Test
    public void testSpeciesAreCorrect() throws Exception {
        SubmissionFileWriter.write(submission, newSubmissionFile);
        Submission newSubmission = SubmissionFileParser.parse(newSubmissionFile);
        assertEquals(2, newSubmission.getMetaData().getSpecies().size());
        assertEquals("741158", newSubmission.getMetaData().getSpecies().get(1).getAccession());
    }

    @Test
    public void testInstrumentIsCorrect() throws Exception {
        SubmissionFileWriter.write(submission, newSubmissionFile);
        Submission newSubmission = SubmissionFileParser.parse(newSubmissionFile);
        assertEquals(1, newSubmission.getMetaData().getInstruments().size());
        assertEquals(2, newSubmission.getMetaData().getInstruments().get(0).size());
        assertEquals("MS:1000122", newSubmission.getMetaData().getInstruments().get(0).get(1).getAccession());
    }

    @Test
    public void testModificationIsCorrect() throws Exception {
        SubmissionFileWriter.write(submission, newSubmissionFile);
        Submission newSubmission = SubmissionFileParser.parse(newSubmissionFile);
        assertEquals(2, newSubmission.getMetaData().getModifications().size());
        assertEquals("MOD:00199", newSubmission.getMetaData().getModifications().get(1).getAccession());
    }

    @Test
    public void testAdditionalIsCorrect() throws Exception {
        SubmissionFileWriter.write(submission, newSubmissionFile);
        Submission newSubmission = SubmissionFileParser.parse(newSubmissionFile);
        assertEquals(1, newSubmission.getMetaData().getAdditional().size());
        assertEquals("PRIDE:0000097", newSubmission.getMetaData().getAdditional().get(0).getAccession());
    }
}
