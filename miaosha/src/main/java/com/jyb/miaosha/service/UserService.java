package com.jyb.miaosha.service;

import com.jyb.miaosha.dao.UserDao;
import com.jyb.miaosha.domain.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserService {

    @Autowired
    UserDao userDao;

    public User getById(int id){
    return userDao.getById(id);
    }

//    事务标签
    @Transactional
    public boolean tx() {
        User u1=new User();
        u1.setId(2);
        u1.setName("lufei");
        userDao.insert(u1);

        User u2=new User();
        u2.setId(1);
        u2.setName("suolong");
        userDao.insert(u2);

        return true;
    }
}
