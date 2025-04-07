package client.view.map;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.imageio.ImageIO;
import javax.swing.JPanel;

import client.controller.GameController;
import client.model.Board;
import client.model.Tile;
import client.model.tiles.BridgeEndTile;
import client.model.tiles.BridgeStartTile;
import client.model.tiles.EndTile;
import client.model.tiles.EventTile;
import client.model.tiles.StartTile;
import client.model.tiles.ToolTile;
import common.model.Direction;

public class MapPanel extends JPanel {
	private Board board;
	private Map<Position, Tile> tilePositions;
	private int cellSize = 60;
	private int arrowSize = 10;
	private int offsetX, offsetY;

	// 타일 이미지 필드
	private BufferedImage normalTile1;
	private BufferedImage normalTile2;
	private BufferedImage bridgeTile1;
	private BufferedImage bridgeTile2;
	private BufferedImage eventTile;
	private BufferedImage pTile;
	private BufferedImage hTile;
	private BufferedImage aTile;

	private boolean imagesLoaded = false;

	private Map<String, Position> pieces;
	private Map<String, BufferedImage> playerImages = new HashMap<>();
	private boolean movable = false;
	private GameController controller;

	// 타일 색상 상수
	private static final Color START_COLOR = new Color(0, 150, 0);
	private static final Color END_COLOR = new Color(150, 0, 0);
	private static final Color NORMAL_COLOR = new Color(200, 200, 200);
	private static final Color BRIDGE_START_COLOR = new Color(0, 100, 200);
	private static final Color BRIDGE_END_COLOR = new Color(0, 150, 250);
	private static final Color TOOL_P_COLOR = new Color(255, 200, 0);
	private static final Color TOOL_H_COLOR = new Color(0, 200, 100);
	private static final Color TOOL_A_COLOR = new Color(200, 0, 200);
	private static final Color EVENT_COLOR = new Color(255, 100, 100);

