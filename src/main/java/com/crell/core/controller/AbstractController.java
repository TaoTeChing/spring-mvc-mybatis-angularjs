package com.crell.core.controller;

import com.crell.core.constant.ExceptionCode;
import com.crell.core.constant.ResponseState;
import com.crell.core.dto.ReturnBody;
import com.crell.core.expection.ErrorException;
import com.crell.core.expection.FailedException;
import com.crell.core.expection.InvalidUserException;
import com.crell.core.expection.ParamsNullException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;

/**
 * 基础controller
 * 统一异常处理
 *
 * @Author crell
 * @Date 2015/12/1
 */
public class AbstractController {

    /**
     * 统一异常处理
     * @param request
     * @param exception
     * @return
     */
    @ExceptionHandler
    @ResponseBody
    public ReturnBody exp(HttpServletRequest request, Exception exception){

        Logger logger = LoggerFactory.getLogger(this.getClass());

        ReturnBody rbody = new ReturnBody();
        if(exception instanceof InvalidUserException){
            rbody.setStatus(ResponseState.INVALID_USER);
        }else if(exception instanceof FailedException){
            rbody.setStatus(ResponseState.FAILED);
            rbody.setMsg(exception.getMessage());
        }else if(exception instanceof ParamsNullException){
            logger.error(request.getServletPath() + ":" + exception.getMessage(),exception);
            rbody.setStatus(ResponseState.FAILED);
            rbody.setMsg(ExceptionCode.SYSTEM_ERROR);
        }else if(exception instanceof ErrorException){
            logger.error(request.getServletPath() + ":" + exception.getMessage(),exception);
            rbody.setStatus(ResponseState.ERROR);
            rbody.setMsg(ExceptionCode.SYSTEM_ERROR);
        }else{
            logger.error(request.getServletPath() + ":" + exception.getMessage(),exception);
            rbody.setStatus(ResponseState.ERROR);
            rbody.setMsg(ExceptionCode.SYSTEM_ERROR);
        }

        return  rbody;
    }
}
