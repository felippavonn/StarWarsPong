import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import javax.sound.sampled.*;

public class GamePanel extends JPanel implements Runnable, KeyListener {
    private int width, height;
    private boolean isPaused = true;
    private Timer pauseTimer;
    private Paddle leftPaddle, rightPaddle;
    private Ball ball;
    private int scoreLeft = 0, scoreRight = 0;
    private BufferedImage buffer;
    private List<Star> stars = new ArrayList<>();
    private final int NUM_STARS = 200;
    private Random rand = new Random();
    private Thread gameThread;
    private volatile boolean running = false;
    private static final Font FONT_SCORE = new Font("SansSerif", Font.BOLD, 96);
    

    public GamePanel() {
        setBackground(Color.BLACK);
        setFocusable(true);
        addKeyListener(this);
        setDoubleBuffered(true);
        setPreferredSize(new Dimension(800, 600));
        
        //  musica de cantina
        sonido.playMusicaFondo("cantina.wav");

        addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                if (getWidth() > 0 && getHeight() > 0) {
                    if (buffer == null) {
                        iniciarJuego();
                    } else if (getWidth() != width || getHeight() != height) {
                        width = Math.max(800, getWidth());
                        height = Math.max(600, getHeight());
                        buffer = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
                        
                        if (leftPaddle != null) {
                            leftPaddle.screenH = height;
                            rightPaddle.screenH = height;
                        }
                        if (ball != null) {
                            ball.setBounds(width, height);
                        }
                        initStars(); 
                    }
                }
            }
        });
    }
    
    private void resetPaddles() {
        int paddleHLeft = leftPaddle.h;
        int paddleHRight = rightPaddle.h;
        leftPaddle.y = height / 2 - paddleHLeft / 2;
        rightPaddle.y = height / 2 - paddleHRight / 2;
    }

    private void startPause() {
        isPaused = true;
        resetPaddles();  

        if (pauseTimer != null && pauseTimer.isRunning()) {
            pauseTimer.stop();
        }
        pauseTimer = new Timer(1500, e -> {
            isPaused = false;
            ball.reset(rand.nextBoolean() ? 1 : -1);
            pauseTimer.stop();
        });
        pauseTimer.setRepeats(false);
        pauseTimer.start();
    }

    private void iniciarJuego() {
        width = Math.max(800, getWidth());
        height = Math.max(600, getHeight());
        buffer = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        initGameObjects();
        initStars();
        startPause();
        if (gameThread == null) {
            running = true;
            gameThread = new Thread(this, "GameLoop");
            gameThread.start();
        }
    }

    private void initStars() {
        stars.clear();
        for (int i = 0; i < NUM_STARS; i++) {
            stars.add(new Star(rand.nextInt(width), rand.nextInt(height), 1 + rand.nextFloat() * 3));
        }
    }

    private void initGameObjects() {
        int paddleW = Math.max(12, width / 80);
        int paddleH = Math.max(80, height / 6);
        leftPaddle = new Paddle(30, height / 2 - paddleH / 2, paddleW, paddleH, height, new Color(180, 240, 255)); // azul
        rightPaddle = new Paddle(width - 30 - paddleW, height / 2 - paddleH / 2, paddleW, paddleH, height, Color.RED); // rojo

        ball = new Ball(width / 2, height / 2, Math.max(12, width / 120));
        ball.setBounds(width, height);
        ball.reset(rand.nextBoolean() ? 1 : -1);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (buffer == null) return;

        Graphics2D g2 = buffer.createGraphics();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        g2.setColor(Color.BLACK);
        g2.fillRect(0, 0, width, height);

        for (Star s : stars) {
            float brightness = Math.min(1f, s.speed / 3f + 0.2f);
            int alpha = (int) (brightness * 255);
            g2.setColor(new Color(255, 255, 200, alpha));
            int size = 1 + (int) (s.speed);
            g2.fillOval((int) s.x, (int) s.y, size, size);
        }

        leftPaddle.draw(g2);
        rightPaddle.draw(g2);
        ball.draw(g2);

        Stroke oldStroke = g2.getStroke();
        g2.setStroke(new BasicStroke(4f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 0, new float[]{20f, 20f}, 0));
        g2.setColor(new Color(60, 180, 255, 80));
        g2.drawLine(width / 2, 0, width / 2, height);
        g2.setStroke(oldStroke);

        String left = String.valueOf(scoreLeft);
        String right = String.valueOf(scoreRight);
        g2.setFont(FONT_SCORE.deriveFont((float) height / 8));
        FontMetrics fm = g2.getFontMetrics();
        int lx = width / 4 - fm.stringWidth(left) / 2;
        int rx = 3 * width / 4 - fm.stringWidth(right) / 2;
        int sy = fm.getAscent() + 20;

        for (int i = 8; i > 0; i--) {
            int alpha = Math.max(8, 40 - i * 4);
            g2.setColor(new Color(255, 220, 80, alpha));
            g2.drawString(left, lx + i, sy + i);
            g2.drawString(right, rx + i, sy + i);
        }
        g2.setColor(new Color(255, 240, 150));
        g2.drawString(left, lx, sy);
        g2.drawString(right, rx, sy);

        g2.dispose();
        g.drawImage(buffer, 0, 0, null);
    }

    @Override
    public void run() {
        long lastTime = System.nanoTime();
        double nsPerFrame = 1000000000.0 / 60.0;
        double delta = 0;
        while (running) {
            long now = System.nanoTime();
            delta += (now - lastTime) / nsPerFrame;
            lastTime = now;
            boolean shouldRender = false;
            while (delta >= 1) {
                updateGame();
                delta -= 1;
                shouldRender = true;
            }
            if (shouldRender) repaint();
            try { Thread.sleep(2); } catch (InterruptedException e) { Thread.currentThread().interrupt(); }
        }
    }

    private void updateGame() {
        //  actualizar estrellas y paddles para que sigan moviendose
        for (Star s : stars) {
            s.x += s.speed;
            if (s.x > width) {
                s.x = -5;
                s.y = rand.nextInt(height);
                s.speed = 1 + rand.nextFloat() * 3;
            }
        }

        leftPaddle.update();
        rightPaddle.update();

        if (!isPaused) {
            ball.update();

            // Verifica colisiones con el paddle izquierdo
            if (ball.intersects(leftPaddle)) {
                // Verifica primero si golpea el mango
                if (leftPaddle.intersectsWithMango(ball)) {
                    sonido.play("mango.wav");
                }
                // Si no es el mango, entonces es el sable
                else if (leftPaddle.intersectsWithSable(ball)) {
                    sonido.play("sable.wav");
                }
                ball.bounceOffPaddle(leftPaddle);
            }
            // Verifica colisiones con el paddle derecho
            else if (ball.intersects(rightPaddle)) {
                // Verifica primero si golpea el mango
                if (rightPaddle.intersectsWithMango(ball)) {
                    sonido.play("mango.wav");
                }
                // Si no es el mango, entonces es el sable
                else if (rightPaddle.intersectsWithSable(ball)) {
                    sonido.play("sable.wav");
                }
                ball.bounceOffPaddle(rightPaddle);
            }

            // Rebote en bordes superior e inferior
            if (ball.y - ball.radius < 0) ball.vy = Math.abs(ball.vy);
            else if (ball.y + ball.radius > height) ball.vy = -Math.abs(ball.vy);

            // Puntuacion
            if (ball.x < -50) {
                scoreRight++;
                startPause();
            } else if (ball.x > width + 50) {
                scoreLeft++;
                startPause();
            }
        }
    }

    @Override public void keyTyped(KeyEvent e) {}
    @Override public void keyPressed(KeyEvent e) {
        int k = e.getKeyCode();
        if (k == KeyEvent.VK_W) leftPaddle.setUp(true);
        if (k == KeyEvent.VK_S) leftPaddle.setDown(true);
        if (k == KeyEvent.VK_UP) rightPaddle.setUp(true);
        if (k == KeyEvent.VK_DOWN) rightPaddle.setDown(true);
        if (k == KeyEvent.VK_ESCAPE) {
            sonido.stopMusicaFondo();
            running = false;
            Window w = SwingUtilities.getWindowAncestor(this);
            if (w instanceof JFrame) {
                JFrame frame = (JFrame) w;
                MenuPrincipal menu = new MenuPrincipal(frame);
                frame.getContentPane().removeAll();
                frame.add(menu);
                frame.revalidate();
                frame.repaint();
                menu.requestFocusInWindow();
            }
        }
    }
    @Override public void keyReleased(KeyEvent e) {
        int k = e.getKeyCode();
        if (k == KeyEvent.VK_W) leftPaddle.setUp(false);
        if (k == KeyEvent.VK_S) leftPaddle.setDown(false);
        if (k == KeyEvent.VK_UP) rightPaddle.setUp(false);
        if (k == KeyEvent.VK_DOWN) rightPaddle.setDown(false);
    }
}