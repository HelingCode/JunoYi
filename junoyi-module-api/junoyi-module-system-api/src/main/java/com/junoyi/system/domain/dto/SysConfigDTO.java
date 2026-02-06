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
    private Long configId;

    /**
     * 参数键名
     */
    @JsonProperty("configKey")
    private String configKey;

    /**
     * 参数键值
     */
    @JsonProperty("configValue")
    private String configValue;

    /**
     * 参数名称
     */
    @JsonProperty("configName")
    private String configName;

    /**
     * 参数类型（text/number/boolean/json）
     */
    private String configType;

    /**
     * 参数分组
     */
    private String configGroup;

    /**
     * 排序
     */
    private Integer sort;

    /**
     * 系统内置（Y是 N否）
     */
    @JsonProperty("isSystem")
    private Integer isSystem;

    /**
     * 状态（0正常 1停用）
     */
    private Integer status;

    /**
     * 备注
     */
    private String remark;
}
