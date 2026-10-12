package client;

import DESencryption.DESencryption;
import background.BackgroundMusicThread;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class GameFrame extends JFrame implements Runnable {
    private JLabel backgroundLabel; // 背景标签
    private JLabel number; // 显示房间内人数的标签
    private JLabel playerName; // 显示玩家ID的标签
    private String id; // 玩家ID
    private JTextField idTextField; // 玩家ID输入框

    public static int numberOfPlayers = 0; // 房间内玩家总数

    private final String encryptionKey = "1234567890"; // 加密密钥
    private JButton startGameBtn; // 开始游戏按钮
    private JButton exitBtn; // 退出按钮




    public GameFrame() {
        super();
        setTitle("GUN MAYHEM REDUX"); // 设置窗口标题
        setSize(885, 424); // 设置窗口大小
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // 设置默认关闭操作
        setLocationRelativeTo(null); // 设置窗口居中显示

        // 创建背景标签
        backgroundLabel = new JLabel();
        backgroundLabel.setLayout(new BorderLayout());

        // 添加标题标签
        JLabel titleLabel = new JLabel("Welcome to GUN MAYHEM REDUX!");
        number = new JLabel("房间内人数:1");
        playerName = new JLabel("你的id: ");
        idTextField = new JTextField(10);

        idTextField.setPreferredSize(new Dimension(100, 20));
        idTextField.setMaximumSize(new Dimension(100,20));
        idTextField.repaint();
        number.setFont(new Font("宋体", Font.BOLD, 20));
        playerName.setFont(new Font("宋体", Font.BOLD, 20));
        number.setForeground(Color.RED);
        playerName.setForeground(Color.RED);
        titleLabel.setHorizontalAlignment(JLabel.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 30));

        setResizable(false);
        // 添加开始游戏按钮
        startGameBtn = new JButton("Get Ready");
        // 添加退出按钮
        exitBtn = new JButton("Exit");
        exitBtn.addActionListener(e -> System.exit(0));

        // 设置布局
        Box buttonPanel = new Box(BoxLayout.Y_AXIS);
        buttonPanel.setAlignmentX(Component.CENTER_ALIGNMENT);

        buttonPanel.add(startGameBtn);
        buttonPanel.add(Box.createVerticalStrut(10));

        buttonPanel.add(exitBtn);
        exitBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        startGameBtn.setAlignmentX(Component.CENTER_ALIGNMENT);

        buttonPanel.setAlignmentY(Component.BOTTOM_ALIGNMENT);

        // 将按钮面板添加到背景标签的中心
        backgroundLabel.add(titleLabel, BorderLayout.NORTH);

        Box box = new Box(BoxLayout.X_AXIS);

        box.add(number);
        box.add(Box.createHorizontalStrut(10));
        box.add(playerName);
        box.add(idTextField);
        box.setAlignmentX(Component.CENTER_ALIGNMENT);
        backgroundLabel.add(box, BorderLayout.CENTER);
        backgroundLabel.add(buttonPanel, BorderLayout.SOUTH);

        // 将背景标签添加到窗口
        add(backgroundLabel);

        exitBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    Socket socket = new Socket("127.0.0.1", 4444);
                    PrintWriter out = new PrintWriter(socket.getOutputStream(), true);

                    out.println("00");

                } catch (IOException ex) {
                    throw new RuntimeException(ex);
                }
            }
        });
        startGameBtn.addActionListener(e -> {

            if(idTextField.getText().equals("")){
                JOptionPane.showMessageDialog(this,
                        "请输入你的id",
                        "警告",
                        JOptionPane.WARNING_MESSAGE);
            }
            else {

                id = idTextField.getText();

                try {
                    Socket socket = new Socket("127.0.0.1", 4444);
                    PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
                    out.println("000_Add clientsIds_"+id);
                    out.println("999_add :"+id);
//                    startGameBtn.setEnabled(false);


                } catch (IOException ex) {
                    throw new RuntimeException(ex);
                }

//                if (numberOfPlayers == 3) {
//
//                    try {
//                        Socket socket = new Socket("127.0.0.1", 4444);
//                        PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
//                        out.println("002_让别人也进来");
//                        out.flush();
//                        out.close();
//                    } catch (IOException ex) {
//                        throw new RuntimeException(ex);
//                    }
//
//                } else {
//                    JOptionPane.showMessageDialog(this,
//                            "人数不够，" + number.getText(),
//                            "警告",
//                            JOptionPane.WARNING_MESSAGE);
//                }
            }

        });

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                try {
                    Socket socket = new Socket("127.0.0.1", 4444);
                    PrintWriter out = new PrintWriter(socket.getOutputStream(), true);

                    out.println("00");

                } catch (IOException ex) {
                    throw new RuntimeException(ex);
                }
            }
        });


    }


    private void createConnection() {
        try {
            Socket socket = new Socket("127.0.0.1", 4444);

            new Thread(new PlayerRead(socket, this)).start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {


        BackgroundMusicThread backgroundMusicThread = new BackgroundMusicThread();
        backgroundMusicThread.start();

        // 加载背景图片
        ImageIcon backgroundImage = null;
        try {
            backgroundImage = new ImageIcon(ImageIO.read(getClass().getResource("/images/bg.png")));
            backgroundLabel.setIcon(backgroundImage);
        } catch (IOException e) {
            e.printStackTrace();
        }

        // 确保 GUI 更新在事件分发线程上执行
        SwingUtilities.invokeLater(() -> {
            setVisible(true);
        });
        createConnection();
    }

    public static void main(String[] args) {
        // 创建并启动新线程
        Thread mainMenuThread = new Thread(new GameFrame());

        mainMenuThread.start();
    }

    public class PlayerRead implements Runnable {
        private Socket socket;
        private GameFrame gameFrame;

        public PlayerRead(Socket socket, GameFrame gameFrame) {
            this.socket = socket;
            this.gameFrame = gameFrame;
        }

        @Override
        public void run() {
            try (PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
                 BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {

                // 向服务器发送消息
                out.println("000_Connection Request from Client");

                out.println("888_"+DESencryption.encrypt("mimakyok", "12345678"));

                while (true) {
                    // 读取服务器的响应
                    String input = in.readLine();


                    // 检查 input 是否为 null
                    if (input == null) {
                        System.out.println("Connection closed by server");
                        return;
                    }

                    String[] inputArray = input.split("_");

                    // 检查 split() 的结果
                    if (inputArray.length >= 2) {

                        if (inputArray[0].equals("000")) {
                            System.out.println("server.Server responded: " + input);
                        }
                        else if (inputArray[0].equals("001")) {

                            SwingUtilities.invokeLater(() -> {
                                synchronized (gameFrame) {

                                    gameFrame.number.setText("房间内人数: " + inputArray[1]);

                                    numberOfPlayers = Integer.parseInt(inputArray[1]);

                                    System.out.println("server.Server responded001: " + input);
                                    gameFrame.repaint();
                                }
                            });
                        }
                        else if (inputArray[0].equals("00")) {
                            SwingUtilities.invokeLater(() -> {
                                synchronized (gameFrame) {
                                    gameFrame.number.setText("房间内人数: " + inputArray[1]);
                                    numberOfPlayers = Integer.parseInt(inputArray[1]);
                                    System.out.println("server.Server responded00: " + input);
                                    gameFrame.repaint();
                                }
                            });
                        } else if (inputArray[0].equals("003")) {
                            if(inputArray[1].equals("3 players has been ready")){
                                System.out.println("client receive:"+input);

                                new GameRunning(gameFrame).setVisible(true);

                                gameFrame.setVisible(false);
                            }

//                            id=idTextField.getText();
//
//                            if (numberOfPlayers == 3) {
//                                new client.GameRunning(gameFrame).setVisible(true);
//                                gameFrame.dispose(); // 关闭当前窗口
//                            } else {
//                                JOptionPane.showMessageDialog(gameFrame,
//                                        "人数不够，" + number,
//                                        "警告",
//                                        JOptionPane.WARNING_MESSAGE);
//                            }

                        }else if (inputArray[0].equals("100")) {
                            System.out.println("server.Server responded100: " + input);
                        } else if (inputArray[0].equals("101")) {


                        } else {
                            System.out.println("Unknown message type: " + inputArray[0]);
                        }
                    } else {
                        System.out.println("Invalid message format: " + input);
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

        }

    }

    public String getId() {
        return id;
    }

}