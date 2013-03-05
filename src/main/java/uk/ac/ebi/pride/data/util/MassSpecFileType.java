package uk.ac.ebi.pride.data.util;

/**
 * Type of mass spec file
 *
 * @author Rui Wang
 * @version $Id$
 */
public enum MassSpecFileType {
    // Result file which contains identification, could be either PRIDE XML or mzIdentML
    RESULT,

    // Raw files from instrument output
    RAW,

    // Search engine output file, such as: Mascot DAT
    SEARCH,

    // Processed peak list file, such as: MGF
    PEAK,

    // Other files to support the experiment, such as: quantification in a excel sheet
    OTHER
}
