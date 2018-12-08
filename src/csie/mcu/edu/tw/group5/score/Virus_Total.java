package csie.mcu.edu.tw.group5.score;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

public class Virus_Total {

	private String applicationName;
    private String status;
    private int cleanCount;
    private int unratedCount;
    private int suspiciousCount;
    private int maliciousCount;
    private int phishingCount;
    private int malwareCount;
    private double ratedScore;
    private JSONObject reqResult;
    private ArrayList<String> saveScore;
    
    // this map structure will store the scan-site's result
    // map --> (url, [clean site, unrated site, suspicious site etc...])
    private Map<String, Integer[]> scan_Sites;
    
    public Virus_Total(String fileName, String status)
    {
    	this.applicationName = fileName;
        this.status = status;
        this.reset_varible_state();
        this.saveScore = new ArrayList <> ();
        this.scan_Sites = new HashMap <> ();
    }
    
    private String checkStatus()
    {
        if (this.status.equals("static") || this.status.equals("Static"))
        	this.status = "Static";
        if (status.equals("dynamic") || this.status.equals("Dynamic"))
        	this.status = "Dynamic";
            
        return this.status;
    }
    
    public void read_VirusTotal_Requests() {
        String vtPath = "Result/" + this.applicationName + "/" + this.checkStatus();
        vtPath += "/VirusTotal_Requests.txt";
        try {
            FileReader fileReader = new FileReader(vtPath);
            BufferedReader bufferedReader = new BufferedReader(fileReader);
            
            String tmpData = null;
            while ((tmpData = bufferedReader.readLine()) != null) {
            	this.addScore(tmpData.trim());
                //System.out.println(tmpData);
            }
            fileReader.close();
            bufferedReader.close();
        } catch(Exception e) {e.printStackTrace();}
    }
    
    public void read_VirusTotal_Requests(ArrayList<String> reqResult) {
        if(reqResult != null)
            for(String req : reqResult)
            	this.addScore(req.trim());
    }
    
    private void addScore(String vtRequest) {
        try {
        	this.reqResult = new JSONObject(vtRequest);
            if(this.reqResult != JSONObject.NULL) {           
                if(this.reqResult.has("scans")) {
                	JSONObject scans_result = this.reqResult.getJSONObject("scans");
	                String tmp = null;
	                Iterator<String> scan_sites = scans_result.keys();
	                while(scan_sites.hasNext()) {
	                    tmp = scan_sites.next().toString();
	                    
	                    String result = null;
	                    try {
	                        JSONObject scans = scans_result.getJSONObject(tmp);
	                        result = scans.get("result").toString();
	                        this.compute_total_score(result);
	                    } catch(JSONException key) {key.printStackTrace();}
	                }
	                int total = this.reqResult.getInt("total");
	                String resource = this.reqResult.getString("resource");
	                this.save_all_Reports_Score(resource, total);
                }
            } else System.out.println(vtRequest + " has no result !!");
        } catch(JSONException e) {e.printStackTrace();}
    }
    
    private void compute_total_score(String result) {
        if (result.equals("clean site")) this.cleanCount += 1;
        else if (result.equals("unrated site")) this.unratedCount += 1;
        else if (result.equals("suspicious site")) this.suspiciousCount += 1;
        else if (result.equals("malicious site")) this.maliciousCount += 1;
        else if (result.equals("phishing site")) this.phishingCount += 1;
        else if (result.equals("malware site")) this.malwareCount += 1;
        else System.out.println(result + ", is Error!!!");
    }
    
    private void save_all_Reports_Score(String resource, int total) {
    	this.ratedScore = (double)this.cleanCount / (double)(total - this.unratedCount);
        
        String saveData = "";
        saveData += resource + " ";
        saveData += this.ratedScore + " ";
        saveData += this.cleanCount + " ";
        saveData += this.unratedCount + " ";
        saveData += this.suspiciousCount + " ";
        saveData += this.maliciousCount + " ";
        saveData += this.phishingCount + " ";
        saveData += this.malwareCount;    
        this.saveScore.add(saveData);
        
        this.reset_varible_state();
    }
    
    private void reset_varible_state() {
    	this.cleanCount = 0;
    	this.unratedCount = 0;
    	this.suspiciousCount = 0;
    	this.maliciousCount = 0;
    	this.phishingCount = 0;
    	this.malwareCount = 0;
    	this.ratedScore = 0.0;
    }
    
    public ArrayList<String> getReportScore() {
    	return this.saveScore;
    }
    
    public void save_VirusTotal_Score() {
        String scorePath = "Result/" + this.applicationName + "/" + this.checkStatus();
        scorePath += "/VirusTotal_Scores.txt";
        try {
            File file = new File("Result/" + this.applicationName);
            if(file.exists())
            try {
                FileWriter fileWriter = new FileWriter(scorePath);
                BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
                
                for (String result : saveScore) {
                    bufferedWriter.write(result + "\n");
                    bufferedWriter.flush();
                }
                fileWriter.close();
                bufferedWriter.close();
            } catch(IOException io) {io.printStackTrace();}
        } catch(Exception e) {e.printStackTrace();}
    }
}