package com.crell.common.controller;

import com.crell.common.dto.LoginForm;
import com.crell.common.model.User;
import com.crell.common.service.UserSer;
import com.crell.core.constant.Context;
import com.crell.core.constant.ResponseState;
import com.crell.core.controller.AbstractController;
import com.crell.core.dto.ReturnBody;
import com.crell.core.util.EncryptUtil;
import com.crell.core.util.LogUtil;
import com.crell.core.util.ParameterUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Created by crell on 2016/1/17.
 */

@RestController
public class LoginController extends AbstractController {

    @Autowired
    UserSer userSer;

    @Autowired
    LogUtil logUtil;

    @RequestMapping(value = {"/login"},method = RequestMethod.POST)
    @ResponseBody
    public ReturnBody login(@RequestBody LoginForm form,HttpServletRequest request, HttpServletResponse response,ModelMap modelMap){
        ReturnBody rbody = new ReturnBody();

        User user = userSer.selectByName(form.getUserName());
        Map data = new HashMap();
        if(user != null){
            if(!user.getPassword().equals(EncryptUtil.doEncrypt(form.getPassword()))){
                rbody.setStatus(ResponseState.FAILED);
                rbody.setMsg("密码错误！");
            }else{
                rbody.setStatus(ResponseState.SUCCESS);
                rbody.setMsg("Success");
                rbody.setRedirectUrl("/");
                String token = UUID.randomUUID().toString();
                data.put("token", token);
                data.put("nickName",user.getNickName());
                data.put("userName",user.getUserName());
                rbody.setData(data);

                userSer.updateToken(user.getUserId(), token);

                HttpSession session = request.getSession();
                session.setAttribute(Context.USER, user);

                logUtil.action("用户登录","用户名:"+user.getUserName());
            }
        }else{
            rbody.setStatus(ResponseState.FAILED);
            rbody.setMsg("用户名不存在！");
        }
        return rbody;
    }

    @RequestMapping(value = {"/logoff"},method = RequestMethod.POST)
    @ResponseBody
    public ReturnBody logoff(HttpServletRequest request){
        HttpSession session = request.getSession();
        session.removeAttribute(Context.USER);

        ReturnBody rbody = new ReturnBody();
        rbody.setStatus(ResponseState.SUCCESS);
        return rbody;
    }

    /**
     * 自动登录验证
     * @param request
     * @return
     */
    @RequestMapping(value = {"/autoLogin"},method = RequestMethod.POST)
    @ResponseBody
    public ReturnBody autoLogin(HttpServletRequest request) throws UnsupportedEncodingException {
        ReturnBody rbody = new ReturnBody();

        Map<String, String> cookieParam = ParameterUtil.cookiesToMap(request.getCookies());
        User user = (User)request.getSession().getAttribute(Context.USER);
        String token = cookieParam.get("token");
        String userName = cookieParam.get("userName");

        if (token != null) {
            userName = URLDecoder.decode(userName, "utf-8");
            user = userSer.selectByName(userName);
            if (user != null && token.equals(user.getToken())) {
                request.getSession().setAttribute(Context.USER, user);
                rbody.setData(user.getNickName());
                rbody.setStatus(ResponseState.SUCCESS);
            } else {
                rbody.setStatus(ResponseState.FAILED);
            }
        }else{
            rbody.setStatus(ResponseState.FAILED);
        }

        return rbody;
    }
}
