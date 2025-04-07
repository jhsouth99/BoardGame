package client.view.minigame.snake;

import java.awt.Color;
import java.awt.Graphics;

public class Snake {

	private final int[] x;
	private final int[] y;
	private int bodyParts;
	private char direction;

	public Snake(int gameUnits) { //뱀의 최대 길이, 최대 유닛 수
		x = new int[gameUnits];
		y = new int[gameUnits];
		reset(); //뱀의 위치 초기화
	}//생성자

	public void reset() {
		bodyParts = 4; //초기 뱀 길이
		direction = 'R'; //초기 방향 오른쪽

		for(int i = 0; i < bodyParts; i++) { //초기 위치 왼쪽 위
			x[i] = 100 - i * 25;
			y[i] = 100;
			//x[0]=100(머리) x[1]=75(머리왼쪽) x[2]=50 x[3]=25(꼬리)
			//(100,100) - (75,100) - (50,100) - (25,100)
			//(100,100) 시작, 왼쪽 방향으로 25(UNIT_SIZE)씩 감소하면서 몸통 배치
		}//for
	}//reset

	public void move() {
		for(int i = bodyParts; i > 0; i--) { //몸통들이 앞부분을 따라감
			x[i] = x[i-1]; //x = [100, 75, 50, 25] -> x = [100, 100, 75, 50]
			y[i] = y[i-1]; //y = [100, 100, 100, 100]
		}//for

		switch(direction) { //머리 이동
		case 'U': y[0] -= 25;
			break;
		case 'D': y[0] += 25;
			break;
		case 'L': x[0] -= 25;
			break;
		case 'R': x[0] += 25;
			break;
		}//switch
	}//move

	public void grow() {
		bodyParts++;
	}
	
	public void draw(Graphics g, int unitSize) {
		//머리
		g.setColor(Color.GREEN);
		g.fillRect(x[0], y[0], unitSize, unitSize);
		
		//몸통
		for(int i = 1; i < bodyParts; i++) {
			g.setColor(new Color(45, 180, 0));
			g.fillRect(x[i], y[i], unitSize, unitSize);
		}//for
	}//draw



	public int getBodyParts() {
		return bodyParts;
	}
	public void setBodyParts(int bodyParts) {
		this.bodyParts = bodyParts;
	}
	public char getDirection() {
		return direction;
	}
	public void setDirection(char direction) {
		this.direction = direction;
	}
	public int[] getX() {
		return x;
	}
	public int[] getY() {
		return y;
	}

}//snake
