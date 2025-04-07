package client.model.tiles;

import client.controller.GameController;
import client.model.Player;
import common.model.Direction;

public class ToolTile extends NormalTile {
	public ToolTile(Direction forwardDirection, Direction backwardDirection, String toolType) {
		super(forwardDirection, backwardDirection);
		this.toolType = toolType;
		super.type = "BONUS";

		switch (toolType) {
		case "P":
			this.points = 1;
			break;
		case "H":
			this.points = 2;
			break;
		case "S":
			this.points = 3;
			break;
		default:
			this.points = 0;
		}
	}

	private String toolType; // 1, 2, 3
	private int points;

	public String getToolType() {
		return toolType;
	}
	
	public int getPoints() {
		return points;
	}

	@Override
	public void onLand(Player player, GameController controller) {
//		player.addScore(points);
	}
}
