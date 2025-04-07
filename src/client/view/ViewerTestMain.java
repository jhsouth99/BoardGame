package client.view;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JFrame;
import javax.swing.JPanel;

public class ViewerTestMain {

	public static void main(String[] args) {
		JFrame frame = new JFrame();
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setSize(300, 300);
		VerticalButtonPanel panel = new VerticalButtonPanel();
		TitleContentButton btnArr[] = { new TitleContentButton("a", "aaaaa"),
				new TitleContentButton("b", "bbbbb"),
				new TitleContentButton("c", "ccccc") };
		panel.addButton(btnArr[0]);
		panel.addButton(btnArr[1]);
		panel.addButton(btnArr[2]);
		frame.getContentPane().add(panel);
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
