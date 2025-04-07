package client.view.map;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import client.model.Board;
import client.model.Tile;
import client.model.tiles.ToolTile;
import client.model.tiles.BridgeEndTile;
import client.model.tiles.BridgeStartTile;
import client.model.tiles.EndTile;
import client.model.tiles.EventTile;
import client.model.tiles.NormalTile;
import client.model.tiles.StartTile;
import common.model.Direction;

// 로드 결과를 포함하는 클래스
public class LoadResult {
	public Board board;
	public Map<Position, Tile> tileMap;
	public Map<Tile, Position> positionMap;

	public LoadResult(Board board, Map<Position, Tile> positions) {
		this.board = board;
		this.tileMap = positions;
		this.positionMap = positions.entrySet().stream()
				.collect(Collectors.toMap(Map.Entry::getValue, Map.Entry::getKey));
	}

	public static LoadResult fromStringArray(String[] arg) {

		List<Tile> tiles = new ArrayList<Tile>();
		Map<Position, Tile> positions = new HashMap<Position, Tile>();
		Tile prevTile = null;
		Position position = new Position(0, 0);
		for (String line : arg) {
			System.out.println(line);
			String[] ss = line.split(" ");
			Tile tile = null;
			Direction fd = Direction.fromChar(ss[2].charAt(0));
			Direction bd = Direction.fromChar(ss[1].charAt(0));
			switch (ss[0]) {
			case "S":
				tile = new StartTile(fd);
				break;
			case "E":
				tile = new EndTile(bd);
				break;
			case "C":
				tile = new NormalTile(fd, bd);
				break;
			case ">":
				tile = new BridgeStartTile(fd, bd);
				break;
			case "<":
				tile = new BridgeEndTile(fd, bd);
				BridgeStartTile sTile = (BridgeStartTile) positions
						.get(position.movePosition(Direction.LEFT).movePosition(Direction.LEFT));
				sTile.connect((BridgeEndTile) tile);
				break;
			case "!":
				tile = new EventTile(fd, bd, ss[3]);
				break;
			case "+":
				tile = new ToolTile(fd, bd, ss[3]);
				break;
			}
			positions.put(position, tile);
			if (prevTile != null) {
				tile.setBackwardTile(prevTile);
				prevTile.setForwardTile(tile);
			}
			tiles.add(tile);
			position = position.movePosition(fd);
			prevTile = tile;
		}
		return new LoadResult(new Board(tiles), positions);
	}
}