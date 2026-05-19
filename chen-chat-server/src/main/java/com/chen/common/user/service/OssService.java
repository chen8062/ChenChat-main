package com.chen.common.user.service;

import com.chen.common.user.domain.vo.request.oss.UploadUrlReq;
import com.chen.oss.domain.OssResp;

/**
 * <p>
 * oss 服务类
 * </p>
 *
 * @author Chen
 * @since 2023-03-19
 */
public interface OssService {

    /**
     * 获取临时的上传链接
     */
    OssResp getUploadUrl(Long uid, UploadUrlReq req);
}
