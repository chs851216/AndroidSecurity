package csie.mcu.edu.tw.group5.socket;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import csie.mcu.edu.tw.group5.ad.AdList;
import csie.mcu.edu.tw.group5.apk.APKDecoder;
import csie.mcu.edu.tw.group5.command.WinDumpCommand;
import csie.mcu.edu.tw.group5.emulator.Emulator;
import csie.mcu.edu.tw.group5.emulator.EmulatorManager;
import csie.mcu.edu.tw.group5.emulator.device.NOX;
import csie.mcu.edu.tw.group5.file.FileNameManager;
import csie.mcu.edu.tw.group5.main.URLAnalyzing;

public class FileServer {

	private static final int ZERO = 0x00000000;
	
	private int fileSize;
	
	private ServerSocket serverSocket;
	private FileNameManager fileNameManager;
	private EmulatorManager EmulatorManager;

	public FileServer(int port) {
		this.fileSize = 0;
		this.fileNameManager = new FileNameManager();
		this.EmulatorManager = new EmulatorManager();
		this.EmulatorManager.addEmulator(new NOX("62025"));
		try {
			this.serverSocket = new ServerSocket(port);
		} catch (IOException e){
			e.printStackTrace();
		}
	}
	
	public void handle() {
		System.out.println(new Date().toString() + ", Waiting for connection...");
		while(true) {
			try {
				Socket clientSocket = this.serverSocket.accept();
				new Thread(new Runnable(){
                    @Override
                    public void run() {
						try{
							handleAPK(clientSocket);
						}catch(IOException e){e.printStackTrace();}
                    }
                }).start();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	private void loggerInformation(String msg) {
		String Date = new Date().toString();
		System.out.println(Date + msg);
	}

	private void handleAPK(Socket clientSocket) throws IOException {
		/*
		 * initialize data stream
		 */
		String result = "";
		String client_IP = "";
		String date = new Date().toString();
		String fileName = "";
		byte flag = 0;
		boolean isURLStaticAnalyze = false;
		Thread dynamicThread;
		
		DataInputStream DataInputStream = new DataInputStream(clientSocket.getInputStream());
		DataOutputStream DataOutputStream = new DataOutputStream(clientSocket.getOutputStream());
		Set<String> advertiseLibraryName = new HashSet<String>();
		URLAnalyzing URLStaticAnalyzing = null;
		URLAnalyzing URLDynamicAnalyzing = null;
		
		try{
			/*get client ip address*/
			client_IP = clientSocket.getInetAddress().toString();
			this.loggerInformation(client_IP +" connection sucessful......");
//			System.out.println(date + "Connection sucessful, Client Address = " + client_IP);
			
			/*get client name from fileNameManage*/
			fileName = this.fileNameManager.popFileName();
			
			/* 0
			 * Client Number is full.
			 */
			if(fileName == null) {
				this.sendMessage(DataOutputStream, date + client_IP, " Client Number is full.");
				this.close(DataInputStream, DataOutputStream, clientSocket);
				return;
			}
			
			/* 1
			 * connect message
			 */
			this.sendMessage(DataOutputStream, date + client_IP, fileName + " is connecting.");
			
			/* 2
			 * receive flag
			 */
			flag = DataInputStream.readByte();
			this.loggerInformation(client_IP + " receive flag is = " + flag);
//			System.out.println(date + client_IP + ", flag " + flag);
			
			/* 3
			 * receive filesize
			 */
			this.fileSize = DataInputStream.readInt();
			this.loggerInformation(client_IP + " receive file size is = " + this.fileSize);
//			System.out.println(date + client_IP + ", fileSize.");
			
			/* 4
			 * receive apk
			 */
			this.loggerInformation(client_IP + " receive file start......");
//			System.out.println(date + client_IP + ", receive file start...");
			this.receiveAPK(DataInputStream, date + client_IP, fileName, this.fileSize);
			this.loggerInformation(client_IP + " receive file successful......");
//			System.out.println(date + client_IP + ", receive file successed. " + this.fileSize);
			
			/*
			 * choose dynamic URL analyzing
			 */
			if((flag & 4) != ZERO) {
				final String fn = fileName;
				final String clientIP = client_IP;
				WinDumpCommand WinDumpCommand = new WinDumpCommand("-i 3 -s 65535 -A host 120.125.80.203");
				Thread winDumpThread = new Thread(WinDumpCommand);
				winDumpThread.start();
				//URLDynamicAnalyzing = new URLAnalyzing("dynamic", fileName, true);
				//URLDynamicAnalyzing.analyze();
				this.loggerInformation(", Dynamic analyze is begining ......");
				dynamicThread = new Thread(new Runnable(){
                    @Override
                    public void run() {
                    	Emulator nox = EmulatorManager.getEulator("62025");
                    	boolean isUsed = EmulatorManager.getEmulatorIsUsed("62025");
        				if(isUsed && nox != null) {
        					try {
        						String pkgName = nox.getPackageName(fn + ".apk");
            					System.out.println("package name: " + pkgName);

            					if(!"".equals(pkgName) && pkgName != null) {
            						nox.runMonkey(pkgName);
            						System.out.println("Monkey Start !!!");
	
	            					nox.unInstallAPK(pkgName);
	            					System.out.println("Uninstall !!!");
	            					loggerInformation(clientIP + ", Dynamic analyze is ending ......");
            					}
            					else {
            						loggerInformation(clientIP + ", Dynamic analyze is fail ......");
            					}
        					} catch(Exception e) {
        						e.printStackTrace();
        					} finally {
        						WinDumpCommand.terminated("");
            					System.out.println("WinDump END !!!");
        					}
        				}
                    }
                });
				dynamicThread.start();
			}
					
			/* 5 6
			 * decode APK
			 */
			this.sendMessage(DataOutputStream, date + client_IP, "decompile start...");//send start decompile message
			this.decodeAPKBySources(fileName);
			this.sendMessage(DataOutputStream, date + client_IP,"decompile successed..");//send end decompile message
			
			/* 7 8 9
			 * detect
			 */
			this.sendMessage(DataOutputStream, date + client_IP, "detect start...");//send start detect message
			if((flag & 1) != ZERO) { //choose permission check
				advertiseLibraryName = this.useAdvertisersList(fileName);//get all the real use adlibrary
				StringBuilder adLibraryList = new StringBuilder();
				for (String advertiserPackageName : advertiseLibraryName) {
					adLibraryList.append(this.getAdLibraryPermission(advertiserPackageName));
				}
				result = adLibraryList.toString();
				this.sendMessage(DataOutputStream, date + client_IP, "detect permission successed...");
				this.sendMessage(DataOutputStream, date + client_IP, result);
			}
			
			/*
			 * choose static URL analyzing
			 */
			if((flag & 2) != ZERO) {
				isURLStaticAnalyze = true;
				URLStaticAnalyzing= new URLAnalyzing("static", fileName, false);
				URLStaticAnalyzing.analyze();
			
				/* 10 11 12
				 * return result for url
				 */
				System.out.println("isURLStaticAnalyze: " + isURLStaticAnalyze);
				if(isURLStaticAnalyze) {
					Map<String, ArrayList<String>> urlResult = URLStaticAnalyzing.getScore();
					this.getResultString(DataOutputStream, client_IP, URLStaticAnalyzing.getUrlCount());
					this.getResultString(DataOutputStream, client_IP, urlResult);
				}
			}
		} catch(IOException e) {
			e.printStackTrace();
		} finally {
			/*close socket*/
			this.close(DataInputStream, DataOutputStream, clientSocket);
			System.out.println(date + client_IP + ", socket is closed.");
			
			/*delete file*/
			this.deleteAPKFile(fileName);
			this.deleteFolderAll(fileName);
			
			// for delete url data
			this.deleteFolderAll("Result/" + fileName);
			this.fileNameManager.pushFileName(fileName);
			System.out.println(date + client_IP + ", delete " + fileName + " successed.");
		}
		System.out.println(date + client_IP + ", end.");
	}
	
	/*
	 * close the connection
	 */
	private void close(DataInputStream dataInputStream, DataOutputStream dataOutputStream, Socket clientSocket) throws IOException {
		dataInputStream.close();
		dataOutputStream.flush();
		dataOutputStream.close();
		clientSocket.close();
	}
	
	/*
	 * send message need to string and output client_ip+message
	 */
	public void sendMessage(DataOutputStream dataOutputStream, String client_ip, String message) throws IOException {
		System.out.println(client_ip + ", " + message);
		dataOutputStream.writeUTF(message);
		dataOutputStream.flush();
	}
	
	/*
	 * send url score to client (old)
	 */
	public void sendMessage(DataOutputStream dataOutputStream, String client_ip, byte[] urlScore) throws IOException {
		System.out.println(client_ip + ", " + new String(urlScore, Charset.forName("UTF-8")));
		dataOutputStream.writeInt(urlScore.length);
		dataOutputStream.write(urlScore);
		dataOutputStream.flush();
	}
	
	/*
	 * get the url counts
	 */
	private void getResultString(DataOutputStream dataOutputStream, String client_ip, int urlCount) throws IOException {
		System.out.println(client_ip + ", " + " have " + urlCount + " URLs.");
		dataOutputStream.writeInt(urlCount);
		dataOutputStream.flush();
	}
	
	/*
	 * get the url analying result
	 */
	private boolean getResultString(DataOutputStream dataOutputStream, String client_ip, Map<String, ArrayList<String>> urlResult) {
		String [] webName = {"WOT", "VT"};
		ArrayList<String> webSite;
		
		int size;
		for(int i=0; i<webName.length; i++) {
			webSite = urlResult.get(webName[i]);
			size = webSite.size();
			
			try {
				dataOutputStream.writeInt(size);
				
				for(String urlScore : webSite) {
//					this.sendMessage(dataOutputStream, client_ip, urlScore.getBytes());
					this.sendMessage(dataOutputStream, client_ip, urlScore);
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return false;
			}
		}
		return true;
	}
	
	private String getAdLibraryPermission(String adLibrary) throws IOException {
		String adLibraryPath = "./" + adLibrary + "/result.txt";
		StringBuilder stringBuilder = new StringBuilder();
		FileReader fileReader = new FileReader(adLibraryPath);
		BufferedReader bufferedReader = new BufferedReader(fileReader);
		while (bufferedReader.ready()) {
			stringBuilder.append(bufferedReader.readLine());
			stringBuilder.append("\r\n");
		}
		fileReader.close();
		return stringBuilder.toString();
	}
	
	private Set<String> useAdvertisersList(String fileName) {
		Set<String> result = new HashSet<String>();
		AdList adList = new AdList();
		String[] keyword = adList.getAllKey();
		String path = "./" + fileName;
		File folder = new File(path);
		String[] list = folder.list();
		
		for (int folderItemsIndex=0;folderItemsIndex<list.length;folderItemsIndex++) {
			if(list[folderItemsIndex].contains("smali")) {
				for(int keywordItemsIndex=0;keywordItemsIndex<keyword.length;keywordItemsIndex++) {
					path = "./" + fileName + "/" + list[folderItemsIndex] + "/" + keyword[keywordItemsIndex];
					if(this.fileExists(path)) {
						result.add(adList.getAdvertisers(keyword[keywordItemsIndex]));
					}
				}
			}
		}
		return result;
	}
	
	private void receiveAPK(DataInputStream dataInputStream, String client_ip, String fileName, int fileSize) throws IOException {
		int buffer_max = 8192;
		byte[] buffer = new byte[buffer_max];
		fileName = fileName + ".apk";
		FileOutputStream fileOutputStream = new FileOutputStream(fileName);
		
		while(fileSize > 0) {
			int readBufferLength = dataInputStream.read(buffer, 0, buffer.length);
			fileOutputStream.write(buffer, 0, readBufferLength);
			fileSize -= readBufferLength;
		}
		//close fileoutput
		fileOutputStream.close();
	}
	
	private void decodeAPKBySources(String fileName) {
		fileName += ".apk";
		String outputDir = fileName.substring(0, fileName.lastIndexOf(".apk"));
		
		try {
			APKDecoder apkDecoder = new APKDecoder(fileName, outputDir);
			apkDecoder.decoderSetting(true, true);
			apkDecoder.decodeAPK();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private boolean fileExists(String path) {
		File file = new File(path);
		if(file.exists()) {
			return true;
		}
		return false;
	}
	
	private void deleteAPKFile(String filePathAndName) {
		try {
			File target = new File(filePathAndName + ".apk");
			target.delete();
		}
		catch(Exception e) {
			System.out.println("delete file failed.");
			e.printStackTrace();
		}
	}
	
	private void deleteFolderAll(String fileName) {
		this.ForceDeleteDir(new File("./" + fileName));
	}
	
	private boolean ForceDeleteDir(File dir_target) {
		if (dir_target.isDirectory() && dir_target.exists()) {
			String[] fileList = dir_target.list();
			for (int i = 0; i < fileList.length; i++) {
				String sFile = dir_target.getPath() + File.separator + fileList[i];
				File tmp = new File(sFile);
				if (tmp.isFile()) {
					tmp.delete();
				}
				if (tmp.isDirectory()) {
					ForceDeleteDir(new File(sFile));
				}
			}
			dir_target.delete();
		} else {
			return false;
		}
		return true;
	}
}