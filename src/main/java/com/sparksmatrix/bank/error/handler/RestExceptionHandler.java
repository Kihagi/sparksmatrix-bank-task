package com.sparksmatrix.bank.error.handler;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;
import static org.springframework.http.HttpStatus.NOT_FOUND;

import java.util.ArrayList;
import java.util.List;

import com.sparksmatrix.bank.error.exception.BadRequestException;
import com.sparksmatrix.bank.error.exception.EntityNotFoundException;
import com.sparksmatrix.bank.error.exception.PersistenceException;
import com.sparksmatrix.bank.error.payload.ApiError;
import com.sparksmatrix.bank.utils.AppConstants;
import com.sparksmatrix.bank.utils.ResponseWrapper;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.http.converter.HttpMessageNotWritableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.NoHandlerFoundException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import com.fasterxml.jackson.core.JsonProcessingException;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Order(Ordered.HIGHEST_PRECEDENCE)
@RestControllerAdvice
public class RestExceptionHandler extends ResponseEntityExceptionHandler {

	/**
	 * Handle MissingServletRequestParameterException. Triggered when a 'required'
	 * request parameter is missing.
	 *
	 * @param ex      MissingServletRequestParameterException
	 * @param headers HttpHeaders
	 * @param status  HttpStatus
	 * @param request WebRequest
	 * @return the ApiError object
	 */
	protected ResponseEntity<Object> handleMissingServletRequestParameter(
			MissingServletRequestParameterException ex,
			HttpHeaders headers, HttpStatus status, WebRequest request) {
		String error = ex.getParameterName() + " parameter is missing";
		return buildResponseEntity(new ApiError(BAD_REQUEST, error, ex));
	}

	@ExceptionHandler(value = Exception.class)
	protected ResponseEntity<Object> handle(Exception exception) {

		if (exception instanceof ConstraintViolationException) {
			ConstraintViolationException ex = (ConstraintViolationException) exception;
			List<String> errors = new ArrayList<>();
			for (ConstraintViolation<?> violation : ex.getConstraintViolations()) {
				errors.add(violation.getRootBeanClass().getName() + " " + violation.getPropertyPath()
						+ ": " + violation.getMessage());
			}
			ResponseWrapper response = ResponseWrapper.builder().code(HttpStatus.BAD_REQUEST.value())
					.message("Constraint validation errors").data(errors).build();
			// return ResponseEntity.status(response.getCode()).body(response);
			return new ResponseEntity<>(response, new HttpHeaders(), response.getCode());
		}
		exception.printStackTrace();
		ResponseWrapper response = ResponseWrapper.builder().code(HttpStatus.INTERNAL_SERVER_ERROR.value())
				.message(AppConstants.ServerResponses.INTERNAL_SERVER_ERROR)
				.data(exception.getMessage()).build();
		return ResponseEntity.status(response.getCode()).body(response);
	}

