import java.awt.*;
import java.util.Random;

public class Ball {
    float x, y; 
    public int radius;
    float vx, vy;
    float speedBase;
    private Random rand = new Random();
    private int width, height;

    public Ball(int x, int y, int radius) {
        this.x = x; this.y = y; this.radius = radius;
        this.speedBase = Math.max(5, radius / 2f + 4);
    }

  
    public void setBounds(int width, int height) {
        this.width = width;
        this.height = height;
    }

    public void reset(int direction) {
        this.x = width / 2f;
        this.y = height / 2f;
        float angle = (float)((rand.nextFloat() * Math.PI / 3.0) - Math.PI / 6.0);
        float initialSpeed = speedBase * 0.8f;  
        vx = (float)(direction * (initialSpeed + rand.nextFloat() * 1)); 
        vy = (float)(Math.sin(angle) * (initialSpeed));
    }

    public void update() { x += vx; y += vy; }

    public void draw(Graphics2D g2) {
        for (int i = 6; i > 0; i--) {
            int alpha = 30 + i * 30;
            g2.setColor(new Color(255, 220, 80, Math.min(200, alpha)));
            g2.fillOval((int)(x - radius - i), (int)(y - radius - i), radius * 2 + i * 2, radius * 2 + i * 2);
        }
        g2.setColor(new Color(255, 245, 180));
        g2.fillOval((int)(x - radius), (int)(y - radius), radius * 2, radius * 2);
    }

    public boolean intersects(Paddle p) {
        return new Rectangle((int)(x - radius), (int)(y - radius), radius * 2, radius * 2).intersects(p.getBounds());
    }

    public void bounceOffPaddle(Paddle p) {
        float relative = (y - (p.y + p.h / 2f)) / (p.h / 2f);
        float bounceAngle = relative * (float)(Math.PI / 3);
        int dir = (p.x < width / 2f) ? 1 : -1;
        float newSpeed = (float)(Math.hypot(vx, vy) * 1.05);
        newSpeed = Math.min(newSpeed, 18f);
        vx = (float)(dir * Math.cos(bounceAngle) * newSpeed);
        vy = (float)(Math.sin(bounceAngle) * newSpeed);
        if (dir > 0) x = p.x + p.w + radius + 2;
        else x = p.x - radius - 2;
    }
}