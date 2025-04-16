package server.service;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import common.dto.RoomBoardDto;
import common.dto.RoomPlayerDto;
import common.dto.UserDetailDto;
import common.model.Direction;
import common.network.GameMessage;
import server.model.Board;
import server.model.BonusCell;
import server.model.Cell;
import server.model.EndCell;
import server.model.EventCell;
import server.model.GameRoom;
import server.model.Player;
import server.network.ClientHandler;
import server.network.GameServer;

public class GameService {

   public static String accountFilePath = System.getProperty("user.home") + "\\g_accounts.txt";

	GameServer server;
	List<GameRoom> rooms;
	Set<String> currentPlayers;

	public GameService(GameServer server) {
		this.server = server;
		rooms = new ArrayList<GameRoom>();
		currentPlayers = new HashSet<String>();
	}

	/**
	 * 클라이언트가 보낸 방 생성 요청을 수락합니다
	 * 
	 * @param handler 클라이언트 핸들러
	 */
	public void acceptMakeRoom(String roomName) {
		server.broadcast(null, new GameMessage("add_room", roomName));
	}

	/**
	 * 클라이언트에게 주사위를 던질 차례임을 알립니다.
	 * 
	 */
	public void notifyDiceTurn(Player player) {
		List<ClientHandler> handlers = player.getRoom().getHandlers();
		System.out.println("이름은? " + player.getName());
		server.broadcast(handlers, new GameMessage("turn", player.getName()));
	}

