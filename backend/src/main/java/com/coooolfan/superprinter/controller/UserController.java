package com.coooolfan.superprinter.controller;

import cn.dev33.satoken.stp.SaTokenInfo;
import cn.dev33.satoken.stp.StpUtil;
import com.coooolfan.superprinter.entity.User;
import com.coooolfan.superprinter.exception.BusinessException;
import com.coooolfan.superprinter.service.UserService;
import com.coooolfan.superprinter.vo.LoginVO;
import com.coooolfan.superprinter.vo.RegisterVO;
import com.coooolfan.superprinter.vo.UserInfoVO;
import com.coooolfan.superprinter.vo.response.LoginResponse;
import com.coooolfan.superprinter.vo.response.MessageResponse;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * 用户控制器
 */
@RestController
@AllArgsConstructor
@RequestMapping("/api/user")
public class UserController {
    private final UserService userService;

    /**
     * 用户登录
     *
     * @param loginVO 登录信息
     * @return 登录结果
     */
    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginVO loginVO) {
        // 登录校验
        User user = userService.login(loginVO.getUsername(), loginVO.getPassword());
        if (user == null) {
            throw new BusinessException("用户名或密码错误");
        }

        // 登录成功，使用Sa-Token记录登录状态
        StpUtil.login(user.getUserId());
        // 获取登录token信息
        SaTokenInfo tokenInfo = StpUtil.getTokenInfo();

        // 创建响应对象
        LoginResponse response = new LoginResponse();
        response.setToken(tokenInfo.getTokenValue());
        response.setUserId(user.getUserId());
        response.setUsername(user.getUsername());
        response.setRole(user.getRole());

        return ResponseEntity.ok(response);
    }

    /**
     * 用户注册
     *
     * @param registerVO 注册信息
     * @return 注册结果
     */
    @PostMapping("/register")
    public ResponseEntity<MessageResponse> register(@RequestBody RegisterVO registerVO) {
        // 注册新用户
        userService.register(registerVO);
        return ResponseEntity.ok(new MessageResponse("注册成功"));
    }

    /**
     * 获取当前登录用户信息
     *
     * @return 用户信息
     */
    @GetMapping("/info")
    public ResponseEntity<UserInfoVO> getUserInfo() {
        // 获取当前登录用户ID
        Long userId = StpUtil.getLoginIdAsLong();
        // 查询用户信息
        User user = userService.getById(userId);
        if (user == null) {
            throw new BusinessException("用户不存在");
        }

        // 转换为VO
        UserInfoVO userInfoVO = new UserInfoVO();
        userInfoVO.setUserId(user.getUserId());
        userInfoVO.setUsername(user.getUsername());
        userInfoVO.setRole(user.getRole());

        return ResponseEntity.ok(userInfoVO);
    }

    /**
     * 用户登出
     *
     * @return 登出结果
     */
    @PostMapping("/logout")
    public ResponseEntity<MessageResponse> logout() {
        StpUtil.logout();
        return ResponseEntity.ok(new MessageResponse("登出成功"));
    }
}