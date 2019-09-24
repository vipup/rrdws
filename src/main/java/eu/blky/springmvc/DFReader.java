package eu.blky.springmvc;

import java.io.BufferedReader;
import java.io.IOException;

public class DFReader {

	private BufferedReader aIN;
	private Long lastTimeStamp;
	private String nextSample;

	public DFReader(BufferedReader inPar) {
		this.aIN = inPar;
	}

	public String readNExt() throws IOException{
		String  sampleTMP;
		for(String aTXT=aIN.readLine();aTXT!=null;aTXT=aIN.readLine()) {
			String[] aLINE = aTXT.split(":");
			Object aVAL = aLINE[1].trim() ;
			if ("nan". equals(aVAL)) continue;
			if ("". equals(aVAL)) continue;
			this.lastTimeStamp = Long.valueOf(aLINE[0].trim());
			sampleTMP = aLINE[0]+":"+aLINE[1];
			return sampleTMP ;
		}
		throw new IOException("EOA");
		
	}

	public long getNextTimestamp() throws IOException {
		if(nextSample==null) {
			this.pushSample(this.readNExt());
		 }
		 return lastTimeStamp; 
	}

	public String popSample() {
		String retval = nextSample;
		nextSample=null;
		return retval; //
	}

	public void pushSample(String nextSample) {
		this.nextSample = nextSample;
	}
}
