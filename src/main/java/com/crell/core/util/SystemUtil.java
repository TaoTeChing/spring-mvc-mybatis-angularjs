package com.crell.core.util;

import com.crell.common.model.User;
import org.springframework.stereotype.Component;

@Component("SystemUtil")
public class SystemUtil {
	
	private static ThreadLocal<User> local = new ThreadLocal<User>();
	
	public static void setUser(User user){
        local.set(user);
    }
    
    public static void removeUser(){
        local.remove();
    }
    
    public static User getUser(){
        return (User)local.get();
    }

    public static String getToken(String authorization){
        String usertoken = "";
        String[] parts = authorization.split(" ");
        if(parts.length == 2){
            String scheme = parts[0];
            String credentials = parts[1];
            if("Bearer".equals(scheme)){
                usertoken = credentials;
            }
        }
        return usertoken;
    }
    
}
