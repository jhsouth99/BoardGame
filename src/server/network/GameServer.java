package server.network;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

import common.network.GameMessage;
import server.service.GameService;

/**
 * GameServer 클래스는 서버의 핵심 기능을 관리합니다. 클라이언트 연결 수락, 핸들러 관리, 메시지 브로드캐스팅을 담당합니다.
 */
public class GameServer {

	ServerSocket serverSocket; // 서버 소켓
	private List<ClientHandler> handlers = new ArrayList<>(); // 연결된 클라이언트 핸들러 목록
	private GameService gameService = new GameService(this); // 게임 서비스 객체

	/**
	 * 특정 그룹에 메시지 브로드캐스팅.
	 *
	 * @param members 메시지를 받을 핸들러 목록
	 * @param msg     전송할 메시지
	 */
	public synchronized void broadcast(List<ClientHandler> members, GameMessage msg) {
		if (members == null)
			members = handlers;
		for (ClientHandler handler : members) {
			handler.send(msg);
		}
	}

	/**
	 * 서버를 시작하고 클라이언트 연결을 수락합니다.
	 *
	 * @param port 서버 포트 번호
	 * @throws IOException 소켓 생성 실패 시 발생
	 */
	public void start(int port) throws IOException {
		serverSocket = new ServerSocket(port);
		System.out.println("Server started on port " + port);
		handlers = new ArrayList<ClientHandler>();

		while (true) {
			try {
				Socket socket = serverSocket.accept(); // 클라이언트 연결 대기
				ClientHandler handler = new ClientHandler(socket, this, gameService);
				handlers.add(handler);

				// 클라이언트 처리 스레드 시작
				Thread th = new Thread(handler);
				th.setDaemon(true); // 데몬 스레드로 설정
				th.start();

				System.out.println(socket.getInetAddress().getHostName() + " 입장");
			} catch (IOException e) {
				e.printStackTrace(); // TODO: 예외 처리 개선 필요
			}
		}
	}

	/**
	 * 핸들러 목록에서 특정 핸들러 제거.
	 *
	 * @handler 제거할 ClientHandler 객체
	 */
	public void removeHandler(ClientHandler handler) {
		this.handlers.remove(handler);
	}
}
