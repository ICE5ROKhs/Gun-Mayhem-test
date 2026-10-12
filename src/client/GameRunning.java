package client;

import background.BackGround; // 导入背景面板类
import javax.swing.*;

public class GameRunning extends JFrame { // 游戏运行的主窗口类

    private static int windowWidth = 885; // 窗口宽度
    private static int windowHeight = 424; // 窗口高度

    private BackGround bgPanel; // 背景面板对象

    private GameFrame gameFrame; // 游戏框架对象

    public GameRunning(GameFrame gameFrame) {
        // 设置窗口大小
        setSize(windowWidth, windowHeight);
        // 设置窗口居中显示
        setLocationRelativeTo(null);
        // 创建背景面板对象
        bgPanel = new BackGround(gameFrame);
        // 设置窗口默认关闭操作
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        // 设置窗口标题
        setTitle("Game");
        // 将背景面板添加到窗口
        add(bgPanel);
        // 让背景面板获取焦点
        bgPanel.requestFocusInWindow();
        // 设置窗口不可调整大小
        setResizable(false);

        // 将传入的游戏框架对象保存为成员变量
        this.gameFrame = gameFrame;

        // 设置窗口可见
        setVisible(true);

        // 使用 Timer 定期重绘背景面板，以实现动画效果
        javax.swing.Timer timer = new javax.swing.Timer(10, e -> {
            bgPanel.repaint(); // 重绘背景面板
        });
        timer.start();

        // 获取线程池管理器实例
        ThreadPoolManager threadPoolManager = ThreadPoolManager.getInstance();

        // 将背景面板的下落线程和消息线程提交给线程池执行
        threadPoolManager.execute(bgPanel.fallThread);
        threadPoolManager.execute(bgPanel.playerMessageThread);
    }

    // 提供获取游戏框架对象的方法
    public GameFrame getGameFrame() {
        return gameFrame;
    }
}