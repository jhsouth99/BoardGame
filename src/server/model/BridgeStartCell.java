package server.model;

import common.model.Direction;

public class BridgeStartCell extends Cell {
	
	public BridgeStartCell(Direction fd, Direction bd) {
		super(fd, bd);
	}

	private BridgeEndCell connectedEnd;

	public void connect(BridgeEndCell end) {
		this.connectedEnd = end;
		if (end != null) {
			end.setConnectedStart(this);
		}
	}

	public BridgeEndCell getConnectedEnd() {
		return connectedEnd;
	}

	/**
	 * 연결된 브릿지 종료 타일을 반환합니다.
	 * 
	 * @return 연결된 타일 또는 null
	 */
	@Override
	public Cell getConnectedCell() {
		return connectedEnd;
	}
	
	@Override
	public String getType() {
		return "BRIDGE_START";
	}
}
