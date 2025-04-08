package client.view.minigame.rhythm;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.util.concurrent.CountDownLatch;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

// 메인 게임 프레임과 UI 초기화, 게임 결과 표시 담당
public class RhythmGame extends JFrame {
	
    private static final int WIDTH = 600;
    private static final int HEIGHT = 500;
    private static final int LANE_COUNT = 4;
    private static final int NOTE_SPEED = 20;
    private static final int GAME_TIME = 20;
    
    private GamePanel gamePanel;
    private GameManager gameManager;
    private JLabel scoreLabel;
    private JLabel comboLabel;
    private JLabel timeLabel;
	private CountDownLatch latch;
	private int score;
    
    public RhythmGame(CountDownLatch latch) {
		this.latch = latch;
        // 기본 창 설정
        setTitle("리듬스타");
        setSize(WIDTH, HEIGHT);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);
        setLocationRelativeTo(null);
        
        // 게임 매니저 초기화
        gameManager = new GameManager(LANE_COUNT, NOTE_SPEED, HEIGHT, GAME_TIME);
        
        // UI 컴포넌트 초기화
        initializeUI();
        
        // 키 리스너 추가
        addKeyListener(new KeyHandler(gameManager, this));
        setFocusable(true);
        
        // 타이머 시작
        gameManager.startGame();
    }
    
    private void initializeUI() {
        // 레이블 초기화
        scoreLabel = new JLabel("Score: 0/" + gameManager.getMaxScore());
        scoreLabel.setForeground(Color.WHITE);
        scoreLabel.setFont(new Font("Arial", Font.BOLD, 16));
        
        comboLabel = new JLabel("Combo: 0");
        comboLabel.setForeground(Color.WHITE);
        comboLabel.setFont(new Font("Arial", Font.BOLD, 16));
        
        timeLabel = new JLabel("Time: " + gameManager.getRemainingTime() + "s");
        timeLabel.setForeground(Color.WHITE);
        timeLabel.setFont(new Font("Arial", Font.BOLD, 16));
        
        // 게임 패널 초기화
        gamePanel = new GamePanel(gameManager);
        
        // 레이아웃 설정
        setLayout(new BorderLayout());
        JPanel topPanel = new JPanel();
        topPanel.setBackground(Color.BLACK);
        topPanel.add(scoreLabel);
        topPanel.add(comboLabel);
        topPanel.add(timeLabel);
        
        add(topPanel, BorderLayout.NORTH);
        add(gamePanel, BorderLayout.CENTER);
        
        // 게임 매니저에 UI 요소 전달
        gameManager.setUIComponents(scoreLabel, comboLabel, timeLabel, gamePanel);
    }
    
    public void showGameOver() {
        // 결과 메시지 생성
        StringBuilder result = new StringBuilder();
        result.append("게임 종료!\n");
        result.append("최종 점수: ").append(gameManager.getScore()).append("/").append(gameManager.getMaxScore()).append("\n");
        result.append("최대 콤보: ").append(gameManager.getMaxCombo()).append("\n\n");
        result.append("PERFECT: ").append(gameManager.getPerfectCount()).append("번 (").append(gameManager.getPerfectCount() * 2).append("점)\n");
        result.append("GOOD: ").append(gameManager.getGoodCount()).append("번 (").append(gameManager.getGoodCount()).append("점)\n");
        result.append("MISS: ").append(gameManager.getMissCount()).append("번 (0점)\n");
        
        // 등급 계산
        String grade;
        double percentage = (double)gameManager.getScore() / gameManager.getMaxScore() * 100;
        if (percentage >= 95) grade = "SSS";
        else if (percentage >= 90) grade = "SS";
        else if (percentage >= 80) grade = "S";
        else if (percentage >= 70) grade = "A";
        else if (percentage >= 60) grade = "B";
        else if (percentage >= 50) grade = "C";
        else grade = "D";
        
        result.append("\n등급: ").append(grade);
        
        JOptionPane.showMessageDialog(this, result.toString(), "Game Over", JOptionPane.INFORMATION_MESSAGE);
        gameManager.dispose();
        score = gameManager.getScore();
		latch.countDown(); // latch 해제!
    }

    public static void main(String[] args) {
    	CountDownLatch latch = new CountDownLatch(1);
        SwingUtilities.invokeLater(() -> {
            try {
                RhythmGame game = new RhythmGame(latch);
                game.setVisible(true);
				latch.await();
	            System.out.println(game.getScore());
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
        });
    }

	public int getScore() {
		return score;
	}
}
