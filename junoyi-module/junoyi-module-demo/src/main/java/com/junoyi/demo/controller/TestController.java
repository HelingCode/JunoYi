package com.junoyi.demo.controller;

import com.junoyi.demo.domain.UserInfoVO;
import com.junoyi.demo.event.TestEvent;
import com.junoyi.framework.core.domain.module.R;
import com.junoyi.framework.datasource.datascope.DataScopeContextHolder;
import com.junoyi.framework.datasource.datascope.DataScopeContextHolder.DataScopeContext;
import com.junoyi.framework.event.core.EventBus;
import com.junoyi.framework.log.core.JunoYiLog;
import com.junoyi.framework.log.core.JunoYiLogFactory;
import com.junoyi.framework.permission.annotation.Permission;
import com.junoyi.framework.security.annotation.PlatformScope;
import com.junoyi.framework.security.enums.PlatformType;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/demo")
@RequiredArgsConstructor
public class TestController {

    private final JunoYiLog log = JunoYiLogFactory.getLogger(TestController.class);

    @GetMapping("/event")
    public void testEvent(){
        EventBus.get().callEvent(new TestEvent("测试事件"));
    }

    /**
     * 只允许 网页端使用 (后台管理web、 前台用户web)
     */
    @PlatformScope( {PlatformType.ADMIN_WEB, PlatformType.FRONT_DESK_WEB})
    @GetMapping("/web")
    public R<String> helloWorld(){
        return R.ok("Hello World");
    }

    /**
     * 只允许小程序和APP使用
     */
    @PlatformScope( {PlatformType.MINI_PROGRAM, PlatformType.APP} )
    @GetMapping("/app")
    public R<String> helloWorldApp(){
        return R.ok();
    }

    /**
     * 只允许桌面端使用
     */
    @PlatformScope( {PlatformType.DESKTOP_APP, PlatformType.ADMIN_WEB} )
    @GetMapping("/desktop")
    public R<String> helloWorldDesktop() {
        return R.ok();
    }

    @GetMapping("/xss")
    public R<?> testXss(){
        return R.ok();
    }

    @PostMapping("/xss")
    public R<?> testXss2(@RequestParam Long id){
        return R.ok();
    }

    @GetMapping("/permission")
    @Permission("system.demo.permission")
    @PlatformScope(PlatformType.APP)
    public R<String> testPermission(){
        return R.ok("Hello World");
    }

    /**
     * 测试字段权限
     * <p>
     * 根据用户权限返回不同的字段值：
     * - 有 field.user.xxx 权限：显示完整值
     * - 无权限但配置了脱敏：显示脱敏值
     * - 无权限且未配置脱敏：显示 null
     */
    @GetMapping("/field-permission")
    public R<UserInfoVO> testFieldPermission() {
        UserInfoVO user = new UserInfoVO();
        user.setId(1L);
        user.setUsername("zhangsan");
        user.setNickName("张三");
        user.setPhone("13812345678");
        user.setIdCard("110101199001011234");
        user.setEmail("zhangsan@example.com");
        user.setSalary(new BigDecimal("15000.00"));
        user.setBankCard("6222021234567890123");
        user.setAddress("北京市朝阳区建国路100号");
        return R.ok(user);
    }

    /**
     * 测试数据范围 - 查看当前用户的数据范围信息
     * <p>
     * 返回当前登录用户的数据范围配置，包括：
     * - scopeType: 数据范围类型 (ALL/DEPT/DEPT_AND_CHILD/SELF)
     * - userId: 当前用户ID
     * - deptIds: 用户所属部门
     * - accessibleDeptIds: 可访问的部门ID列表
     * - superAdmin: 是否超级管理员
     */
    @GetMapping("/data-scope")
    public R<Map<String, Object>> testDataScope() {
        DataScopeContext context = DataScopeContextHolder.get();
        
        Map<String, Object> result = new HashMap<>();
        if (context == null) {
            result.put("message", "未登录或数据范围上下文为空");
            return R.ok(result);
        }
        
        result.put("scopeType", context.getScopeType() != null ? context.getScopeType().name() : null);
        result.put("scopeTypeDesc", context.getScopeType() != null ? context.getScopeType().getDesc() : null);
        result.put("userId", context.getUserId());
        result.put("userName", context.getUserName());
        result.put("deptIds", context.getDeptIds());
        result.put("accessibleDeptIds", context.getAccessibleDeptIds());
        result.put("superAdmin", context.isSuperAdmin());
        
        // 说明
        if (context.isSuperAdmin()) {
            result.put("说明", "超级管理员，可查看所有数据");
        } else if (context.getScopeType() != null) {
            switch (context.getScopeType()) {
                case ALL:
                    result.put("说明", "全部数据权限，可查看所有数据");
                    break;
                case DEPT:
                    result.put("说明", "本部门数据权限，只能查看部门ID在 " + context.getDeptIds() + " 中的数据");
                    break;
                case DEPT_AND_CHILD:
                    result.put("说明", "本部门及下级数据权限，只能查看部门ID在 " + context.getAccessibleDeptIds() + " 中的数据");
                    break;
                case SELF:
                    result.put("说明", "仅本人数据权限，只能查看 create_by = '" + context.getUserName() + "' 的数据");
                    break;
            }
        }
        
        return R.ok(result);
    }
}