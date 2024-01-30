package com.tencent.supersonic;

import com.google.common.collect.Lists;
import com.tencent.supersonic.auth.api.authentication.pojo.User;
import com.tencent.supersonic.auth.api.authorization.pojo.AuthGroup;
import com.tencent.supersonic.auth.api.authorization.pojo.AuthRule;
import com.tencent.supersonic.auth.api.authorization.service.AuthService;
import com.tencent.supersonic.common.pojo.JoinCondition;
import com.tencent.supersonic.common.pojo.ModelRela;
import com.tencent.supersonic.common.pojo.enums.AggOperatorEnum;
import com.tencent.supersonic.common.pojo.enums.AggregateTypeEnum;
import com.tencent.supersonic.common.pojo.enums.FilterOperatorEnum;
import com.tencent.supersonic.common.pojo.enums.SensitiveLevelEnum;
import com.tencent.supersonic.common.pojo.enums.StatusEnum;
import com.tencent.supersonic.common.pojo.enums.TimeMode;
import com.tencent.supersonic.common.pojo.enums.TypeEnums;
import com.tencent.supersonic.headless.api.pojo.MetricTypeDefaultConfig;
import com.tencent.supersonic.headless.api.pojo.QueryConfig;
import com.tencent.supersonic.headless.api.pojo.TagTypeDefaultConfig;
import com.tencent.supersonic.headless.api.pojo.TimeDefaultConfig;
import com.tencent.supersonic.headless.api.pojo.enums.DataType;
import com.tencent.supersonic.headless.api.pojo.enums.DimensionType;
import com.tencent.supersonic.headless.api.pojo.enums.IdentifyType;
import com.tencent.supersonic.headless.api.pojo.enums.MetricDefineType;
import com.tencent.supersonic.headless.api.pojo.enums.SemanticType;
import com.tencent.supersonic.headless.api.pojo.Dim;
import com.tencent.supersonic.headless.api.pojo.DimensionTimeTypeParams;
import com.tencent.supersonic.headless.api.pojo.DrillDownDimension;
import com.tencent.supersonic.headless.api.pojo.Field;
import com.tencent.supersonic.headless.api.pojo.FieldParam;
import com.tencent.supersonic.headless.api.pojo.Identify;
import com.tencent.supersonic.headless.api.pojo.Measure;
import com.tencent.supersonic.headless.api.pojo.MeasureParam;
import com.tencent.supersonic.headless.api.pojo.MetricDefineByFieldParams;
import com.tencent.supersonic.headless.api.pojo.MetricDefineByMeasureParams;
import com.tencent.supersonic.headless.api.pojo.MetricDefineByMetricParams;
import com.tencent.supersonic.headless.api.pojo.MetricParam;
import com.tencent.supersonic.headless.api.pojo.ModelDetail;
import com.tencent.supersonic.headless.api.pojo.RelateDimension;
import com.tencent.supersonic.headless.api.pojo.ViewDetail;
import com.tencent.supersonic.headless.api.pojo.ViewModelConfig;
import com.tencent.supersonic.headless.api.pojo.request.DatabaseReq;
import com.tencent.supersonic.headless.api.pojo.request.DimensionReq;
import com.tencent.supersonic.headless.api.pojo.request.DomainReq;
import com.tencent.supersonic.headless.api.pojo.request.MetricReq;
import com.tencent.supersonic.headless.api.pojo.request.ModelReq;
import com.tencent.supersonic.headless.api.pojo.request.ViewReq;
import com.tencent.supersonic.headless.server.service.DatabaseService;
import com.tencent.supersonic.headless.server.service.DimensionService;
import com.tencent.supersonic.headless.server.service.DomainService;
import com.tencent.supersonic.headless.server.service.MetricService;
import com.tencent.supersonic.headless.server.service.ModelRelaService;
import com.tencent.supersonic.headless.server.service.ModelService;
import com.tencent.supersonic.headless.server.service.ViewService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Component
@Slf4j
public class ModelDemoDataLoader {

