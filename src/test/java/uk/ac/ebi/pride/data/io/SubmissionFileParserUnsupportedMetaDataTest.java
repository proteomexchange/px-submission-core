package uk.ac.ebi.pride.data.io;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import uk.ac.ebi.pride.data.model.CvParam;
import uk.ac.ebi.pride.data.model.Submission;
import uk.ac.ebi.pride.archive.dataprovider.project.SubmissionType;

import java.io.File;
import java.net.URL;
import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

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
        URL url = SubmissionFileParser.class.getClassLoader().getResource("partialSubmissionFile.px");
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
        assertEquals("John Arthur Smith", submission.getProjectMetaData().getSubmitterContact().getName());
    }

    @Test
    public void testEmail() throws Exception {
        assertEquals("john.smith@cam.edu", submission.getProjectMetaData().getSubmitterContact().getEmail());
    }

    @Test
    public void testAffiliation() throws Exception {
        assertEquals("University of Cambridge", submission.getProjectMetaData().getSubmitterContact().getAffiliation());
    }

    @Test
    public void testExpTitle() throws Exception {
        assertEquals("Human proteome An experiment about human proteome An experiment about human proteome An experiment about human proteome", submission.getProjectMetaData().getProjectTitle());
    }

    @Test
    public void testExpDesc() throws Exception {
        assertEquals("An experiment about human proteome An experiment about human proteome An experiment about human proteome", submission.getProjectMetaData().getProjectDescription());
    }

    @Test
    public void testKeywords() throws Exception {
        assertEquals("human, proteome", submission.getProjectMetaData().getKeywords());
    }

    @Test
    public void testPubMeds() throws Exception {
        assertEquals(2, submission.getProjectMetaData().getPubmedIds().size());
        assertTrue(submission.getProjectMetaData().getPubmedIds().contains("12345"));
    }

    @Test
    public void testResubmissionPxAccession() throws Exception {
        assertTrue(submission.getProjectMetaData().getResubmissionPxAccession().equals("PXD000001"));
    }

    @Test
    public void testReanalysisPxAccessions() throws Exception {
        assertEquals(2, submission.getProjectMetaData().getReanalysisAccessions().size());
        assertTrue(submission.getProjectMetaData().getReanalysisAccessions().contains("PXD000004"));
    }

    @Test
    public void testSupportedIsFalse() throws Exception {
        assertEquals(SubmissionType.PARTIAL, submission.getProjectMetaData().getSubmissionType());
    }

    @Test
    public void testCommentIsCorrect() throws Exception {
        assertEquals("cannot convert using the PRIDE Converter", submission.getProjectMetaData().getReasonForPartialSubmission());
    }

    @Test
    public void testSpeciesAreCorrect() throws Exception {
        assertEquals(2, submission.getProjectMetaData().getSpecies().size());
        Set<CvParam> species = submission.getProjectMetaData().getSpecies();
        boolean hasHuman = CollectionTestUtils.findCvParamAccession(species, "9606");
        assertTrue(hasHuman);
    }

    @Test
    public void testTissueIsCorrect() throws Exception {
        assertEquals(1, submission.getProjectMetaData().getTissues().size());
        Set<CvParam> tissues = submission.getProjectMetaData().getTissues();
        boolean hasTissue = CollectionTestUtils.findCvParamAccession(tissues, "BTO:0000142");
        assertTrue(hasTissue);
    }

    @Test
    public void testCellTypeIsCorrect() throws Exception {
        assertEquals(1, submission.getProjectMetaData().getCellTypes().size());
        Set<CvParam> cellTypes = submission.getProjectMetaData().getCellTypes();
        boolean hasCellType = CollectionTestUtils.findCvParamAccession(cellTypes, "CL:0000236");
        assertTrue(hasCellType);
    }

    @Test
    public void testDiseaseCorrect() throws Exception {
        assertEquals(1, submission.getProjectMetaData().getDiseases().size());
        Set<CvParam> diseases = submission.getProjectMetaData().getDiseases();
        boolean hasDisease = CollectionTestUtils.findCvParamAccession(diseases, "DOID:1319");
        assertTrue(hasDisease);
    }

    @Test
    public void testInstrumentIsCorrect() throws Exception {
        assertEquals(1, submission.getProjectMetaData().getInstruments().size());
        Set<CvParam> instruments = submission.getProjectMetaData().getInstruments();
        boolean hasInstrument = CollectionTestUtils.findCvParamAccession(instruments, "MS:1000447");
        assertTrue(hasInstrument);
    }

    @Test
    public void testModificationIsCorrect() throws Exception {
        assertEquals(2, submission.getProjectMetaData().getModifications().size());
        assertEquals("MOD:00198", submission.getProjectMetaData().getModifications().iterator().next().getAccession());
    }

    @Test
    public void testAdditionalIsCorrect() throws Exception {
        assertEquals(1, submission.getProjectMetaData().getAdditional().size());
        boolean hasAdditionalParamValue = CollectionTestUtils.findParamValue(submission.getProjectMetaData().getAdditional(), "additional param value");
        assertTrue(hasAdditionalParamValue);
    }

}
