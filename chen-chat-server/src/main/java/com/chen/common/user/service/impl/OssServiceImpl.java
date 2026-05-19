package com.chen.common.user.service.impl;

import com.chen.common.common.utils.AssertUtil;
import com.chen.common.user.domain.enums.OssSceneEnum;
import com.chen.common.user.domain.vo.request.oss.UploadUrlReq;
import com.chen.common.user.service.OssService;
import com.chen.oss.MinIOTemplate;
import com.chen.oss.domain.OssReq;
import com.chen.oss.domain.OssResp;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Description:
 * Author: Chen
 * Date: 2023-06-20
 */
@Service
public class OssServiceImpl implements OssService {
    @Autowired
    private MinIOTemplate minIOTemplate;

    @Override
    public OssResp getUploadUrl(Long uid, UploadUrlReq req) {
        OssSceneEnum sceneEnum = OssSceneEnum.of(req.getScene());
        AssertUtil.isNotEmpty(sceneEnum, "场景有误");
        OssReq ossReq = OssReq.builder()
                .fileName(req.getFileName())
                .filePath(sceneEnum.getPath())
                .uid(uid)
                .build();
        return minIOTemplate.getPreSignedObjectUrl(ossReq);
    }
}
