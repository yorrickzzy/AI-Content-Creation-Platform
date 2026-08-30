package com.example.service;

import com.example.pojo.User;

public interface UserService {

    User findByUserName(String username);

    void register(String username, String password);

    boolean update(User user);

    // 更新头像
    void updateAvatar(String avatarUrl);

    //更新密码
    void updatePwd(String newPwd);
} 