package com.junoyi.system.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.junoyi.framework.core.domain.page.PageResult;
import com.junoyi.system.domain.dto.SysUserQueryDTO;
import com.junoyi.system.domain.po.SysUser;
import com.junoyi.system.domain.vo.SysUserVO;

/**
 * 系统用户业务接口类
 *
 * @author Fan
 */
public interface ISysUserService {

    /**
     * 获取用户列表（分页）
     * @param queryDTO 查询条件
     * @param page 分页参数
     * @return 用户分页列表
     */
    PageResult<SysUserVO> getUserList(SysUserQueryDTO queryDTO, Page<SysUser> page);
}
