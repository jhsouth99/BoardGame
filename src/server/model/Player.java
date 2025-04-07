package server.model;

import common.model.Direction;
import server.network.ClientHandler;

public class Player {
	private int score;

	String name;
	GameRoom room;
	int highestScore = 0;
	int winCount = 0;
	int playCount = 0;
	ClientHandler handler;
	boolean readyToPlay = false;
	Cell currentCell;

	/**
	 * Player
	 *
	 * @param name
	 * @param handler
	 */
	public Player(String name, ClientHandler handler) {
		this.name = name;
		this.score = 0;
		this.handler = handler;

	}

	public Player(String name, ClientHandler handler, int highestScore, int winCount, int playCount) {
		this.name = name;
		this.handler = handler;
		this.highestScore = highestScore;
		this.winCount = winCount;
		this.playCount = playCount;
	}

	public void addScore(int points) {
		this.score += points;
	}

	public boolean moveTo(Direction dir) {
		if (dir.equals(currentCell.getFd())) {
			currentCell = currentCell.getFc();
			return true;
		}
		if (dir.equals(currentCell.getBd())) {
			currentCell = currentCell.getBc();
			return true;
		}
		if (dir.equals(Direction.RIGHT) && currentCell instanceof BridgeStartCell) {
			currentCell = currentCell.getConnectedCell();
			return true;
		}
		if (dir.equals(Direction.LEFT) && currentCell instanceof BridgeEndCell) {
			currentCell = currentCell.getConnectedCell();
			return true;
		}
		return false;
	}

	public void setCurrentCell(Cell cell) {
		this.currentCell = cell;
	}

	// [Getter]
	public String getName() {
		return name;
	}

	public ClientHandler getHandler() {
		return handler;
	}

	public GameRoom getRoom() {
		return room;
	}

	public int getScore() {
		return score;
	}

	public Cell getCurrentCell() {
		return currentCell;
	}

	public void useItem(int index) {
		// 실제 아이템 사용 로직 TODO
	}

	public boolean isReadyToPlay() {
		return readyToPlay;
	}

	public void setRoom(GameRoom room) {
		this.room = room;
	}

	public void setReadyToPlay(boolean ready) {
		readyToPlay = ready;
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
