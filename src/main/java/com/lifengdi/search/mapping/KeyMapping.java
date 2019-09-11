package com.lifengdi.search.mapping;

import com.lifengdi.SearchApplication;
import com.lifengdi.model.FieldDefinition;
import com.lifengdi.model.Key;
import com.lifengdi.search.annotation.DefinitionQuery;
import com.lifengdi.search.annotation.DefinitionQueryRepeatable;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * @author 李锋镝
 * @date Create at 09:15 2019/8/28
 */
public class KeyMapping {

    // 启动类所在包
    private static final String BOOTSTRAP_PATH = SearchApplication.class.getPackage().getName();

    /**
     * 字段映射
     * @param clazz Class
     * @return Map
     */
    public static Map<Key, FieldDefinition> mapping(Class clazz) {
        Map<Key, FieldDefinition> mappings = mapping(clazz.getDeclaredFields(), "");
        mappings.putAll(typeMapping(clazz));
        return mappings;
    }

    /**
     * 字段映射
     *
     * @param fields      字段
     * @param parentField 父级字段名
     * @return Map
     */
    public static Map<Key, FieldDefinition> mapping(Field[] fields, String parentField) {
        Map<Key, FieldDefinition> mappings = new HashMap<>();
        for (Field field : fields) {
            org.springframework.data.elasticsearch.annotations.Field fieldAnnotation = field.getAnnotation
                    (org.springframework.data.elasticsearch.annotations.Field.class);
            String nestedPath = null;
            if (Objects.nonNull(fieldAnnotation) && FieldType.Nested.equals(fieldAnnotation.type())) {
                nestedPath = parentField + field.getName();
            }
            DefinitionQuery[] definitionQueries = field.getAnnotationsByType(DefinitionQuery.class);
            // 如果属性非BOOTSTRAP_PATH包下的类，说明属性为基础字段 即跳出循环，否则递归调用mapping
            if (!field.getType().getName().startsWith(BOOTSTRAP_PATH)) {
                for (DefinitionQuery definitionQuery : definitionQueries) {
                    buildMapping(parentField, mappings, field, nestedPath, definitionQuery);
                }
            } else {
                for (DefinitionQuery definitionQuery : definitionQueries) {
                    if (StringUtils.isNotBlank(definitionQuery.mapped())) {
                        buildMapping(parentField, mappings, field, nestedPath, definitionQuery);
                    }
                }
                mappings.putAll(mapping(field.getType().getDeclaredFields(), parentField + field.getName() + "."));
            }
        }
        return mappings;
    }

    /**
     * 构建mapping
     * @param parentField 父级字段名
     * @param mappings mapping
     * @param field 字段
     * @param nestedPath 默认嵌套路径
     * @param definitionQuery 字段定义
     */
    private static void buildMapping(String parentField, Map<Key, FieldDefinition> mappings, Field field,
                                     String nestedPath, DefinitionQuery definitionQuery) {
        FieldDefinition fieldDefinition;
        nestedPath = StringUtils.isNotBlank(definitionQuery.nestedPath()) ? definitionQuery.nestedPath() : nestedPath;
        String key = StringUtils.isBlank(definitionQuery.key()) ? field.getName() : definitionQuery.key();
        String filedName = StringUtils.isBlank(definitionQuery.mapped()) ? field.getName() : definitionQuery.mapped();
        switch (definitionQuery.type()) {
            case RANGE:
                buildRange(parentField, mappings, definitionQuery, key, filedName);
                break;
            default:
                fieldDefinition = FieldDefinition.builder()
                        .key(key)
                        .queryField(parentField + filedName)
                        .queryType(definitionQuery.type())
                        .separator(definitionQuery.separator())
                        .nestedPath(nestedPath)
                        .build();
                mappings.put(new Key(key), fieldDefinition);
                break;
        }
    }

    /**
     * 构建范围查询
     * @param parentField 父级字段名
     * @param mappings mapping
     * @param definitionQuery 字段定义
     * @param key 入参查询字段
     * @param filedName 索引文档中字段名
     */
    private static void buildRange(String parentField, Map<Key, FieldDefinition> mappings, DefinitionQuery definitionQuery,
                              String key, String filedName) {
        FieldDefinition fieldDefinition;
        String queryField = parentField + filedName;
        String rangeKeyFrom = key + definitionQuery.fromSuffix();
        String rangeKeyTo = key + definitionQuery.toSuffix();

        fieldDefinition = FieldDefinition.builder()
                .key(rangeKeyFrom)
                .queryField(queryField)
                .queryType(definitionQuery.type())
                .fromSuffix(definitionQuery.fromSuffix())
                .toSuffix(definitionQuery.toSuffix())
                .build();
        mappings.put(new Key(rangeKeyFrom), fieldDefinition);

        fieldDefinition = FieldDefinition.builder()
                .key(rangeKeyTo)
                .queryField(queryField)
                .queryType(definitionQuery.type())
                .fromSuffix(definitionQuery.fromSuffix())
                .toSuffix(definitionQuery.toSuffix())
                .build();
        mappings.put(new Key(rangeKeyTo), fieldDefinition);
    }

    /**
     * 对象映射
     * @param clazz document
     * @return Map
     */
    public static Map<Key, FieldDefinition> typeMapping(Class clazz) {
        DefinitionQueryRepeatable repeatable = (DefinitionQueryRepeatable) clazz.getAnnotation(DefinitionQueryRepeatable.class);
        Map<Key, FieldDefinition> mappings = new HashMap<>();
        for (DefinitionQuery definitionQuery : repeatable.value()) {
            String key = definitionQuery.key();
            switch (definitionQuery.type()) {
                case RANGE:
                    buildRange("", mappings, definitionQuery, key, definitionQuery.mapped());
                    break;
                default:
                    FieldDefinition fieldDefinition = FieldDefinition.builder()
                            .key(key)
                            .queryField(key)
                            .queryType(definitionQuery.type())
                            .separator(definitionQuery.separator())
                            .nestedPath(definitionQuery.nestedPath())
                            .build();
                    mappings.put(new Key(key), fieldDefinition);
                    break;
            }

        }
        return mappings;
    }


}
