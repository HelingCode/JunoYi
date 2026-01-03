package com.junoyi.system.service;

import com.junoyi.system.convert.SysPermGroupConverter;
import com.junoyi.system.domain.po.SysPermGroup;
import com.junoyi.system.domain.vo.SysPermGroupVO;
import com.junoyi.system.mapper.SysPermGroupMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 权限组服务实现
 *
 * @author Fan
 */
@Service
@RequiredArgsConstructor
public class SysPermGroupServiceImpl implements ISysPermGroupService {

    private final SysPermGroupMapper sysPermGroupMapper;
    private final SysPermGroupConverter sysPermGroupConverter;

    @Override
    public List<SysPermGroupVO> getPermGroupList() {
        List<SysPermGroup> permGroups = sysPermGroupMapper.selectList(null);
        return sysPermGroupConverter.toVoList(permGroups);
    }
}