    private User user = User.getFakeUser();
    @Autowired
    private DatabaseService databaseService;
    @Autowired
    private DomainService domainService;
    @Autowired
    private ModelService modelService;
    @Autowired
    private ModelRelaService modelRelaService;
    @Autowired
    private DimensionService dimensionService;
    @Autowired
    private MetricService metricService;
    @Autowired
    private AuthService authService;
    @Autowired
    private ViewService viewService;
    @Autowired
    private DataSourceProperties dataSourceProperties;

    public void doRun() {
        try {
            addDatabase();
            addDomain();
            addModel_1();
            addModel_2();
            addMetric_uv();
            addMetric_pv_avg();
            addModel_3();
            addModelRela_1();
            addModelRela_2();
            addDomain_2();
            addModel_4();
            updateDimension();
            updateMetric();
            updateMetric_pv();
            addView_1();
            addView_2();
            addAuthGroup_1();
            addAuthGroup_2();
        } catch (Exception e) {
            log.error("Failed to add model demo data", e);
        }

    }

    public void addDatabase() {
        String url = dataSourceProperties.getUrl();
        DatabaseReq databaseReq = new DatabaseReq();
        databaseReq.setName("数据实例");
        databaseReq.setDescription("样例数据库实例");
        if (StringUtils.isNotBlank(url)
                && url.toLowerCase().contains(DataType.MYSQL.getFeature().toLowerCase())) {
            databaseReq.setType(DataType.MYSQL.getFeature());
            databaseReq.setVersion("5.7");
        } else {
            databaseReq.setType(DataType.H2.getFeature());
        }
        databaseReq.setUrl(url);
        databaseReq.setUsername(dataSourceProperties.getUsername());
        databaseReq.setPassword(dataSourceProperties.getPassword());
        databaseService.createOrUpdateDatabase(databaseReq, user);
    }

    public void addDomain() {
        DomainReq domainReq = new DomainReq();
        domainReq.setName("超音数");
        domainReq.setBizName("supersonic");
        domainReq.setParentId(0L);
        domainReq.setStatus(StatusEnum.ONLINE.getCode());
        domainReq.setViewers(Arrays.asList("admin", "tom", "jack"));
        domainReq.setViewOrgs(Collections.singletonList("1"));
        domainReq.setAdmins(Collections.singletonList("admin"));
        domainReq.setAdminOrgs(Collections.emptyList());
        domainService.createDomain(domainReq, user);
    }

    public void addModel_1() throws Exception {
        ModelReq modelReq = new ModelReq();
        modelReq.setName("超音数用户部门");
        modelReq.setBizName("user_department");
        modelReq.setDescription("用户部门信息");
        modelReq.setDatabaseId(1L);
        modelReq.setDomainId(1L);
        modelReq.setViewers(Arrays.asList("admin", "tom", "jack"));
        modelReq.setViewOrgs(Collections.singletonList("1"));
        modelReq.setAdmins(Collections.singletonList("admin"));
        modelReq.setAdminOrgs(Collections.emptyList());
        ModelDetail modelDetail = new ModelDetail();
        List<Identify> identifiers = new ArrayList<>();
        identifiers.add(new Identify("用户", IdentifyType.primary.name(), "user_name", 1));
        modelDetail.setIdentifiers(identifiers);

        List<Dim> dimensions = new ArrayList<>();
        dimensions.add(new Dim("部门", "department",
                DimensionType.categorical.name(), 1));
        modelDetail.setDimensions(dimensions);
        List<Field> fields = Lists.newArrayList();
        fields.add(Field.builder().fieldName("user_name").dataType("Varchar").build());
        fields.add(Field.builder().fieldName("department").dataType("Varchar").build());
        modelDetail.setFields(fields);
        modelDetail.setMeasures(Collections.emptyList());
        modelDetail.setQueryType("sql_query");
        modelDetail.setSqlQuery("select user_name,department from s2_user_department");
        modelReq.setModelDetail(modelDetail);
        modelReq.setDomainId(1L);
        modelService.createModel(modelReq, user);
    }

