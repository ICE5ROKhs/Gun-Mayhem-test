package model;

import java.awt.*;
import java.util.*;
import java.util.List;
import background.BackGround;


/**
 * CowBoy 类实现了 Runnable 接口，用于表示游戏中的牛仔角色。
 * 该类负责牛仔的移动、射击和绘制等行为。
 */
public class CowBoy implements Runnable{
    // 牛仔向左和向右行走的图像资源
    private final Image imageLeft;
    private final Image imageRight;

    // 牛仔当前的状态，"right" 表示向右，"left" 表示向左
    private String status="right";

    // 背景对象，用于与背景的交互
    private BackGround backGround;
    // 牛仔的坐标和速度
    private int x = 60;
    private int y = 60;
    private int speed = 2;
    // 牛仔的尺寸
    private final int width = 60;
    private final int height = 60;

    // 跳跃的高度和时间，用于控制跳跃行为
    private int jumpHeight = 15; // 跳跃初始速度
    private int jumpTime = 7;

    // 控制牛仔左右移动的标志位
    private boolean movingLeft = false;
    private boolean movingRight = false;

    // 控制牛仔向上移动的标志位
    private boolean isMovingUp = true;

    // 控制牛仔跳跃的标志位
    private boolean isJumping = false;
    // 控制牛仔射击的标志位
    private boolean isShooting = false;

    // 存储牛仔发射的子弹
    private Vector<Bullet> bullets=new Vector<>();

    // 牛仔的血量
    private int HP=100;

    // 牛仔的唯一标识ID
    private String ID;


    /**
     * 构造函数，初始化牛仔对象，并加载图像资源。
     * @param backGround 背景对象，用于与背景交互。
     */
    public CowBoy(BackGround backGround) {
        Toolkit toolkit = Toolkit.getDefaultToolkit();
        imageLeft = toolkit.getImage(getClass().getResource("/images/2.png"));
        imageRight = toolkit.getImage(getClass().getResource("/images/1.png"));

        this.backGround = backGround;

        ID= backGround.getId();

    }

    /**
     * 构造函数，初始化牛仔对象的位置，并加载图像资源。
     * @param id 牛仔的唯一标识ID。
     * @param x 牛仔的初始x坐标。
     */
    public CowBoy (String id,int x){
        Toolkit toolkit = Toolkit.getDefaultToolkit();
        imageLeft = toolkit.getImage(getClass().getResource("/images/2.png"));
        imageRight = toolkit.getImage(getClass().getResource("/images/1.png"));
        this.x = x;
        ID= id;
    }

    /**
     * 绘制牛仔及其相关信息（如ID和HP）到指定的Graphics上下文中。
     * @param g 绘制的Graphics上下文。
     */
    public void draw(Graphics g) {
        // 根据牛仔的状态和血量绘制相应的图像
        if (status.equals("left")&&HP>0) {
            g.drawImage(imageLeft, x, y - 10, null);
        } else if (status.equals("right")&&HP>0) {
            g.drawImage(imageRight, x, y, null);
        } else if(HP>0){
            g.drawImage(imageRight, x, y, null);
        }

        // 如果牛仔有血量，则绘制ID和HP信息
        if(HP>0){
            // 设置字体和颜色
            g.setFont(new Font("Arial", Font.PLAIN, 12));
            g.setColor(Color.BLACK);

            // 计算文字的位置
            int textX = x + (width / 2) - 15; // 假设ID和HP的总宽度大约为30个像素
            int textY = y - 20; // 文字位于图片上方

            // 绘制ID
            g.drawString(ID, textX, textY);

            // 绘制HP
            textY += 15; // 下移一行
            g.drawString("HP: " + HP, textX, textY);
        }

    }

    /**
     * 根据指定的方向更新牛仔的移动状态。
     * @param movingDirection 牛仔的移动方向，包括"left"、"right"、"stop"和"dawn"。
     */
    public void setMoving(String movingDirection) {


        if (movingDirection.equals("left")) {
            if (x > 0) {
                x -= speed;
                movingLeft = true;
                movingRight = false;
                status="left";

            }
        } else if (movingDirection.equals("right")) {
            if (x < 885 - 60) {
                x += speed;
                movingLeft = false;
                movingRight = true;
                status="right";

            }
        } else if (movingDirection.equals("stop")) {
            movingRight = false;
            movingLeft = false;

        } else if (movingDirection.equals("dawn")) {
            y+=12;

        }

    }

    /**
     * 实现 Runnable 接口的 run 方法，用于牛仔的持续动作，如移动、跳跃和更新子弹状态。
     */
    @Override
    public void run() {
        while (true) {
            // 更新移动状态
            if (movingLeft && x > 0) {
                x -= speed;
            }
            if (movingRight && x < 885 - 60) {
                x += speed;
            }

            // 更新跳跃状态
            if (isJumping&&isMovingUp) {
                y -= jumpHeight;
                jumpTime -= 1; // 减少跳跃速度

                if (jumpTime <= 0) {
                    isJumping = false;
                    isMovingUp = false;
                    jumpTime = 7;
                }
            }

            // 更新子弹状态，移除已经消失的子弹
            Iterator<Bullet> iterator = bullets.iterator();

            while (iterator.hasNext()){
                Bullet bullet = iterator.next();
                bullet.move();
                if(!bullet.isLive()){
                    iterator.remove();
                }
            }

            try {
                Thread.sleep(10); // 控制更新频率
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }
    }

    /**
     * 让牛仔射击。
     * 射击行为是同步的，以避免同一时间多次射击导致的并发问题。
     */
    public synchronized void shoot(){
        if(!isShooting){
            synchronized (this){
                switch (status){
                    case "left":
                        bullets.add(new Bullet(x+12,y+35, Bullet.Direction.LEFT,backGround));
                        break;
                    case "right":
                        bullets.add(new Bullet(x+40,y+35, Bullet.Direction.RIGHT,backGround));
                        break;
                }

            }
        }

    }
    // 下面是一些用于获取和设置牛仔属性的方法，如坐标、尺寸、移动状态和血量等。

    public int getX() {
        return x;
    }
    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public boolean isMovingLeft() {
        return movingLeft;
    }

    public boolean isMovingRight() {
        return movingRight;
    }


    public boolean getIsMovingUp() {
        return isMovingUp;
    }

    public List<Bullet> getBullets() {
        return bullets;
    }

    public void setIsJumping(boolean isJumping) {
        this.isJumping = isJumping;
    }

    public void resetIsMovingUp() {
        isMovingUp = true;
    }

    public int getHP() {
        return HP;
    }

    public void setHP(int HP) {
        this.HP = HP;
    }

    public String getStatus() {
        return status;
    }

    public void setX(int x) {
        this.x = x;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getID() {
        return ID;
    }
}
