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
public class LineSeparatorRemovalTest {

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
    public void shouldRemoveWindowsLineSeparator() throws Exception {
        ProjectMetaData projectMetaData = submission.getProjectMetaData();
        String desc = projectMetaData.getProjectDescription();
        String newDesc = desc + " test test";
        projectMetaData.setProjectDescription(desc + "\r\ntest test");
        SubmissionFileWriter.write(submission, newSubmissionFile);
        Submission newSubmission = SubmissionFileParser.parse(newSubmissionFile);
        assertEquals(newDesc, newSubmission.getProjectMetaData().getProjectDescription());
    }

    @Test
    public void shouldRemoveLinuxLineSeparator() throws Exception {
        ProjectMetaData projectMetaData = submission.getProjectMetaData();
        String desc = projectMetaData.getProjectDescription();
        String newDesc = desc + " test test";
        projectMetaData.setProjectDescription(desc + "\ntest test");
        SubmissionFileWriter.write(submission, newSubmissionFile);
        Submission newSubmission = SubmissionFileParser.parse(newSubmissionFile);
        assertEquals(newDesc, newSubmission.getProjectMetaData().getProjectDescription());
    }
}
