package com.crell.common.mapper;

import com.crell.common.model.User;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

/**
 * 用户Mapper
 * Created by crell on 2016/1/17.
 */
@Repository
public interface UserMapper {

    /**
     * 根据用户名获取用户
     * @param userName
     * @return User
     */
    User selectByName(String userName);

    /**
     * 根据昵称获取用户
     * @param nickName
     * @return User
     */
    User selectByNickName(String nickName);

    /**
     * 用户登录后更新用户token
     * @param userId
     * @param token
     * @return
     */
    int updateToken(@Param("userId") String userId, @Param("token") String token);

    /**
     * 插入用户
     * @param user
     * @return int
     */
    int insertUser(User user);

    Boolean update(User user);

    Boolean del(@Param("userId") String userId);
}
