package com.junoyi.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.junoyi.framework.core.domain.page.PageResult;
import com.junoyi.system.domain.dto.SysOperLogQueryDTO;
import com.junoyi.system.domain.po.SysOperLog;
import com.junoyi.system.domain.vo.SysOperLogVO;
import com.junoyi.system.mapper.SysOperLogMapper;
import com.junoyi.system.service.ISysOperLogService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 系统操作日志业务接口实现类
 *
 * @author Fan
 */
@Service
@RequiredArgsConstructor
public class SysOperLogServiceImpl implements ISysOperLogService {

    private final SysOperLogMapper sysOperLogMapper;

    private static final Map<String, String> ACTION_MAP = new HashMap<>();
    private static final Map<String, String> MODULE_MAP = new HashMap<>();

    static {
        ACTION_MAP.put("view", "查看");
        ACTION_MAP.put("create", "创建");
        ACTION_MAP.put("update", "更新");
        ACTION_MAP.put("delete", "删除");
        ACTION_MAP.put("export", "导出");
        ACTION_MAP.put("import", "导入");

        MODULE_MAP.put("user", "用户");
        MODULE_MAP.put("role", "角色");
        MODULE_MAP.put("dept", "部门");
        MODULE_MAP.put("menu", "菜单");
        MODULE_MAP.put("permission", "权限");
        MODULE_MAP.put("file", "文件");
        MODULE_MAP.put("system", "系统");
    }

    /**
     * 分页查询操作日志
     *
     * @param queryDTO 查询条件
     * @param page     分页参数
     * @return 分页结果
     */
    @Override
    public PageResult<SysOperLogVO> getOperationLogList(SysOperLogQueryDTO queryDTO, Page<SysOperLog> page) {
        LambdaQueryWrapper<SysOperLog> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(StringUtils.hasText(queryDTO.getLevel()), SysOperLog::getLevel, queryDTO.getLevel())
                .eq(StringUtils.hasText(queryDTO.getAction()), SysOperLog::getAction, queryDTO.getAction())
                .eq(StringUtils.hasText(queryDTO.getModule()), SysOperLog::getModule, queryDTO.getModule())
                .like(StringUtils.hasText(queryDTO.getUserName()), SysOperLog::getUserName, queryDTO.getUserName())
                .eq(StringUtils.hasText(queryDTO.getTargetId()), SysOperLog::getTargetId, queryDTO.getTargetId())
                .like(StringUtils.hasText(queryDTO.getMessage()), SysOperLog::getMessage, queryDTO.getMessage())
                .ge(StringUtils.hasText(queryDTO.getStartTime()), SysOperLog::getCreateTime, queryDTO.getStartTime())
                .le(StringUtils.hasText(queryDTO.getEndTime()), SysOperLog::getCreateTime, queryDTO.getEndTime())
                .orderByDesc(SysOperLog::getCreateTime);

        Page<SysOperLog> resultPage = sysOperLogMapper.selectPage(page, wrapper);

        List<SysOperLogVO> voList = resultPage.getRecords().stream()
                .map(this::convertToVO)
                .collect(Collectors.toList());

        return PageResult.of(voList,
                resultPage.getTotal(),
                (int) resultPage.getCurrent(),
                (int) resultPage.getSize());
    }


    private SysOperLogVO convertToVO(SysOperLog log) {
        SysOperLogVO vo = new SysOperLogVO();
        BeanUtils.copyProperties(log, vo);
        vo.setActionName(ACTION_MAP.getOrDefault(log.getAction(), log.getAction()));
        vo.setModuleName(MODULE_MAP.getOrDefault(log.getModule(), log.getModule()));
        return vo;
    }
}