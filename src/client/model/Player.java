package client.model;

public class Player {

	private int score;
	Tile currentTile;

	String name;
	GameRoom room;
	
	boolean readyToPlay = false;
	
	/**
	 * Player
	 *
	 * @param name
	 */
	public Player(String name) {
		this.name = name;
	}

	public void addScore(int points) {
		this.score += points;
	}

	public void setCurrentTile(Tile tile) {
		this.currentTile = tile;
	}

	// [Getter]
	public String getName() {
		return name;
	}

	public GameRoom getRoom() {
		return room;
	}

	public int getScore() {
		return score;
	}

	public Tile getCurrentTile() {
		return currentTile;
	}

	public void useItem(int index) {
		// 실제 아이템 사용 로직 TODO
	}

	public void setRoom(GameRoom room) {
		this.room = room;
	}
}
