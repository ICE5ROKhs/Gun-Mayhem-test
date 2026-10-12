package server;

import java.awt.*;
import java.io.*;
import java.net.Socket;
import java.util.*;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import DESencryption.DESencryption;
import database.DocSaving;


import javax.swing.*;

/**
 * PlayerServer 类实现了 Runnable 接口，用于处理客户端的连接和通信。
 * 它维护了与客户端的通信，包括接收消息、处理消息并发送响应。
 */
public class PlayerServer  implements Runnable {

    /**
     * 用于线程同步的锁对象。
     */
    private static final Object lock = new Object();

    /**
     * 加密使用的密钥。
     */
    private final String encryptionKey = "1234567890";

    /**
     * 客户端的Socket连接。
     */
    private final Socket socket;

    /**
     * 日志记录器。
     */
    private static final Logger LOGGER = Logger.getLogger(PlayerServer.class.getName());

    /**
     * 存储所有客户端ID的列表。
     */
    private List<String> clientIds;

    /**
     * 当前在线玩家的数量。
     */
    private static int numberOfPlayers = 0;

    /**
     * 用于文档保存的实例。
     */
    private DocSaving docSaving = new DocSaving();

    /**
     * 已准备好的玩家数量。
     */
    private static int readyNumber = 0;

    /**
     * 存储所有客户端打印流的集合，用于广播消息。
     */
    private static Set<PrintWriter> clients = new HashSet<>();

    /**
     * 使用ConcurrentMap存储客户端连接，以支持并发操作。
     */
    private static ConcurrentMap<String,PrintWriter> clientsConnection = new ConcurrentHashMap<>();

    /**
     * 计数器，用于唯一标识客户端连接。
     */
    private static int count=0;

    /**
     * 标志位，用于检查客户端ID是否已存在。
     */
    private static boolean exists;

    /**
     * 用于向特定客户端发送消息的打印流。
     */
    private PrintWriter serverOut;

    /**
     * PlayerServer 构造函数初始化一个客户端连接。
     *
     * @param socket 客户端的Socket连接。
     * @param clients 存储所有客户端打印流的集合。
     */
    public PlayerServer(Socket socket, Set<PrintWriter> clients) {
        docSaving.initializeDatabase();
        this.socket = socket;
        this.clients = clients;
        this.clientIds = new ArrayList<>();
    }

    /**
     * 自定义线程，用于检查是否所有玩家都已准备就绪。
     */
    Thread startThread = new Thread(new Runnable() {
        public void run() {
            if (readyNumber == 3) {
                broadcastMessage("003_3 players has been ready");
                readyNumber = 0;
            }
        }
    });

    /**
     * Runnable接口的run方法，用于处理客户端的通信。
     */
    @Override
    public void run() {
        try (PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
             BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {

            String input;
            startThread.start();

            while ((input = in.readLine()) != null) {
                String[] inputArray = input.split("_", -1);

                // 处理客户端请求连接的消息
                if (inputArray.length > 1 && inputArray[0].equals("000")) {
                    if (inputArray[1].equals("Connection Request from Client")) {
                        out.println("000_Connection Accepted");
                        numberOfPlayers++;
                        clients.add(out);
                        broadcastMessage("00_" + numberOfPlayers);
                        
                        out.println("001_" + numberOfPlayers);
                    } else if (inputArray[1].equals("Add clientsIds")) {
                        exists = false;
                        Iterator<String> iterator = clientIds.iterator();
                        for (String clientId : clientIds) {
                            if (clientId.equals(inputArray[2])) {
                                exists = true;
                                break;
                            }
                        }
                        if (!exists) {
                            clientIds.add(inputArray[2]);
                            docSaving.saveUserId(inputArray[2]);
                            readyNumber++;
                        }
                    }
                    // 处理客户端断开连接的消息
                } else if (inputArray.length == 1 && inputArray[0].equals("00")) {
                    numberOfPlayers--;
                    broadcastMessage("00_" + numberOfPlayers);
                    clients.remove(out);
                    // 处理其他类型的消息
                } else if(inputArray.length > 1 && inputArray[0].equals("100")){
                    clientsConnection.put(String.valueOf(count),out);
                    count++;
                    if(count==3){
                        broadcastConnection("300_3");
                    }
                } else if (inputArray.length > 1 && inputArray[0].equals("200")) {
                    broadcastConnection(input);
                } else if(inputArray.length > 1 && inputArray[0].equals("101")){
                    broadcastConnection(input);
                } else if (inputArray.length>1&&inputArray[0].equals("999")) {
                } else if (inputArray.length>1&&inputArray[0].equals("666")) {
                    broadcastConnection(input);
                } else if (inputArray.length>1&&inputArray[0].equals("6666")) {
                    broadcastConnection(input);
                }else if (inputArray[0].equals("777")){
                    serverOut =out;
                } else if (inputArray.length>1&&inputArray[0].equals("888")) {
                    System.out.println("888 解密后:"+DESencryption.decrypt(inputArray[1], encryptionKey));
                }
                if (serverOut!=null){
                    serverOut.println("777_" + input);
                }
                LOGGER.info("Server Received: "+ input);
            }
        } catch (IOException e) {
            System.out.println("User disconnected   " + e.getMessage());
            LOGGER.warning("User disconnected: " + e.getMessage());
        } finally {
            try {
                socket.close();
            } catch (IOException e) {
                System.out.println("User closed socket: " + e.getMessage());
                LOGGER.warning("User closed socket: " + e.getMessage());
            }
        }
    }

    /**
     * 向所有客户端广播消息。
     *
     * @param message 要广播的消息。
     */
    public static void broadcastMessage(String message) {
        synchronized (lock) {
            for (PrintWriter client : clients) {
                client.println(message);
            }
        }
    }

    /**
     * 向所有连接的客户端广播连接状态消息。
     *
     * @param message 包含连接状态信息的消息。
     */
    public static void broadcastConnection(String message) {
        for (PrintWriter client : clientsConnection.values()) {
            client.println(message);
        }
    }
}
