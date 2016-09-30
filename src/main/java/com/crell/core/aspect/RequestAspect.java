package com.crell.core.aspect;

import com.crell.core.annotation.BodyFormat;
import com.crell.core.annotation.NotNull;
import com.crell.core.constant.ResponseState;
import com.crell.core.dto.ParamsBody;
import com.crell.core.dto.ReturnBody;
import com.crell.core.util.LogUtil;
import com.crell.core.util.SystemUtil;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.BoundHashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletRequest;
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

    @Autowired
    RedisTemplate redis;

    //Logger logger = LoggerFactory.getLogger(this.getClass());

    //定义切入点
    @SuppressWarnings("unused")
    @Pointcut("@annotation(org.springframework.web.bind.annotation.RequestMapping) ")
    private  void pointCut(){}

    @Around("pointCut()")
    public Object doAround(ProceedingJoinPoint pjp) throws Throwable {
        Object[] args = pjp.getArgs();
        ParamsBody paramsBody = null;
        HttpServletRequest request = null;
        Map<String,Object> params = null;
        for (int j = 0; j < args.length; j++) {
            if(args[j] instanceof ParamsBody){
                paramsBody = (ParamsBody)args[j];
            }else if(args[j] instanceof HttpServletRequest){
                request = (HttpServletRequest)args[j];
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
                    String authorization = request.getHeader("Authorization");
                    if(StringUtils.isEmpty(authorization)){
                        returnbody.setStatus(ResponseState.INVALID_TOKEN);
                        returnbody.setMsg( "Authorization 为空");
                        LogUtil.error(method.getName() + "Authorization 为空");
                        return returnbody;
                    }
                    String usertoken = SystemUtil.getToken(authorization);
                    if(StringUtils.isEmpty(usertoken)){
                        returnbody.setStatus(ResponseState.INVALID_TOKEN);
                        returnbody.setMsg( "token为空");
                        LogUtil.error(method.getName() + "token为空");
                        return returnbody;
                    }else{
                        BoundHashOperations<String, String, String> userToken = redis.boundHashOps("userToken");
                        String token = userToken.get(usertoken);
                        if(StringUtils.isEmpty(token)){
                            returnbody.setStatus(ResponseState.INVALID_TOKEN);
                            returnbody.setMsg( "无效token");
                            LogUtil.error(method.getName() + "无效token");
                            return returnbody;
                        }else{
                            long token_time = Long.parseLong(token.split(",")[1]);
                            long timestamp = System.currentTimeMillis();
                            if(timestamp > token_time){
                                returnbody.setStatus(ResponseState.INVALID_TOKEN);
                                returnbody.setMsg( "token过期");
                                LogUtil.error(method.getName() + "token过期，失效");
                                return returnbody;
                            }
                        }
                    }
                }

                String[] fields = value.split(",");
                Object keyValue = null;
                if(params != null){
                    for (String filed : fields) {
                        keyValue = params.get(filed);
                        if(StringUtils.isEmpty(keyValue)){
                            returnbody.setStatus(ResponseState.FAILED);
                            returnbody.setMsg("入参" + filed + "值为空");
                            LogUtil.error(method.getName() + "入参" + filed + "值为空");
                            return returnbody;
                        }
                    }
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
