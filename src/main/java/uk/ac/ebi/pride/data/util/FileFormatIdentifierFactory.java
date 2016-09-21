package uk.ac.ebi.pride.data.util;

/**
 * Project: px-submission-core
 * Package: uk.ac.ebi.pride.data.util
 * Timestamp: 2016-07-20 16:09
 * ---
 * © 2016 Manuel Bernal Llinares <mbdebian@gmail.com>
 * All rights reserved.
 */
public class FileFormatIdentifierFactory {
    private static FileFormatIdentifier fileFormatIdentifier = null;

    public static FileFormatIdentifier getFileFormatIdentifier() {
        if (fileFormatIdentifier == null) {
            fileFormatIdentifier = buildChainOfIdentifiers();
        }
        return fileFormatIdentifier;
    }

    private static FileFormatIdentifier buildChainOfIdentifiers() {
        FileFormatIdentifier[] fileFormatIdentifiers = new FileFormatIdentifier[]{
                new PrideFileFormatIdentifier(),
                new IndexedMzmlFileFormatIdentifier(),
                new MzmlFileFormatIdentifier(),
                new MzIdentmlFileFormatIdentifier(),
                new MzXmlFileFormatIdentifier(),
                new MzDataFileFormatIdentifier(),
                new MzTabFileFormatIdentifier()
        };
        // Setup the chain of responsibility
        for (int i = 0; i < (fileFormatIdentifiers.length - 1); i++) {
            fileFormatIdentifiers[i].setNextHandler(fileFormatIdentifiers[i + 1]);
        }
        return fileFormatIdentifiers[0];
    }
}
