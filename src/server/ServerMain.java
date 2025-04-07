package server;

import java.io.IOException;

import server.network.GameServer;

public class ServerMain {
	
	public static void main(String[] args) {
		GameServer server = new GameServer();
		try {
			server.start(3000);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
