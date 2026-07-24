import javax.swing.*;
import java.awt.*;

public class StarWarsPong {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Pong Wars - Star Wars Edition");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(1200, 800);
            frame.setLocationRelativeTo(null);
            frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
            
            MenuPrincipal menu = new MenuPrincipal(frame);
            frame.add(menu);
            frame.setVisible(true);
            menu.requestFocusInWindow();
        });
    }
}