package com.coooolfan.superprinter.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.coooolfan.superprinter.entity.User;
import com.coooolfan.superprinter.vo.RegisterVO;

/**
 * 用户服务接口
 */
public interface UserService extends IService<User> {

    /**
     * 用户登录
     *
     * @param username 用户名
     * @param password 密码
     * @return 用户信息
     */
    User login(String username, String password);

    /**
     * 用户注册
     *
     * @param registerVO 注册信息
     * @return 用户ID
     */
    Long register(RegisterVO registerVO);

    /**
     * 根据用户名查询用户
     *
     * @param username 用户名
     * @return 用户信息
     */
    User getByUsername(String username);
}