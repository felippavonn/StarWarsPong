import java.awt.*;
import javax.swing.ImageIcon;

public class Paddle {
    float x, y;
    int w, h;
    float speed = 6f;
    boolean up = false, down = false;
    int screenH;
    Color coreColor;

    private Image imagen;

    public Paddle(int x, int y, int w, int h, int screenH, Color coreColor) {
        this.x = x;
        this.y = y;
        this.w = w;
        this.h = h + 100;
        this.screenH = screenH;
        this.coreColor = coreColor;

        imagen = new ImageIcon(getClass().getResource("/imagenes/mango_sable.png")).getImage();
    }

    public void setUp(boolean v) { up = v; }
    public void setDown(boolean v) { down = v; }

    public void update() {
        if (up) y -= speed;
        if (down) y += speed;
        if (y < 0) y = 0;
        if (y + h > screenH) y = screenH - h;
    }

    public Rectangle getBounds() {
        return new Rectangle((int)x, (int)y, w , h);
    }
    
    //obtiene los bordes del area del sable (sin el mango)
    public Rectangle getSableBounds() {
        return new Rectangle((int)x, (int)y, w, h - (h / 2));
    }
    
    //obtiene los bordes del area del mango mi querido papu que esta leyendo esto XD
    public Rectangle getMangoBounds() {
        int recuadroX = (int)x - (w / 2);
        int recuadroY = (int)y + h - (h / 14);
        int recuadroW = w + w;
        int recuadroH = h / 2;
        return new Rectangle(recuadroX - 65, recuadroY - 122, recuadroH + 10, recuadroH + 10);
    }
    
    //verifica si la colision es especificamente con el mango
    public boolean intersectsWithMango(Ball ball) {
        Rectangle ballBounds = new Rectangle((int)(ball.x - ball.radius), (int)(ball.y - ball.radius), 
                                           ball.radius * 2, ball.radius * 2);
        return getMangoBounds().intersects(ballBounds);
    }
    
    //verifica  si la colision es específicamente con el sable
    public boolean intersectsWithSable(Ball ball) {
        Rectangle ballBounds = new Rectangle((int)(ball.x - ball.radius), (int)(ball.y - ball.radius), 
                                           ball.radius * 2, ball.radius * 2);
        return getSableBounds().intersects(ballBounds) && !intersectsWithMango(ball);
    }

    public void draw(Graphics2D g2) {
        
        for (int i = 6; i > 0; i--) {
            int alpha = 20 + i * 20;
            g2.setColor(new Color(coreColor.getRed(), coreColor.getGreen(), coreColor.getBlue(), Math.min(200, alpha)));
        }

        
        g2.setColor(coreColor);
        g2.fillRoundRect((int)x, (int)y, w, h, w, w);

        
        g2.setColor(new Color(0, 0, 0, 0));
        int recuadroX = (int)x - (w / 2);
        int recuadroY = (int)y + h - (h / 14);
        int recuadroW = w + w;
        int recuadroH = h / 2;
        g2.fillRect(recuadroX, recuadroY, recuadroW, recuadroH);

        
        if (imagen != null) {
            g2.drawImage(imagen, recuadroX - 55, recuadroY - 108, recuadroH + 25, recuadroH + 10, null);
        }
    }
}