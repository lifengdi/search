package com.lifengdi.controller;

import com.lifengdi.init.InitIndexService;
import com.lifengdi.response.ResponseResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * 索引
 * @author 李锋镝
 * @date Create at 14:59 2019/8/23
 */
@RestController
@RequestMapping("/index")
public class IndexController extends BaseController {

    @Resource
    private InitIndexService initIndexService;

    /**
     * 初始化索引、mapping
     * @return ResponseResult
     */
    @GetMapping("/init")
    public ResponseResult init() {
        return ResponseResult.success(initIndexService.initIndex());
    }
}
