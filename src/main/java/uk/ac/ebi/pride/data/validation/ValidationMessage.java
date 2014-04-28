package uk.ac.ebi.pride.data.validation;

/**
 * Validation message shows the validation details
 *
 * @author Rui Wang
 * @version $Id$
 */
public class ValidationMessage {

    public enum Type {
        WARNING,
        ERROR,
        INFO,
        SUCCESS
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

    public ValidationMessage(Type type, String message) {
        this(null, type, message);
    }

    public ValidationMessage(Object source, Type type, String message) {
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ValidationMessage)) return false;

        ValidationMessage that = (ValidationMessage) o;

        if (message != null ? !message.equals(that.message) : that.message != null) return false;
        if (source != null ? !source.equals(that.source) : that.source != null) return false;
        if (type != that.type) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = source != null ? source.hashCode() : 0;
        result = 31 * result + (type != null ? type.hashCode() : 0);
        result = 31 * result + (message != null ? message.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return type.toString() + " { " + message + " }";
    }
}
