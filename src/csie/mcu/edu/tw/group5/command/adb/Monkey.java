package csie.mcu.edu.tw.group5.command.adb;

import csie.mcu.edu.tw.group5.command.Command;

public class Monkey {

	private final String monkey = " monkey";
	
	private String momkeyInstruction = new String();
	
	private int eventCount;
	private int intervalTime;
	private Command command;
	
	public Monkey(Command command) {
		this.momkeyInstruction += this.monkey;
		this.command = command;
		this.intervalTime = 0;
	}
	
	public void monkeyTest(String cmd, String pkgName) {
		this.setPackageName(pkgName);
		this.setEventRandomSeed();
		this.setIntervalTime(60);
		this.setEventTimes(10000);
		this.setSystemKeyEventPercent(0);
		this.setIgnoreTimeout();
		this.setEventLogLevel(2);
		this.momkeyInstruction += this.eventCount;
		
		System.out.println("Monkey Instruction: " + cmd + this.momkeyInstruction);
		this.command.runCommand(cmd + this.momkeyInstruction);
	}
	
	/*
	 * set the event log's level, the number higher the description is more detailed
	 */
	public void setEventLogLevel(int level) {
		this.momkeyInstruction += " ";
		if(level <= 3) {
			for(int i=0; i<level; i++) {
				this.momkeyInstruction += "-v";
			}
		}
		else {
			System.out.println("Log level only allow less than 3 !");
			this.momkeyInstruction += "-v";
		}
		this.momkeyInstruction += " ";
	}
	
	public void setEventRandomSeed() {
		this.momkeyInstruction += (" -s " + new java.util.Random().nextInt());
	}
	
	/*
	 * set package name you want to test
	 */
	public void setPackageName(String pkgName) {
		this.momkeyInstruction += (" -p " + pkgName);
	}
	
	/*
	 * set the interval time between every event
	 */
	public void setIntervalTime(int intervalTime) {
		this.momkeyInstruction += (" --throttle " + intervalTime);
	}
	
	/*
	 * set the percentage of touch event
	 */
	public void setTouchEventPercent(int percent) {
		this.momkeyInstruction += (" --pct-touch " + percent);
	}
	
	/*
	 * set the percentage of system key, like home, back etc...
	 */
	public void setSystemKeyEventPercent(int percent) {
		this.momkeyInstruction += (" --pct-syskeys " + percent);
	}
	
	/*
	 * set the percentage of a series event action
	 */
	public void setEventActionPercent(int percent) {
		this.momkeyInstruction += (" --pct-motion " + percent);
	}
	
	/*
	 * set the monkey test to ignore timeout error
	 */
	public void setIgnoreTimeout() {
		this.momkeyInstruction += (" --ignore-timeouts");
	}
	

	
	public void setEventTimes(int executeTimes) {
		this.eventCount = executeTimes;
	}
	
	public int getEventTimes() {
		return this.eventCount;
	}
	
	public void setCommand(Command command) {
		this.command = command;
	}
	
	public Command getCommand() {
		return this.command;
	}
}