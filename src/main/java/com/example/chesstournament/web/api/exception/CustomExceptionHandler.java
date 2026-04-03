package com.example.chesstournament.web.api.exception;

import java.util.List;
import java.util.stream.Collectors;

import com.example.chesstournament.security.dto.ResponseBusta;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class CustomExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ResponseBusta<String>> handleValidationExceptions(MethodArgumentNotValidException ex) {

        List<String> errors = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(x -> x.getDefaultMessage())
                .collect(Collectors.toList());

        String messaggioErroreCompleto = String.join(", ", errors);


        ResponseBusta<String> bustaErrore = ResponseBusta.error(400, messaggioErroreCompleto);

        return ResponseEntity.status(400).body(bustaErrore);
    }

    @ExceptionHandler(Forbidden403Exception.class)
    public ResponseEntity<ResponseBusta<String>> handleForbidden403Exception(Forbidden403Exception ex){

        ResponseBusta<String> bustaErrore = ResponseBusta.error(403, ex.getMessage());
        return ResponseEntity.status(403).body(bustaErrore);
    }

    @ExceptionHandler(NotFound404Exception.class)
    public ResponseEntity<ResponseBusta<String>> handleNotFound404Exception(NotFound404Exception ex){

        ResponseBusta<String> bustaErrore = ResponseBusta.error(404, ex.getMessage());
        return ResponseEntity.status(404).body(bustaErrore);
    }

    @ExceptionHandler(Unauthorized401Exception.class)
    public ResponseEntity<ResponseBusta<String>> handleUnauthorized401Exception(Unauthorized401Exception ex){
        ResponseBusta<String> bustaErrore = ResponseBusta.error(401, ex.getMessage());
        return ResponseEntity.status(401).body(bustaErrore);
    }

    @ExceptionHandler(BadRequest400Exception.class)
    public ResponseEntity<ResponseBusta<String>> handleBadRequest400Exception(BadRequest400Exception ex){
        ResponseBusta<String> bustaErrore = ResponseBusta.error(400, ex.getMessage());
        return ResponseEntity.status(400).body(bustaErrore);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ResponseBusta<String>> handleAllOtherExceptions(Exception ex) {
        ex.printStackTrace();
        ResponseBusta<String> bustaErrore = ResponseBusta.error(500, "Errore interno del server: " + ex.getMessage());
        return ResponseEntity.status(500).body(bustaErrore);
    }

    @ExceptionHandler(CustomPlayerException.class)
    public ResponseEntity<ResponseBusta<String>> handleCustomPlayerException(Exception ex){
        ex.printStackTrace();
        ResponseBusta<String> bustaErrore = ResponseBusta.error(400, ex.getMessage());
        return ResponseEntity.status(400).body(bustaErrore);
    }




}

