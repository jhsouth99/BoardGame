package client.model.tiles;

import client.controller.GameController;
import client.model.Player;
import client.model.Tile;
import common.model.Direction;

public class BridgeEndTile extends Tile {
	public BridgeEndTile(Direction forwardDirection, Direction backwardDirection) {
		super(forwardDirection, backwardDirection);
		super.type = "BRIDGE_END";
	}

	private BridgeStartTile connectedStart;

	public void setConnectedStart(BridgeStartTile start) {
		this.connectedStart = start;
	}

	public BridgeStartTile getConnectedStart() {
		return connectedStart;
	}

	/**
	 * 연결된 브릿지 시작 타일을 반환합니다.
	 * 
	 * @return 연결된 타일 또는 null
	 */
	@Override
	public Tile getConnectedTile() {
		return connectedStart;
	}

	@Override
	public void onLand(Player player, GameController controller) {
	}
}
