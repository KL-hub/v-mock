package com.vmock.biz.service.impl;

import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.vmock.base.constant.CommonConst;
import com.vmock.biz.entity.Log;
import com.vmock.biz.entity.Response;
import com.vmock.biz.entity.Url;
import com.vmock.biz.enums.MainEnum;
import com.vmock.biz.mapper.ResponseMapper;
import com.vmock.biz.mapper.UrlMapper;
import com.vmock.biz.service.ILogService;
import com.vmock.biz.service.IResponseService;
import com.vmock.biz.util.AESUtil;
import com.vmock.biz.util.HttpClinetUtil;
import netscape.javascript.JSObject;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * 返回体Service业务层处理
 *
 * @author mock
 * @date 2019-11-20
 */
@Service
public class ResponseServiceImpl extends ServiceImpl<ResponseMapper, Response> implements IResponseService {

    @Autowired
    private ResponseMapper mockResponseMapper;
    @Autowired
    private UrlMapper urlMapper;

    @Autowired
    private ILogService iLogService;

    /**
     * 查询返回体列表
     *
     * @param mockResponse 返回体
     * @return 返回体
     */
    @Override
    public List<Response> selectMockResponseList(Response mockResponse) {
        return this.list(Wrappers.<Response>lambdaQuery()
                // 业务过滤 -> url id
                .eq(Response::getUrlId, mockResponse.getUrlId())
                // 条件查询 -> 是否启用
                .eq(mockResponse.getMain() != null, Response::getMain, mockResponse.getMain())
                // 条件查询 -> 简述
                .like(StrUtil.isNotBlank(mockResponse.getDescription()), Response::getDescription, mockResponse.getDescription())
                // 条件查询 -> 方法
                .eq(StrUtil.isNotBlank(mockResponse.getMethod()), Response::getMethod, mockResponse.getMethod()));
    }

    /**
     * 修改是否启用
     *
     * @param responseId
     * @param main
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public void changeMain(Long responseId, Integer main) {
        // 启用
        if (MainEnum.ENABLED.getCode().equals(main)) {
            Response targetEntity = this.getById(responseId);
            // set other disabled
            Response updateModel = new Response();
            updateModel.setMain(MainEnum.DISABLED.getCode());
            mockResponseMapper.update(updateModel, Wrappers.<Response>lambdaUpdate()
                    .eq(Response::getUrlId, targetEntity.getUrlId()));
        }
        // update [main] status
        Response mockResponse = new Response();
        mockResponse.setResponseId(responseId);
        mockResponse.setMain(main);
        this.updateById(mockResponse);
    }

    /**
     * 获取指定url使用中的返回体
     *
     * @param urlId
     * @return MockResponse
     */
    @Override
    public Response selectMainResponse(Long urlId) {
        return this.getOne(Wrappers.<Response>lambdaQuery()
                .eq(Response::getUrlId, urlId)
                // select enabled record
                .eq(Response::getMain, MainEnum.ENABLED.getCode())
                // limit
                .last(CommonConst.LIMIT_ONE));
    }

    @Override
    public boolean sendCallBackMessage(Long responseId, Long urlId) throws Exception {
        Response response = mockResponseMapper.selectById(responseId);
        Url url = urlMapper.selectById(urlId);
        String s="";
        if(StringUtils.isNotBlank(response.getSecretKey())){
            s = HttpClinetUtil.doPost(url.getUrl(), AESUtil.encrypt(response.getContent(),response.getSecretKey()), response.getCustomHeader());
        }else {
            s = HttpClinetUtil.doPost(url.getUrl(), response.getContent(), response.getCustomHeader());
        }
        Log log=new Log();
        log.setHitUrl(url.getUrl());
        JSONObject jsonObject = new JSONObject();
        JSONObject jsObject2 = new JSONObject();
        if(StringUtils.isNotBlank(response.getCustomHeader())){
            JSONArray jsonArray1 = new JSONArray(response.getCustomHeader());
            for (Object o : jsonArray1) {
                JSONObject jsonObject1 = new JSONObject(o);
                jsObject2.put(String.valueOf(jsonObject1.get("key")),jsonObject1.get("val"));
            }
        }
        jsonObject.put("headers",jsObject2);
        jsonObject.put("params",null);
        jsonObject.put("body",response.getContent());
        log.setRequestDetail(jsonObject.toString());

        JSONObject jsonObject1=new JSONObject();
        jsonObject1.put("respStatus",200);
        jsonObject1.put("respContent",s);
        log.setResponseDetail(jsonObject1.toString());
        iLogService.asyncInsert(log);
        return true;
    }
}
