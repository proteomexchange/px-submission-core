package uk.ac.ebi.pride.data.util;

/**
 * Project: px-submission-core
 * Package: uk.ac.ebi.pride.data.util
 * Timestamp: 2016-07-20 15:52
 * ---
 * © 2016 Manuel Bernal Llinares <mbdebian@gmail.com>
 * All rights reserved.
 */
public class MzDataFileFormatIdentifier extends FileFormatIdentifier {
    @Override
    protected MassSpecFileFormat doIdentifyFormatFromContent(String content) {
        if (MassSpecFileRegx.MZDATA_PATTERN.matcher(content).find()) {
            return MassSpecFileFormat.MZDATA;
        }
        return null;
    }
}
