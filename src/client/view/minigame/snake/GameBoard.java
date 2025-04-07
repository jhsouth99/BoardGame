package client.view.minigame.snake;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Image;
import java.util.Random;

import javax.swing.ImageIcon;
import javax.swing.JPanel;

public class GameBoard extends JPanel { //GUI
	
    static final int WIDTH = 500;
    static final int HEIGHT = 500;
    static final int UNIT_SIZE = 25;
    static final int GAME_UNITS = (WIDTH * HEIGHT) / (UNIT_SIZE * UNIT_SIZE);
    
    private GameManager gameManager;
    private int foodX;
    private int foodY;
    private Random random;
    
    public GameBoard() {
        random = new Random();
        this.setPreferredSize(new Dimension(WIDTH, HEIGHT));
        this.setBackground(Color.BLACK);
        this.setFocusable(true);
        gameManager = new GameManager(this);
        this.addKeyListener(new Key(gameManager));
        
        gameManager.startGame();
    }//GameBoard
    
    public void newFood() {
        foodX = random.nextInt((WIDTH / UNIT_SIZE)) * UNIT_SIZE;
        foodY = random.nextInt((HEIGHT / UNIT_SIZE)) * UNIT_SIZE;
    }//newFood
    
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        
        Image backgroundImage = new ImageIcon("images/snake/moon.png").getImage();
        g.drawImage(backgroundImage, 0, 0, WIDTH, HEIGHT, this);
        
        draw(g);
    }//paintComponent
    
    private void draw(Graphics g) {
        if (gameManager.isRunning()) {
            g.setColor(Color.RED);
            g.fillOval(foodX, foodY, UNIT_SIZE, UNIT_SIZE);
            gameManager.getSnake().draw(g, UNIT_SIZE);
            g.setColor(Color.WHITE);
            g.setFont(new Font("", Font.BOLD, 20));
            g.drawString("점수 : " + gameManager.getScore() + "점", 10, 30);
            g.drawString("남은 시간 : " + gameManager.getTimeLeft() + "초", WIDTH - 160, 30);
        } else {
            gameOverScreen(g);
        }
    }//draw
    
    private void gameOverScreen(Graphics g) {
        g.setColor(Color.GREEN);
        g.setFont(new Font("", Font.BOLD, 50));
        FontMetrics metrics = getFontMetrics(g.getFont());
        String message = "게임 종료";
        g.drawString(message, (WIDTH - metrics.stringWidth(message)) / 2, HEIGHT / 2);
        g.setFont(new Font("", Font.BOLD, 30));
        metrics = getFontMetrics(g.getFont());
        String scoreText = "점수 : " + gameManager.getScore() + "점";
        g.drawString(scoreText, (WIDTH - metrics.stringWidth(scoreText)) / 2, HEIGHT / 2 + 50);
    }//gameOverScreen
    
    public int getFoodX() { 
		return foodX; 
	}
	public int getFoodY() { 
		return foodY; 
	}
	
	public int getScore() {
		return gameManager.getScore();
	}
	
}//GameBoard

