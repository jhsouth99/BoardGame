package client.view.minigame.memory;

import java.awt.GridLayout;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import javax.swing.JPanel;

public class GameBoard {

	private JPanel panel;
	private List<Card> cards;
	
	public GameBoard(String[] cardName) {
		panel = new JPanel(new GridLayout(3, 4, 10, 10));
		cards = new ArrayList<>();
		
		// 카드 이름을 섞을 수 있는 형태로 준비
		List<String> shuffledNames = new ArrayList<>(Arrays.asList(cardName));
		Collections.shuffle(shuffledNames);
		
		for (String name : shuffledNames) {
			Card card = new Card(name);
			cards.add(card);
			panel.add(card.getBtn());
		}
		
	}
	
	public JPanel getPanel() {
		return panel;
	}

	public List<Card> getCards() {
		return cards;
	}
	
	// 모든 카드의 앞면을 보여주는 메서드
	public void flipAllToFront() {
		for (Card card : cards) {
			card.flipToFront();
		}
	}
	
	// 모든 카드의 뒷면을 보여주는 메서드
	public void flipAllToBack() {
		for (Card card : cards) {
			card.flipToBack();
		}
	}
	
}
