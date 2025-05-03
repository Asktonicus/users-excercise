package cl.exercise.users.exception;

public class EmailExistException extends RuntimeException {

    public EmailExistException(String msg) {
        super(msg);
    }
}
