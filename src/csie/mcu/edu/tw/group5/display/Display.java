package csie.mcu.edu.tw.group5.display;

public abstract class Display {

	protected String applicationName;
    protected String status;
    protected int urlCount;
    protected double totalScore;
    
    protected String checkStatus() {
        if (this.status.equals("static") || this.status.equals("Static"))
        	this.status = "Static";
        if (this.status.equals("dynamic") || this.status.equals("Dynamic"))
        	this.status = "Dynamic";
            
        return status;
    }
    
    protected abstract void reset_varible_state();
    protected abstract void get_Score_Data();
    protected abstract String display();
}