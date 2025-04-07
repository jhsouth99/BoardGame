package client.model;

import java.util.ArrayList;
import java.util.List;

import client.view.map.LoadResult;

public class GameRoom {

	// 게임 방 이름
	String name;

	// 게임 방에 속한 플레이어 목록
	List<Player> players;
	int readyToPlayCount;
	LoadResult loadResult;
	boolean ending;

	/**
	 * GameRoom 생성자. 방 이름을 설정하고 빈 플레이어 목록을 초기화합니다.
	 *
	 * @param name 게임 방 이름
	 */
	public GameRoom(String name) {
		this.name = name; // 방 이름 설정
		this.players = new ArrayList<Player>(); // 플레이어 목록 초기화
	}

	/**
	 * 특정 플레이어를 방에서 제거합니다.
	 *
	 * @param player 제거할 Player 객체
	 */
	public void removePlayer(Player player) {
		players.remove(player); // 플레이어를 목록에서 제거
		player.room = null; // 플레이어의 room 참조를 null로 설정
	}

	public void setLoadResult(LoadResult loadResult) {
		// TODO Auto-generated method stub
		this.loadResult = loadResult;
	}

	public String getName() {
		return name;
	}

	public List<Player> getPlayers() {
		return players;
	}

	public List<String> getPlayerNames() {
		return players.stream().map(p -> p.getName()).toList();
	}
	
	public int getReadyToPlayCount() {
		return readyToPlayCount;
	}
	
	public void setReadyToPlayCount(int readyToPlayCount) {
		this.readyToPlayCount = readyToPlayCount;
	}
	
	public boolean isEnding() {
		return ending;
	}
	
	public void setEnding(boolean ending) {
		this.ending = ending;
	}
	
	public LoadResult getLoadResult() {
		return loadResult;
	}
}
