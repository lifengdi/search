package com.lifengdi.document.store;

import lombok.Data;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

/**
 * @author 李锋镝
 * @date Create at 18:15 2019/2/18
 */
@Data
public class StoreTags {
    @Field(type = FieldType.Keyword)
    private String key;

    @Field(type = FieldType.Keyword)
    private String value;

    private String showName;
}
