package com.app.retail.exception;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@ControllerAdvice
public class ProductExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<?> notValid(MethodArgumentNotValidException ex, HttpServletRequest request) {
        List<String> errors = new ArrayList<>();
        ex.getAllErrors().forEach(err -> errors.add(err.getDefaultMessage()));
        Map<String, List<String>> result = new HashMap<>();
        result.put("errors", errors);
        return new ResponseEntity<>(result, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler (value = ProductNotFoundException.class)
    public ResponseEntity<Object> handleProductNotFoundException(ProductNotFoundException ex) {
        return new ResponseEntity<>("Product Not Found", HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler (value = ActiveProductsNotFoundException.class)
    public ResponseEntity<Object> handleActiveProductsNotFoundException(ActiveProductsNotFoundException ex) {
        return new ResponseEntity<>("No Active Product Found", HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler (value = ApprovalQueueNotFoundException.class)
    public ResponseEntity<Object> handleActiveProductsNotFoundException(ApprovalQueueNotFoundException ex) {
        return new ResponseEntity<>("No Approval queues data found", HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler (value = PriceLimitExceedException.class)
    public ResponseEntity<Object> handleActiveProductsNotFoundException(PriceLimitExceedException ex) {
        return new ResponseEntity<>("Price limit more than 10000. Please enter less than 10000", HttpStatus.BAD_REQUEST);
    }
}
