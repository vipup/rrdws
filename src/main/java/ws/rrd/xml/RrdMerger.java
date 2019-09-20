package ws.rrd.xml;

import java.io.BufferedReader; 
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.Date;
import java.util.Set;
import java.util.TreeMap;
import java.util.regex.Pattern;

import org.jrobin.cmd.RrdCommander;
import org.jrobin.core.RrdException;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
import org.apache.juli.logging.Log;
import org.apache.juli.logging.LogFactory;

//import eu.blky.rrd.cmd.RrdCommander;
//import eu.blky.rrd.core.RrdException;

/**
 * <b>Description:DraftUndDirty Python2Java reimplementation of
 * http://oss.oetiker.ch/rrdtool/pub/contrib/merge-rrd.tgz </b>
 * 
 * @author vipup<br>
 *         <br>
 *         <b>Copyright:</b> Copyright (c) 2006-2008 Monster AG <br>
 *         <b>Company:</b> Monster AG <br>
 * FA
 *         Creation: 16.11.2011::14:50:49<br>
 */
public class RrdMerger {
	public static final String ANY_WHITE_SPACE_CHAR = " \n\t";

	//private static final Logger log = LoggerFactory.getLogger(RrdMerger.class.getName());
	//private static Logger log = LoggerFactory.getLogger(RestoreService.class);
	private static final Log LOG = LogFactory.getLog(RrdMerger.class);
	

	public void mergeRRD(String aPar, String bPar, String cPar) throws IOException, RrdException {
		File fileA, fileB, fileC, fileD;
		String Acmd = "rrdtool dump  " + aPar;
		String Axml = (String) RrdCommander.execute(Acmd); 
		// TODO: RrdCommander.execute(Acmd+" "+aPar+".dump.xml"); // existAsXml(aPar) ? readFully(aPar) :
		{
			String dumpA = aPar+".Adump.XML";
			fileA = new File(dumpA);
			fileA.deleteOnExit();
			FileWriter fwA = new FileWriter(fileA);
			fwA.write(Axml);
			fwA.close();
		}
		String Bcmd = "rrdtool dump  " + bPar;
		String Bxml = (String) RrdCommander.execute(Bcmd); 
		// TODO :RrdCommander.execute(Bcmd+" "+bPar+".dump.xml");; // existAsXml(bPar) ? readFully(bPar) :
		{
			String dumpB = bPar+".Bdump.XML";
			fileB = new File( dumpB);
			fileB.deleteOnExit();
			FileWriter fwB = new FileWriter(fileB);
			fwB.write(Bxml);
			fwB.close();
		}
		ByteArrayOutputStream CxmlMERGED = new ByteArrayOutputStream();
		PrintWriter Cpw = new PrintWriter(CxmlMERGED, true);
		Cpw.write(rra(Axml, Bxml));
		Cpw.write("</rrd>");
		Cpw.flush();
		Cpw.close();
		String dumpCTMP = cPar+".Ctmp.XML";
		fileC = new File(dumpCTMP);
		fileC.deleteOnExit();
		CxmlMERGED.writeTo(new FileOutputStream(fileC));
		CxmlMERGED.flush();
		CxmlMERGED.close();
		String CcmdREST = "rrdtool restore "+dumpCTMP+" "+cPar+" ";
		LOG.error("RESTORE:{}..."+cPar);
		try {
			String cTMP = (String) RrdCommander.execute(CcmdREST); // existAsXml(bPar) ? readFully(bPar) :
			LOG.debug("RESTORED:{}"+cTMP);
			fileC.delete();
			fileB.delete();
			fileA.delete();
		}catch(Exception e) {
			System.out.println("CcmdREST::::::::::::::::"+CcmdREST);
			e.printStackTrace();
		}
		
		// check again - results of merge: 
		String Ccmd = "rrdtool dump  " + cPar;
		String Cxml = (String) RrdCommander.execute(Ccmd); 
		// TODO: RrdCommander.execute(Acmd+" "+aPar+".dump.xml"); // existAsXml(aPar) ? readFully(aPar) :
		try{
			String dumpC = cPar+".Ddump.XML";
			fileD = new File(dumpC);
			fileD.deleteOnExit();
			FileWriter fwC = new FileWriter(fileD);
			fwC.write(Cxml);
			fwC.close();
			fileD.delete();
		}catch (Exception e) {
			// TODO: handle exception
		} 

	}

