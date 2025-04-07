package client.view.map;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

public class SaveLoadManager {

	// 저장 메서드
	public boolean saveBoard(Board board, Map<Position, Tile> positions, String filename) {
		try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(filename))) {
			// 맵 데이터 저장
			oos.writeObject(board);

			// 위치 데이터 저장 (Position은 Serializable이어야 함)
			oos.writeObject(positions);

			return true;
		} catch (IOException e) {
			System.err.println("맵 저장 중 오류 발생: " + e.getMessage());
			e.printStackTrace();
			return false;
		}
	}

	// 로드 메서드
	public LoadResult loadBoard(String filename) {
		try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(filename))) {
			// 맵 데이터 로드
			Board board = (Board) ois.readObject();

			// 위치 데이터 로드
			@SuppressWarnings("unchecked")
			Map<Position, Tile> positions = (Map<Position, Tile>) ois.readObject();

			return new LoadResult(board, positions);
		} catch (IOException | ClassNotFoundException e) {
			System.err.println("맵 로드 중 오류 발생: " + e.getMessage());
			e.printStackTrace();
			return null;
		}
	}

	public boolean saveMapToText(Board board, String filename) {
		try (BufferedWriter br = new BufferedWriter(new FileWriter(filename))) {
			// 맵 데이터 저장
			f: for (int i = 0; i < board.getTiles().size(); i++) {
				Tile t = board.getTiles().get(i);
				switch (t.getType()) {
				case "START":
				case "END":
				case "CELL":
					br.write(t.getType().charAt(0));
					break;
				case "BRIDGE_START":
					br.write('>');
					break;
				case "BRIDGE_END":
					br.write('<');
					break;
				case "EVENT":
					br.write('!');
					br.write(" " + t.getBackwardDirection());
					br.write(" " + t.getForwardDirection());
					br.write(" " + ((EventTile) t).getMiniGameType());
					br.write("\n");
					continue f;
				case "BONUS":
					br.write("+");
					br.write(" " + t.getBackwardDirection());
					br.write(" " + t.getForwardDirection());
					br.write(" " + ((ToolTile) t).getToolType());
					br.write("\n");
					continue f;
				}
				br.write(" " + t.getBackwardDirection());
				br.write(" " + t.getForwardDirection());
				br.write("\n");
			}
			return true;
		} catch (IOException e) {
			System.err.println("맵 저장 중 오류 발생: " + e.getMessage());
			e.printStackTrace();
			return false;
		}
	}

	public LoadResult loadMapFromText(String filename) {
		try (BufferedReader br = new BufferedReader(new FileReader(filename))) {
			// 맵 데이터 로드
			List<Tile> tiles = new ArrayList<Tile>();
			Map<Position, Tile> positions = new HashMap<Position, Tile>();

			String line = null;
			Tile prevTile = null;
			Position position = new Position(0, 0);
			while ((line = br.readLine()) != null) {
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
			}

			return new LoadResult(new Board(tiles), positions);
		} catch (IOException e) {
			System.err.println("맵 로드 중 오류 발생: " + e.getMessage());
			e.printStackTrace();
			return null;
		}

	}

}
