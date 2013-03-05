package uk.ac.ebi.pride.data.validation;

/**
 * Validation message shows the validation details
 *
 * @author Rui Wang
 * @version $Id$
 */
public class SubmissionValidationMessage {
    public enum Type {
        WARNING,
        ERROR
    }

    /**
     * Source of the message
     */
    private Object source;
    /**
     * Type of the message
     */
    private Type type;
    /**
     * Message indicates the reason
     */
    private String message;

    public SubmissionValidationMessage(Type type, String message) {
        this(null, type, message);
    }

    public SubmissionValidationMessage(Object source, Type type, String message) {
        this.source = source;
        this.type = type;
        this.message = message;
    }

    public Object getSource() {
        return source;
    }

    public void setSource(Object source) {
        this.source = source;
    }

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
