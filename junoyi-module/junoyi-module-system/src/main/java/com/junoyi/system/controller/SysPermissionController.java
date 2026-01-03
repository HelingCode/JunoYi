package com.junoyi.system.controller;

import com.junoyi.framework.core.domain.module.R;
import com.junoyi.framework.log.core.JunoYiLog;
import com.junoyi.framework.log.core.JunoYiLogFactory;
import com.junoyi.framework.permission.annotation.Permission;
import com.junoyi.framework.security.annotation.PlatformScope;
import com.junoyi.framework.security.enums.PlatformType;
import com.junoyi.system.domain.vo.SysPermGroupVO;
import com.junoyi.system.service.ISysPermGroupService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 系统权限管理控制器
 *
 * @author Fan
 */
@RestController
@RequestMapping("/system/permission")
@RequiredArgsConstructor
public class SysPermissionController {

    private final JunoYiLog log = JunoYiLogFactory.getLogger(SysPermissionController.class);

    private final ISysPermGroupService sysPermGroupService;

    /**
     * 获取权限组列表
     */
    @GetMapping("/list")
    @PlatformScope(PlatformType.ADMIN_WEB)
    @Permission(
            value = {"system.ui.permission.view", "system.api.permission.get"}
    )
    public R<List<SysPermGroupVO>> getPermissionGroupList(){
        return R.ok(sysPermGroupService.getPermGroupList());
    }

    /**
     * 添加权限组
     */
    @PostMapping
    @PlatformScope(PlatformType.ADMIN_WEB)
    @Permission(
            value = {"system.ui.permission.view", "system.api.permission.add"}
    )
    public R<Void> addPermission(){
        return R.ok();
    }

    /**
     * 更新权限组
     */
    @PutMapping
    @PlatformScope(PlatformType.ADMIN_WEB)
    @Permission(
            value = {"system.ui.permission.view", "system.api.permission.update"}
    )
    public R<Void> updatePermission(){
        return R.ok();
    }

    /**
     * 删除权限组
     */
    @DeleteMapping("/{id}")
    @PlatformScope(PlatformType.ADMIN_WEB)
    @Permission(
            value = {"system.ui.permission.view", "system.api.permission.delete"}
    )
    public R<Void> deletePermission(@PathVariable("id") Long id){
        return R.ok();
    }

    /**
     * 批量删除权限组
     */
    @DeleteMapping("/{id}/batch")
    @PlatformScope(PlatformType.ADMIN_WEB)
    @Permission(
            value = {"system.ui.permission.view", "system.api.permission.delete"}
    )
    public R<Void> deletePermissionBatch(){
        return R.ok();
    }
}
