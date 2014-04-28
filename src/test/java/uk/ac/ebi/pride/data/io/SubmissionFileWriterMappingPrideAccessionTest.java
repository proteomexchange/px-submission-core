package uk.ac.ebi.pride.data.io;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
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
public class SubmissionFileWriterMappingPrideAccessionTest {
    @Rule
    public TemporaryFolder temporaryFolder = new TemporaryFolder();

    private Submission submission;

    private File newSubmissionFile;

    @Before
    public void setUp() throws Exception {
        URL url = SubmissionFileWriterSupportedMetaDataTest.class.getClassLoader().getResource("submissionFilePride.px");
        submission = SubmissionFileParser.parse(new File(url.toURI()));
        newSubmissionFile = new File(temporaryFolder.getRoot().getAbsolutePath() + File.separator + "submission.px");
    }

    @Test
    public void prideAccessionCountCorrect() throws Exception {
        SubmissionFileWriter.write(submission, newSubmissionFile);
        Submission newSubmission = SubmissionFileParser.parse(newSubmissionFile);
        List<DataFile> dataFiles = newSubmission.getDataFiles();
        int accessionCnt = 0;
        for (DataFile dataFile : dataFiles) {
            if (dataFile.getAssayAccession() != null && !dataFile.getAssayAccession().trim().equals("")) {
                accessionCnt++;
            }
        }
        assertEquals(3, accessionCnt);
    }

    @Test
    public void prideAccessionCorrect() throws Exception {
        SubmissionFileWriter.write(submission, newSubmissionFile);
        Submission newSubmission = SubmissionFileParser.parse(newSubmissionFile);
        List<DataFile> dataFiles = newSubmission.getDataFiles();
        assertEquals("12345", dataFiles.get(0).getAssayAccession());
        assertEquals("23456", dataFiles.get(1).getAssayAccession());
    }
}
