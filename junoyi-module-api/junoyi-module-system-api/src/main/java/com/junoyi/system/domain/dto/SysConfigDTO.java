package com.junoyi.system.domain.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * 系统参数DTO
 *
 * @author Fan
 */
@Data
public class SysConfigDTO {

    /**
     * 参数ID（修改时必填）
     */
    @JsonProperty("id")
    private Long settingId;

    /**
     * 参数键名
     */
    @JsonProperty("configKey")
    private String settingKey;

    /**
     * 参数键值
     */
    @JsonProperty("configValue")
    private String settingValue;

    /**
     * 参数名称
     */
    @JsonProperty("configName")
    private String settingName;

    /**
     * 参数类型（text/number/boolean/json）
     */
    private String settingType;

    /**
     * 参数分组
     */
    private String settingGroup;

    /**
     * 排序
     */
    private Integer sort;

    /**
     * 系统内置（Y是 N否）
     */
    @JsonProperty("configType")
    private String configType;

    /**
     * 状态（0正常 1停用）
     */
    private Integer status;

    /**
     * 备注
     */
    private String remark;
}