	/**
	 * Handle MethodArgumentNotValidException. Triggered when an object fails @Valid
	 * validation.
	 *
	 * @param ex      the MethodArgumentNotValidException that is thrown when @Valid
	 *                validation fails
	 * @param headers HttpHeaders
	 * @param status  HttpStatus
	 * @param request WebRequest
	 * @return the ApiError object
	 */
	@Override
	protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex,
																  HttpHeaders headers, HttpStatusCode status, WebRequest request) {
		ApiError apiError = new ApiError(BAD_REQUEST);
		apiError.setMessage("Validation error");
		apiError.addValidationErrors(ex.getBindingResult().getFieldErrors());
		apiError.addValidationError(ex.getBindingResult().getGlobalErrors());

		return buildResponseEntity(apiError);
	}


	/**
	 * Handles javax.validation.ConstraintViolationException. Thrown when @Validated
	 * fails.
	 *
	 * @param ex the ConstraintViolationException
	 * @return the ApiError object
	 */
	@ExceptionHandler(ConstraintViolationException.class)
	protected ResponseEntity<Object> handleConstraintViolation(ConstraintViolationException ex) {
		ApiError apiError = new ApiError(BAD_REQUEST);
		apiError.setMessage("Validation error");
		apiError.addValidationErrors(ex.getConstraintViolations());

		return buildResponseEntity(apiError);
	}

	/**
	 * Handles EntityNotFoundException. Created to encapsulate errors with more
	 * detail than javax.persistence.EntityNotFoundException.
	 *
	 * @param ex the EntityNotFoundException
	 * @return the ApiError object
	 */
	@ExceptionHandler(EntityNotFoundException.class)
	protected ResponseEntity<Object> handleEntityNotFound(EntityNotFoundException ex) {
		ApiError apiError = new ApiError(NOT_FOUND, "Entity not found", ex);

		return buildResponseEntity(apiError);
	}

	/**
	 * Handle persistence failure that is when saving/updating Encapsulates errors
	 * relating to PersistenceException
	 *
	 * @param ex the PersistenceException
	 * @return the ApiError object
	 */
	@ExceptionHandler(PersistenceException.class)
	protected ResponseEntity<Object> handlePersistenceFailure(PersistenceException ex) {
		ApiError apiError = new ApiError(INTERNAL_SERVER_ERROR);
		apiError.setMessage(ex.getMessage());

		return buildResponseEntity(apiError);
	}

	/**
	 * Handles errors arising from copying from entity to dto or vice versa
	 * JsonProcessingException
	 *
	 * @param ex the JsonProcessingException
	 * @return the ApiError object
	 */
	@ExceptionHandler(JsonProcessingException.class)
	protected ResponseEntity<Object> handleJsonProcessingFailure(JsonProcessingException ex) {
		ApiError apiError = new ApiError(INTERNAL_SERVER_ERROR);
		apiError.setMessage(ex.getMessage());

		return buildResponseEntity(apiError);
	}

	/**
	 * Handle requests that are invalid
	 *
	 * @param ex the BadRequestException
	 * @return the ApiError object
	 */
	@ExceptionHandler(BadRequestException.class)
	protected ResponseEntity<Object> handleBadRequests(BadRequestException ex) {
		ApiError apiError = new ApiError(BAD_REQUEST);
		apiError.setMessage(ex.getMessage());

		return buildResponseEntity(apiError);
	}

	/**
	 * Handle HttpMessageNotReadableException. Happens when request JSON is
	 * malformed.
	 *
	 * @param ex      HttpMessageNotReadableException
	 * @param headers HttpHeaders
	 * @param status  HttpStatus
	 * @param request WebRequest
	 * @return the ApiError object
	 */
    @Override
	protected ResponseEntity<Object> handleHttpMessageNotReadable(HttpMessageNotReadableException ex,
			HttpHeaders headers, HttpStatusCode status, WebRequest request) {
		ServletWebRequest servletWebRequest = (ServletWebRequest) request;
		log.info("{} to {}", servletWebRequest.getHttpMethod(),
				servletWebRequest.getRequest().getServletPath());
		String error = "Malformed JSON request";

		return buildResponseEntity(new ApiError(HttpStatus.BAD_REQUEST, error, ex));
	}

	/**
	 * Handle HttpMessageNotWritableException.
	 *
	 * @param ex      HttpMessageNotWritableException
	 * @param headers HttpHeaders
	 * @param status  HttpStatus
	 * @param request WebRequest
	 * @return the ApiError object
	 */
	@Override
	protected ResponseEntity<Object> handleHttpMessageNotWritable(HttpMessageNotWritableException ex,
			HttpHeaders headers, HttpStatusCode status, WebRequest request) {
		String error = "Error writing JSON output";
		return buildResponseEntity(new ApiError(HttpStatus.INTERNAL_SERVER_ERROR, error, ex));
	}

	/**
	 * Handle NoHandlerFoundException.
	 *
	 * @param ex      NoHandlerFoundException
	 * @param headers HttpHeaders
	 * @param status  HttpStatus
	 * @param request WebRequest
	 * @return the ApiError object
	 */
//	@Override
	protected ResponseEntity<Object> handleNoHandlerFoundException(NoHandlerFoundException ex, HttpHeaders headers,
			HttpStatus status, WebRequest request) {
		ApiError apiError = new ApiError(BAD_REQUEST);
		apiError.setMessage(
				String.format("Could not find the %s method for URL %s", ex.getHttpMethod(),
						ex.getRequestURL()));
		apiError.setDebugMessage(ex.getMessage());
		return buildResponseEntity(apiError);
	}

	/**
	 * Handle DataIntegrityViolationException, inspects the cause for different DB
	 * causes.
	 *
	 * @param request WebRequest
	 * @param ex      the DataIntegrityViolationException
	 * @return the ApiError object
	 */
	@ExceptionHandler(DataIntegrityViolationException.class)
	protected ResponseEntity<Object> handleDataIntegrityViolation(DataIntegrityViolationException ex,
			WebRequest request) {
		if (ex.getCause() instanceof ConstraintViolationException) {
			return buildResponseEntity(new ApiError(HttpStatus.CONFLICT, "Database error", ex.getCause()));
		}
		return buildResponseEntity(new ApiError(HttpStatus.INTERNAL_SERVER_ERROR, ex));
	}

	/**
	 * Handle Exception, handle generic Exception.class
	 *
	 * @param request WebRequest
	 * @param ex      the Exception
	 * @return the ApiError object
	 */
	@ExceptionHandler(MethodArgumentTypeMismatchException.class)
	protected ResponseEntity<Object> handleMethodArgumentTypeMismatch(MethodArgumentTypeMismatchException ex,
			WebRequest request) {
		ApiError apiError = new ApiError(BAD_REQUEST);
		apiError.setMessage(
				String.format("The parameter '%s' of value '%s' could not be converted to type '%s'",
				ex.getName(), ex.getValue(), ex.getRequiredType().getSimpleName()));
		apiError.setDebugMessage(ex.getMessage());
		return buildResponseEntity(apiError);
	}

	private ResponseEntity<Object> buildResponseEntity(ApiError apiError) {

		ResponseWrapper response = ResponseWrapper.builder().code(apiError.getStatus().value())
				.message(apiError.getMessage()).data(apiError.getDebugMessage()).build();
		return ResponseEntity.status(apiError.getStatus()).body(response);

	}
}