    public void addModel_2() throws Exception {
        ModelReq modelReq = new ModelReq();
        modelReq.setName("超音数PVUV统计");
        modelReq.setBizName("s2_pv_uv_statis");
        modelReq.setDescription("超音数PVUV统计");
        modelReq.setDatabaseId(1L);
        modelReq.setViewers(Arrays.asList("admin", "tom", "jack"));
        modelReq.setViewOrgs(Collections.singletonList("1"));
        modelReq.setAdmins(Collections.singletonList("admin"));
        modelReq.setAdminOrgs(Collections.emptyList());
        List<Identify> identifiers = new ArrayList<>();
        ModelDetail modelDetail = new ModelDetail();
        identifiers.add(new Identify("用户名", IdentifyType.primary.name(), "user_name", 0));
        modelDetail.setIdentifiers(identifiers);

        List<Dim> dimensions = new ArrayList<>();
        Dim dimension1 = new Dim("", "imp_date", DimensionType.time.name(), 0);
        dimension1.setTypeParams(new DimensionTimeTypeParams());
        dimensions.add(dimension1);
        Dim dimension2 = new Dim("", "page", DimensionType.categorical.name(), 0);
        dimension2.setExpr("page");
        dimensions.add(dimension2);
        modelDetail.setDimensions(dimensions);
        List<Measure> measures = new ArrayList<>();
        Measure measure1 = new Measure("访问次数", "pv", AggOperatorEnum.SUM.name(), 1);
        measures.add(measure1);
        Measure measure2 = new Measure("访问用户数", "user_id", AggOperatorEnum.SUM.name(), 0);
        measures.add(measure2);
        modelDetail.setMeasures(measures);
        List<Field> fields = Lists.newArrayList();
        fields.add(Field.builder().fieldName("user_name").dataType("Varchar").build());
        fields.add(Field.builder().fieldName("imp_date").dataType("Date").build());
        fields.add(Field.builder().fieldName("page").dataType("Varchar").build());
        fields.add(Field.builder().fieldName("pv").dataType("Long").build());
        fields.add(Field.builder().fieldName("user_id").dataType("Varchar").build());
        modelDetail.setFields(fields);
        modelDetail.setSqlQuery("SELECT imp_date, user_name, page, 1 as pv, "
                + "user_name as user_id FROM s2_pv_uv_statis");
        modelDetail.setQueryType("sql_query");
        modelReq.setDomainId(1L);
        modelReq.setModelDetail(modelDetail);
        modelService.createModel(modelReq, user);
    }

    public void addModel_3() throws Exception {
        ModelReq modelReq = new ModelReq();
        modelReq.setName("停留时长统计");
        modelReq.setBizName("s2_stay_time_statis");
        modelReq.setDescription("停留时长统计");
        modelReq.setDatabaseId(1L);
        modelReq.setViewers(Arrays.asList("admin", "tom", "jack"));
        modelReq.setViewOrgs(Collections.singletonList("1"));
        modelReq.setAdmins(Collections.singletonList("admin"));
        modelReq.setAdminOrgs(Collections.emptyList());
        List<Identify> identifiers = new ArrayList<>();
        ModelDetail modelDetail = new ModelDetail();
        identifiers.add(new Identify("用户", IdentifyType.primary.name(), "user_name", 0));
        modelDetail.setIdentifiers(identifiers);

        List<Dim> dimensions = new ArrayList<>();
        Dim dimension1 = new Dim("", "imp_date", DimensionType.time.name(), 0);
        dimension1.setTypeParams(new DimensionTimeTypeParams());
        dimensions.add(dimension1);
        Dim dimension2 = new Dim("页面", "page", DimensionType.categorical.name(), 1);
        dimension2.setExpr("page");
        dimensions.add(dimension2);
        modelDetail.setDimensions(dimensions);

        List<Measure> measures = new ArrayList<>();
        Measure measure1 = new Measure("停留时长", "stay_hours", AggregateTypeEnum.SUM.name(), 1);
        measures.add(measure1);
        modelDetail.setMeasures(measures);
        List<Field> fields = Lists.newArrayList();
        fields.add(Field.builder().fieldName("user_name").dataType("Varchar").build());
        fields.add(Field.builder().fieldName("imp_date").dataType("Date").build());
        fields.add(Field.builder().fieldName("page").dataType("Varchar").build());
        fields.add(Field.builder().fieldName("stay_hours").dataType("Double").build());
        modelDetail.setFields(fields);
        modelDetail.setSqlQuery("select imp_date,user_name,stay_hours,page from s2_stay_time_statis");
        modelDetail.setQueryType("sql_query");
        modelReq.setDomainId(1L);
        modelReq.setModelDetail(modelDetail);
        modelService.createModel(modelReq, user);
    }

