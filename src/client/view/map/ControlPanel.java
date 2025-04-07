package client.view.map;

import java.awt.FlowLayout;
import java.awt.GridLayout;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;

public class ControlPanel extends JPanel {
	private MapVisualizerApp app;

	private JSpinner minSegmentSpinner;
	private JSpinner maxSegmentSpinner;
	private JSpinner segmentCountSpinner;

	public ControlPanel(MapVisualizerApp app) {
		this.app = app;
		setupUI();
	}

	private void setupUI() {
		setLayout(new FlowLayout(FlowLayout.CENTER, 10, 5));
		setBorder(BorderFactory.createTitledBorder("맵 컨트롤"));

		// 세그먼트 길이 및 개수 설정
		JPanel settingsPanel = new JPanel(new GridLayout(3, 2, 5, 5));

		settingsPanel.add(new JLabel("최소 세그먼트 길이:"));
		minSegmentSpinner = new JSpinner(new SpinnerNumberModel(2, 1, 10, 1));
		settingsPanel.add(minSegmentSpinner);

		settingsPanel.add(new JLabel("최대 세그먼트 길이:"));
		maxSegmentSpinner = new JSpinner(new SpinnerNumberModel(5, 1, 15, 1));
		settingsPanel.add(maxSegmentSpinner);

		settingsPanel.add(new JLabel("세그먼트 개수:"));
		segmentCountSpinner = new JSpinner(new SpinnerNumberModel(3, 1, 10, 1));
		settingsPanel.add(segmentCountSpinner);

		add(settingsPanel);

		// 버튼 패널
		JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));

		// 맵 생성 버튼
		JButton generateButton = new JButton("맵 생성");
		generateButton.addActionListener(e -> generateMap());
		buttonPanel.add(generateButton);

		JButton saveTextButton = new JButton("세이브");
		saveTextButton.addActionListener(e -> saveMapAsText());
		buttonPanel.add(saveTextButton);

		JButton loadTextButton = new JButton("로드");
		loadTextButton.addActionListener(e -> loadMapFromText());
		buttonPanel.add(loadTextButton);

		add(buttonPanel);

		JButton confirmBtn = new JButton("이 지도로 생성");
		confirmBtn.addActionListener(e -> {
			app.dispose(); // 다이얼로그 종료
		});
		add(confirmBtn);
	}

	private void generateMap() {
		int minSegmentLength = (int) minSegmentSpinner.getValue();
		int maxSegmentLength = (int) maxSegmentSpinner.getValue();
		int segmentCount = (int) segmentCountSpinner.getValue();

		if (maxSegmentLength < minSegmentLength) {
			JOptionPane.showMessageDialog(this, "최대 세그먼트 길이는 최소 길이보다 커야 합니다.", "입력 오류", JOptionPane.ERROR_MESSAGE);
			return;
		}

		app.generateRandomMap(minSegmentLength, maxSegmentLength, segmentCount);
	}

//	private void saveMap() {
//		JFileChooser fileChooser = new JFileChooser();
//		fileChooser.setDialogTitle("맵 저장");
//		fileChooser.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter("맵 파일 (*.map)", "map"));
//
//		int result = fileChooser.showSaveDialog(this);
//		if (result == JFileChooser.APPROVE_OPTION) {
//			String path = fileChooser.getSelectedFile().getAbsolutePath();
//			if (!path.toLowerCase().endsWith(".map")) {
//				path += ".map";
//			}
//			app.saveMap(path);
//		}
//	}
//
//	private void loadMap() {
//		JFileChooser fileChooser = new JFileChooser();
//		fileChooser.setDialogTitle("맵 로드");
//		fileChooser.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter("맵 파일 (*.map)", "map"));
//
//		int result = fileChooser.showOpenDialog(this);
//		if (result == JFileChooser.APPROVE_OPTION) {
//			app.loadMap(fileChooser.getSelectedFile().getAbsolutePath());
//		}
//	}

	private void saveMapAsText() {
		JFileChooser fileChooser = new JFileChooser();
		fileChooser.setDialogTitle("텍스트 맵 저장");
		fileChooser.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter("텍스트 파일 (*.txt)", "txt"));
		int result = fileChooser.showSaveDialog(this);
		if (result == JFileChooser.APPROVE_OPTION) {
			String path = fileChooser.getSelectedFile().getAbsolutePath();
			if (!path.toLowerCase().endsWith(".txt")) {
				path += ".txt";
			}
			app.saveMapAsText(path);
		}
	}

	private void loadMapFromText() {
		JFileChooser fileChooser = new JFileChooser();
		fileChooser.setDialogTitle("텍스트 맵 로드");
		fileChooser.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter("텍스트 파일 (*.txt)", "txt"));
		int result = fileChooser.showOpenDialog(this);
		if (result == JFileChooser.APPROVE_OPTION) {
			app.loadMapFromText(fileChooser.getSelectedFile().getAbsolutePath());
		}
	}
}
