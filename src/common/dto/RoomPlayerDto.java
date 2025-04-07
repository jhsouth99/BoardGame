package common.dto;

import java.io.Serializable;

public class RoomPlayerDto implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	String roomName;
	String playerName;
	boolean readyToPlay;
	
	public RoomPlayerDto(String roomName, String playerName, boolean readyToPlay) {
		this.roomName = roomName;
		this.playerName = playerName;
		this.readyToPlay = readyToPlay;
	}
	public String getRoomName() {
		return roomName;
	}
	public void setRoomName(String roomName) {
		this.roomName = roomName;
	}
	public String getPlayerName() {
		return playerName;
	}
	public void setPlayerName(String playerName) {
		this.playerName = playerName;
	}
	public boolean isReadyToPlay() {
		return readyToPlay;
	}
	public void setReadyToPlay(boolean readyToPlay) {
		this.readyToPlay = readyToPlay;
	}
}
