package com.crell.core.aspect;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;
import org.springframework.util.StopWatch;
/**
 * 切面，记录执行方法执行时间
 *
 * @Author crell
 * @Date 2016/1/7 10:52
 */
@Component
@Aspect
public class ServiceAspect {
	
	@Around(value="@annotation(com.crell.core.annotation.NotNull)")
    public Object doAround(ProceedingJoinPoint pjp) throws Throwable{
		//计时时钟
		StopWatch clock = new StopWatch();
		clock.start();
        Object object = pjp.proceed();//执行该方法   
        clock.stop();
		Long.toString(clock.getTaskCount());
		return object;
    }
}
