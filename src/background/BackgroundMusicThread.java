package background;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import java.io.File;

/**
 * 背景音乐播放线程类。
 * 用于在应用程序运行时播放背景音乐，并提供开始、停止和检查音乐播放状态的方法。
 */
public class BackgroundMusicThread extends Thread {
    private File musicFile; // 背景音乐文件
    private Clip clip; // 音乐片段
    private boolean isPlaying = false; // 播放状态标志

    /**
     * 构造函数，初始化背景音乐文件。
     * 通过资源路径获取音乐文件，并设置为线程的音乐文件。
     */
    public BackgroundMusicThread() {
        String resourcePath = "/music/bgmusic.wav";
        musicFile = new File(this.getClass().getResource(resourcePath).getFile());
    }

    /**
     * 线程运行方法。
     * 如果当前音乐播放状态为正在播放，则直接返回。
     * 否则，设置播放状态为正在播放，打开音频流并开始播放音乐。
     */
    @Override
    public void run() {
        try {
            if (isPlaying) {
                return;
            }
            isPlaying = true;
            AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(musicFile);
            clip = AudioSystem.getClip();
            clip.open(audioInputStream);
            clip.start();
        } catch (Exception e) {
            System.out.println("Error playing music: " + e.getMessage());
        }
    }

    /**
     * 停止音乐播放。
     * 如果音乐片段不为空且正在运行，则停止音乐播放，并将播放状态标志设置为false。
     */
    public void stopMusic() {
        if (clip != null && clip.isRunning()) {
            clip.stop();
            isPlaying = false;
        }
    }

    /**
     * 检查音乐是否正在播放。
     *
     * @return boolean 返回当前音乐的播放状态。
     */
    public boolean isPlaying() {
        return isPlaying;
    }
}
