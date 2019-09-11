package com.lifengdi.model;

import com.lifengdi.search.enums.QueryTypeEnum;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author 李锋镝
 * @date Create at 19:47 2019/8/27
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FieldDefinition {

    /**
     * 查询参数
     */
    private String key;

    /**
     * 查询类型
     */
    private QueryTypeEnum queryType;

    /**
     * 查询参数对应的文档中的字段
     */
    private String queryField;

    /**
     * from后缀
     */
    private String fromSuffix;

    /**
     * to后缀
     */
    private String toSuffix;

    /**
     * 分隔符
     */
    private String separator;

    /**
     * 嵌套查询的路径
     */
    private String nestedPath;
}
