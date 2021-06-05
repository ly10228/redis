package com.ly.redis.service;

import com.ly.redis.constants.Constants;
import com.ly.redis.entity.Product;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;

/**
 * @author luoyong
 *  * @create 2021-05-29 11:53 上午
 *  * @last modify by [luoyong 2021-05-29 11:53 上午]
 * @Description: 聚划算活动 目前代码适用于单机 多机需要改造代码
 **/
@Service
@Slf4j
public class JHSTaskService {
    @Autowired
    private RedisTemplate redisTemplate;

    /**
     * @return void
     * @Description: init
     * 问题：
     * 1：redisTemplate.delete与redisTemplate.opsForList().leftPushAll不是原子性操作
     * @author luoyong
     * @create 1:25 下午 2021/5/29
     * @last modify by [LuoYong 1:25 下午 2021/5/29 ]
     */
    @PostConstruct
    public void initJHS() {
        log.info("启动定时器淘宝聚划算功能模拟");
        //模拟定时器 定时把mysql数据库的活动商品 刷新到redis当中
        new Thread(() -> {
            while (true) {
                //模拟定时器 从数据库读取100件特价商品 用于加载到聚划算的页面当中
                List<Product> productList = listProducts();
                //采用redis list数据结构的lpush来实现存储
                redisTemplate.delete(Constants.JHS_KEY);
                //lpush命令
                redisTemplate.opsForList().leftPushAll(Constants.JHS_KEY, productList);
                //一分钟执行一次
                try {
                    TimeUnit.MINUTES.sleep(1);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                log.info("runJHS定时刷新............");
            }
        }, "JHS").start();
    }

    /**
     * @return
     * @Description: 获取产品信息集合 模拟
     * @author luoyong
     * @create 12:01 下午 2021/5/29
     * @last modify by [LuoYong 12:01 下午 2021/5/29 ]
     */
    public List<Product> listProducts() {
        List<Product> productList = new ArrayList<>();
        for (int i = 0; i < 20; i++) {
            Random rand = new Random();
            int id = rand.nextInt(10000);
            Product obj = new Product((long) id, "product" + i, i, "detail");
            productList.add(obj);
        }
        return productList;
    }
}
