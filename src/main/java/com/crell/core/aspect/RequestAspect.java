package com.crell.core.aspect;

import com.crell.common.model.User;
import com.crell.core.annotation.BodyFormat;
import com.crell.core.annotation.NotNull;
import com.crell.core.constant.Context;
import com.crell.core.constant.ResponseState;
import com.crell.core.dto.ParamsBody;
import com.crell.core.dto.ReturnBody;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

/**
 * 切面，对RequestMapping所有请求切入
 * 判断是否有效用户、参数值是否为空
 *
 * @Author crell
 * @Date 2016/1/1
 */
@Component("requestAspect")
@Aspect
public class RequestAspect {

    Logger logger = LoggerFactory.getLogger(this.getClass());

    //定义切入点
    @SuppressWarnings("unused")
    @Pointcut("@annotation(org.springframework.web.bind.annotation.RequestMapping) ")
    private  void pointCut(){}

    @Around("pointCut()")
    public Object doAround(ProceedingJoinPoint pjp) throws Throwable {
        Object[] args = pjp.getArgs();
        ParamsBody paramsBody = null;
        Map<String,Object> params = null;
        for (int j = 0; j < args.length; j++) {
            if(args[j] instanceof ParamsBody){
                paramsBody = (ParamsBody)args[j];
            }
        }

        Method method = ((MethodSignature)pjp.getSignature()).getMethod();
        Annotation[] annotations = method.getAnnotations();

        for (Annotation annotation : annotations) {
            Class<? extends Annotation> type = annotation.annotationType();
            if(paramsBody != null) params = paramsBody.getBody();
            if(BodyFormat.class.equals(type) && params != null){
                BodyFormat ann = (BodyFormat)annotation;
                String value = ann.value();
                String[] fields = value.split(",");
                Map<String,Object> formatParams = new HashMap<String, Object>();
                for (String filed : fields) {
                    if(params.get(filed) != null) formatParams.put(filed,params.get(filed));
                }
                paramsBody.setBody(formatParams);
            }
            else if(NotNull.class.equals(type)){
                NotNull ann = (NotNull)annotation;
                String value = ann.value();
                boolean user = ann.user();
                ReturnBody returnbody = new ReturnBody();

                if(user){
                    HttpServletRequest request = null;
                    for (int j = 0; j < args.length; j++) {
                        if(args[j] instanceof HttpServletRequest){
                            request = (HttpServletRequest)args[j];
                        }
                    }
                    HttpSession session = request.getSession();
                    User sessionUser = (User) session.getAttribute(Context.USER);
                    if(sessionUser == null){
                        returnbody.setStatus(ResponseState.INVALID_USER);
                        logger.info(method.getName() + "用户未登录，跳转登录页");
                        return returnbody;
                    }
                }

                String[] fields = value.split(",");
                Object keyValue = null;
                if(params != null){
                    for (String filed : fields) {
                        keyValue = params.get(filed);
                        if(StringUtils.isEmpty(keyValue)){
                            returnbody.setStatus(ResponseState.FAILED);
                            returnbody.setMsg(method.getName() + "入参" + filed + "值为空");
                            logger.error(method.getName() + "入参" + filed + "值为空");
                            return returnbody;
                        }
                    }
                }else{
                    returnbody.setStatus(ResponseState.FAILED);
                    returnbody.setMsg(method.getName() + "入参为空");
                    return returnbody;
                }

//                Class clazz = ParamsBody.class;
//                String getName = null;
//                Method getMethod = null;
//                Object keyValue = null;
//                for (int j = 0; j < values.length; j++) {
//                    getName = "get" + values[j].substring(0, 1).toUpperCase() + values[j].substring(1);
//                    getMethod = clazz.getMethod(getName, new Class[] {});
//                    keyValue = getMethod.invoke(paramsBody, new Object[] {});
//                    if(StringUtils.isEmpty(keyValue)){
//                        logger.error(method.getName() + "入参" + values[j] + "值为空");
//                        return null;
//                    }
//                }
            }
        }
        return pjp.proceed();
    }

}
