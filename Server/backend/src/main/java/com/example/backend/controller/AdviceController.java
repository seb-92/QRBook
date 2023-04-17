package com.example.backend.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.example.backend.dto.MessageResponse;

@RestControllerAdvice
public class AdviceController
{
    @ExceptionHandler(
    { RuntimeException.class } )
    public ResponseEntity< Object > bookUnavailableExceptionHandler( RuntimeException exception )
    {
        return new ResponseEntity<>( new MessageResponse( exception.getMessage() ), HttpStatus.BAD_REQUEST );
    }
}
