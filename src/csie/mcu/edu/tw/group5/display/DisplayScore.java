package csie.mcu.edu.tw.group5.display;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import csie.mcu.edu.tw.group5.display.Display;
import csie.mcu.edu.tw.group5.file.FileOption;
import csie.mcu.edu.tw.group5.score.Virus_Total;
import csie.mcu.edu.tw.group5.score.Web_of_Trust;

public class DisplayScore implements FileOption{

	private String applicationName;
    private String status;
    private Map<String, ArrayList<String>> resultMap = new HashMap<>();
    private Web_of_Trust wotScore;
    private Virus_Total vtScore;
    private Display WOT, VT;
    
    public DisplayScore(String fileName, String status)
    {
        this.applicationName = fileName;
        this.status = status;
        this.wotScore = new Web_of_Trust(fileName, this.checkStatus());
        this.WOT = new WOTScore(fileName, status);
        
        this.vtScore = new Virus_Total(fileName, this.checkStatus());
        this.VT = new VTScore(fileName, status);
    }
    
    public String checkStatus() {
        if (this.status.equals("static") || this.status.equals("Static"))
        	this.status = "Static";
        if (this.status.equals("dynamic") || this.status.equals("Dynamic"))
        	this.status = "Dynamic";
            
        return this.status;
    }

    public void get_WOT_Score(ArrayList<String> reqResult)
    {
        if(isWriteToFile)
        	this.wotScore.read_WOT_Requests();
        else
        	this.wotScore.read_WOT_Requests(reqResult);
        this.wotScore.save_WOT_Score();
    }
    
    public void get_VirusTotal_Score(ArrayList<String> reqResult)
    {
        if(isWriteToFile)
        	this.vtScore.read_VirusTotal_Requests();
        else
        	this.vtScore.read_VirusTotal_Requests(reqResult);
        this.vtScore.save_VirusTotal_Score();
    }
    
    public Map<String, ArrayList<String>> getResultMap() {
    	this.resultMap.put("WOT", this.wotScore.getReportScore());
    	this.resultMap.put("VT", this.vtScore.getReportScore());
    	return this.resultMap;
    }
    
    public void show_Application_Score() {
        System.out.println(this.applicationName + "¡G");
        
        this.WOT.get_Score_Data();
        System.out.println(this.WOT.display());
        
        this.VT.get_Score_Data();
        System.out.println(this.VT.display());
        
        System.out.println();
    }
    
    public void addRequest(String reqResult) {}
    public ArrayList<String> getRequest() {return null;}
    
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}
}