package client.view;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JFrame;
import javax.swing.JPanel;

public class ViewerTestMain2 {

	public static void main(String[] args) {
		JFrame frame = new JFrame();
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setSize(300, 300);
		JPanel pnlMain = new JPanel();
		pnlMain.setLayout(new BorderLayout());
		pnlMain.setBounds(0, 0, frame.getWidth(), frame.getHeight());
		VerticalButtonPanel panel = new VerticalButtonPanel();
		TitleContentButton btnArr[] = { new TitleContentButton("a", "aaaaa"),
				new TitleContentButton("b", "bbbbb"),
				new TitleContentButton("c", "ccccc") };
		panel.addButton(btnArr[0]);
		panel.addButton(btnArr[1]);
		panel.addButton(btnArr[2]);
		pnlMain.add(panel);
		frame.getContentPane().add(pnlMain);
		frame.setVisible(true);
		
		TitleContentButton btn = panel.findButtonByTitle("a");
		btn.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				String title = e.getActionCommand();
				System.out.println(title);
				panel.updateButton("a", "a1", "aaaaa");
				panel.removeButtonByTitle("c");
			}
		});
	}
}
