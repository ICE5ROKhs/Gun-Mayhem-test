package server;

import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.net.Socket;

/**
 * 服务器日志查看器的图形用户界面类。
 * 该类继承自JFrame，实现了Runnable接口，用于显示服务器日志并处理与客户端的通信。
 */
public class LayoutGUI extends JFrame implements Runnable {
    private JTextArea logArea; // 用于显示日志信息的文本区域
    private JScrollPane scrollPane; // 用于滚动显示日志信息的面板

    private Socket socket; // 用于与客户端通信的Socket

    /**
     * 构造函数，初始化用户界面。
     */
    public LayoutGUI()
    {
        // 设置窗口标题
        super("Server Log Viewer");

        // 创建不可编辑的日志显示区域
        logArea = new JTextArea();
        logArea.setEditable(false);

        // 创建滚动面板，并设置其大小
        scrollPane = new JScrollPane(logArea);
        scrollPane.setPreferredSize(new Dimension(300, 400));

        // 将滚动面板添加到窗口中
        add(scrollPane);

        // 设置窗口大小
        setSize(300, 400);

        // 设置窗口在屏幕中心位置
        setLocationRelativeTo(null);

        // 设置窗口关闭时程序退出
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // 重定向System.out到logArea，以便在图形界面中显示输出
        System.setOut(new PrintStream(System.out) {
            @Override
            public void println(String message) {
                super.println(message);
                 logArea.append(message + "\n");
                logArea.setCaretPosition(logArea.getDocument().getLength());
            }
        });

        // 使窗口可见
        setVisible(true);
    }

    /**
     * Runnable接口的run方法，用于处理与客户端的通信。
     */
    @Override
    public void run()
    {
        try {
            // 连接到本地主机的4444端口
            socket=new Socket("127.0.0.1",4444);
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);

            // 向客户端发送标识信息
            out.println("777");
            String input;

            // 循环读取客户端发送的信息
            while ((input = in.readLine()) != null) {
                String[] inputArray = input.split("_", -1);

                // 如果接收到的标识与发送的标识匹配，则在控制台打印接收到的信息
                if(inputArray[0].equals("777")){
                    System.out.println(input);
                }

            }

        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }
}
