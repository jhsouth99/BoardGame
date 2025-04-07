package client.model;

import java.util.Set;

import client.controller.GameController;
import client.model.tiles.ToolTile;
import client.model.tiles.BridgeEndTile;
import client.model.tiles.BridgeStartTile;
import client.model.tiles.EventTile;
import common.model.Direction;

/**
 * 게임 보드의 기본 타일 추상 클래스
 */
public abstract class Tile {
	protected String type;
	protected Direction forwardDirection;
	protected Direction backwardDirection;
	protected Tile forwardTile;
	protected Tile backwardTile;

	public Tile(Direction forwardDirection, Direction backwardDirection) {
		this.forwardDirection = forwardDirection;
		this.backwardDirection = backwardDirection;
	}

	/**
	 * 멀리 떨어졌는데 연결된 타일
	 * 
	 * @return 없으면 null
	 */
	public Tile getConnectedTile() {
		return null;
	}

	public Set<Direction> getDirections() {
		return Set.of(forwardDirection, backwardDirection);
	}

	public String getType() {
		return type;
	}

	/**
	 * 플레이어가 타일을 지나갈 때 처리 로직
	 */
	public void onPass(Player player) {
		// 기본 구현: 아무것도 하지 않음
	}

	/**
	 * 플레이어가 타일에 도착했을 때 처리 로직
	 */
	public abstract void onLand(Player player, GameController controller);

	public void setForwardDirection(Direction forwardDirection) {
		this.forwardDirection = forwardDirection;
	}
	public void setForwardTile(Tile tile) {
		forwardTile = tile;
	}

	public void setBackwardDirection(Direction backwardDirection) {
		this.backwardDirection = backwardDirection;
	}
	public void setBackwardTile(Tile tile) {
		backwardTile = tile;
	}
	
	public Direction getForwardDirection() {
		return forwardDirection;
	}
	
	public Tile getForwardTile() {
		return forwardTile;
	}
	
	public Direction getBackwardDirection() {
		return backwardDirection;
	}
	
	public Tile getBackwardTile() {
		return backwardTile;
	}
	
	public Tile getAdjacentTile(Direction dir) {
		System.out.println("GETTYPE: " + getType());
		if (forwardDirection.equals(dir))
			return forwardTile;
		if (backwardDirection.equals(dir))
			return backwardTile;
		if (dir.equals(Direction.RIGHT) && this instanceof BridgeStartTile)
			return getConnectedTile();
		if (dir.equals(Direction.LEFT) && this instanceof BridgeEndTile)
			return getConnectedTile();
		return null;
	}
}
