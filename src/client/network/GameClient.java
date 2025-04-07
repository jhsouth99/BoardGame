package client.network;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

import client.controller.GameController;
import common.network.GameMessage;

/**
 * GameClient 클래스는 서버와의 네트워크 통신을 담당합니다. 
 * 소켓 연결, 메시지 송수신, 컨트롤러와의 상호작용을 관리합니다.
 */
public class GameClient {

	// 서버와의 통신을 위한 소켓
	Socket socket;

	// 서버로부터 들어오는 데이터를 읽기 위한 입력 스트림
	ObjectInputStream in;

	// 서버로 데이터를 보내기 위한 출력 스트림
	ObjectOutputStream out;

	// 게임 로직을 관리하는 컨트롤러
	GameController controller;

	// 클라이언트가 서버 메시지를 계속 수신할지 여부를 나타내는 플래그
	boolean listening;

	/**
	 * 서버에 연결하는 메서드. 소켓을 생성하고 입출력 스트림을 초기화합니다.
	 *
	 * @param ip   서버의 IP 주소
	 * @param port 서버의 포트 번호
	 * @throws IOException 소켓 연결 또는 스트림 초기화 중 오류 발생 시 예외를 던집니다.
	 */
	public void connectToServer(String ip, int port) throws IOException {
		socket = new Socket(ip, port); // 서버에 연결
		out = new ObjectOutputStream(socket.getOutputStream()); // 출력 스트림 초기화
		in = new ObjectInputStream(socket.getInputStream()); // 입력 스트림 초기화

		System.out.println("IN OUT"); // 디버깅용 메시지 출력

		listening = true; // 메시지 수신 활성화
		new Thread(this::listen).start(); // 메시지 수신용 스레드 시작
	}

	/**
	 * GameController를 설정하는 메서드.
	 *
	 * @param controller GameController 객체
	 */
	public void setController(GameController controller) {
		this.controller = controller;
	}

	/**
	 * 서버로 메시지를 전송하는 메서드.
	 *
	 * @param msg 전송할 GameMessage 객체
	 */
	public void send(GameMessage msg) {
		try {
			out.writeObject(msg); // 메시지를 출력 스트림으로 전송
		} catch (IOException e) {
			e.printStackTrace(); // 전송 중 오류 발생 시 스택 트레이스 출력
		}
	}

	/**
	 * 서버로부터 메시지를 수신하고 처리하는 메서드. 수신된 메시지는 컨트롤러로 전달됩니다.
	 */
	public void listen() {
		while (listening) { // listening이 true인 동안 반복 실행
			try {
				GameMessage msg = (GameMessage) in.readObject(); // 서버로부터 메시지 읽기

				if (msg.type.equals("exit")) // "exit" 타입 메시지가 오면 루프 종료
					break;

				if (controller != null) {
					controller.handleGameMessage(msg); // 컨트롤러에 메시지 전달 및 처리 요청
				}
			} catch (Exception e) {
				e.printStackTrace(); // 수신 중 오류 발생 시 스택 트레이스 출력
			}
		}

		// 연결 종료 시 스트림과 소켓 닫기
		try {
			in.close();
			out.close();
			socket.close();

			System.exit(0); // 프로그램 종료
		} catch (IOException e) {
			e.printStackTrace(); // 자원 해제 중 오류 발생 시 스택 트레이스 출력
		}
	}

	/**
	 * listening 플래그를 설정하는 메서드.
	 *
	 * @param listening true면 메시지 수신 활성화, false면 비활성화
	 */
	public void setListening(boolean listening) {
		this.listening = listening;
	}
	
	public boolean isConnected() {
		return this.socket != null && this.socket.isConnected();
	}
}
