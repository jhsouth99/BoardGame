package client.view.map;

import java.awt.BorderLayout;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

import client.model.Board;
import client.model.Tile;

public class MapVisualizerApp extends JDialog {
	private MapPanel mapPanel;
	private ControlPanel controlPanel;
	private SaveLoadManager saveLoadManager;
	private MapGenerator mapGenerator;
	private Board currentBoard;

	public MapVisualizerApp(JFrame owner) {
//		super("타일 맵 시각화 도구");
        super(owner, "지도 생성기", true); // modal dialog
		setSize(1000, 800);
		setLocationRelativeTo(owner);

		mapGenerator = new MapGenerator();
		mapPanel = new MapPanel();
		controlPanel = new ControlPanel(this);
		saveLoadManager = new SaveLoadManager();

		setLayout(new BorderLayout());
		add(mapPanel, BorderLayout.CENTER);
		add(controlPanel, BorderLayout.SOUTH);

		setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
	}

	// 랜덤 맵 생성
	public void generateRandomMap(int minSegmentLength, int maxSegmentLength, int segmentCount) {
		try {
			currentBoard = mapGenerator.generateMap(minSegmentLength, maxSegmentLength, segmentCount);

			// 위치 맵 변환
			Map<Position, Tile> convertedPositions = convertPositionMap(mapGenerator.getTilePositions());

			// 변환된 위치 맵으로 설정
			mapPanel.setBoard(currentBoard, convertedPositions);
			mapPanel.repaint();
			JOptionPane.showMessageDialog(this, "맵이 성공적으로 생성되었습니다!", "성공", JOptionPane.INFORMATION_MESSAGE);
		} catch (Exception e) {
			JOptionPane.showMessageDialog(this, "맵 생성 중 오류 발생: " + e.getMessage(), "오류", JOptionPane.ERROR_MESSAGE);
		}
	}

	private Map<Position, Tile> convertPositionMap(Map<MapGenerator.Position, Tile> original) {
		Map<Position, Tile> converted = new HashMap<>();

		for (Map.Entry<MapGenerator.Position, Tile> entry : original.entrySet()) {
			MapGenerator.Position origPos = entry.getKey();
			Position newPos = new Position(origPos.x, origPos.y);
			converted.put(newPos, entry.getValue());
		}

		return converted;
	}

//	// 맵 저장
//	public void saveMap(String filename) {
//		if (currentBoard != null) {
//			// 위치 맵 변환
//			Map<Position, Tile> convertedPositions = convertPositionMap(mapGenerator.getTilePositions());
//
//			boolean success = saveLoadManager.saveBoard(currentBoard, convertedPositions, filename);
//			if (success) {
//				JOptionPane.showMessageDialog(this, "맵이 성공적으로 저장되었습니다!", "성공", JOptionPane.INFORMATION_MESSAGE);
//			} else {
//				JOptionPane.showMessageDialog(this, "맵 저장 실패", "오류", JOptionPane.ERROR_MESSAGE);
//			}
//		} else {
//			JOptionPane.showMessageDialog(this, "저장할 맵이 없습니다.", "경고", JOptionPane.WARNING_MESSAGE);
//		}
//	}
//
//	// 맵 로드
//	public void loadMap(String filename) {
//		SaveLoadManager.LoadResult result = saveLoadManager.loadBoard(filename);
//		if (result != null) {
//			currentBoard = result.board;
//			mapPanel.setBoard(currentBoard, result.positions);
//			mapPanel.repaint();
//			JOptionPane.showMessageDialog(this, "맵이 성공적으로 로드되었습니다!", "성공", JOptionPane.INFORMATION_MESSAGE);
//		} else {
//			JOptionPane.showMessageDialog(this, "맵 로드 실패", "오류", JOptionPane.ERROR_MESSAGE);
//		}
//	}

	public void saveMapAsText(String filename) {
		if (currentBoard != null) {
			boolean success = saveLoadManager.saveMapToText(currentBoard, filename);
			if (success) {
				JOptionPane.showMessageDialog(this, "맵이 텍스트 형식으로 성공적으로 저장되었습니다!", "성공",
						JOptionPane.INFORMATION_MESSAGE);
			} else {
				JOptionPane.showMessageDialog(this, "텍스트 맵 저장 실패", "오류", JOptionPane.ERROR_MESSAGE);
			}
		} else {
			JOptionPane.showMessageDialog(this, "저장할 맵이 없습니다.", "경고", JOptionPane.WARNING_MESSAGE);
		}
	}

	public void loadMapFromText(String filename) {
		LoadResult result = saveLoadManager.loadMapFromText(filename);
		if (result != null) {
			currentBoard = result.board;
			mapPanel.setBoard(currentBoard, result.tileMap);
			mapPanel.repaint();
			JOptionPane.showMessageDialog(this, "맵이 텍스트 파일에서 성공적으로 로드되었습니다!", "성공", JOptionPane.INFORMATION_MESSAGE);
		} else {
			JOptionPane.showMessageDialog(this, "텍스트 맵 로드 실패", "오류", JOptionPane.ERROR_MESSAGE);
		}
	}
	
	public Board getCurrentBoard() {
		return currentBoard;
	}
    public void setCurrentBoard(Board map) {
        this.currentBoard = map;
    }

	public static void main(String[] args) {
		SwingUtilities.invokeLater(() -> {
			MapVisualizerApp app = new MapVisualizerApp(null);
			app.setVisible(true);
		});
	}
}
