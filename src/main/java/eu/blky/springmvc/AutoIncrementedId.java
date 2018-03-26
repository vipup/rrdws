package eu.blky.springmvc;

public class AutoIncrementedId {
	private static int callCount = 0;
	
	private synchronized int getUpdatedCounter(){
		return callCount++;
	}
	
	public String toString() {
		return ""+getUpdatedCounter();
	}

}
