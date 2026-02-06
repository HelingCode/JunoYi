package com.junoyi.system.controller;

import com.junoyi.framework.log.core.JunoYiLog;
import com.junoyi.framework.log.core.JunoYiLogFactory;
import com.junoyi.framework.web.domain.BaseController;
import com.junoyi.system.service.ISysConfigService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 系统参数配置控制器
 *
 * @author Fan
 */
@RestController
@RequestMapping("/system/config")
@RequiredArgsConstructor
public class SysConfigController extends BaseController {

    private final JunoYiLog log = JunoYiLogFactory.getLogger(SysConfigController.class);

    private final ISysConfigService sysConfigService;

}