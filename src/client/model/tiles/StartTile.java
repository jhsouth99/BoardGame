package client.model.tiles;

import client.controller.GameController;
import client.model.Player;
import client.model.Tile;
import common.model.Direction;

public class StartTile extends Tile {

	public StartTile(Direction forwardDirection) {
		super(forwardDirection, Direction.NONE);
		super.type = "START";
	}

	@Override
	public void onLand(Player player, GameController controller) {
	}
}