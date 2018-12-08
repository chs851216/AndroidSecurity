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

public class Web_of_Trust {

	private String applicationName;
    private String status;
    private ArrayList <String> resourcesUrl;
    private ArrayList <Integer> confidenceScore;
    private ArrayList <Integer> childSafetyScore;
    private ArrayList<String> saveScore;
    private JSONObject reqResult, reqTarget;
    private Map <String, Integer> categories;
    
    private Iterator <String> checkKey;
    
    public Web_of_Trust (String name, String status) {
    	this.applicationName = name;
        this.status = status;
        this.resourcesUrl = new ArrayList <> ();
        this.confidenceScore = new ArrayList <> ();
        this.childSafetyScore = new ArrayList <> ();
        this.saveScore = new ArrayList <> ();
        
        this.categories = new HashMap<String, Integer>();
    }
    
    public String checkStatus() {
        if (this.status.equals("static") || this.status.equals("Static"))
        	this.status = "Static";
        if (this.status.equals("dynamic") || this.status.equals("Dynamic"))
        	this.status = "Dynamic";
            
        return this.status;
    }
    
    private void add_Confirdence_Score(int score) {
    	this.confidenceScore.add(score);
    }
    
    private void add_ChildSafety_Score(int score) {
    	this.childSafetyScore.add(score);
    }
    
    private void add_Resource(String resource) {
    	this.resourcesUrl.add(resource);
    }
    
    public void read_WOT_Requests() {
        String wotPath = "Result/" + applicationName + "/" + checkStatus();
        wotPath += "/WOT_Requests.txt";
        try {
            FileReader fileReader = new FileReader(wotPath);
            BufferedReader bufferedReader = new BufferedReader(fileReader);
            
            String tmpData = null;
            while ((tmpData = bufferedReader.readLine()) != null) {
                addScore(tmpData.trim());
                //System.out.println(tmpData);
            }
            bufferedReader.close();
        } catch(Exception e) {e.printStackTrace();}
    }
    
    public void read_WOT_Requests(ArrayList<String> reqResult) {
        if(reqResult != null) {
            for(String req : reqResult) {
                addScore(req.trim());
            }
        }
    }
    
    private void addScore(String wotRequest) {
        // confidenceScore and childSafetyScore
        int cfScore = 0, csScore = 0;
        String targetUrl = null;

        try {
            reqResult = new JSONObject(wotRequest);
            checkKey = reqResult.keys();
            
            if (checkKey.hasNext())
            try {
                while (checkKey.hasNext())
                {
                    targetUrl = checkKey.next();
                    reqTarget = reqResult.getJSONObject(targetUrl);
                    boolean cf = reqTarget.has("0");
                    boolean cs = reqTarget.has("4");
                    if (cf && cs)
                    {
                        cfScore = reqTarget.getJSONArray("0").getInt(0);
                        csScore = reqTarget.getJSONArray("4").getInt(0);
                        
                        add_Resource(targetUrl);
                        add_Confirdence_Score(cfScore);
                        add_ChildSafety_Score(csScore);
                        this.saveScore.add(targetUrl + " " + cfScore + " " + csScore);
                    }
                }   
            } catch(Exception e) {e.printStackTrace();}
        } catch(JSONException json) {
            json.printStackTrace();
            System.out.println("There is NO Results!!!");
        }
    }
    
    public ArrayList<String> getReportScore() {
    	return this.saveScore;
    }
    
    public void save_WOT_Score() {
        String scorePath = "Result/" + this.applicationName + "/" + checkStatus();
        scorePath += "/WOT_Scores.txt";
        try {
            File file = new File("Result/" + this.applicationName);
            if(file.exists())
            try {
                FileWriter fileWriter = new FileWriter(scorePath, true);
                BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
                
                for (int i=0; i<this.confidenceScore.size(); i++) {
                    int cf = this.confidenceScore.get(i);
                    int cs = this.childSafetyScore.get(i);
                    String url = this.resourcesUrl.get(i);
                    bufferedWriter.write(url + " " + cf + " " + cs + "\n");
                    bufferedWriter.flush();
                }
                fileWriter.close();
                bufferedWriter.close();
            } catch(IOException io) {io.printStackTrace();}
        } catch(Exception e) {e.printStackTrace();}
    }
}