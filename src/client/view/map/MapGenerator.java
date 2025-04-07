package client.view.map;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Queue;
import java.util.Random;
import java.util.Set;

import client.model.Board;
import client.model.Tile;
import client.model.tiles.BridgeEndTile;
import client.model.tiles.BridgeStartTile;
import client.model.tiles.EndTile;
import client.model.tiles.EventTile;
import client.model.tiles.NormalTile;
import client.model.tiles.StartTile;
import common.model.Direction;
import client.model.tiles.ToolTile;

/**
 * 브릿지 게임 맵 생성기
 */
public class MapGenerator {
	private Random random = new Random();
	private List<Tile> tiles = new ArrayList<>();
	private Map<Position, Tile> tilePositions = new HashMap<>();
	private Position startPosition;
	private Position endPosition;
	private int maxWidth = 0;
	private int maxHeight = 0;
	private int minHeight = 0;

	// 브릿지, 아이템, 이벤트 타일 확률
	private static final double BRIDGE_TILE_RATIO = 0.15;
	private static final double TOOL_TILE_RATIO = 0.2;
	private static final double EVENT_TILE_RATIO = 0.15;

	// 미니게임 종류
	private static final String[] miniGameTypes = { "SNAKE", "MEMORY", "RSP", "RHYTHM" };

	/**
	 * 지그재그 형태의 맵을 생성합니다.
	 * 
	 * @param minSegmentLength 한줄의 최소 길이
	 * @param maxSegmentLength 한줄의 최대 길이
	 * @param segmentCount     줄의 개수
	 * @return 생성된 게임 보드
	 */
	public Board generateMap(int minSegmentLength, int maxSegmentLength, int segmentCount) {
		// 맵 관련 변수 초기화
		tiles.clear();
		tilePositions.clear();
		maxWidth = 0;
		maxHeight = 0;
		minHeight = 0;

		// 1. 일반타일로 맵 경로 생성
		// 2. 맵 경로 검증
		// 3. 브릿지 타일 배치
		// 4. 브릿지 타일 검증
		// 5. 아이템 타일 배치
		// 6. 이벤트 타일 배치

		createBasePath(minSegmentLength, maxSegmentLength, segmentCount);
		validatePath();
		placeBridges();
		validateBridges();
		placeToolTiles();
		placeEventTiles();

		return new Board(tiles);
	}

	/**
	 * 기본 맵 경로를 생성합니다. 지그재그 형태로 확장됩니다.
	 */
	private void createBasePath(int minSegmentLength, int maxSegmentLength, int segmentCount) {
		startPosition = new Position(0, 0);
		Position currentPosition = startPosition;

		StartTile startTile = new StartTile(Direction.DOWN);
		tiles.add(startTile);
		tilePositions.put(currentPosition, startTile);

		// 현재 방향
		Direction direction = Direction.RIGHT;

		boolean goingDown = true;
		for (int segment = 0; segment < segmentCount; segment++) {
			// 세그먼트 길이 결정 (랜덤)
			int segmentLength = minSegmentLength + random.nextInt(maxSegmentLength - minSegmentLength + 1);

			Direction verticalDirection = goingDown ? Direction.DOWN : Direction.UP;
			Tile previousTile = tilePositions.get(currentPosition);
			previousTile.setForwardDirection(verticalDirection);

			// 세그먼트 생성 (수직 방향)
			for (int i = 0; i < segmentLength; i++) {
				currentPosition = movePosition(currentPosition, verticalDirection);
				updateMapBoundaries(currentPosition);

				// 새 타일 생성 및 방향 설정
				Direction forwardDirection = verticalDirection;
				Direction backwardDirection = verticalDirection.getOpposite();

				// 마지막 타일이면 현재 방향 제거 (끝 타일이므로)
				if (i == segmentLength - 1) {
					forwardDirection = direction;
				}

				NormalTile tile = new NormalTile(forwardDirection, backwardDirection);
				tile.setBackwardTile(previousTile);
				previousTile.setForwardTile(tile);
				tiles.add(tile);
				tilePositions.put(currentPosition, tile);
				previousTile = tile;
			}

			// 다음 세그먼트가 있다면 우측으로 이동
			if (segment < segmentCount - 1) {
				// 우측으로 한 칸씩 이동하면서 타일 생성 (중간 타일 추가)
				Position prevPos = currentPosition;

				// 첫 번째 이동 (중간 타일)
				currentPosition = movePosition(currentPosition, direction);
				updateMapBoundaries(currentPosition);

				// 중간 타일 생성
				Set<Direction> midDirections = EnumSet.of(Direction.LEFT, Direction.RIGHT);
				NormalTile midTile = new NormalTile(direction, direction.getOpposite());
				midTile.setBackwardTile(previousTile);
				previousTile.setForwardTile(midTile);
				tiles.add(midTile);
				tilePositions.put(currentPosition, midTile);

				// 두 번째 이동
				prevPos = currentPosition;
				currentPosition = movePosition(currentPosition, direction);
				updateMapBoundaries(currentPosition);

				// 다음 세그먼트의 첫 타일 생성
				NormalTile tile = new NormalTile(null, direction.getOpposite());
				tile.setBackwardTile(midTile);
				midTile.setForwardTile(tile);
				tiles.add(tile);
				tilePositions.put(currentPosition, tile);
				previousTile = tile;

				// 다음 세그먼트의 방향 전환
				goingDown = !goingDown;
			}
		}

		// 마지막 타일을 종료 타일로 설정
		endPosition = currentPosition;
		Tile lastTile = tilePositions.get(endPosition);
		int lastIndex = tiles.indexOf(lastTile);
		EndTile endTile = new EndTile(lastTile.getBackwardDirection());
		tiles.set(lastIndex, endTile);
		tilePositions.put(endPosition, endTile);

		// 디버깅: 맵 경로 출력
		System.out.println("생성된 맵 경로:");
		System.out.println("타일 개수: " + tiles.size());
		System.out.println("시작 위치: " + startPosition);
		System.out.println("종료 위치: " + endPosition);
	}

