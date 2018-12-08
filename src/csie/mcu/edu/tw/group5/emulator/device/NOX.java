package csie.mcu.edu.tw.group5.emulator.device;

import java.util.ArrayList;

import csie.mcu.edu.tw.group5.command.Command;
import csie.mcu.edu.tw.group5.command.adb.Monkey;
import csie.mcu.edu.tw.group5.emulator.Emulator;

public class NOX extends Emulator{

	public NOX(String devicePort) {
		this.setEmulatorPort(devicePort);
		this.command = new Command();
		this.monkey = new Monkey(this.command);
		this.packageList = new ArrayList<>();
	}
}
