package client.view.minigame.memory;

import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashSet;
import java.util.Set;

import javax.swing.JLabel;
import javax.swing.Timer;

public class GameLogic {

	// 현재 선택된 두 개의 카드
	private Card firstCard, secondCard;
	
	// 맞춘 카드 쌍 개수 및 오답 횟수 변수
	private int matchedPairs = 0;
	private int wrongAnswers = 0;
	private static final int Max_WRONG_ANSWERS = 3;
	
	// 게임 상태 표시 라벨
	private JLabel statusLabel;
	
	// 버튼 클릭 가능 여부
	private boolean canClick = false;
	
	// 이미 짝을 맞춰서 게임에서 제외된 카드들을 저장하는 HashSet (중복 선택 방지용)
	private Set<Card> matchedCards = new HashSet<>();
	
	// 메인 프레임 참조
	private Frame matchCardsFrame;

	public GameLogic(JLabel statusLabel, Frame matchCardsFrame) {
		this.statusLabel = statusLabel;
		this.matchCardsFrame = matchCardsFrame;
	}
	
	// 두 개의 카드 선택 메서드
	public void selectCard(Card card) {
		// 클릭 가능 여부 확인 및 중복 클릭 방지
		if (!canClick || matchedCards.contains(card) || card == firstCard) {
			return;
		}
		
		// 선택한 카드의 앞면 이미지를 보여줌
		card.flipToFront();

		if (firstCard == null) {
			firstCard = card;
		} else {
			secondCard = card;
			canClick = false;

			Timer timer = new Timer(1000, new ActionListener() {
				
				@Override
				public void actionPerformed(ActionEvent e) {
					checkMatch();
					((Timer)e.getSource()).stop();
				}
			});
			timer.setRepeats(false);
			timer.start();
		}
	}
	
	// 두 개의 카드가 일치하는지 확인하고, 게임 상태를 업데이트
	private void checkMatch() {
		if (firstCard == null || secondCard == null) {
			return;
		}

		if (firstCard.getName().equals(secondCard.getName())) {
			matchedPairs++;
			
			// 맞춘 카드를 matchedCards에 넣음
			matchedCards.add(firstCard);
			matchedCards.add(secondCard);

			if (matchedPairs == 6) {

				// 게임 성공 팝업
				new FinishPop(matchCardsFrame, true, calculateScore());
				canClick = false;
				return;
			}

		} else {
			// 맞추지 못한 두 카드를 다시 뒷면으로 뒤집음
			firstCard.flipToBack();
			secondCard.flipToBack();

			wrongAnswers++;
			statusLabel.setText
			("오답 :  " + wrongAnswers + " (" + Max_WRONG_ANSWERS + "회 초과시 종료)");

			if (wrongAnswers > Max_WRONG_ANSWERS) {
				new FinishPop(matchCardsFrame, false, calculateScore());
				canClick = false;
				return;
			}
		}

		firstCard = null;
		secondCard = null;
		canClick = true;
	}
	
	// 점수 계산 메서드
	int calculateScore() {
		return 64 - (wrongAnswers * 16);
	}
	
	// 선택 가능 메서드
	public void enableClick() {
		canClick = true;
	}
	
}
