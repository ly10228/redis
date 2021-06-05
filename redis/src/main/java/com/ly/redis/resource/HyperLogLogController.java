package com.ly.redis.resource;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * @author luoyong
 *  * @create 2021-05-22 3:19 下午
 *  * @last modify by [luoyong 2021-05-22 3:19 下午]
 * @Description: 天猫网站首页亿级UV的Redis统计方案
 **/
@Api(description = "天猫网站首页亿级UV的Redis统计方案")
@RestController
@Slf4j
public class HyperLogLogController {

    @Resource
    private RedisTemplate redisTemplate;

    @ApiOperation("获得ip去重复后的首页访问量，总数统计")
    @RequestMapping(value = "/uv", method = RequestMethod.GET)
    public long uv() {
        //pfcount
        return redisTemplate.opsForHyperLogLog().size("hll");
    }
}
