package csie.mcu.edu.tw.group5.apk;

import java.io.File;
import java.io.IOException;

import brut.androlib.AndrolibException;
import brut.androlib.ApkDecoder;

public class APKDecoder {

	private File apkFile;
	private File outputDireatory;
	private File frameworkFile;
	private ApkDecoder apkDecoder = new ApkDecoder();
	
	public File getApkFile() {
		return this.apkFile;
	}
	
	public void setApkFile(String path) {
		this.apkFile = new File(path);
	}
	
	public File getOutputDireatory() {
		return this.outputDireatory;
	}
	
	public void setOutputDireatory(String outputDir) {
		this.outputDireatory = new File(outputDir);
	}
	
	public File getFrameworkFile() {
		return this.frameworkFile;
	}
	
	public void setFrameworkFile(String path) {
		this.frameworkFile = new File(path);
	}
	
	public APKDecoder() throws Exception {
		this.apkFile =  getApkFile();
		this.outputDireatory = getOutputDireatory();
		if(!this.apkFile.exists()) {
			System.out.println("File is not exist!");
			throw new IOException();
		}
	}
	
	public APKDecoder(String path, String outputDir) throws Exception {
		this.apkFile = new File(path);
		this.outputDireatory = new File(outputDir);
		if(!this.apkFile.exists()) {
			System.out.println("File is not exist!");
			throw new IOException();
		}
	}
	
	public void decoderSetting(boolean isDecodeAssets, boolean isDecodeResource) throws AndrolibException {
		if(isDecodeAssets) {
			this.apkDecoder.setDecodeAssets(ApkDecoder.DECODE_ASSETS_NONE);
		}
		if(isDecodeResource) {
			this.apkDecoder.setDecodeResources(ApkDecoder.DECODE_RESOURCES_NONE);
		}
		
		this.apkDecoder.setApkFile(this.apkFile);
		this.apkDecoder.setOutDir(this.outputDireatory);
	}
	
	public void decodeAPK() throws Exception {
		System.out.println(this.apkFile.getPath() + " decoding start...");
		this.apkDecoder.decode();
		this.apkDecoder.close();
		System.out.println(this.apkFile.getPath() + " decoding complete!");
	}
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}
}