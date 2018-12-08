package csie.mcu.edu.tw.group5.main;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import csie.mcu.edu.tw.group5.display.DisplayScore;
import csie.mcu.edu.tw.group5.file.FileHandler;
import csie.mcu.edu.tw.group5.request.URLRequest;
import csie.mcu.edu.tw.group5.request.VirusTotal_URLRequest;
import csie.mcu.edu.tw.group5.request.WebOfTrust_URLRequest;

public class URLAnalyzing {

	private final String regex = "([(https?|ftp|file)://]*[-a-zA-Z0-9+&@#/%?=~_|!,.;]*[-a-zA-Z0-9+&@#/%=~_|])";
	private String status;
	private String outputDireatory;
	private boolean hasRegex;
	private ArrayList<String> url;
	private Map<String, ArrayList<String>> resultMap;
	
	private FileHandler fileHandler;
	private URLRequest urlRequest;
	private DisplayScore displayScore;
	
	public URLAnalyzing(String status, String outputDireatory, boolean hasRegex) {
		this.status = status;
		this.outputDireatory = outputDireatory;
		this.hasRegex = hasRegex;
		this.url = new ArrayList<>();
		this.resultMap = new HashMap<>();
		
		this.fileHandler = new FileHandler(this.outputDireatory, this.status, "");
		if(this.hasRegex) {
			this.fileHandler = new FileHandler(this.outputDireatory, this.status, this.regex);
		}
		
		this.urlRequest = null;
		this.displayScore = null;
	}
	
	/*
	 * for static
	 */
	public void analyze() {
		this.url = this.getURL();
		this.display(this.url);
	}
	
	/*
	 * for dynamic
	 */
	public void analyze(String packetFileName) {
		this.url = this.getURL(packetFileName);
		this.display(this.url);
	}
	
	public int getUrlCount() {
		return this.fileHandler.getUrlCount();
	}
	
	private ArrayList<String> getURL() {
		if(!this.hasRegex) {
			this.fileHandler.save_File_Paths();
		}
		this.fileHandler.save_File_Urls();
		
		return this.fileHandler.get_Urls();
	}
	
	private ArrayList<String> getURL(String packetFileName) {
		this.fileHandler.save_File_Urls(packetFileName);
		
		return this.fileHandler.get_Urls();
	}
	
	private ArrayList<String> wotRequest(ArrayList<String> url) {
		this.urlRequest = new WebOfTrust_URLRequest(url, this.outputDireatory, this.status);
		this.urlRequest.getWOTResult();
		return this.urlRequest.getRequest();
	}
	
	private ArrayList<String> vtRequest(ArrayList<String> url) {
		this.urlRequest = new VirusTotal_URLRequest(url, this.outputDireatory, this.status);
		this.urlRequest.getVirusTotalResult();
		return this.urlRequest.getRequest();
	}
	
	public Map<String, ArrayList<String>> getScore() {
		return this.resultMap;
	}
	
	private void display(ArrayList<String> response) {
		this.displayScore = new DisplayScore(this.outputDireatory, this.status);
		this.displayScore.get_WOT_Score(this.wotRequest(response));
		this.displayScore.get_VirusTotal_Score(this.vtRequest(response));
		this.resultMap = this.displayScore.getResultMap();
		this.displayScore.show_Application_Score();
	}
}