	/**
	 * 맵 경로가 유효한지 검증합니다. 시작에서 끝까지 이동 가능해야 합니다.
	 */
	private void validatePath() {
		// BFS를 사용하여 시작에서 끝까지 도달 가능한지 확인
		Set<Position> visited = new HashSet<>();
		Queue<Position> queue = new LinkedList<>();

		queue.add(startPosition);
		visited.add(startPosition);

		// 디버깅을 위한 방문 경로 추적
		StringBuilder pathLog = new StringBuilder("BFS 경로: ");
		pathLog.append(startPosition.toString());

		while (!queue.isEmpty()) {
			Position current = queue.poll();
			Tile currentTile = tilePositions.get(current);

			// 각 방향으로 이동 가능한지 확인
			for (Direction dir : currentTile.getDirections()) {
				Position next = movePosition(current, dir);

				// 다음 위치에 타일이 있고 아직 방문하지 않았다면 큐에 추가
				if (tilePositions.containsKey(next) && !visited.contains(next)) {
					// 다음 타일이 현재 위치로 돌아올 수 있는지 확인 (양방향 연결 확인)
					Tile nextTile = tilePositions.get(next);
					if (nextTile.getDirections().contains(dir.getOpposite())) {
						queue.add(next);
						visited.add(next);
						pathLog.append(" -> ").append(next.toString());
					} else {
						System.out.println("경고: 위치 " + next + "에 역방향 연결이 없습니다.");
					}
				}
			}
		}

		// 디버깅 정보 출력
		System.out.println(pathLog.toString());
		System.out.println("방문한 타일 수: " + visited.size() + " / 전체 타일 수: " + tilePositions.size());

		// 종료 지점에 도달할 수 없다면 예외 발생
		if (!visited.contains(endPosition)) {
			System.out.println("오류: 종료 지점(" + endPosition + ")에 도달할 수 없습니다.");
			System.out.println("타일 방향 정보:");
			for (Map.Entry<Position, Tile> entry : tilePositions.entrySet()) {
				System.out
						.println("위치 " + entry.getKey() + ": " + directionsToString(entry.getValue().getDirections()));
			}
			throw new IllegalStateException("Invalid map: Cannot reach the end from the start");
		}
	}

	/**
	 * 방향 집합을 문자열로 변환
	 */
	private String directionsToString(Set<Direction> directions) {
		StringBuilder sb = new StringBuilder("[");
		for (Direction dir : directions) {
			sb.append(dir.name()).append(", ");
		}
		if (!directions.isEmpty()) {
			sb.setLength(sb.length() - 2);
		}
		sb.append("]");
		return sb.toString();
	}

