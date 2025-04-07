package client.view.minigame.rsp;

import java.awt.Color;
import java.awt.Font;
import java.awt.Label;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Random;
import java.util.concurrent.CountDownLatch;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;

public class Rsp {

	int win = 0;
	int lose = 0;
	int draw = 0;
	CountDownLatch latch;

	public Rsp(CountDownLatch latch) {
		this.latch = latch;
		JFrame frame = new JFrame("가위바위보");
		frame.setLayout(null);
		frame.setSize(720, 720);
		frame.setBackground(Color.WHITE);

		ImageIcon[] imgHand1 = new ImageIcon[3];
		ImageIcon[] imgHand2 = new ImageIcon[3];
		ImageIcon imgVs = new ImageIcon();

		imgHand1[0] = new ImageIcon("images/rsp/cr.png");
		imgHand1[1] = new ImageIcon("images/rsp/cs.png");
		imgHand1[2] = new ImageIcon("images/rsp/cp.png");

		imgHand2[0] = new ImageIcon("images/rsp/ur.png");
		imgHand2[1] = new ImageIcon("images/rsp/us.png");
		imgHand2[2] = new ImageIcon("images/rsp/up.png");

		JLabel lblHand1 = new JLabel(imgHand1[0]);
		lblHand1.setBounds(30, 60, 240, 240);
		JLabel lblHand2 = new JLabel(imgHand2[0]);
		lblHand2.setBounds(450, 60, 240, 240);
		JLabel lblVs = new JLabel(new ImageIcon("images/rsp/vs.jpg"));
		lblVs.setBounds(300, 120, 120, 120);

		Font fntLblCount = new Font("", Font.BOLD, 20);
		Label lblCount = new Label("0 win  0 draw  0 lose", Label.CENTER);
		lblCount.setBounds(120, 360, 480, 60);
		lblCount.setFont(fntLblCount);

		HandThread th1 = new HandThread(lblHand1, imgHand1);
		HandThread th2 = new HandThread(lblHand2, imgHand2);
		Thread th3 = new Thread(() -> {
			try {
				Thread.sleep(15000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			JOptionPane.showMessageDialog(frame, "점수: " + getScore(), "게임 끝", JOptionPane.PLAIN_MESSAGE);
			frame.dispose();
		});

		th1.setDaemon(true);
		th2.setDaemon(true);
		th3.setDaemon(true);

		JButton btnRocks = new JButton("바위");
		btnRocks.setBounds(40, 440, 180, 80);
		JButton btnScissors = new JButton("가위");
		btnScissors.setBounds(260, 440, 180, 80);
		JButton btnPaper = new JButton("보");
		btnPaper.setBounds(480, 440, 180, 80);
		JButton btnRetry = new JButton("다시 시작");
		btnRetry.setBounds(260, 560, 180, 80);
		btnRetry.setEnabled(false);

		ActionListener listener = new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				int selected = -1;
				switch (e.getActionCommand()) {
				case "바위":
					selected = 0;
					break;
				case "가위":
					selected = 1;
					break;
				case "보":
					selected = 2;
					break;
				case "다시 시작":
					btnRocks.setEnabled(true);
					btnScissors.setEnabled(true);
					btnPaper.setEnabled(true);
					btnRetry.setEnabled(false);
					break;
				}
				th1.toChange = !th1.toChange;
				th2.toChange = !th2.toChange;
				if (selected == -1)
					return;
				btnRocks.setEnabled(false);
				btnScissors.setEnabled(false);
				btnPaper.setEnabled(false);
				btnRetry.setEnabled(true);
				int rand = new Random().nextInt(3);
				lblHand1.setIcon(imgHand1[rand]);
				lblHand2.setIcon(imgHand2[selected]);
				switch (selected - rand) {
				case -1:
				case 2:
					win++;
					break;
				case 0:
					draw++;
					break;
				default:
					lose++;
					break;
				}
				lblCount.setText(win + " win  " + draw + " draw  " + lose + " lose");
				frame.invalidate();
			}
		};

		btnRocks.addActionListener(listener);
		btnScissors.addActionListener(listener);
		btnPaper.addActionListener(listener);
		btnRetry.addActionListener(listener);

		frame.add(lblHand1);
		frame.add(lblVs);
		frame.add(lblHand2);
		frame.add(lblCount);
		frame.add(btnRocks);
		frame.add(btnScissors);
		frame.add(btnPaper);
		frame.add(btnRetry);

		frame.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				frame.dispose();
			}
			@Override
			public void windowClosed(WindowEvent e) {
				latch.countDown();
			}
		});

		th1.start();
		th2.start();
		th3.start();

		frame.setVisible(true);

	}
	
	public int getScore() {
		return 5 * win;
	}

	public static class HandThread extends Thread {

		JLabel lblHand;
		ImageIcon[] icons;
		boolean toChange;

		public HandThread(JLabel lblHand, ImageIcon[] icons) {
			this.lblHand = lblHand;
			this.icons = icons;
			toChange = true;
		}

		@Override
		public void run() {
			try {
				int i = 0;
				while (true) {
					Thread.sleep(50);
					if (toChange)
						lblHand.setIcon(icons[++i % 3]);
				}
			} catch (InterruptedException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}
	}

	
}
