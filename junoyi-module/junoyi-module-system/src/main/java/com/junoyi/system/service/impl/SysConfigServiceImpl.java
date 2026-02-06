package com.junoyi.system.service.impl;

import com.junoyi.framework.log.core.JunoYiLog;
import com.junoyi.framework.log.core.JunoYiLogFactory;
import com.junoyi.system.controller.SysConfigController;
import com.junoyi.system.mapper.SysConfigMapper;
import com.junoyi.system.service.ISysConfigService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * 系统参数配置业务接口类
 *
 * @author Fan
 */
@Service
@RequiredArgsConstructor
public class SysConfigServiceImpl implements ISysConfigService {

    private final JunoYiLog log = JunoYiLogFactory.getLogger(SysConfigController.class);

    private final SysConfigMapper sysConfigMapper;
}