package com.ly.redis.iomultiplex.bio.accept;

import java.io.IOException;
import java.net.Socket;

/**
 * @author luoyong
 *  * @create 2021-06-12 3:54 下午
 *  * @last modify by [luoyong 2021-06-12 3:54 下午]
 * @Description: RedisClient02
 **/
public class RedisClient02 {
    public static void main(String[] args) throws IOException {
        System.out.println("------RedisClient02 start");
        Socket socket = new Socket("127.0.0.1", 6379);
    }
}
