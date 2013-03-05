package uk.ac.ebi.pride.data.io;

import org.junit.Before;
import org.junit.Test;
import uk.ac.ebi.pride.data.model.DataFile;
import uk.ac.ebi.pride.data.model.Submission;

import java.io.File;
import java.net.URL;
import java.util.List;

import static junit.framework.Assert.assertEquals;

/**
 * @author Rui Wang
 * @version $Id$
 */
public class SubmissionFileParserMappingPrideAccessionTest {
    private Submission submission;

    @Before
    public void setUp() throws Exception {
        URL url = SubmissionFileParser.class.getClassLoader().getResource("submissionFilePride.px");
        if (url == null) {
            throw new IllegalStateException("no file for input found!");
        }
        File inputFile = new File(url.toURI());
        submission = SubmissionFileParser.parse(inputFile);
    }

    @Test
    public void prideAccessionCountCorrect() throws Exception {
        List<DataFile> dataFiles = submission.getDataFiles();
        int accessionCnt = 0;
        for (DataFile dataFile : dataFiles) {
            if (dataFile.getPrideAccession() != null) {
                accessionCnt++;
            }
        }
        assertEquals(3, accessionCnt);
    }

    @Test
    public void prideAccessionCorrect() throws Exception {
        List<DataFile> dataFiles = submission.getDataFiles();
        assertEquals("12345", dataFiles.get(0).getPrideAccession());
        assertEquals("23456", dataFiles.get(1).getPrideAccession());
    }
}
