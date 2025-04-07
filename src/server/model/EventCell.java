package server.model;

import common.model.Direction;

public class EventCell extends Cell {

	String miniGameName = null;
	
	public EventCell(Direction fd, Direction bd, String eventName) {
		super(fd, bd);
		this.miniGameName = eventName;
		// TODO Auto-generated constructor stub
	}

	public String getMiniGameType() {
		// TODO Auto-generated method stub
		return miniGameName;
	}

	@Override
	public String getType() {
		return "EVENT";
	}
}
