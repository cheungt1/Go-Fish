package game;

import java.io.*;
import java.net.Socket;

public class GameClient {
	private final static String serverHostIP = "";
	private final static int port = 0;
	
	public static void main(String[] args) {
		try {
			Socket socket = new Socket(serverHostIP, port);
			
			DataInputStream is = new DataInputStream(socket.getInputStream());
			DataOutputStream os = new DataOutputStream(socket.getOutputStream());
			
		} catch(IOException e) {
			e.printStackTrace();
		}
	}
}
