package client.model.tiles;

import client.controller.GameController;
import client.model.Player;
import client.model.Tile;
import common.model.Direction;

/**
 * 일반 셀 타일 클래스
 */
public class NormalTile extends Tile {
    
    public NormalTile(Direction forwardDirection, Direction backwardDirection) {
		super(forwardDirection, backwardDirection);
        super.type = "CELL";
	}

	@Override
    public void onLand(Player player, GameController controller) {
        System.out.println("플레이어 " + player.getName() + "이(가) 일반 셀에 도착했습니다.");
    }
}