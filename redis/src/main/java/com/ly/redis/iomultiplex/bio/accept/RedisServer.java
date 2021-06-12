package com.ly.redis.iomultiplex.bio.accept;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * @author luoyong
 *  * @create 2021-06-12 3:55 下午
 *  * @last modify by [luoyong 2021-06-12 3:55 下午]
 * @Description: RedisServer
 **/
public class RedisServer {
    public static void main(String[] args) throws IOException {
        byte[] bytes = new byte[1024];

        ServerSocket serverSocket = new ServerSocket(6379);

        while (true) {
            System.out.println("模拟RedisServer启动-----111 等待连接");
            Socket socket = serverSocket.accept();
            System.out.println("-----222 成功连接");
            System.out.println();
        }
    }
}
