package uk.ac.ebi.pride.data.util;

/**
 * Project: px-submission-core
 * Package: uk.ac.ebi.pride.data.util
 * Timestamp: 2016-07-20 15:41
 * ---
 * © 2016 Manuel Bernal Llinares <mbdebian@gmail.com>
 * All rights reserved.
 */
public class PrideFileFormatIdentifier extends FileFormatIdentifier {
    @Override
    protected MassSpecFileFormat doIdentifyFormatFromContent(String content) {
        if (MassSpecFileRegx.PRIDE_XML_PATTERN.matcher(content).find()) {
            return MassSpecFileFormat.PRIDE;
        }
        return null;
    }
}
