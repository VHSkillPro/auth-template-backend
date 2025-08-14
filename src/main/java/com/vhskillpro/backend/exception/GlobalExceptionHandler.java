package com.vhskillpro.backend.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.vhskillpro.backend.common.constants.MessageConstants;
import com.vhskillpro.backend.common.response.ApiResponse;

@RestControllerAdvice
public class GlobalExceptionHandler {
  private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

  /**
   * Handles exceptions of type {@link AppException} thrown within the
   * application.
   *
   * @param ex the {@link AppException} instance that was thrown
   * @return an {@link ApiResponse} containing information about the exception
   */
  @ExceptionHandler(AppException.class)
  public ApiResponse<Void> handleAppException(AppException ex) {
    return ApiResponse.from(ex);
  }

  /**
   * Handles all uncaught exceptions thrown within the application.
   * Logs the exception details and returns a standardized internal server error
   * response.
   *
   * @param ex the exception that was thrown
   * @return an {@link ApiResponse} indicating an internal server error
   */
  @ExceptionHandler(Exception.class)
  public ApiResponse<Void> handleException(Exception ex) {
    logger.error(MessageConstants.INTERNAL_SERVER_ERROR.getMessage(), ex);
    return ApiResponse.internalServerError(MessageConstants.INTERNAL_SERVER_ERROR.getMessage());
  }
}
