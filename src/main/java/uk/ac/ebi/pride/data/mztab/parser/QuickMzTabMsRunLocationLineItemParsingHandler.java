package uk.ac.ebi.pride.data.mztab.parser;

import uk.ac.ebi.pride.data.mztab.parser.exceptions.LineItemParsingHandlerException;

import java.net.MalformedURLException;
import java.net.URL;

/**
 * Project: px-submission-tool
 * Package: uk.ac.ebi.pride.gui.data.mztab.parser
 * Timestamp: 2016-06-21 12:15
 * ---
 * © 2016 Manuel Bernal Llinares <mbdebian@gmail.com>
 * All rights reserved.
 *
 * Quick processing strategy for ms-run location entries
 */

public class QuickMzTabMsRunLocationLineItemParsingHandler extends MzTabMsRunLocationLineItemParsingHandler {
    // TODO - NOTE - Checking for duplicated entries should be performed at a higher level of the hierarchy, as it is a
    // TODO - NOTE - common policy to all those subclasses actually processing a particular entry. At some point in the
    // TODO - NOTE - future, it should be refactor out, and promoted up in the class hierarchy
    private void checkForDuplicatedEntry(MzTabParser context, long lineNumber) throws LineItemParsingHandlerException {
        if (getMsRunFromContext(context, getIndex()).hasLocationBeenSeen() || (getMsRunFromContext(context, getIndex()).getLocation() != null)) {
            throw new LineItemParsingHandlerException("DUPLICATED MS-Run location entry FOUND AT LINE " + lineNumber);
        }
    }

    @Override
    protected boolean doProcessEntry(MzTabParser context, long lineNumber, long offset) throws LineItemParsingHandlerException {
        checkForDuplicatedEntry(context, lineNumber);
        // Process entry
        try {
            if (!getPropertyValue().trim().equals("null")) {
                context.getMetaDataSection().getMsRunEntry(getIndex()).setLocation(new URL(getPropertyValue()));
            }
            // I've seen a location entry for this ms-run
            context.getMetaDataSection().getMsRunEntry(getIndex()).setLocationSeen();
        } catch (MalformedURLException e) {
            throw new LineItemParsingHandlerException(e.getMessage());
        }
        return true;
    }
}
