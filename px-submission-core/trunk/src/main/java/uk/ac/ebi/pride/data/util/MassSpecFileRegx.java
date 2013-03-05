package uk.ac.ebi.pride.data.util;

import java.util.regex.Pattern;

/**
 * This utility class contains regular expressions for recognizing mass spec file format
 *
 * @author Rui Wang
 * @version $Id$
 */
public class MassSpecFileRegx {

    private MassSpecFileRegx() {
    }


    public static final Pattern PRIDE_XML_PATTERN = Pattern.compile("^(<\\?xml [^>]*>\\s*(<!--[^>]*-->\\s*)*){0,1}<ExperimentCollection [^>]*>", Pattern.MULTILINE | Pattern.CASE_INSENSITIVE);

    public static final Pattern MZML_PATTERN = Pattern.compile("^(<\\?xml [^>]*>\\s*(<!--[^>]*-->\\s*)*){0,1}<(mzML)|(indexedmzML) xmlns=.*", Pattern.MULTILINE | Pattern.CASE_INSENSITIVE);

    public static final Pattern MZIDENTML_PATTERN = Pattern.compile("^(<\\?xml [^>]*>\\s*(<!--[^>]*-->\\s*)*){0,1}<mzIdentML [^>]*>", Pattern.MULTILINE | Pattern.CASE_INSENSITIVE);

    public static final Pattern MZXML_PATTERN = Pattern.compile("^(<\\?xml [^>]*>\\s*(<!--[^>]*-->\\s*)*){0,1}<mzXML .*", Pattern.MULTILINE | Pattern.CASE_INSENSITIVE);

    public static final Pattern MZDATA_PATTERN = Pattern.compile("^(<\\?xml [^>]*>\\s*(<!--[^>]*-->\\s*)*){0,1}<mzData .*", Pattern.MULTILINE | Pattern.CASE_INSENSITIVE);
}
