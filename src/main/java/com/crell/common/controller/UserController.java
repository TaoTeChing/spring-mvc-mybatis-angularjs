package com.crell.common.controller;

import com.crell.common.model.User;
import com.crell.common.service.UserSer;
import com.crell.core.annotation.NotNull;
import com.crell.core.constant.ResponseState;
import com.crell.core.controller.AbstractController;
import com.crell.core.dto.ParamsBody;
import com.crell.core.dto.ReturnBody;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Date;
import java.util.Map;

/**
 * Created by crell on 2016/1/17.
 */
@RestController
public class UserController extends AbstractController {

    @Autowired
    UserSer userSer;

    /**
     * 校验用户名
     * @param paramsBody
     * @return
     */
    @RequestMapping(value = {"/validUserName"},method = RequestMethod.POST)
    @ResponseBody
    @NotNull(value = "userName",user = true)
    public ReturnBody ValidUserName(@RequestBody ParamsBody paramsBody,HttpServletRequest request, HttpServletResponse response){
        ReturnBody rbody = new ReturnBody();
        Map params = paramsBody.getBody();
        String userName = (String) params.get("userName");
        User validUser = userSer.selectByName(userName);
        if(validUser == null){
            rbody.setStatus(ResponseState.SUCCESS);
        }else{
            rbody.setStatus(ResponseState.FAILED);
        }
        return rbody;
    }

}
