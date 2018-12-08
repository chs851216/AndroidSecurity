package csie.mcu.edu.tw.group5.command;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class Command {

	private static final String cmdPrefix = "cmd.exe /c ";
	private Runtime runtime = Runtime.getRuntime();
	private Process commandProcess;
	
	public String command;
	
	public Command() {
		this.command = new String();
	}
	
	public void setCommand(String cmd) {
		this.command = cmd;
	}
	
	public String getCommand() {
		return this.command;
	}
	
	public Process getCommandProcess() {
		return this.commandProcess;
	}
	
	public void runCommand(String cmd) {
		try {
			this.setCommand(cmd);
			this.commandProcess = this.runtime.exec(cmdPrefix + this.command);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			this.commandProcess.destroyForcibly();
		}
	}
	
	public String getCommandInputStream() {
		StringBuffer StringBuffer = new StringBuffer();
		InputStream InputStream = this.commandProcess.getInputStream();
		try {
			BufferedReader BufferedReader = new BufferedReader(new InputStreamReader(InputStream, "UTF-8"));
			
			String tmp = null;
			while((tmp = BufferedReader.readLine()) != null) {
				if(!"".equals(tmp)) {
					StringBuffer.append(tmp + "\n");
				}
			}
			
			return StringBuffer.toString();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			this.commandProcess.destroyForcibly();
			return null;
		}
	}
	
	public static void main(String[] args) throws InterruptedException {
		// TODO Auto-generated method stub
		
	}
}