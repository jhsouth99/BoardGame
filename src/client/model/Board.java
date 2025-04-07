package client.model;

import java.util.List;

import client.model.tiles.ToolTile;
import client.model.tiles.EventTile;
import common.model.Direction;

/**
 * 게임 보드 관리 클래스
 */
public class Board {
	private final List<Tile> tiles;
	private final Tile startTile;
	private final Tile endTile;

	public Board(List<Tile> tiles) {
		this.tiles = tiles;
		this.startTile = tiles.get(0);
		this.endTile = tiles.get(tiles.size() - 1);
	}

	public Tile getStartTile() {
		return startTile;
	}

	public Tile getEndTile() {
		return endTile;
	}

	public List<Tile> getTiles() {
		return tiles;
	}

	/**
	 * 현재 위치에서 이동 가능 여부 확인
	 */
	public boolean isValidMove(Tile current, Direction dir) {
		return current.getDirections().contains(dir);
	}
	
	public String[] toStringArray() {

		String[] arg = new String[tiles.size()];
		f: for (int i = 0; i < tiles.size(); i++) {
			arg[i] = "";
			Tile t = tiles.get(i);
			switch (t.getType()) {
			case "START":
			case "END":
			case "CELL":
				arg[i] += t.getType().charAt(0);
				break;
			case "BRIDGE_START":
				arg[i] += '>';
				break;
			case "BRIDGE_END":
				arg[i] += '<';
				break;
			case "EVENT":
				arg[i] += '!';
				arg[i] += " " + t.getBackwardDirection();
				arg[i] += " " + t.getForwardDirection();
				arg[i] += " " + ((EventTile) t).getMiniGameType();
				continue f;
			case "BONUS":
				arg[i] += "+";
				arg[i] += " " + t.getBackwardDirection();
				arg[i] += " " + t.getForwardDirection();
				arg[i] += " " + ((ToolTile) t).getToolType();
				continue f;
			}
			arg[i] += " " + t.getBackwardDirection();
			arg[i] += " " + t.getForwardDirection();
		}
		return arg;
	}
}
