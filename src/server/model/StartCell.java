package server.model;

import common.model.Direction;

public class StartCell extends Cell {

	public StartCell(Direction fd) {
		super(fd, Direction.NONE);
	}

	@Override
	public String getType() {
		return "START";
	}
}
