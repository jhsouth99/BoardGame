package client.view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import client.controller.GameController;
import client.model.Board;
import client.model.Tile;
import client.view.map.MapPanel;
import client.view.map.MapVisualizerApp;
import client.view.map.PlayerInfoPanel;
import client.view.map.Position;
import common.dto.UserDetailDto;
import common.model.Direction;

public class GameViewer {

	public static final String STR_1 = "접속자: ";

	// 게임 컨트롤러 객체. 뷰와 컨트롤러 간의 상호작용을 관리합니다.
	GameController controller;

	JFrame frame; // 게임 화면을 표시할 Frame 객체.
	JPanel pnlFirst;
	JPanel pnlSecond;
	JPanel pnlGame;

	UserLoginPanel pnlFirstTop;
	TitleContentButton btnNewRoom;
	VerticalButtonPanel pnlBtnRooms;
	VerticalLabelPanel pnlRoomPlayers;
	JButton btnGameReady;
	JButton btnPlay;
	JPanel pnlSecondTop;
	MapPanel pnlMap;
	PlayerInfoPanel pnlPlayerInfo;
	JButton btnDice;
	JLabel lblDiceRes;

	MapVisualizerApp gameBoardDialog;

	List<JLabel> lblsPlayerInfo;

	private JPanel pnlGameTop;

	private JPanel pnlDice;

	private JLabel lblNotice;

	/**
	 * GameViewer 생성자. 컨트롤러를 초기화하고 게임 화면(Frame)을 설정합니다.
	 *
	 * @param controller GameController 객체로, 게임 로직을 관리합니다.
	 */
	public GameViewer(GameController controller) {
		this.controller = controller; // 컨트롤러를 저장
		controller.setViewer(this); // 컨트롤러에 현재 뷰를 설정

		// Frame 객체를 생성하고 초기화
		frame = new JFrame("Board Game");

		// Frame의 위치 및 크기 설정
		frame.setBounds(300, 50, 960, 960);

		pnlFirst = new JPanel();
		pnlFirst.setLayout(new BorderLayout());
		pnlFirst.setBounds(0, 0, frame.getWidth(), frame.getHeight());

		pnlSecond = new JPanel();
		pnlSecond.setLayout(new BorderLayout());
		pnlSecond.setBounds(0, 0, frame.getWidth(), frame.getHeight());

		pnlGame = new JPanel();
		pnlGame.setLayout(new BorderLayout());
		pnlGame.setBounds(0, 0, frame.getWidth(), frame.getHeight());

		// 회원가입/로그인/로그아웃 및 방 참여 전 화면 구성

		pnlFirstTop = new UserLoginPanel(controller);

		pnlFirst.add(pnlFirstTop, BorderLayout.NORTH);

		pnlBtnRooms = new VerticalButtonPanel();
		pnlBtnRooms.setBounds(20, 120, pnlFirst.getWidth() - 80, pnlFirst.getHeight() - 180);

		btnNewRoom = new TitleContentButton("ADD NEW ROOM", "");
		btnNewRoom.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				String s = JOptionPane.showInputDialog("방 이름");
				if (s == null || s.isBlank()) {
					JOptionPane.showMessageDialog(null, "올바른 방 이름을 입력하세요.");
					return;
				}
				gameBoardDialog = new MapVisualizerApp(frame);
				gameBoardDialog.setVisible(true); // 이 시점에서 '대기' 상태

				Board selectedMap = gameBoardDialog.getCurrentBoard();
				if (selectedMap != null) {
					controller.makeRoom(s, selectedMap); // 확인 누르면 실행
				} else {
					JOptionPane.showMessageDialog(null, "지도를 선택하지 않았습니다.");
				}
			}
		});
		pnlBtnRooms.addButton(btnNewRoom);

		// 방 참여 후 게임 시작 전 화면 구성
		
		pnlSecondTop = new JPanel();
		pnlSecondTop.setLayout(new FlowLayout(FlowLayout.LEFT));

		pnlRoomPlayers = new VerticalLabelPanel();

		btnGameReady = new JButton("READY");
		btnGameReady.setSize(80, 30);
		btnGameReady.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				btnGameReady.setEnabled(false);
				controller.setReadyToPlay();
			}
		});

		btnPlay = new JButton("START TO PLAY");
		btnPlay.setSize(150, 30);
		btnPlay.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				btnPlay.setEnabled(false);
				controller.startGame();
			}
		});
		btnPlay.setEnabled(false);

		pnlSecondTop.add(btnGameReady);
		pnlSecondTop.add(btnPlay);
		pnlSecond.add(pnlSecondTop, BorderLayout.NORTH);
		pnlSecond.add(pnlRoomPlayers, BorderLayout.CENTER);

		// 게임 시작 후 화면 구성
		
		pnlGameTop = new JPanel(new BorderLayout());
		
		pnlDice = new JPanel(new FlowLayout(FlowLayout.LEFT));

		pnlMap = new MapPanel();
