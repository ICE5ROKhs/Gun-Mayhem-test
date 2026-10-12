package model;

import java.awt.*;

public class Platform {
    private Rectangle bounds; // 平台的边界

    public Platform(int x, int y, int width, int height) {
        bounds = new Rectangle(x, y, width, height);
    }

    public Rectangle getBounds() {
        return bounds;
    }//获取碰撞体积

    public void draw(Graphics g) {
        g.setColor(new Color(0, 0, 0,0));
        g.fillRect(bounds.x, bounds.y, bounds.width, bounds.height);
    }
}