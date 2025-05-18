package view.game;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;

public class MusicPlayer {
    private static MusicPlayer instance;  // 单例实例

    private Clip clip;
    private boolean isMuted = false;

    private MusicPlayer() {} // 私有构造方法

    public static MusicPlayer getInstance() {
        if (instance == null) {
            instance = new MusicPlayer();
        }
        return instance;
    }

    public void play(String path) {
        try {
            // 关闭已有音频
            if (clip != null && clip.isRunning()) {
                clip.stop();
                clip.close();
            }

            AudioInputStream audioIn = AudioSystem.getAudioInputStream(getClass().getResource(path));
            clip = AudioSystem.getClip();
            clip.open(audioIn);
            clip.loop(Clip.LOOP_CONTINUOUSLY); // 循环播放

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
