package com.lifengdi.search.impl;

import com.lifengdi.config.properties.IndexEntity;
import com.lifengdi.document.StoreDocument;
import com.lifengdi.model.FieldDefinition;
import com.lifengdi.model.IndexConfig;
import com.lifengdi.model.Key;
import com.lifengdi.search.SearchService;
import com.lifengdi.search.mapping.KeyMapping;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author 李锋镝
 * @date Create at 17:57 2019/8/27
 */
@Service
@Slf4j
public class StoreSearchService extends SearchService {

    private Map<Key, FieldDefinition> keyMappings;

    private Map<String, Map<Key, FieldDefinition>> keyMappingsMap;

    @Resource
    private IndexEntity indexEntity;

    // 默认排序
    private static final String DEFAULT_SORT = "-baseInfo.createdTime";

    private static final String DOC_CODE = IndexEntity.DOC_CODE_STORE;

    @PostConstruct
    private void init() {
        log.info("Mapping queries");
        keyMappingsMap = new HashMap<>();
        IndexConfig config = indexEntity.getConfigByDocCode(DOC_CODE);
        log.info("Get Config:{}", config);
        log.info("Mapping query[clusterName: {}, indexName: {}, indexType: {}]", "my-cluster",
                config.getIndexName(), config.getType());
        keyMappings = KeyMapping.mapping(StoreDocument.class);
        keyMappingsMap.put(config.getIndexName(), keyMappings);
        keyMappings.forEach((k, v) -> log.info("Mapped query {}", v));
    }

    /**
     * 查询
     * @param params 查询条件
     * @return page
     */
    public Page<Map> search(Map<String, String> params) {
        IndexConfig config = indexEntity.getConfigByDocCode(DOC_CODE);
        return commonSearch(params, config.getIndexName(), config.getType(), DEFAULT_SORT,
                keyMappings, keyMappingsMap);
    }

    public List aggregate(Map<String, String> params) {
        IndexConfig config = indexEntity.getConfigByDocCode(DOC_CODE);
        return aggregate(params, config.getIndexName(), config.getType(),
                keyMappings, keyMappingsMap);
    }

    /**
     * 游标查询
     * @param params 查询条件
     * @return page
     */
    public Page<Map> scroll(Map<String, String> params) {
        IndexConfig config = indexEntity.getConfigByDocCode(DOC_CODE);

        // 如果请求参数包含游标ID，则说明执行翻页操作，否则认为开启新的游标查询
        String scrollId = params.getOrDefault(SCROLL_ID, null);
        if (StringUtils.isNotBlank(scrollId)) {
            return commonContinueScroll(params.get(scrollId), config.getScrollTimeInMillis());
        }
        return commonStartScroll(params, config.getIndexName(), config.getType(), DEFAULT_SORT,
                keyMappings, keyMappingsMap, config.getScrollTimeInMillis());
    }

    /**
     * 查询
     * @param params 查询条件
     * @return page
     */
    public long count(Map<String, String> params) {
        IndexConfig config = indexEntity.getConfigByDocCode(DOC_CODE);
        return count(params, config.getIndexName(), config.getType(), null,
                keyMappings, keyMappingsMap);
    }

    /**
     * 根据ID获取索引
     * @param id ID
     * @return 索引
     */
    public Map get(String id) {
        IndexConfig config = indexEntity.getConfigByDocCode(DOC_CODE);
        return get(id, config.getIndexName(), config.getType());
    }

    public void clearScroll(String scrollId) {
        super.clearScroll(scrollId);
    }

}
