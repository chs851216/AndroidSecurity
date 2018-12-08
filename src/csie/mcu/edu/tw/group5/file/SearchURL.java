package csie.mcu.edu.tw.group5.file;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SearchURL {

	private String apkDirctoryName;
    private String status;
    private int urlCount;
    private ArrayList <String> urlData;
    private Set <String> duplicateUrls;
    // ^(https?|ftp|file)://[-a-zA-Z0-9+&@#/%?=~_|!:,.;]*[-a-zA-Z0-9+&@#/%=~_|]
    private String regex = "https?://[-a-zA-Z0-9+&@#/%?=~_|!:.;]*[-a-zA-Z0-9+&@#/?=~_|]";
    
    public SearchURL(String apkDirctoryName, String status) {
        this.apkDirctoryName = apkDirctoryName;
        this.status = status;
        this.urlCount = 0;
        this.urlData = new ArrayList<> ();
        this.duplicateUrls = new HashSet<String> ();
    }
    
    public SearchURL(String apkDirctoryName, String status, String regex) {
        this.apkDirctoryName = apkDirctoryName;
        this.status = status;
        this.regex = regex;
        this.urlCount = 0;
        this.urlData = new ArrayList<> ();
        this.duplicateUrls = new HashSet<String> ();
    }
    
    private String checkStatus() {
        if (this.status.equals("static") || this.status.equals("Static"))
        	this.status = "Static";
        if (this.status.equals("dynamic") || this.status.equals("Dynamic"))
        	this.status = "Dynamic";
            
        return this.status;
    }
    
    public void searchAndSaveURL() {
        String smaliFilePathes = "Result/" + this.apkDirctoryName + "/" + this.checkStatus();
        smaliFilePathes += "/smaliFilePathes.txt";
        try {
            FileReader fileReader = new FileReader (smaliFilePathes);
            BufferedReader bufferedReader = new BufferedReader (fileReader);
            
            String tmpData = null;
            while ((tmpData = bufferedReader.readLine()) != null) {
                try {
                    FileReader pathReader = new FileReader (tmpData);
                    BufferedReader pathBuffer = new BufferedReader (pathReader);
                    
                    String lines = null;
                    while ((lines = pathBuffer.readLine()) != null) {                  
                        String [] stringArray = lines.split(" ");
                        for (String line : stringArray) {
                            line = line.replaceAll("\"", "");
                            if (line.matches(this.regex) && !line.contains("%s"))
                            	this.saveURLFiles(line);
                        }
                    }
                    pathBuffer.close();
                } catch (IOException e) {e.printStackTrace();}
            }
            bufferedReader.close();
        } catch (IOException read) {read.printStackTrace();}
        System.out.println("And in " +  this.apkDirctoryName + ", find " + this.urlCount + " URL in all smali file...");
    }
    
    public void searchAndSaveURL(String packetFileName) {
        String packetPathe = "Result/" + this.apkDirctoryName + "/" + this.checkStatus();
        Pattern pattern = Pattern.compile(this.regex);
		Matcher matcher;
        packetPathe += (packetFileName + ".txt");
        try {
            FileReader fileReader = new FileReader (packetPathe);
            BufferedReader bufferedReader = new BufferedReader (fileReader);
            
            String tmpData = null;
            while ((tmpData = bufferedReader.readLine()) != null) {         
	            String [] stringArray = tmpData.split(" ");
	            for (String line : stringArray) {
	                //line = line.replaceAll("\"", "");
	            	matcher = pattern.matcher(line);
                    if (matcher.matches()) {        
                    	line = matcher.group();
                        
                        if(line.indexOf(".") != -1 && line.lastIndexOf(".")-line.indexOf(".") > 3) {
                        	line = line.substring(0, line.lastIndexOf("."));
                        	this.saveURLFiles(line);
                        }
                    }
	            }
            }
            bufferedReader.close();
        } catch (IOException read) {read.printStackTrace();}
        System.out.println("And in " +  this.apkDirctoryName + ", find " + this.urlCount + " URL in dynamic test...");
    }
    
    private void saveURLFiles(String URL) {
        /*
         *  judge if the url is duplicate or not
         *  if the url is already exist in urlSet, it will return 0
         */
        boolean check = this.duplicateUrls.add(URL);
        if (check)
        try {
            String pathName = "Result/" + this.apkDirctoryName + "/" + this.checkStatus();
            pathName += "/URLs.txt";

            FileWriter fileWriter = new FileWriter(pathName, true);
            BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
            
            bufferedWriter.write(URL + "\n");
            bufferedWriter.flush();
            bufferedWriter.close();
            this.urlCount += 1;
        } catch (Exception e) {e.printStackTrace();}
    }
    
    public int getUrlCount() {
    	return this.urlCount;
    }
    
    public ArrayList<String> getUrlFiles() {
        String urls = "Result/" + this.apkDirctoryName + "/" + this.checkStatus();
        urls += "/URLs.txt";
        try {
            FileReader fileReader = new FileReader(urls);
            BufferedReader bufferedReader = new BufferedReader(fileReader);
            
            String data;
            while((data = bufferedReader.readLine()) != null)
            	this.urlData.add(data);
            
            fileReader.close();
            bufferedReader.close();
        } catch(IOException read) {read.printStackTrace();}
        
        return this.urlData;
    }
}