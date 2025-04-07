package common.dto;

import java.io.Serializable;

import server.model.Player;

public class UserDetailDto implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	String name;
	int highestScore;
	int winCount;
	int playCount;

	public UserDetailDto(String name, int highestScore, int winCount, int playCount) {
		this.name = name;
		this.highestScore = highestScore;
		this.winCount = winCount;
		this.playCount = playCount;
	}

	public UserDetailDto(Player player) {
		this.name = player.getName();
		this.highestScore = player.getHighestScore();
		this.winCount = player.getWinCount();
		this.playCount = player.getPlayCount();
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}

	public int getHighestScore() {
		return highestScore;
	}
	public void setHighestScore(int highestScore) {
		this.highestScore = highestScore;
	}
	public int getWinCount() {
		return winCount;
	}
	public void setWinCount(int winCount) {
		this.winCount = winCount;
	}
	public int getPlayCount() {
		return playCount;
	}
	public void setPlayCount(int playCount) {
		this.playCount = playCount;
	}
}
