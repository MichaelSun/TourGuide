package com.find.guide.api.resource;

import java.io.File;

import android.os.Bundle;
import android.text.TextUtils;

import com.find.guide.api.base.PMRequestBase;
import com.plugin.internet.core.NetWorkException;
import com.plugin.internet.core.RequestEntity;
import com.plugin.internet.core.RequestEntity.MultipartFileItem;
import com.plugin.internet.core.annotations.NeedTicket;
import com.plugin.internet.core.annotations.RequiredParam;
import com.plugin.internet.core.annotations.RestMethodUrl;

@NeedTicket
@RestMethodUrl("resource/uploadResource")
public class UploadResourceRequest extends PMRequestBase<UploadResourceResponse> {

    private RequestEntity mRequestEntity;

    private String uploadFile;

    @RequiredParam("suffix")
    private String suffix = "jpg";

    public UploadResourceRequest(String file, String suffix) {
        this.uploadFile = file;
        this.suffix = suffix;
    }

    @Override
    public RequestEntity getRequestEntity() throws NetWorkException {
        if (mRequestEntity != null) {
            return mRequestEntity;
        }
        mRequestEntity = new RequestEntity();

        if (TextUtils.isEmpty(uploadFile)) {
            throw new RuntimeException("Param file MUST NOT be null");
        }
        File upload = new File(uploadFile);
        if (!upload.exists()) {
            throw new RuntimeException("upload file : " + uploadFile + " does not exist !!!");
        }

        Bundle params = this.getParams();
        mRequestEntity.setBasicParams(params);
        mRequestEntity.addFileItem(new MultipartFileItem("file", uploadFile, upload, null, "image/jpg"));
        mRequestEntity.setContentType(RequestEntity.REQUEST_CONTENT_TYPE_MUTIPART);
        return mRequestEntity;
    }
}
