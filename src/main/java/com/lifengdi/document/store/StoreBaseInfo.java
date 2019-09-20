package com.lifengdi.document.store;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.lifengdi.search.annotation.DefinitionQuery;
import com.lifengdi.search.enums.QueryTypeEnum;
import com.lifengdi.serializer.JodaDateTimeDeserializer;
import com.lifengdi.serializer.JodaDateTimeSerializer;
import lombok.Data;
import org.joda.time.DateTime;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

/**
 * 门店基础信息
 * 
 */
@Data
public class StoreBaseInfo {

    /**
     * 门店id
     */
    @Field(type = FieldType.Keyword)
    private String storeId;

    /**
     * 门店名称
     */
    @Field(type = FieldType.Text, analyzer = "ik_smart")
    @DefinitionQuery(type = QueryTypeEnum.FUZZY)
    @DefinitionQuery(key = "name", type = QueryTypeEnum.SHOULD)
    private String storeName;

    /**
     * 门店简称
     */
    @Field(type = FieldType.Text, analyzer = "ik_smart")
    private String shortName;

    /**
     * 门店简介
     */
    @Field(type = FieldType.Text, analyzer = "ik_smart")
    private String profile;

    /**
     * 门店属性
     */
    @Field(type = FieldType.Integer)
    private Integer property;

    /**
     * 门店类型
     */
    @Field(type = FieldType.Integer)
    @DefinitionQuery(key = "typeAgg", type = QueryTypeEnum.AGGREGATION)
    private Integer type;

    /**
     * 详细地址
     */
    @Field(type = FieldType.Text, analyzer = "ik_smart")
    private String address;

    /**
     * 所在城市
     */
    @Field(type = FieldType.Keyword)
    @DefinitionQuery(type = QueryTypeEnum.IN)
    private String cityCode;

    /**
     * 城市名称
     */
    @Field(type = FieldType.Keyword)
    @DefinitionQuery(key = "cityNameAgg", type = QueryTypeEnum.AGGREGATION)
    private String cityName;

    /**
     * 所在省份
     */
    @Field(type = FieldType.Keyword)
    private String provinceCode;

    /**
     * 省份名称
     */
    @Field(type = FieldType.Keyword)
    private String provinceName;

    /**
     * 所在地区
     */
    @Field(type = FieldType.Keyword)
    private String regionCode;

    /**
     * 地区名称
     */
    @Field(type = FieldType.Keyword)
    private String regionName;

    /**
     * 所属市场id
     */
    @Field(type = FieldType.Long)
    @DefinitionQuery(type = QueryTypeEnum.IN)
    private Integer marketId;

    /**
     * 所属市场key
     */
    @Field(type = FieldType.Keyword)
    @DefinitionQuery(type = QueryTypeEnum.IN)
    private String marketKey;

    /**
     * 所属市场名称
     */
    @Field(type = FieldType.Keyword)
    private String marketName;

    /**
     * 摊位号
     */
    @Field(type = FieldType.Text)
    private String marketStall;

    /**
     * 门店状态
     */
    @Field(type = FieldType.Keyword)
    @DefinitionQuery(key = "storeStatus", type = QueryTypeEnum.IN)
    @DefinitionQuery(key = "_storeStatus", type = QueryTypeEnum.IN)
    private String status;

    /**
     * 删除标示
     */
    @Field(type = FieldType.Integer)
    @DefinitionQuery(key = "deleted")
    private Integer deleted;

    /**
     * 创建时间
     */
    @Field(type = FieldType.Date)
    @JsonDeserialize(using = JodaDateTimeDeserializer.class)
    @JsonSerialize(using = JodaDateTimeSerializer.class)
    @DefinitionQuery(type = QueryTypeEnum.RANGE)
    public DateTime createdTime;

    /**
     * 创建人id
     */
    @Field(type = FieldType.Keyword)
    @DefinitionQuery
    private String createdUserId;

    /**
     * 创建人名称
     */
    @Field(type = FieldType.Keyword)
    private String createdUserName;

    /**
     * 修改时间
     */
    @Field(type = FieldType.Date)
    @JsonDeserialize(using = JodaDateTimeDeserializer.class)
    @JsonSerialize(using = JodaDateTimeSerializer.class)
    private DateTime updatedTime;

    /**
     * 修改人ID
     */
    @Field(type = FieldType.Keyword)
    private String updatedUserId;

    /**
     * 修改人姓名
     */
    @Field(type = FieldType.Keyword)
    private String updatedUserName;

    /**
     * 业务类型
     */
    @Field(type = FieldType.Long)
    private Long businessType;

    /**
     * storeNo
     */
    @Field(type = FieldType.Keyword)
    @DefinitionQuery(type = QueryTypeEnum.SHOULD)
    private String storeNo;
}