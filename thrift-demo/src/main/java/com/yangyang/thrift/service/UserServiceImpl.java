package com.yangyang.thrift.service;

import com.yangyang.thrift.api.User;
import com.yangyang.thrift.api.UserService;
import org.apache.thrift.TException;

/**
 * Created by chenshunyang on 2016/10/31.
 */
public class UserServiceImpl implements UserService.Iface{
    @Override
    public User findUser() throws TException {
        User user = new User();
        user.setAge(27);
        user.setName("csy");
        return user;
    }

    @Override
    public void saveUser(User user) throws TException {
        System.out.println("save user success");
    }
}
