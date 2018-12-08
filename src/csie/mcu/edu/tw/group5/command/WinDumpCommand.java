package csie.mcu.edu.tw.group5.command;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class WinDumpCommand implements Runnable{
	
	public static final String cmdPrefix = "cmd.exe /c ";
	public static final String winDumpPath = "D:/WinDump ";
	private Runtime runtime = Runtime.getRuntime();
	private Process commandProcess;
	private StringBuffer streamData;
	private String windumpPath;
	private String command;
	
	private boolean isActive;
	
	public WinDumpCommand(String windumpPath, String command) {
		// for class extends Thread
		// super("WinDump");
		this.windumpPath = windumpPath;
		this.setCommand(command);
		this.streamData = new StringBuffer();
		this.isActive = true;
	}
	
	public WinDumpCommand(String command) {
		// for class extends Thread
		// super("WinDump");
		this.setCommand(command);
		this.streamData = new StringBuffer();
		this.isActive = true;
	}
	
	public void setCommand(String cmd) {
		this.command = cmd;
	}
	
	public String getCommand() {
		return this.command;
	}
	
	private void runCommand() {
		String cmd = cmdPrefix + winDumpPath + this.command;
		try {
			this.commandProcess = this.runtime.exec(cmd);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			this.commandProcess.destroyForcibly();
		}
	}
	
	public void terminated(String packetPath){
		this.isActive = false;
		// for class extends Thread
		//this.interrupt();
		
		try {
			// "C:/Users/User/Desktop/ddd.txt"
			FileWriter FileWriter = new FileWriter("C:/Users/User/Desktop/ddad.txt");
			BufferedWriter BufferedWriter = new BufferedWriter(FileWriter);
			BufferedWriter.write(this.streamData.toString());
			BufferedWriter.flush();
			BufferedWriter.close();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println("WinDump saving completed !!!!");
	}
	
	@Override
	public void run() {
		try {
			String tmp = null;
			this.runCommand();
			InputStream InputStream = this.commandProcess.getInputStream();
			BufferedReader BufferedReader = new BufferedReader(new InputStreamReader(InputStream, "UTF-8"));
			while(this.isActive && (tmp = BufferedReader.readLine()) != null) {
				if(!"".equals(tmp)) {
					this.streamData.append(tmp + "\n");
				}
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}