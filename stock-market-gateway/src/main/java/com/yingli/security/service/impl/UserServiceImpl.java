package com.yingli.security.service.impl;


import com.yingli.security.model.CustomUserDetails;
import com.yingli.security.model.User;
import com.yingli.security.service.UserService;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

@Component
public class UserServiceImpl implements UserService {

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        System.out.println("===================获取到token已进入自定义验证：" + username);
        // 可以进行数据库请求，这里进行模拟
        User user = new User();
        user.setUsername("chh");
        //如果是从数据库请求,password来自表中username对应的password
        user.setPassword("123456");

        if (user == null) {
            System.out.println("===================" + username);
            throw new UsernameNotFoundException("Could not find the user '" + username + "'");
        }

        return new CustomUserDetails(user, true, true, true, true, null);

    }

}
