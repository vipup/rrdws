package eu.blky.springmvc;

import java.io.BufferedReader;
import java.io.IOException;

public class DFReader {

	private BufferedReader aIN;
	private Long lastTimeStamp;
	private String nextSample;
	private DFReader chainedReader;

	public DFReader(BufferedReader inPar) {
		this.aIN = inPar;
	}

	public DFReader(BufferedReader bufferedReader, DFReader a) {
		this(bufferedReader);
		this.chainedReader = a; 
		//a.chainedReader = this;
	}
	
	public String readNextChainedSample() throws IOException {
		try {
			String retval = chainedReader==null||getNextTimestamp()<=chainedReader.getNextTimestamp()?popSample():chainedReader.readNextChainedSample();
			return retval;
		}catch(StackOverflowError e) {
			throw new IOException("EOChain");
		}
	}

	public String readNExt() throws IOException{
		String  sampleTMP;
		for(String aTXT=aIN.readLine();aTXT!=null;aTXT=aIN.readLine()) {
			if ("". equals(aTXT.trim())) continue;
			if ("nan". equals(aTXT.trim())) continue;
			String[] aLINE = aTXT.split(":");
			if (aLINE.length<2) continue;
			Object aVAL = aLINE[1].trim() ;
			if ("nan". equals(aVAL)) continue;
			if ("". equals(aVAL)) continue;
			this.lastTimeStamp = Long.valueOf(aLINE[0].trim());
			sampleTMP = aLINE[0]+":"+aLINE[1];
			return sampleTMP ;
		}
		DFReader lastHope = this.chainedReader;
		//this.chainedReader = null;
		
		return lastHope.readNExt();
		
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
