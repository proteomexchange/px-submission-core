package uk.ac.ebi.pride.data.mztab.parser;

import uk.ac.ebi.pride.data.mztab.parser.exceptions.LineItemParsingHandlerException;

/**
 * Project: px-submission-core
 * Package: uk.ac.ebi.pride.data.mztab.parser
 * Timestamp: 2016-08-24 13:27
 * ---
 * © 2016 Manuel Bernal Llinares <mbdebian@gmail.com>
 * All rights reserved.
 */
public class QuickMzTabInstrumentSourceLineItemParsingHandler extends MzTabInstrumentSourceLineItemParsingHandler {
    // Check for duplicated entry
    private void checkForDuplicatedEntry(MzTabParser context, long lineNumber) throws LineItemParsingHandlerException {
        if (getInstrumentFromContext(context, getIndex()).getSource() != null) {
            throw new LineItemParsingHandlerException("DUPLICATED entry for instrument source FOUND AT LINE " + lineNumber);
        }
    }

    @Override
    protected boolean doProcessEntry(MzTabParser context, long lineNumber, long offset) throws LineItemParsingHandlerException {
        checkForDuplicatedEntry(context, lineNumber);
        getInstrumentFromContext(context, getIndex()).setSource(CvParameterParser.fromString(getPropertyValue()));
        return true;
    }
}
