package client.controller;

import java.util.List;

import client.model.Board;
import client.model.GameRoom;
import client.model.Player;
import client.model.Tile;
import client.network.GameClient;
import client.view.GameViewer;
import client.view.map.LoadResult;
import client.view.map.Position;
import common.dto.RoomBoardDto;
import common.dto.RoomPlayerDto;
import common.dto.UserDetailDto;
import common.model.Direction;
import common.network.GameMessage;

/**
 * GameController 클래스는 게임의 주요 로직을 관리하며, 네트워크 클라이언트와 뷰(View) 간의 상호작용을 중재합니다.
 */
public class GameController {

	// 서버와 통신을 담당하는 GameClient 객체
	private GameClient client;

	// 사용자 인터페이스를 담당하는 GameViewer 객체
	private GameViewer viewer;

	private GameRoom room;

	private Player player;
	private String username;

	private Stage stage = Stage.FIRST;

	private int finishedPlayerCount = 0;

	/**
	 * GameController 생성자. 네트워크 클라이언트를 초기화하고 컨트롤러를 설정합니다.
	 *
	 * @param client 서버와 통신할 GameClient 객체
	 */
	public GameController(GameClient client) {
		this.client = client; // 클라이언트 객체 저장
		client.setController(this); // 클라이언트에 현재 컨트롤러를 설정
	}

	/**
	 * GameViewer 객체를 설정하는 메서드.
	 *
	 * @param viewer 사용자 인터페이스를 담당하는 GameViewer 객체
	 */
	public void setViewer(GameViewer viewer) {
		this.viewer = viewer;
	}

	public void signup(String[] usernamePassword) {
		client.send(new GameMessage("signup", usernamePassword));
	}

	public void login(String[] usernamePassword) {
		client.send(new GameMessage("login", usernamePassword));
		username = usernamePassword[0];
	}

	public void logout() {
		client.send(new GameMessage("logout", null));
		username = null;
		viewer.logout();
	}

	/**
	 * 방 생성을 요청합니다.
	 * 
	 * @param name 방 이름
	 */
	public void makeRoom(String name, Board board) {
		client.send(new GameMessage("make_room", new RoomBoardDto(name, board.toStringArray())));
	}

	/**
	 * 방에 참가를 요청합니다.
	 * 
	 * @param name 방 이름
	 */
	public void enterRoom(String name) {
		client.send(new GameMessage("enter_room", name));
	}

	/**
	 * 입장한 방에서 게임 시작 준비 완료했다고 알립니다.
	 *
	 */
	public void startGame() {
		client.send(new GameMessage("start_game", null));
	}

	/**
	 * 게임 중 자신의 턴일 때 움직이는 방향을 한 칸씩 결정합니다.
	 * 
	 * @param dir 이동 방향
	 */
	public void move(String dir) {
		client.send(new GameMessage("move", dir));
	}

	/**
	 * 이벤트 결과로 얻은 포인트 결과를 알립니다.
	 * 
	 * @param bonusPoint 이벤트 결과로 얻은 보너스 포인트
	 */
	public void notifyEventResult(int bonusPoint) {
		client.send(new GameMessage("minigame", bonusPoint));
	}

	/**
	 * 주사위를 굴리는 요청을 서버에 전송합니다.
	 */
	public void rollDice() {
		client.send(new GameMessage("roll_dice", null));
	}

	/**
	 * 게임 종료 요청을 서버에 전송하고 클라이언트를 종료함.
	 */
	public void exitGame() {
		if (!client.isConnected())
			System.exit(0);
		client.send(new GameMessage("exit", null)); // 게임 종료 요청 전송
		client.setListening(false); // 메시지 수신 비활성화
	}

	public void setReadyToPlay() {
		client.send(new GameMessage("ready_to_play", null));
	}

