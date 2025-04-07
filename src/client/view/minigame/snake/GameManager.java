package client.view.minigame.snake;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.Timer;

public class GameManager implements ActionListener {
	//게임 로직 관리, 뱀 제어, 뱀 먹이, 타이머, 충돌감지, 점수 관리, 게임종료 처리 등
	
	static final int DELAY = 100; //100ms
	static final int MAX_SCORE = 64; //최대점수
	static final int TIME_LIMIT = 20; // 20초 타임어택

	private Snake snake;
	private GameBoard gameBoard;
	private Timer gameTimer;
	private Timer countdownTimer; //카운트다운
	private int timeLeft; //게임 종료까지 남은 시간(초)
	private int score; //점수
	private boolean running; //게임 진행 상태

	public GameManager(GameBoard board) {
		this.gameBoard = board;
		this.snake = new Snake(GameBoard.GAME_UNITS);
	}//생성자

	public void startGame() { //초기화
		score = 0; //점수 : 0점
		timeLeft = TIME_LIMIT; //남은 시간 : 20초
		gameBoard.newFood(); //뱀 먹이
		running = true;

		gameTimer = new Timer(DELAY, this);
		gameTimer.start();

		countdownTimer = new Timer(1000, new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				timeLeft--;
				if (timeLeft <= 0) {
					gameOver();
				}
				gameBoard.repaint();
			}
		}); 
		countdownTimer.start();
	}//startGame

	public void checkFood() {
		if (snake.getX()[0] == gameBoard.getFoodX() && snake.getY()[0] == gameBoard.getFoodY()) {
			snake.grow();
			score += 8;
			if (score >= MAX_SCORE) {
				gameOver();
			} else {
				gameBoard.newFood();
			}
		}
	}//checkFood

	public void checkCollisions() {
		for (int i = snake.getBodyParts(); i > 0; i--) {
			if (snake.getX()[0] == snake.getX()[i] && snake.getY()[0] == snake.getY()[i]) {
				running = false;
			}
		}
		if (snake.getX()[0] < 0 || snake.getX()[0] >= GameBoard.WIDTH ||
				snake.getY()[0] < 0 || snake.getY()[0] >= GameBoard.HEIGHT) {
			running = false;
		}
		if (!running) {
			gameOver();
		}
	}//checkCollisions

	public void gameOver() {
		running = false;
		gameTimer.stop();
		countdownTimer.stop();
		gameBoard.repaint();
	}//gameOver

	@Override
	public void actionPerformed(ActionEvent e) {
		if (running) {
			snake.move();
			checkFood();
			checkCollisions();
		}//if
		gameBoard.repaint();
	}//actionPerformed

	
	
	public Snake getSnake() {
		return snake;
	}
	public int getTimeLeft() {
		return timeLeft;
	}
	public int getScore() {
		return score;
	}
	public boolean isRunning() {
		return running;
	}
	
}//GameManager
