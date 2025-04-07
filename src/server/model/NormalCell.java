package server.model;

import common.model.Direction;

public class NormalCell extends Cell {

	public NormalCell(Direction fd, Direction bd) {
		super(fd, bd);
	}

	@Override
	public String getType() {
		return "CELL";
	}
}