	/**
	 * 브릿지 타일을 배치합니다.
	 */
	private void placeBridges() {
		List<Position> potentialBridgeStarts = new ArrayList<>();

		// 브릿지 후보 제작
		for (Position pos : tilePositions.keySet()) {
			Position midPos = new Position(pos.x + 1, pos.y);
			Position rightPos = new Position(pos.x + 2, pos.y);

			// 조건
			// 1. 중간에 타일이 없어야 함 (빈 공간)
			// 2. 시작점에서 오른쪽 두 칸에 타일이 있어야 함
			// 3. 시작과 끝 타일은 일반 타일이어야 함
			if (!tilePositions.containsKey(midPos) && tilePositions.containsKey(rightPos)
					&& tilePositions.get(pos) instanceof NormalTile && tilePositions.get(rightPos) instanceof NormalTile
					&& random.nextDouble() < BRIDGE_TILE_RATIO) {

				potentialBridgeStarts.add(pos);
			}
		}

		// 사용된 위치를 추적하는 집합
		Set<Position> usedPositions = new HashSet<>();

		for (Position startPos : potentialBridgeStarts) {
			Position endPos = new Position(startPos.x + 2, startPos.y);

			// 이미 사용된 위치면 스킵
			if (usedPositions.contains(startPos) || usedPositions.contains(endPos)) {
				continue;
			}

			// 사용된 위치로 표시
			usedPositions.add(startPos);
			usedPositions.add(endPos);

			// 기존 타일 정보 가져오기
			Tile startTile = tilePositions.get(startPos);
			Tile endTile = tilePositions.get(endPos);

			// 원래 방향 정보 복사
			Direction startForwardDirection = startTile.getForwardDirection();
			Direction startBackwardDirection = startTile.getBackwardDirection();
			Direction endForwardDirection = endTile.getForwardDirection();
			Direction endBackwardDirection = endTile.getBackwardDirection();

			// 브릿지 타일 생성
			BridgeStartTile bridgeStart = new BridgeStartTile(startForwardDirection, startBackwardDirection);
			BridgeEndTile bridgeEnd = new BridgeEndTile(endForwardDirection, endBackwardDirection);

			// 브릿지 연결 설정
			bridgeStart.connect(bridgeEnd);

			// 참조 정보 설정
			bridgeStart.setForwardTile(startTile.getForwardTile());
			bridgeStart.getForwardTile().setBackwardTile(bridgeStart);
			bridgeStart.setBackwardTile(startTile.getBackwardTile());
			bridgeStart.getBackwardTile().setForwardTile(bridgeStart);
			bridgeEnd.setForwardTile(endTile.getForwardTile());
			bridgeEnd.getForwardTile().setBackwardTile(bridgeEnd);
			bridgeEnd.setBackwardTile(endTile.getBackwardTile());
			bridgeEnd.getBackwardTile().setForwardTile(bridgeEnd);

			// tiles 리스트 업데이트
			int startIndex = tiles.indexOf(startTile);
			int endIndex = tiles.indexOf(endTile);
			if (startIndex >= 0) {
				tiles.set(startIndex, bridgeStart);
			}
			if (endIndex >= 0) {
				tiles.set(endIndex, bridgeEnd);
			}

			// 위치 맵 업데이트
			tilePositions.put(startPos, bridgeStart);
			tilePositions.put(endPos, bridgeEnd);
		}
	}

	/**
	 * 브릿지 타일의 연결 상태를 검증합니다.
	 */
	private void validateBridges() {
		for (Tile tile : tiles) {
			if (tile instanceof BridgeStartTile) {
				BridgeStartTile start = (BridgeStartTile) tile;
				// 연결된 엔드 타일이 없으면 경고
				if (start.getConnectedEnd() == null) {
					System.out.println("경고: 브릿지 시작 타일이 종료 타일과 연결되어 있지 않습니다.");
				}
			} else if (tile instanceof BridgeEndTile) {
				BridgeEndTile end = (BridgeEndTile) tile;
				// 연결된 시작 타일이 없으면 경고
				if (end.getConnectedStart() == null) {
					System.out.println("경고: 브릿지 종료 타일이 시작 타일과 연결되어 있지 않습니다.");
				}
			}
		}
	}

	public Map<Position, Tile> getTilePositions() {
		return new HashMap<>(tilePositions);
	}

