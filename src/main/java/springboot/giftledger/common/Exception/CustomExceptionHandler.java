package springboot.giftledger.common.Exception;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import springboot.giftledger.common.dto.ExceptionResult;

@RestControllerAdvice
public class CustomExceptionHandler {
	
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ExceptionResult> handleUnexpectedException(Exception e) {
    	
    	ExceptionResult resultDto = ExceptionResult.builder()
							    				   .result(e.getClass().getName())
							    				   .message(e.getMessage())
							    				   .build();
    	
    	return ResponseEntity
                .status(400)
                .body(resultDto);
    }

}
