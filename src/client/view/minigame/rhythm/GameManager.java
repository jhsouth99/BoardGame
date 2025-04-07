package client.view.minigame.rhythm;

import java.awt.event.ActionEvent; 
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Random;
import javax.swing.*;

public class GameManager {
    private int laneCount;
    private int noteSpeed;
    private int screenHeight;
    private int gameTime;
    
    private ArrayList<Note> notes = new ArrayList<>();
    private ArrayList<JudgementEffect> judgementEffects = new ArrayList<>();
    
    private int score = 0;
    private int combo = 0;
    private int maxCombo = 0;
    private int perfectCount = 0;
    private int goodCount = 0;
    private int missCount = 0;
    private int maxScore = 64;
    
    private int remainingTime;
    private boolean gameRunning = true;
    private boolean[] keyPressed;
    private long[] lastKeyPressTime;
    
    private Random random = new Random();
    private Timer gameTimer;
    private Timer countdownTimer;
    
    private JLabel scoreLabel;
    private JLabel comboLabel;
    private JLabel timeLabel;
    private GamePanel gamePanel;
    
    private SoundManager soundManager;
    private RhythmGame gameFrame;
    
    public GameManager(int laneCount, int noteSpeed, int screenHeight, int gameTime) {
        this.laneCount = laneCount;
        this.noteSpeed = noteSpeed;
        this.screenHeight = screenHeight;
        this.gameTime = gameTime;
        this.remainingTime = gameTime;
        
        this.keyPressed = new boolean[laneCount];
        this.lastKeyPressTime = new long[laneCount];
        
        this.soundManager = new SoundManager();
    }
    
    public void setUIComponents(JLabel scoreLabel, JLabel comboLabel, JLabel timeLabel, GamePanel gamePanel) {
        this.scoreLabel = scoreLabel;
        this.comboLabel = comboLabel;
        this.timeLabel = timeLabel;
        this.gamePanel = gamePanel;
    }
    
    public void setGameFrame(RhythmGame gameFrame) {
        this.gameFrame = gameFrame;
    }
    
    public void startGame() {
        // 게임 타이머 설정
        gameTimer = new Timer(16, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (gameRunning) {
                    updateGame();
                    gamePanel.repaint();
                    
                    // 랜덤하게 노트 생성
                    if (random.nextInt(100) < 5) {
                        createNote();
                    }
                }
            }
        });
        
        // 카운트다운 타이머 설정
        countdownTimer = new Timer(1000, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                remainingTime--;
                timeLabel.setText("Time: " + remainingTime + "s");
                
                if (remainingTime <= 0) {
                    gameRunning = false;
                    countdownTimer.stop();
                    gameTimer.stop();
                    gameFrame.showGameOver();
                }
            }
        });
        
        gameTimer.start();
        countdownTimer.start();
    }
    
    public void createNote() {
        int lane = random.nextInt(laneCount);
        notes.add(new Note(lane, 0));
    }
    
    public void updateGame() {
        // 노트 업데이트 및 화면 밖으로 나간 노트 제거
        ArrayList<Note> notesToRemove = new ArrayList<>();
        
        for (Note note : notes) {
            note.y += noteSpeed;
            
            // 노트가 판정선을 지났는지 확인
            if (note.y > screenHeight - 50 && !note.isHit) {
                note.isMissed = true;
                notesToRemove.add(note);
                combo = 0;
                missCount++;
                comboLabel.setText("Combo: " + combo);
                soundManager.playMissSound();
                
                // 미스 이펙트 추가
                addJudgementEffect("MISS", note.lane);
            }
        }
        
        notes.removeAll(notesToRemove);
        
        // 판정 이펙트 업데이트
        ArrayList<JudgementEffect> effectsToRemove = new ArrayList<>();
        for (JudgementEffect effect : judgementEffects) {
            effect.duration--;
            if (effect.duration <= 0) {
                effectsToRemove.add(effect);
            }
        }
        judgementEffects.removeAll(effectsToRemove);
    }
    
    public void checkNoteHit(int lane) {
        for (Note note : notes) {
            if (note.lane == lane && !note.isHit && !note.isMissed) {
                int hitY = note.y;
                
                // 퍼펙트 판정 (±34픽셀)
                if (Math.abs(hitY - (screenHeight - 100)) < 34) {
                    note.isHit = true;
                    score += 2; // 퍼펙트는 2점
                    perfectCount++;
                    combo++;
                    if (combo > maxCombo) {
                        maxCombo = combo;
                    }
                    
                    scoreLabel.setText("Score: " + score + "/" + maxScore);
                    comboLabel.setText("Combo: " + combo);
                    soundManager.playHitSound();
                    
                    // 퍼펙트 이펙트 추가
                    addJudgementEffect("PERFECT", lane);
                    return;
                }
                // 굿 판정 (±68픽셀)
                else if (Math.abs(hitY - (screenHeight - 100)) < 68) {
                    note.isHit = true;
                    score += 1; // 굿은 1점
                    goodCount++;
                    combo++;
                    if (combo > maxCombo) {
                        maxCombo = combo;
                    }
                    
                    scoreLabel.setText("Score: " + score + "/" + maxScore);
                    comboLabel.setText("Combo: " + combo);
                    soundManager.playHitSound();
                    
                    // 굿 이펙트 추가
                    addJudgementEffect("GOOD", lane);
                    return;
                }
            }
        }
    }
    
    public void addJudgementEffect(String judgement, int lane) {
        judgementEffects.add(new JudgementEffect(judgement, lane, 10));
    }
    
    public void setKeyPressed(int lane, boolean pressed) {
        keyPressed[lane] = pressed;
        if (pressed) {
            lastKeyPressTime[lane] = System.currentTimeMillis();
            checkNoteHit(lane);
        }
    }
    
    public void stopGame() {
        gameRunning = false;
        countdownTimer.stop();
        gameTimer.stop();
        gameFrame.showGameOver();
    }
    
    // 게터 메소드들
    public ArrayList<Note> getNotes() {
        return notes;
    }
    
    public ArrayList<JudgementEffect> getJudgementEffects() {
        return judgementEffects;
    }
    
    public boolean[] getKeyPressed() {
        return keyPressed;
    }
    
    public int getScore() {
        return score;
    }
    
    public int getMaxScore() {
        return maxScore;
    }
    
    public int getMaxCombo() {
        return maxCombo;
    }
    
    public int getPerfectCount() {
        return perfectCount;
    }
    
    public int getGoodCount() {
        return goodCount;
    }
    
    public int getMissCount() {
        return missCount;
    }
    
    public int getRemainingTime() {
        return remainingTime;
    }
    
    public boolean isGameRunning() {
        return gameRunning;
    }
    
    public void dispose() {
    	gameFrame.dispose();
    }
}

