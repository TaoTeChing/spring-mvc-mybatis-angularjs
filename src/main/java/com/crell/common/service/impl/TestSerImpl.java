package com.crell.common.service.impl;

import com.crell.common.model.User;
import com.crell.common.service.TestSer;
import com.crell.common.service.UserSer;
import com.crell.common.test.PushTask;
import com.crell.core.dto.Page;
import com.crell.core.servlet.ThreadPool;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.core.task.TaskExecutor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * Created by Administrator on 2015/4/17.
 */
@Service
public class TestSerImpl implements TestSer {

    @Autowired
    UserSer userSer;

    @Autowired
    TaskExecutor taskExecutor;

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

    public void testThread() {
        User user = userSer.selectById("1");
        User user1 = null;
        List<User> list = new ArrayList<User>();
        for(int i=0;i<15;i++){
            user1 = new User();
            user1.setUserName(user.getUserName() + i);
            list.add(user1);
        }

        try{
            ThreadPoolExecutor pool = ThreadPool.getThreadPool();
            for(User u : list){
                PushTask pushTask = new PushTask(u.getUserName());
                pool.execute(pushTask);
            }
        }catch (Exception e){
            e.printStackTrace();
        }

//        try{
//            for(User u : list){
//                PushTask pushTask = new PushTask(u.getUserName());
//                taskExecutor.execute(pushTask);
//            }
//        }catch(Exception e){
//            e.printStackTrace();
//        }

    }
}
