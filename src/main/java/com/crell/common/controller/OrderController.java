package com.crell.common.controller;

import com.crell.core.annotation.NotNull;
import com.crell.core.constant.ResponseState;
import com.crell.core.controller.AbstractController;
import com.crell.core.dto.ParamsBody;
import com.crell.core.dto.ReturnBody;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

/**
 * Created by crell on 2016/2/18.
 */
@RestController
public class OrderController extends AbstractController {


    @RequestMapping(value = {"/order"},method = RequestMethod.POST)
    @NotNull(value = "businessId",user = true)
    public ReturnBody add(@RequestBody ParamsBody paramsBody,HttpServletRequest request) throws Exception {
        ReturnBody rbody = new ReturnBody();
        Map<String, Object> body = paramsBody.getBody();

        String Authorization = request.getHeader("Authorization");
        rbody.setStatus(ResponseState.SUCCESS);
        return rbody;
    }
}
