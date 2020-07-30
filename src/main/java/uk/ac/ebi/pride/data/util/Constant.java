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
    public static final String COMMENT_ENTRY = "COM";

    public static final String METADATA_HEADER = "MTD";

    public static final String FILE_MAPPING_HEADER = "FMH";

    public static final String FILE_MAPPING_ENTRY = "FME";

    public static final String SAMPLE_METADATA_HEADER = "SMH";

    public static final String SAMPLE_METADATA_ENTRY = "SME";

    public static final String SUBMITTER_NAME = "submitter_name";

    public static final String LEGACY_SUBMITTER_NAME = "name";

    public static final String SUBMITTER_EMAIL = "submitter_email";

    public static final String LEGACY_SUBMITTER_EMAIL = "email";

    public static final String SUBMITTER_AFFILIATION = "submitter_affiliation";

    public static final String LEGACY_SUBMITTER_AFFILIATION = "affiliation";

    public static final String LAB_HEAD_NAME = "lab_head_name";

    public static final String LAB_HEAD_EMAIL = "lab_head_email";

    public static final String LAB_HEAD_AFFILIATION = "lab_head_affiliation";

    public static final String SUBMITTER_USER_NAME = "submitter_pride_login";

    public static final String LEGACY_SUBMITTER_USER_NAME = "pride_login";

    public static final String PROJECT_TITLE = "project_title";

    public static final String LEGACY_PROJECT_TITLE = "title";

    public static final String PROJECT_DESC = "project_description";

    public static final String LEGACY_PROJECT_DESC = "description";

    public static final String PROJECT_TAG = "project_tag";

    public static final String SAMPLE_PROCESSING_PROTOCOL = "sample_processing_protocol";

    public static final String DATA_PROCESSING_PROTOCOL = "data_processing_protocol";

    public static final String OTHER_OMICS_LINK = "other_omics_link";

    public static final String KEYWORDS = "keywords";

    public static final String SUBMISSION_TYPE = "submission_type";

    public static final String LEGACY_SUBMISSION_TYPE = "type";

    public static final String LEGACY_SUPPORTED_SUBMISSION = "supported";

    public static final String LEGACY_UNSUPPORTED_SUBMISSION = "unsupported";

    public static final String EXPERIMENT_TYPE = "experiment_type";

    public static final String REASON_FOR_PARTIAL = "reason_for_partial";

    public static final String LEGACY_REASON_FOR_PARTIAL = "comment";

    public static final String SPECIES = "species";

    public static final String TISSUE = "tissue";

    public static final String CELL_TYPE = "cell_type";

    public static final String DISEASE = "disease";

    public static final String QUANTIFICATION = "quantification";

    public static final String INSTRUMENT = "instrument";

    public static final String MODIFICATION = "modification";

    public static final String ADDITIONAL = "additional";

    public static final String PUBMED_ID = "pubmed_id";

    public static final String DOI = "DOI";

    public static final String RESUBMISSION_PX_ACCESSION = "resubmission_px";

    public static final String REANALYSIS_PX_ACCESSION = "reanalysis_px";

    public static final String FILE_ID = "file_id";

    public static final String FILE_TYPE = "file_type";

    public static final String FILE_PATH = "file_path";

    public static final String FILE_MAPPING = "file_mapping";

    public static final String PRIDE_ACCESSION = "pride_accession";

    public static final String EXPERIMENTAL_FACTOR = "experimental_factor";

    public static final String URL = "url";

    public static final int MINIMUM_STRING_LENGTH = 50;

    public static final int MINIMUM_SHORT_STRING_LENGTH = 30;

    public static final int MAXIMUM_SHORT_STRING_LENGTH = 500;

    public static final int MAXIMUM_MEDIUM_STRING_LENGTH = 500;

    public static final int MAXIMUM_LONG_STRING_LENGTH = 5000;



    /**
     * Ontologies
     */
    public static final String NEWT = "NEWT";

    public static final String PSI_MOD = "MOD";

    public static final String UNIMOD = "UNIMOD";

    public static final String MS = "MS";

    public static final String PRIDE = "PRIDE";

    public static final String CL = "CL";

    public static final String BTO = "BTO";

    public static final String DOID = "DOID";

    public static final String EFO = "EFO";


    public static final String NO_MOD_PRIDE_ACCESSION = "PRIDE:0000398";

    public static final String EXPERIMENTAL_FACTOR_ACCESSION = "EFO:0000001";

    public static final String EXPERIMENTAL_FACTOR_NAME = "experimental factor";

    /**
     * This section contains all the separators
     */
    public static final String TAB = "\t";

    public static final String COMMA = ",";

    public static final String DOT = ".";

    public static final String LINE_SEPARATOR = System.getProperty("line.separator");

    public static final String LINUX_LINE_SEPARATOR = "\n";

    public static final String WIN_LINE_SEPARATOR = "\r\n";

    public static final String PARAM_START = "[";

    public static final String PARAM_END = "]";

    /**
     * This section contains all the regular expressions
     */
    public static final Pattern EMAIL_PATTERN = Pattern.compile("^[\\w\\-]+(\\.[\\w\\-]+)*@([A-Za-z0-9-]+\\.)+[A-Za-z]+$");

    public static final Pattern PUBMED_PATTERN = Pattern.compile("^\\d+$");

    public static final Pattern DOI_PATTERN = Pattern.compile("^10\\.\\d+/");

    public static final Pattern PX_PATTERN = Pattern.compile("^PXD\\d{6}");

    public static final Pattern PRIDE_PATTERN = Pattern.compile("^PRD\\d{6}");

    public static final Pattern PX_TEST_PATTERN = Pattern.compile("^PXT\\d+");

    public static final Pattern PRIDE_TEST_PATTERN = Pattern.compile("^PRDTEST\\d+");

    public static final Pattern URL_PATTERN = Pattern.compile("\\b(https?|ftp)://[-a-zA-Z0-9+&@#/%?=~_|!:,.;]*[-a-zA-Z0-9+&@#/%=~_|]");

    /**
     * Pre-defined annotations
     */

    public static final String PROJECT_TAG_FILE="https://raw.githubusercontent.com/PRIDE-Utilities/pride-ontology/master/pride-annotations/project-tags.csv";

}
