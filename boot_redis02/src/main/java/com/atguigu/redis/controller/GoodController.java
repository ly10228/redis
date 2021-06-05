package com.atguigu.redis.controller;

import org.redisson.Redisson;
import org.redisson.api.RLock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @auther luoyong
 * @create 2020-09-15 14:48
 */
@RestController
public class GoodController {

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    public static final String KEY = "lyLock_0511";

    @Value("${server.port}")
    private String serverPort;
    @Autowired
    private Redisson redisson;


    private Lock lock = new ReentrantLock();


    /**
     * @return java.lang.String
     * @Description: 单机版本购买
     * 问题：单机版没加锁 会出现超卖
     * @author luoyong
     * @create 3:07 下午 2021/5/29
     * @last modify by [LuoYong 3:07 下午 2021/5/29 ]
     */
    @GetMapping("/buyGoods001")
    public String buyGoods001() throws Exception {
        String result = stringRedisTemplate.opsForValue().get("goods:001");
        int goodsNumber = result == null ? 0 : Integer.parseInt(result);
        if (goodsNumber > 0) {
            int realNumber = goodsNumber - 1;
            stringRedisTemplate.opsForValue().set("goods:001", realNumber + "");
            System.out.println("你已经成功秒杀商品，此时还剩余：" + realNumber + "件" + "\t 服务器端口：" + serverPort);
            return "你已经成功秒杀商品，此时还剩余：" + realNumber + "件" + "\t 服务器端口：" + serverPort;
        } else {
            System.out.println("商品已经售罄/活动结束/调用超时，欢迎下次光临" + "\t 服务器端口：" + serverPort);
        }
        return "商品已经售罄/活动结束/调用超时，欢迎下次光临" + "\t 服务器端口：" + serverPort;
    }


    /**
     * @return java.lang.String
     * @Description: 单机版本加锁
     * synchronized和ReentrantLock取舍-->精细化控制选ReentrantLock(不见不散 过时不候)
     * 缺点：单机版锁 都是自己玩自己的 多实例下 也会出现超卖的情况--->所以还得上分布式锁
     * @author luoyong
     * @create 3:07 下午 2021/5/29
     * @last modify by [LuoYong 3:07 下午 2021/5/29 ]
     */
    @GetMapping("/buyGoods002")
    public String buyGoods002() throws Exception {
        //不见不散
        //synchronized&& lock.lock()
        //过时不候
        //lock.tryLock() 抢到就用 抢不到立马就走
        //lock.tryLock(2, TimeUnit.SECONDS) 过时不候 超过2s还是抢不到直接走
        lock.lock();
        try {
            String result = stringRedisTemplate.opsForValue().get("goods:001");
            int goodsNumber = result == null ? 0 : Integer.parseInt(result);
            if (goodsNumber > 0) {
                int realNumber = goodsNumber - 1;
                stringRedisTemplate.opsForValue().set("goods:001", realNumber + "");
                System.out.println("你已经成功秒杀商品，此时还剩余：" + realNumber + "件" + "\t 服务器端口：" + serverPort);
                return "你已经成功秒杀商品，此时还剩余：" + realNumber + "件" + "\t 服务器端口：" + serverPort;
            } else {
                System.out.println("商品已经售罄/活动结束/调用超时，欢迎下次光临" + "\t 服务器端口：" + serverPort);
            }
        } catch (Exception ex) {

        } finally {
            lock.unlock();
        }

        return "商品已经售罄/活动结束/调用超时，欢迎下次光临" + "\t 服务器端口：" + serverPort;
    }


    /**
     * @return java.lang.String
     * @Description: 上分布式锁
     * @author luoyong
     * @create 5:08 下午 2021/5/29
     * @last modify by [LuoYong 5:08 下午 2021/5/29 ]
     */
    @GetMapping("/buyGoods003")
    public String buyGoods003() throws Exception {

        String value = UUID.randomUUID().toString() + Thread.currentThread().getName();
        Boolean flag = stringRedisTemplate.opsForValue().setIfAbsent(KEY, value);

        if (!flag) {
            return "抢锁失败,please try again";
        }

        String result = stringRedisTemplate.opsForValue().get("goods:001");
        int goodsNumber = result == null ? 0 : Integer.parseInt(result);
        if (goodsNumber > 0) {
            int realNumber = goodsNumber - 1;
            stringRedisTemplate.opsForValue().set("goods:001", realNumber + "");
            stringRedisTemplate.delete(KEY);
            System.out.println("你已经成功秒杀商品，此时还剩余：" + realNumber + "件" + "\t 服务器端口：" + serverPort);
            return "你已经成功秒杀商品，此时还剩余：" + realNumber + "件" + "\t 服务器端口：" + serverPort;
        } else {
            System.out.println("商品已经售罄/活动结束/调用超时，欢迎下次光临" + "\t 服务器端口：" + serverPort);
        }
        return "商品已经售罄/活动结束/调用超时，欢迎下次光临" + "\t 服务器端口：" + serverPort;
    }


