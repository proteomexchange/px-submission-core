package uk.ac.ebi.pride.data.io;

import org.junit.Assert;
import org.junit.Test;
import uk.ac.ebi.pride.data.util.MassSpecFileFormat;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;

import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;


/**
 *
 *
 * @author Rui Wang
 * @version $Id$
 */
public class MassSpecFileFormatTest {

    private String TEST_PRIDE_FILE_URL = "http://www.ebi.ac.uk/test/test_file.pride.xml";
    private String TEST_MZIDENT_FILE_URL = "http://www.ebi.ac.uk/test/test_file.mzid";
    private String TEST_MZIDENT_GZIPPED_FILE_URL = "http://www.ebi.ac.uk/test/test_file.mzid.gz";
    private String TEST_MZML_FILE_URL = "http://www.ebi.ac.uk/test/test_file.mzML";
    private URL TEST_MZTAB_FILE_URL = MassSpecFileFormatTest.class.getClassLoader().getResource("./mzml/F002759.dat-pride.pride.mztab");
    private URL TEST_UNRECOGNIZABLE_FILE = MassSpecFileFormatTest.class.getClassLoader().getResource("./mzml/unrecognizable_file.unknown");

    @Test
    public void testPRIDEFileURL() throws Exception {

        Assert.assertEquals(MassSpecFileFormat.PRIDE, MassSpecFileFormat.checkFormat(new URL(TEST_PRIDE_FILE_URL)));
    }

    @Test
    public void testMzIdentMLFileURL() throws Exception {

        Assert.assertEquals(MassSpecFileFormat.MZIDENTML, MassSpecFileFormat.checkFormat(new URL(TEST_MZIDENT_FILE_URL)));
    }

    @Test
    public void testMzIdentMLGzippedFileURL() throws Exception {

        Assert.assertEquals(MassSpecFileFormat.MZIDENTML, MassSpecFileFormat.checkFormat(new URL(TEST_MZIDENT_GZIPPED_FILE_URL)));
    }

    @Test
    public void testMzMLGzippedFileURL() throws Exception {

        Assert.assertEquals(MassSpecFileFormat.MZML, MassSpecFileFormat.checkFormat(new URL(TEST_MZML_FILE_URL)));
    }

    @Test
    public void testMzMLFile() throws Exception {
        URL resource = MassSpecFileFormatTest.class.getClassLoader().getResource("./mzml/valid.mzML");
        File file = new File(resource.toURI());

        Assert.assertEquals(MassSpecFileFormat.MZML, MassSpecFileFormat.checkFormat(file));
    }

    @Test
    public void testMzMLXMLFile() throws Exception {
        URL resource = MassSpecFileFormatTest.class.getClassLoader().getResource("./mzml/valid.xml");
        File file = new File(resource.toURI());

        Assert.assertEquals(MassSpecFileFormat.MZML, MassSpecFileFormat.checkFormat(file));
    }

    @Test
    public void testIndexedMzMLXMLFile() throws Exception {
        URL resource = MassSpecFileFormatTest.class.getClassLoader().getResource("./mzml/indexed_valid.mzML");
        File file = new File(resource.toURI());

        Assert.assertEquals(MassSpecFileFormat.INDEXED_MZML, MassSpecFileFormat.checkFormat(file));
    }

    @Test
    public void mzTabFileDetected() throws URISyntaxException, IOException {
        File file = new File(TEST_MZTAB_FILE_URL.toURI());

        assertThat("mzTab file format is recognized from mzTab file content", MassSpecFileFormat.checkFormat(file).equals(MassSpecFileFormat.MZTAB), is(true));
    }

    @Test
    public void unrecognizableFileIsUnrecognized() throws URISyntaxException, IOException {
        File file = new File(TEST_UNRECOGNIZABLE_FILE.toURI());

        assertThat("unrecognizable file is not recognized", MassSpecFileFormat.checkFormat(file), is(nullValue()));
    }
}
