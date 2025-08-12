import javax.swing.*;
import java.awt.*;

public class StarWarsPong extends JFrame {
    public static void main(String[] args) {
        EventQueue.invokeLater(() -> {
            StarWarsPong frame = new StarWarsPong();
            frame.setVisible(true);
        });
    }

    private GamePanel gamePanel;

    public StarWarsPong() {
        setTitle("Star Wars Pong");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setUndecorated(true);
        setBackground(Color.BLACK);

        gamePanel = new GamePanel();
        setContentPane(gamePanel);
        pack();

        GraphicsDevice gd = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();
        if (gd.isFullScreenSupported()) {
            gd.setFullScreenWindow(this);
        } else {
            setExtendedState(JFrame.MAXIMIZED_BOTH);
            setVisible(true);
        }

        gamePanel.requestFocusInWindow();
    }
}