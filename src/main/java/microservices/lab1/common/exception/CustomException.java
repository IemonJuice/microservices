package microservices.lab1.common.exception;

public class CustomException extends RuntimeException {

    public CustomException(String message) {
        super(message);
    }
}