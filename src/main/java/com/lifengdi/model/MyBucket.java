package com.lifengdi.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author 李锋镝
 * @date Create at 14:02 2019/9/20
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class MyBucket {

    private Object key;

    private Long docCount;
}
