package co.za.tut.student.gateway_service.exception;


import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GatewayExceptionHandler {

    @ExceptionHandler(Exception.class)
    public ProblemDetail handleException(Exception ex) {

        ProblemDetail problem =
                ProblemDetail.forStatusAndDetail(
                        HttpStatus.INTERNAL_SERVER_ERROR,
                        ex.getMessage()
                );

        problem.setTitle("Gateway Error");

        return problem;
    }
}