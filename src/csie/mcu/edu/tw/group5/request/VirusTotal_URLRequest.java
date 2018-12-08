package csie.mcu.edu.tw.group5.request;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import org.json.JSONException;
import org.json.JSONObject;

public class VirusTotal_URLRequest extends URLRequest{

	private static final String APIRequestUrl = "https://www.virustotal.com/vtapi/v2/url/report";
    private static final String APIKey = "2814e38e8e605d0bf32fdea258626ee2db43e2632cab27071cf7fb860f56585d";
    private static final String urlParameters = "apikey=" + APIKey + "&resource=";
    
    private Set<String> duplicateURL;
    
    public VirusTotal_URLRequest(ArrayList<String> urlDataForSearch, String apkFilePath, String status) {
        this.status = status;
        this.error = 0;
        this.limitCount = 0;
        this.apkFilePath = apkFilePath;
        this.urlDataForSearch = urlDataForSearch;
        this.requestResult = new String();
        this.duplicateURL = new HashSet<>();
    }
    
    public void getVirusTotalResult() {
        double startTime = 0, endTime = 0, totalTime = 0;
        try {
            for (String url : urlDataForSearch) {
                startTime = System.currentTimeMillis();
                if (totalTime > 60) {
                    if(limitCount == 200) {
                        System.out.println("Please wait for 1 minute since the limit in VirusTotal's public key request...");
                        try {
                            Thread.sleep(60000);
                        } catch(Exception e) {e.printStackTrace();}
                        this.limitCount = 0;
                    }
                    totalTime = 0;
                }
                URL obj = new URL(APIRequestUrl);
                HttpURLConnection httpURLConnection = (HttpURLConnection) obj.openConnection();
                
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setRequestProperty("User-Agent", USER_AGENT);
                
                // set the status that it can be writen parameters
                httpURLConnection.setDoOutput(true);
                
                String parameters = urlParameters + url + "&scan=1";
                
                DataOutputStream dataOutputStream = new DataOutputStream(httpURLConnection.getOutputStream());
                dataOutputStream.write(parameters.getBytes());
                dataOutputStream.flush();
                dataOutputStream.close();
                
                this.responseCode = httpURLConnection.getResponseCode();

                try {
                    BufferedReader in = new BufferedReader(
                    new InputStreamReader(httpURLConnection.getInputStream()));
    
                    String inputLine;
                    while ((inputLine = in.readLine()) != null) {
                    	this.requestResult = inputLine.trim();
                    }
                        
                    in.close();
                    httpURLConnection.disconnect();
                } catch(IOException read) {read.printStackTrace();}
                
                endTime = System.currentTimeMillis();
                totalTime += ((endTime-startTime)/1000);
                try {
                    JSONObject scanReport = new JSONObject(this.requestResult);
                    boolean scans = scanReport.has("scans");
                    
                    if (!scans && scanReport.has("scans")) {
                    	this.requestResult = re_Send_Request(url);
                    	this.limitCount++;
                    }
                } catch(JSONException e) {e.printStackTrace();}

                if(isDuplicate(this.requestResult.toString())) {
	                this.addRequest(this.requestResult.toString());
	                if (isWriteToFile) {
	                    saveAllRequest(this.requestResult.toString());
	                }
                }
                this.limitCount++;
            }
        } catch(Exception e) {e.printStackTrace();}
    }
    
    private String re_Send_Request(String re_Send_Url) throws IOException, JSONException {
        URL obj = new URL(APIRequestUrl);
        HttpURLConnection httpURLConnection = (HttpURLConnection) obj.openConnection();
        
        httpURLConnection.setRequestMethod("POST");
        httpURLConnection.setRequestProperty("User-Agent", USER_AGENT);
        
        // set the status that it can be writen parameters
        httpURLConnection.setDoOutput(true);
        
        String parameters = urlParameters + re_Send_Url;// + "&scan=1";
        
        DataOutputStream dataOutputStream = new DataOutputStream(httpURLConnection.getOutputStream());
        dataOutputStream.write(parameters.getBytes());
        dataOutputStream.flush();
        dataOutputStream.close();
        
        responseCode = httpURLConnection.getResponseCode();
        
        // get the data 
        BufferedReader in = new BufferedReader(
        new InputStreamReader(httpURLConnection.getInputStream()));
        
        String result = null;
        String inputLine = null;
        while ((inputLine = in.readLine()) != null)
            result = inputLine;
            
        in.close();
        httpURLConnection.disconnect();
        System.out.println(result);
        return result;
    }
    
	@Override
	protected void saveAllRequest(String result) {
		// TODO Auto-generated method stub
		
		String vtRequests = "Result/" + this.apkFilePath + "/" + checkStatus();
        vtRequests += "/VirusTotal_Requests.txt";
        try {
            FileWriter fileWriter = new FileWriter(vtRequests, true);
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
		
		String vtErrors = "Result/" + this.apkFilePath + "/" + checkStatus();
        vtErrors += "/VirusTotal_Error_Requests.txt";
        try {
            FileWriter fileWriter = new FileWriter(vtErrors, true);
            BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
            
            String errorCode = this.error + ". responseCode : " + responseCode + "\n";
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
	
	private boolean isDuplicate(String result) {
        if (this.duplicateURL.add(result)) {
            return true;
        }
        return false;
    }
}