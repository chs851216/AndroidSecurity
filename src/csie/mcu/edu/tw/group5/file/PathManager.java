package csie.mcu.edu.tw.group5.file;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;

public class PathManager {
	
	private String apkFilePath;
    private String status;
    private ArrayList <String> smaliFilePath;
    public ArrayList <String> allPath;
    
    public PathManager(String apkFilePath, String status) {
        this.apkFilePath = apkFilePath;
        this.status = status;
        this.smaliFilePath = new ArrayList<> ();
        this.allPath = new ArrayList<> ();
    }
    
    private String checkStatus() {
        if (this.status.equals("static") || this.status.equals("Static"))
        	this.status = "Static";
        if (this.status.equals("dynamic") || this.status.equals("Dynamic"))
        	this.status = "Dynamic";
            
        return this.status;
    }
    
    public void saveFile() {
    	this.smaliFilePath = this.readAllFiles(this.apkFilePath);
        String meg = "In " + this.apkFilePath + ", find " + this.smaliFilePath.size();
        System.out.println(meg + " smali files...");
        try {
            String pathName = "Result/" + this.apkFilePath + "/" + this.checkStatus();
            File file = new File(pathName);
            if (!file.exists()) file.mkdirs();
            
            pathName += "/smaliFilePathes.txt";
            FileWriter fileWriter = new FileWriter(pathName, true);
            BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
            
            for (String s : allPath) {
            	bufferedWriter.write(s + "\n");
            	bufferedWriter.flush();
            }
            fileWriter.close();
            bufferedWriter.close();
        } catch (Exception e) {e.printStackTrace();}
    }
    
    private ArrayList<String> readAllFiles(String apkFilePath) {
        ArrayList <String> allFiles = new ArrayList <> ();
        File file = new File(apkFilePath);
        
        if(file.isDirectory()) {
            for(String fn : file.list()) {
                allFiles.addAll(readAllFiles(apkFilePath + "/" + fn));
            }
        }
        else {
            if(apkFilePath.toString().contains(".smali")) {
                allFiles.add(apkFilePath.toString());
                
                // This line is add the smali file's path to array...
                allPath.add(apkFilePath.toString());
                return allFiles;
            }
            else {
                return allFiles;
            }
        }
        return allFiles;
    }
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub

		boolean checkFile = false;
        if (args.length > 0 && args.length <= 1) {
            String apkPath = args[0];
            try {
                File file = new File(apkPath);
                if (file.exists()) checkFile = true;
            } catch (Exception e) {e.printStackTrace();}
            
            if (checkFile) {
                PathManager path = new PathManager(apkPath, "Static");
                path.saveFile();
            }
            else System.out.println("Error, File Not Exist !!!");
        }
        else
            System.out.println("Input Error !!!");
	}
}