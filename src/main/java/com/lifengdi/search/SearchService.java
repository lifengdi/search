package com.lifengdi.search;

import com.lifengdi.model.FieldDefinition;
import com.lifengdi.model.Key;
import com.lifengdi.model.MyBucket;
import com.lifengdi.search.enums.QueryTypeEnum;
import org.apache.commons.lang3.StringUtils;
import org.apache.lucene.search.join.ScoreMode;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.index.query.*;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.bucket.MultiBucketsAggregation;
import org.elasticsearch.search.sort.SortBuilders;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.core.aggregation.AggregatedPage;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.data.elasticsearch.core.query.SearchQuery;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

import static com.lifengdi.global.Global.*;

/**
 * @author 李锋镝
 * @date Create at 16:49 2019/8/27
 */
@Service
public class SearchService {

    @Resource
    private ElasticsearchTemplate elasticsearchTemplate;

    // 游标ID的参数名
    public static final String SCROLL_ID = "_scrollId";

    /**
     * 通用查询
     * @param params 查询入参
     * @param indexName 索引名称
     * @param type 索引类型
     * @param defaultSort 默认排序
     * @param keyMappings 字段映射
     * @param keyMappingsMap 索引对应字段映射
     * @return Page
     */
    protected Page<Map> commonSearch(Map<String, String> params, String indexName, String type, String defaultSort,
                             Map<Key, FieldDefinition> keyMappings,
                             Map<String, Map<Key, FieldDefinition>> keyMappingsMap) {
        SearchQuery searchQuery = buildSearchQuery(params, indexName, type, defaultSort, keyMappings, keyMappingsMap);
        return elasticsearchTemplate.queryForPage(searchQuery, Map.class);
    }

    protected List aggregate(Map<String, String> params, String indexName, String type,
                                            Map<Key, FieldDefinition> keyMappings,
                                            Map<String, Map<Key, FieldDefinition>> keyMappingsMap) {
        SearchQuery searchQuery = buildSearchQuery(params, indexName, type, null, keyMappings, keyMappingsMap);
        AggregatedPage<Map> aggregatedPage = elasticsearchTemplate.queryForPage(searchQuery, Map.class);

        return  aggregatedPage.getAggregations().asList().stream().map(aggregation -> {
            MultiBucketsAggregation bucketsAggregation = (MultiBucketsAggregation)aggregation;
            return Collections.singletonMap(aggregation.getName(), bucketsAggregation.getBuckets()
                    .stream()
                    .map(bucket -> new MyBucket(bucket.getKey(), bucket.getDocCount()))
                    .collect(Collectors.toList())
            );
        }).collect(Collectors.toList());
    }

    /**
     * 数量通用查询
     * @param params 查询入参
     * @param indexName 索引名称
     * @param type 索引类型
     * @param defaultSort 默认排序
     * @param keyMappings 字段映射
     * @param keyMappingsMap 索引对应字段映射
     * @return Page
     */
    protected long count(Map<String, String> params, String indexName, String type, String defaultSort,
                      Map<Key, FieldDefinition> keyMappings,
                      Map<String, Map<Key, FieldDefinition>> keyMappingsMap) {
        SearchQuery searchQuery = buildSearchQuery(params, indexName, type, defaultSort, keyMappings, keyMappingsMap);

        return elasticsearchTemplate.count(searchQuery);
    }

    /**
     * 根据ID获取索引
     * @param id ID
     * @param indexName 索引名
     * @param type 索引类型
     * @return 索引
     */
    protected Map get(String id, String indexName, String type) {
        return elasticsearchTemplate.getClient()
                .prepareGet(indexName, type, id)
                .execute()
                .actionGet()
                .getSourceAsMap();
    }

    /**
     * 根据定义的查询字段封装查询语句
     * @param params 查询入参
     * @param indexName 索引名称
     * @param type 索引类型
     * @param defaultSort 默认排序
     * @param keyMappings 字段映射
     * @param keyMappingsMap 索引对应字段映射
     * @return SearchQuery
     */
    private SearchQuery buildSearchQuery(Map<String, String> params, String indexName, String type, String defaultSort,
                                         Map<Key, FieldDefinition> keyMappings,
                                         Map<String, Map<Key, FieldDefinition>> keyMappingsMap) {
        NativeSearchQueryBuilder searchQueryBuilder = buildSearchField(params, indexName, type, keyMappings, keyMappingsMap);

        String sortFiled = params.getOrDefault(SORT, defaultSort);
        if (StringUtils.isNotBlank(sortFiled)) {
            String[] sorts = sortFiled.split(SPLIT_FLAG_COMMA);
            handleQuerySort(searchQueryBuilder, sorts);
        }

        return searchQueryBuilder.build();
    }