	// https://github.com/mcdarren/rrdtool-merge/blob/master/rrdtool-merge.pl
	// * sub rra
	String rra(String a, String b) {
		String retval = " ";
		Diff d = new Diff();
		String[] splitA = a.split("\n");
		String[] splitB = b.split("\n");
//		String[] sc = d.diff(splitA, splitB);
		int iA = 0;
		int iB = 0;
		int iC = 0;
		 
		
/// PY###
//		def main():
//			  rrd_data = {}
//			  rrds = sys.argv[1:]
//			  last_rrd = len(rrds) - 1
//
//			  for i, rrdname in enumerate(rrds):
//			    p = subprocess.Popen(
//			          ('rrdtool', 'dump', rrdname), stdout=subprocess.PIPE)
		
//			    for j, line in enumerate(p.stdout):
		String cf = null;
		String pdp = null;
		String k = "";
		TreeMap  startDatabase = null; 
		for (;iB< splitB.length-1 && iA <splitA.length-1;) {
			
			String aLINE = splitA[iA];
			String bLINE = splitB[iB]; 
			for(; iB< splitB.length-1 && iA <splitA.length-1 ;aLINE = splitA[iA], bLINE = splitB[iB] )	
			{
				LOG.trace("-B-"+bLINE);
				LOG.trace("A--"+bLINE);
 
				
				String l = bLINE;//splitA[iA];
				if (!aLINE.equals(bLINE)) {
					LOG.trace("???"+aLINE +"!="+bLINE);
					
					{
						retval += l;
						retval += "\n";
						iA++;
						iB++;				
						break;
					}
				};
				
	//			      m = re.search(r'<cf>(.*)</cf>', line)		
	//			      if m:
	//			        cf = m.group(1)
				  if (aLINE.indexOf("<cf>")>0 && aLINE.indexOf("</cf>")>0 ) {
					  cf = aLINE;
				  }
	//			      m = re.search(r'<pdp_per_row>(.*)</pdp_per_row>', line)
	//			      if m:
	//			        pdp = m.group(1)
				  if (aLINE.indexOf("<pdp_per_row>")>0 && aLINE.indexOf("</pdp_per_row>")>0 ) {
					  pdp = aLINE;
				  }			  
	//			      
	//			      m = re.search(r' / (\d+) --> (.*)', line)
	//			      if m:
	//			        k = cf + pdp
	//			        rrd_data.setdefault(k, {})
	//			        if ('NaN' not in m.group(2)) or (
	//			            m.group(1) not in rrd_data[k]):
	//			          rrd_data[k][m.group(1)] = line
	//			        line = rrd_data[k][m.group(1)]
				  if (aLINE.indexOf(" CET / ")>0  ) {
					  k = cf + pdp;
				  }	
				  if (aLINE.indexOf("<database>")>=0  ) {
					  //startDatabase
					  startDatabase = new TreeMap(); 
					  break;
				  }	
				  //  
	//
				  
	//			      if i == last_rrd:
	//			        print line.rstrip()
				  iA++;
				  iB++;
				 
					retval += l;
					retval += "\n";
	//
			} //  a|b != c
					
			String aValue = ""; 
			Object bValue = "";
			Object aKey = "FAKEA";
			Object bKey = "FAKEB";
			while(startDatabase != null && iB< splitB.length-1 && iA <splitA.length-1)	{
				  iA++;
				  iB++;
				  
				  aLINE = splitA[iA];
				  bLINE = splitB[iB];				  
				  if (aLINE.indexOf("</database>")>=0 ||  iB == splitB.length ||  iA == splitA.length) {
					String databaseLINES = toDATABASE(startDatabase);
					  
					retval += databaseLINES;
					//flush database
					startDatabase=null;
				 				
					break;
				  }		
				  // A
				  if (toDateKey(aLINE) !=null) {
					  startDatabase.put(aKey, aValue);
					  //log.trace(".....A>>>>>>>>+++>>>>"+aValue);	
					  aValue = aLINE;
					  aKey = toDateKey(aLINE);
					  
					  
				  }else { // Overwrite
					  aValue += aLINE;
					  startDatabase.put(aKey, aValue);
					  //log.trace(">>>>>A>>>>>>>>+++>>>>"+aValue);					  
				  }
				  // B
				  if (toDateKey(bLINE) !=null) {

					  startDatabase.put(bKey, bValue);
					  //log.trace("......B>>>>>>>+++>>>>"+bValue);
					  bValue = bLINE;
					  bKey = toDateKey(bLINE);
					  
					  
				  }else {// Overwrite
					  bValue += bLINE;
					  startDatabase.put(bKey, bValue);
					  //log.trace(">>>>>>B>>>>>>>+++>>>>"+bValue);
					  
				  }
				  
				  
			}
			

		}// for (;iC<c.length;iC++ ) {
		 
			
		retval += toDATABASE(startDatabase);
	 
		return retval;
	}

	private String toDATABASE(TreeMap startDatabase) {
		String databaseLINES="";
		try {
			Set keySet = startDatabase.keySet();
			Object[] array = keySet.toArray();
			databaseLINES+="<database>\n";
			for(Object dKey:array ) {
				String dValue = (String) startDatabase.get(dKey);
				if (dValue.indexOf("</row>")>0) {
					databaseLINES += dValue;
					databaseLINES += "\n";
				}else {
					LOG.error(" !!!!!!"+dKey+"!!!!!!!!!!!"+dValue);
				}
			}
			//databaseLINES+="</database>\n";
		}catch(Exception e) {
			//IGNORE e.printStackTrace();	
		}
		return databaseLINES;
	}

	private String toDateKey(String string) {
		String retval = null;
		try {
			int beginIndex = string.indexOf("/")+2;
			int endIndex= string.indexOf("-->")-2;
			retval = ""+new Date (Long.parseLong(string.substring(beginIndex, endIndex))*10000); // Ahhhh!? 10000 :-D
		}catch (Exception e) {
			// ignore
		}
		return retval;
	}

	private String readFully(String opath) throws IOException {
		FileInputStream fileInputStream = new FileInputStream(opath);
		BufferedReader rdTmp = new BufferedReader(new InputStreamReader(fileInputStream));
		StringBuffer retval = new StringBuffer();
		for (String lnTmp = rdTmp.readLine(); lnTmp != null; lnTmp = rdTmp.readLine()) {
			retval.append(lnTmp);
			retval.append("\n");
		}
		return retval.toString();

	}

}
