package com.tencent.supersonic.headless.api.pojo.request;

import com.google.common.collect.Lists;
import com.tencent.supersonic.headless.api.pojo.Cache;
import com.tencent.supersonic.headless.api.pojo.Param;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.digest.DigestUtils;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


@Data
@Slf4j
public abstract class SemanticQueryReq {

    protected boolean needAuth = true;

    protected Long viewId;

    protected String viewName;

    protected Set<Long> modelIds = new HashSet<>();

    protected List<Param> params = new ArrayList<>();

    protected Cache cacheInfo = new Cache();

    public void addModelId(Long modelId) {
        modelIds.add(modelId);
    }

    public String generateCommandMd5() {
        return DigestUtils.md5Hex(this.toCustomizedString());
    }

    public abstract String toCustomizedString();

    public List<Long> getModelIds() {
        return Lists.newArrayList(modelIds);
    }

    public Set<Long> getModelIdSet() {
        return modelIds;
    }

    public boolean isNeedAuth() {
        return needAuth;
    }

    public void setNeedAuth(boolean needAuth) {
        this.needAuth = needAuth;
    }
}
