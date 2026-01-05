package com.junoyi.system.controller;

import com.junoyi.framework.core.domain.module.R;
import com.junoyi.framework.core.domain.page.PageResult;
import com.junoyi.framework.log.core.JunoYiLog;
import com.junoyi.framework.log.core.JunoYiLogFactory;
import com.junoyi.framework.permission.annotation.Permission;
import com.junoyi.framework.security.annotation.PlatformScope;
import com.junoyi.framework.security.enums.PlatformType;
import com.junoyi.framework.web.domain.BaseController;
import com.junoyi.system.domain.dto.ResetPasswordDTO;
import com.junoyi.system.domain.dto.SysUserDTO;
import com.junoyi.system.domain.dto.SysUserQueryDTO;
import com.junoyi.system.domain.vo.SysDeptVO;
import com.junoyi.system.domain.vo.SysPermGroupVO;
import com.junoyi.system.domain.vo.SysRoleVO;
import com.junoyi.system.domain.vo.SysUserVO;
import com.junoyi.system.service.ISysUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 系统用户控制类
 *
 * @author Fan
 */
@RestController
@RequestMapping("/system/user")
@RequiredArgsConstructor
public class SysUserController extends BaseController {

    private final JunoYiLog log = JunoYiLogFactory.getLogger(SysUserController.class);
    private final ISysUserService sysUserService;

    /**
     * 获取用户列表（分页）
     * @return 响应结果
     */
    @GetMapping("/list")
    @Permission(
            value = {"system.ui.user.view", "system.api.user.get"}
    )
    @PlatformScope(PlatformType.ADMIN_WEB)
    public R<PageResult<SysUserVO>> getUserList(SysUserQueryDTO queryDTO){
        return R.ok(sysUserService.getUserList(queryDTO, buildPage()));
    }

    /**
     * 通过 id 来获取用户
     * @param id 用户 id
     * @return 响应结果
     */
    @GetMapping("/{id}")
    @Permission("system.user.data.id")
    @PlatformScope(PlatformType.ADMIN_WEB)
    public R<?> getUserById(@PathVariable Long id){
        return R.ok(id);
    }


    /**
     * 添加用户
     */
    @PostMapping
    @PlatformScope(PlatformType.ADMIN_WEB)
    @Permission(
            value = {"system.ui.user.view", "system.api.user.add"}
    )
    public R<Void> addUser(@RequestBody SysUserDTO sysUserDTO){
        sysUserService.addUser(sysUserDTO);
        return R.ok();
    }


    /**
     * 更新用户
     */
    @PutMapping
    @PlatformScope(PlatformType.ADMIN_WEB)
    @Permission(
            value = {"system.ui.user.view", "system.api.user.update"}
    )
    public R<Void> updateUser(@RequestBody SysUserDTO sysUserDTO){
        sysUserService.updateUser(sysUserDTO);
        return R.ok();
    }

    /**
     * 删除用户
     */
    @DeleteMapping("/{id}")
    @PlatformScope(PlatformType.ADMIN_WEB)
    @Permission(
            value = {"system.ui.user.view", "system.api.user.delete"}
    )
    public R<Void> deleteUser(@PathVariable("id") Long id){
        sysUserService.deleteUser(id);
        return R.ok();
    }

    /**
     * 批量删除
     */
    @DeleteMapping("/batch")
    @PlatformScope(PlatformType.ADMIN_WEB)
    @Permission(
            value = {"system.ui.user.view", "system.api.user.delete"}
    )
    public R<Void> deleteUserBatch(@RequestBody List<Long> ids){
        sysUserService.deleteUserBatch(ids);
        return R.ok();
    }

    /**
     * 获取用户绑定的角色列表
     */
    @GetMapping("/{id}/roles")
    @PlatformScope(PlatformType.ADMIN_WEB)
    @Permission(
            value = {"system.ui.user.view", "system.api.user.get"}
    )
    public R<List<SysRoleVO>> getUserRoles(@PathVariable("id") Long id){
        return R.ok(sysUserService.getUserRoles(id));
    }

    /**
     * 用户绑定角色
     */
    @PutMapping("/{id}/roles")
    @PlatformScope(PlatformType.ADMIN_WEB)
    @Permission(
            value = {"system.ui.user.view", "system.api.user.update"}
    )
    public R<Void> updateUserRoles(@PathVariable("id") Long id, @RequestBody List<Long> roleIds){
        sysUserService.updateUserRoles(id, roleIds);
        return R.ok();
    }

    /**
     * 获取用户已绑定的部门
     */
    @GetMapping("/{id}/depts")
    @PlatformScope(PlatformType.ADMIN_WEB)
    @Permission(
            value = {"system.ui.user.view","system.api.user.get"}
    )
    public R<List<SysDeptVO>> getUserDepts(@PathVariable("id") Long id){
        return R.ok(sysUserService.getUserDepts(id));
    }

    /**
     * 更新用户绑定的部门
     */
    @PutMapping("/{id}/depts")
    @PlatformScope(PlatformType.ADMIN_WEB)
    @Permission(
            value = {"system.ui.user.view", "system.api.user.update"}
    )
    public R<Void> updateUserDepts(@PathVariable("id") Long id, @RequestBody List<Long> deptIds){
        sysUserService.updateUserDepts(id, deptIds);
        return R.ok();
    }


    /**
     * 重置密码（仅超级管理员可操作）
     */
    @PutMapping("/{id}/password")
    @PlatformScope(PlatformType.ADMIN_WEB)
    @Permission("*")
    public R<Void> resetUserPassword(@PathVariable("id") Long id, @RequestBody ResetPasswordDTO resetPasswordDTO){
        if (!getLoginUser().isSuperAdmin()) {
            return R.fail("仅超级管理员可重置密码");
        }
        sysUserService.resetPassword(id, resetPasswordDTO.getNewPassword());
        return R.ok();
    }

    /**
     * 获取用户已经绑定的权限组
     */
    @GetMapping("/{id}/permission-groups")
    @PlatformScope(PlatformType.ADMIN_WEB)
    @Permission(
            value = {"system.ui.user.view","system.api.user.get"}
    )
    public R<List<SysPermGroupVO>> getUserPermissionGroup(@PathVariable("id") Long id){
        return R.ok(sysUserService.getUserPermGroups(id));
    }

    /**
     * 更新用户绑定权限组
     */
    @PutMapping("/{id}/permission-groups")
    @PlatformScope(PlatformType.ADMIN_WEB)
    @Permission(
            value = {"system.ui.user.view","system.api.user.update"}
    )
    public R<Void> updateUserGroup(@PathVariable("id") Long id, @RequestBody List<Long> groupIds){
        sysUserService.updateUserPermGroups(id, groupIds);
        return R.ok();
    }
}