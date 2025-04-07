package client.view.minigame.rhythm;

import java.awt.*;
import javax.swing.*;

public class GamePanel extends JPanel {
    private GameManager gameManager;
    private ImageManager imageManager;
    private static final int WIDTH = 600;
    private static final int HEIGHT = 500;
    private static final int LANE_COUNT = 4;
    
    public GamePanel(GameManager gameManager) {
        this.gameManager = gameManager;
        this.imageManager = new ImageManager();
        setBackground(Color.BLACK);
    }
    
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        drawGame(g);
    }
    
    private void drawGame(Graphics g) {
        // 배경 그리기
        g.setColor(Color.BLACK);
        g.fillRect(0, 0, WIDTH, HEIGHT);
        
        // 레인 그리기
        int laneWidth = WIDTH / LANE_COUNT;
        for (int i = 0; i < LANE_COUNT; i++) {
            g.setColor(Color.DARK_GRAY);
            g.fillRect(i * laneWidth, 0, laneWidth, HEIGHT);
            
            g.setColor(Color.GRAY);
            g.drawRect(i * laneWidth, 0, laneWidth, HEIGHT);
        }
        
        // 판정선 그리기
        g.setColor(Color.RED);
        g.drawLine(0, HEIGHT - 100, WIDTH, HEIGHT - 100);
        
        // 노트 그리기
        laneWidth = WIDTH / LANE_COUNT;
        for (Note note : gameManager.getNotes()) {
            if (!note.isHit && !note.isMissed) {
                g.setColor(Color.CYAN);
                g.fillRect(note.lane * laneWidth + 10, note.y, laneWidth - 20, 20);
            }
        }
        
        // 키 입력 효과 그리기
        boolean[] keyPressed = gameManager.getKeyPressed();
        for (int i = 0; i < LANE_COUNT; i++) {
            if (keyPressed[i]) {
                g.setColor(new Color(255, 255, 0, 150));
                g.fillRect(i * laneWidth, HEIGHT - 120, laneWidth, 40);
            }
        }
        
        // 판정 이펙트 그리기
        for (JudgementEffect effect : gameManager.getJudgementEffects()) {
            int x = effect.lane * laneWidth + laneWidth/2;
            int y = HEIGHT - 150;
            
            // 이미지가 있으면 이미지 사용, 없으면 텍스트로 대체
            Image effectImage = null;
            if (effect.judgement.equals("PERFECT")) {
                effectImage = imageManager.getPerfectImage();
                if (effectImage == null) {
                    g.setColor(Color.YELLOW);
                    g.setFont(new Font("Arial", Font.BOLD, 16));
                    g.drawString(effect.judgement, x - 30, y);
                } else {
                    g.drawImage(effectImage, x - 50, y - 20, 100, 40, null);
                }
            } else if (effect.judgement.equals("GOOD")) {
                effectImage = imageManager.getGoodImage();
                if (effectImage == null) {
                    g.setColor(Color.GREEN);
                    g.setFont(new Font("Arial", Font.BOLD, 16));
                    g.drawString(effect.judgement, x - 30, y);
                } else {
                    g.drawImage(effectImage, x - 50, y - 20, 100, 40, null);
                }
            } else if (effect.judgement.equals("MISS")) {
                effectImage = imageManager.getMissImage();
                if (effectImage == null) {
                    g.setColor(Color.RED);
                    g.setFont(new Font("Arial", Font.BOLD, 16));
                    g.drawString(effect.judgement, x - 30, y);
                } else {
                    g.drawImage(effectImage, x - 50, y - 20, 100, 40, null);
                }
            }
        }
    }
}