    public void addModelRela_1() {
        List<JoinCondition> joinConditions = Lists.newArrayList();
        joinConditions.add(new JoinCondition("user_name", "user_name", FilterOperatorEnum.EQUALS));
        ModelRela modelRelaReq = new ModelRela();
        modelRelaReq.setDomainId(1L);
        modelRelaReq.setFromModelId(1L);
        modelRelaReq.setToModelId(2L);
        modelRelaReq.setJoinType("left join");
        modelRelaReq.setJoinConditions(joinConditions);
        modelRelaService.save(modelRelaReq, user);
    }

    public void addModelRela_2() {
        List<JoinCondition> joinConditions = Lists.newArrayList();
        joinConditions.add(new JoinCondition("user_name", "user_name", FilterOperatorEnum.EQUALS));
        ModelRela modelRelaReq = new ModelRela();
        modelRelaReq.setDomainId(1L);
        modelRelaReq.setFromModelId(1L);
        modelRelaReq.setToModelId(3L);
        modelRelaReq.setJoinType("left join");
        modelRelaReq.setJoinConditions(joinConditions);
        modelRelaService.save(modelRelaReq, user);
    }

    public void addDomain_2() {
        DomainReq domainReq = new DomainReq();
        domainReq.setName("艺人库");
        domainReq.setBizName("supersonic");
        domainReq.setParentId(0L);
        domainReq.setStatus(StatusEnum.ONLINE.getCode());
        domainReq.setViewers(Arrays.asList("admin", "tom", "jack"));
        domainReq.setViewOrgs(Collections.singletonList("1"));
        domainReq.setAdmins(Collections.singletonList("admin"));
        domainReq.setAdminOrgs(Collections.emptyList());
        domainService.createDomain(domainReq, user);
    }

    public void addModel_4() throws Exception {
        ModelReq modelReq = new ModelReq();
        modelReq.setName("艺人库");
        modelReq.setBizName("singer");
        modelReq.setDescription("艺人库");
        modelReq.setDatabaseId(1L);
        modelReq.setDomainId(2L);
        modelReq.setViewers(Arrays.asList("admin", "tom", "jack"));
        modelReq.setViewOrgs(Collections.singletonList("1"));
        modelReq.setAdmins(Collections.singletonList("admin"));
        modelReq.setAdminOrgs(Collections.emptyList());
        ModelDetail modelDetail = new ModelDetail();
        List<Identify> identifiers = new ArrayList<>();
        Identify identify = new Identify("歌手名", IdentifyType.primary.name(), "singer_name", 1);
        identify.setEntityNames(Lists.newArrayList("歌手", "艺人"));
        identifiers.add(identify);
        modelDetail.setIdentifiers(identifiers);

        List<Dim> dimensions = new ArrayList<>();
        Dim dimension1 = new Dim("", "imp_date", DimensionType.time.name(), 0);
        dimension1.setTypeParams(new DimensionTimeTypeParams());
        dimensions.add(dimension1);
        dimensions.add(new Dim("活跃区域", "act_area",
                DimensionType.categorical.name(), 1, 1));
        dimensions.add(new Dim("代表作", "song_name",
                DimensionType.categorical.name(), 1));
        dimensions.add(new Dim("风格", "genre",
                DimensionType.categorical.name(), 1, 1));
        modelDetail.setDimensions(dimensions);

        Measure measure1 = new Measure("播放量", "js_play_cnt", "sum", 1);
        Measure measure2 = new Measure("下载量", "down_cnt", "sum", 1);
        Measure measure3 = new Measure("收藏量", "favor_cnt", "sum", 1);
        modelDetail.setMeasures(Lists.newArrayList(measure1, measure2, measure3));
        modelDetail.setQueryType("sql_query");
        modelDetail.setSqlQuery("select imp_date, singer_name, act_area, song_name, genre, "
                + "js_play_cnt, down_cnt, favor_cnt from singer");
        modelReq.setModelDetail(modelDetail);
        modelService.createModel(modelReq, user);
    }

