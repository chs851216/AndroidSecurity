package csie.mcu.edu.tw.group5.request;

import java.util.ArrayList;

import csie.mcu.edu.tw.group5.file.FileOption;

public abstract class URLRequest implements FileOption {

	protected final String USER_AGENT = "Mozilla/5.0";
    
    protected String requestResult;
    protected String apkFilePath;
    protected String status;
    protected int responseCode;
    protected int error;
    protected int limitCount;
    protected ArrayList <String> urlDataForSearch;
    
    protected ArrayList <String> urlRequestResult = new ArrayList<>();
    
    public String checkStatus() {
        if (this.status.equals("static") || this.status.equals("Static"))
        	this.status = "Static";
        if (status.equals("dynamic") || this.status.equals("Dynamic"))
        	this.status = "Dynamic";
            
        return this.status;
    }
    
    public void addRequest(String reqResult) {
        this.urlRequestResult.add(reqResult);
    }
    
    public ArrayList<String> getRequest() {
        return this.urlRequestResult;
    }
    
    public void getWOTResult() {}
    public void getVirusTotalResult() {}
    protected abstract void saveAllRequest(String result);
    protected abstract void saveErrorRequests(String URL, String errorResult, int responseCode);
}