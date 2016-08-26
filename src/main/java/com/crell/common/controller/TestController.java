package com.crell.common.controller;


import com.crell.common.service.TestSer;
import com.crell.core.constant.ResponseState;
import com.crell.core.controller.AbstractController;
import com.crell.core.dto.Page;
import com.crell.core.dto.ParamsBody;
import com.crell.core.dto.ReturnBody;
import com.crell.core.util.ParamsBodyUtil;
import com.crell.weixin.model.Photo;
import com.crell.weixin.model.Signature;
import com.crell.weixin.util.WeixinUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
public class TestController extends AbstractController {

    @Autowired
    TestSer testSer;

    @Autowired
    @Qualifier("weixinUtil")
    private WeixinUtil weixinUtil;

    @RequestMapping(value = {"/test"},method = RequestMethod.GET)
    public ModelAndView test(HttpServletRequest request){
        return new ModelAndView("test");
    }

    @RequestMapping(value = {"/bodyTest"},method = RequestMethod.GET)
    @ResponseBody
    public ReturnBody bodyTest(HttpServletRequest request) throws Exception {
        ReturnBody returnbody = new ReturnBody();
        ParamsBody paramsBody = ParamsBodyUtil.getBody(request);

        Map<String,Object> body = paramsBody.getBody();
        Page page = paramsBody.getPage();
        
        List<Object> list = new ArrayList<Object>();
        list.add(body.get("name"));
        list.add(page.getPageSize());
        list.add("测试1");
        list.add("测试2");
        list.add("测试3");

        String aa = testSer.selectUser();
        //String test = userSer.testException();

        returnbody.setStatus(ResponseState.SUCCESS);
        returnbody.setData(list);

        return returnbody;
    }

    @RequestMapping(value = {"/pageTest"},method = RequestMethod.POST)
    @ResponseBody
    public ReturnBody pageTest(@RequestBody ParamsBody paramsBody,HttpServletRequest request) throws Exception {
        ReturnBody returnbody = new ReturnBody();

        List<String> list = testSer.testPage(paramsBody.getBody(),paramsBody.getPage());

        returnbody.setStatus(ResponseState.SUCCESS);
        returnbody.setData(list);

        return returnbody;
    }

    @RequestMapping(value = {"/vmTest"},method = RequestMethod.GET)
    public ModelAndView vmTest(HttpServletRequest request) throws Exception {
        Map<String,Object> map = new HashMap<String,Object>();
        map.put("xm", "陈奇");
        ReturnBody returnBody = new ReturnBody();
        returnBody.setMsg("陈奇");
        map.put("name", returnBody);
        ReturnBody returnBody1 = new ReturnBody();
        returnBody1.setMsg("陈奇1");
        List<ReturnBody> list = new ArrayList<ReturnBody>();
        list.add(returnBody);
        list.add(returnBody1);
        map.put("list", list);
        return new ModelAndView("test.vm",map);
    }

    @RequestMapping(value = {"/jssdkTest"},method = RequestMethod.GET)
    public ModelAndView jssdkTest(HttpServletRequest request, Model model) throws Exception {
        Signature signature = weixinUtil.getJssdkSign(request);
        Photo photo = new Photo();

        model.addAttribute("signature", signature);
        model.addAttribute("photo", photo);
        return new ModelAndView("jssdkTest.jsp");
    }

    @RequestMapping(value = {"/jssdkTest/upImg"}, method = RequestMethod.POST)
    @ResponseBody
    public ReturnBody uploadOutStock(String weight) {
        ReturnBody returnbody = new ReturnBody();

        returnbody.setStatus(ResponseState.SUCCESS);
        returnbody.setData(weight);

        return returnbody;
    }

    @RequestMapping(value = {"/pushTest"},method = RequestMethod.GET)
    @ResponseBody
    public ReturnBody pushTest(HttpServletRequest request) throws Exception {
        ReturnBody returnbody = new ReturnBody();

        testSer.testThread();

        returnbody.setStatus(ResponseState.SUCCESS);
        returnbody.setData("成功");

        return returnbody;
    }

    @RequestMapping(value = {"/transactionTest"},method = RequestMethod.GET)
    @ResponseBody
    public ReturnBody transactionTest(HttpServletRequest request) throws Exception {
        ReturnBody returnbody = new ReturnBody();

        testSer.addTransactionTest();

        returnbody.setStatus(ResponseState.SUCCESS);
        returnbody.setData("成功");

        return returnbody;
    }

    @RequestMapping(value = {"/clearAllCache"},method = RequestMethod.GET)
    @ResponseBody
    public ReturnBody clearAllCache(HttpServletRequest request){
        ReturnBody returnbody = new ReturnBody();

        testSer.clearAllCache();

        returnbody.setStatus(ResponseState.SUCCESS);
        returnbody.setData("成功");

        return returnbody;
    }

}