    public void updateDimension() throws Exception {
        DimensionReq dimensionReq = new DimensionReq();
        dimensionReq.setType(DimensionType.categorical.name());
        dimensionReq.setId(3L);
        dimensionReq.setName("页面");
        dimensionReq.setBizName("page");
        dimensionReq.setModelId(3L);
        dimensionReq.setAlias("page");
        dimensionReq.setSemanticType(SemanticType.CATEGORY.name());
        dimensionReq.setSensitiveLevel(2);
        dimensionReq.setDescription("页面");
        dimensionReq.setExpr("page");
        dimensionReq.setDimValueMaps(Collections.emptyList());
        dimensionService.updateDimension(dimensionReq, user);
    }

    public void updateMetric() throws Exception {
        MetricReq metricReq = new MetricReq();
        metricReq.setModelId(3L);
        metricReq.setId(4L);
        metricReq.setName("停留时长");
        metricReq.setBizName("stay_hours");
        metricReq.setSensitiveLevel(SensitiveLevelEnum.HIGH.getCode());
        metricReq.setDescription("停留时长");
        metricReq.setTags(Collections.singletonList("核心指标"));
        metricReq.setAlias("访问时长");
        MetricDefineByMeasureParams metricTypeParams = new MetricDefineByMeasureParams();
        metricTypeParams.setExpr("s2_stay_time_statis_stay_hours");
        List<MeasureParam> measures = new ArrayList<>();
        MeasureParam measure = new MeasureParam("s2_stay_time_statis_stay_hours",
                "", AggOperatorEnum.SUM.getOperator());
        measures.add(measure);
        metricTypeParams.setMeasures(measures);
        metricReq.setMetricDefineByMeasureParams(metricTypeParams);
        metricReq.setMetricDefineType(MetricDefineType.MEASURE);
        metricReq.setRelateDimension(getRelateDimension(Lists.newArrayList(1L, 2L)));
        metricService.updateMetric(metricReq, user);
    }

    public void updateMetric_pv() throws Exception {
        MetricReq metricReq = new MetricReq();
        metricReq.setModelId(2L);
        metricReq.setId(1L);
        metricReq.setName("访问次数");
        metricReq.setBizName("pv");
        MetricDefineByMeasureParams metricTypeParams = new MetricDefineByMeasureParams();
        metricTypeParams.setExpr("s2_pv_uv_statis_pv");
        List<MeasureParam> measures = new ArrayList<>();
        MeasureParam measure = new MeasureParam("s2_pv_uv_statis_pv",
                "", AggOperatorEnum.SUM.getOperator());
        measures.add(measure);
        metricTypeParams.setMeasures(measures);
        metricReq.setMetricDefineByMeasureParams(metricTypeParams);
        metricReq.setMetricDefineType(MetricDefineType.MEASURE);
        metricReq.setRelateDimension(getRelateDimension(Lists.newArrayList(1L, 2L)));
        metricService.updateMetric(metricReq, user);
    }

