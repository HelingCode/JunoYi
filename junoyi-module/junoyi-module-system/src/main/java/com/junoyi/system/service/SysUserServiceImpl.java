package com.junoyi.system.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.junoyi.framework.core.domain.page.PageResult;
import com.junoyi.framework.core.utils.DateUtils;
import com.junoyi.framework.security.utils.PasswordUtils;
import com.junoyi.framework.security.utils.SecurityUtils;
import com.junoyi.system.convert.SysUserConverter;
import com.junoyi.system.domain.dto.SysUserDTO;
import com.junoyi.system.domain.dto.SysUserQueryDTO;
import com.junoyi.system.domain.po.SysUser;
import com.junoyi.system.domain.po.SysUserDept;
import com.junoyi.system.domain.vo.SysUserVO;
import com.junoyi.system.enums.SysUserStatus;
import com.junoyi.system.mapper.SysUserDeptMapper;
import com.junoyi.system.mapper.SysUserMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 系统用户业务接口实现类
 *
 * @author Fan
 */
@Service
@RequiredArgsConstructor
public class SysUserServiceImpl implements ISysUserService {

    private final SysUserMapper sysUserMapper;
    private final SysUserDeptMapper sysUserDeptMapper;
    private final SysUserConverter sysUserConverter;

    @Override
    public PageResult<SysUserVO> getUserList(SysUserQueryDTO queryDTO, Page<SysUser> page) {
        LambdaQueryWrapper<SysUser> wrapper = new LambdaQueryWrapper<>();
        
        // 如果有部门筛选条件，先查询该部门下的用户ID
        if (queryDTO.getDeptId() != null) {
            LambdaQueryWrapper<SysUserDept> deptWrapper = new LambdaQueryWrapper<>();
            deptWrapper.eq(SysUserDept::getDeptId, queryDTO.getDeptId());
            List<SysUserDept> userDeptList = sysUserDeptMapper.selectList(deptWrapper);
            List<Long> userIds = userDeptList.stream()
                    .map(SysUserDept::getUserId)
                    .collect(Collectors.toList());
            if (userIds.isEmpty()) {
                // 没有用户属于该部门，返回空结果
                return PageResult.of(List.of(), 0L, (int) page.getCurrent(), (int) page.getSize());
            }
            wrapper.in(SysUser::getUserId, userIds);
        }
        
        wrapper.like(StringUtils.hasText(queryDTO.getUserName()), SysUser::getUserName, queryDTO.getUserName())
               .like(StringUtils.hasText(queryDTO.getNickName()), SysUser::getNickName, queryDTO.getNickName())
               .like(StringUtils.hasText(queryDTO.getEmail()), SysUser::getEmail, queryDTO.getEmail())
               .like(StringUtils.hasText(queryDTO.getPhonenumber()), SysUser::getPhonenumber, queryDTO.getPhonenumber())
               .eq(StringUtils.hasText(queryDTO.getSex()), SysUser::getSex, queryDTO.getSex())
               .eq(queryDTO.getStatus() != null, SysUser::getStatus, queryDTO.getStatus())
               .eq(SysUser::isDelFlag, false);

        Page<SysUser> resultPage = sysUserMapper.selectPage(page, wrapper);
        return PageResult.of(
                sysUserConverter.toVoList(resultPage.getRecords()),
                resultPage.getTotal(),
                (int) resultPage.getCurrent(),
                (int) resultPage.getSize()
        );
    }

    @Override
    public void addUser(SysUserDTO userDTO) {
        // 创建用户实体
        SysUser sysUser = sysUserConverter.toEntity(userDTO);
        
        // 密码加密
        PasswordUtils.EncryptResult encryptResult = PasswordUtils.encrypt(userDTO.getPassword());
        sysUser.setPassword(encryptResult.getEncodedPassword());
        sysUser.setSalt(encryptResult.getSalt());
        
        // 设置默认值
        sysUser.setDelFlag(false);
        sysUser.setStatus(userDTO.getStatus() != null ? userDTO.getStatus() : SysUserStatus.NORMAL.getCode());
        sysUser.setCreateTime(DateUtils.getNowDate());
        sysUser.setCreateBy(SecurityUtils.getUserName());
        sysUser.setUpdateTime(DateUtils.getNowDate());
        sysUser.setUpdateBy(SecurityUtils.getUserName());
        
        // 插入用户
        sysUserMapper.insert(sysUser);
    }

    @Override
    public void updateUser(SysUserDTO userDTO) {
        // 更新用户基本信息（不更新密码）
        SysUser sysUser = sysUserConverter.toEntity(userDTO);
        sysUser.setUserId(userDTO.getId());
        sysUser.setPassword(null);  // 不更新密码
        sysUser.setSalt(null);      // 不更新盐值
        sysUser.setUpdateTime(DateUtils.getNowDate());
        sysUser.setUpdateBy(SecurityUtils.getUserName());
        sysUserMapper.updateById(sysUser);
    }
}