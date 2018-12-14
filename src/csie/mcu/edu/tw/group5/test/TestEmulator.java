package csie.mcu.edu.tw.group5.test;

import csie.mcu.edu.tw.group5.emulator.Emulator;
import csie.mcu.edu.tw.group5.emulator.device.NOX;

public class TestEmulator {

	public static void main(String[] args) {
		// TODO Auto-generated method stub

		String port = "62025";
		Emulator nox = new NOX(port);
		String ip = nox.getEmulatorBridgeIP();
		System.out.println(ip);
	}
}