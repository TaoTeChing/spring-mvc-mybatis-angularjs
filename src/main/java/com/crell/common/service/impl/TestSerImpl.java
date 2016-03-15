package com.crell.common.service.impl;

import com.crell.common.service.TestSer;
import com.crell.core.dto.Page;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2015/4/17.
 */
@Service
public class TestSerImpl implements TestSer {

    public String selectUser() {
        return "cq";
    }

    public String testException() throws Exception {
        if (true){
            throw new Exception("service报错");
        }
        return null;
    }

    //@Cacheable(value="default")  测试，spring redis cache。"default" 为配置的cacheManager的名称，可配多个
    public List<String> testPage(Map<String, Object> body, Page page) {
        List<String> list = new ArrayList<String>();
        int pageNo = page.getPageNo();
        for (int i = 1; i < page.getPageSize(); i++) {
            list.add(String.valueOf((pageNo-1)*10+i));
        }
        return list;
    }
}
