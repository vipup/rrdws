package ws.rrd.csv;

import java.io.BufferedReader; 
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader; 
import java.util.ArrayList; 
import java.util.Map;
import java.util.TreeMap;  

/** 
 * <b>Description:TODO</b>
 * @author      vipup<br>
 * <br>
 * <b>Copyright:</b>     Copyright (c) 2006-2008 Monster AG <br>
 * <b>Company:</b>       Monster AG  <br>
 * 
 * Creation:  20.04.2010::11:48:50<br> 
 */
public class CSVParser {
	private String headersStr = null;
	String[] heads = null;
	final String DELIM = ",";
	BufferedReader rdr  ;
	
	public CSVParser(InputStream in) throws IOException{
        
		rdr  = new BufferedReader( new InputStreamReader ( in ) );
        
        headersStr  = rdr.readLine();
        init();
 
	}
	private void init (){
        heads = headersStr.split(DELIM);
        heads = combineChains(heads);
	}

	private String[]   combineChains(String[] quoteCOntainedStringPar ) {
		ArrayList<String> headsArr = new ArrayList<String>();
        for (int i=0;i<quoteCOntainedStringPar.length ;i++){
        	String nextStr = quoteCOntainedStringPar[i];
			if (nextStr.startsWith("\"") && (!nextStr.endsWith("\""))){
        		StringBuffer sb = new StringBuffer("");
        		String suffix = "";
        		do{
        			sb.append(suffix);
        			sb.append(nextStr);
        			if (nextStr.endsWith("\""))break; 
        			nextStr = quoteCOntainedStringPar[++i] ;        			
        			suffix = DELIM;        			
        		}while(i<quoteCOntainedStringPar.length);
        		String trimmedStr = sb.toString().substring(1,sb.length()-1).trim();
				headsArr.add(trimmedStr);
        	}else{ // '33' OR '"34"'
        		String trimedStr = nextStr.substring(0,nextStr.length()).trim();
        		trimedStr = trimedStr.startsWith("\"")?trimedStr.substring(1):trimedStr;
        		trimedStr = trimedStr.endsWith("\"")?trimedStr.substring(0,trimedStr.length()- 1):trimedStr;
				headsArr.add (trimedStr.trim());
        	} 
        }
        quoteCOntainedStringPar = headsArr.toArray(new String[]{"XeXe"});
        return quoteCOntainedStringPar ;
	}

	String[] getHeads() {
			return heads;
	} 
	

	
	/**
	 * iterate through in:InputStream line-by-line and call for any NewLine "rrdtool update" for corresponding rrd-DB.
	 *  - first column will be recognized as event-time
	 *  - will skip any duplicated header-lines
	 *  - will ignore any duplicated-time samples
	 *  
	 * @author vipup
	 * @param a
	 * @return list of compleeted work
	 * @throws IOException
	 */
	public Object perform(Action a) throws IOException{
		Map retval = new TreeMap();
		long start = System.currentTimeMillis();
		int lineCount =0;
        for (String nextLineStr = rdr.readLine();nextLineStr != null;nextLineStr = rdr.readLine()){
        	lineCount++;
        	if (nextLineStr.equals(headersStr))continue;
        	if (nextLineStr.equals(""))continue;
        	
        	String[] dataTmp =   nextLineStr.split(DELIM);
        	
        	try{
        		dataTmp = combineChains(dataTmp);
        		if (dataTmp.length>this.heads.length){
        			//WOW!!! new headers!!!
        			System.out.println("HEADS == "+headersStr );
        			System.out.println("nextLineStr == "+nextLineStr);
        			this.headersStr = nextLineStr;
        			init();
        			continue;
        		}
        	}catch(StringIndexOutOfBoundsException e){
        		throw new IOException("error at parcing line "+lineCount +"["+nextLineStr+"]",e);
        	}
        	int i=0;
        	try{
	        	for (String next:dataTmp){
	        		++i;
	        		if (i>=this.heads.length )break;
	        		if (next == dataTmp[0])continue; 
	        		// timestamp, uri, data
	        		if (!("".equals(next) || " ".equals(next) ) ){
	        			String keyTmp = heads[i];
	        			final Object valTmp = a.perform(keyTmp, dataTmp[0],    next );
	        			ArrayList line = (ArrayList )retval.get( keyTmp);
	        			if( line == null ){
	        				line = new ArrayList();
	        				retval.put(keyTmp, line);
	        			}
	        			if (valTmp instanceof Exception){
	        				//((Exception)valTmp ).printStackTrace();
	        			}else{
	        				line.add( ""+valTmp+":"+next );
	        			}
	        		}
	        	}
        	}catch(ArrayIndexOutOfBoundsException e){
        		System.out.println( e.getMessage() + "::::"+i+" ["+nextLineStr);
        	}
        	if (lineCount%100 == 0  || lineCount%100 == 1){
        		System.out.println( "#" +lineCount +"done with "+(1000.00*(1+lineCount)/((System.currentTimeMillis()-start)+1) ) +" lps.");
        	}
        }
        System.out.println("done "+lineCount+" lines in "+(System.currentTimeMillis()-start)+" ms. ::= "+(1000.00*(1+lineCount)/((System.currentTimeMillis()-start)+1) ) +" lps.");
        return retval;
	}
}


 