    /**
     * 根据定义的查询字段封装查询语句
     * @param params 查询入参
     * @param indexName 索引名称
     * @param type 索引类型
     * @param keyMappings 字段映射
     * @param keyMappingsMap 索引对应字段映射
     * @return NativeSearchQueryBuilder
     */
    private NativeSearchQueryBuilder buildSearchField(Map<String, String> params, String indexName, String type,
                                                        Map<Key, FieldDefinition> keyMappings,
                                                        Map<String, Map<Key, FieldDefinition>> keyMappingsMap) {

        int page = Integer.parseInt(params.getOrDefault(PAGE, "0"));
        int size = Integer.parseInt(params.getOrDefault(SIZE, "10"));

        AtomicBoolean matchSearch = new AtomicBoolean(false);

        String q = params.get(Q);
        String missingFields = params.get(MISSING);
        String existsFields = params.get(EXISTS);

        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
        BoolQueryBuilder boolFilterBuilder = QueryBuilders.boolQuery();


        Map<String, BoolQueryBuilder> nestedMustMap = new HashMap<>();
        Map<String, BoolQueryBuilder> nestedMustNotMap = new HashMap<>();
        List<String> fullTextFieldList = new ArrayList<>();

        // 查询条件构建器
        NativeSearchQueryBuilder searchQueryBuilder = new NativeSearchQueryBuilder()
                .withIndices(params.getOrDefault(INDEX_NAME, indexName))
                .withTypes(params.getOrDefault(INDEX_TYPE, type))
                .withPageable(PageRequest.of(page, size));

        String fields = params.get(FIELDS);
        if (Objects.nonNull(fields)) {
            searchQueryBuilder.withFields(fields.split(SPLIT_FLAG_COMMA));
        }

        keyMappingsMap.getOrDefault(params.getOrDefault(INDEX_NAME, indexName), keyMappings)
                .entrySet()
                .stream()
                .filter(m -> m.getValue().getQueryType() == QueryTypeEnum.FULLTEXT
                        || m.getValue().getQueryType() != QueryTypeEnum.IGNORE
                        && params.get(m.getKey().toString()) != null)
                .forEach(m -> {
                    String k = m.getKey().toString();
                    FieldDefinition v = m.getValue();
                    String queryValue = params.get(k);
                    QueryTypeEnum queryType = v.getQueryType();
                    String queryName = v.getQueryField();
                    String nestedPath = v.getNestedPath();
                    BoolQueryBuilder nestedMustBoolQuery = null;
                    BoolQueryBuilder nestedMustNotBoolQuery = null;
                    boolean nested = false;
                    if (StringUtils.isNotBlank(nestedPath)) {
                        nested = true;
                        if (nestedMustMap.containsKey(nestedPath)) {
                            nestedMustBoolQuery = nestedMustMap.get(nestedPath);
                        } else {
                            nestedMustBoolQuery = QueryBuilders.boolQuery();
                        }
                        if (nestedMustNotMap.containsKey(nestedPath)) {
                            nestedMustNotBoolQuery = nestedMustNotMap.get(nestedPath);
                        } else {
                            nestedMustNotBoolQuery = QueryBuilders.boolQuery();
                        }
                    }
                    switch (queryType) {
                        case RANGE:
                            RangeQueryBuilder rangeQueryBuilder = new RangeQueryBuilder(queryName);
                            if (k.endsWith(v.getFromSuffix())) {
                                rangeQueryBuilder.from(queryValue);
                            } else {
                                rangeQueryBuilder.to(queryValue);
                            }
                            boolFilterBuilder.must(rangeQueryBuilder);
                            break;
                        case FUZZY:
                            if (nested) {
                                if (k.startsWith(NON_FLAG)) {
                                    nestedMustBoolQuery.mustNot(QueryBuilders.wildcardQuery(queryName, queryValue));
                                } else {
                                    nestedMustBoolQuery.filter(QueryBuilders.wildcardQuery(queryName,
                                            StringUtils.wrapIfMissing(queryValue, WILDCARD)));
                                }
                            } else {
                                if (k.startsWith(NON_FLAG)) {
                                    boolFilterBuilder.mustNot(QueryBuilders.wildcardQuery(queryName, queryValue));
                                } else {
                                    boolFilterBuilder.filter(QueryBuilders.wildcardQuery(queryName,
                                            StringUtils.wrapIfMissing(queryValue, WILDCARD)));
                                }
                            }
                            break;
                        case PREFIX:
                            boolFilterBuilder.filter(QueryBuilders.prefixQuery(queryName, queryValue));
                            break;
                        case AND:
                            if (nested) {
                                for (String and : queryValue.split(v.getSeparator())) {
                                    nestedMustBoolQuery.must(QueryBuilders.termQuery(queryName, and));
                                }
                            } else {
                                for (String and : queryValue.split(v.getSeparator())) {
                                    boolFilterBuilder.must(QueryBuilders.termQuery(queryName, and));
                                }
                            }
                            break;
                        case IN:
                            String inQuerySeparator = v.getSeparator();
                            if (nested) {
                                buildIn(k, queryValue, queryName, nestedMustBoolQuery, inQuerySeparator, nestedMustNotBoolQuery);
                            } else {
                                buildIn(k, queryValue, queryName, boolFilterBuilder, inQuerySeparator);
                            }
                            break;
                        case SHOULD:
                            boolFilterBuilder.should(QueryBuilders.wildcardQuery(queryName,
                                    StringUtils.wrapIfMissing(queryValue, WILDCARD)));
                            break;
                        case FULLTEXT:
                            if (!Q.equalsIgnoreCase(queryName)) {
                                fullTextFieldList.add(queryName);
                            }
                            break;
                        case MATCH:
                            boolQueryBuilder.must(QueryBuilders.matchQuery(queryName, queryValue));
                            matchSearch.set(true);
                            break;
                        case EQUAL_IGNORE_CASE:
                            boolFilterBuilder.must(QueryBuilders.termQuery(queryName, queryValue.toLowerCase()));
                            break;
                        case AGGREGATION:
                            searchQueryBuilder.addAggregation(AggregationBuilders.terms(v.getKey())
                                    .field(queryName)
                                    .showTermDocCountError(true)
                                    .size(Integer.MAX_VALUE)
                            );
                            break;
                        default:
                            boolFilterBuilder.must(QueryBuilders.termQuery(queryName, queryValue));
                            break;
                    }
                    if (nested) {
                        if (nestedMustBoolQuery.hasClauses()) {
                            nestedMustMap.put(nestedPath, nestedMustBoolQuery);
                        }
                        if (nestedMustNotBoolQuery.hasClauses()) {
                            nestedMustNotMap.put(nestedPath, nestedMustNotBoolQuery);
                        }
                    }
                });
        if (StringUtils.isNotBlank(q)) {
            MultiMatchQueryBuilder multiMatchQueryBuilder = QueryBuilders.multiMatchQuery(q);
            fullTextFieldList.forEach(multiMatchQueryBuilder::field);
            boolQueryBuilder.should(multiMatchQueryBuilder);
        }
        if (StringUtils.isNotBlank(q) || matchSearch.get()) {
            searchQueryBuilder.withSort(SortBuilders.scoreSort().order(SortOrder.DESC));
        }
        if (StringUtils.isNotBlank(missingFields)) {
            for (String miss : missingFields.split(SPLIT_FLAG_COMMA)) {
                boolFilterBuilder.mustNot(QueryBuilders.existsQuery(miss));
            }
        }
        if (StringUtils.isNotBlank(existsFields)) {
            for (String exists : existsFields.split(SPLIT_FLAG_COMMA)) {
                boolFilterBuilder.must(QueryBuilders.existsQuery(exists));
            }
        }

        if (!CollectionUtils.isEmpty(nestedMustMap)) {
            for (String key : nestedMustMap.keySet()) {
                if (StringUtils.isBlank(key)) {
                    continue;
                }
                boolFilterBuilder.must(QueryBuilders.nestedQuery(key, nestedMustMap.get(key), ScoreMode.None));
            }
        }
        if (!CollectionUtils.isEmpty(nestedMustNotMap)) {
            for (String key : nestedMustNotMap.keySet()) {
                if (StringUtils.isBlank(key)) {
                    continue;
                }
                boolFilterBuilder.mustNot(QueryBuilders.nestedQuery(key, nestedMustNotMap.get(key), ScoreMode.None));
            }
        }

        searchQueryBuilder.withFilter(boolFilterBuilder);
        searchQueryBuilder.withQuery(boolQueryBuilder);

        return searchQueryBuilder;
    }

