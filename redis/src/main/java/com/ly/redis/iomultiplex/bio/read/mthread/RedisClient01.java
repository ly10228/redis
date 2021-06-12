package com.ly.redis.iomultiplex.bio.read.mthread;

import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import java.util.Scanner;

/**
 * @author luoyong
 *  * @create 2021-06-12 4:00 下午
 *  * @last modify by [luoyong 2021-06-12 4:00 下午]
 * @Description: RedisClient01
 **/
public class RedisClient01 {
    public static void main(String[] args) throws IOException
    {
        Socket socket = new Socket("127.0.0.1",6379);
        OutputStream outputStream = socket.getOutputStream();

        //socket.getOutputStream().write("RedisClient01".getBytes());

        while(true)
        {
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
