package com.junoyi.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.junoyi.framework.datasource.datascope.annotation.IgnoreDataScope;
import com.junoyi.system.domain.po.SysMenu;
import org.apache.ibatis.annotations.Mapper;

/**
 * 系统菜单 Mapper
 *
 * @author Fan
 */
@Mapper
@IgnoreDataScope
public interface SysMenuMapper extends BaseMapper<SysMenu> {
}
