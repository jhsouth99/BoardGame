package client;

import java.io.IOException;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

import client.controller.GameController;
import client.network.GameClient;
import client.view.GameViewer;

public class ClientMain {

	public static void main(String[] args) {
		// 기본값
		String defaultIp = "localhost";
		String defaultPort = "3000";

		// 커스텀 패널 생성
		JTextField ipField = new JTextField(defaultIp);
		JTextField portField = new JTextField(defaultPort);

		JPanel panel = new JPanel();
		panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
		panel.add(new JLabel("서버 IP 주소:"));
		panel.add(ipField);
		panel.add(Box.createVerticalStrut(10)); // 여백
		panel.add(new JLabel("포트 번호:"));
		panel.add(portField);

		int result = JOptionPane.showConfirmDialog(null, panel, "서버 연결 정보 입력", JOptionPane.OK_CANCEL_OPTION,
				JOptionPane.PLAIN_MESSAGE);

		if (result != JOptionPane.OK_OPTION) {
			System.out.println("사용자가 입력을 취소했습니다.");
			return;
		}

		String ip = ipField.getText().trim();
		String portStr = portField.getText().trim();
		int port;

		// 유효성 검사
		if (ip.isEmpty())
			ip = defaultIp;

		try {
			port = Integer.parseInt(portStr);
		} catch (NumberFormatException e) {
			JOptionPane.showMessageDialog(null, "포트 번호가 올바르지 않습니다. 기본값 3000을 사용합니다.");
			port = 3000;
		}

		// 게임 클라이언트 실행
		GameClient client = new GameClient();
		GameController controller = new GameController(client);

		try {
			client.connectToServer(ip, port);
			GameViewer viewer = new GameViewer(controller);
			System.out.println("연결 성공!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
		} catch (IOException e) {
			e.printStackTrace();
			JOptionPane.showMessageDialog(null, "서버 연결에 실패했습니다: " + e.getMessage());
			controller.exitGame();
		}
	}
}
