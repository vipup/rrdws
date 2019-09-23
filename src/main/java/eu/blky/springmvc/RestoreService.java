package eu.blky.springmvc; 
import java.io.File;
import java.io.FileWriter;
import java.io.FilenameFilter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

import javax.annotation.PostConstruct;

import org.jrobin.cmd.RrdCommander;
import org.jrobin.core.RrdDbPool;
import org.jrobin.core.RrdException;
import org.jrobin.core.RrdFileBackend;
import org.jrobin.core.RrdFileBackendFactory;
import org.jrobin.core.jrrd.RRDFile;
import org.jrobin.mrtg.server.Config;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import cc.co.llabor.system.Merger;
import cc.co.llabor.system.StatusMonitor;
import cc.co.llabor.system.Unzipper; 

@Service
public class RestoreService  extends Merger{
	 
 

	private StatusMonitor st;
	private boolean restorePerformedFromExternal=false;

	public RestoreService ( StatusMonitor st ) {
		this.st = st;

		System.out.println("RestoreController created....");
	}

	@PostConstruct
	public void init() {
		this.restore( );
		System.out.println("RestoreController inited."	+ "");
	}
	
	private static Logger log = LoggerFactory.getLogger(RestoreService.class);

	{
		System.out.println("RestoreController loaded");
	}
	 

	public void restore( ) { 
		
		try{
			File workdirTmp = new File ( Config.CALC_DEFAULT_WORKDIR() );
			File tmpdirTmp = new File (System.getProperty("java.io.tmpdir"));
			FilenameFilter filterTmp = new FilenameFilter(){

				@Override
				public boolean accept(File dir, String name) {
					return  // internal - reuse locally stored between restart-redeploy-etc
							(name.startsWith("rrd") && name.endsWith(".backup"))||
							// external - use externally uploaded zip
							(name.startsWith("backup") && name.endsWith(".zip")); 
				} 
			}; 
			// search last backup
			File toRestore = null;
			for (String next: tmpdirTmp.list(filterTmp)){
				if (toRestore == null){
					toRestore = new File(tmpdirTmp, next);
					continue;
				}
				File theNext = new File(tmpdirTmp, next);
				if (toRestore.lastModified() < theNext.lastModified()){
					toRestore = theNext;
				}
			}
			
			if (toRestore != null){
				RrdDbPool.getInstance().reset();
				Unzipper zTmp = new Unzipper(toRestore, workdirTmp);
				zTmp.unzip(this);
				st.getStatus().put("restoreDB", "DB restore Done"); 

			}
		}catch(Exception e){
			log.error("restoreDB", e);
			st.getStatus().put("restoreDB", "DB restore is not possible! New Server/instance/App/Node/DB?");
		}
	}

	public boolean restorePerformedFromExternal() { 
		return isRestorePerformedFromExternal();
	}

	public boolean isRestorePerformedFromExternal() {
		return restorePerformedFromExternal;
	}

	public void setRestorePerformedFromExternal(boolean restorePerformedFromExternal) {
		this.restorePerformedFromExternal = restorePerformedFromExternal;
	}

