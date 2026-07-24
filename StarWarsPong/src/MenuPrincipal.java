import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class MenuPrincipal extends JPanel implements KeyListener {
    private Image vaderImage;
    private Font starWarsFont;
    private GamePanel gamePanel;
    private JFrame parentFrame;
    
    public MenuPrincipal(JFrame frame) {
        this.parentFrame = frame;
        setBackground(Color.BLACK);
        setFocusable(true);
        addKeyListener(this);
        
        //imagen de Vader
        try {
            vaderImage = new ImageIcon(getClass().getResource("/imagenes/vader.png")).getImage();
        } catch (Exception e) {
            try {
                vaderImage = new ImageIcon(getClass().getResource("imagenes/vader.png")).getImage();
            } catch (Exception e2) {
                System.err.println("No se pudo cargar vader.png");
            }
        }
        
        starWarsFont = new Font("SansSerif", Font.BOLD, 48);
        
        // musica del menu en bucle
        sonido.playMusicaFondo("menu.wav");
    }
    
    private void startGame() {
        //musica del menu
        sonido.stopMusicaFondo();
        
        //pequeña pausa antes de cambiar
        try {
            Thread.sleep(300);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        
        //cambia al juego 
        gamePanel = new GamePanel();
        parentFrame.getContentPane().removeAll();
        parentFrame.add(gamePanel);
        parentFrame.revalidate();
        parentFrame.repaint();
        gamePanel.requestFocusInWindow();
    }
    
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        int width = getWidth();
        int height = getHeight();
        
        //Fondo negro con estrellas
        drawStarfield(g2, width, height);
        
        //Dibujar imagen de Vader en el lado izquierdo
        if (vaderImage != null) {
            int vaderWidth = Math.min(300, width / 3);
            int vaderHeight = (int) (vaderWidth * 1.2f); //Mantener proporcion
            int vaderX = width / 6 - vaderWidth / 2;
            int vaderY = height / 2 - vaderHeight / 2;
            g2.drawImage(vaderImage, vaderX, vaderY, vaderWidth, vaderHeight, null);
        }
        
        // Configurar fuente para el menu
        g2.setFont(starWarsFont.deriveFont((float) height / 12));
        FontMetrics fm = g2.getFontMetrics();
        
        //Titulo del juego
        String title = "PONG WARS";
        int titleX = 2 * width / 3 - fm.stringWidth(title) / 2;
        int titleY = height / 3;
        
        //Efecto de contorno dorado para el titulo
        drawStarWarsText(g2, title, titleX, titleY, true);
        
        //Opcion START
        g2.setFont(starWarsFont.deriveFont((float) height / 16));
        fm = g2.getFontMetrics();
        String startText = "PRESIONE ENTER PARA COMENZAR";
        int startX = 2 * width / 3 - fm.stringWidth(startText) / 2;
        int startY = height / 2 + height / 8;
        
        drawStarWarsText(g2, startText, startX, startY, true);
        
       
        
        //Controles
        String controls1 = "Jugador Izquierdo: W/S";
        String controls2 = "Jugador Derecho: ↑/↓";
        String controls3 = "ESC: Volver al menú";
        
        g2.setFont(new Font("SansSerif", Font.PLAIN, 14));
        FontMetrics ctrlFm = g2.getFontMetrics();
        
        int ctrlX = width / 2 - ctrlFm.stringWidth(controls1) / 2;
        g2.drawString(controls1, ctrlX, height - 100);
        
        ctrlX = width / 2 - ctrlFm.stringWidth(controls2) / 2;
        g2.drawString(controls2, ctrlX, height - 80);
        
        ctrlX = width / 2 - ctrlFm.stringWidth(controls3) / 2;
        g2.drawString(controls3, ctrlX, height - 60);
    }
    
    private void drawStarfield(Graphics2D g2, int width, int height) {
        //Dibujar algunas estrellas de fondo
        g2.setColor(Color.WHITE);
        for (int i = 0; i < 100; i++) {
            int x = (int) (Math.random() * width);
            int y = (int) (Math.random() * height);
            int size = (int) (Math.random() * 3) + 1;
            g2.fillOval(x, y, size, size);
        }
    }
    
    private void drawStarWarsText(Graphics2D g2, String text, int x, int y, boolean highlighted) {
        //Efecto de contorno amarillo/dorado
        for (int i = 3; i >= 1; i--) {
            if (highlighted) {
                g2.setColor(new Color(255, 215, 0, 100 - i * 20)); // Dorado con transparencia
            } else {
                g2.setColor(new Color(128, 128, 128, 100 - i * 20)); // Gris con transparencia
            }
            g2.drawString(text, x + i, y + i);
        }
        
        //Texto principal
        if (highlighted) {
            g2.setColor(Color.YELLOW);
        } else {
            g2.setColor(Color.WHITE);
        }
        g2.drawString(text, x, y);
        
        //Efecto de brillo interno
        if (highlighted) {
            g2.setColor(new Color(255, 255, 255, 150));
            g2.drawString(text, x, y);
        }
    }
    
    @Override
    public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_ENTER) {
            startGame();
        } else if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
            sonido.stopMusicaFondo();
            System.exit(0);
        }
    }
    
    @Override
    public void keyTyped(KeyEvent e) {}
    
    @Override
    public void keyReleased(KeyEvent e) {}
}