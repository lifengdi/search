package com.lifengdi.model;

/**
 * @author 李锋镝
 * @date Create at 09:25 2019/8/28
 */
public class Key {

    private String key;

    public Key(String key) {
        this.key = key;
    }

    @Override
    public String toString() {
        return key;
    }

    public String getKey() {
        return key;
    }
}
