package uk.ac.ebi.pride.data.io;

import org.junit.Before;
import org.junit.Test;
import uk.ac.ebi.pride.data.model.DataFile;
import uk.ac.ebi.pride.data.model.Submission;
import uk.ac.ebi.pride.data.util.MassSpecFileType;

import java.io.File;
import java.net.URL;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

/**
 * Tests for parsing file mappings
 *
 * @author Rui Wang
 * @version $Id$
 */
public class SubmissionFileParserMappingTest {
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

    @Test
    public void testNumberOfFiles() throws Exception {
        assertEquals(10, submission.getDataFiles().size());
    }

    @Test
    public void testNumberOfResults() throws Exception {
        List<DataFile> dataFiles = submission.getDataFiles();
        int cnt = 0;
        for (DataFile dataFile : dataFiles) {
            if (dataFile.getFileType().equals(MassSpecFileType.RESULT)) {
                cnt ++;
            }
        }
        assertEquals(3, cnt);
    }

    @Test
    public void testFileMappings() throws Exception {
        List<DataFile> dataFiles = submission.getDataFiles();
        DataFile result = dataFiles.get(0);
        assertEquals(3, result.getFileMappings().size());
        assertEquals(9, result.getFileMappings().get(2).getId());
    }

    @Test
    public void testURL() throws Exception {
        List<DataFile> dataFiles = submission.getDataFiles();
        DataFile dataFile = dataFiles.get(6);
        assertNull(dataFile.getFile());
        assertEquals("ftp://some.url/at/some/place/raw-4.bin", dataFile.getUrl().toString());
    }
}
