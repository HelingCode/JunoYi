package com.junoyi.system.service;

import com.junoyi.system.domain.vo.SysPermGroupVO;

import java.util.List;

/**
 * 权限组服务接口
 *
 * @author Fan
 */
public interface ISysPermGroupService {

    /**
     * 获取权限组列表
     *
     * @return 权限组列表
     */
    List<SysPermGroupVO> getPermGroupList();
}
