package client.model;

import client.controller.GameController;
import common.model.Direction;

public class NormalTile extends Tile {

    public NormalTile(Direction forwardDirection, Direction backwardDirection) {
		super(forwardDirection, backwardDirection);
		// TODO Auto-generated constructor stub
	}

	@Override
    public String getType() {
        return "Normal";
    }

	@Override
	public void onLand(Player player, GameController controller) {
		// TODO Auto-generated method stub
		
	}
}