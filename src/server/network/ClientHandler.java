package server.network;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

import common.network.GameMessage;
import server.model.Player;
import server.service.GameService;

/**
 * ClientHandler 클래스는 개별 클라이언트 연결을 관리합니다. 메시지 수신/발신 및 서비스 계층과의 상호작용을 처리합니다.
 */
public class ClientHandler implements Runnable {

	Player player; // 연결된 플레이어 객체
	Socket socket; // 클라이언트 소켓
	GameServer server; // 서버 참조
	GameService service; // 비즈니스 로직 처리 서비스
	ObjectInputStream in; // 입력 스트림
	ObjectOutputStream out; // 출력 스트림

	/**
	 * ClientHandler 생성자.
	 *
	 * @param socket  클라이언트 소켓
	 * @param server  서버 객체
	 * @param service 게임 서비스 객체
	 * @throws IOException 스트림 초기화 실패 시 발생
	 */
	public ClientHandler(Socket socket, GameServer server, GameService service) throws IOException {
		this.socket = socket;
		this.server = server;
		this.service = service;
		out = new ObjectOutputStream(socket.getOutputStream());
		in = new ObjectInputStream(socket.getInputStream());
	}

	/**
	 * 클라이언트로 메시지 전송.
	 *
	 * @param msg 전송할 GameMessage 객체
	 */
	public void send(GameMessage msg) {
		try {
			out.writeObject(msg);
		} catch (IOException e) {
			e.printStackTrace(); // TODO: 연결 끊김 처리 개선 필요
		}
	}

	/**
	 * 클라이언트 메시지 수신 루프. 수신된 메시지를 GameService로 전달합니다.
	 */
	@Override
	public void run() {
		try {
			while (true) {
				GameMessage msg = (GameMessage) in.readObject();
				service.processMessage(msg, this); // 서비스에 메시지 처리 위임
				if (msg.type.equals("exit"))
					break; // 종료 메시지 시 루프 탈출
			}
		} catch (ClassNotFoundException | IOException e) {
			e.printStackTrace();
			System.out.println("Client disconnected."); // TODO: 연결 종료 처리 개선
		}
		System.out.println(socket.getInetAddress().getHostName() + " 퇴장");
	}

	public void setPlayer(Player player) {
		this.player = player;
	}

	public Player getPlayer() {
		return player;
	}

	public GameServer getServer() {
		return server;
	}
}
