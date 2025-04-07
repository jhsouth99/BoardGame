package client.view.minigame;

import java.util.concurrent.CountDownLatch;

import client.model.Player;
import client.view.minigame.memory.MatchCards;
import client.view.minigame.rhythm.RhythmGame;
import client.view.minigame.rsp.Rsp;
import client.view.minigame.snake.SnakeMain;

public class MiniGameManager {
	private static MiniGameManager instance;

	private MiniGameManager() {
	}

	public static MiniGameManager getInstance() {
		if (instance == null) {
			instance = new MiniGameManager();
		}
		return instance;
	}

	// 미니게임 실행 메서드
	public int startMiniGame(Player player, String miniGameType) {
		System.out.println(player.getName() + miniGameType);
		CountDownLatch latch = new CountDownLatch(1);

		System.out.println("미니게임 할까?: " + miniGameType);
		int score = 0;
		switch (miniGameType) {
		case "RHYTHM":
			try {
				RhythmGame rg = new RhythmGame(latch);
				rg.setVisible(true);
				rg.setVisible(true);
				latch.await();
				score = rg.getScore();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			break;
		case "MEMORY":
			try {
				MatchCards mg = new MatchCards(latch);
				mg.run();
				latch.await();
				score = mg.getScore();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			break;
		case "SNAKE":
			try {
				SnakeMain sg = new SnakeMain(latch);
				sg.run();
				latch.await();
				score = sg.getScore();
				System.out.println("점수:" + score);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			break;
		case "RSP":
			try {
				Rsp r = new Rsp(latch);
				latch.await();
				score = r.getScore();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		awardPoints(player, score);
		return score;
	}

	// 점수 부여 메서드
	private void awardPoints(Player player, int score) {
		player.addScore(score);
		System.out.println(player.getName() + "님이 미니게임에서 " + score + "점 획득");
	}
}
