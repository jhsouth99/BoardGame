package client.view.map;

import java.io.Serializable;
import java.util.Objects;

import common.model.Direction;

public class Position implements Serializable {
	public int x;
	public int y;

	public Position(int x, int y) {
		this.x = x;
		this.y = y;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!(obj instanceof Position))
			return false;
		Position other = (Position) obj;
		return x == other.x && y == other.y;
	}

	@Override
	public int hashCode() {
		return Objects.hash(x, y);
	}

	@Override
	public String toString() {
		return "(" + x + ", " + y + ")";
	}
	
	/**
	 * 위치를 주어진 방향으로 이동합니다.
	 */
	public Position movePosition(Direction dir) {
		switch (dir) {
		case UP:
			return new Position(x, y - 1);
		case DOWN:
			return new Position(x, y + 1);
		case LEFT:
			return new Position(x - 1, y);
		case RIGHT:
			return new Position(x + 1, y);
		default:
			return this;
		}
	}

}
