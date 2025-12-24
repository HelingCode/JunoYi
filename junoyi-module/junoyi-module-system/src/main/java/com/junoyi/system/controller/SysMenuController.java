package com.junoyi.system.controller;


import com.junoyi.framework.core.domain.base.BaseController;
import com.junoyi.framework.core.domain.module.R;
import com.junoyi.framework.security.annotation.PlatformScope;
import com.junoyi.framework.security.enums.PlatformType;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * 系统菜单控制器
 * 提供菜单的增删改查等操作接口
 *
 * @author Fan
 */
@RestController
@RequestMapping("/system/menu")
@RequiredArgsConstructor
public class SysMenuController extends BaseController {

    /**
     * 获取菜单列表
     * 用于查询系统中所有的菜单信息
     *
     * @return R<?> 通用响应结果，包含菜单列表数据
     */
    @GetMapping("/list")
    @PlatformScope(PlatformType.ADMIN_WEB)
    public R<?> getMenuList(){

        return R.ok();
    }

    /**
     * 添加菜单
     * 用于向系统中新增菜单项
     *
     * @return R<?> 通用响应结果，表示添加操作的执行结果
     */
    @PostMapping()
    @PlatformScope(PlatformType.ADMIN_WEB)
    public R<?> addMenu(){
        return R.ok();
    }

    /**
     * 更新菜单
     * 用于修改系统中已存在的菜单信息
     *
     * @return R<?> 通用响应结果，表示更新操作的执行结果
     */
    @PutMapping
    @PlatformScope(PlatformType.ADMIN_WEB)
    public R<?> updateMenu(){
        return R.ok();
    }
}
