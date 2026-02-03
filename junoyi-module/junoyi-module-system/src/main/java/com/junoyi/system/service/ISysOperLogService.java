package com.junoyi.system.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.junoyi.framework.core.domain.page.PageResult;
import com.junoyi.system.domain.dto.SysOperLogQueryDTO;
import com.junoyi.system.domain.po.SysOperLog;
import com.junoyi.system.domain.vo.SysOperLogVO;

/**
 * 操作日志业务接口类
 *
 * @author Fan
 */
public interface ISysOperLogService {

    /**
     * 分页查询操作日志
     *
     * @param queryDTO 查询条件
     * @param page     分页参数
     * @return 分页结果
     */
    PageResult<SysOperLogVO> getOperationLogList(SysOperLogQueryDTO queryDTO, Page<SysOperLog> page);
}
