package view.game;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;

public class MusicPlayer {
    private static MusicPlayer instance;
    private String currentPath = null;
    private Clip clip;
    private boolean isMuted = false;

    private MusicPlayer() {}

    public static MusicPlayer getInstance() {
        if (instance == null) {
            instance = new MusicPlayer();
        }
        return instance;
    }

    public void play(String path) {
        if (clip != null && clip.isRunning() && path.equals(currentPath)) {
            return;
        }

        try {
            //关闭
            if (clip != null && clip.isRunning()) {
                clip.stop();
                clip.close();
            }

            AudioInputStream audioIn = AudioSystem.getAudioInputStream(getClass().getResource(path));
            clip = AudioSystem.getClip();
            clip.open(audioIn);
            clip.loop(Clip.LOOP_CONTINUOUSLY);
            currentPath = path;

            if (isMuted) {
                clip.stop();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void toggleMute() {
        isMuted = !isMuted;
        if (clip != null) {
            if (isMuted) {
                clip.stop();
            } else {
                clip.start();
            }
        }
    }

    public boolean isMuted() {
        return isMuted;
    }
}
