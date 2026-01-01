package com.junoyi.system.controller;

import com.junoyi.framework.core.domain.module.R;
import com.junoyi.framework.log.core.JunoYiLog;
import com.junoyi.framework.log.core.JunoYiLogFactory;
import com.junoyi.framework.permission.annotation.Permission;
import com.junoyi.framework.security.annotation.PlatformScope;
import com.junoyi.framework.security.enums.PlatformType;
import com.junoyi.framework.web.domain.BaseController;
import com.junoyi.system.domain.dto.SysDeptQueryDTO;
import com.junoyi.system.domain.vo.SysDeptVO;
import com.junoyi.system.service.ISysDeptService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 系统部门控制类
 *
 * @author Fan
 */
@RestController
@RequestMapping("/system/dept")
@RequiredArgsConstructor
public class SysDeptController extends BaseController {

    private final JunoYiLog log = JunoYiLogFactory.getLogger(SysDeptController.class);

    private final ISysDeptService sysDeptService;

    /**
     * 获取部门树状列表（支持查询）
     */
    @GetMapping("/tree")
    @PlatformScope(PlatformType.ADMIN_WEB)
    @Permission(
            value = {"system.ui.dept.view", "system.api.dept.get"}
    )
    public R<List<SysDeptVO>> getDeptTree(SysDeptQueryDTO queryDTO){
        return R.ok(sysDeptService.getDeptTree(queryDTO));
    }

    /**
     * 获取部门详情
     */
    @GetMapping("/{id}")
    @PlatformScope(PlatformType.ADMIN_WEB)
    @Permission(
            value = {"system.ui.dept.view","system.api.dept.get"}
    )
    public R<SysDeptVO> getDeptById(@PathVariable("id") Long id){
        return R.ok(sysDeptService.getDeptById(id));
    }
}