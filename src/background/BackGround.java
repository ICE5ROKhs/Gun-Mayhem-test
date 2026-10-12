package background;

import client.GameFrame;
import model.Bullet;
import model.CowBoy;
import model.Platform;
import server.MailOut;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

/**
 * 背景类，负责游戏的背景渲染和玩家动作的监听。
 * 扩展自JPanel，实现KeyListener接口以处理键盘事件。
 */
public class BackGround extends JPanel implements KeyListener {
    // 背景图像
    private Image bg;
    // 玩家对象
    private CowBoy player;

    // 平台列表，用于碰撞检测和玩家移动
    private List<Platform> platforms = new ArrayList<>();

    // 控制玩家是否下落
    private boolean falling = true;
    // 游戏框架引用，用于交互
    private GameFrame gameFrame;
    // 第二个玩家对象，用于多人游戏
    private CowBoy player2 = null;
    // 第三个玩家对象，预留
    private CowBoy player3 = null;
    // 玩家ID，用于网络对战
    private String id;

    // 网络通信的Socket对象
    private Socket socket;
    // 输出流，用于向服务器发送数据
    private PrintWriter out;
    // 输入流，用于接收服务器数据
    private BufferedReader in;

    // 处理邮件输出的类，预留
    private MailOut mailOut;

    // 游戏是否结束的标志
    private int isOver=0;
    // 游戏是否开始的标志
    private boolean isStarted = false;

    /**
     * 获取玩家ID。
     * @return 玩家的唯一标识ID。
     */
    public String getId() {
        return id;
    }

