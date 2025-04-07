package common.model;

public enum Direction {
	UP('U'), DOWN('D'), LEFT('L'), RIGHT('R'), NONE('N');

	private final char code;

	Direction(char c) {
		code = c;
	}

	public static Direction fromChar(char c) {
		return switch (Character.toUpperCase(c)) {
		case 'U' -> UP;
		case 'D' -> DOWN;
		case 'L' -> LEFT;
		case 'R' -> RIGHT;
		case 'N' -> NONE;
		default -> throw new IllegalArgumentException("Invalid direction: " + c);
		};
	}

	/**
	 * 현재 방향의 반대 방향을 반환합니다.
	 * 
	 * @return 반대 방향
	 */
	public Direction getOpposite() {
		return switch (this) {
		case UP -> DOWN;
		case DOWN -> UP;
		case LEFT -> RIGHT;
		case RIGHT -> LEFT;
		case NONE -> NONE;
		};
	}

	public char getCode() {
		return code;
	}
}
