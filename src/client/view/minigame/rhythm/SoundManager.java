package client.view.minigame.rhythm;

import javax.sound.sampled.*;
import java.io.*;

public class SoundManager {
    private Clip hitSound;
    private Clip missSound;
    
    public SoundManager() {
        loadSounds();
    }
    
    private void loadSounds() {
        try {
            // 실제 프로젝트에서는 실제 파일 경로를 사용해야 합니다
            File hitFile = new File("hit.wav");
            AudioInputStream hitStream = AudioSystem.getAudioInputStream(hitFile);
            hitSound = AudioSystem.getClip();
            hitSound.open(hitStream);
            
            File missFile = new File("miss.wav");
            AudioInputStream missStream = AudioSystem.getAudioInputStream(missFile);
            missSound = AudioSystem.getClip();
            missSound.open(missStream);
        } catch (Exception e) {
            // 사운드 파일이 없거나 오류가 발생하면 조용히 넘어갑니다
            System.out.println("사운드 로딩 오류: " + e.getMessage());
        }
    }
    
    public void playHitSound() {
        if (hitSound != null) {
            hitSound.setFramePosition(0);
            hitSound.start();
        }
    }
    
    public void playMissSound() {
        if (missSound != null) {
            missSound.setFramePosition(0);
            missSound.start();
        }
    }
}