    private void buildIn(String k, String queryValue, String queryName, BoolQueryBuilder boolQuery, String separator) {
        buildIn(k, queryValue, queryName, boolQuery, separator, null);
    }

    private void buildIn(String k, String queryValue, String queryName, BoolQueryBuilder boolQuery, String separator,
                         BoolQueryBuilder nestedMustNotBoolQuery) {
        if (queryValue.contains(separator)) {
            if (k.startsWith(NON_FLAG)) {
                if (Objects.nonNull(nestedMustNotBoolQuery)) {
                    nestedMustNotBoolQuery.must(QueryBuilders.termsQuery(queryName, Arrays.asList(queryValue.split(separator))));
                } else {
                    boolQuery.mustNot(QueryBuilders.termsQuery(queryName, Arrays.asList(queryValue.split(separator))));
                }
            } else {
                boolQuery.must(QueryBuilders.termsQuery(queryName, Arrays.asList(queryValue.split(separator))));
            }
        } else {
            if (k.startsWith(NON_FLAG)) {
                if (Objects.nonNull(nestedMustNotBoolQuery)) {
                    nestedMustNotBoolQuery.must(QueryBuilders.termsQuery(queryName, queryValue));
                } else {
                    boolQuery.mustNot(QueryBuilders.termsQuery(queryName, queryValue));
                }
            } else {
                boolQuery.must(QueryBuilders.termsQuery(queryName, queryValue));
            }
        }
    }

