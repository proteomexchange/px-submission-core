package uk.ac.ebi.pride.data.exception;

/**
 * Exception for invalid submission file format
 *
 * @author Rui Wang
 * @version $Id$
 */
public class SubmissionFileException extends Exception{

    public SubmissionFileException() {
        this("Invalid submission submission file");
    }

    public SubmissionFileException(String message) {
        super(message);
    }

    public SubmissionFileException(String message, Throwable cause) {
        super(message, cause);
    }

    public SubmissionFileException(Throwable cause) {
        super(cause);
    }
}
