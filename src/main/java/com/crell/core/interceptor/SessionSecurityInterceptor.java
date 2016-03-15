package com.crell.core.interceptor;

import com.crell.common.model.User;
import com.crell.common.service.UserSer;
import com.crell.core.constant.Context;
import com.crell.core.dto.ReturnBody;
import com.crell.core.util.IpUtil;
import com.crell.core.util.SystemUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 验证用户权限
 * Created by crell on 2016/1/17.
 */
public class SessionSecurityInterceptor implements HandlerInterceptor {

    public String[] allowUrls;//需要拦截的url，配置文件中注入

    @Autowired
    UserSer userSer;

    public void setAllowUrls(String[] allowUrls) {
        this.allowUrls = allowUrls;
    }

    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        //请求url
        String requestUrl = request.getRequestURI().replace(request.getContextPath(), "");
        //session中取当前用户session
        User user = (User)request.getSession().getAttribute(Context.USER);
        String ipAddr = IpUtil.getIpAddr(request);

        if (null != allowUrls && allowUrls.length >= 1) {

        }
        if(user!=null){
            user.setIp(ipAddr);
            SystemUtil.setUser(user);
        }else if(requestUrl.contains("/login")){
            user = new User();
            user.setUserName(request.getParameter("userName"));
            user.setIp(ipAddr);
            SystemUtil.setUser(user);
        }
        return true;
    }

    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
    }

    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
    }
}
