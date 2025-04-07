package client.view.minigame.memory;

import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.concurrent.CountDownLatch;

import javax.swing.JLabel;
import javax.swing.SwingConstants;
import javax.swing.Timer;

public class MatchCards {
	
	private int score;
	private CountDownLatch latch;

	public MatchCards(CountDownLatch latch) {
		this.latch = latch;
	}
	public void run() {

		Frame matchCardsFrame = new Frame("MatchCards");
		matchCardsFrame.setSize(640, 620);
		matchCardsFrame.setLocationRelativeTo(null);
		matchCardsFrame.setResizable(false);
		
		JLabel statusLabel = new JLabel("오답 :  0  (3번 초과시 종료)", SwingConstants.CENTER);
		statusLabel.setFont(new Font("", Font.BOLD, 20));
		matchCardsFrame.add(statusLabel, BorderLayout.NORTH);
		
		String[] cardNames = { "Coke", "Dolphin", "Palm", "Pepsi", "Sprite", "Sunset", 
							   "Coke", "Dolphin", "Palm", "Pepsi", "Sprite", "Sunset" };
		
		GameBoard board = new GameBoard(cardNames);
		GameLogic logic = new GameLogic(statusLabel, matchCardsFrame);
		
		// board에서 섞인 카드를 버튼에 추가
		for (Card card : board.getCards()) {
			card.getBtn().addActionListener(e -> logic.selectCard(card));
		}
		
		// 게임판 추가
		matchCardsFrame.add(board.getPanel(), BorderLayout.CENTER);
		
		matchCardsFrame.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				matchCardsFrame.dispose();
			};
			@Override
			public void windowClosed(WindowEvent e) {
				score = logic.calculateScore();
				latch.countDown(); // latch 해제!
			};
		});

		matchCardsFrame.setVisible(true);
		
		// 모든 카드의 앞면을 보여줌.
		board.flipAllToFront();

		// 2초 후 모든 카드의 뒷면을 보여줌.
		Timer timer = new Timer(2000, new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				board.flipAllToBack();
				// 카드가 클릭될 수 있게함
				logic.enableClick();
			}
		});
		timer.setRepeats(false);
		timer.start();
	}
	
	public int getScore() {
		return score;
	}
	
	public static void main(String[] args) {
		
		Frame matchCardsFrame = new Frame("MatchCards");
		matchCardsFrame.setSize(640, 620);
		matchCardsFrame.setLocationRelativeTo(null);
		matchCardsFrame.setResizable(false);
		
		JLabel statusLabel = new JLabel("오답 :  0  (3번 초과시 종료)", SwingConstants.CENTER);
		statusLabel.setFont(new Font("", Font.BOLD, 20));
		matchCardsFrame.add(statusLabel, BorderLayout.NORTH);
		
		String[] cardNames = { "Coke", "Dolphin", "Palm", "Pepsi", "Sprite", "Sunset", 
							   "Coke", "Dolphin", "Palm", "Pepsi", "Sprite", "Sunset" };
		
		GameBoard board = new GameBoard(cardNames);
		GameLogic logic = new GameLogic(statusLabel, matchCardsFrame);
		
		// board에서 섞인 카드를 버튼에 추가
		for (Card card : board.getCards()) {
			card.getBtn().addActionListener(e -> logic.selectCard(card));
		}
		
		// 게임판 추가
		matchCardsFrame.add(board.getPanel(), BorderLayout.CENTER);
		
		matchCardsFrame.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				System.exit(0);
			};
		});

		matchCardsFrame.setVisible(true);
		
		// 모든 카드의 앞면을 보여줌.
		board.flipAllToFront();

		// 2초 후 모든 카드의 뒷면을 보여줌.
		Timer timer = new Timer(2000, new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				board.flipAllToBack();
				// 카드가 클릭될 수 있게함
				logic.enableClick();
			}
		});
		timer.setRepeats(false);
		timer.start();
	}
	
}