	public void processMessage(GameMessage msg, ClientHandler handler) {
		Player player = handler.getPlayer();
		GameRoom room = player != null ? player.getRoom() : null;
		String[] usernamePassword;

		System.out.println("server recieved message: " + msg.type);
		switch (msg.type) {
		case "exit":
			server.removeHandler(handler);
		case "logout":
			if (player != null) {
				currentPlayers.remove(player.getName());
				if (room != null) {
					RoomPlayerDto dto = new RoomPlayerDto(room.getName(), player.getName(), player.isReadyToPlay());
					server.broadcast(null, new GameMessage("exit_room", dto));
					room.removePlayer(player);

					if (room.getPlayers().size() == 0) {
						room.setPlaying(false);
						rooms.remove(room);
						server.broadcast(null, new GameMessage("delete_room", room.getName()));
					} else if (room.isEnded()) {
						if (!room.isResultSaved()) {
							Player winner = room.getPlayers().get(0);
							for (Player p : room.getPlayers()) {
								if (winner.getScore() <= p.getScore()) {
									winner = p;
									p.setWinCount(p.getWinCount() + 1);
								}
								if (p.getScore() > p.getHighestScore())
									p.setHighestScore(p.getScore());
								p.setPlayCount(p.getPlayCount() + 1);
								save(p);
							}
							server.broadcast(room.getHandlers(), new GameMessage("winner", winner.getName()));
						}
					} else {
						String payload = room.getCurrentPlayer().getName();
						server.broadcast(room.getHandlers(), new GameMessage("turn", payload));
					}
				}
			}
			break;

		case "signup":
			usernamePassword = (String[]) msg.payload;
			player = signup(usernamePassword, handler);
			if (player == null) {
				handler.send(new GameMessage("notify", "회원가입 실패!"));
			}
			handler.setPlayer(player);
			handler.send(new GameMessage("signup_success", new UserDetailDto(player)));
			break;

		case "login":
			usernamePassword = (String[]) msg.payload;
			player = login(usernamePassword, handler);
			if (player == null) {
				handler.send(new GameMessage("login_fail", "올바르지 않은 아이디 또는 비밀번호입니다!"));
				break;
			}
			if (currentPlayers.contains(player.getName())) {
				handler.send(new GameMessage("login_fail", "이미 로그인 중입니다!"));
				break;
			}
			handler.setPlayer(player);
			currentPlayers.add(player.getName());
			handler.send(new GameMessage("login_success", new UserDetailDto(player)));
			break;

		case "list_room":
			System.out.println("room size: " + rooms.size());
			for (int i = 0; i < rooms.size(); i++) {
				room = rooms.get(i);
				List<String> playerNames = room.getPlayerNames();
				String memberStr = "";
				for (int j = 0; j < playerNames.size(); j++) {
					memberStr += playerNames.get(j);
				}
				handler.send(new GameMessage("add_room", room.getName() + '\n' + memberStr));
			}
			break;

		case "make_room":
			RoomBoardDto roomBoardDto = (RoomBoardDto) msg.payload;
			room = new GameRoom(roomBoardDto.roomName);
			Board board = new Board(roomBoardDto.tiles);
			room.setBoard(board);
			room.addPlayer(player);
			rooms.add(room);
			server.broadcast(null, new GameMessage("add_room", room.getName() + '\n' + player.getName()));
			handler.send(new GameMessage("enter_room", new RoomBoardDto(room.getName(), room.getBoardStrings())));
			break;

		case "enter_room":
			String roomName = (String) msg.payload;
			room = rooms.stream().filter(r -> r.getName().equals(roomName)).findFirst().orElse(null);
			if (room == null)
				break;
			if (room.isPlaying()) {
				handler.send(new GameMessage("notify", "이미 게임 진행 중인 방입니다."));
				break;
			}
			room.addPlayer(player);
			RoomPlayerDto dto = new RoomPlayerDto(roomName, player.getName(), false);
			handler.send(new GameMessage("enter_room", new RoomBoardDto(roomName, room.getBoardStrings())));
			System.out.println("server sent message: enter_room");
			server.broadcast(null, new GameMessage("add_player", dto));
			break;

		case "list_players":
			List<ClientHandler> handlers = room.getHandlers();
			RoomPlayerDto[] dtos = new RoomPlayerDto[handlers.size()];
			for (int i = 0; i < handlers.size(); i++) {
				Player p = handlers.get(i).getPlayer();
				dtos[i] = new RoomPlayerDto(room.getName(), p.getName(), p.isReadyToPlay());
			}
			handler.send(new GameMessage("players_list", dtos));
			break;

		case "ready_to_play":
			handlers = room.getHandlers();
			player.setReadyToPlay(true);
			dto = new RoomPlayerDto(room.getName(), player.getName(), true);
			server.broadcast(handlers, new GameMessage("ready_to_play", dto));
			break;

		case "start_game":
			handlers = room.getHandlers();
			if (room.isReadyToPlay()) {
				room.setPlaying(true);
				server.broadcast(handlers, new GameMessage("game_start", null));
				Player curP = room.getCurrentPlayer();
				for (Player p : room.getPlayers()) {
					p.setCurrentCell(room.getBoard().getCells().get(0));
				}
				server.broadcast(handlers, new GameMessage("turn", curP.getName()));
			}
			break;

		case "roll_dice":
			handlers = room.getHandlers();
			room.setDiceResult((int) (Math.random() * 6) + 1);
			Player curP = room.getCurrentPlayer();
			server.broadcast(handlers, new GameMessage("dice_result", curP.getName() + '\n' + room.getDiceResult()));
			break;

		case "move":
			handlers = room.getHandlers();
			Cell currCell = player.getCurrentCell();
			Direction dir = Direction.valueOf((String) msg.payload);
			if (!player.equals(room.getCurrentPlayer()))
				break;
			System.out.println(room.getDiceResult());
			if (room.getDiceResult() == 0)
				break;
			if (player.getRoom().isEnding() && currCell.getFd() != dir)
				break;
			if ((currCell = currCell.adjacent(dir)) == null)
				break;
			boolean moved = player.moveTo(dir);
			if (!moved)
				break;
			room.increaseMovement();
//			System.out.println(
//					player.getName() + ": " + player.getCurrentCell().getX() + ", " + player.getCurrentCell().getY());

			server.broadcast(handlers, new GameMessage("move", player.getName() + '\n' + dir));
			if (currCell instanceof BonusCell) {
				System.out.println("보너스 : " + ((BonusCell) currCell).getToolType());
				server.broadcast(handlers,
						new GameMessage("bonus", player.getName() + '\n' + ((BonusCell) currCell).getBonus()));
			} else if (currCell instanceof EndCell) {
				server.broadcast(handlers, new GameMessage("arrived", player.getName()));
				room.increaseEndCount();
				if (room.getEndCount() == 1) {
					server.broadcast(handlers, new GameMessage("bonus", player.getName() + '\n' + 300));
					server.broadcast(handlers,
							new GameMessage("notify", player.getName() + "은(는) 첫 번째로 도달: 300점 지급!!!"));
				} else if (room.getEndCount() == 2) {
					server.broadcast(handlers, new GameMessage("bonus", player.getName() + '\n' + 100));
					server.broadcast(handlers,
							new GameMessage("notify", player.getName() + "은(는) 두 번째로 도달: 100점 지급!!!"));
				}
				if (room.isEnded()) {
					Player winner = room.getPlayers().get(0);
					for (Player p : room.getPlayers()) {
						if (winner.getScore() <= p.getScore())
							winner = p;
						if (p.getScore() > p.getHighestScore())
							p.setHighestScore(p.getScore());
						p.setPlayCount(p.getPlayCount() + 1);
						save(p);
					}
					winner.setWinCount(winner.getWinCount() + 1);
					save(winner);
					server.broadcast(handlers, new GameMessage("winner", winner.getName()));
					room.setResultSaved(true);
					break;
				}
				room.setNextPlayer();
				curP = room.getCurrentPlayer();
				server.broadcast(handlers, new GameMessage("turn", curP.getName()));
				break;
			} else if (currCell instanceof EventCell) {
				if (!room.isEnding()) {
					server.broadcast(handlers, new GameMessage("minigame", player.getName()));
					break;
				}
			}
			if (room.getMovement() == room.getDiceResult()) {
				room.setNextPlayer();
				curP = room.getCurrentPlayer();
				server.broadcast(handlers, new GameMessage("turn", curP.getName()));
			}
			break;

		case "minigame":
			handlers = player.getRoom().getHandlers();
			int bonus = (int) msg.payload;
			server.broadcast(handlers, new GameMessage("notice", ""));
			server.broadcast(handlers, new GameMessage("bonus", player.getName() + '\n' + bonus));
			if (room.getMovement() == room.getDiceResult()) {
				room.setNextPlayer();
				curP = room.getCurrentPlayer();
				server.broadcast(handlers, new GameMessage("turn", curP.getName()));
			}
			break;

		case "use_item":
			int index = (Integer) msg.payload;
			player.useItem(index);
			server.broadcast(room.getHandlers(), new GameMessage("item_used", player));
			break;

		case "select_map":
			String mapName = (String) msg.payload;
			room.setMap(mapName);
			server.broadcast(room.getHandlers(), new GameMessage("map_selected", mapName));
			break;
		}
	}

