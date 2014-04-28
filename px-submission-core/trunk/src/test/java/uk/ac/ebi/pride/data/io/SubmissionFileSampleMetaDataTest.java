package uk.ac.ebi.pride.data.io;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import uk.ac.ebi.pride.data.model.CvParam;
import uk.ac.ebi.pride.data.model.DataFile;
import uk.ac.ebi.pride.data.model.SampleMetaData;
import uk.ac.ebi.pride.data.model.Submission;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNull;
import static junit.framework.Assert.assertTrue;

/**
 * @author Rui Wang
 * @version $Id$
 */
public class SubmissionFileSampleMetaDataTest {
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


    private List<SampleMetaData> getSampleMetaData() {
        List<SampleMetaData> sampleMetaData = new ArrayList<SampleMetaData>();

        for (DataFile dataFile : submission.getDataFiles()) {
            if (dataFile.getSampleMetaData() != null) {
                sampleMetaData.add(dataFile.getSampleMetaData());
            }
        }

        return sampleMetaData;
    }

    @Test
    public void testNumberOfSampleMetaDataIsCorrect() throws Exception {
        List<SampleMetaData> sampleMetaData = getSampleMetaData();
        assertEquals(3, sampleMetaData.size());
    }

    @Test
    public void testSpeciesAreCorrect() throws Exception {
        List<SampleMetaData> sampleMetaData = getSampleMetaData();
        assertEquals(2, sampleMetaData.get(0).getMetaData(SampleMetaData.Type.SPECIES).size());
        assertEquals("9606", (sampleMetaData.get(0).getMetaData(SampleMetaData.Type.SPECIES).iterator().next()).getAccession());

        assertEquals(1, sampleMetaData.get(1).getMetaData(SampleMetaData.Type.SPECIES).size());
        assertEquals("9606", (sampleMetaData.get(1).getMetaData(SampleMetaData.Type.SPECIES).iterator().next()).getAccession());

        assertEquals(1, sampleMetaData.get(2).getMetaData(SampleMetaData.Type.SPECIES).size());
        assertEquals("9606", (sampleMetaData.get(2).getMetaData(SampleMetaData.Type.SPECIES).iterator().next()).getAccession());
    }

    @Test
    public void testTissuesAreCorrect() throws Exception {
        List<SampleMetaData> sampleMetaData = getSampleMetaData();
        assertEquals(1, sampleMetaData.get(0).getMetaData(SampleMetaData.Type.TISSUE).size());
        assertEquals("BTO:0000142", ((CvParam)sampleMetaData.get(0).getMetaData(SampleMetaData.Type.TISSUE).iterator().next()).getAccession());

        assertEquals(1, sampleMetaData.get(1).getMetaData(SampleMetaData.Type.TISSUE).size());
        assertEquals("BTO:0000142", ((CvParam)sampleMetaData.get(1).getMetaData(SampleMetaData.Type.TISSUE).iterator().next()).getAccession());

        assertEquals(1, sampleMetaData.get(2).getMetaData(SampleMetaData.Type.TISSUE).size());
        assertEquals("BTO:0000142", ((CvParam)sampleMetaData.get(2).getMetaData(SampleMetaData.Type.TISSUE).iterator().next()).getAccession());
    }

    @Test
    public void testCellTypesAreCorrect() throws Exception {
        List<SampleMetaData> sampleMetaData = getSampleMetaData();

        assertEquals(1, sampleMetaData.get(0).getMetaData(SampleMetaData.Type.CELL_TYPE).size());
        assertEquals("CL:0000236", ((CvParam)sampleMetaData.get(0).getMetaData(SampleMetaData.Type.CELL_TYPE).iterator().next()).getAccession());

        assertNull(sampleMetaData.get(1).getMetaData(SampleMetaData.Type.CELL_TYPE));

        assertNull(sampleMetaData.get(2).getMetaData(SampleMetaData.Type.CELL_TYPE));
    }

    @Test
    public void testDiseasesAreCorrect() throws Exception {
        List<SampleMetaData> sampleMetaData = getSampleMetaData();

        assertEquals(1, sampleMetaData.get(0).getMetaData(SampleMetaData.Type.DISEASE).size());
        assertEquals("DOID:1319", ((CvParam)sampleMetaData.get(0).getMetaData(SampleMetaData.Type.DISEASE).iterator().next()).getAccession());

        assertNull(sampleMetaData.get(1).getMetaData(SampleMetaData.Type.DISEASE));

        assertNull(sampleMetaData.get(2).getMetaData(SampleMetaData.Type.DISEASE));
    }

    @Test
    public void testInstrumentsAreCorrect() throws Exception {
        List<SampleMetaData> sampleMetaData = getSampleMetaData();

        assertEquals(1, sampleMetaData.get(0).getMetaData(SampleMetaData.Type.INSTRUMENT).size());
        assertEquals("MS:1000447", (sampleMetaData.get(0).getMetaData(SampleMetaData.Type.INSTRUMENT).iterator().next()).getAccession());

        assertEquals(1, sampleMetaData.get(1).getMetaData(SampleMetaData.Type.INSTRUMENT).size());
        assertEquals("MS:1000447", (sampleMetaData.get(1).getMetaData(SampleMetaData.Type.INSTRUMENT).iterator().next()).getAccession());

        assertEquals(1, sampleMetaData.get(2).getMetaData(SampleMetaData.Type.INSTRUMENT).size());
        assertEquals("MS:1000447", (sampleMetaData.get(2).getMetaData(SampleMetaData.Type.INSTRUMENT).iterator().next()).getAccession());
    }

    @Test
    public void testQuantificationAreCorrect() throws Exception {
        List<SampleMetaData> sampleMetaData = getSampleMetaData();

        assertEquals(1, sampleMetaData.get(0).getMetaData(SampleMetaData.Type.QUANTIFICATION_METHOD).size());
        boolean hasQuantitation = CollectionTestUtils.findCvParamAccession(sampleMetaData.get(0).getMetaData(SampleMetaData.Type.QUANTIFICATION_METHOD), "PRIDE:00199");
        assertTrue(hasQuantitation);

        assertNull(sampleMetaData.get(1).getMetaData(SampleMetaData.Type.QUANTIFICATION_METHOD));

        assertNull(sampleMetaData.get(2).getMetaData(SampleMetaData.Type.QUANTIFICATION_METHOD));
    }

    @Test
    public void testExperimentalFactorsAreCorrect() throws Exception {
        List<SampleMetaData> sampleMetaData = getSampleMetaData();

        assertEquals("first experimental factor", sampleMetaData.get(0).getMetaData(SampleMetaData.Type.EXPERIMENTAL_FACTOR).iterator().next().getValue());
        assertEquals("second experimental factor", sampleMetaData.get(1).getMetaData(SampleMetaData.Type.EXPERIMENTAL_FACTOR).iterator().next().getValue());
        assertEquals("third experimental factor", sampleMetaData.get(2).getMetaData(SampleMetaData.Type.EXPERIMENTAL_FACTOR).iterator().next().getValue());
    }
}
