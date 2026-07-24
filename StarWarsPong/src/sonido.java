import javax.sound.sampled.*;
import java.io.InputStream;
import java.io.BufferedInputStream;

public class sonido {
    private static Clip musicaFondo;
    
    public static void play(String filename) {
        new Thread(() -> {
            try {
                InputStream audioSrc = sonido.class.getResourceAsStream("/sonido/" + filename);
                if (audioSrc == null) {
                    audioSrc = sonido.class.getResourceAsStream("sonido/" + filename);
                }
                if (audioSrc == null) {
                    System.err.println("No se encontro: " + filename);
                    return;
                }
                
                BufferedInputStream bufferedIn = new BufferedInputStream(audioSrc);
                AudioInputStream audioStream = AudioSystem.getAudioInputStream(bufferedIn);
                Clip clip = AudioSystem.getClip();
                clip.open(audioStream);
                clip.start();
                
                clip.addLineListener(event -> {
                    if (event.getType() == LineEvent.Type.STOP) {
                        clip.close();
                    }
                });
                
            } catch (Exception e) {
                System.err.println("Error con audio: " + filename);
            }
        }).start();
    }
    
    public static void playMusicaFondo(String filename) {
        new Thread(() -> {
            try {
                stopMusicaFondo();
                
                InputStream audioSrc = sonido.class.getResourceAsStream("/sonido/" + filename);
                if (audioSrc == null) {
                    audioSrc = sonido.class.getResourceAsStream("sonido/" + filename);
                }
                if (audioSrc == null) {
                    System.err.println("No se encontro musica: " + filename);
                    return;
                }
                
                BufferedInputStream bufferedIn = new BufferedInputStream(audioSrc);
                AudioInputStream audioStream = AudioSystem.getAudioInputStream(bufferedIn);
                musicaFondo = AudioSystem.getClip();
                musicaFondo.open(audioStream);
                musicaFondo.loop(Clip.LOOP_CONTINUOUSLY);
                
            } catch (Exception e) {
                System.err.println("Error con musica: " + filename);
            }
        }).start();
    }
    
    public static void stopMusicaFondo() {
        if (musicaFondo != null && musicaFondo.isRunning()) {
            musicaFondo.stop();
            musicaFondo.close();
            musicaFondo = null;
        }
    }
}