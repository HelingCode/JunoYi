package com.junoyi.system.service;

import com.junoyi.framework.security.module.LoginUser;
import com.junoyi.system.domain.vo.RouterVo;

/**
 * 系统路由业务接口类
 *
 * @author Fan
 */
public interface ISysRouterService {

    /**
     * 获取用户路由信息
     *
     * @param loginUser 登录用户信息，包含用户身份和权限等相关数据
     * @return RouterVo 路由视图对象，封装了用户的路由配置信息
     */
    RouterVo getUserRouter(LoginUser loginUser);
}
