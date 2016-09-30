package com.crell.common.controller;

import com.crell.common.model.User;
import com.crell.common.service.UserSer;
import com.crell.core.annotation.NotNull;
import com.crell.core.constant.Context;
import com.crell.core.constant.ResponseState;
import com.crell.core.controller.AbstractController;
import com.crell.core.dto.ParamsBody;
import com.crell.core.dto.ReturnBody;
import com.crell.core.util.EncryptUtil;
import com.crell.core.util.IpUtil;
import com.crell.core.util.LogUtil;
import com.crell.core.util.SystemUtil;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Date;
import java.util.Map;

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
    public ReturnBody login(@RequestBody ParamsBody paramsBody,HttpServletRequest request, HttpServletResponse response){
        ReturnBody rbody = new ReturnBody();
        Map<String, Object> body = paramsBody.getBody();

        User user = userSer.selectByName((String) body.get("userName"));
        if(user != null){
            if(!user.getPassword().equals(EncryptUtil.doEncrypt((String) body.get("password")))){
                rbody.setStatus(ResponseState.FAILED);
                rbody.setMsg("密码错误！");
            }else{
                String compactJws = Jwts.builder()
                        .setSubject(user.getUserName())
                        .signWith(SignatureAlgorithm.HS512, Context.SECRET)
                        .compact();
//                String token = UUID.randomUUID().toString();
                user.setToken(compactJws);
                user.setIp(IpUtil.getIpAddr(request));
                user.setLastLoginDate(new Date());
                user.setPassword("");
                rbody.setData(user);

                userSer.login(user);

                logUtil.action("用户登录", "用户名:" + user.getUserName());

                rbody.setStatus(ResponseState.SUCCESS);
                rbody.setMsg("Success");
            }
        }else{
            rbody.setStatus(ResponseState.FAILED);
            rbody.setMsg("用户名不存在！");
        }
        return rbody;
    }

    @RequestMapping(value = {"/logoff"},method = RequestMethod.POST)
    @ResponseBody
    @NotNull(value = "", user = true)
    public ReturnBody logoff(@RequestBody ParamsBody paramsBody,HttpServletRequest request){
        ReturnBody rbody = new ReturnBody();

        String authorization = request.getHeader("Authorization");
        String token = SystemUtil.getToken(authorization);
        userSer.logout(token);
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
    @NotNull(value = "", user = true)
    public ReturnBody autoLogin(@RequestBody ParamsBody paramsBody,HttpServletRequest request) {
        ReturnBody rbody = new ReturnBody();

        //Map<String, String> cookieParam = ParameterUtil.cookiesToMap(request.getCookies());
        User user = null;
        String authorization = request.getHeader("Authorization");
        String token = SystemUtil.getToken(authorization);

        user = userSer.selectByToken(token);
        if (user != null) {
            user.setPassword("");
            rbody.setData(user);
            rbody.setStatus(ResponseState.SUCCESS);
        } else {
            rbody.setMsg("token失效");
            rbody.setStatus(ResponseState.FAILED);
        }

        return rbody;
    }
}
