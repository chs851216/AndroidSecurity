package csie.mcu.edu.tw.group5.emulator;

import java.util.HashMap;
import java.util.Map;

public class EmulatorManager implements EmulatorOption {

	/*
	 * Map <deviccePort, emulator>
	 */
	private Map<String, Emulator> emulatorMap;
	
	/*
	 * Map <deviccePort, isUse>
	 */
	private Map<String, Boolean> emulatorIsUsed;
	
	private String emulatorList;
	
	public EmulatorManager() {
		this.emulatorList = new String();
		this.emulatorMap = new HashMap<>();
		this.emulatorIsUsed = new HashMap<>();
	}
	
	public void addEmulator(Emulator emulator) {
		this.emulatorMap.put(emulator.getEmulatorPort(), emulator);
		this.emulatorIsUsed.put(emulator.getEmulatorPort(), false);
	}
	
	public Emulator getEulator(String port) {
		this.emulatorIsUsed.replace(port, true);
		return this.emulatorMap.get(port);
	}
	
	public boolean getEmulatorIsUsed(String port) {
		if(this.emulatorMap.get(port) != null) {
			return this.emulatorIsUsed.get(port);
		}
		return false;
	}
	
	public void releaseEmulator(String port) {
		this.emulatorIsUsed.replace(port, false);
	}
	
	public String showEmulatorList() {
		this.emulatorMap.forEach((emulatorName, emulator) -> {
			this.emulatorList += (emulatorName + "\n");
		});
		
		return this.emulatorList;
	}
}