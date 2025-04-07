package server.model;

import common.model.Direction;

public class Cell {

	Direction fd;
	Direction bd;
	Cell fc;
	Cell bc;
	int x; //DEBUG
	int y; //DEBUG
	
	public Cell() {
		fd = Direction.NONE;
		bd = Direction.NONE;
	}
	
	public Cell(Direction fd, Direction bd) {
		this.fd = fd;
		this.bd = bd;
	}

	/**
	 * 연결된 브릿지 시작 셀을 반환합니다.
	 * 
	 * @return 연결된 셀 또는 null
	 */
	public Cell getConnectedCell() {
		// TODO Auto-generated method stub
		return null;
	}
	
	public String getType() {
		return "NORMAL";
	}
	
	public Direction getFd() {
		return fd;
	}

	public void setFd(Direction fd) {
		this.fd = fd;
	}

	public Direction getBd() {
		return bd;
	}

	public void setBd(Direction bd) {
		this.bd = bd;
	}

	public Cell getFc() {
		return fc;
	}

	public Cell getBc() {
		return bc;
	}

	public void setFc(Cell fc) {
		this.fc = fc;
	}
	
	public void setBc(Cell bc) {
		this.bc = bc;
	}

	public Cell adjacent(Direction dir) {
		if (fd.equals(dir))
			return fc;
		if (bd.equals(dir))
			return bc;
		if (getConnectedCell() != null)
			return getConnectedCell();
		return null;
	}

	public int getX() {
		return x;
	}

	public void setX(int x) {
		this.x = x;
	}

	public int getY() {
		return y;
	}

	public void setY(int y) {
		this.y = y;
	}
	
	
}