    public void addMetric_uv() throws Exception {
        MetricReq metricReq = new MetricReq();
        metricReq.setModelId(2L);
        metricReq.setName("访问用户数");
        metricReq.setBizName("uv");
        metricReq.setSensitiveLevel(SensitiveLevelEnum.LOW.getCode());
        metricReq.setDescription("访问的用户个数");
        metricReq.setAlias("UV");
        MetricDefineByFieldParams metricTypeParams = new MetricDefineByFieldParams();
        metricTypeParams.setExpr("count(distinct user_id)");
        List<FieldParam> fieldParams = new ArrayList<>();
        fieldParams.add(new FieldParam("user_id"));
        metricTypeParams.setFields(fieldParams);
        RelateDimension relateDimension = new RelateDimension();
        relateDimension.setDrillDownDimensions(Lists.newArrayList(
                new DrillDownDimension(1L)));
        metricReq.setRelateDimension(relateDimension);
        metricReq.setMetricDefineByFieldParams(metricTypeParams);
        metricReq.setMetricDefineType(MetricDefineType.FIELD);
        metricReq.setRelateDimension(getRelateDimension(Lists.newArrayList(1L)));
        metricService.createMetric(metricReq, user);
    }

    public void addMetric_pv_avg() throws Exception {
        MetricReq metricReq = new MetricReq();
        metricReq.setModelId(2L);
        metricReq.setName("人均访问次数");
        metricReq.setBizName("pv_avg");
        metricReq.setSensitiveLevel(SensitiveLevelEnum.HIGH.getCode());
        metricReq.setDescription("每个用户平均访问的次数");
        metricReq.setTags(Collections.singletonList("核心指标"));
        metricReq.setAlias("平均访问次数");
        MetricDefineByMetricParams metricTypeParams = new MetricDefineByMetricParams();
        metricTypeParams.setExpr("pv/uv");
        List<MetricParam> metrics = new ArrayList<>();
        MetricParam metricPv = new MetricParam(1L, "pv");
        MetricParam metricUv = new MetricParam(2L, "uv");
        metrics.add(metricPv);
        metrics.add(metricUv);
        metricTypeParams.setMetrics(metrics);
        metricReq.setMetricDefineByMetricParams(metricTypeParams);
        metricReq.setMetricDefineType(MetricDefineType.METRIC);
        metricReq.setRelateDimension(getRelateDimension(Lists.newArrayList(1L)));
        metricService.createMetric(metricReq, user);
    }

    public void addView_1() {
        ViewReq viewReq = new ViewReq();
        viewReq.setName("超音数");
        viewReq.setBizName("s2");
        viewReq.setDomainId(1L);
        viewReq.setDescription("包含超音数访问统计相关的指标和维度等");
        viewReq.setAdmins(Lists.newArrayList("admin"));
        List<ViewModelConfig> viewModelConfigs = Lists.newArrayList(
                new ViewModelConfig(1L, true),
                new ViewModelConfig(2L, true),
                new ViewModelConfig(3L, true));

        ViewDetail viewDetail = new ViewDetail();
        viewDetail.setViewModelConfigs(viewModelConfigs);
        viewReq.setViewDetail(viewDetail);
        viewReq.setTypeEnum(TypeEnums.VIEW);
        QueryConfig queryConfig = new QueryConfig();
        MetricTypeDefaultConfig metricTypeDefaultConfig = new MetricTypeDefaultConfig();
        TimeDefaultConfig timeDefaultConfig = new TimeDefaultConfig();
        timeDefaultConfig.setTimeMode(TimeMode.RECENT);
        timeDefaultConfig.setUnit(7);
        metricTypeDefaultConfig.setTimeDefaultConfig(timeDefaultConfig);
        queryConfig.setMetricTypeDefaultConfig(metricTypeDefaultConfig);
        viewReq.setQueryConfig(queryConfig);
        viewService.save(viewReq, User.getFakeUser());
    }

