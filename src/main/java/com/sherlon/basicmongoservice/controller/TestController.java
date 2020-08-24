package com.sherlon.basicmongoservice.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author :  sherlonWang
 * @description :  add some desc...
 * @date: 2020-08-18
 */
@RestController
public class TestController {

    @GetMapping(value = "/hello")
    public String test(){
        return "hello world";
    }

}