	/**
	 * 아이템 타일을 배치합니다.
	 */
	private void placeToolTiles() {
		// 일반 타일 중에서 랜덤하게 선택해 아이템 타일로 변경
		List<Integer> normalTileIndices = new ArrayList<>();

		// 일반 타일 찾기
		for (int i = 0; i < tiles.size(); i++) {
			if (tiles.get(i) instanceof NormalTile && !(tiles.get(i) instanceof ToolTile)
					&& !(tiles.get(i) instanceof BridgeStartTile) && !(tiles.get(i) instanceof BridgeEndTile)) {
				normalTileIndices.add(i);
			}
		}

		// 변경할 타일 수 계산
		int numToolTiles = (int) (normalTileIndices.size() * TOOL_TILE_RATIO);

		for (int i = 0; i < numToolTiles && !normalTileIndices.isEmpty(); i++) {
			int index = random.nextInt(normalTileIndices.size());
			int tileIndex = normalTileIndices.get(index);
			normalTileIndices.remove(index);

			NormalTile normalTile = (NormalTile) tiles.get(tileIndex);

			String[] toolTypes = { "P", "H", "S" };
			String toolType = toolTypes[random.nextInt(toolTypes.length)];

			ToolTile toolTile = new ToolTile(normalTile.getForwardDirection(), normalTile.getBackwardDirection(),
					toolType);

			// 참조 정보 설정
			toolTile.setForwardTile(normalTile.getForwardTile());
			toolTile.getForwardTile().setBackwardTile(toolTile);
			toolTile.setBackwardTile(normalTile.getBackwardTile());
			toolTile.getBackwardTile().setForwardTile(toolTile);

			tiles.set(tileIndex, toolTile);

			for (Map.Entry<Position, Tile> entry : tilePositions.entrySet()) {
				if (entry.getValue() == normalTile) {
					tilePositions.put(entry.getKey(), toolTile);
					break;
				}
			}
		}
	}

	private void placeEventTiles() {
		List<Integer> normalTileIndices = new ArrayList<>();

		// 일반 타일 찾기
		for (int i = 0; i < tiles.size(); i++) {
			Tile tile = tiles.get(i);
			if (tile instanceof NormalTile && !(tile instanceof ToolTile) && !(tile instanceof BridgeStartTile)
					&& !(tile instanceof BridgeEndTile) && !(tile instanceof StartTile) && !(tile instanceof EndTile)) {

				normalTileIndices.add(i);
			}
		}

		// 변경할 타일 수 계산
		int numEventTiles = (int) (normalTileIndices.size() * EVENT_TILE_RATIO);

		// 랜덤하게 선택하여 이벤트 타일로 변경
		for (int i = 0; i < numEventTiles && !normalTileIndices.isEmpty(); i++) {
			int index = random.nextInt(normalTileIndices.size());
			int tileIndex = normalTileIndices.get(index);
			normalTileIndices.remove(index);

			// 기존 타일 정보 가져오기
			NormalTile normalTile = (NormalTile) tiles.get(tileIndex);

			// 미니게임 타입 랜덤 선택
			String miniGameType = miniGameTypes[random.nextInt(miniGameTypes.length)];

			// 이벤트 타일 생성 및 교체
			EventTile eventTile = new EventTile(normalTile.getForwardDirection(), normalTile.getBackwardDirection(),
					miniGameType);

			// 참조 정보 설정
			eventTile.setForwardTile(normalTile.getForwardTile());
			eventTile.getForwardTile().setBackwardTile(eventTile);
			eventTile.setBackwardTile(normalTile.getBackwardTile());
			eventTile.getBackwardTile().setForwardTile(eventTile);

			tiles.set(tileIndex, eventTile);

			// 위치 맵도 업데이트
			for (Map.Entry<Position, Tile> entry : tilePositions.entrySet()) {
				if (entry.getValue() == normalTile) {
					tilePositions.put(entry.getKey(), eventTile);
					break;
				}
			}
		}
	}

	/**
	 * 위치를 주어진 방향으로 이동합니다.
	 */
	private Position movePosition(Position pos, Direction dir) {
		switch (dir) {
		case UP:
			return new Position(pos.x, pos.y - 1);
		case DOWN:
			return new Position(pos.x, pos.y + 1);
		case LEFT:
			return new Position(pos.x - 1, pos.y);
		case RIGHT:
			return new Position(pos.x + 1, pos.y);
		default:
			return pos;
		}
	}

	/**
	 * 맵 경계를 업데이트합니다.
	 */
	private void updateMapBoundaries(Position position) {
		maxWidth = Math.max(maxWidth, position.x);
		maxHeight = Math.max(maxHeight, position.y);
		minHeight = Math.min(minHeight, position.y);
	}

	public static class Position {
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
	}
}
