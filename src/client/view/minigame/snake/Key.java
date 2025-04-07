package client.view.minigame.snake;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

public class Key extends KeyAdapter {
	
    private GameManager gameManager;

    public Key(GameManager gameManager) {
        this.gameManager = gameManager;
    }//생성자

    @Override
    public void keyPressed(KeyEvent e) {
        switch (e.getKeyCode()) {
            case KeyEvent.VK_LEFT:
                if (gameManager.getSnake().getDirection() != 'R') {
                    gameManager.getSnake().setDirection('L');
                }
                break;
            case KeyEvent.VK_RIGHT:
                if (gameManager.getSnake().getDirection() != 'L') {
                    gameManager.getSnake().setDirection('R');
                }
                break;
            case KeyEvent.VK_UP:
                if (gameManager.getSnake().getDirection() != 'D') {
                    gameManager.getSnake().setDirection('U');
                }
                break;
            case KeyEvent.VK_DOWN:
                if (gameManager.getSnake().getDirection() != 'U') {
                    gameManager.getSnake().setDirection('D');
                }
                break;
        }//switch
    }//keyPressed
}//Key
