package com.junoyi.system.controller;

import com.junoyi.framework.core.domain.module.R;
import com.junoyi.framework.security.annotation.PlatformScope;
import com.junoyi.framework.security.enums.PlatformType;
import com.junoyi.framework.web.domain.BaseController;
import com.junoyi.system.domain.dto.SysMenuDTO;
import com.junoyi.system.domain.dto.SysMenuQueryDTO;
import com.junoyi.system.domain.vo.SysMenuVO;
import com.junoyi.system.service.ISysMenuService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 系统菜单控制器
 *
 * @author Fan
 */
@RestController
@RequestMapping("/system/menu")
@RequiredArgsConstructor
public class SysMenuController extends BaseController {

    private final ISysMenuService sysMenuService;

    /**
     * 获取菜单树形列表
     */
    @GetMapping("/tree")
    @PlatformScope(PlatformType.ADMIN_WEB)
    public R<List<SysMenuVO>> getMenuTree(SysMenuQueryDTO queryDTO) {
        return R.ok(sysMenuService.getMenuTree(queryDTO));
    }

    /**
     * 获取菜单列表（平铺）
     */
    @GetMapping("/list")
    @PlatformScope(PlatformType.ADMIN_WEB)
    public R<List<SysMenuVO>> getMenuList(SysMenuQueryDTO queryDTO) {
        return R.ok(sysMenuService.getMenuList(queryDTO));
    }

    /**
     * 获取菜单详情
     */
    @GetMapping("/{id}")
    @PlatformScope(PlatformType.ADMIN_WEB)
    public R<SysMenuVO> getMenuById(@PathVariable Long id) {
        return R.ok(sysMenuService.getMenuById(id));
    }

    /**
     * 添加菜单
     */
    @PostMapping
    @PlatformScope(PlatformType.ADMIN_WEB)
    public R<Long> addMenu(@RequestBody SysMenuDTO menuDTO) {
        return R.ok(sysMenuService.addMenu(menuDTO));
    }

    /**
     * 更新菜单
     */
    @PutMapping
    @PlatformScope(PlatformType.ADMIN_WEB)
    public R<?> updateMenu(@RequestBody SysMenuDTO menuDTO) {
        return sysMenuService.updateMenu(menuDTO) ? R.ok() : R.fail("更新失败");
    }

    /**
     * 删除菜单
     */
    @DeleteMapping("/{id}")
    @PlatformScope(PlatformType.ADMIN_WEB)
    public R<?> deleteMenu(@PathVariable Long id) {
        return sysMenuService.deleteMenu(id) ? R.ok() : R.fail("删除失败");
    }

    /**
     * 批量删除菜单
     */
    @DeleteMapping("/batch")
    @PlatformScope(PlatformType.ADMIN_WEB)
    public R<?> deleteMenuBatch(@RequestBody List<Long> ids) {
        return sysMenuService.deleteMenuBatch(ids) ? R.ok() : R.fail("删除失败");
    }
}
