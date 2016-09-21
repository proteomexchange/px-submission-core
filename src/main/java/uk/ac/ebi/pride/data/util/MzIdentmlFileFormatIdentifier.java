package uk.ac.ebi.pride.data.util;

/**
 * Project: px-submission-core
 * Package: uk.ac.ebi.pride.data.util
 * Timestamp: 2016-07-20 15:50
 * ---
 * © 2016 Manuel Bernal Llinares <mbdebian@gmail.com>
 * All rights reserved.
 */
public class MzIdentmlFileFormatIdentifier extends FileFormatIdentifier {
    @Override
    protected MassSpecFileFormat doIdentifyFormatFromContent(String content) {
        if (MassSpecFileRegx.MZIDENTML_PATTERN.matcher(content).find()) {
            return MassSpecFileFormat.MZIDENTML;
        }
        return null;
    }
}
