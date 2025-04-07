package server.model;

import common.model.Direction;

public class BridgeEndCell extends Cell {

	public BridgeEndCell(Direction fd, Direction bd) {
		super(fd, bd);
	}

	private BridgeStartCell connectedStart;

	public void setConnectedStart(BridgeStartCell start) {
		this.connectedStart = start;
	}

	public BridgeStartCell getConnectedStart() {
		return connectedStart;
	}

	/**
	 * 연결된 브릿지 시작 타일을 반환합니다.
	 * 
	 * @return 연결된 타일 또는 null
	 */
	@Override
	public Cell getConnectedCell() {
		return connectedStart;
	}
	
	@Override
	public String getType() {
		return "BRIDGE_END";
	}
}
