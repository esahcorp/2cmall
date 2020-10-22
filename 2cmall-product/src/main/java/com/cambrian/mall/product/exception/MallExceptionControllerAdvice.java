package com.cambrian.mall.product.exception;

import com.cambrian.common.utils.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

/**
 * @author kuma 2020-10-16
 */
@Slf4j
@RestControllerAdvice("com.cambrian.mall.product.controller")
public class MallExceptionControllerAdvice {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public R handleValidException(MethodArgumentNotValidException e) {
        log.error("数据校验出现问题{}, 异常类型{}", e.getMessage(), e.getClass());
        BindingResult bindingResult = e.getBindingResult();
        Map<String, String> errMsg = new HashMap<>(bindingResult.getFieldErrors().size());
        for (FieldError error : bindingResult.getFieldErrors()) {
            String message = error.getDefaultMessage();
            String field = error.getField();
            errMsg.put(field, message);
        }
        return R.error(400, "数据校验出错").put("data", errMsg);
    }

    @ExceptionHandler(Throwable.class)
    public R handUnknownException(Throwable t) {
        log.error("出现未知异常{}, 异常类型{}", t.getMessage(), t.getClass());
        return R.error(500, "服务器内部异常").put("data", t.getMessage());
    }
}
