package client.model.tiles;

import client.controller.GameController;
import client.model.Player;
import client.model.Tile;
import common.model.Direction;

public class EndTile extends Tile {

	public EndTile(Direction backwardDirection) {
		super(Direction.NONE, backwardDirection);
		super.type = "END";
	}

	@Override
	public void onLand(Player player, GameController controller) {
		player.addScore(7);
	}
}