	public MapPanel() {
		setBackground(Color.WHITE);
		createTileImages();
		loadTileImages();
		addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent e) {
				if (movable && controller != null) {
					int key = e.getKeyCode();

					switch (key) {
					case KeyEvent.VK_LEFT:
						controller.move("LEFT");
						break;
					case KeyEvent.VK_RIGHT:
						controller.move("RIGHT");
						break;
					case KeyEvent.VK_UP:
						controller.move("UP");
						break;
					case KeyEvent.VK_DOWN:
						controller.move("DOWN");
						break;
					}
				}
			}
		});
	}

	public void setBoard(Board board, Map<Position, Tile> positions) {
		this.board = board;
		this.tilePositions = positions;
		calculateOffsets();
	}

	public void setPieces(Map<String, Position> pieces) {
		this.pieces = pieces;
		repaint();
	}

	public void updatePiecePosition(String player, Position newPos) {
		if (pieces == null)
			return;
		pieces.put(player, newPos);
		repaint(); // 화면 갱신
		System.out.println("화면갱신함");
	}

	private void calculateOffsets() {
		if (tilePositions == null || tilePositions.isEmpty())
			return;

		// 맵 경계 찾기
		int minX = Integer.MAX_VALUE, maxX = Integer.MIN_VALUE;
		int minY = Integer.MAX_VALUE, maxY = Integer.MIN_VALUE;

		for (Position pos : tilePositions.keySet()) {
			minX = Math.min(minX, pos.x);
			maxX = Math.max(maxX, pos.x);
			minY = Math.min(minY, pos.y);
			maxY = Math.max(maxY, pos.y);
		}

		// 화면 중앙에 맵이 오도록 오프셋 계산
		offsetX = (getWidth() - (maxX - minX + 1) * cellSize) / 2 - minX * cellSize;
		offsetY = (getHeight() - (maxY - minY + 1) * cellSize) / 2 - minY * cellSize;
	}

	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		if (board == null || tilePositions == null)
			return;

		Graphics2D g2d = (Graphics2D) g;
		g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

		// 오프셋 재계산
		calculateOffsets();

		// 브릿지 연결선 먼저 그리기
		drawBridgeConnections(g2d);

		// 타일 그리기
		for (Map.Entry<Position, Tile> entry : tilePositions.entrySet()) {
			Position pos = entry.getKey();
			Tile tile = entry.getValue();
			int x = offsetX + pos.x * cellSize;
			int y = offsetY + pos.y * cellSize;
			drawTile(g2d, tile, x, y);
//            drawDirections(g2d, tile, x, y);
		}

		// 플레이어 그리기
		drawPlayers(g2d);
	}

	// 타일 이미지 생성 메서드
	private void createTileImages() {
		try {
			// src/images 디렉토리 확인 및 생성
			File directory = new File("images");
			if (!directory.exists()) {
				directory.mkdirs();
			}

			// 이미지 파일 존재 여부 확인
			File normalTile1File = new File("images/NormalTile1.png");
			File normalTile2File = new File("images/NormalTile2.png");
			File bridgeTile1File = new File("images/BridgeTile1.png");
			File bridgeTile2File = new File("images/BridgeTile2.png");
			File eventTileFile = new File("images/EventTile.png");
			File pTileFile = new File("images/PTile.png");
			File hTileFile = new File("images/HTile.png");
			File aTileFile = new File("images/ATile.png");
			File player = new File("images/Player.png");

			// NormalTile1 생성 (밝은 회색)
			if (!normalTile1File.exists()) {
				BufferedImage img = new BufferedImage(64, 64, BufferedImage.TYPE_INT_ARGB);
				Graphics2D g2d = img.createGraphics();
				g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
				g2d.setColor(new Color(230, 230, 230));
				g2d.fillRoundRect(0, 0, 64, 64, 10, 10);
				g2d.setColor(Color.BLACK);
				g2d.drawRoundRect(0, 0, 63, 63, 10, 10);
				g2d.dispose();
				ImageIO.write(img, "png", normalTile1File);
			}

			// NormalTile2 생성 (어두운 회색)
			if (!normalTile2File.exists()) {
				BufferedImage img = new BufferedImage(64, 64, BufferedImage.TYPE_INT_ARGB);
				Graphics2D g2d = img.createGraphics();
				g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
				g2d.setColor(new Color(200, 200, 200));
				g2d.fillRoundRect(0, 0, 64, 64, 10, 10);
				g2d.setColor(Color.BLACK);
				g2d.drawRoundRect(0, 0, 63, 63, 10, 10);
				g2d.dispose();
				ImageIO.write(img, "png", normalTile2File);
			}

			// BridgeTile1 생성 (다리 시작)
			if (!bridgeTile1File.exists()) {
				BufferedImage img = new BufferedImage(64, 64, BufferedImage.TYPE_INT_ARGB);
				Graphics2D g2d = img.createGraphics();
				g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
				g2d.setColor(new Color(0, 100, 200, 150)); // 반투명 블루
				g2d.fillRoundRect(0, 0, 64, 64, 10, 10);
				g2d.setColor(Color.BLUE);
				g2d.setStroke(new BasicStroke(2f));
				g2d.drawRoundRect(0, 0, 63, 63, 10, 10);
				// 다리 시작 아이콘 그리기
				g2d.drawLine(32, 32, 64, 32);
				g2d.drawLine(54, 26, 64, 32);
				g2d.drawLine(54, 38, 64, 32);
				g2d.dispose();
				ImageIO.write(img, "png", bridgeTile1File);
			}

			// BridgeTile2 생성 (다리 끝)
			if (!bridgeTile2File.exists()) {
				BufferedImage img = new BufferedImage(64, 64, BufferedImage.TYPE_INT_ARGB);
				Graphics2D g2d = img.createGraphics();
				g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
				g2d.setColor(new Color(0, 150, 250, 150)); // 반투명 라이트 블루
				g2d.fillRoundRect(0, 0, 64, 64, 10, 10);
				g2d.setColor(Color.BLUE);
				g2d.setStroke(new BasicStroke(2f));
				g2d.drawRoundRect(0, 0, 63, 63, 10, 10);
				// 다리 끝 아이콘 그리기
				g2d.drawLine(0, 32, 32, 32);
				g2d.drawLine(0, 32, 10, 26);
				g2d.drawLine(0, 32, 10, 38);
				g2d.dispose();
				ImageIO.write(img, "png", bridgeTile2File);
			}

			// EventTile 생성 (이벤트 타일)
			if (!eventTileFile.exists()) {
				BufferedImage img = new BufferedImage(64, 64, BufferedImage.TYPE_INT_ARGB);
				Graphics2D g2d = img.createGraphics();
				g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
				g2d.setColor(EVENT_COLOR);
				g2d.fillRoundRect(0, 0, 64, 64, 10, 10);
				g2d.setColor(new Color(150, 0, 0));
				g2d.setStroke(new BasicStroke(2f));
				g2d.drawRoundRect(0, 0, 63, 63, 10, 10);
				// 이벤트 아이콘 그리기
				g2d.setColor(Color.WHITE);
				g2d.setFont(g2d.getFont().deriveFont(20f));
				g2d.drawString("E", 25, 40);
				g2d.dispose();
				ImageIO.write(img, "png", eventTileFile);
			}

			// PTile 생성 (P 도구 타일)
			if (!pTileFile.exists()) {
				BufferedImage img = new BufferedImage(64, 64, BufferedImage.TYPE_INT_ARGB);
				Graphics2D g2d = img.createGraphics();
				g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
				g2d.setColor(new Color(255, 200, 0, 180)); // 반투명 노랑
				g2d.fillRoundRect(0, 0, 64, 64, 10, 10);
				g2d.setColor(new Color(200, 150, 0));
				g2d.setStroke(new BasicStroke(2f));
				g2d.drawRoundRect(0, 0, 63, 63, 10, 10);
				// P 아이콘 그리기
				g2d.setColor(Color.BLACK);
				g2d.setFont(g2d.getFont().deriveFont(24f));
				g2d.drawString("P", 25, 40);
				g2d.dispose();
				ImageIO.write(img, "png", pTileFile);
			}

			// HTile 생성 (H 도구 타일)
			if (!hTileFile.exists()) {
				BufferedImage img = new BufferedImage(64, 64, BufferedImage.TYPE_INT_ARGB);
				Graphics2D g2d = img.createGraphics();
				g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
				g2d.setColor(new Color(0, 200, 100, 180)); // 반투명 녹색
				g2d.fillRoundRect(0, 0, 64, 64, 10, 10);
				g2d.setColor(new Color(0, 150, 50));
				g2d.setStroke(new BasicStroke(2f));
				g2d.drawRoundRect(0, 0, 63, 63, 10, 10);
				// H 아이콘 그리기
				g2d.setColor(Color.BLACK);
				g2d.setFont(g2d.getFont().deriveFont(24f));
				g2d.drawString("H", 25, 40);
				g2d.dispose();
				ImageIO.write(img, "png", hTileFile);
			}

			// ATile 생성 (A 도구 타일)
			if (!aTileFile.exists()) {
				BufferedImage img = new BufferedImage(64, 64, BufferedImage.TYPE_INT_ARGB);
				Graphics2D g2d = img.createGraphics();
				g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
				g2d.setColor(new Color(200, 0, 200, 180)); // 반투명 보라색
				g2d.fillRoundRect(0, 0, 64, 64, 10, 10);
				g2d.setColor(new Color(150, 0, 150));
				g2d.setStroke(new BasicStroke(2f));
				g2d.drawRoundRect(0, 0, 63, 63, 10, 10);
				// A 아이콘 그리기
				g2d.setColor(Color.BLACK);
				g2d.setFont(g2d.getFont().deriveFont(24f));
				g2d.drawString("A", 25, 40);
				g2d.dispose();
				ImageIO.write(img, "png", aTileFile);
			}

			System.out.println("타일 이미지가 성공적으로 생성되었습니다!");
		} catch (IOException e) {
			System.err.println("타일 이미지 생성 실패: " + e.getMessage());
			e.printStackTrace();
		}
	}

	// 타일 이미지 로드 메서드
	private void loadTileImages() {
		try {
			normalTile1 = ImageIO.read(new File("images/NormalTile1.png"));
			normalTile2 = ImageIO.read(new File("images/NormalTile2.png"));
			bridgeTile1 = ImageIO.read(new File("images/BridgeTile1.png"));
			bridgeTile2 = ImageIO.read(new File("images/BridgeTile2.png"));
			eventTile = ImageIO.read(new File("images/EventTile.png"));
			pTile = ImageIO.read(new File("images/PTile.png"));
			hTile = ImageIO.read(new File("images/HTile.png"));
			aTile = ImageIO.read(new File("images/ATile.png"));

			imagesLoaded = true;
			System.out.println("타일 이미지가 성공적으로 로드되었습니다!");
		} catch (IOException e) {
			System.err.println("타일 이미지 로드 실패: " + e.getMessage());
			e.printStackTrace();
		}
	}

	public void initPlayerImages() {
		try {
			// 기본 하얀색 플레이어 이미지 로드
			BufferedImage baseImage = ImageIO.read(new File("images/Player.png"));

			// 각 플레이어별 색상 이미지 생성 및 캐싱
			for (String playerName : pieces.keySet()) {
				Color playerColor = getColorForPlayer(playerName);
				playerImages.put(playerName, changeColor(baseImage, playerColor));
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void drawBridgeConnections(Graphics2D g2d) {
		g2d.setStroke(new BasicStroke(2.0f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND, 0, new float[] { 5 }, 0));
		g2d.setColor(Color.BLUE);

		for (Map.Entry<Position, Tile> entry : tilePositions.entrySet()) {
			Tile tile = entry.getValue();
			if (tile instanceof BridgeStartTile) {
				BridgeStartTile start = (BridgeStartTile) tile;
				BridgeEndTile end = start.getConnectedEnd();

				if (end != null) {
					Position startPos = entry.getKey();
					Position endPos = null;

					// 브릿지 종료 타일의 위치 찾기
					for (Map.Entry<Position, Tile> endEntry : tilePositions.entrySet()) {
						if (endEntry.getValue() == end) {
							endPos = endEntry.getKey();
							break;
						}
					}

					if (endPos != null) {
						// 브릿지 연결선 그리기 (점선)
						int x1 = offsetX + startPos.x * cellSize + cellSize / 2;
						int y1 = offsetY + startPos.y * cellSize + cellSize / 2;
						int x2 = offsetX + endPos.x * cellSize + cellSize / 2;
						int y2 = offsetY + endPos.y * cellSize + cellSize / 2;

						g2d.drawLine(x1, y1, x2, y2);

						// 브릿지 방향 화살표 그리기
						drawArrowHead(g2d, x1, y1, x2, y2);
					}
				}
			}
		}

		// 기본 선 스타일로 복원
		g2d.setStroke(new BasicStroke(1.0f));
	}

	// 플레이어마다 다른 색상을 주기
	private Color getColorForPlayer(String name) {
		// 기본 색상은 흰색으로 설정
		if (name == null || name.isEmpty()) {
			return Color.WHITE;
		}

		Map<String, Color> colorMap = new HashMap<>();
		colorMap.put("player1", new Color(255, 100, 100)); // 연한 빨강
		colorMap.put("player2", new Color(100, 100, 255)); // 연한 파랑
		colorMap.put("player3", new Color(100, 255, 100)); // 연한 초록
		colorMap.put("player4", new Color(255, 255, 100)); // 연한 노랑

		// 매핑된 색상이 있으면 해당 색상 반환
		if (colorMap.containsKey(name)) {
			return colorMap.get(name);
		}

		// 매핑된 색상이 없으면 해시값 기반으로 색상 생성
		int hash = Math.abs(name.hashCode());
		float hue = (hash % 360) / 360f; // 0.0 ~ 1.0 범위
		return Color.getHSBColor(hue, 0.7f, 0.9f);
	}

	private void drawPlayers(Graphics2D g2d) {
		if (pieces == null || pieces.isEmpty())
			return;

		int pieceSize = (int) (cellSize * 0.7);
		int nameTagHeight = 20; // 이름표 높이

		for (Map.Entry<String, Position> entry : pieces.entrySet()) {
			String playerName = entry.getKey();
			Position pos = entry.getValue();

			// 말의 위치 계산
			int pieceX = offsetX + pos.x * cellSize + (cellSize - pieceSize) / 2;
			int pieceY = offsetY + pos.y * cellSize + (cellSize - pieceSize) / 2;

			// 이름표 위치 (플레이어 타일 위에)
			int nameTagX = pieceX - pieceSize / 4;
			int nameTagY = pieceY - nameTagHeight - 5;
			int nameTagWidth = pieceSize + pieceSize / 2;

			// 이름표 배경 그리기 (반투명 검정)
			g2d.setColor(new Color(0, 0, 0, 180));
			g2d.fillRoundRect(nameTagX, nameTagY, nameTagWidth, nameTagHeight, 6, 6);

			// 이름표 텍스트 그리기
			g2d.setColor(Color.WHITE);
			g2d.setFont(g2d.getFont().deriveFont(11f));
			FontMetrics fm = g2d.getFontMetrics();

			// 이름이 너무 길면 자르기
			String displayName = playerName;
			if (fm.stringWidth(displayName) > nameTagWidth - 4) {
				displayName = playerName.substring(0, 5) + "...";
			}
			int textWidth = fm.stringWidth(displayName);
			g2d.drawString(displayName, nameTagX + (nameTagWidth - textWidth) / 2,
					nameTagY + nameTagHeight / 2 + fm.getAscent() / 2 - 2);

			// 플레이어 이미지가 없으면 생성
			if (!playerImages.containsKey(playerName)) {
				initPlayerImages();
			}

			// 이미지 그리기
			BufferedImage playerImage = playerImages.get(playerName);
			if (playerImage != null) {
				g2d.drawImage(playerImage, pieceX, pieceY, pieceSize, pieceSize, null);
			} else {
				// 이미지가 없을 경우 기본 원형 말 그리기
				g2d.setColor(getColorForPlayer(playerName));
				g2d.fillOval(pieceX, pieceY, pieceSize, pieceSize);
				g2d.setColor(Color.BLACK);
				g2d.drawOval(pieceX, pieceY, pieceSize, pieceSize);

				// 이름 이니셜 표시
				g2d.setColor(Color.WHITE);
				g2d.setFont(g2d.getFont().deriveFont(14f));
				fm = g2d.getFontMetrics();
				String initial = playerName.substring(0, 1).toUpperCase();
				textWidth = fm.stringWidth(initial);
				int textHeight = fm.getAscent();
				g2d.drawString(initial, pieceX + (pieceSize - textWidth) / 2,
						pieceY + (pieceSize + textHeight) / 2 - 2);
			}
		}
	}

	private BufferedImage changeColor(BufferedImage originalImage, Color targetColor) {
		int width = originalImage.getWidth();
		int height = originalImage.getHeight();
		BufferedImage newImage = new BufferedImage(width, height, originalImage.getType());

		for (int y = 0; y < height; y++) {
			for (int x = 0; x < width; x++) {
				int rgb = originalImage.getRGB(x, y);
				Color pixelColor = new Color(rgb, true);

				if (pixelColor.getRed() > 200 && pixelColor.getGreen() > 200 && pixelColor.getBlue() > 200) {
					int alpha = pixelColor.getAlpha();
					Color newColor = new Color(targetColor.getRed(), targetColor.getGreen(), targetColor.getBlue(),
							alpha);
					newImage.setRGB(x, y, newColor.getRGB());
				} else {
					newImage.setRGB(x, y, rgb);
				}
			}
		}
		return newImage;
	}

	private void drawTile(Graphics2D g2d, Tile tile, int x, int y) {
		Position pos = null;

		// 위치 찾기
		for (Map.Entry<Position, Tile> entry : tilePositions.entrySet()) {
			if (entry.getValue() == tile) {
				pos = entry.getKey();
				break;
			}
		}

		if (!imagesLoaded) {
			// 이미지가 로드되지 않았을 경우 색상으로 타일 그리기
			drawTileWithColor(g2d, tile, x, y);
			return;
		}

		// 이벤트 타일인 경우 별도의 이미지 사용
		if (tile instanceof EventTile) {
			g2d.drawImage(eventTile, x, y, cellSize, cellSize, null);

			// 이벤트 타일인 경우 텍스트를 위한 반투명 배경 추가
			String typeText = getTileTypeText(tile);
			FontMetrics fm = g2d.getFontMetrics();
			int textWidth = fm.stringWidth(typeText);
			int textHeight = fm.getHeight();
			int textX = x + (cellSize - textWidth) / 2;
			int textY = y + cellSize / 2 + fm.getAscent() / 2 - fm.getDescent() / 2 + 18;

			// 텍스트 배경 그리기 (반투명 검정색 배경)
			g2d.setColor(new Color(0, 0, 0, 180)); // 알파값 180의 검정색 (0-255)
			g2d.fillRoundRect(textX - 2, textY - fm.getAscent(), textWidth + 4, textHeight, 5, 5);

			g2d.setColor(Color.WHITE);
			g2d.drawString(typeText, textX, textY);
		} else {
			// 기본 타일 이미지 선택 (번갈아가며 사용)
			BufferedImage baseImage;

			// 체스판처럼 번갈아가며 타일 이미지 선택
			if (pos != null && (pos.x + pos.y) % 2 == 0) {
				baseImage = normalTile1;
			} else {
				baseImage = normalTile2;
			}

			// 기본 타일 그리기
			g2d.drawImage(baseImage, x, y, cellSize, cellSize, null);

			// 브릿지 타일 그리기 (일반 타일 위에)
			if (tile instanceof BridgeStartTile) {
				g2d.drawImage(bridgeTile1, x, y, cellSize, cellSize, null);
			} else if (tile instanceof BridgeEndTile) {
				g2d.drawImage(bridgeTile2, x, y, cellSize, cellSize, null);
			}

			// 도구 타일 그리기 (일반 타일 위에)
			if (tile instanceof ToolTile) {
				ToolTile toolTile = (ToolTile) tile;
				String toolType = toolTile.getToolType();

				if ("P".equals(toolType)) {
					g2d.drawImage(pTile, x, y, cellSize, cellSize, null);
				} else if ("H".equals(toolType)) {
					g2d.drawImage(hTile, x, y, cellSize, cellSize, null);
				} else if ("A".equals(toolType)) {
					g2d.drawImage(aTile, x, y, cellSize, cellSize, null);
				}
			}

			// 일반 타일의 텍스트 표시
			String typeText = getTileTypeText(tile);

			// START나 END 타일인 경우 볼드체 적용
			if ("START".equals(typeText) || "END".equals(typeText)) {
				Font originalFont = g2d.getFont();
				Font boldFont = new Font(originalFont.getName(), Font.BOLD, originalFont.getSize());
				g2d.setFont(boldFont);

				g2d.setColor(Color.BLACK);
				FontMetrics fm = g2d.getFontMetrics();
				int textWidth = fm.stringWidth(typeText);
				int textHeight = fm.getHeight();

				// 정확히 중앙에 배치
				int textX = x + (cellSize - textWidth) / 2;
				int textY = y + cellSize / 2 + fm.getAscent() / 2 - fm.getDescent() / 2;

				g2d.drawString(typeText, textX, textY);

				// 원래 폰트로 복원
				g2d.setFont(originalFont);
			} else if (!typeText.isEmpty()) {
				g2d.setColor(Color.BLACK);
				FontMetrics fm = g2d.getFontMetrics();
				int textWidth = fm.stringWidth(typeText);
				int textHeight = fm.getHeight();

				// 정확히 중앙에 배치
				int textX = x + (cellSize - textWidth) / 2;
				int textY = y + cellSize / 2 + fm.getAscent() / 2 - fm.getDescent() / 2;

				g2d.drawString(typeText, textX, textY);
			}
		}
	}

	// 색상으로 타일 그리기
	private void drawTileWithColor(Graphics2D g2d, Tile tile, int x, int y) {
		// 타일 유형에 따라 색상 설정
		if (tile instanceof StartTile) {
			g2d.setColor(START_COLOR);
		} else if (tile instanceof EndTile) {
			g2d.setColor(END_COLOR);
		} else if (tile instanceof BridgeStartTile) {
			g2d.setColor(BRIDGE_START_COLOR);
		} else if (tile instanceof BridgeEndTile) {
			g2d.setColor(BRIDGE_END_COLOR);
		} else if (tile instanceof ToolTile) {
			ToolTile toolTile = (ToolTile) tile;
			String toolType = toolTile.getToolType();
			if ("P".equals(toolType)) {
				g2d.setColor(TOOL_P_COLOR);
			} else if ("H".equals(toolType)) {
				g2d.setColor(TOOL_H_COLOR);
			} else if ("A".equals(toolType)) {
				g2d.setColor(TOOL_A_COLOR);
			} else {
				g2d.setColor(NORMAL_COLOR);
			}
		} else if (tile instanceof EventTile) {
			g2d.setColor(EVENT_COLOR);
		} else {
			g2d.setColor(NORMAL_COLOR);
		}

		// 타일 그리기
		g2d.fillRoundRect(x, y, cellSize, cellSize, 10, 10);
		g2d.setColor(Color.BLACK);
		g2d.drawRoundRect(x, y, cellSize - 1, cellSize - 1, 10, 10);

		// 타일 유형 표시
		String typeText = getTileTypeText(tile);

		if (tile instanceof EventTile) {
			// 이벤트 타일에 대한 텍스트 강조
			FontMetrics fm = g2d.getFontMetrics();
			int textWidth = fm.stringWidth(typeText);
			int textHeight = fm.getHeight();
			int textX = x + (cellSize - textWidth) / 2;
			int textY = y + cellSize / 2 + fm.getAscent() / 2 - fm.getDescent() / 2;

			// 텍스트 배경 그리기
			g2d.setColor(new Color(0, 0, 0, 180));
			g2d.fillRoundRect(textX - 2, textY - textHeight + 2, textWidth + 4, textHeight, 5, 5);

			// 텍스트 그리기
			g2d.setColor(Color.WHITE);
			g2d.drawString(typeText, textX, textY);
		} else if ("START".equals(typeText) || "END".equals(typeText)) {
			// START, END 타일에 볼드체 검은색 적용
			Font originalFont = g2d.getFont();
			Font boldFont = new Font(originalFont.getName(), Font.BOLD, originalFont.getSize());
			g2d.setFont(boldFont);

			g2d.setColor(Color.BLACK);
			FontMetrics fm = g2d.getFontMetrics();
			int textWidth = fm.stringWidth(typeText);
			int textHeight = fm.getHeight();

			// 정확히 중앙에 배치
			int textX = x + (cellSize - textWidth) / 2;
			int textY = y + cellSize / 2 + fm.getAscent() / 2 - fm.getDescent() / 2;

			g2d.drawString(typeText, textX, textY);

			// 원래 폰트로 복원
			g2d.setFont(originalFont);
		} else if (!typeText.isEmpty()) {
			// 일반 타일의 텍스트
			g2d.setColor(Color.BLACK);
			FontMetrics fm = g2d.getFontMetrics();
			int textWidth = fm.stringWidth(typeText);
			int textHeight = fm.getHeight();

			// 정확히 중앙에 배치
			int textX = x + (cellSize - textWidth) / 2;
			int textY = y + cellSize / 2 + fm.getAscent() / 2 - fm.getDescent() / 2;

			g2d.drawString(typeText, textX, textY);
		}
	}

	private String getTileTypeText(Tile tile) {
		if (tile instanceof StartTile)
			return "START";
		if (tile instanceof EndTile)
			return "END";
		if (tile instanceof BridgeStartTile)
			return "";
		if (tile instanceof BridgeEndTile)
			return "";
		if (tile instanceof ToolTile) {
			ToolTile toolTile = (ToolTile) tile;
			return "";
		}
		if (tile instanceof EventTile) {
			EventTile eventTile = (EventTile) tile;
			return eventTile.getMiniGameType();
		}
		return "";
	}

	// 화살표 표기 디버깅용.
	private void drawDirections(Graphics2D g2d, Tile tile, int x, int y) {
		for (Direction dir : tile.getDirections()) {
			int centerX = x + cellSize / 2;
			int centerY = y + cellSize / 2;
			int endX = centerX, endY = centerY;

			switch (dir) {
			case UP:
				endY = centerY - cellSize / 3;
				break;
			case DOWN:
				endY = centerY + cellSize / 3;
				break;
			case LEFT:
				endX = centerX - cellSize / 3;
				break;
			case RIGHT:
				endX = centerX + cellSize / 3;
				break;
			case NONE:
				continue;
			}

			// 방향별 색상 설정
			switch (dir) {
			case UP:
				g2d.setColor(new Color(0, 150, 0));
				break;
			case DOWN:
				g2d.setColor(new Color(150, 0, 0));
				break;
			case LEFT:
				g2d.setColor(new Color(0, 0, 150));
				break;
			case RIGHT:
				g2d.setColor(new Color(150, 150, 0));
				break;
			default:
				g2d.setColor(Color.BLACK);
			}

			// 선 그리기
			g2d.setStroke(new BasicStroke(2.0f));
			g2d.drawLine(centerX, centerY, endX, endY);

			// 화살표 그리기
			drawArrowHead(g2d, centerX, centerY, endX, endY);

			// 기본 스트로크로 복원
			g2d.setStroke(new BasicStroke(1.0f));
		}
	}

	private void drawArrowHead(Graphics2D g2d, int x1, int y1, int x2, int y2) {
		double dx = x2 - x1;
		double dy = y2 - y1;
		double angle = Math.atan2(dy, dx);

		// 화살표 머리 그리기
		int[] xPoints = new int[3];
		int[] yPoints = new int[3];

		xPoints[0] = x2;
		yPoints[0] = y2;

		xPoints[1] = (int) (x2 - arrowSize * Math.cos(angle - Math.PI / 6));
		yPoints[1] = (int) (y2 - arrowSize * Math.sin(angle - Math.PI / 6));

		xPoints[2] = (int) (x2 - arrowSize * Math.cos(angle + Math.PI / 6));
		yPoints[2] = (int) (y2 - arrowSize * Math.sin(angle + Math.PI / 6));

		g2d.fillPolygon(xPoints, yPoints, 3);
	}

	// 게터/세터 메소드
	public BufferedImage getNormalTile1() {
		return normalTile1;
	}

	public void setNormalTile1(BufferedImage normalTile1) {
		this.normalTile1 = normalTile1;
	}

	public BufferedImage getNormalTile2() {
		return normalTile2;
	}

	public void setNormalTile2(BufferedImage normalTile2) {
		this.normalTile2 = normalTile2;
	}

	public BufferedImage getBridgeTile1() {
		return bridgeTile1;
	}

	public void setBridgeTile1(BufferedImage bridgeTile1) {
		this.bridgeTile1 = bridgeTile1;
	}

	public BufferedImage getBridgeTile2() {
		return bridgeTile2;
	}

	public void setBridgeTile2(BufferedImage bridgeTile2) {
		this.bridgeTile2 = bridgeTile2;
	}

	public BufferedImage getEventTile() {
		return eventTile;
	}

	public void setEventTile(BufferedImage eventTile) {
		this.eventTile = eventTile;
	}

	public BufferedImage getPTile() {
		return pTile;
	}

	public void setPTile(BufferedImage pTile) {
		this.pTile = pTile;
	}

	public BufferedImage getHTile() {
		return hTile;
	}

	public void setHTile(BufferedImage hTile) {
		this.hTile = hTile;
	}

	public BufferedImage getATile() {
		return aTile;
	}

	public void setATile(BufferedImage aTile) {
		this.aTile = aTile;
	}

	public boolean isMovable() {
		return movable;
	}

	public void setMovable(boolean movable) {
		this.movable = movable;
	}

	public void setController(GameController controller) {
		this.controller = controller;
	}
}
