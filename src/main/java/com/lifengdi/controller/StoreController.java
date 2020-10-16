package com.lifengdi.controller;

import com.lifengdi.docindex.service.StoreDocumentIndexService;
import com.lifengdi.exception.BaseException;
import com.lifengdi.global.Global;
import com.lifengdi.response.ResponseResult;
import com.lifengdi.search.impl.StoreSearchService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.Arrays;
import java.util.Map;

/**
 * store
 * @author 李锋镝
 * @date Create at 17:27 2019/8/23
 */
@RestController
@RequestMapping("/store")
public class StoreController extends BaseController {

    @Resource
    private StoreDocumentIndexService storeDocumentIndexService;

    @Resource
    private StoreSearchService storeSearchService;

    /**
     * 保存索引
     * @param params 数据
     * @return ID
     */
    @PostMapping("/index")
    public ResponseResult index(@RequestBody Map<String, Object> params) {

        return ResponseResult.success(storeDocumentIndexService.index(params));
    }

    /**
     * 搜索
     * @param params 查询参数
     * @return 搜索结果
     */
    @PostMapping("/search")
    public ResponseResult search(@RequestBody Map<String, String> params) {

        return ResponseResult.success(storeSearchService.search(params));
    }

    /**
     * 搜索
     * @param params 查询参数
     * @return 搜索结果
     */
    @PostMapping("/aggregate")
    public ResponseResult aggregate(@RequestBody Map<String, String> params) {

        return ResponseResult.success(storeSearchService.aggregate(params));
    }

    /**
     * 搜索数量
     * @param params 查询参数
     * @return 搜索结果
     */
    @PostMapping("/count")
    public ResponseResult count(@RequestBody Map<String, String> params) {

        return ResponseResult.success(storeSearchService.count(params));
    }

    /**
     * 根据ID获取数据
     * @param id ID
     * @return 搜索结果
     */
    @GetMapping("/get/{id}")
    public ResponseResult get(@PathVariable String id) {
        return ResponseResult.success(storeSearchService.get(id));
    }

    /**
     * 批量更新
     * @param params 更新的数据
     * @return ResponseResult
     */
    @PostMapping("/sync/bulk/update")
    public ResponseResult bulkUpdate(@RequestBody Map<String, String> params) {
        if (!params.containsKey("ids") || !params.containsKey("source")) {
            return ResponseResult.fail(BaseException.NULL_PARAM_EXCEPTION.build());
        }
        storeDocumentIndexService.bulkUpdate(Arrays.asList(params.get("ids").split(Global.SPLIT_FLAG_COMMA)),
                params.get("source"));
        return ResponseResult.success(null);
    }

    @PostMapping("/scroll")
    public ResponseResult scroll(@RequestBody Map<String, String> params) {

        return ResponseResult.success(storeSearchService.scroll(params));
    }

    @GetMapping("/scroll/clear/{scrollId}")
    public ResponseResult clearScroll(@PathVariable String scrollId) {
        storeSearchService.clearScroll(scrollId);
        return ResponseResult.success(null);
    }

}
