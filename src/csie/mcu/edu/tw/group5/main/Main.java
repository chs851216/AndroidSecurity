package csie.mcu.edu.tw.group5.main;

import csie.mcu.edu.tw.group5.socket.FileServer;

public class Main {

	public static void main(String[] args) {
		// TODO Auto-generated method stub

//		int port = 32954;
		int port = 8080;
		if(args.length > 0) {
			port = Integer.parseInt(args[0]);
		}
		new FileServer(port).handle();
	}
}