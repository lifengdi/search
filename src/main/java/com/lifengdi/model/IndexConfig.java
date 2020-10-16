package com.lifengdi.model;

import lombok.Data;

/**
 * @author 李锋镝
 * @date Create at 11:40 2019/8/28
 */
@Data
public class IndexConfig {

    /**
     * 文档编码
     */
    private String docCode;

    /**
     * 索引名称
     */
    private String indexName;

    /**
     * 索引类型
     */
    private String type;

    /**
     * 索引文档路径
     */
    private String documentPath;

    /**
     * 游标开启的时间
     */
    private Long scrollTimeInMillis;

}
