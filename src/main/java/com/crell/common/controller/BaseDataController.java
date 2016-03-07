package com.crell.common.controller;

import com.crell.common.model.Dictionary;
import com.crell.common.service.BaseDataSer;
import com.crell.core.constant.ResponseState;
import com.crell.core.controller.AbstractController;
import com.crell.core.dto.ReturnBody;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Created by Administrator on 2016/1/11.
 */
@RestController
public class BaseDataController extends AbstractController {

    @Autowired
    BaseDataSer baseDataSer;

    @RequestMapping(value = {"/baseData/{type}"},method = RequestMethod.GET)
    @ResponseBody
    public ReturnBody getByType(@PathVariable("type") String type){

        List<Dictionary> dictList = baseDataSer.getListByType(type);
        ReturnBody returnBody = new ReturnBody();
        returnBody.setStatus(ResponseState.SUCCESS);
        returnBody.setData(dictList);

        return  returnBody;
    }
}
