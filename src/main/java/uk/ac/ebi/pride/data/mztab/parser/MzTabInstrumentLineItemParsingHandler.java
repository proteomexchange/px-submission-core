package uk.ac.ebi.pride.data.mztab.parser;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.ac.ebi.pride.data.mztab.model.Instrument;
import uk.ac.ebi.pride.data.mztab.parser.exceptions.LineItemParsingHandlerException;
import uk.ac.ebi.pride.data.mztab.parser.exceptions.MetadataLineItemParserStrategyException;

/**
 * Project: px-submission-core
 * Package: uk.ac.ebi.pride.data.mztab.parser
 * Timestamp: 2016-08-24 11:12
 * ---
 * © 2016 Manuel Bernal Llinares <mbdebian@gmail.com>
 * All rights reserved.
 */
public abstract class MzTabInstrumentLineItemParsingHandler extends MetaDataLineItemParsingHandler implements MetaDataLineItemParsingHandler.IndexedLineItemWithIndexedPropertyDataEntry {
    private static final Logger logger = LoggerFactory.getLogger(MzTabInstrumentLineItemParsingHandler.class);

    protected static final String MZTAB_INSTRUMENT_ITEM_KEY = "instrument";
    // TODO - Refactor out the bean part
    // Bean Defaults
    protected static final String DEFAULT_LINE_ITEM_KEY = "";
    protected static final int DEFAULT_INDEX = -1;
    protected static final String DEFAULT_PROPERTY_KEY = "";
    protected static final String DEFAULT_PROPERTY_VALUE = "";
    protected static final int DEFAULT_PROPERTY_ENTRY_INDEX = -1;
    // Bean attributes
    private String lineItemKey = DEFAULT_LINE_ITEM_KEY;
    private int index = DEFAULT_INDEX;
    private String propertyKey = DEFAULT_PROPERTY_KEY;
    private String propertyValue = DEFAULT_PROPERTY_VALUE;
    private int propertyEntryIndex = DEFAULT_PROPERTY_ENTRY_INDEX;

    @Override
    public String getLineItemKey() {
        return lineItemKey;
    }

    @Override
    public void setLineItemKey(String lineItemKey) {
        this.lineItemKey = lineItemKey;
    }

    @Override
    public int getIndex() {
        return index;
    }

    @Override
    public void setIndex(int index) {
        this.index = index;
    }

    @Override
    public String getPropertyKey() {
        return propertyKey;
    }

    @Override
    public void setPropertyKey(String propertyKey) {
        this.propertyKey = propertyKey;
    }

    @Override
    public String getPropertyValue() {
        return propertyValue;
    }

    @Override
    public void setPropertyValue(String propertyValue) {
        this.propertyValue = propertyValue;
    }

    @Override
    public int getPropertyEntryIndex() {
        return propertyEntryIndex;
    }

    @Override
    public void setPropertyEntryIndex(int propertyEntryIndex) {
        this.propertyEntryIndex = propertyEntryIndex;
    }

    private void cleanBean() {
        lineItemKey = DEFAULT_LINE_ITEM_KEY;
        index = DEFAULT_INDEX;
        propertyKey = DEFAULT_PROPERTY_KEY;
        propertyValue = DEFAULT_PROPERTY_VALUE;
        propertyEntryIndex = DEFAULT_PROPERTY_ENTRY_INDEX;
    }

    protected Instrument getInstrumentFromContext(MzTabParser context, int instrumentIndex) {
        Instrument instrument = context.getMetaDataSection().getInstrument(instrumentIndex);
        if (instrument == null) {
            instrument = new Instrument();
            context.getMetaDataSection().updateInstrument(instrument, instrumentIndex);
        }
        return instrument;
    }

    @Override
    protected boolean doParseLineItem(MzTabParser context, String line, long lineNumber, long offset) throws LineItemParsingHandlerException {
        // TODO - I should probably refactor this code out to a superclass for all those subclasses dealing with indexed
        // TODO - line items, with or without properties share the same code
        cleanBean();
        try {
            if (MetadataLineItemParserStrategy.parseLine(this, line)) {
                if (getLineItemKey().equals(MZTAB_INSTRUMENT_ITEM_KEY)) {
                    // The line item key is ok, go ahead
                    // check that the line item index has been set up
                    if (getIndex() == DEFAULT_INDEX) {
                        throw new LineItemParsingHandlerException("MISSING INDEX for '" + MZTAB_INSTRUMENT_ITEM_KEY + "'");
                    }
                    return processEntry(context, lineNumber, offset);
                }
            }
        } catch (MetadataLineItemParserStrategyException e) {
            throw new LineItemParsingHandlerException(e.getMessage());
        }
        return false;
    }

    // Delegate processing
    protected abstract boolean processEntry(MzTabParser context, long lineNumber, long offset);
}
