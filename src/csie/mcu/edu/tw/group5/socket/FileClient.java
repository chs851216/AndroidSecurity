package csie.mcu.edu.tw.group5.socket;

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.Socket;
import java.nio.charset.Charset;
import java.util.ArrayList;

public class FileClient {

	private static final int ZERO = 0x00000000;
	
	private int fileSize;
	private byte[] buffer = new byte [8192];
	
	private Socket socket;
	private FileInputStream FileInputStream;
	private DataInputStream DataInputStream;
	private DataOutputStream DataOutputStream;
	
	public FileClient(String host, int port, String file, int flag) {
		this.fileSize = 0;
		try {
			this.socket = new Socket(host, port);
			this.sendFile(file, flag);
		} catch (Exception e) {
			e.printStackTrace();
		}		
	}
	
	public boolean isConnected() {
		if(this.socket == null) {
			return false;
		}
		return true;
	}
	
	private void sendFile(String file, int flag) throws IOException {
		this.DataOutputStream = new DataOutputStream(this.socket.getOutputStream());
		this.DataInputStream = new DataInputStream(this.socket.getInputStream());
		this.FileInputStream = new FileInputStream(file);
		this.fileSize = this.FileInputStream.available();
		
		// 1 connect message
		this.receieveMessage();
		
		// 2 write flag
		System.out.println("flag: " + flag);
		this.DataOutputStream.writeByte(flag);
		
		// 3 write file size
		System.out.println("fileSize: " + this.fileSize);
		this.DataOutputStream.writeInt(this.fileSize);
		
		// 4 send file
		System.out.println("send file...");
		int len = 0;
		while(this.fileSize > 0) {
			len = this.FileInputStream.read(this.buffer, 0, this.buffer.length);
			this.DataOutputStream.write(this.buffer, 0, len);
			this.fileSize -= len;
		}
		System.out.println("send file successed.");
		
		/* 
		 * 5 6 receive decode message
		 */
		this.receieveMessage();
		this.receieveMessage();
		
		/*
		 * for ad detect
		 */
		if((flag & 1) == ZERO) {
			this.receieveMessage();
			this.receieveMessage();
		}
		
		/* 
		 * for url static analyze
		 */
		if((flag & 2) != ZERO) {
			// wot
			int size = this.DataInputStream.readInt();
			this.receieveMessage(size);
			
			System.out.println();
			
			// vt
			size = this.DataInputStream.readInt();
			this.receieveMessage(size);
		}
		
		this.close();
		System.out.println("connection is down .......");
	}
	
	private void close() throws IOException {
		this.FileInputStream.close();
		this.DataOutputStream.flush();
		this.DataOutputStream.close();
		this.DataInputStream.close();
	}
	
	private void receieveMessage() {
		String msg = "No Message!";
		try {
			msg = this.DataInputStream.readUTF();
			this.DataOutputStream.flush();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		System.out.println(msg);
	}
	
	private boolean receieveMessage(int size) {
		byte [] buffer = new byte [512];
		ArrayList<String> urlScore = new ArrayList<String>();
		
		String tmp;
		while(size > 0) {
			try {
				tmp = this.DataInputStream.readUTF();
				urlScore.add(tmp);
				size -= 1;
				System.out.println(tmp);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return false;
			}
		}
		
		return true;
	}
	
	public static void main(String[] args) {
		int flag = 3;
		new FileClient("120.125.80.30", 8080, "HONEYMOON_v1.0_apkpure.com.apk", flag);
	}
}