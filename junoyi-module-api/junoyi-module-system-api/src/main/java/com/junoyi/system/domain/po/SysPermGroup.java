package com.junoyi.system.domain.po;

import lombok.Data;

import java.util.Date;
import java.util.Set;

/**
 * 系统权限组实体类
 * 用于表示系统中的权限组信息，包含权限组的基本属性和相关权限集合
 */
@Data
public class SysPermGroup {

    /**
     * 权限组唯一标识ID
     */
    private Long id;

    /**
     * 权限组编码
     */
    private String code;

    /**
     * 权限组名称
     */
    private String name;

    /**
     * 父级权限组ID，用于构建权限组层级关系
     */
    private Long parentId;

    /**
     * 优先级，用于排序显示
     */
    private int priority;

    /**
     * 权限组描述信息
     */
    private String description;

    /**
     * 状态标识，用于控制权限组的启用/禁用状态
     */
    private int status;

    /**
     * 权限集合，包含该权限组所拥有的具体权限标识
     */
    private Set<String> permissions;

    /**
     * 创建人标识
     */
    private String createBy;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 更新人标识
     */
    private String updateBy;

    /**
     * 更新时间
     */
    private Date updateTime;

    /**
     * 备注信息
     */
    private String remark;
}
