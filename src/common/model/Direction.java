package common.model;

public enum Direction {
	UP('U'), DOWN('D'), LEFT('L'), RIGHT('R'), NONE('N');

	private final char code;

	Direction(char c) {
		code = c;
	}

    public static Direction fromChar(char c) {
        switch (Character.toUpperCase(c)) {
        case 'U':
            return UP;
        case 'D':
            return DOWN;
        case 'L':
            return LEFT;
        case 'R':
            return RIGHT;
        case 'N':
            return NONE;
        default:
            throw new IllegalArgumentException("Invalid direction: " + c);
        }
    }

	/**
	 * 현재 방향의 반대 방향을 반환합니다.
	 * 
	 * @return 반대 방향
	 */
    public Direction getOpposite() {
        switch (this) {
        case UP:
            return DOWN;
        case DOWN:
            return UP;
        case LEFT:
            return RIGHT;
        case RIGHT:
            return LEFT;
        case NONE:
            return NONE;
        default:
            throw new IllegalStateException("Unexpected value: " + this);
        }
    }

	public char getCode() {
		return code;
	}
}