    /**
     * 构造函数，初始化背景类。
     * @param gameFrame 游戏框架的引用，用于交互。
     */
    public BackGround(GameFrame gameFrame) {
        // 设置面板可获取焦点，以便监听键盘事件
        setFocusable(true);
        requestFocusInWindow();

        try {
            // 初始化网络连接
            socket = new Socket("127.0.0.1", 4444);
            out = new PrintWriter(socket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        // 设置游戏框架引用和玩家ID
        this.gameFrame = gameFrame;
        id = gameFrame.getId();

        // 输出玩家ID到控制台
        System.out.println("background.BackGround id=" + id);

        // 初始化玩家对象
        player = new CowBoy(this);
        // 创建玩家线程并启动
        Thread playerThread = new Thread(player);
        playerThread.start();

        platforms.add(new Platform(395, 69, 531 - 395, 1));

        platforms.add(new Platform(130, 133, 406 - 130, 1));
        platforms.add(new Platform(64, 194, 207 - 64, 1));
        platforms.add(new Platform(277, 194, 367 - 277, 1));
        platforms.add(new Platform(15, 257, 423 - 15, 1));

        platforms.add(new Platform(527, 133, 406 - 130, 1));
        platforms.add(new Platform(554, 191, 648 - 554, 1));
        platforms.add(new Platform(725, 192, 842 - 725, 1));
        platforms.add(new Platform(508, 254, 875 - 508, 1));

        platforms.add(new Platform(329, 321, 584 - 329, 1));


        bg = Toolkit.getDefaultToolkit().getImage(getClass().getResource("/images/bg1.png"));
        this.addKeyListener(this);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.drawImage(bg, 0, 0, this);
        for (Platform platform : platforms) {
            platform.draw(g);
        }
        if (player2 != null) {
            player2.draw(g);
            for (Bullet bullet : player2.getBullets()) {//所有人的子弹
                bullet.draw(g);
                if (bullet.isLive()) {
                    bullet.move();
                }
            }
        }
        if (player3 != null) {
            player3.draw(g);
            for (Bullet bullet : player3.getBullets()) {//所有人的子弹
                bullet.draw(g);
                if (bullet.isLive()) {
                    bullet.move();
                }
            }
        }
        player.draw(g);

        for (Bullet bullet : player.getBullets()) {//所有人的子弹
            bullet.draw(g);

        }

    }


    @Override
    public void keyTyped(KeyEvent e) {
    }

    @Override
    public void keyPressed(KeyEvent e) {

        switch (e.getKeyCode()) {
            case KeyEvent.VK_A:
                player.setMoving("left");

                break;
            case KeyEvent.VK_D:
                player.setMoving("right");


                break;
            case KeyEvent.VK_J:
                out.println("666_" + id + "_SHOOT");
                player.shoot();
                break;
            case KeyEvent.VK_S:
                player.setMoving("dawn");
                break;

        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
        switch (e.getKeyCode()) {
            case KeyEvent.VK_A, KeyEvent.VK_D:
                player.setMoving("stop");

                break;
            case KeyEvent.VK_SPACE:
                synchronized (player) {

                    player.setIsJumping(true); // 添加空格键跳跃

                    break;
                }

        }

    }

    @Override
    public void addKeyListener(KeyListener l) {
        super.addKeyListener(l);
    }

    public Thread playerMessageThread = new Thread(new Runnable() {
        @Override
        public void run() {
            try {


                out.println("100_" + id);

                Thread sendThread = new Thread(new Runnable() {
                    @Override
                    public void run() {

                        javax.swing.Timer timer1 = new javax.swing.Timer(10, e -> {
                            out.println("101_" + id + "_" + player.getX() + "_" + player.getY() + "_" + player.getStatus());
                        });
                        timer1.start();

                    }
                });
                sendThread.start();
                //ok
                while (true) {
                    if(isOver==0){
                        if(player2!=null&&player3!=null){
                            if(player.getHP()<=0){
                                JOptionPane.showMessageDialog(null, "玩家"+player.getID()+"输了！", "游戏结束", JOptionPane.INFORMATION_MESSAGE);
                                isOver=1;
                            }
                            else if(player2.getHP()<=0&&player3.getHP()<=0){
                                JOptionPane.showMessageDialog(null, "玩家"+player.getID()+"你赢了！", "游戏结束", JOptionPane.INFORMATION_MESSAGE);
                                mailOut = new MailOut("1551095653@qq.com","玩家id："+player.getID()+" 获胜了！！");
                                isOver=1;
                            }
                        }
                    }

                    //ok
                    String input = in.readLine();
                    if (input != null) {
                        //ok

                        String[] inputArray = input.split("_");
                        if (inputArray.length >= 1) {

                            if (inputArray[0].equals("200")) {

                                if (!inputArray[1].equals(id)) {

                                    SwingUtilities.invokeLater(() -> {
                                        if (player2 == null) {
                                            player2 = new CowBoy(inputArray[1],80);
                                            synchronized (player2){
                                                player2.setX(80);
                                            }
                                            out.println("999_" + id + "  player2_" + player2.getID()+" "+player2.getX());

                                        } else if (player3 == null && !inputArray[1].equals(player2.getID())) {
                                            player3 = new CowBoy(inputArray[1],100);
                                            synchronized (player3){
                                                player3.setX(100);
                                            }

                                            out.println("999_" + id + "  player3_" + player3.getID());
                                        }
                                    });

                                }

                            } else if (inputArray[0].equals("300")) {
                                out.println("200_" + id);

                            } else if (inputArray[0].equals("101")) {
                                SwingUtilities.invokeLater(() -> {
                                    if (player2 != null && inputArray[1].equals(player2.getID())) {
                                        player2.setX(Integer.parseInt(inputArray[2]));
                                        player2.setY(Integer.parseInt(inputArray[3]));
                                        player2.setStatus(inputArray[4]);

                                    } else if (player3 != null && inputArray[1].equals(player3.getID())) {
                                        ;
                                        player3.setX(Integer.parseInt(inputArray[2]));
                                        player3.setY(Integer.parseInt(inputArray[3]));
                                        player3.setStatus(inputArray[4]);

                                    }
                                });
                            } else if (inputArray[0].equals("666")) {
                                out.println("999_" + id + "SHOOT");
                                SwingUtilities.invokeLater(() -> {
                                    if (player2 != null && inputArray[1].equals(player2.getID())) {
                                        player2.shoot();
                                        out.println("999_" + id + "player2" + player2.getID() + " SHOOT,子弹添加 总数:" + player2.getBullets().size());

                                    } else if (player3 != null && inputArray[1].equals(player3.getID())) {
                                        player3.shoot();
                                        out.println("999_" + id + "player3" + player3.getID() + " SHOOT,子弹添加 总数:" + player3.getBullets().size());
                                    }
                                });
                            }else if (inputArray[0].equals("6666")){
                                SwingUtilities.invokeLater(() -> {
                                    if(player2!=null&&inputArray[1].equals(player2.getID())){
                                        out.println("999_6666lail2");
                                        player2.setHP(player2.getHP()-10);
                                    } else if (player3!=null&&inputArray[1].equals(player3.getID())) {
                                        player3.setHP(player3.getHP()-10);
                                        out.println("999_6666lail3");

                                    } else if (inputArray[1].equals(player.getID())) {
                                        player.setHP(player.getHP()-10);
                                        out.println("999_6666lail1");

                                    }
                                });

                            }
                        }
                    }
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    });


    public Thread fallThread = new Thread(new Runnable() {
        public void run() {
            while (true) {
                if (player.getY() > 885) {
                    player.setY(-60);
                }

                if (falling && !isCollisionDetected()) { // 只有当正在下落且未检测到碰撞时才增加y坐标
                    player.setY(player.getY() + 2);

                } else if (falling || (player.getIsMovingUp())) { // 如果检测到碰撞
                    falling = false; // 设置为不再下落

                    try {
                        Thread.sleep(5); // 暂停500毫秒，可以根据需要调整
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }

                    falling = true; // 再次设置为可以下落，以便重新检测碰撞
                }

                try {
                    Thread.sleep(5);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    break;
                }
            }
        }

        private boolean isCollisionDetected() {
            // 这里是碰撞检测的逻辑
            // 假设bgPanel.platforms是所有平台的列表
            Rectangle cowBoyBounds = new Rectangle(player.getX() + 15, player.getY() + 50, 27, 10);

            for (Platform platform : platforms) {
                if (cowBoyBounds.intersects(platform.getBounds())) {
                    player.resetIsMovingUp();
                    // 如果与平台相交，则停止下落
                    return true;
                }
            }
            // 如果没有检测到碰撞，返回false
            return false;
        }

    });

    public PrintWriter getOut() {
        return out;
    }

    public CowBoy getPlayer() {
        return player;
    }

    public CowBoy getPlayer2() {
        return player2;
    }

    public CowBoy getPlayer3() {
        return player3;
    }
}
