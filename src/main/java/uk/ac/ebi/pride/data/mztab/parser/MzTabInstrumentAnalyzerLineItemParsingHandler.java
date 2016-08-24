package uk.ac.ebi.pride.data.mztab.parser;

import uk.ac.ebi.pride.data.mztab.parser.exceptions.LineItemParsingHandlerException;

/**
 * Project: px-submission-core
 * Package: uk.ac.ebi.pride.data.mztab.parser
 * Timestamp: 2016-08-24 13:58
 * ---
 * © 2016 Manuel Bernal Llinares <mbdebian@gmail.com>
 * All rights reserved.
 */
public abstract class MzTabInstrumentAnalyzerLineItemParsingHandler extends MzTabInstrumentLineItemParsingHandler {
    protected static final String MZTAB_INSTRUMENT_ANALYZER_PROPERTY_KEY = "analyzer";

    @Override
    protected boolean processEntry(MzTabParser context, long lineNumber, long offset) {
        if (getPropertyKey().equals(MZTAB_INSTRUMENT_ANALYZER_PROPERTY_KEY)) {
            if (getPropertyEntryIndex() == DEFAULT_PROPERTY_ENTRY_INDEX) {
                throw new LineItemParsingHandlerException("Missing index for property key '" + MZTAB_INSTRUMENT_ANALYZER_PROPERTY_KEY + "'");
            }
            return doProcessEntry(context, lineNumber, offset);
        }
        return false;
    }

    // Delegate processing strategy
    protected abstract boolean doProcessEntry(MzTabParser context, long lineNumber, long offset) throws LineItemParsingHandlerException;
}
