package com.ly.redis.bloomfilter;

import org.redisson.Redisson;
import org.redisson.api.RBloomFilter;
import org.redisson.api.RBucket;
import org.redisson.api.RedissonClient;
import org.redisson.client.codec.StringCodec;
import org.redisson.config.Config;
import org.springframework.data.redis.core.RedisTemplate;

import javax.annotation.Resource;
import java.util.concurrent.TimeUnit;

/**
 * @author luoyong
 *  * @create 2021-05-23 12:26 下午
 *  * @last modify by [luoyong 2021-05-23 12:26 下午]
 * @Description: Redis布隆过滤器解决缓存穿透
 **/
public class RedissonBloomFilterDemo {
    public static final int _1W = 10000;
    //布隆过滤器里预计要插入多少数据
    public static int size = 100 * _1W;
    //误判率,它越小误判的个数也就越少
    public static double fpp = 0.03;


    //jedis
    static RedissonClient redissonClient = null;

    //redis版内置的布隆过滤器
    static RBloomFilter rBloomFilter = null;

    @Resource
    RedisTemplate redisTemplate;

    public static void main(String[] args) {
        //String phoneListById = getPhoneListById("10086");
        //请测试执行2次
        //String phoneListById = getPhoneListById("10087");
        String phoneListById = getPhoneListById("10088");
        System.out.println("------查询出来的结果： " + phoneListById);

        //暂停几秒钟线程
        try {
            TimeUnit.SECONDS.sleep(1);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        redissonClient.shutdown();
    }


    static {
        Config config = new Config();
        config.useSingleServer().setAddress("redis://192.168.111.147:6379").setDatabase(0);
        //构造redisson
        redissonClient = Redisson.create(config);
        //通过redisson构造rBloomFilter
        rBloomFilter = redissonClient.getBloomFilter("phoneListBloomFilter", new StringCodec());

        rBloomFilter.tryInit(size, fpp);

        // 1测试  布隆过滤器有+redis有
        //rBloomFilter.add("10086");
        //redissonClient.getBucket("10086",new StringCodec()).set("chinamobile10086");

        // 2测试  布隆过滤器有+redis无
        //rBloomFilter.add("10087");

        //3 测试 ，布隆过滤器无+redis无

    }


    private static String getPhoneListById(String IDNumber) {
        String result = null;

        if (IDNumber == null) {
            return null;
        }
        //1 先去布隆过滤器里面查询
        if (rBloomFilter.contains(IDNumber)) {
            //2 布隆过滤器里有，再去redis里面查询
            RBucket<String> rBucket = redissonClient.getBucket(IDNumber, new StringCodec());
            result = rBucket.get();
            if (result != null) {
                return "i come from redis: " + result;
            } else {
                result = getPhoneListByMySQL(IDNumber);
                if (result == null) {
                    return null;
                }
                // 重新将数据更新回redis
                redissonClient.getBucket(IDNumber, new StringCodec()).set(result);
            }
            return "i come from mysql: " + result;
        }
        return result;
    }

    private static String getPhoneListByMySQL(String IDNumber) {
        return "chinamobile" + IDNumber;
    }


}
