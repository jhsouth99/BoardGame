package common.network;

import java.io.Serializable;

/**
 * GameMessage 클래스는 클라이언트와 서버 간의 메시지 교환에 사용됩니다. 메시지 타입과 관련 데이터를 포함하며, 직렬화를
 * 지원합니다.
 */
public class GameMessage implements Serializable {

	// 메시지의 유형 (예: "login", "roll_dice" 등)
	public String type;

	// 메시지와 함께 전달되는 데이터 (예: 사용자 이름, 게임 상태 등)
	public Object payload;

	/**
	 * GameMessage 생성자. 메시지의 타입과 데이터를 초기화합니다.
	 *
	 * @param type    메시지의 유형
	 * @param payload 메시지와 함께 전달할 데이터
	 */
	public GameMessage(String type, Object payload) {
		this.type = type; // 메시지 유형 설정
		this.payload = payload; // 메시지 데이터 설정
	}
}
