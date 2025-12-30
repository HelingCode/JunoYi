package com.junoyi.system.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.junoyi.framework.core.utils.StringUtils;
import com.junoyi.system.convert.SysMenuConverter;
import com.junoyi.system.domain.dto.SysMenuDTO;
import com.junoyi.system.domain.dto.SysMenuQueryDTO;
import com.junoyi.system.domain.po.SysMenu;
import com.junoyi.system.domain.vo.SysMenuVO;
import com.junoyi.system.mapper.SysMenuMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * 系统菜单业务实现
 *
 * @author Fan
 */
@Service
@RequiredArgsConstructor
public class SysMenuServiceImpl implements ISysMenuService {

    private final SysMenuMapper sysMenuMapper;
    private final SysMenuConverter sysMenuConverter;

    @Override
    public List<SysMenuVO> getMenuTree(SysMenuQueryDTO queryDTO) {
        // 查询所有菜单
        List<SysMenu> menus = queryMenuList(queryDTO);
        // 转换为 VO
        List<SysMenuVO> menuVOList = sysMenuConverter.toVoList(menus);
        // 构建树形结构
        return buildTree(menuVOList, 0L);
    }

    @Override
    public List<SysMenuVO> getMenuList(SysMenuQueryDTO queryDTO) {
        List<SysMenu> menus = queryMenuList(queryDTO);
        return sysMenuConverter.toVoList(menus);
    }

    @Override
    public SysMenuVO getMenuById(Long id) {
        SysMenu menu = sysMenuMapper.selectById(id);
        return menu != null ? sysMenuConverter.toVo(menu) : null;
    }

    @Override
    public Long addMenu(SysMenuDTO menuDTO) {
        SysMenu menu = sysMenuConverter.toEntity(menuDTO);
        // 设置默认值
        if (menu.getParentId() == null) {
            menu.setParentId(0L);
        }
        if (menu.getSort() == null) {
            menu.setSort(0);
        }
        if (menu.getStatus() == null) {
            menu.setStatus(1);
        }
        sysMenuMapper.insert(menu);
        return menu.getId();
    }

    @Override
    public boolean updateMenu(SysMenuDTO menuDTO) {
        if (menuDTO.getId() == null) {
            return false;
        }
        SysMenu menu = sysMenuConverter.toEntity(menuDTO);
        return sysMenuMapper.updateById(menu) > 0;
    }

    @Override
    public boolean deleteMenu(Long id) {
        // 检查是否有子菜单
        Long childCount = sysMenuMapper.selectCount(
                new LambdaQueryWrapper<SysMenu>()
                        .eq(SysMenu::getParentId, id)
        );
        if (childCount > 0) {
            throw new RuntimeException("存在子菜单，无法删除");
        }
        return sysMenuMapper.deleteById(id) > 0;
    }

    @Override
    public boolean deleteMenuBatch(List<Long> ids) {
        if (ids == null || ids.isEmpty()) {
            return false;
        }
        // 检查是否有子菜单
        Long childCount = sysMenuMapper.selectCount(
                new LambdaQueryWrapper<SysMenu>()
                        .in(SysMenu::getParentId, ids)
        );
        if (childCount > 0) {
            throw new RuntimeException("存在子菜单，无法删除");
        }
        return sysMenuMapper.deleteBatchIds(ids) > 0;
    }

    /**
     * 查询菜单列表
     */
    private List<SysMenu> queryMenuList(SysMenuQueryDTO queryDTO) {
        LambdaQueryWrapper<SysMenu> wrapper = new LambdaQueryWrapper<>();
        
        if (queryDTO != null) {
            wrapper.like(StringUtils.isNotBlank(queryDTO.getTitle()), SysMenu::getTitle, queryDTO.getTitle())
                    .eq(queryDTO.getMenuType() != null, SysMenu::getMenuType, queryDTO.getMenuType())
                    .eq(queryDTO.getStatus() != null, SysMenu::getStatus, queryDTO.getStatus());
        }
        
        wrapper.orderByAsc(SysMenu::getSort);
        
        return sysMenuMapper.selectList(wrapper);
    }

    /**
     * 构建树形结构
     */
    private List<SysMenuVO> buildTree(List<SysMenuVO> menus, Long parentId) {
        return menus.stream()
                .filter(menu -> Objects.equals(menu.getParentId(), parentId))
                .peek(menu -> {
                    List<SysMenuVO> children = buildTree(menus, menu.getId());
                    if (!children.isEmpty()) {
                        menu.setChildren(children);
                    }
                })
                .collect(Collectors.toList());
    }
}
