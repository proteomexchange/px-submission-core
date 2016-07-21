package uk.ac.ebi.pride.data.util;

/**
 * Project: px-submission-core
 * Package: uk.ac.ebi.pride.data.util
 * Timestamp: 2016-07-20 14:59
 * ---
 * © 2016 Manuel Bernal Llinares <mbdebian@gmail.com>
 * All rights reserved.
 *
 * Identifies a file format based on the provided information, designed as a chain of responsibility where the last
 * handler will return null in case it can't identify the file format
 */
public abstract class FileFormatIdentifier {
    private FileFormatIdentifier nextHandler = null;

    public FileFormatIdentifier getNextHandler() {
        return nextHandler;
    }

    public void setNextHandler(FileFormatIdentifier nextHandler) {
        this.nextHandler = nextHandler;
    }

    public MassSpecFileFormat identifyFormatFromContent(String content) {
        MassSpecFileFormat format = null;
        if ((content != null) && (!content.isEmpty())) {
            format = doIdentifyFormatFromContent(content);
            if ((format == null) && (getNextHandler() != null)) {
                return getNextHandler().identifyFormatFromContent(content);
            }
        }
        return format;
    }

    // Delegate format identification to subclasses
    protected abstract MassSpecFileFormat doIdentifyFormatFromContent(String content);
}
