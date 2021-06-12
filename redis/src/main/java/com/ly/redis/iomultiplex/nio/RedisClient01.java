package com.ly.redis.iomultiplex.nio;

import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import java.util.Scanner;

/**
 * @author luoyong
 *  * @create 2021-06-12 8:06 下午
 *  * @last modify by [luoyong 2021-06-12 8:06 下午]
 * @Description: RedisClient01
 **/
public class RedisClient01 {
    public static void main(String[] args) throws IOException {
        System.out.println("------RedisClient01 start");
        Socket socket = new Socket("127.0.0.1", 6379);
        OutputStream outputStream = socket.getOutputStream();
        while (true) {
            Scanner scanner = new Scanner(System.in);
            String string = scanner.next();
            if (string.equalsIgnoreCase("quit")) {
                break;
            }
            socket.getOutputStream().write(string.getBytes());
            System.out.println("------input quit keyword to finish......");
        }
        outputStream.close();
        socket.close();
    }
}
