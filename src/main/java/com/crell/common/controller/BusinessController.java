package com.crell.common.controller;

import com.crell.common.model.Business;
import com.crell.common.service.BusinessSer;
import com.crell.core.constant.ResponseState;
import com.crell.core.controller.AbstractController;
import com.crell.core.dto.Page;
import com.crell.core.dto.ParamsBody;
import com.crell.core.dto.ReturnBody;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.UUID;

/**
 * Created by FunkySoya on 2015/6/18.
 */
@RestController
public class BusinessController extends AbstractController {

    @Autowired
    BusinessSer businessSer;

    @RequestMapping(value = {"/business/getBusinessList"},method = RequestMethod.POST)
    @ResponseBody
    public ReturnBody getBusinessList(@RequestBody ParamsBody paramsBody,HttpServletRequest request){
        ReturnBody rbody = new ReturnBody();
        Page page = businessSer.getBusinessList(paramsBody.getBody(),paramsBody.getPage());
        List<Business> businessList = page.getResults();

        rbody.setStatus(ResponseState.SUCCESS);
        rbody.setData(businessList);
        rbody.setPages(page.getTotalPage());

        return rbody;
    }

}