	private void save(Player p) {
		List<String> lines;
		try {
			lines = Files.readAllLines(Paths.get(ACCOUNT_FILE_PATH));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return;
		}

		try (BufferedWriter bw = new BufferedWriter(new FileWriter(ACCOUNT_FILE_PATH))) {
			for (String line : lines) {
				String[] split = line.split(";");
				if (split[0].equals(p.getName())) {
					split[2] = String.valueOf(p.getHighestScore());
					split[3] = String.valueOf(p.getWinCount());
					split[4] = String.valueOf(p.getPlayCount());
					line = String.join(";", split);
				}
				bw.write(line);
				bw.newLine();
				bw.flush();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public Player signup(String[] usernamePassword, ClientHandler handler) {
		File file = new File(ACCOUNT_FILE_PATH);
		FileReader fr = null;
		BufferedReader br = null;
		FileWriter fw = null;
		BufferedWriter bw = null;
		try {
			if (file.exists()) {
				fr = new FileReader(file);
				br = new BufferedReader(fr);
				String s = null;
				while ((s = br.readLine()) != null) {
					String[] uP = s.split(";");
					if (uP.length != 5)
						return null;
					if (uP[0].equals(usernamePassword[0]) && uP[1].equals(usernamePassword[1])) {
						return null;
					}
				}
			}
			fw = new FileWriter(file, file.exists());
			bw = new BufferedWriter(fw);
			bw.write(usernamePassword[0] + ';' + usernamePassword[1] + ";0;0;0\n");
			bw.flush();
			return new Player(usernamePassword[0], handler);
		} catch (IOException e) {
			// TODO: handle exception
		} finally {
			if (file.exists()) {
				try {
					if (br != null)
						br.close();
					if (fr != null)
						fr.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			try {
				if (bw != null)
					bw.close();
				if (fw != null)
					fw.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		return null;
	}

	public Player login(String[] usernamePassword, ClientHandler handler) {
		File file = new File(ACCOUNT_FILE_PATH);
		FileReader fr = null;
		BufferedReader br = null;
		if (!file.exists())
			return null;
		try {
			fr = new FileReader(file);
			br = new BufferedReader(fr);
			String s = null;
			while ((s = br.readLine()) != null) {
				String[] uP = s.split(";");
				if (uP.length != 5)
					return null;
				if (uP[0].equals(usernamePassword[0]) && uP[1].equals(usernamePassword[1])) {
					return new Player(uP[0], handler, Integer.parseInt(uP[2]), Integer.parseInt(uP[3]),
							Integer.parseInt(uP[4]));
				}
			}
		} catch (IOException e) {
			// TODO: handle exception
		} finally {
			try {
				br.close();
				fr.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return null;
	}
}
