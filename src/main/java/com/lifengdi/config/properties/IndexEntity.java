package com.lifengdi.config.properties;

import com.lifengdi.model.IndexConfig;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @author 李锋镝
 * @date Create at 14:06 2019/8/23
 */
@ConfigurationProperties("index-entity")
@Data
@Component
public class IndexEntity {

    public static final String DOC_CODE_STORE = "store";

    /**
     * 创建索引的文档配置
     */
    private List<IndexConfig> configs;

    /**
     * 根据文档编码获取配置信息
     * @param docCode 文档编码
     * @return 配置
     */
    public IndexConfig getConfigByDocCode(String docCode) {
        for (IndexConfig config : configs) {
            if (config.getDocCode().equals(docCode)) {
                return config;
            }
        }
        return null;
    }
}
