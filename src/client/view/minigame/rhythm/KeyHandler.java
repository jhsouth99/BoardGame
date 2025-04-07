package client.view.minigame.rhythm;

import java.awt.event.*;

public class KeyHandler extends KeyAdapter {
    private GameManager gameManager;
    private RhythmGame gameFrame;
    private int[] keyMappings = {KeyEvent.VK_D, KeyEvent.VK_F, KeyEvent.VK_J, KeyEvent.VK_K};
    
    public KeyHandler(GameManager gameManager, RhythmGame gameFrame) {
        this.gameManager = gameManager;
        this.gameFrame = gameFrame;
        gameManager.setGameFrame(gameFrame);
    }
    
    @Override
    public void keyPressed(KeyEvent e) {
        for (int i = 0; i < keyMappings.length; i++) {
            if (e.getKeyCode() == keyMappings[i]) {
                gameManager.setKeyPressed(i, true);
            }
        }
        
        // ESC 키로 게임 종료
        if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
            gameManager.stopGame();
        }
    }
    
    @Override
    public void keyReleased(KeyEvent e) {
        for (int i = 0; i < keyMappings.length; i++) {
            if (e.getKeyCode() == keyMappings[i]) {
                gameManager.setKeyPressed(i, false);
            }
        }
    }
}
