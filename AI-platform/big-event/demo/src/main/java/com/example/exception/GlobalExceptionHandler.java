package com.example.exception;

import com.example.pojo.Result;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.validation.BindException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler({MethodArgumentNotValidException.class, BindException.class})
    public Result<Void> handleValidationException(Exception e) {
        String message;
        if (e instanceof MethodArgumentNotValidException ex) {
            message = ex.getBindingResult()
                    .getFieldErrors()
                    .stream()
                    .map(DefaultMessageSourceResolvable::getDefaultMessage)
                    .collect(Collectors.joining("; "));
        } else {
            BindException ex = (BindException) e;
            message = ex.getBindingResult()
                    .getFieldErrors()
                    .stream()
                    .map(DefaultMessageSourceResolvable::getDefaultMessage)
                    .collect(Collectors.joining("; "));
        }

        if (!StringUtils.hasLength(message)) {
            message = "参数校验失败";
        }
        return Result.error(message);
    }

    @ExceptionHandler(Exception.class)
    public Result<Void> handleException(Exception e) {
        e.printStackTrace();
        String message = StringUtils.hasLength(e.getMessage()) ? e.getMessage() : "操作失败";
        return Result.error(message);
    }
}
