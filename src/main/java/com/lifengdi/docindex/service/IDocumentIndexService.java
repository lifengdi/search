package com.lifengdi.docindex.service;

import java.util.Map;

/**
 * @author 李锋镝
 * @date Create at 17:13 2019/8/23
 */
public interface IDocumentIndexService {

    /**
     * 加载扩展数据
     * @param sourceData 原数据
     */
    void loadExpansion(Map sourceData);

}
