package com.junoyi.system.domain.po;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.junoyi.framework.core.domain.base.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 系统参数配置数据实体类
 *
 * @author Fan
 */
@EqualsAndHashCode(callSuper = true)
@Data
@TableName("sys_config")
public class SysConfig extends BaseEntity {
    private static final long serialVersionUID = 1L;

    /**
     * 设置ID
     */
    @TableId
    private Long settingId;

    /**
     * 设置键名
     */
    private String settingKey;

    /**
     * 设置键值
     */
    private String settingValue;

    /**
     * 设置名称
     */
    private String settingName;

    /**
     * 设置类型（text/number/boolean/json）
     */
    private String settingType;

    /**
     * 设置分组
     */
    private String settingGroup;

    /**
     * 排序
     */
    private Integer sort;

    /**
     * 是否系统内置
     */
    private Integer isSystem;

    /**
     * 状态（0正常 1停用）
     */
    private Integer status;
}