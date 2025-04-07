package client.view.minigame.memory;

import java.awt.Button;
import java.awt.Font;
import java.awt.Frame;
import java.awt.Label;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JLabel;
import javax.swing.SwingConstants;

public class FinishPop {

	private Frame matchCardsFrame;
	private Frame finishFrame;

	public FinishPop(Frame matchCardFrame, boolean isSucces, int score) {
		this.matchCardsFrame = matchCardFrame;

		finishFrame = new Frame("Game Result");
		finishFrame.setSize(300, 200);
		finishFrame.setLocationRelativeTo(null);
		finishFrame.setLayout(null);

		JLabel resultLabel = new JLabel(isSucces ? "축하합니다!" : "Game Over", SwingConstants.CENTER);
		resultLabel.setFont(new Font("", Font.BOLD, 24));
		resultLabel.setBounds(50, 50, 200, 30);
		finishFrame.add(resultLabel);

		JLabel scoreLabel = new JLabel("획득점수: " + score, SwingConstants.CENTER);
		scoreLabel.setFont(new Font("", Font.BOLD, 18));
		scoreLabel.setBounds(50, 90, 200, 30);
		finishFrame.add(scoreLabel);
		
		Button confirmBtn = new Button("OK");
		confirmBtn.setBounds(110, 140, 80, 30);
		confirmBtn.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				closeAllFrames();
			}
		});
		finishFrame.add(confirmBtn);

		finishFrame.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				closeAllFrames();
			};
		});

		finishFrame.setVisible(true);

	}

	private void closeAllFrames() {
			matchCardsFrame.dispose();
			finishFrame.dispose();
	}

}
