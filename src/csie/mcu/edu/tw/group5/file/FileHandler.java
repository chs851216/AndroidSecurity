package csie.mcu.edu.tw.group5.file;

import java.util.ArrayList;

public class FileHandler {

	private PathManager paths;
    private SearchURL allUrls;
    
    public FileHandler(String fileName, String status) {
        this.paths = new PathManager(fileName, status);
        this.allUrls = new SearchURL(fileName, status);
    }
    
    public FileHandler(String fileName, String status, String regex) {
        this.paths = new PathManager(fileName, status);
        if("".equals(regex)) {
        	this.allUrls = new SearchURL(fileName, status, regex);
        }
        this.allUrls = new SearchURL(fileName, status);
    }
    
    public int getUrlCount() {
    	return this.allUrls.getUrlCount();
    }
    
    public void save_File_Paths() {
        this.paths.saveFile();
    }
    
    /*
     * for static
     */
    public void save_File_Urls() {
        this.allUrls.searchAndSaveURL();
    }
    
    /*
     * for dynamics
     */
    public void save_File_Urls(String packetFileName) {
        this.allUrls.searchAndSaveURL(packetFileName);
    }
    
    public ArrayList<String> get_Urls() {
        return this.allUrls.getUrlFiles();
    }
}