package uk.ac.ebi.pride.data.mztab.parser;

import uk.ac.ebi.pride.data.mztab.parser.exceptions.LineItemParsingHandlerException;

/**
 * Project: px-submission-core
 * Package: uk.ac.ebi.pride.data.mztab.parser
 * Timestamp: 2016-08-24 13:28
 * ---
 * © 2016 Manuel Bernal Llinares <mbdebian@gmail.com>
 * All rights reserved.
 */
public abstract class MzTabInstrumentDetectorLineItemParsingHandler extends MzTabInstrumentLineItemParsingHandler {
    protected static final String MZTAB_INSTRUMENT_DETECTOR_PROPERTY_KEY = "detector";

    @Override
    protected boolean processEntry(MzTabParser context, long lineNumber, long offset) {
        if (getPropertyKey().equals(MZTAB_INSTRUMENT_DETECTOR_PROPERTY_KEY)) {
            // This property is NOT indexed, thus, we don't check property index
            return doProcessEntry(context, lineNumber, offset);
        }
        return false;
    }

    // Delegate processing strategy
    protected abstract boolean doProcessEntry(MzTabParser context, long lineNumber, long offset) throws LineItemParsingHandlerException;
}
