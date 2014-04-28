package uk.ac.ebi.pride.data.io;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import uk.ac.ebi.pride.data.model.ProjectMetaData;
import uk.ac.ebi.pride.data.model.Submission;

import java.io.File;
import java.net.URL;

import static junit.framework.Assert.assertEquals;

/**
 * @author Rui Wang
 * @version $Id$
 */
public class SubmissionFileWriterSupportedMetaDataTest {
    @Rule
    public TemporaryFolder temporaryFolder = new TemporaryFolder();
    
    private Submission submission;
    
    private File newSubmissionFile;

    @Before
    public void setUp() throws Exception {
        URL url = SubmissionFileWriterSupportedMetaDataTest.class.getClassLoader().getResource("submissionFile.px");
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
}
