package com.junoyi.system.service;

import com.junoyi.system.domain.bo.LoginBO;
import com.junoyi.system.domain.vo.AuthVo;

/**
 * 系统验证认证业务接口类
 *
 * @author Fan
 */
public interface ISysAuthService {


    AuthVo login(LoginBO loginBO);
}
