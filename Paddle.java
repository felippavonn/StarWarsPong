import java.awt.*;

public class Paddle {
    float x, y;
    int w, h;
    float speed = 6f;
    boolean up = false, down = false;
    int screenH;
    Color coreColor;

    public Paddle(int x, int y, int w, int h, int screenH, Color coreColor) {
        this.x = x; this.y = y; this.w = w; this.h = h;
        this.screenH = screenH; this.coreColor = coreColor;
    }

    public void setUp(boolean v) { up = v; }
    public void setDown(boolean v) { down = v; }

    public void update() {
        if (up) y -= speed;
        if (down) y += speed;
        if (y < 0) y = 0;
        if (y + h > screenH) y = screenH - h;
    }

    public Rectangle getBounds() { return new Rectangle((int)x, (int)y, w, h); }

    public void draw(Graphics2D g2) {
        for (int i = 6; i > 0; i--) {
            int alpha = 20 + i * 20;
            g2.setColor(new Color(coreColor.getRed(), coreColor.getGreen(), coreColor.getBlue(), Math.min(200, alpha)));
            g2.fillRoundRect((int)x - (i * 2), (int)y - (i * 2), w + (i * 4), h + (i * 4), w, w);
        }
        g2.setColor(coreColor);
        g2.fillRoundRect((int)x, (int)y, w, h, w, w);
        g2.setColor(new Color(50, 50, 50, 200));
        g2.fillRect((int)x - (w / 2), (int)y + h - (h / 6), w + w, h / 6);
    }
}