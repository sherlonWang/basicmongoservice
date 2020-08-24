package com.sherlon.basicmongoservice.exception;

import com.sherlon.basicmongoservice.vo.JsonResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

/**
 * @author :  sherlonWang
 * @description :  统一异常处理类
 * @date: 2020-03-02
 */
@ControllerAdvice(annotations = RestController.class)
@ResponseBody
public class RestExceptionHandler {
    Logger log = LoggerFactory.getLogger(RestExceptionHandler.class);

    /**
     * 默认统一异常处理方法
     * @param e 默认异常对象
     * @return 异常信息
     */
    @ExceptionHandler
    @ResponseStatus
    public JsonResult<Object> exceptionHandler(Exception e){
        log.info(e.getMessage());
        return new JsonResult<>(e);
    }
}
