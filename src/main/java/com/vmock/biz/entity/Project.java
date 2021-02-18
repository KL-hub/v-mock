package com.vmock.biz.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.vmock.base.annotation.Excel;
import lombok.Data;

/**
 * 新建一个项目
 *
 * @author mock
 * @date 2019-11-20
 */
@Data
@TableName("mock_project")
public class Project extends BaseEntity<Project> {

    /**
     * 主键
     */
    @TableId(type = IdType.AUTO)
    private Long projectId;

    /**
     * url名称
     */
    @Excel(name = "项目名称")
    private String projectName;

    /**
     * 描述
     */
    @Excel(name = "项目描述")
    private String description;


}
