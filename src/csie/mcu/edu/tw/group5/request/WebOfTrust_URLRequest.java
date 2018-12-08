package csie.mcu.edu.tw.group5.request;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class WebOfTrust_URLRequest extends URLRequest{

	private static final String APIRequestUrl = "http://api.mywot.com/0.4/public_link_json2?hosts=";
    private static final String APIKey = "&key=635a34a33f3b794681ebb03b2cf28ba1aed525e5";
    private Set <String> urlHostName;
    
    public WebOfTrust_URLRequest(ArrayList<String> urlData, String apkFilePath, String status) {
        this.status = status;
        this.error = 0;
        this.limitCount = 0;
        this.apkFilePath = apkFilePath;
        this.urlDataForSearch = urlData;
        this.urlHostName = new HashSet<> ();
        this.requestResult = new String();
    }
    
    public void getWOTResult() {
        double startTime = 0, endTime = 0, totalTime = 0;
        try {
            for (String url : urlDataForSearch) {
                startTime = System.currentTimeMillis();
                if (totalTime > 60) {
                    if(this.limitCount == 10) {
                        System.out.println("Please wait for 1 minute since the limit in WebOfTrust's public key request...");
                        try {
                            Thread.sleep(1000);
                        } catch(Exception e) {e.printStackTrace();}
                        this.limitCount = 0;
                    }
                    totalTime = 0;
                }
                
                URL obj = new URL(APIRequestUrl + url + "/" + APIKey);
                HttpURLConnection connection = (HttpURLConnection) obj.openConnection();
                
                // optional default is GET
                connection.setRequestMethod("GET");
                
                //add request header
                connection.setRequestProperty("User-Agent", USER_AGENT);
                this.responseCode = connection.getResponseCode();
                
                if (this.responseCode == 200) {
                    try {
                        BufferedReader in = new BufferedReader(
                        new InputStreamReader(connection.getInputStream()));
        
                        String inputLine;
                        while ((inputLine = in.readLine()) != null)
                        	this.requestResult = inputLine.trim();
                            
                        in.close();
                    } catch(IOException read) {read.printStackTrace();}
                }
                else {
                	this.error += 1;
                    saveErrorRequests(url, this.requestResult.toString(), this.responseCode);
                }
                
                endTime = System.currentTimeMillis();
                totalTime += ((endTime-startTime)/1000);
                
                if (!isDuplicate(url)) {
                	this.addRequest(this.requestResult.toString());
                    if (isWriteToFile) {
                        saveAllRequest(this.requestResult.toString());
                    }
                }
                this.limitCount++;
            }
        } catch(Exception e) {e.printStackTrace();}
    }
     
	@Override
	protected void saveAllRequest(String result) {
		// TODO Auto-generated method stub
		
		String wotRequests = "Result/" + this.apkFilePath + "/" + this.checkStatus();
        wotRequests += "/WOT_Requests.txt";
        try {
            FileWriter fileWriter = new FileWriter(wotRequests, true);
            BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
            bufferedWriter.write(result + "\n");
            bufferedWriter.flush();
            bufferedWriter.close();
            fileWriter.close();
        } catch(IOException w) {w.printStackTrace();}
	}

	@Override
	protected void saveErrorRequests(String URL, String errorResult, int responseCode) {
		// TODO Auto-generated method stub
		
		String wotErrors = "Result/" + this.apkFilePath + "/" + this.checkStatus();
        wotErrors += "/WOT_Error_Requests.txt";
        try {
            FileWriter fileWriter = new FileWriter(wotErrors, true);
            BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
            
            String errorCode = error + ". responseCode : " + responseCode + "\n";
            bufferedWriter.write(errorCode);
            
            String errorUrl = "   URL : " + URL + "\n";
            bufferedWriter.write(errorUrl);
            
            String errorURL_result = "   Result : " + errorResult + "\n";
            bufferedWriter.write(errorURL_result + "\n");
            
            bufferedWriter.flush();
            bufferedWriter.close();
            fileWriter.close();
        } catch(IOException w) {w.printStackTrace();}
	}
	
	private boolean isDuplicate(String url) {
        try {
            URL duplicateURL = new URL(url);
            String dupUrlHost = duplicateURL.getHost();
            if (!this.urlHostName.add(dupUrlHost))  
                return true;
        } catch(Exception e) {e.printStackTrace();}
        return false;
    }
}