package com.vmock.biz.service;

import com.vmock.biz.entity.Project;

import java.util.List;

public interface IProjectService {

    /**
     * 新增项目
     *
     * @param project
     * @return 结果
     */
    boolean insertMockProject(Project project);

    /**
     * 获取所有的项目
     *
     * @return 结果
     */
    List<Project> listAllProject();
}
