package server;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.IOException;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;

import server.network.GameServer;
import server.service.GameService;

public class ServerMain {
	   
	   public static void main(String[] args) {
	      JFrame frame = new JFrame("게임 서버");
	      frame.setBounds(200, 200, 800, 200);
	      
	      String str = JOptionPane.showInputDialog("설정 파일 위치");
	      if (str != null && !str.isBlank()) {
	         File file = new File(str);
	         if (file.exists()) {
	            GameService.accountFilePath = str;
	         }
	      }
	      
	      frame.addWindowListener(new WindowAdapter() {
	         @Override
	         public void windowClosing(WindowEvent e) {
	            System.out.println("SERVER EXIT");
	            System.exit(0);
	         }
	      });
	      
	      JLabel lblFilePath = new JLabel(GameService.accountFilePath);
	      lblFilePath.setSize(200, 200);
	      frame.add(lblFilePath);
	      
	      frame.setVisible(true);
	      GameServer server = new GameServer();
	      try {
	         server.start(3000);
	      } catch (IOException e) {
	         // TODO Auto-generated catch block
	         e.printStackTrace();
	      }
	      
	      frame.setVisible(true);
	   }
	}
