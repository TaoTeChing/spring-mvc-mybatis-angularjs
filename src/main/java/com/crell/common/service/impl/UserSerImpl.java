package com.crell.common.service.impl;

import com.crell.common.mapper.UserMapper;
import com.crell.common.model.User;
import com.crell.common.service.UserSer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Created by crell on 2016/1/19.
 */
@Service
public class UserSerImpl implements UserSer {
    @Autowired
    UserMapper mapper;

    public User selectByName(String userName) {
        return mapper.selectByName(userName);
    }

    public int updateToken(String userId,String token) {
        return mapper.updateToken(userId, token);
    }

    public User selectByNickName(String nickName) {
        return mapper.selectByNickName(nickName);
    }
}
