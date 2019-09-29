package eu.blky.springmvc; 
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.FilenameFilter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import javax.annotation.PostConstruct;

import org.jrobin.cmd.RrdCommander;
import org.jrobin.core.FetchData;
import org.jrobin.core.RrdDbPool;
import org.jrobin.core.RrdException;
import org.jrobin.core.RrdFileBackend;
import org.jrobin.core.RrdFileBackendFactory;
import org.jrobin.core.jrrd.RRDFile;
import org.jrobin.mrtg.server.Config;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import cc.co.llabor.system.Merger;
import cc.co.llabor.system.StatusMonitor;
import cc.co.llabor.system.Unzipper;
import cc.co.llabor.websocket.PoloWSEndpoint;
import eu.blky.logparser.ParaReader; 

@Service
public class RestoreService  extends Merger{
	 
	// works
	@Autowired
	private PoloWSEndpoint poloWS;

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
		
		try {
			if (poloWS !=null ) {
				System.out.println("STOP poloWS-...");
			 	poloWS.destroy();
			}
			
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
					if ((new File(tmpdirTmp,"unzipperwhitelist.txt").exists())){
						List<String> whiteList = readWhiteList(new File(tmpdirTmp,"unzipperwhitelist.txt"));
						zTmp.setWhilelist(whiteList);
					}
					zTmp.unzip(this);
					st.getStatus().put("restoreDB", "DB restore Done"); 
	
				}
			}catch(Exception e){
				log.error("restoreDB", e);
				st.getStatus().put("restoreDB", "DB restore is not possible! New Server/instance/App/Node/DB?");
			}
			if (poloWS !=null ) {
				System.out.println("...and START poloWS again...");
				poloWS.start();
			}
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}	
	}

	private List<String> readWhiteList(File file) {
		
		List<String> retval = new ArrayList<String>();
		BufferedReader bin;
		try {
			bin = new BufferedReader(new FileReader(file));
			for(String l=bin.readLine();l!=null;l=bin.readLine()) {
				retval.add(l.split("\t")[2]);
			}
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}  

		return retval ; 
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

	/** 
	 * 
	 * @throws IOException
	 * @throws RrdException
	 */
	public static boolean postCreateAndUpdateSTDRRD_OwnMergeOverFetchToUpdate(String aRRD /* = "X-553689133.rrd";*/) throws IOException, RrdException {
		long initDate = 920804700; 
		long  secToLive = 1 * 365 * 24 * 60 *60;	//1 000 001 111    630 720 000	
		if (!aRRD.endsWith(".rrd")){
			throw new RrdException("this call should be performed with existing rrd-File-name!", aRRD);
		}
		String RRD_WORK_DIRECTORY = RrdFileBackend.CALC_DEFAULT_WORKDIR() +"/"+RrdFileBackend.RRD_HOME+"/";
		if (!(new File(RRD_WORK_DIRECTORY , aRRD)).exists()) {
			Path target = new File(RRD_WORK_DIRECTORY , aRRD).toPath();
			Path newTmp = new File(RRD_WORK_DIRECTORY , aRRD.replace(".rrd", "NEW.rrd")).toPath();
			Files.copy(newTmp, target, StandardCopyOption.REPLACE_EXISTING);
			return true;
		}
		//long  secToLive = 20 * 365 * 24 * 60 *60;	//1 000 001 111    630 720 000	

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
		cmdGraphrrdTmp += " LINE1:min1#EE444499  ";
		cmdGraphrrdTmp += " LINE1:max1#4444EE99 ";
		cmdGraphrrdTmp += " LINE2:dbdata#44EE4499  LINE1:dbdata#003300AA ";
		cmdGraphrrdTmp += "";
		String aToGraph = cmdGraphrrdTmp.replace(cRRD, aRRD);

		Properties pA = new Properties();
		pA.load(new ByteArrayInputStream( (""+RrdCommander.execute(" rrdtool info  "+aRRD)).getBytes() ));
		Properties pB = new Properties();
		pB.load(new ByteArrayInputStream( (""+RrdCommander.execute(" rrdtool info  "+bRRD)).getBytes() ));
		if (pB.get( "last_update").equals(pA.get( "last_update"))) {
			System.out.println("nothig to do:: Last update:"+pB.get( "last_update"));
			
			return (new File(bRRD)).delete();
		}	
		System.out.println(aToGraph); 
		if (aToGraph.indexOf("553689133")>=0) RrdCommander.execute(aToGraph);
		String bToGraph = cmdGraphrrdTmp.replace(cRRD, bRRD);
		System.out.println(bToGraph); 
		if (bToGraph.indexOf("553689133")>=0)RrdCommander.execute(bToGraph);
		
		// ws.rrd.csv.RrdUpdateAction.makeCreateCMD(long, String) 
		String crTmp = "rrdtool create " +
				""+cRRD+" --start "+(initDate)+"" + 
				" --step 1 " +
				"				DS:data:GAUGE:2121212240:U:U "+ 
				"				RRA:AVERAGE:0.5:3:480 " +
				"				RRA:AVERAGE:0.5:17:592 " +
				"				RRA:AVERAGE:0.5:131:340 " +
				"				RRA:AVERAGE:0.5:731:719 " + 
				"				RRA:AVERAGE:0.5:10000:273 " +  
				"				RRA:MAX:0.5:3:480 " +
				"				RRA:MAX:0.5:17:592 " +
				"				RRA:MAX:0.5:131:340 " +
				"				RRA:MAX:0.5:731:719 " +
				"				RRA:MAX:0.5:10000:7310 " +
				"				RRA:MIN:0.5:3:480 " +
				"				RRA:MIN:0.5:17:592 " +
				"				RRA:MIN:0.5:131:340 " +
				"				RRA:MIN:0.5:731:719 " +
				"				RRA:MIN:0.5:10000:7310 " + 
												 
				" "; 		

		RrdCommander.execute(crTmp  );
		String updatetxtTmp= " rrdtool update "+ cRRD+" "; //920804700:12345
		
		int toshft=1;
		RrdCommander.execute(updatetxtTmp +(initDate+toshft++) +":0"  );
		
		// replacement >> m.mergeRRD(a, b, c);
		// fetch all from a, b, then push it with update into c
		// rrdtool fetch  a.rrd AVERAGE -r 1 -s 920804700 > a.txt
		// rrdtool fetch  b.rrd AVERAGE -r 1 -s 920804700 > b.txt
		ParaReader aR = null;
		/**
		 * new DFReader(aIN); 
		DFReader bR = new DFReader(bIN,aR);
		 */
		for (String rate:  new String[]{ "AVERAGE","MAX","MIN"} ) {
						
					
					String command = "rrdtool fetch "+aRRD+" -r 1 -s  "+initDate+" "+rate; // 1hour  . see 3600 https://oss.oetiker.ch/rrdtool/doc/rrdfetch.en.html
					log.info(command); // command = "rrdtool info "+aRRD; RrdCommander.execute("rrdtool tune -h data:240 "+aRRD);RrdCommander.execute("rrdtool info "+aRRD);
					FetchData fA = (FetchData) RrdCommander.execute(command);
					File a2DEL = writeToSubFIle(a+"."+rate+".A.txt",fA);
					
					String command2 = "rrdtool fetch "+bRRD+"   -r 1  -s  "+initDate+" "+rate;
					log.info(command2);
					FetchData fB = (FetchData) RrdCommander.execute(command2);
					File b2DEL = writeToSubFIle(b+"."+rate+".B.txt", fB);
					
					//String aTxt[] = sA.split("\n");
					//String bTxt[] = sB.split("\n");
					
	
					BufferedReader aIN = new BufferedReader(new FileReader(a2DEL));
					aR = new   ParaReader(aIN,aR);
					BufferedReader bIN = new BufferedReader(new FileReader(b2DEL));
					aR = new   ParaReader(bIN,aR);
		}
			
		  
		String upTmp = updatetxtTmp  ;
		String oldTmp = "----------EMPTY---------------------" ;
		int i = 0;
		int j = 0;

		try { 
			long lastTIMESTAMP = 0; 
			for (String popSample = aR.readLine(); popSample != null; popSample = aR.readLine() ) {
				log.trace("O:{}",popSample);
				i++; 
				
				long newTIMESTAMP = Long.valueOf(popSample.split(":")[0].trim());
				if (lastTIMESTAMP <newTIMESTAMP) {
					lastTIMESTAMP =newTIMESTAMP;
					j=0;
				}else{
					j++;
				}
				if (newTIMESTAMP/10 ==  lastTIMESTAMP/10) { 
					popSample = popSample.replace(""+lastTIMESTAMP, ""+(lastTIMESTAMP+(j))); 
				} 
				log.trace("m:{}",popSample);
				upTmp +=  " "+popSample.replaceAll(" " , "");
				
				if (i%99 == 0) {
					// DO THE JOB 					upTmp +=  " " +aLINE[0].trim()+":"+ valueToPush; 
					Object o = RrdCommander.execute( upTmp );
					log.trace("::{}",o);
					oldTmp = upTmp ;
					upTmp = updatetxtTmp ;
				}
			}
		}catch (IOException e) {
			try { 
				Object o = RrdCommander.execute( upTmp );
				log.trace("{}",o);
			}catch (Exception e1) {
				log.error("Object o = RrdCommander.execute( "+upTmp+" );",e1);
				e1.printStackTrace();
			}
		}catch (Exception e) {
			log.error("oldTmp::::::::::{}{}{}::::::::",oldTmp);
			log.error("upTmp:::::{}{}{}:::::::::::::",upTmp);
			e.printStackTrace();
		} 
		//secToLive+
		RrdCommander.execute("rrdtool graph  "+cRRD+  ".gif  --start "+initDate+"  --end "+ (secToLive+initDate) + " DEF:data="+cRRD+":data:AVERAGE AREA:data#FF55FF " );
		RrdCommander.execute("rrdtool graph  "+cRRD+  ".O.gif -o --start "+initDate+"  --end "+ (secToLive+initDate) + " DEF:data="+cRRD+":data:AVERAGE AREA:data#FF55FF " );
		System.out.println("RESULT GIF:"+cmdGraphrrdTmp);
		if (cmdGraphrrdTmp.indexOf("graph")>=0)			RrdCommander.execute(cmdGraphrrdTmp);
		

		

		
		String toXMPcmd = "rrdtool dump  " + cRRD+" ";
		String Cxml = (String) RrdCommander.execute(toXMPcmd); 
		File pathToExported = writeToSubFIle(c+".XML",Cxml);
		String resultTmp= aRRD.replace(".rrd",".AB_merged."+System.currentTimeMillis()+".rrd"); // cRRD = aRRD.replace(".rrd","TMP.rrd");
		String cmdREST = "rrdtool restore "+pathToExported.getCanonicalPath()+"   "+ resultTmp;


		String retval = (String) RrdCommander.execute(cmdREST);
		log.info("AFTER RESTORE::{}:::",retval );

		
		String cToGraph = cmdGraphrrdTmp.replace(cRRD, resultTmp);
		log.info("TO EXEC::",cToGraph);
		if (cToGraph.indexOf("X-553689133")>=0)				RrdCommander.execute(cToGraph); 
		
		
		(new File(a)).renameTo(new File(a.replace(".rrd", "."+System.currentTimeMillis()+".BAK.rrd")));

	    Path copied = new File(a).toPath();

	    File resultFile = new File(RRD_WORK_DIRECTORY+resultTmp);
	    resultFile.deleteOnExit();
		Path originalPath = resultFile.toPath();
	    RrdDbPool.getInstance().reset();
	    System.out.println("FROM:"+originalPath);
	    System.out.println("TO  :"+ copied );
	    Files.copy(originalPath, copied, StandardCopyOption.REPLACE_EXISTING);
	    
	    // clean tmps
	    if (!a.contains("X-553689133")) {
		    File aFile = new File(a+".gif");
		    aFile.delete();
		    File bFile = new File(b+".gif");
		    bFile.delete();
		    File cFile = new File(c+".gif");
		    cFile.delete();
	    }
	    
	    System.out.println(retval);
		return true;
		
	}

	private static File writeToSubFIle(String path, FetchData data) throws IOException {
		File fileA = new File(path);
		fileA.deleteOnExit();
		FileWriter fwA = new FileWriter(fileA);
		data.writeTo(fwA);
		fwA.flush();
		fwA.close();
		return fileA;
	}
	
	private static File writeToSubFIle(String path, String data) throws IOException {
		File fileA = new File(path);
		fileA.deleteOnExit();
		FileWriter fwA = new FileWriter(fileA);
		fwA.write(data);
		fwA.flush();
		fwA.close();
		return fileA;
	}	 

	@Override
	public boolean merge(String rrdname) throws IOException { 
		try {
			return postCreateAndUpdateSTDRRD_OwnMergeOverFetchToUpdate( rrdname );
		} catch (RrdException e) {	
			System.out.println("IGNORE error:"+e.getMessage());System.gc();e.printStackTrace();
			return false;
		} catch (Throwable  e) {
			e.printStackTrace();
			return false;
		} 
	}

	/** 
	 * take A.rrd , B.rrd::File , and merge it into C.rrd. then rename A.rrd into A.bak#${TIMESTAMP}.rrd, B.rrd into B.bak#${TIMESTAMP}.rrd
	 * @return merged C.rrd == A.rrd:: ::File 
	 * @throws IOException
	 * @throws RrdException
	 */
	public static File mergeAndB(File a, File b) throws IOException, RrdException {
		long initDate = 920804700; 
		long  secToLive = 1 * 365 * 24 * 60 *60;	//1 000 001 111    630 720 000	
		if (!a.getAbsolutePath().endsWith(".rrd")){
			throw new RrdException("this call should be performed with existing rrd-File-name!",a);
		}
		if (!b.exists()){
			throw new RrdException("new File should exist before call this method:!",b) ;
		}		 
		if ((!a.exists()) && (b.exists())) {
			Path c = Files.copy(b.toPath() , a.toPath(), StandardCopyOption.REPLACE_EXISTING); 
			return c.toFile();
		}  
	
		Properties pA = new Properties();
		pA.load(new ByteArrayInputStream( (""+RrdCommander.execute(" rrdtool info  "+a.getAbsolutePath())).getBytes() ));
		Properties pB = new Properties();
		pB.load(new ByteArrayInputStream( (""+RrdCommander.execute(" rrdtool info  "+b.getAbsolutePath())).getBytes() ));
		if (pB.get( "last_update").equals(pA.get( "last_update"))) {
			System.out.println("nothig to do:: Last update:"+pB.get( "last_update")+"  B == A! B --> removed."); 
			b.delete();
			return a;
		}	 
		
		// ws.rrd.csv.RrdUpdateAction.makeCreateCMD(long, String) 
		String aRRD_origin = a.getAbsolutePath();
		String aRRD = aRRD_origin.replace(".rrd", ".A#"+System.currentTimeMillis()+".rrd" );
		Files.copy(a.toPath(), new File(aRRD).toPath());
		String bRRD = b.getAbsolutePath();
		String cRRD = a.getAbsolutePath().replace(".rrd", ".C#"+System.currentTimeMillis()+".rrd");
		File cTmp = new File(cRRD);
		mergeLinesOfDumps(initDate, aRRD, bRRD, cRRD); 
		//secToLive+
//		RrdCommander.execute("rrdtool graph  "+cRRD+  ".gif  --start "+initDate+"  --end "+ (secToLive+initDate) + " DEF:data="+cRRD+":data:AVERAGE AREA:data#FF55FF " );
//		RrdCommander.execute("rrdtool graph  "+cRRD+  ".O.gif -o --start "+initDate+"  --end "+ (secToLive+initDate) + " DEF:data="+cRRD+":data:AVERAGE AREA:data#FF55FF " );
//		String toXMPcmd = "rrdtool dump  " + cRRD+" ";
//		String Cxml = (String) RrdCommander.execute(toXMPcmd); 
//		File pathToExported = writeToSubFIle(cRRD+".XML",Cxml);
//		String resultTmp= aRRD.replace(".rrd",".AB_merged."+System.currentTimeMillis()+".rrd"); // cRRD = aRRD.replace(".rrd","TMP.rrd");
//		String cmdREST = "rrdtool restore "+pathToExported.getCanonicalPath()+"   "+ resultTmp;
//	
//	
//		String retval = (String) RrdCommander.execute(cmdREST);
//		log.info("AFTER RESTORE::{}:::",retval );
//	
//		
//		String cToGraph = cmdGraphrrdTmp.replace(cRRD, resultTmp);
//		log.info("TO EXEC::",cToGraph);
//		if (cToGraph.indexOf("X-553689133")>=0)				RrdCommander.execute(cToGraph); 
//		
//		
//		(new File(a)).renameTo(new File(a.replace(".rrd", "."+System.currentTimeMillis()+".BAK.rrd")));
//	
//	    Path copied = new File(a).toPath();
//	
//	    File resultFile = new File(RRD_WORK_DIRECTORY+resultTmp);
//	    resultFile.deleteOnExit();
//		Path originalPath = resultFile.toPath();
//	    RrdDbPool.getInstance().reset();
//	    System.out.println("FROM:"+originalPath);
//	    System.out.println("TO  :"+ copied );
//	    Files.copy(originalPath, copied, StandardCopyOption.REPLACE_EXISTING);
//	    
//	    // clean tmps
//	    if (!a.contains("X-553689133")) {
//		    File aFile = new File(a+".gif");
//		    aFile.delete();
//		    File bFile = new File(b+".gif");
//		    bFile.delete();
//		    File cFile = new File(c+".gif");
//		    cFile.delete();
//	    }
//	    
//	    System.out.println(retval);
//		return true;
		return cTmp;
		
	}

	private static void mergeLinesOfDumps(  long initDate, String aRRD, String bRRD, String cRRD)
			throws IOException, RrdException, FileNotFoundException {
		String crTmp = "rrdtool create " +
				""+cRRD+" --start "+(initDate)+"" + 
				" --step 1 " +
				"				DS:data:GAUGE:2121212240:U:U "+ 
				"				RRA:AVERAGE:0.5:3:480 " +
				"				RRA:AVERAGE:0.5:17:592 " +
				"				RRA:AVERAGE:0.5:131:340 " +
				"				RRA:AVERAGE:0.5:731:719 " + 
				"				RRA:AVERAGE:0.5:10000:273 " +  
				"				RRA:MAX:0.5:3:480 " +
				"				RRA:MAX:0.5:17:592 " +
				"				RRA:MAX:0.5:131:340 " +
				"				RRA:MAX:0.5:731:719 " +
				"				RRA:MAX:0.5:10000:7310 " +
				"				RRA:MIN:0.5:3:480 " +
				"				RRA:MIN:0.5:17:592 " +
				"				RRA:MIN:0.5:131:340 " +
				"				RRA:MIN:0.5:731:719 " +
				"				RRA:MIN:0.5:10000:7310 " + 
												 
				" "; 		
	
		RrdCommander.execute(crTmp  );
		String updatetxtTmp= " rrdtool update "+ cRRD+" "; //920804700:12345
		
		int toshft=1;
		RrdCommander.execute(updatetxtTmp +(initDate+toshft++) +":0"  );
		
		// replacement >> m.mergeRRD(a, b, c);
		// fetch all from a, b, then push it with update into c
		// rrdtool fetch  a.rrd AVERAGE -r 1 -s 920804700 > a.txt
		// rrdtool fetch  b.rrd AVERAGE -r 1 -s 920804700 > b.txt
		ParaReader aR = null;
		/**
		 * new DFReader(aIN); 
		DFReader bR = new DFReader(bIN,aR);
		 */
		for (String rate:  new String[]{ "MAX","MIN","AVERAGE"} ) {
						
					
					String command = "rrdtool fetch "+aRRD+" -r 1 -s  "+initDate+" "+rate; // 1hour  . see 3600 https://oss.oetiker.ch/rrdtool/doc/rrdfetch.en.html
					log.info(command); // command = "rrdtool info "+aRRD; RrdCommander.execute("rrdtool tune -h data:240 "+aRRD);RrdCommander.execute("rrdtool info "+aRRD);
					FetchData fA = (FetchData) RrdCommander.execute(command);
					File a2DEL = writeToSubFIle(aRRD+"."+rate+".A.txt",fA);
					
					String command2 = "rrdtool fetch "+bRRD+"   -r 1  -s  "+initDate+" "+rate;
					log.info(command2);
					FetchData fB = (FetchData) RrdCommander.execute(command2);
					File b2DEL = writeToSubFIle(bRRD+"."+rate+".B.txt", fB);
					
					//String aTxt[] = sA.split("\n");
					//String bTxt[] = sB.split("\n");
					
	
					BufferedReader aIN = new BufferedReader(new FileReader(a2DEL));
					aR = new   ParaReader(aIN,aR);
					BufferedReader bIN = new BufferedReader(new FileReader(b2DEL));
					aR = new   ParaReader(bIN,aR);
		}
			
		  
		String upTmp = updatetxtTmp  ;
		String oldTmp = "----------EMPTY---------------------" ;
		int i = 0;
		int j = 0;
	
		try { 
			long lastTIMESTAMP = 0; 
			for (String popSample = aR.readLine(); popSample != null; popSample = aR.readLine() ) {
				log.trace("O:{}",popSample);
				i++; 
				
				long newTIMESTAMP = Long.valueOf(popSample.split(":")[0].trim());
				if (lastTIMESTAMP <newTIMESTAMP) {
					lastTIMESTAMP =newTIMESTAMP;
					j=0;
				}else{
					j++;
				}
				if (newTIMESTAMP/10 ==  lastTIMESTAMP/10) { 
					popSample = popSample.replace(""+lastTIMESTAMP, ""+(lastTIMESTAMP+(j))); 
				} 
				log.trace("m:{}",popSample);
				upTmp +=  " "+popSample.replaceAll(" " , "");
				
				if (i%99 == 0) {
					// DO THE JOB 					upTmp +=  " " +aLINE[0].trim()+":"+ valueToPush; 
					Object o = RrdCommander.execute( upTmp );
					log.trace("::{}",o);
					oldTmp = upTmp ;
					upTmp = updatetxtTmp ;
				}
			}
		}catch (IOException e) {
			try { 
				Object o = RrdCommander.execute( upTmp );
				log.trace("{}",o);
			}catch (Exception e1) {
				log.error("Object o = RrdCommander.execute( "+upTmp+" );",e1);
				e1.printStackTrace();
			}
		}catch (Exception e) {
			log.error("oldTmp::::::::::{}{}{}::::::::",oldTmp);
			log.error("upTmp:::::{}{}{}:::::::::::::",upTmp);
			e.printStackTrace();
		}
	} 
 
}