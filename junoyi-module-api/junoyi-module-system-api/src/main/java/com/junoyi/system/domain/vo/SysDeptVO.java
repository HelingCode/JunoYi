package com.junoyi.system.domain.vo;

import lombok.Data;

import java.util.Date;
import java.util.List;

/**
 * 系统部门视图对象
 * 用于封装系统部门相关的数据传输和展示信息
 * 该类通常用于前端页面展示或API接口数据传输
 *
 * @author Fan
 */
@Data
public class SysDeptVO {

    private Long id;

    private Long parentId;

    private String name;

    private Integer sort;

    private String leader;

    private String phonenumber;

    private String email;

    private Integer status;

    private Date createTime;

    private Date updateTime;

    private String remark;

    /**
     * 子部门列表
     */
    private List<SysDeptVO> children;
}
