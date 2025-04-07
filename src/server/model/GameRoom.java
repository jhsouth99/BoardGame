package server.model;

import java.util.ArrayList;
import java.util.List;

import server.network.ClientHandler;

/**
 * GameRoom 클래스는 게임 방을 나타내며, 방 이름과 플레이어 목록을 관리합니다.
 */
public class GameRoom {

	// 게임 방 이름
	String name;

	// 게임 방에 속한 플레이어 목록
	List<Player> players;

	boolean playing;
	int endCount;
	Board board;

	private int movement;

	private int diceResult;
	private int playerIndex;

	private boolean resultSaved;

	/**
	 * GameRoom 생성자. 방 이름을 설정하고 빈 플레이어 목록을 초기화합니다.
	 *
	 * @param name 게임 방 이름
	 */
	public GameRoom(String name) {
		this.name = name; // 방 이름 설정
		this.players = new ArrayList<Player>(); // 플레이어 목록 초기화
		playing = false;
		endCount = 0;
		movement = 0;
		diceResult = 0;
		resultSaved = false;
	}

	/**
	 * 방에 있는 모든 플레이어의 ClientHandler 객체를 반환합니다.
	 *
	 * @return ClientHandler 객체 리스트
	 */
	public List<ClientHandler> getHandlers() {
		List<ClientHandler> handlers = new ArrayList<ClientHandler>();

		for (Player p : players) { // 각 플레이어에 대해
			handlers.add(p.getHandler()); // 해당 플레이어의 핸들러를 리스트에 추가
		}

		return handlers; // 핸들러 리스트 반환
	}

	/**
	 * 특정 플레이어를 방에서 제거합니다.
	 *
	 * @param player 제거할 Player 객체
	 */
	public void removePlayer(Player player) {
		if (isEnded()) {
			endCount--;
			++playerIndex;
			if (playerIndex >= players.size())
				playerIndex -= players.size();
		} else if (playing)
			if (players.get(playerIndex).equals(player)) {
				if (endCount < players.size() - 1) {
					setNextPlayer();
				}
			} else if (player.getCurrentCell().getType().equals("END")) {
				endCount--;
				++playerIndex;
				if (playerIndex >= players.size())
					playerIndex -= players.size();
			}
		if (playerIndex > players.indexOf(player)) {
			playerIndex--;
		}
		players.remove(player); // 플레이어를 목록에서 제거
		player.room = null; // 플레이어의 room 참조를 null로 설정
	}

	public void setMap(String mapName) {
		// TODO Auto-generated method stub

	}

	public String getName() {
		return name;
	}

	public List<Player> getPlayers() {
		return players;
	}

	public void addPlayer(Player player) {
		players.add(player);
		player.setRoom(this);
	}

	public List<String> getPlayerNames() {
		return players.stream().map(p -> p.getName()).toList();
	}

	public boolean isReadyToPlay() {
		for (Player p : players) {
			if (!p.isReadyToPlay())
				return false;
		}
		return true;
	}

	public boolean isPlaying() {
		return playing;
	}

	public void setPlaying(boolean playing) {
		this.playing = playing;
	}

	public boolean isEnding() {
		// TODO Auto-generated method stub
		return endCount > 0;
	}

	public void increaseEndCount() {
		endCount++;
	}

	public String[] getBoardStrings() {

		List<Cell> cells = board.getCells();
		String[] ret = new String[cells.size()];
		f: for (int i = 0; i < cells.size(); i++) {
			ret[i] = "";
			Cell t = cells.get(i);
			switch (t.getType()) {
			case "START":
			case "END":
			case "CELL":
				ret[i] += t.getType().charAt(0);
				break;
			case "BRIDGE_START":
				ret[i] += '>';
				break;
			case "BRIDGE_END":
				ret[i] += '<';
				break;
			case "EVENT":
				ret[i] += '!';
				ret[i] += " " + t.getBd();
				ret[i] += " " + t.getFd();
				ret[i] += " " + ((EventCell) t).getMiniGameType();
				continue f;
			case "BONUS":
				ret[i] += "+";
				ret[i] += " " + t.getBd();
				ret[i] += " " + t.getFd();
				ret[i] += " " + ((BonusCell) t).getToolType();
				continue f;
			}
			ret[i] += " " + t.getBd();
			ret[i] += " " + t.getFd();
		}
		return ret;
	}

	public Board getBoard() {
		return board;
	}

	public void setBoard(Board board) {
		this.board = board;
	}

	public int getMovement() {
		// TODO Auto-generated method stub
		return movement;
	}

	public void setMovement(int i) {
		movement = i;
	}

	public void increaseMovement() {
		movement++;
	}

	public int getDiceResult() {
		// TODO Auto-generated method stub
		return diceResult;
	}

	public void setDiceResult(int diceResult) {
		this.diceResult = diceResult;
	}

	public boolean isEnded() {
		return endCount >= players.size();
	}

	public int getEndCount() {
		return endCount;
	}

	public void decreaseEndCount() {
		endCount--;
	}

	public void setNextPlayer() {
		if (endCount >= players.size()) {
			return;
		}
		movement = 0;
		diceResult = 0;
		while (true) {
			++playerIndex;
			if (playerIndex >= players.size())
				playerIndex -= players.size();
			if (!players.get(playerIndex).getCurrentCell().getType().equals("END"))
				break;
		}
	}

	public Player getCurrentPlayer() {
		if (endCount >= players.size()) {
			return null;
		}
		return players.get(playerIndex);
	}

	public boolean isResultSaved() {
		return resultSaved;
	}

	public void setResultSaved(boolean resultSaved) {
		this.resultSaved = resultSaved;
	}
}
