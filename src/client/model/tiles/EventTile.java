package client.model.tiles;

import client.controller.GameController;
import client.model.Player;
import client.view.minigame.MiniGameManager;
import common.model.Direction;

public class EventTile extends NormalTile {
	public EventTile(Direction forwardDirection, Direction backwardDirection, String miniGameType) {
		super(forwardDirection, backwardDirection);
		super.type = "EVENT";
		this.miniGameType = miniGameType;
	}

	private String miniGameType; // 미니게임 종류 식별자

	public String getMiniGameType() {
		return miniGameType;
	}

	@Override
	public void onLand(Player player, GameController controller) {
		if (player.getRoom().isEnding()) {
			controller.notifyEventResult(0);
	    }
		int res = MiniGameManager.getInstance().startMiniGame(player, miniGameType);
		controller.notifyEventResult(res);
	}
}
