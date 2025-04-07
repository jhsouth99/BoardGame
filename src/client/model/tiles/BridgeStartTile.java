package client.model.tiles;

import client.controller.GameController;
import client.model.Player;
import client.model.Tile;
import common.model.Direction;

public class BridgeStartTile extends Tile {
	public BridgeStartTile(Direction forwardDirection, Direction backwardDirection) {
		super(forwardDirection, backwardDirection);
		super.type = "BRIDGE_START";
	}

	private BridgeEndTile connectedEnd;

	public void connect(BridgeEndTile end) {
		this.connectedEnd = end;
		if (end != null) {
			end.setConnectedStart(this);
		}
	}

	public BridgeEndTile getConnectedEnd() {
		return connectedEnd;
	}

	/**
	 * 연결된 브릿지 종료 타일을 반환합니다.
	 * 
	 * @return 연결된 타일 또는 null
	 */
	@Override
	public BridgeEndTile getConnectedTile() {
		return connectedEnd;
	}

	@Override
	public void onLand(Player player, GameController controller) {
		// 모든 onLand는 밟을때 나오는것...
	}
}
