package abalone;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.logging.Level;
import java.util.logging.Logger;
import javazoom.jl.decoder.JavaLayerException;
import javazoom.jl.player.AudioDevice;
import javazoom.jl.player.FactoryRegistry;
import javazoom.jl.player.Player;

/**
 *
 * @author JPEXS
 */
public class SoundPlayer {

    private static class PlayerThread extends Thread{
        private String file;

        public PlayerThread(String file) {
            this.file = file;
        }



        @Override
        public void run() {
            try {
            AudioDevice dev = getAudioDevice();
            Player player = new Player(SoundPlayer.class.getResourceAsStream(file), dev);
            player.play();
        } catch (JavaLayerException ex) {
        }
        }

    }

    private static AudioDevice getAudioDevice()
            throws JavaLayerException {
        return FactoryRegistry.systemRegistry().createAudioDevice();
    }

    private static InputStream getInputStream(String file) {
        FileInputStream fin = null;
        try {
            fin = new FileInputStream(file);
            BufferedInputStream bin = new BufferedInputStream(fin);
            return bin;
        } catch (FileNotFoundException ex) {
            Logger.getLogger(SoundPlayer.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public static void playSound(String file) {
        (new PlayerThread(file)).start();
    }
}
