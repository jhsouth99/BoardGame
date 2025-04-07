package client.view.minigame.snake;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.concurrent.CountDownLatch;

import javax.swing.JFrame;

public class SnakeMain {
	
	private int score = 0;
	private CountDownLatch latch;
	
	public SnakeMain(CountDownLatch latch) {
		this.latch = latch;
	}

	public void run() {

		JFrame frame = new JFrame("Snake Game");
//		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); //창이 닫힐 때 종료
		frame.setResizable(false); //창의 크기 고정
		
		GameBoard gameBoard = new GameBoard();
		frame.add(gameBoard);
		frame.pack(); //프레임 크기 자동 조정
		frame.setLocationRelativeTo(null); //화면 중앙에 배치

		frame.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				frame.dispose();
			}
			@Override
			public void windowClosed(WindowEvent e) {
				score = gameBoard.getScore();
				latch.countDown(); // latch 해제!
			}
		});
		frame.setVisible(true);
	}

	public static void main(String[] args) {
		
		JFrame frame = new JFrame("Snake Game");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); //창이 닫힐 때 종료
		frame.setResizable(false); //창의 크기 고정
		
		GameBoard gameBoard = new GameBoard();
		frame.add(gameBoard);
		frame.pack(); //프레임 크기 자동 조정
		frame.setLocationRelativeTo(null); //화면 중앙에 배치
		
		frame.setVisible(true);
		
	}//main

	public int getScore() {
		return score;
	}
	
}//SnakeMain
