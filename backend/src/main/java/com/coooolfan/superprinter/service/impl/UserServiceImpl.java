package com.coooolfan.superprinter.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.coooolfan.superprinter.entity.User;
import com.coooolfan.superprinter.enums.UserRole;
import com.coooolfan.superprinter.exception.BusinessException;
import com.coooolfan.superprinter.mapper.UserMapper;
import com.coooolfan.superprinter.service.UserService;
import com.coooolfan.superprinter.util.IdGenerator;
import com.coooolfan.superprinter.util.PasswordUtils;
import com.coooolfan.superprinter.vo.RegisterVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;

/**
 * 用户服务实现类
 */
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

    @Autowired
    private IdGenerator idGenerator;

    @Autowired
    private PasswordUtils passwordUtils;

    @Override
    public User login(String username, String password) {
        if (!StringUtils.hasText(username) || !StringUtils.hasText(password)) {
            throw new BusinessException("用户名或密码不能为空");
        }

        // 根据用户名查询用户
        User user = getByUsername(username);
        if (user == null) {
            return null;
        }

        // 校验密码
        if (!passwordUtils.matches(password, user.getPassword())) {
            return null;
        }

        return user;
    }

    @Override
    public Long register(RegisterVO registerVO) {
        // 参数校验
        if (!StringUtils.hasText(registerVO.getUsername())) {
            throw new BusinessException("用户名不能为空");
        }
        if (!StringUtils.hasText(registerVO.getPassword())) {
            throw new BusinessException("密码不能为空");
        }

        // 检查用户名是否已存在
        User existUser = getByUsername(registerVO.getUsername());
        if (existUser != null) {
            throw new BusinessException("用户名已存在");
        }

        // 创建用户
        User user = new User();
        user.setUserId(idGenerator.nextId());
        user.setUsername(registerVO.getUsername().toLowerCase());
        user.setPassword(passwordUtils.encode(registerVO.getPassword()));
        user.setRole(UserRole.USER.getCode());

        // 保存用户
        save(user);

        return user.getUserId();
    }

    @Override
    public User getByUsername(String username) {
        return getOne(new LambdaQueryWrapper<User>()
                .eq(User::getUsername, username.toLowerCase()) // 即使不加toLowerCase，Mysql默认也不区分大小写
                .last("LIMIT 1"));
    }
}