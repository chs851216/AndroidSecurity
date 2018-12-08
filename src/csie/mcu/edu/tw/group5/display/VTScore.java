package csie.mcu.edu.tw.group5.display;

import java.io.BufferedReader;
import java.io.FileReader;

public class VTScore extends Display{

	private int cleanTotalCount;
    private int unratedTotalCount;
    private int suspiciousTotalCount;
    private int maliciousTotalCount;
    private int phishingTotalCount;
    private int malwareTotalCount;
    
    public VTScore(String fileName, String status) {
    	this.applicationName = fileName;
        this.status = status;
        this.reset_varible_state();
    }
    
    protected void reset_varible_state() {
    	this.urlCount = 0;
    	this.cleanTotalCount = 0;
    	this.unratedTotalCount = 0;
    	this.suspiciousTotalCount = 0;
    	this.maliciousTotalCount = 0;
    	this.phishingTotalCount = 0;
    	this.malwareTotalCount = 0;
    	this.totalScore = 0.0;
    }
    
    protected void get_Score_Data() {
        String vtPath = "Result/" + this.applicationName + "/" + checkStatus();
        vtPath += "/VirusTotal_Scores.txt";
        try {
            FileReader fileReader = new FileReader(vtPath);
            BufferedReader bufferedReader = new BufferedReader(fileReader);
            
            String tmpData = null;
            String [] score = null;
            while ((tmpData = bufferedReader.readLine()) != null) {
                score = tmpData.split(" ");
                this.totalScore += Double.parseDouble(score[1]);
                this.cleanTotalCount = this.cleanTotalCount + Integer.valueOf(score[2]);
                this.unratedTotalCount = this.unratedTotalCount +  Integer.valueOf(score[3]);
                this.suspiciousTotalCount =  this.suspiciousTotalCount + Integer.valueOf(score[4]);
                this.maliciousTotalCount = this.maliciousTotalCount + Integer.valueOf(score[5]);
                this.phishingTotalCount = this.phishingTotalCount + Integer.valueOf(score[6]);
                this.malwareTotalCount = this.malwareTotalCount + Integer.valueOf(score[7]);
                this.urlCount += 1;
            }
            bufferedReader.close();
        } catch(Exception e) {e.printStackTrace();}
    }
    
    protected String display() {
        String msg = "VirusTotal's score is: ";
        msg += (this.totalScore / this.urlCount) * 100;
        return msg + " (Clean / Total).";
    }
}