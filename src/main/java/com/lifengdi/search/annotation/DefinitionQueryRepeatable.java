package com.lifengdi.search.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author 李锋镝
 * @date Create at 19:11 2019/8/27
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD, ElementType.TYPE})
public @interface DefinitionQueryRepeatable {
    DefinitionQuery[] value();
}
