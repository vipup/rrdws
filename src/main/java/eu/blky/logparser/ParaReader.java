package eu.blky.logparser;

import java.io.BufferedReader;
import java.io.IOException;
/**
 * Reader is enable to read multiple time-stamp-leaded, EOL-separated text-input-stream into single input.
 * Example: C= A+B
 * A-Stream: 00010:44441\n00020:44442\n00030:44443\n 
 * B-Stream: 00010:88881\n00020:88882\n00030:88883\n 
 * C-Stream: 00010:44441\n00010:88881\n00020:44442\n00020:88882\n00030:44443\n00030:88883\n0004:88884\n
 * 
 * Here is assumed, that leaded Timestamp is historically ordered (line after always has timestamp equals or great then prev).
 * Leaded timestamp will be analized. 
 * The rest of any text-line will be keept unmodified. 
 * 
 * 
 * @author i1
 *
 */
public class ParaReader/* implements Reader */{

	private BufferedReader myIN;
	private long lastTimeStamp=-1;
	private String nextSample;
	private ParaReader chainedReader;

	public ParaReader(BufferedReader inPar) {
		this.myIN = inPar;
	}

	public ParaReader(BufferedReader bufferedReader, ParaReader a) {
		this(bufferedReader);
		this.chainedReader = a; 
		//a.chainedReader = this;
	}
	
	public String readLine()  {
 
			String retval = null;
			if ( chainedReader==null ) {
				retval  = popSample();
			}else {
				long myTIME = getNextTimestamp();
			 
				if (myTIME==this.lastTimeStamp){
					//System.out.println("+++++++++++++++"+myTIME+" << "+chainedReader.getNextTimestamp());	
					retval  = popSample();

//				}else if (myTIME==chTIME){
//					System.out.println("..............."+myTIME+" == "+chTIME);
//					retval = popSample(); 										
				}else {
					//System.out.println("---------------"+myTIME+" >> "+chainedReader.getNextTimestamp());
					retval = chainedReader.readLine();
//					if (myTIME==this.lastTimeStamp){
						 
//					}else {
//						retval = chainedReader.readLine();
//					}
				}
			}	  
			return retval;
  
	}

	private String readOwnNext(){
		
		String  sampleTMP;
		try {
			for(String aTXT=myIN.readLine();aTXT!=null;aTXT=myIN.readLine()) {
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
		}catch(IOException e) {
			this.lastTimeStamp = Long.MAX_VALUE;
		}
		this.lastTimeStamp = Long.MAX_VALUE;
		return null;		
	}

	protected long getNextTimestamp()  {
		if( lastTimeStamp < Long.MAX_VALUE  && nextSample==null ) {
			this.pushSample(this.readOwnNext());
		 }
		 return chainedReader==null? lastTimeStamp:Math.min(lastTimeStamp, chainedReader.getNextTimestamp()); 
	}

	protected String popSample() {
		String retval = nextSample;
		nextSample=null;
		
		return retval; 
	}

	public void pushSample(String nextSample) {
		this.nextSample = nextSample;
	}
}
