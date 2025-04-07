package server.model;

import common.model.Direction;

public class EndCell extends Cell {

	public EndCell(Direction bd) {
		super(Direction.NONE, bd);
	}

	@Override
	public String getType() {
		return "END";
	}
}