    /**
     * @return
     * @Description: 程序执行异常保证锁被释放
     * 问题：服务器宕机？？？ 锁一样无法被释放 咋个解决？--->设置key的过期失效时间
     * @author luoyong
     * @create 5:20 下午 2021/5/29
     * @last modify by [LuoYong 5:20 下午 2021/5/29 ]
     */
    @GetMapping("/buyGoods004")
    public String buyGoods004() throws Exception {
        try {
            String value = UUID.randomUUID().toString() + Thread.currentThread().getName();
            Boolean flag = stringRedisTemplate.opsForValue().setIfAbsent(KEY, value);
            if (!flag) {
                return "抢锁失败,please try again";
            }

            String result = stringRedisTemplate.opsForValue().get("goods:001");
            int goodsNumber = result == null ? 0 : Integer.parseInt(result);
            if (goodsNumber > 0) {
                int realNumber = goodsNumber - 1;
                stringRedisTemplate.opsForValue().set("goods:001", realNumber + "");
                System.out.println("你已经成功秒杀商品，此时还剩余：" + realNumber + "件" + "\t 服务器端口：" + serverPort);
                return "你已经成功秒杀商品，此时还剩余：" + realNumber + "件" + "\t 服务器端口：" + serverPort;
            } else {
                System.out.println("商品已经售罄/活动结束/调用超时，欢迎下次光临" + "\t 服务器端口：" + serverPort);
            }
            return "商品已经售罄/活动结束/调用超时，欢迎下次光临" + "\t 服务器端口：" + serverPort;
        } finally {
            stringRedisTemplate.delete(KEY);
        }
    }

    /**
     * @return java.lang.String
     * @Description: 部署了微服务jar包的机器挂了，代码层面根本没有走到finally这块，没办法保证解锁，这个key没有被删除，需要加入一个过期时间限定key
     * 问题：设置key+过期时间不是原子性操作 设置key+过期时间分开了，必须要合并成一行具备原子性
     * @author luoyong
     * @create 5:28 下午 2021/5/29
     * @last modify by [LuoYong 5:28 下午 2021/5/29 ]
     */
    @GetMapping("/buyGoods005")
    public String buyGoods005() throws Exception {
        try {
            String value = UUID.randomUUID().toString() + Thread.currentThread().getName();
            Boolean flag = stringRedisTemplate.opsForValue().setIfAbsent(KEY, value);
            stringRedisTemplate.expire(KEY, 10L, TimeUnit.SECONDS);
            if (!flag) {
                return "抢锁失败,please try again";
            }

            String result = stringRedisTemplate.opsForValue().get("goods:001");
            int goodsNumber = result == null ? 0 : Integer.parseInt(result);
            if (goodsNumber > 0) {
                int realNumber = goodsNumber - 1;
                stringRedisTemplate.opsForValue().set("goods:001", realNumber + "");
                System.out.println("你已经成功秒杀商品，此时还剩余：" + realNumber + "件" + "\t 服务器端口：" + serverPort);
                return "你已经成功秒杀商品，此时还剩余：" + realNumber + "件" + "\t 服务器端口：" + serverPort;
            } else {
                System.out.println("商品已经售罄/活动结束/调用超时，欢迎下次光临" + "\t 服务器端口：" + serverPort);
            }
            return "商品已经售罄/活动结束/调用超时，欢迎下次光临" + "\t 服务器端口：" + serverPort;
        } finally {
            stringRedisTemplate.delete(KEY);
        }
    }


    /**
     * @return java.lang.String
     * @Description: 设置key+过期时间成原子性操作
     * 问题 张冠李戴 误删
     * @author luoyong
     * @create 5:33 下午 2021/5/29
     * @last modify by [LuoYong 5:33 下午 2021/5/29 ]
     */
    @GetMapping("/buyGoods006")
    public String buyGoods006() throws Exception {
        try {
            String value = UUID.randomUUID().toString() + Thread.currentThread().getName();
            //设置key+过期时间合成一条命令
            Boolean flag = stringRedisTemplate.opsForValue().setIfAbsent(KEY, value, 10L, TimeUnit.SECONDS);
            if (!flag) {
                return "抢锁失败,please try again";
            }

            String result = stringRedisTemplate.opsForValue().get("goods:001");
            int goodsNumber = result == null ? 0 : Integer.parseInt(result);
            if (goodsNumber > 0) {
                int realNumber = goodsNumber - 1;
                stringRedisTemplate.opsForValue().set("goods:001", realNumber + "");
                System.out.println("你已经成功秒杀商品，此时还剩余：" + realNumber + "件" + "\t 服务器端口：" + serverPort);
                return "你已经成功秒杀商品，此时还剩余：" + realNumber + "件" + "\t 服务器端口：" + serverPort;
            } else {
                System.out.println("商品已经售罄/活动结束/调用超时，欢迎下次光临" + "\t 服务器端口：" + serverPort);
            }
            return "商品已经售罄/活动结束/调用超时，欢迎下次光临" + "\t 服务器端口：" + serverPort;
        } finally {
            stringRedisTemplate.delete(KEY);
        }
    }


    @GetMapping("/buy_goods")
    public String buy_Goods() throws Exception {
        RLock redissonLock = redisson.getLock(KEY);
        redissonLock.lock();
        try {

            String result = stringRedisTemplate.opsForValue().get("goods:001");
            int goodsNumber = result == null ? 0 : Integer.parseInt(result);
            if (goodsNumber > 0) {
                int realNumber = goodsNumber - 1;
                stringRedisTemplate.opsForValue().set("goods:001", realNumber + "");
                System.out.println("你已经成功秒杀商品，此时还剩余：" + realNumber + "件" + "\t 服务器端口：" + serverPort);
                return "你已经成功秒杀商品，此时还剩余：" + realNumber + "件" + "\t 服务器端口：" + serverPort;
            } else {
                System.out.println("商品已经售罄/活动结束/调用超时，欢迎下次光临" + "\t 服务器端口：" + serverPort);
            }
            return "商品已经售罄/活动结束/调用超时，欢迎下次光临" + "\t 服务器端口：" + serverPort;
        } finally {
            if (redissonLock.isLocked() && redissonLock.isHeldByCurrentThread()) {
                redissonLock.unlock();
            }
        }
    }
}