    public void addView_2() {
        ViewReq viewReq = new ViewReq();
        viewReq.setName("艺人库");
        viewReq.setBizName("singer");
        viewReq.setDomainId(2L);
        viewReq.setDescription("包含艺人相关标签和指标信息");
        viewReq.setAdmins(Lists.newArrayList("admin"));
        List<ViewModelConfig> viewModelConfigs =
                Lists.newArrayList(new ViewModelConfig(4L, true));
        ViewDetail viewDetail = new ViewDetail();
        viewDetail.setViewModelConfigs(viewModelConfigs);
        viewReq.setViewDetail(viewDetail);
        viewReq.setTypeEnum(TypeEnums.VIEW);
        QueryConfig queryConfig = new QueryConfig();
        TagTypeDefaultConfig tagTypeDefaultConfig = new TagTypeDefaultConfig();
        TimeDefaultConfig tagTimeDefaultConfig = new TimeDefaultConfig();
        tagTimeDefaultConfig.setTimeMode(TimeMode.LAST);
        tagTimeDefaultConfig.setUnit(7);
        tagTypeDefaultConfig.setTimeDefaultConfig(tagTimeDefaultConfig);
        tagTypeDefaultConfig.setDimensionIds(Lists.newArrayList(4L, 5L, 6L, 7L));
        tagTypeDefaultConfig.setMetricIds(Lists.newArrayList(5L));
        MetricTypeDefaultConfig metricTypeDefaultConfig = new MetricTypeDefaultConfig();
        TimeDefaultConfig timeDefaultConfig = new TimeDefaultConfig();
        timeDefaultConfig.setTimeMode(TimeMode.RECENT);
        timeDefaultConfig.setUnit(7);
        metricTypeDefaultConfig.setTimeDefaultConfig(timeDefaultConfig);
        queryConfig.setTagTypeDefaultConfig(tagTypeDefaultConfig);
        queryConfig.setMetricTypeDefaultConfig(metricTypeDefaultConfig);
        viewReq.setQueryConfig(queryConfig);
        viewService.save(viewReq, User.getFakeUser());
    }

    public void addAuthGroup_1() {
        AuthGroup authGroupReq = new AuthGroup();
        authGroupReq.setModelId(3L);
        authGroupReq.setName("admin-permission");

        List<AuthRule> authRules = new ArrayList<>();
        AuthRule authRule = new AuthRule();
        authRule.setMetrics(Collections.singletonList("stay_hours"));
        authRule.setDimensions(Collections.singletonList("page"));
        authRules.add(authRule);

        authGroupReq.setAuthRules(authRules);
        authGroupReq.setAuthorizedUsers(Collections.singletonList("jack"));
        authGroupReq.setAuthorizedDepartmentIds(Collections.emptyList());
        authService.addOrUpdateAuthGroup(authGroupReq);
    }

    public void addAuthGroup_2() {
        AuthGroup authGroupReq = new AuthGroup();
        authGroupReq.setModelId(3L);
        authGroupReq.setName("tom_sales_permission");

        List<AuthRule> authRules = new ArrayList<>();
        AuthRule authRule = new AuthRule();
        authRule.setMetrics(Collections.singletonList("stay_hours"));
        authRule.setDimensions(Collections.singletonList("page"));
        authRules.add(authRule);

        authGroupReq.setAuthRules(authRules);
        authGroupReq.setDimensionFilters(Collections.singletonList("user_name = 'tom'"));
        authGroupReq.setDimensionFilterDescription("用户名='tom'");
        authGroupReq.setAuthorizedUsers(Collections.singletonList("tom"));
        authGroupReq.setAuthorizedDepartmentIds(Collections.emptyList());
        authService.addOrUpdateAuthGroup(authGroupReq);
    }

    private RelateDimension getRelateDimension(List<Long> dimensionIds) {
        RelateDimension relateDimension = new RelateDimension();
        for (Long id : dimensionIds) {
            relateDimension.getDrillDownDimensions().add(new DrillDownDimension(id));
        }
        return relateDimension;
    }

}