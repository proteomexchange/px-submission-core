package uk.ac.ebi.pride.data.mztab.parser;

import uk.ac.ebi.pride.data.mztab.parser.exceptions.LineItemParsingHandlerException;

/**
 * Project: px-submission-core
 * Package: uk.ac.ebi.pride.data.mztab.parser
 * Timestamp: 2016-08-24 13:30
 * ---
 * © 2016 Manuel Bernal Llinares <mbdebian@gmail.com>
 * All rights reserved.
 */
public class QuickMzTabInstrumentDetectorLineItemParsingHandler extends MzTabInstrumentDetectorLineItemParsingHandler {
    // Check for duplicated entry
    private void checkForDuplicatedEntry(MzTabParser context, long lineNumber) throws LineItemParsingHandlerException {
        if (getInstrumentFromContext(context, getIndex()).getDetector() != null) {
            throw new LineItemParsingHandlerException("DUPLICATED entry for instrument detector FOUND AT LINE " + lineNumber);
        }
    }

    @Override
    protected boolean doProcessEntry(MzTabParser context, long lineNumber, long offset) throws LineItemParsingHandlerException {
        checkForDuplicatedEntry(context, lineNumber);
        getInstrumentFromContext(context, getIndex()).setDetector(CvParameterParser.fromString(getPropertyValue()));
        return true;
    }
}
