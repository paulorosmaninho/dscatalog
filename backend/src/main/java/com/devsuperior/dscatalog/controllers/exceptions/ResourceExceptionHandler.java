package com.devsuperior.dscatalog.controllers.exceptions;

import java.time.Instant;

import javax.servlet.http.HttpServletRequest;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import com.devsuperior.dscatalog.services.exceptions.DatabaseException;
import com.devsuperior.dscatalog.services.exceptions.ResourceNotFoundException;


@ControllerAdvice
public class ResourceExceptionHandler {
	
	@ExceptionHandler(ResourceNotFoundException.class)
	public ResponseEntity<StandardError> resourceNotFound(ResourceNotFoundException e, HttpServletRequest request){
		String error = "Recurso não encontrado.";
		HttpStatus status = HttpStatus.NOT_FOUND;
		StandardError stdError = new StandardError();
		
		stdError.setTimestampUTC(Instant.now());
		stdError.setStatus(status.value());
		stdError.setError(error);
		stdError.setMessage(e.getMessage());
		stdError.setPath(request.getRequestURI());
		
		return ResponseEntity.status(status).body(stdError);
	}

	@ExceptionHandler(DatabaseException.class)
	public ResponseEntity<StandardError> resourceNotFound(DatabaseException e, HttpServletRequest request){
		String error = "Erro de violação de integridade.";
		HttpStatus status = HttpStatus.BAD_REQUEST;
		StandardError stdError = new StandardError();
		
		stdError.setTimestampUTC(Instant.now());
		stdError.setStatus(status.value());
		stdError.setError(error);
		stdError.setMessage(e.getMessage());
		stdError.setPath(request.getRequestURI());
		
		return ResponseEntity.status(status).body(stdError);
	}


}
