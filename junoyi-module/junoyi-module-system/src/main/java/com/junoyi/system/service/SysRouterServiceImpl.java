package com.junoyi.system.service;

import com.junoyi.framework.security.module.LoginUser;
import com.junoyi.system.domain.vo.RouterVo;
import org.springframework.stereotype.Service;

@Service
public class SysRouterServiceImpl implements ISysRouterService {


    @Override
    public RouterVo getUserRouter(LoginUser loginUser) {

        // 获取所有的可用菜单

        // 用权限过滤菜单 （如果菜单没有设置权限，就可见）

        // 最后构建权限树


        return null;
    }
}