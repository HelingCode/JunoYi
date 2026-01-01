package com.junoyi.system.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.junoyi.system.convert.SysDeptConverter;
import com.junoyi.system.domain.dto.SysDeptQueryDTO;
import com.junoyi.system.domain.po.SysDept;
import com.junoyi.system.domain.vo.SysDeptVO;
import com.junoyi.system.mapper.SysDeptMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 系统部门业务接口实现类
 *
 * @author Fan
 */
@Service
@RequiredArgsConstructor
public class SysDeptServiceImpl implements ISysDeptService {

    private final SysDeptMapper sysDeptMapper;
    private final SysDeptConverter sysDeptConverter;

    @Override
    public List<SysDeptVO> getDeptTree(SysDeptQueryDTO queryDTO) {
        LambdaQueryWrapper<SysDept> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(StringUtils.hasText(queryDTO.getName()), SysDept::getName, queryDTO.getName())
               .like(StringUtils.hasText(queryDTO.getLeader()), SysDept::getLeader, queryDTO.getLeader())
               .like(StringUtils.hasText(queryDTO.getPhonenumber()), SysDept::getPhonenumber, queryDTO.getPhonenumber())
               .like(StringUtils.hasText(queryDTO.getEmail()), SysDept::getEmail, queryDTO.getEmail())
               .eq(queryDTO.getStatus() != null, SysDept::getStatus, queryDTO.getStatus())
               .eq(SysDept::isDelFlag, false)
               .orderByAsc(SysDept::getSort);

        List<SysDept> deptList = sysDeptMapper.selectList(wrapper);
        List<SysDeptVO> voList = sysDeptConverter.toVoList(deptList);
        return buildTree(voList);
    }

    @Override
    public SysDeptVO getDeptById(Long id) {
        SysDept sysDept = sysDeptMapper.selectById(id);
        if (sysDept == null || sysDept.isDelFlag()) {
            return null;
        }
        return sysDeptConverter.toVo(sysDept);
    }

    /**
     * 构建部门树
     */
    private List<SysDeptVO> buildTree(List<SysDeptVO> deptList) {
        Map<Long, SysDeptVO> deptMap = deptList.stream()
                .collect(Collectors.toMap(SysDeptVO::getId, dept -> dept));

        List<SysDeptVO> rootList = new ArrayList<>();
        for (SysDeptVO dept : deptList) {
            Long parentId = dept.getParentId();
            if (parentId == null || parentId == 0L) {
                rootList.add(dept);
            } else {
                SysDeptVO parent = deptMap.get(parentId);
                if (parent != null) {
                    if (parent.getChildren() == null) {
                        parent.setChildren(new ArrayList<>());
                    }
                    parent.getChildren().add(dept);
                } else {
                    // 父节点不存在时作为根节点
                    rootList.add(dept);
                }
            }
        }
        return rootList;
    }
}