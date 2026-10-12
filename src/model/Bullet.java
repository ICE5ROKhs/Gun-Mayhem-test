package model;

import java.awt.*;

import background.BackGround;

public class Bullet {
    private int x; // 子弹的X坐标
    private int y; // 子弹的Y坐标
    private int width; // 子弹的宽度
    private int height; // 子弹的高度
    private int speed; // 子弹的移动速度
    private boolean isLive; // 子弹是否有效
    private Direction direction; // 子弹飞行方向

    private Rectangle bounds;//子弹的碰撞体积

    private BackGround backGround;//背景

    // 枚举类型，定义子弹的飞行方向
    public enum Direction {//方向
        LEFT, RIGHT
    }

    public Bullet(int startX, int startY, Direction dir, BackGround backGround) {
        this.x = startX;
        this.y = startY;
        this.width = 20; // 假设子弹宽度为20像素
        this.height = 5; // 假设子弹高度为5像素
        this.speed = 5; // 子弹每帧移动5像素
        this.isLive = true;
        this.direction = dir;//方向

        this.backGround = backGround;
        bounds = new Rectangle(x, y, width, height);
    }

    // 移动子弹
    public void move() {
        if (direction == Direction.LEFT) {
            x -= speed;
            bounds.setLocation(x, y);
        } else if (direction == Direction.RIGHT) {
            x += speed;
            bounds.setLocation(x, y);
        }

        // 检查子弹是否超出边界
        if (x < 0 || x > 885) {
            isLive = false;
        }

        if (backGround != null && backGround != null) {

            if ((x < backGround.getPlayer2().getX() && backGround.getPlayer2().getX() - x < 20) || (x > backGround.getPlayer2().getX() && x - backGround.getPlayer2().getX() < 20)) {
                isLive = false;
                backGround.getOut().println("6666"+"_"+backGround.getPlayer2().getID()+"_"+backGround.getPlayer2().getHP());
            }
            if ((x < backGround.getPlayer3().getX() && backGround.getPlayer3().getX() - x < 20) || (x > backGround.getPlayer3().getX() && x - backGround.getPlayer3().getX() < 20)) {
                isLive = false;
                backGround.getOut().println("6666"+"_"+backGround.getPlayer3().getID()+"_"+backGround.getPlayer3().getHP());
            }
        }

    }

    public Rectangle getBounds() {
        return bounds;
    }

    // 绘制子弹
    public void draw(Graphics g) {
        if (isLive) {
            g.setColor(Color.white);
            g.fillRect(x, y, width, height);
        }
    }

    // 获取子弹是否有效
    public boolean isLive() {
        return isLive;
    }

    public int getY() {
        return y;
    }

    public boolean getIsLive() {
        return isLive;
    }

    public void setLive(boolean live) {
        isLive = live;
    }

    public int getX() {
        return x;
    }
    // 其他可能需要的方法，如获取子弹位置、设置子弹状态等
}
