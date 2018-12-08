package csie.mcu.edu.tw.group5.test;

public class DDDDDD implements Runnable{
	
	private boolean isAlive;
	
	public DDDDDD() {
		isAlive = true;
	}
	
	public void ter() {
		isAlive = false;
	}
	
	@Override
	public void run() {
		while(isAlive) {
			System.out.println("Hi");
//			try {
//				Thread.sleep(2000);
//			} catch (InterruptedException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
		}
	}
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		double start = System.currentTimeMillis();
		double end, t;
		
		DDDDDD d = new DDDDDD();
		Thread tt = new Thread(d);
		tt.start();
		
		while(true) {
			end = System.currentTimeMillis();
			t = (end - start)/1000;
			if(t > 15) {
				d.ter();
				break;
			}
		}
		System.out.println("Main Thread cost " + t + " seconds.");
	}
}