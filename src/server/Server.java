package server;

import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashSet;
import java.util.Set;

public class Server  {

    private static final int PORT = 4444;//设置监听端口
    private static final String HOST = "127.0.0.1";//ip地址
    private static Set<PrintWriter> clients = new HashSet<>(); // 集合用于存储所有客户端的 PrintWriter 实例

    /**
     * 主方法，启动服务器并监听客户端连接。
     *
     * @param args 命令行参数，此方法不使用这些参数
     */
    public static void main(String[] args) {
        try (
                // 创建ServerSocket实例，绑定到指定的主机和端口，并设置最大连接队列长度为50
                ServerSocket serverSocket = new ServerSocket(PORT, 50, InetAddress.getByName(HOST))
        ) {
            // 输出服务器启动信息，包括监听的端口号
            System.out.println("server.Server is listening on port " + PORT);

            // 创建一个新的线程，用于执行LayoutGUI任务（假设是显示服务器的GUI界面）
            Thread LayoutGUI = new Thread(new LayoutGUI());
            // 启动GUI线程
            LayoutGUI.start();

            // 无限循环，等待并接受客户端的连接请求
            while (true) {
                // accept()方法阻塞，直到一个客户端连接，返回代表该连接的Socket实例
                Socket socket = serverSocket.accept();

                // 为每个客户端创建一个新的线程，传入客户端Socket和客户端集合
                Thread t = new Thread(new PlayerServer(socket, clients));
                // 启动处理客户端连接的线程
                t.start();
            }
        } catch (IOException e) {
            // 如果在创建ServerSocket或处理连接时发生IO异常，打印异常堆栈信息
            e.printStackTrace();
        }
    }

}
