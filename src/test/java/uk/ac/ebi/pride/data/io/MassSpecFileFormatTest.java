package uk.ac.ebi.pride.data.io;

import org.junit.Assert;
import org.junit.Test;
import uk.ac.ebi.pride.data.util.MassSpecFileFormat;

import java.io.File;
import java.net.URL;

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
}
