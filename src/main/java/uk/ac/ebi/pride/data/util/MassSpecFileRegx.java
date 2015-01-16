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


    public static final Pattern PRIDE_XML_PATTERN = Pattern.compile("^[^<]*(<\\?xml [^>]*>\\s*(<!--[^>]*-->\\s*)*)?<ExperimentCollection [^>]*>", Pattern.MULTILINE | Pattern.CASE_INSENSITIVE);

    public static final Pattern MZML_PATTERN = Pattern.compile("^[^<]*(<\\?xml [^>]*>\\s*(<!--[^>]*-->\\s*)*)?<mzML [^>]*", Pattern.MULTILINE | Pattern.CASE_INSENSITIVE);

    public static final Pattern INDEXED_MZML_PATTERN = Pattern.compile("^[^<]*(<\\?xml [^>]*>\\s*(<!--[^>]*-->\\s*)*)?<indexedmzML [^>]*", Pattern.MULTILINE | Pattern.CASE_INSENSITIVE);

    public static final Pattern MZIDENTML_PATTERN = Pattern.compile("^[^<]*(<\\?xml [^>]*>\\s*(<!--[^>]*-->\\s*)*)?<(MzIdentML)|(indexedmzIdentML) [^>]*", Pattern.MULTILINE | Pattern.CASE_INSENSITIVE);

    public static final Pattern MZXML_PATTERN = Pattern.compile("^[^<]*(<\\?xml [^>]*>\\s*(<!--[^>]*-->\\s*)*)?<(mzXML) [^>]*", Pattern.MULTILINE | Pattern.CASE_INSENSITIVE);

    public static final Pattern MZDATA_PATTERN = Pattern.compile("^[^<]*(<\\?xml [^>]*>\\s*(<!--[^>]*-->\\s*)*)?<(mzData) [^>]*", Pattern.MULTILINE | Pattern.CASE_INSENSITIVE);
}
