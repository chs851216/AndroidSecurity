package csie.mcu.edu.tw.group5.emulator;

import java.util.ArrayList;
import java.util.List;

import csie.mcu.edu.tw.group5.command.Command;
import csie.mcu.edu.tw.group5.command.adb.Monkey;

public abstract class Emulator implements EmulatorOption{

	private final String emulatorIP = "127.0.0.1";
//	private final String adbPath = "D:/\"Program Files\"/Nox/bin/";
	
	private String emulatorBridgeIP;
	protected String emulatorPort;
	protected Command command;
	protected Monkey monkey;
	protected List<String> packageList;
	
	protected void setEmulatorPort(String devicePort) {
		this.emulatorPort = devicePort;
	}
	
	protected String getEmulatorPort() {
		return this.emulatorPort;
	}
	
	protected Command getCommand() {
		return this.command;
	}
	
	protected List<String> getPackageList() {
		return this.packageList;
	}
	
	public String getEmulatorBridgeIP() {
		String netTable = "";
		this.command.runCommand(adbPath + "adb " + this.emulatorIP + ":" + this.emulatorPort + " shell netcfg");
		netTable = this.command.getCommandInputStream();
		
		String[] ip = netTable.split("\n")[2].split(" ");
		List<String> netInfo = new ArrayList<>();
		for(int i=0; i<ip.length; i++) {
			if(!"".equals(ip[i])) {
				netInfo.add(ip[i]);
			}
		}
		this.emulatorBridgeIP = netInfo.get(2).substring(0, netInfo.get(2).indexOf("/"));
		return this.emulatorBridgeIP;
	}
	
	public String getPackageName(String apkPath) throws InterruptedException {
		int returnValue = 0;
		String trash = null;
		String pkgName = "";
		Process p = null;
		
		List<String> beforeInstall = this.getPackages();
		List<String> afterInstall = null;
		
		p = this.command.getCommandProcess();
		returnValue = p.waitFor(); p.destroy();
		if(returnValue == 0) {
			this.installAPK(apkPath);
			trash = this.command.getCommandInputStream();
		}
		
		p = this.command.getCommandProcess();
		returnValue = p.waitFor();
		if(returnValue == 0) {
			afterInstall = this.getPackages();
		}
		
		p = this.command.getCommandProcess();
		returnValue = p.waitFor();
		if(returnValue == 0 && afterInstall != null) {
			System.out.println(apkPath + " install successfully !!!");
			afterInstall.removeAll(beforeInstall);
			if(afterInstall.size() > 0) {
				pkgName = afterInstall.get(0).substring(pkgName.indexOf(":")+1, pkgName.length());
				return pkgName;
			}
		}
		return null;
	}
	
	public void installAPK(String apkPath) {
		String install = adbPath + "adb -s " + this.emulatorIP + ":" + this.emulatorPort + " ";
		this.command.runCommand(install + "install -r " + apkPath);
	}
	
	public void unInstallAPK(String pkgName) {
		String unInstall = adbPath + "adb -s " + this.emulatorIP + ":" + this.emulatorPort + " ";
		this.command.runCommand(unInstall + "uninstall " + pkgName);
	}
	
	// synchronized
	private List<String> getPackages() {
		List<String> packageList = new ArrayList<>();
		String listPkg = adbPath + "adb -s " + this.emulatorIP + ":" + this.emulatorPort + " shell ";
		this.command.runCommand(listPkg + "pm list packages");
		
		this.packageList.removeAll(this.packageList);
		
		String commandInputStream = this.command.getCommandInputStream();
		for(String pkg : commandInputStream.split("\n")) {
			packageList.add(pkg.trim());
		}
		this.packageList.addAll(packageList);
		
		return packageList;
	}
	
	/*
	 * the new one
	 */
	public void runMonkey(String pkgName) throws InterruptedException {
		int returnVal = 0;
		String trash = null;
		Process p = null;
		double sta = System.currentTimeMillis();
		this.monkey.monkeyTest(adbPath + "adb -s " + this.emulatorIP + ":" + this.emulatorPort + " shell", pkgName);
		trash = this.command.getCommandInputStream();
		
		System.out.println(trash);
		
		p = this.command.getCommandProcess();
		returnVal = p.waitFor();
		double end = System.currentTimeMillis();
		if(returnVal == 0) {
			System.out.println("Monkey test is end !");
			System.out.println("Monkey test cost " + (end-sta)/1000 + " s.");
		}
	}	
	
	/*
	 * the old method
	 */
	protected void runMonkey(String pkgName, int eventCount) throws InterruptedException {
		int returnVal = 0;
		String trash = null;
		Process p = null;
		this.monkey(pkgName, eventCount);
		trash = this.command.getCommandInputStream();
//		System.out.println(trash);
		
		p = this.command.getCommandProcess();
		returnVal = p.waitFor();
		if(returnVal == 0) {
			this.runKeyEvent("3");
		}
		
		p = this.command.getCommandProcess();
		returnVal = p.waitFor();
		if(returnVal == 0) {
			System.out.println("Monkey test is end !");
		}
	}
	
	protected void monkey(String pkgName, int eventCount) {
		String monkey = adbPath + "adb -s " + this.emulatorIP + ":" + this.emulatorPort + " shell ";
		this.command.runCommand(monkey + "monkey -p " + pkgName + " -v " + eventCount);
	}
	
	protected void runKeyEvent(String keyCode) {
		String keyEvent = adbPath + "adb -s " + this.emulatorIP + ":" + this.emulatorPort + " shell ";
		this.command.runCommand(keyEvent + "input keyevent " + keyCode);
	}
}