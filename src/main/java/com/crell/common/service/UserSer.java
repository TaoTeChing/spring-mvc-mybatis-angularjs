package com.crell.common.service;


import com.crell.common.model.User;

/**
 * Created by Administrator on 2015/4/17.
 */
public interface UserSer {

    public User selectByName(String userName);

    public User selectByNickName(String nickName);

    public int updateToken(String userId, String token);
}