	@Test 
		/** 
		 * 
		 * @throws IOException
		 * @throws RrdException
		 */
		public boolean postCreateAndUpdateSTDRRD_OwnMergeOverFetchToUpdate(String aRRD /* = "X-553689133.rrd";*/) throws IOException, RrdException {
			long initDate = 920804700; 
			if (!aRRD.endsWith(".rrd")){
				throw new RrdException("this call should be performed with existing rrd-File-name!", aRRD);
			}
			//long  secToLive = 20 * 365 * 24 * 60 *60;	//1 000 001 111    630 720 000	
			
			String RRD_WORK_DIRECTORY = RrdFileBackend.CALC_DEFAULT_WORKDIR() +"/"+RrdFileBackend.RRD_HOME+"/";
			String a = RRD_WORK_DIRECTORY+aRRD;
			String bRRD = aRRD.replace(".rrd","NEW.rrd");
			String b = RRD_WORK_DIRECTORY+bRRD;
			String cRRD = aRRD.replace(".rrd",".TMP.rrd");
			String c = RRD_WORK_DIRECTORY+cRRD;
			String cmdGraphrrdTmp = "rrdtool graph  "+cRRD+  ".gif  ";
			String _v=  "- ";
			String _h =  " 480 ";
			String _w = " 640 ";
			String _start = "end-4month";
			String _end = "now";
			String _t = "  - ";
			String dbName = cRRD;
			cmdGraphrrdTmp +="-v '"+_v+"' -t '"+_t+"'  -h "+ _h +" -w  ";
			cmdGraphrrdTmp += _w+" --start="+_start+"   --end="+_end;
			cmdGraphrrdTmp += " DEF:dbdata="+dbName+":data:AVERAGE  ";
			cmdGraphrrdTmp += " DEF:min1="+dbName+":data:MIN  ";
			cmdGraphrrdTmp += " DEF:max1="+dbName+":data:MAX  ";
			//cmdTmp += " LINE1:min1#EE444499  ";
			//cmdTmp += " LINE1:max1#4444EE99 ";
			cmdGraphrrdTmp += " LINE2:dbdata#44EE4499  LINE1:dbdata#003300AA ";
			cmdGraphrrdTmp += "";
			String aToGraph = cmdGraphrrdTmp.replace(cRRD, aRRD);
			System.out.println(aToGraph); 
			RrdCommander.execute(aToGraph);
			String bToGraph = cmdGraphrrdTmp.replace(cRRD, bRRD);
			System.out.println(bToGraph); 
			RrdCommander.execute(bToGraph);
			
			// ws.rrd.csv.RrdUpdateAction.makeCreateCMD(long, String) 
			String crTmp = "rrdtool create " +
					""+cRRD+" --start "+(initDate)+"" + 
					" --step 1 " +
					"				DS:data:GAUGE:2121212240:U:U " +
					"				RRA:AVERAGE:0.5:3:480 " +
					"				RRA:AVERAGE:0.5:17:592 " +
					"				RRA:AVERAGE:0.5:131:340 " +
					"				RRA:AVERAGE:0.5:731:719 " +
					"				RRA:AVERAGE:0.5:10000:2730 " +
					"				RRA:MAX:0.5:3:480 " +
					"				RRA:MAX:0.5:17:592 " +
					"				RRA:MAX:0.5:131:340 " +
					"				RRA:MAX:0.5:731:719 " +
					"				RRA:MAX:0.5:10000:273 " +
					"				RRA:MIN:0.5:3:480 " +
					"				RRA:MIN:0.5:17:592 " +
					"				RRA:MIN:0.5:131:340 " +
					"				RRA:MIN:0.5:731:719 " +
					"				RRA:MIN:0.5:10000:273 " +
													" "; 		
 
			RrdCommander.execute(crTmp  );
			String updatetxtTmp= " rrdtool update "+ cRRD+" "; //920804700:12345
			
			int toshft=1;
			RrdCommander.execute(updatetxtTmp +(initDate+toshft++) +":0"  );
			
			// replacement >> m.mergeRRD(a, b, c);
			// fetch all from a, b, then push it with update into c
			// rrdtool fetch  a.rrd AVERAGE -r 1 -s 920804700 > a.txt
			// rrdtool fetch  b.rrd AVERAGE -r 1 -s 920804700 > b.txt
			String sA = ""+RrdCommander.execute("rrdtool fetch "+aRRD+" -r 1 -s  "+initDate+" AVERAGE");
			writeToTextSubFIle(a+".txt",sA);
			String aTxt[] = sA.split("\n");
			String sB = ""+RrdCommander.execute("rrdtool fetch "+bRRD+" -r 1 -s  "+initDate+" AVERAGE");
			writeToTextSubFIle(b+".txt",sB);
			String bTxt[] = sB.split("\n");
			
			 
			int toSkip = 0;
			String upTmp = updatetxtTmp  ;
			String oldTmp = "----------EMPTY---------------------" ;
			for (int i=0;i<aTxt.length;i+=1) {
				try {
		
					String[] aLINE = aTxt[i].split(":"); 
					toSkip =  Integer.valueOf( aTxt[i].split(":")[0].trim() ) - Integer.valueOf( bTxt[i].split(":")[0].trim()  ); 
					if (toSkip >0) {
						System.out.println("TOSKIP."+toSkip);
					}
					Object valueToPush = aLINE[1].trim().equals("nan")?bTxt[i+toSkip].split(":")[1].trim():aLINE[1].trim();
					
					if ("nan". equals(valueToPush)) continue;
					if ("". equals(valueToPush)) continue;
					
				// DO THE JOB
					upTmp +=  " " +aLINE[0].trim()+":"+ valueToPush;
					if (i%1 == 0) {
						Object o = RrdCommander.execute( upTmp );
						oldTmp = upTmp ;
						upTmp = updatetxtTmp ;
					}
					
				}catch(  ArrayIndexOutOfBoundsException e) {
					System.err.println("BREAK:#"+i+"::"+aTxt[i]);
					System.err.println("BREAK:#"+i+"::"+bTxt[i+toSkip]+"::"+toSkip);
					System.err.println("LAST UPDATE:"+upTmp);
					System.err.println("PRELAST UPDATE: "+oldTmp);
					e.printStackTrace();
					break;
				}catch(  NumberFormatException e) {
					System.err.println("skip:#"+i+"::"+aTxt[i]);
					e.printStackTrace();
				}catch(Throwable e) {
					System.out.println("skip:#"+i+"::"+aTxt[i]);
					System.err.println("LAST UPDATE:"+upTmp);
					System.err.println("PRELAST UPDATE: "+oldTmp);
					upTmp = updatetxtTmp ;
					e.printStackTrace();
					 
				}			
			} 
	
			
	//		RrdCommander.execute("rrdtool graph  "+cRRD+  ".gif --start "+initDate+"  --end "+ (secToLive+initDate) + " DEF:data="+cRRD+":data:AVERAGE AREA:data#FF55FF " );
			System.out.println(cmdGraphrrdTmp);
			RrdCommander.execute(cmdGraphrrdTmp);
			
	
			
	
			
			String toXMPcmd = "rrdtool dump  " + cRRD+" ";
			String Cxml = (String) RrdCommander.execute(toXMPcmd); 
			String pathToExported = writeToTextSubFIle(c,Cxml);
			String resultTmp= aRRD.replace(".rrd",".AB_merged."+System.currentTimeMillis()+".rrd"); // cRRD = aRRD.replace(".rrd","TMP.rrd");
			String cmdREST = "rrdtool restore "+pathToExported+"   "+ resultTmp;
	
	
			String retval = (String) RrdCommander.execute(cmdREST);
			System.out.println("AFTER RESTORE::"+retval+":::");
			
			String cToGraph = cmdGraphrrdTmp.replace(cRRD, resultTmp);
			System.out.println(cToGraph);
			RrdCommander.execute(cToGraph); 
			
			
			(new File(a)).renameTo(new File(a.replace(".rrd", "."+System.currentTimeMillis()+".BAK.rrd")));
			//(new File(resultTmp)).renameTo(new File(a));
		    Path copied = new File(a).toPath();
	
		    File resultFile = new File(RRD_WORK_DIRECTORY+resultTmp);
		    resultFile.deleteOnExit();
			Path originalPath = resultFile.toPath();
		    RrdDbPool.getInstance().reset();
		    System.out.println("FROM:"+originalPath);
		    System.out.println("TO  :"+ copied );
		    Files.copy(originalPath, copied, StandardCopyOption.REPLACE_EXISTING);
		    
		    // clean tmps
		    File aFile = new File(a+".gif");
		    aFile.delete();
		    File bFile = new File(b+".gif");
		    bFile.delete();
		    File cFile = new File(c+".gif");
		    cFile.delete();
			
		    
		    System.out.println(retval);
			return true;
			
		}

	private String writeToTextSubFIle(String path, String data) throws IOException {
		File fileA = new File( path + ".txt");
		fileA.deleteOnExit();
		FileWriter fwA = new FileWriter(fileA);
		fwA.write(data);
		fwA.flush();
		fwA.close();
		return fileA.getCanonicalPath();
	}

	@Override
	public boolean merge(String rrdname) throws IOException, RrdException {
		return postCreateAndUpdateSTDRRD_OwnMergeOverFetchToUpdate( rrdname ); 
	}
 
}