    /**
     * 处理排序
     *
     * @param sorts 排序字段
     */
    private void handleQuerySort(NativeSearchQueryBuilder searchQueryBuilder, String[] sorts) {
        for (String sort : sorts) {
            sortBuilder(searchQueryBuilder, sort);
        }
    }

    private void sortBuilder(NativeSearchQueryBuilder searchQueryBuilder, String sort) {
        switch (sort.charAt(0)) {
            case '-': // 字段前有-: 倒序排序
                searchQueryBuilder.withSort(SortBuilders.fieldSort(sort.substring(1)).order(SortOrder.DESC));
                break;
            case '+': // 字段前有+: 正序排序
                searchQueryBuilder.withSort(SortBuilders.fieldSort(sort.substring(1)).order(SortOrder.ASC));
                break;
            default:
                searchQueryBuilder.withSort(SortBuilders.fieldSort(sort.trim()).order(SortOrder.ASC));
                break;
        }
    }

    /**
     * 获取一个符合查询条件的数据
     * @param filterBuilder 查询条件
     * @param indexName 索引名
     * @param type 索引类型
     * @return Map
     */
    protected Map<String, Object> getOne(TermQueryBuilder filterBuilder, String indexName, String type) {
        final SearchResponse searchResponse = elasticsearchTemplate.getClient()
                .prepareSearch(indexName)
                .setTypes(type)
                .setPostFilter(filterBuilder)
                .setSize(1)
                .get();
        final long total = searchResponse.getHits().getTotalHits();
        if (total > 0) {
            return searchResponse.getHits().getAt(0).getSourceAsMap();
        }
        return null;
    }

    /**
     * 游标查询
     * @param params 查询入参
     * @param indexName 索引名称
     * @param type 索引类型
     * @param defaultSort 默认排序
     * @param keyMappings 字段映射
     * @param keyMappingsMap 索引对应字段映射
     * @param scrollTimeInMillis 游标开启的时间
     * @return Page
     */
    protected Page<Map> commonStartScroll(Map<String, String> params, String indexName, String type, String defaultSort,
                                     Map<Key, FieldDefinition> keyMappings,
                                     Map<String, Map<Key, FieldDefinition>> keyMappingsMap, long scrollTimeInMillis) {
        SearchQuery searchQuery = buildSearchQuery(params, indexName, type, defaultSort, keyMappings, keyMappingsMap);
        return elasticsearchTemplate.startScroll(scrollTimeInMillis, searchQuery, Map.class);
    }

    /**
     * 游标查询
     * @param scrollId 游标ID
     * @param scrollTimeInMillis 游标开启的时间
     * @return Page
     */
    protected Page<Map> commonContinueScroll(String scrollId, long scrollTimeInMillis) {
        return elasticsearchTemplate.continueScroll(scrollId, scrollTimeInMillis, Map.class);
    }

    /**
     * 根据游标ID清除游标（提早释放资源，降低ES的负担）
     * @param scrollId 游标ID
     */
    protected void clearScroll(String scrollId) {
        elasticsearchTemplate.clearScroll(scrollId);
    }
}
