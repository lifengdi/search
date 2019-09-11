package com.lifengdi.controller;

import org.springframework.beans.factory.annotation.Autowired;

import javax.servlet.http.HttpServletRequest;

/**
 * @author 李锋镝
 * @date Create at 14:59 2019/8/23
 */
public class BaseController {

    @Autowired
    protected HttpServletRequest request;
}
