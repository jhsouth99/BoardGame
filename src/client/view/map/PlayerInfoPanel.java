package client.view.map;

import java.awt.Color;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class PlayerInfoPanel extends JPanel {

	JLabel lblWinner;
	JButton btnReturn;
	JLabel[] attributeLine;
	JLabel[][] labelTable;
	JLabel[] blankLabels;

	public PlayerInfoPanel(List<String> playerNames) {
		super(new GridLayout(playerNames.size() + 2, 5));

		blankLabels = new JLabel[3];
		add(lblWinner = new JLabel(""));
		for (int i = 0; i < 3; i++) {
			blankLabels[i] = new JLabel("");
			add(blankLabels[i]);
		}
		add(btnReturn = new JButton("되돌아가기"));

		attributeLine = new JLabel[5];
		attributeLine[0] = new JLabel("이름");
		attributeLine[1] = new JLabel("x값(시작점은 0, 오른쪽으로 증가)");
		attributeLine[2] = new JLabel("y값(시작점은 0, 아래쪽으로 증가)");
		attributeLine[3] = new JLabel("점수");
		attributeLine[4] = new JLabel("이동 경로");
		for (int i = 0; i < 5; i++) {
			add(attributeLine[i]);
		}

		labelTable = new JLabel[playerNames.size()][5];
		for (int i = 0; i < playerNames.size(); i++) {
			labelTable[i][0] = new JLabel(playerNames.get(i));
			labelTable[i][1] = new JLabel("0");
			labelTable[i][2] = new JLabel("0");
			labelTable[i][3] = new JLabel("0");
			labelTable[i][4] = new JLabel("");
			for (int j = 0; j < 5; j++) {
				add(labelTable[i][j]);
			}
		}

		btnReturn.setVisible(false);
	}

	public void highlightRow(String name) {
		for (int i = 0; i < labelTable.length; i++) {
			if (labelTable[i][0].getForeground().equals(Color.BLUE)) {
				for (int j = 0; j < 5; j++) {
					labelTable[i][j].setForeground(Color.BLACK);
				}
				labelTable[i][4].setText("");
				break;
			}
		}
		for (int i = 0; i < labelTable.length; i++) {
			if (labelTable[i][0].getText().equals(name)) {
				for (int j = 0; j < 5; j++) {
					labelTable[i][j].setForeground(Color.BLUE);
				}
				break;
			}
		}
	}

	public void addPlayerMovement(String name, String dirName, int step) {
		for (int i = 0; i < labelTable.length; i++) {
			if (labelTable[i][0].getText().equals(name)) {
				int x = Integer.valueOf(labelTable[i][1].getText());
				int y = Integer.valueOf(labelTable[i][2].getText());
				switch (dirName) {
				case "LEFT":
					labelTable[i][1].setText(String.valueOf(x - step));
					labelTable[i][4].setText(labelTable[i][4].getText() + "←");
					break;
				case "RIGHT":
					labelTable[i][1].setText(String.valueOf(x + step));
					labelTable[i][4].setText(labelTable[i][4].getText() + "→");
					break;
				case "UP":
					labelTable[i][2].setText(String.valueOf(y - step));
					labelTable[i][4].setText(labelTable[i][4].getText() + "↑");
					break;
				case "DOWN":
					labelTable[i][2].setText(String.valueOf(y + step));
					labelTable[i][4].setText(labelTable[i][4].getText() + "↓");
					break;
				default:
				}
				break;
			}
		}
	}

	public void movePlayer(String name, int x, int y) {
		for (int i = 0; i < labelTable.length; i++) {
			if (labelTable[i][0].getText().equals(name)) {
				labelTable[i][1].setText(String.valueOf(x));
				labelTable[i][2].setText(String.valueOf(y));
				break;
			}
		}
	}

	public void setPlayerEnd(String name) {
		for (int i = 0; i < labelTable.length; i++) {
			labelTable[i][4].setText("");
			if (labelTable[i][0].getText().equals(name)) {
				for (int j = 0; j < 5; j++) {
					labelTable[i][j].setForeground(Color.LIGHT_GRAY);
				}
				break;
			}
		}
	}

	public void giveBonus(String name, int bonus) {
		for (int i = 0; i < labelTable.length; i++) {
			if (labelTable[i][0].getText().equals(name)) {
				int score = Integer.parseInt(labelTable[i][3].getText());
				labelTable[i][3].setText(String.valueOf(score + bonus));
				break;
			}
		}
	}

	public void setWinner(String playerName, Runnable r) {
		lblWinner.setText(playerName + "가 승리!");

		btnReturn.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				r.run();
			}
		});

		btnReturn.setVisible(true);
	}
}
