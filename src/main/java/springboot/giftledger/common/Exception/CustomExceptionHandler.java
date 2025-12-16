package springboot.giftledger.common.Exception;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import springboot.giftledger.common.dto.ExceptionResult;

@RestControllerAdvice
public class CustomExceptionHandler {
	
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ExceptionResult> handleIllegalArgumentException(Exception e) {
    	
    	ExceptionResult resultDto = ExceptionResult.builder()
							    				   .result(e.getClass().getSimpleName())
							    				   .message(e.getMessage())
							    				   .build();
    	
    	return ResponseEntity
                .status(400)
                .body(resultDto);
    }
    
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ExceptionResult> handleAccessDeniedException(Exception e) {
    	
    	ExceptionResult resultDto = ExceptionResult.builder()
    			.result(e.getClass().getSimpleName())
    			.message(e.getMessage())
    			.build();
    	
    	return ResponseEntity
    			.status(401)
    			.body(resultDto);
    }
    
    @ExceptionHandler(SecurityException.class)
    public ResponseEntity<ExceptionResult> handleSecurityException(Exception e) {
    	
    	ExceptionResult resultDto = ExceptionResult.builder()
    			.result(e.getClass().getSimpleName())
    			.message(e.getMessage())
    			.build();
    	
    	return ResponseEntity
    			.status(401)
    			.body(resultDto);
    }
    
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ExceptionResult> handleException(Exception e) {
    	
    	ExceptionResult resultDto = ExceptionResult.builder()
    			.result(e.getClass().getSimpleName())
    			.message(e.getMessage())
    			.build();
    	
    	return ResponseEntity
    			.status(500)
    			.body(resultDto);
    }

}
