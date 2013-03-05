package uk.ac.ebi.pride.data.util;

import java.util.regex.Pattern;

/**
 * This class stores all the static constants shared by this application
 *
 * @author Rui Wang
 * @version $Id$
 */
public class Constant {

    private Constant() {
    }

    /**
     * This section is used for submission file input/output purpose
     */
    public static final String METADATA_HEADER = "MTD";

    public static final String FILE_MAPPING_HEADER = "FMH";

    public static final String FILE_MAPPING_ENTRY = "FME";

    public static final String NAME = "name";

    public static final String EMAIL = "email";

    public static final String AFFILIATION = "affiliation";

    public static final String EXPERIMENT_TITLE = "title";

    public static final String EXPERIMENT_DESC = "description";

    public static final String KEYWORDS = "keywords";

    public static final String SUBMISSION_TYPE = "type";

    public static final String COMMENT = "comment";

    public static final String SPECIES = "species";

    public static final String INSTRUMENT = "instrument";

    public static final String MODIFICATION = "modification";

    public static final String ADDITIONAL = "additional";

    public static final String PUBMED_ID = "pubmed";

    public static final String RESUBMISSION_PX_ACCESSION = "resubmission_px";

    public static final String REANALYSIS_PX_ACCESSION = "reanalysis_px";

    public static final String PRIDE_LOGIN = "pride_login";

    public static final String FILE_ID = "file_id";

    public static final String FILE_TYPE = "file_type";

    public static final String FILE_PATH = "file_path";

    public static final String FILE_MAPPING = "file_mapping";

    public static final String PRIDE_ACCESSION = "pride_accession";

    public static final String RESULT_FILE_TYPE = "result";

    public static final String RAW_FILE_TYPE = "raw";

    public static final String SEARCH_ENGINE_FILE_TYPE = "search";

    public static final String PEAK_LIST_FILE_TYPE = "peak";

    public static final String OTHER_FILE_TYPE = "other";

    /**
     * Ontologies
     */
    public static final String NEWT = "NEWT";

    public static final String PSI_MOD = "MOD";

    public static final String UNIMOD = "UNIMOD";

    public static final String MS = "MS";

    public static final String PRIDE = "PRIDE";


    public static final String NO_MOD_PRIDE_ACCESSION = "PRIDE:0000398";



    /**
     * This section contains all the separators
     */
    public static final String TAB = "\t";

    public static final String COMMA = ",";

    public static final String DOT = ".";

    public static final String LINE_SEPARATOR = System.getProperty("line.separator");

    public static final String LINUX_LINE_SEPARATOR = "\n";

    public static final String WIN_LINE_SEPARATOR = "\r\n";

    public static final String CV_START = "[";

    public static final String CV_END= "]";

    /**
     * This section contains all the regular expressions
     */
    public static final Pattern EMAIL_PATTERN = Pattern.compile("^[\\w\\-]+(\\.[\\w\\-]+)*@([A-Za-z0-9-]+\\.)+[A-Za-z]{2,4}$");

    public static final Pattern PUBMED_PATTERN = Pattern.compile("^\\d+$");

    public static final Pattern DOI_PATTERN = Pattern.compile("^10\\.\\d+/\\d+$");

    public static final Pattern PX_PATTERN = Pattern.compile("^PXD\\d{6}");

    public static final Pattern PX_TEST_PATTERN = Pattern.compile("^PXTEST\\d+");

    public static final Pattern URL_PATTERN = Pattern.compile("\\b(https?|ftp)://[-a-zA-Z0-9+&@#/%?=~_|!:,.;]*[-a-zA-Z0-9+&@#/%=~_|]");
}