//		pnlMap.setBounds(0, 50, 640, 520);

		pnlMap.setController(controller);
        pnlMap.setFocusable(true);

		btnDice = new JButton("주사위 던지기");
		btnDice.setSize(120, 30);
		btnDice.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				controller.rollDice();
			}
		});
		btnDice.setEnabled(false);

		lblDiceRes = new JLabel("");
		lblDiceRes.setSize(240, 30);
		
		lblNotice = new JLabel("");
		lblNotice.setForeground(Color.GREEN);

		pnlDice.add(btnDice);
		pnlDice.add(lblDiceRes);
		pnlGameTop.add(pnlDice, BorderLayout.WEST);
		pnlGameTop.add(lblNotice, BorderLayout.CENTER);

		pnlGame.add(pnlGameTop, BorderLayout.NORTH);
		pnlGame.add(pnlMap, BorderLayout.CENTER);

		frame.add(pnlFirst);

		// 창 닫기 이벤트 처리기 추가
		frame.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				// 창이 닫힐 때 컨트롤러의 exitGame 메서드를 호출하여 게임 종료 처리
				controller.exitGame();

				// Frame을 닫고 리소스를 해제
				frame.dispose();
			}
		});

		// Frame을 화면에 표시
		frame.setVisible(true);
	}

	/**
	 * update 메서드는 게임 상태가 변경될 때 화면을 업데이트하는 역할을 합니다.
	 */
	public void update() {
		frame.getContentPane().revalidate();
		frame.getContentPane().repaint();
	}

	public String[] getLoginInput() {
		String[] usernamePassword = new String[2];
		usernamePassword[0] = JOptionPane.showInputDialog("로그인할 ID를 입력하세요.");
		if (usernamePassword[0] == null || usernamePassword[0].isBlank())
			return null;
		usernamePassword[1] = JOptionPane.showInputDialog("비밀번호를 입력하세요.");
		if (usernamePassword[1] == null || usernamePassword[1].isBlank())
			return null;
		return usernamePassword;
	}

	public String[] getSignupInput() {
		String[] usernamePassword = new String[2];
		usernamePassword[0] = JOptionPane.showInputDialog("새로운 ID를 입력하세요.");
		if (usernamePassword[0] == null || usernamePassword[0].isBlank())
			return null;
		usernamePassword[1] = JOptionPane.showInputDialog("비밀번호를 입력하세요.");
		if (usernamePassword[1] == null || usernamePassword[1].isBlank())
			return null;
		return usernamePassword;
	}

	public void loginSuccess(String name) {
		pnlFirstTop.login(name, 0);
		pnlFirst.add(pnlBtnRooms, BorderLayout.CENTER);
		update();
	}

	public void loginSuccess(UserDetailDto dto) {
		pnlFirstTop.login(dto.getName(), dto.getWinCount());
		pnlFirst.add(pnlBtnRooms, BorderLayout.CENTER);
		update();
	}

	public void addBtnRoom(String name, String members) {
		pnlBtnRooms.removeButton(btnNewRoom);
		TitleContentButton b = new TitleContentButton(name, STR_1 + members);
		b.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				String roomName = b.getTitle();
				controller.enterRoom(roomName);
			}
		});
		pnlBtnRooms.addButton(b);
		pnlBtnRooms.addButton(btnNewRoom);
		update();
	}

	public void cleanLblRooms() {
		pnlBtnRooms.removeAllButtons();
		pnlBtnRooms.addButton(btnNewRoom);
		update();
	}

	public void addPlayersInOtherRoom(String roomName, String playerName) {
		TitleContentButton target = pnlBtnRooms.findButtonByTitle(roomName);
		if (target == null)
			return;
		if (target.getContent().equals(STR_1))
			target.setContent(target.getContent() + playerName);
		else
			target.setContent(target.getContent() + ", " + playerName);
		update();
	}

	public void addLblPlayersInRoom(String playerName, boolean prepared) {
		TitleContentLabel lblPlayer = new TitleContentLabel(playerName, String.valueOf(prepared));
		pnlRoomPlayers.addLabel(lblPlayer);
		update();
	}

	public void setLblPlayersInRoomReady(String playerName, boolean prepared) {
		TitleContentLabel target = pnlRoomPlayers.findLabelByTitle(playerName);
		if (target == null)
			return;
		target.setContent(String.valueOf(prepared));
		update();
	}

	public void removePlayer(String playerName) {
		TitleContentLabel target = pnlRoomPlayers.findLabelByTitle(playerName);
		if (target == null)
			return;
		pnlRoomPlayers.removeLabel(target);
		update();
	}

	public void setMapPanel(Board board, Map<Position, Tile> positions) {

		pnlMap.removeAll();
		pnlMap.setBoard(board, positions);
		pnlMap.repaint();
	}

	public void setMainPanel(int arg) {
		frame.getContentPane().removeAll();
		update();
		switch (arg) {
		case 1:
			frame.add(pnlFirst);
			break;
		case 2:
			frame.add(pnlSecond);
			btnGameReady.setEnabled(true);
			btnPlay.setEnabled(false);
			break;
		case 3:
			frame.add(pnlGame);
			break;
		}
		update();
	}

	public void loginFail(String arg) {
		JOptionPane.showMessageDialog(frame, "로그인 실패!\n" + arg, "Error", JOptionPane.ERROR_MESSAGE);
	}

	public void notify(String message) {
		// TODO Auto-generated method stub
		JOptionPane.showMessageDialog(null, message);
	}

	public void removePlayerInOtherRoom(String roomName, String playerName) {
		TitleContentButton btn = pnlBtnRooms.findButtonByTitle(roomName);
		if (btn == null)
			return;
		String str = btn.getContent();
		System.out.println(str);
		str = str.substring(STR_1.length());
		String[] memberNames = str.split(", ");
		str = STR_1;
		boolean first = true;
		for (int i = 0; i < memberNames.length; i++) {
			if (!memberNames[i].equals(playerName)) {
				if (first)
					first = false;
				else
					str += ", ";
				str += memberNames[i];
			}
		}
		btn.setContent(str);
		update();
	}

	public void setStartToPlayBtnAvailable(boolean b) {
		btnPlay.setEnabled(b);
	}

	public void deleteBtnRoom(String roomName) {
		pnlBtnRooms.removeButtonByTitle(roomName);
	}

	public void setGamePlayers(List<String> playerNames) {
		if (pnlPlayerInfo != null)
			pnlPlayerInfo.removeAll();
		pnlPlayerInfo = new PlayerInfoPanel(playerNames);
		Map<String, Position> pieceMap = new HashMap<String, Position>();
		for (String playerName : playerNames) {
			pieceMap.put(playerName, new Position(0, 0));
		}
		pnlMap.setPieces(pieceMap);
		pnlGame.add(pnlPlayerInfo, BorderLayout.SOUTH);
		pnlPlayerInfo.repaint();
		update();
	}

	public void setTurn(String name, boolean myTurn) {
		lblDiceRes.setText("");
		pnlPlayerInfo.highlightRow(name);
		btnDice.setEnabled(myTurn);
		update();
	}

	public void setDiceResult(int res, boolean movable) {
		btnDice.setEnabled(false);
		lblDiceRes.setText(res + "이 나왔습니다. 방향키로 움직이세요.");
		pnlMap.setMovable(movable);
		if (movable)
	        pnlMap.requestFocusInWindow();
	}

	public void logout() {
		pnlFirstTop.logout();
		pnlBtnRooms.removeAllButtons();
		pnlBtnRooms.addButton(btnNewRoom);
		pnlFirst.remove(pnlBtnRooms);
		update();
	}
	
	public void listenKeyPress() {
        pnlMap.requestFocusInWindow();
	}

	public void movePlayerSingleStep(String playerName, int currX, int currY, Direction dir, int step) {
		switch (dir) {
		case UP:
			pnlPlayerInfo.addPlayerMovement(playerName, "UP", step);
			pnlMap.updatePiecePosition(playerName, new Position(currX, currY - step));
			break;
		case DOWN:
			pnlPlayerInfo.addPlayerMovement(playerName, "DOWN", step);
			pnlMap.updatePiecePosition(playerName, new Position(currX, currY + step));
			break;
		case LEFT:
			pnlPlayerInfo.addPlayerMovement(playerName, "LEFT", step);
			pnlMap.updatePiecePosition(playerName, new Position(currX - step, currY));
			break;
		case RIGHT:
			pnlPlayerInfo.addPlayerMovement(playerName, "RIGHT", step);
			pnlMap.updatePiecePosition(playerName, new Position(currX + step, currY));
			break;
		default:
		}
		System.out.println(dir);
	}

	public void movePlayer(String playerName, int currX, int currY, int nextX, int nextY) {
		pnlPlayerInfo.movePlayer(playerName, nextX , nextY);
		pnlMap.updatePiecePosition(playerName, new Position(nextX, nextY));
		pnlMap.repaint();
		update();
	}

	public void setLblNotice(String string) {
		lblNotice.setText(string);
	}

	public void giveBonus(String name, int bonus) {
		pnlPlayerInfo.giveBonus(name, bonus);
	}

	public void setArrived(String playerName) {
		pnlPlayerInfo.setPlayerEnd(playerName);
	}

	public void setWinner(String playerName) {
		pnlPlayerInfo.setWinner(playerName, () -> {
			controller.goToFirst();
		});
	}
}
