package client.view.minigame.memory;

import javax.swing.ImageIcon;
import javax.swing.JButton;

public class Card {
	
	private String name;
	private JButton btn;
	private static ImageIcon backImage = new ImageIcon("images/memory/BackImage.jpg");
	private static final int WIDTH = 159;
	private static final int HEIGHT = 207;
	
	public Card(String name) {
		this.name = name;
		this.btn = new JButton(backImage);
	}

	public String getName() {
		return name;
	}

	public JButton getBtn() {
		return btn;
	}
	
	public void flipToFront() {
		btn.setIcon(new ImageIcon("images/memory/" + name + ".jpg"));
	}
	
	public void flipToBack() {
		btn.setIcon(backImage);
	}
	
}
