package uk.ac.ebi.pride.data.validation;

import java.util.ArrayList;
import java.util.List;

/**
 * {@code ValidationLog} encapsulate a list of validation messages
 *
 * @author Rui Wang
 * @version $Id$
 */
public class ValidationReport {

    private final List<ValidationMessage> validationMessages;

    public ValidationReport() {
        this.validationMessages = new ArrayList<ValidationMessage>();
    }

    public ValidationReport combine(ValidationReport validationReport) {
        this.validationMessages.addAll(validationReport.getMessages());
        return this;
    }

    public void addMessages(List<ValidationMessage> messages) {
        this.validationMessages.addAll(messages);
    }

    public void addMessage(ValidationMessage message) {
        this.validationMessages.add(message);
    }

    public void removeMessage(ValidationMessage message) {
        this.validationMessages.remove(message);
    }

    public List<ValidationMessage> getMessages() {
        return validationMessages;
    }

    public boolean hasError() {
        return hasMessageType(ValidationMessage.Type.ERROR);
    }

    public boolean hasWarning() {
        return hasMessageType(ValidationMessage.Type.WARNING);
    }

    public boolean hasInfo() {
        return hasMessageType(ValidationMessage.Type.INFO);
    }

    public boolean hasSuccess() {
        return hasMessageType(ValidationMessage.Type.SUCCESS);
    }

    private boolean hasMessageType(ValidationMessage.Type type) {
        for (ValidationMessage validationMessage : validationMessages) {
            if (validationMessage.getType().equals(type)) {
                return true;
            }
        }
        return false;
    }
}
