package uk.ac.ebi.pride.data.util;

/**
 * Different submission types
 *
 * @author Rui Wang
 * @version $Id$
 */
public enum SubmissionType {
    // Supported: contains supported result files, raw file and optionally other files
    SUPPORTED,
    // Unsupported: contains non-supported result files, raw file and optionally other files
    UNSUPPORTED,
    // Raw only: contains only raw files
    RAW_ONLY
}
