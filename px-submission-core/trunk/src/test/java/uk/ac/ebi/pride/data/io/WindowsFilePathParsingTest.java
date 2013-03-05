package uk.ac.ebi.pride.data.io;

import org.junit.Before;
import org.junit.Test;
import uk.ac.ebi.pride.data.model.DataFile;
import uk.ac.ebi.pride.data.model.Submission;

import java.io.File;
import java.net.URL;

import static junit.framework.Assert.assertEquals;

/**
 * @author Rui Wang
 * @version $Id$
 */
public class WindowsFilePathParsingTest {

    private Submission submission;

    @Before
    public void setUp() throws Exception {
        URL url = SubmissionFileParser.class.getClassLoader().getResource("submissionWindows.px");
        if (url == null) {
            throw new IllegalStateException("no file for input found!");
        }
        File inputFile = new File(url.toURI());
        submission = SubmissionFileParser.parse(inputFile);
    }

    @Test
    public void fileNameDoesNotContainTheFullPath() throws Exception {
        DataFile dataFile = submission.getDataFiles().get(0);
        assertEquals("HSAs.msf-pride.xml", dataFile.getFile().getName());
    }
}
