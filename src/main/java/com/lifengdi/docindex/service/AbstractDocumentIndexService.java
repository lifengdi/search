package com.lifengdi.docindex.service;

import com.alibaba.fastjson.JSON;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lifengdi.exception.BaseException;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.action.update.UpdateRequest;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.core.query.IndexQuery;
import org.springframework.data.elasticsearch.core.query.IndexQueryBuilder;
import org.springframework.data.elasticsearch.core.query.UpdateQuery;
import org.springframework.data.elasticsearch.core.query.UpdateQueryBuilder;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @author 李锋镝
 * @date Create at 16:38 2019/8/29
 */
@Component
@Slf4j
public abstract class AbstractDocumentIndexService implements IDocumentIndexService {

    @Resource
    private ElasticsearchTemplate elasticsearchTemplate;

    @Resource
    private ObjectMapper objectMapper;

    /**
     * 更新索引
     * @param indexName 索引名称
     * @param type 索引类型
     * @param id ID
     * @param jsonDoc JSON格式的文档
     * @param refresh 是否刷新索引
     * @return ID
     */
    public String index(String indexName, String type, String id, JsonNode jsonDoc, boolean refresh)
            throws JsonProcessingException {

        log.info("AbstractDocumentIndexService更新索引.indexName:{},type:{},id:{},jsonDoc:{}", indexName, type, id, jsonDoc);
        IndexQuery indexQuery = new IndexQueryBuilder()
                .withIndexName(indexName)
                .withType(type)
                .withId(id)
                .withSource(objectMapper.writeValueAsString(jsonDoc))
                .build();
        try {
            if (elasticsearchTemplate.indexExists(indexName)) {
                String index = elasticsearchTemplate.index(indexQuery);
                if (refresh) {
                    elasticsearchTemplate.refresh(indexName);
                }
                return index;
            }
        } catch (Exception e) {
            log.error("更新索引失败,刷新ES重试", e);
            elasticsearchTemplate.refresh(indexName);
            return elasticsearchTemplate.index(indexQuery);
        }
        throw BaseException.INDEX_NOT_EXISTS_EXCEPTION.build();
    }

    /**
     * 更新索引
     * @param indexName 索引名称
     * @param type 索引类型
     * @param id ID
     * @param jsonDoc JSON格式的文档
     * @param refresh 是否刷新ES索引
     * @return ID
     */
    public String index(String indexName, String type, String id, Map<String, Object> jsonDoc, boolean refresh) {

        loadExpansion(jsonDoc);
        try {
            JsonNode doc = objectMapper.readTree(JSON.toJSONString(jsonDoc));
            return index(indexName, type, id, doc, refresh);
        } catch (Exception e) {
            log.error("更新索引失败", e);
            throw BaseException.INDEX_EXCEPTION.build();
        }
    }

    @Override
    public void loadExpansion(Map sourceData) {}

    /**
     * 批量更新索引
     * @param indexName 索引名
     * @param type 索引类型
     * @param idList 需要更新的文档的ID
     * @param doc 需要更新的内容
     */
    public void bulkUpdate(String indexName, String type, List<Object> idList, Map doc) {
        List<UpdateQuery> updateQueryList = idList.stream().filter(Objects::nonNull)
                .map(id -> new UpdateQueryBuilder()
                        .withIndexName(indexName)
                        .withType(type)
                        .withId(id.toString())
                        .withUpdateRequest(new UpdateRequest(indexName, type, id.toString()).doc(doc))
                        .build())
                .collect(Collectors.toList());
        try {
            elasticsearchTemplate.bulkUpdate(updateQueryList);
            elasticsearchTemplate.refresh(indexName);
        } catch (Exception e) {
            log.error("批量更新索引失败", e);
            elasticsearchTemplate.refresh(indexName);
            elasticsearchTemplate.bulkUpdate(updateQueryList);
        }
    }

}
