package uk.ac.ebi.pride.data.io;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import uk.ac.ebi.pride.data.model.Submission;

import java.io.File;
import java.net.URL;

import static org.junit.Assert.assertEquals;


/**
 * Tests to check parsing on Contact and MetaData
 *
 * @author Rui Wang
 * @version $Id$
 */
public class SubmissionFileParserSupportedMetaDataTest {
    private Submission submission;

    @Before
    public void setUp() throws Exception {
        URL url = SubmissionFileParser.class.getClassLoader().getResource("submissionFile.px");
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
    public void testPxAccessions() throws Exception {
        assertEquals(2, submission.getMetaData().getReanalysisAccessions().size());
        assertEquals("PXD000005", submission.getMetaData().getReanalysisAccessions().get(1));
    }
}
