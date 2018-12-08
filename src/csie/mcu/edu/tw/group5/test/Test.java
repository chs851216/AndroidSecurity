package csie.mcu.edu.tw.group5.test;

import csie.mcu.edu.tw.group5.apk.APKDecoder;
import csie.mcu.edu.tw.group5.main.URLAnalyzing;

public class Test {

	public static void main(String[] args) throws Exception {
		// TODO Auto-generated method stub

		String fileName = "Rich.apk";
		APKDecoder apkDecoder = new APKDecoder(fileName, "Rich");
		apkDecoder.decoderSetting(true, true);
		//apkDecoder.decodeAPK();
		
		new URLAnalyzing("Status", "Rich", false).analyze();
	}
}