package csie.mcu.edu.tw.group5.file;

import java.util.ArrayList;

public interface FileOption {

	public boolean isWriteToFile = true;
    
    public String checkStatus();
    public void addRequest(String reqResult);
    public ArrayList<String> getRequest();
}