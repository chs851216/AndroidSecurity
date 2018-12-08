package csie.mcu.edu.tw.group5.file;

import java.util.Stack;

public class FileNameManager {

	private static String firstName = "TestAPK";
	private static int max_fileNumber = 20;
	private Stack<String> filename;

	public FileNameManager() {
		this.filename = new Stack<String>();
		for(int lastName=max_fileNumber-1; lastName>=0; lastName--)
		{
			this.filename.push(firstName + lastName);
		}
	}
	
	/*
	if successed get filename, than return filename string
	else failed, than return null
	*/
	public synchronized String popFileName() {
		if(this.filename.empty()) {
			return null;
		}
		return this.filename.pop();
	}
	
	public synchronized void pushFileName(String filename) {
		this.filename.push(filename);
	}
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub

		FileNameManager fileNameManager = new FileNameManager();
		String[] filename = new String[20];
		for(int i=0;i<20;i++) {
			filename[i] = fileNameManager.popFileName();
			System.out.println(filename[i]);
		}
	}
}