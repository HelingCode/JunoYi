package com.junoyi.system.service;

import com.junoyi.system.domain.dto.SysMenuDTO;
import com.junoyi.system.domain.dto.SysMenuQueryDTO;
import com.junoyi.system.domain.vo.SysMenuVO;

import java.util.List;

/**
 * 系统菜单业务接口
 *
 * @author Fan
 */
public interface ISysMenuService {

    /**
     * 查询菜单列表（树形结构）
     *
     * @param queryDTO 查询参数
     * @return 菜单树形列表
     */
    List<SysMenuVO> getMenuTree(SysMenuQueryDTO queryDTO);

    /**
     * 查询菜单列表（平铺）
     *
     * @param queryDTO 查询参数
     * @return 菜单列表
     */
    List<SysMenuVO> getMenuList(SysMenuQueryDTO queryDTO);

    /**
     * 根据ID查询菜单详情
     *
     * @param id 菜单ID
     * @return 菜单详情
     */
    SysMenuVO getMenuById(Long id);

    /**
     * 新增菜单
     *
     * @param menuDTO 菜单数据
     * @return 新增的菜单ID
     */
    Long addMenu(SysMenuDTO menuDTO);

    /**
     * 更新菜单
     *
     * @param menuDTO 菜单数据
     * @return 是否成功
     */
    boolean updateMenu(SysMenuDTO menuDTO);

    /**
     * 删除菜单
     *
     * @param id 菜单ID
     * @return 是否成功
     */
    boolean deleteMenu(Long id);

    /**
     * 批量删除菜单
     *
     * @param ids 菜单ID列表
     * @return 是否成功
     */
    boolean deleteMenuBatch(List<Long> ids);
}
