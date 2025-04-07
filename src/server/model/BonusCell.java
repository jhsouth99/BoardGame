package server.model;

import common.model.Direction;

public class BonusCell extends Cell {

	String toolType;
	public BonusCell(Direction fd, Direction bd, String toolType) {
		super(fd, bd);
		this.toolType = toolType;
	}
	public String getToolType() {
		// TODO Auto-generated method stub
		return toolType;
	}
	
	public int getBonus() {
		switch (toolType) {
		case "P":
			return 20;
		case "H":
			return 40;
		case "S":
			return 60;
		}
		return 0;
	}
	
	@Override
	public String getType() {
		return "BONUS";
	}
}
