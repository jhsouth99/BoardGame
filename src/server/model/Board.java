package server.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import common.model.Direction;

public class Board {

	private List<Cell> cells;
	
	public Board(String[] cellInfo) {

		cells = new ArrayList<Cell>();
		Cell prevCell = null;
		Map<Integer, Cell> bridgeCounter = new HashMap<Integer, Cell>();
		int y = 0;
		int x = 0;
		for (String line : cellInfo) {
			String[] ss = line.split(" ");
			Cell cell = null;
			Direction fd = Direction.fromChar(ss[2].charAt(0));
			Direction bd = Direction.fromChar(ss[1].charAt(0));
			switch (ss[0]) {
			case "S":
				cell = new StartCell(fd);
				break;
			case "E":
				cell = new EndCell(bd);
				break;
			case "C":
				cell = new NormalCell(fd, bd);
				break;
			case ">":
				cell = new BridgeStartCell(fd, bd);
				bridgeCounter.put(y, cell);
				break;
			case "<":
				cell = new BridgeEndCell(fd, bd);
				BridgeStartCell sTile = (BridgeStartCell) bridgeCounter.get(y);
				bridgeCounter.remove(y);
				sTile.connect((BridgeEndCell) cell);
				break;
			case "!":
				cell = new EventCell(fd, bd, ss[3]);
				break;
			case "+":
				cell = new BonusCell(fd, bd, ss[3]);
				break;
			}
			if (prevCell != null) {
				cell.setBc(prevCell);
				prevCell.setFc(cell);
			}
			cell.setX(x);
			cell.setY(y);
			cells.add(cell);
			if (fd == Direction.UP)
				y--;
			else if (fd == Direction.DOWN)
				y++;
			else if (fd == Direction.RIGHT)
				x++;
			prevCell = cell;
		}
	}
	
	public List<Cell> getCells() {
		return cells;
	}
}
