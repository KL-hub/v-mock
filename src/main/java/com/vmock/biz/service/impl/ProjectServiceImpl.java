package com.vmock.biz.service.impl;

import cn.hutool.core.convert.Convert;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.vmock.biz.entity.Log;
import com.vmock.biz.entity.Project;
import com.vmock.biz.mapper.LogMapper;
import com.vmock.biz.mapper.ProjectlMapper;
import com.vmock.biz.service.ILogService;
import com.vmock.biz.service.IProjectService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * mock 项目 服务层处理
 *
 * @author mock
 */
@Service
public class ProjectServiceImpl extends ServiceImpl<ProjectlMapper, Project> implements IProjectService {


    @Override
    public boolean insertMockProject(Project project) {
        project.setDelFlag(0);
        return  this.save(project);
    }

    @Override
    public List<Project> listAllProject() {
        return this.list();
    }
}
