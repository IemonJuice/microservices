package microservices.lab1.common.exception;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class ErrorResponse {
    private int status;
    private String error;
    private String message;
    private List<String> errors;
}