	/**
	 * 서버로부터 수신된 게임 메시지를 처리하는 메서드.
	 *
	 * @param msg 서버로부터 수신된 GameMessage 객체
	 */
	public void handleGameMessage(GameMessage msg) {

		System.out.println("client recieved message: " + msg.type);
		switch (msg.type) {
		case "signup_success":
			UserDetailDto userDetail = (UserDetailDto) msg.payload;
			String name = userDetail.getName();
			viewer.loginSuccess(name);
			player = new Player(username);
			client.send(new GameMessage("list_room", null));
			break;
		case "login_success":
			userDetail = (UserDetailDto) msg.payload;
			name = userDetail.getName();
			viewer.loginSuccess(userDetail);
			player = new Player(name);
			client.send(new GameMessage("list_room", null));
			break;
		case "login_fail":
			viewer.loginFail((String) msg.payload);
			break;
		case "room_list":
			String[] roomInfos = (String[]) msg.payload;
			for (String roomInfo : roomInfos) {
				String roomName = roomInfo.split("\n")[0];
				String roomMembers = roomInfo.split("\n")[1];
				viewer.addBtnRoom(roomName, roomMembers);
			}
			break;
		case "add_room":
			String roomInfo = (String) msg.payload;
			String[] splitStr = roomInfo.split("\n");
			String roomName = splitStr[0];

			String roomMembers = splitStr.length > 1 ? splitStr[1] : "";
			viewer.addBtnRoom(roomName, roomMembers);
			break;
		case "delete_room":
			roomName = (String) msg.payload;
			if (stage == Stage.FIRST || roomName.equals(room.getName()))
				viewer.deleteBtnRoom(roomName);
			break;
		case "add_player":
			RoomPlayerDto dtoPlayer = (RoomPlayerDto) msg.payload;
			if (stage == Stage.SECOND && dtoPlayer.getRoomName().equals(room.getName())) {
				if (dtoPlayer.getPlayerName().equals(username))
					break;
				viewer.addLblPlayersInRoom(dtoPlayer.getPlayerName(), dtoPlayer.isReadyToPlay());
				room.getPlayers().add(new Player(dtoPlayer.getPlayerName()));
				if (dtoPlayer.isReadyToPlay())
					room.setReadyToPlayCount(room.getReadyToPlayCount() + 1);
				else if (room.getReadyToPlayCount() < room.getPlayers().size()) {
					viewer.setStartToPlayBtnAvailable(false);
				}
			} else if (stage == Stage.FIRST && room == null)
				viewer.addPlayersInOtherRoom(dtoPlayer.getRoomName(), dtoPlayer.getPlayerName());
			break;
		case "enter_room":
			RoomBoardDto boardDto = (RoomBoardDto) msg.payload;
			if (stage == Stage.FIRST) {
				stage = Stage.SECOND;
				viewer.setMainPanel(2);
				room = new GameRoom(boardDto.roomName);
				LoadResult loadResult = LoadResult.fromStringArray(boardDto.tiles);
				room.setLoadResult(loadResult);
				client.send(new GameMessage("list_players", null));
				room.getPlayers().add(player);
				player.setRoom(room);
				break;
			}
		case "players_list":
			RoomPlayerDto[] dtos = (RoomPlayerDto[]) msg.payload;
			if (stage == Stage.FIRST) {
				for (RoomPlayerDto dto : dtos) {
					viewer.addPlayersInOtherRoom(dto.getRoomName(), dto.getPlayerName());
				}
			} else if (stage == Stage.SECOND) {
				System.out.println(dtos.length);
				for (RoomPlayerDto dto : dtos) {
					if (dto.getRoomName().equals(room.getName())) {
						viewer.addLblPlayersInRoom(dto.getPlayerName(), dto.isReadyToPlay());
						if (!dto.getPlayerName().equals(player.getName())) {
							Player other = new Player(dto.getPlayerName());
							other.setRoom(room);
							room.getPlayers().add(other);
						}
						if (dto.isReadyToPlay())
							room.setReadyToPlayCount(room.getReadyToPlayCount() + 1);
					}
				}
			}
			break;
		case "ready_to_play":
			dtoPlayer = (RoomPlayerDto) msg.payload;
			Player tmpPlayer = room.getPlayers().stream().filter(p -> p.getName().equals(dtoPlayer.getPlayerName()))
					.findFirst().orElse(null);
			if (tmpPlayer == null) {
				break;
			}
			room.setReadyToPlayCount(room.getReadyToPlayCount() + 1);
			viewer.setLblPlayersInRoomReady(tmpPlayer.getName(), dtoPlayer.isReadyToPlay());
			if (room.getReadyToPlayCount() == room.getPlayers().size())
				viewer.setStartToPlayBtnAvailable(true);
			break;
		case "game_start":
			if (stage == Stage.SECOND) {
				stage = Stage.PLAYING;
				for (Player p : room.getPlayers())
					p.setCurrentTile(room.getLoadResult().board.getStartTile());
				viewer.setMainPanel(3);
				viewer.setMapPanel(room.getLoadResult().board, room.getLoadResult().tileMap);
				viewer.setGamePlayers(room.getPlayerNames());
			}
			break;
		case "turn":
			if (stage == Stage.PLAYING) {
				name = (String) msg.payload;
				viewer.setTurn(name, name.equals(player.getName()));
			}
			break;
		case "dice_result":
			if (stage == Stage.PLAYING) {
				String payload = (String) msg.payload;
				int res = Integer.valueOf(payload.split("\n")[1]);
				viewer.setDiceResult(res, player.getName().equals(payload.split("\n")[0]));
			}
			break;
		case "move":
			if (stage == Stage.PLAYING) {
				String payload = (String) msg.payload;
				String[] tmpStrings = payload.split("\n");
				name = tmpStrings[0];
				Direction dir = Direction.valueOf(tmpStrings[1]);
				Player targetPlayer = null;
				for (Player p : room.getPlayers()) {
					if (p.getName().equals(name)) {
						targetPlayer = p;
						break;
					}
				}
				if (targetPlayer == null)
					break;
				Tile currTile = targetPlayer.getCurrentTile();
				Position currPos = room.getLoadResult().positionMap.get(currTile);
				Tile nextTile = currTile.getAdjacentTile(dir);
				if (nextTile == null)
					break;
				targetPlayer.setCurrentTile(nextTile);
				viewer.movePlayerSingleStep(tmpStrings[0], currPos.x, currPos.y, dir,
						(nextTile.getConnectedTile() != null && nextTile.getConnectedTile().equals(currTile) ? 2 : 1));
			}
			break;
		case "minigame":
			if (stage == Stage.PLAYING) {
				name = (String) msg.payload;
				if (name.equals(player.getName())) {
					player.getCurrentTile().onLand(player, this);
				} else {
					viewer.setLblNotice(player.getName() + "이 미니게임 중");
				}
			}
			break;
		case "bonus":
			if (stage == Stage.PLAYING) {
				String payload = (String) msg.payload;
				String[] tmpStrings = payload.split("\n");
				name = tmpStrings[0];
				int bonus = Integer.valueOf(tmpStrings[1]);
				Player targetPlayer = null;
				for (Player p : room.getPlayers()) {
					if (p.getName().equals(name)) {
						targetPlayer = p;
						break;
					}
				}
				if (targetPlayer == null)
					break;
				viewer.giveBonus(name, bonus);
			}
			break;
		case "arrived":
			if (stage == Stage.PLAYING) {
				viewer.setArrived((String) msg.payload);
			}
			break;
		case "winner":
			if (stage == Stage.PLAYING) {
				viewer.setWinner((String) msg.payload);
			}
			break;
		case "exit_room":
			dtoPlayer = (RoomPlayerDto) msg.payload;
			if (stage == Stage.PLAYING) {
				viewer.notify(dtoPlayer.getPlayerName() + "가 방에서 나갔습니다!");
				room.setReadyToPlayCount(room.getReadyToPlayCount() - 1);
			}
			if (stage == Stage.FIRST) {
				viewer.removePlayerInOtherRoom(dtoPlayer.getRoomName(), dtoPlayer.getPlayerName());
			} else if (stage == Stage.SECOND) {
				List<Player> players = room.getPlayers();
				name = dtoPlayer.getPlayerName();
				Player targetPlayer = null;
				for (Player p : room.getPlayers()) {
					if (p.getName().equals(name)) {
						targetPlayer = p;
						break;
					}
				}
				if (targetPlayer == null) {
					break;
				}
				players.remove(targetPlayer);
				viewer.removePlayer(targetPlayer.getName());
				if (dtoPlayer.isReadyToPlay()) {
					room.setReadyToPlayCount(room.getReadyToPlayCount() - 1);
					if (room.getReadyToPlayCount() >= room.getPlayers().size()) {
						viewer.setStartToPlayBtnAvailable(true);
					}
				}
			}
			break;
		case "notify":
			viewer.notify((String) msg.payload);
			break;
		case "notice":
			viewer.setLblNotice((String) msg.payload);
		default:
			System.out.println("알 수 없는 메시지 타입: " + msg.type);
			break;
		}
	}
	
	public void goToFirst() {
		stage = Stage.FIRST;
		logout();
		viewer.setMainPanel(1);
	}

	public void showRoomsList(GameRoom[] rooms, int loggedIn) {
		String[] roomButtonTexts = new String[rooms.length];
		for (int i = 0; i < rooms.length; i++) {
			GameRoom room = rooms[i];
			String roomName = room.getName();
			String[] playerNames = (String[]) room.getPlayerNames().toArray();
			roomButtonTexts[i] = roomName + "\n";
			for (String playerName : playerNames) {
				roomButtonTexts[i] += ", " + playerName;
			}
		}
	}

	static enum Stage {
		FIRST, SECOND, PLAYING
	}
}
