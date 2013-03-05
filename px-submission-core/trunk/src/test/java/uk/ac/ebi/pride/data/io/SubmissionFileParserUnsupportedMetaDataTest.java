package uk.ac.ebi.pride.data.io;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import uk.ac.ebi.pride.data.model.Submission;
import uk.ac.ebi.pride.data.util.SubmissionType;

import java.io.File;
import java.net.URL;

import static org.junit.Assert.assertEquals;

/**
 * Test submission file parser with unsupported result files
 *
 * @author Rui Wang
 * @version $Id$
 */
public class SubmissionFileParserUnsupportedMetaDataTest {
    private Submission submission;

    @Before
    public void setUp() throws Exception {
        URL url = SubmissionFileParser.class.getClassLoader().getResource("submissionFileUnsupported.px");
        if (url == null) {
            throw new IllegalStateException("no file for input found!");
        }
        File inputFile = new File(url.toURI());
        submission = SubmissionFileParser.parse(inputFile);
    }

    @After
    public void tearDown() throws Exception {
        submission = null;
    }

    @Test
    public void testName() throws Exception {
        assertEquals("John Arthur Smith", submission.getContact().getName());
    }

    @Test
    public void testEmail() throws Exception {
        assertEquals("john.smith@cam.edu", submission.getContact().getEmail());
    }

    @Test
    public void testAffiliation() throws Exception {
        assertEquals("University of Cambridge", submission.getContact().getAffiliation());
    }

    @Test
    public void testPrideUserName() throws Exception {
        assertEquals("pride_user", submission.getContact().getUserName());
    }

    @Test
    public void testExpTitle() throws Exception {
        assertEquals("Human proteome", submission.getMetaData().getTitle());
    }

    @Test
    public void testExpDesc() throws Exception {
        assertEquals("An experiment about human proteome", submission.getMetaData().getDescription());
    }

    @Test
    public void testKeywords() throws Exception {
        assertEquals("human, proteome", submission.getMetaData().getKeywords());
    }

    @Test
    public void testPubMeds() throws Exception {
        assertEquals(2, submission.getMetaData().getPubmedIds().size());
        assertEquals("23456", submission.getMetaData().getPubmedIds().get(1));
    }

    @Test
    public void testResubmissionPxAccession() throws Exception {
        assertEquals("PXD000001", submission.getMetaData().getResubmissionPxAccession());
    }

    @Test
    public void testReanalysisPxAccessions() throws Exception {
        assertEquals(2, submission.getMetaData().getReanalysisAccessions().size());
        assertEquals("PXD000005", submission.getMetaData().getReanalysisAccessions().get(1));
    }

    @Test
    public void testSupportedIsFalse() throws Exception {
        assertEquals(SubmissionType.UNSUPPORTED, submission.getMetaData().getSubmissionType());
    }

    @Test
    public void testCommentIsCorrect() throws Exception {
        assertEquals("cannot convert using the PRIDE Converter", submission.getMetaData().getComment());
    }

    @Test
    public void testSpeciesAreCorrect() throws Exception {
        assertEquals(2, submission.getMetaData().getSpecies().size());
        assertEquals("741158", submission.getMetaData().getSpecies().get(1).getAccession());
    }

    @Test
    public void testInstrumentIsCorrect() throws Exception {
        assertEquals(1, submission.getMetaData().getInstruments().size());
        assertEquals(2, submission.getMetaData().getInstruments().get(0).size());
        assertEquals("MS:1000122", submission.getMetaData().getInstruments().get(0).get(1).getAccession());
    }

    @Test
    public void testModificationIsCorrect() throws Exception {
        assertEquals(2, submission.getMetaData().getModifications().size());
        assertEquals("MOD:00199", submission.getMetaData().getModifications().get(1).getAccession());
    }

    @Test
    public void testAdditionalIsCorrect() throws Exception {
        assertEquals(1, submission.getMetaData().getAdditional().size());
        assertEquals("PRIDE:0000097", submission.getMetaData().getAdditional().get(0).getAccession());
    }
}
