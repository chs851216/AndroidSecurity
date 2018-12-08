package csie.mcu.edu.tw.group5.display;

import java.io.BufferedReader;
import java.io.FileReader;

public class WOTScore extends Display{

	private double confidenceTotalScore;
    private double childSafetyTotalScore;
    
    public WOTScore(String fileName, String status) {
        this.applicationName = fileName;
        this.status = status;
        this.reset_varible_state();
    }
    
    protected void reset_varible_state() {
    	this.urlCount = 0;
    	this.totalScore = 0.0;
    	this.confidenceTotalScore = 0;
    	this.childSafetyTotalScore = 0;
    }
    
    protected void get_Score_Data() {
        String wotPath = "Result/" + this.applicationName + "/" + checkStatus();
        wotPath += "/WOT_Scores.txt";
        try {
            FileReader fileReader = new FileReader(wotPath);
            BufferedReader bufferedReader = new BufferedReader(fileReader);
            
            String tmpData = null;
            String [] score = null;
            while ((tmpData = bufferedReader.readLine()) != null) {
                score = tmpData.split(" ");
                this.confidenceTotalScore += Integer.valueOf(score[1]);
                this.childSafetyTotalScore += Integer.valueOf(score[2]);
                this.urlCount += 1;
            }
            bufferedReader.close();
        } catch(Exception e) {e.printStackTrace();}
    }
    
    protected String display()  {
        String msg = "Web of Trust's score is: ";
        msg += this.confidenceTotalScore / this.urlCount;
        msg += ", ";
        msg += this.childSafetyTotalScore / this.urlCount;
        return msg + " (Confidence, ChildSafety).";
    }
}