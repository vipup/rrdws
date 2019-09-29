package ws.rrd.xml;

import static org.junit.Assert.*;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.CopyOption;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption; 
import java.util.Properties;

import org.jrobin.cmd.RrdCommander;
import org.jrobin.core.RrdDbPool;
import org.jrobin.core.RrdException;
import org.jrobin.core.RrdFileBackend;
import org.junit.Assert;
import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runners.MethodSorters;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
 
import eu.blky.logparser.ParaReader;
import eu.blky.springmvc.RestoreService;
 
/** 
 * <b>Description:TODO</b>
 * @author      vipup<br>
 * <br>
 * <b>Copyright:</b>     Copyright (c) 2006-2008 Monster AG <br>
 * <b>Company:</b>       Monster AG  <br>
 * 
 * Creation:  16.11.2011::14:50:23<br> 
 */
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class RrdMergerTest {
	private static Logger log = LoggerFactory.getLogger(RestoreService.class);
	@Before
	public void setup() {
		System.out.println("nothing to setup yet..");
	}
	@org.junit.After
	public void cleanup() {
		new File(RrdFileBackend.CALC_DEFAULT_WORKDIR()  +"/"+RrdFileBackend.RRD_HOME+ "/test.rrd") .delete();
		new File(RrdFileBackend.CALC_DEFAULT_WORKDIR()  +"/"+RrdFileBackend.RRD_HOME+ "/testA.rrd") .delete();
		new File(RrdFileBackend.CALC_DEFAULT_WORKDIR()  +"/"+RrdFileBackend.RRD_HOME+ "/testB.rrd") .delete();
		new File(RrdFileBackend.CALC_DEFAULT_WORKDIR()  +"/"+RrdFileBackend.RRD_HOME+ "/testC.rrd") .delete();
	}
	
	@SuppressWarnings("deprecation")
	@Test
	public void testCreateAandBThenMerge() throws IOException, RrdException {
		createA() ; // create before!! 
		createB();
		String updatetxtTmp= " rrdtool update testA.rrd 920804700:12345 920805000:12357 920805300:12363\r\n" + 
				" rrdtool update testB.rrd 920805600:12363 920805900:12363 920806200:12373\r\n" + 
				" rrdtool update testA.rrd 920806500:12383 920806800:12393 920807100:12399\r\n" + 
				" rrdtool update testB.rrd 920806500:12383 920806800:12393 920807100:12399\r\n" + 
				" rrdtool update testB.rrd 920807400:12405 920807700:12411 920808000:12415\r\n" + 
				" rrdtool update testB.rrd 920808300:12420 920808600:12422 920808900:12423";
		String[] updates=  updatetxtTmp.split("\n");
		for (String update:updates) RrdCommander.execute( update ); 
 
 		RrdMerger m = new RrdMerger();
 		String opath = RrdFileBackend.CALC_DEFAULT_WORKDIR() +"/"+RrdFileBackend.RRD_HOME+"/"+"testA.rrd";
		String npath = RrdFileBackend.CALC_DEFAULT_WORKDIR() +"/"+RrdFileBackend.RRD_HOME+"/"+"testB.rrd";
		String mpath = RrdFileBackend.CALC_DEFAULT_WORKDIR() +"/"+RrdFileBackend.RRD_HOME+"/"+"testC.rrd";
		m.mergeRRD(opath, npath, mpath);
		
		// C = A + B
		Object a = RrdCommander.execute("rrdtool fetch testA.rrd AVERAGE --start 920804400 --end 920809200" );
		log.trace("{}",a);
		Object b = RrdCommander.execute("rrdtool fetch testB.rrd AVERAGE --start 920804400 --end 920809200" );
		log.trace("{}",b);
		Object c = RrdCommander.execute("rrdtool fetch testC.rrd AVERAGE --start 920804400 --end 920809200" );
		log.trace("{}",c);
 
		assertEquals( readFully("t4.txt"), (""+c)   ); // 
		
 
		Object aG = RrdCommander.execute("rrdtool graph testA.gif  --start 920804400 --end 920808000 DEF:myspeed=testA.rrd:speed:AVERAGE AREA:myspeed#FF0000 " );
		log.trace("{}",aG);
		Object bG = RrdCommander.execute("rrdtool graph testB.gif  --start 920804400 --end 920808000 DEF:myspeed=testB.rrd:speed:AVERAGE AREA:myspeed#22FF00 " );
		log.trace("{}",bG);
		Object cG = RrdCommander.execute("rrdtool graph testC.gif  --start 920804400 --end 920808000 DEF:myspeed=testC.rrd:speed:AVERAGE AREA:myspeed#2222FF " );
		log.trace("{}",cG);
 
		
	}	
	
	
	private void createA() throws IOException, RrdException {
		String createA = "rrdtool create testA.rrd \\"
				+ " --start 920804400 \\"
				+ " DS:speed:COUNTER:600:U:U \\ "
				+ "  RRA:AVERAGE:0.5:1:24 \\" 
				+ "  RRA:AVERAGE:0.5:6:101"
				+ " "
				+ ""
				+ "";
 		Object o = RrdCommander.execute(createA.replace("\\", "\n ") );
 		log.trace("{}",o);
		
	}
	private void createB() throws IOException, RrdException {
		String createA = "rrdtool create testB.rrd \\"
				+ " --start 920804400 \\"
				+ " DS:speed:COUNTER:600:U:U \\ "
				+ "  RRA:AVERAGE:0.5:1:24 \\" 
				+ "  RRA:AVERAGE:0.5:6:10"
				+ " "
				+ ""
				+ "";
 		Object o = RrdCommander.execute(createA.replace("\\", "\n ") );
 		log.trace("{}",o);
	}
	
	
	 
	public void testCreateAndUpdateAndFetch() throws IOException, RrdException {
		testCreateA();
		testCreateAndUpdate();
		Object o =  RrdCommander.execute("rrdtool fetch test.rrd AVERAGE --start 920804400 --end 920809200" );
		(new Diff()).diff((""+o).split("\n"), readFully("t3.txt").split("\n"));  
	}
	
	 
	public void testCreateA() throws IOException, RrdException {
		String createA = "rrdtool create test.rrd \\"
				+ " --start 920804400 \\"
				+ " DS:speed:COUNTER:600:U:U \\ "
				+ "  RRA:AVERAGE:0.5:1:24 \\" 
				+ "  RRA:AVERAGE:0.5:6:10"
				+ " "
				+ ""
				+ "";
 		Object o = RrdCommander.execute(createA.replace("\\", "\n ") );
 		Assert.assertEquals( o , "test.rrd");
 		
 		o =  RrdCommander.execute("rrdtool fetch test.rrd AVERAGE --start 920804400 --end 920809200" );
 		String[] d = (new Diff()).diff((""+o).replace("  ", " ").split("\n"), readFully("t1.txt").replace("  ", " ").split("\n"));
 		log.trace("{}",""+d);

 		
 		
	}
	
	
	@Test
	public void testCreateAndUpdate20years() throws IOException, RrdException {
		// testCreateA() ; // create before!!
		// create 20 year rrd
		long initDate = 920804700;
		String crTmp = " rrdtool create 20yearsdata.rrd  --start  "+initDate+" --step 1 \n"
		+ "		DS:data:GAUGE:1111111:U:U \n"
		+ "		RRA:AVERAGE:0.5:60:10080 \n"
		+ "		RRA:AVERAGE:0.5:300:9504 \n"
		+ "		RRA:AVERAGE:0.5:600:15984 \n"
		+ "		RRA:AVERAGE:0.5:900:35040 \n"
		+ " 	RRA:AVERAGE:0.5:1800:350400 \n"
		+ "		RRA:MAX:0.5:60:10080 \n"
		+ "		RRA:MAX:0.5:300:9504 \n"
		+ "		RRA:MAX:0.5:600:15984 \n"
		+ "		RRA:MAX:0.5:900:35040 \n"
		+ "		RRA:MAX:0.5:1800:350400 \n"
		+ "		RRA:MIN:0.5:60:10080 \n"
		+ "		RRA:MIN:0.5:300:9504 \n"
		+ "		RRA:MIN:0.5:600:15984 \n"
		+ "		RRA:MIN:0.5:900:35040 \n"
		+ "		RRA:MIN:0.5:1800:350400 \n";
	
		long  secToLive = 20 * 365 * 24 * 60 *60;	//1 000 001 111    630 720 000	
		RrdCommander.execute(crTmp  );
		RrdCommander.execute(crTmp.replace("20yearsdata.rrd ", "ODDyearsdata.rrd ")  );
		RrdCommander.execute(crTmp.replace("20yearsdata.rrd ", "EVENyearsdata.rrd ")  );
 		RrdCommander.execute("rrdtool graph  20yearsdata.gif --start "+initDate+"  --end "+ (secToLive+initDate) + " DEF:data=20yearsdata.rrd:data:AVERAGE AREA:data#FF0000 " );
		
		String updatetxtTmp= " rrdtool update 20yearsdata.rrd ";  
		long j = 0;
		long starttime = System.currentTimeMillis();
		long n = (secToLive - 111 ) / (secToLive/10000);  
		for (int i=111;i<secToLive;i+=secToLive/10000 ) {
			
			if (j++%100 == 0) {
				System.out.println("done:"+j+"-/-"+n+ " update/per/sec:"+(System.currentTimeMillis()-starttime));
				String grafCmpTMP = "rrdtool graph  20yearsdata"+j+".gif -w 640 -h 480  --start "+initDate+"  --end "+ (secToLive+initDate) + " DEF:data=20yearsdata.rrd:data:AVERAGE AREA:data#FF0000 " ;
				RrdCommander.execute(grafCmpTMP);
				RrdCommander.execute(grafCmpTMP.replace("20yearsdata", "ODDyearsdata" ).replace("FF0000", "00FF00") );
				RrdCommander.execute(grafCmpTMP.replace("20yearsdata", "EVENyearsdata" ).replace("FF0000", "0000FF") );
			}
			String upTmp = updatetxtTmp +" " + (initDate+i)+":"+(11100*Math.sin( j/365)+100.0*Math.sin( j/7)+1100.0*Math.sin( j/30) +j);
			try {
				Object o = RrdCommander.execute( upTmp );
				log.trace("{}",o);
				if ( j/365%2 == 0) {
					RrdCommander.execute( upTmp.replace("20yearsdata.rrd ", "ODDyearsdata.rrd ")  );
				}else {
					RrdCommander.execute( upTmp.replace("20yearsdata.rrd ", "EVENyearsdata.rrd ") );
				}
				
			}catch(Exception e) {
				System.out.println("iiiiiiiii:"+i);
				e.printStackTrace();
				break;
			} 
		} 
		RrdCommander.execute("rrdtool graph  20yearsdata.gif --start "+920804700+"  --end "+ (secToLive+initDate) + " DEF:data=20yearsdata.rrd:data:AVERAGE AREA:data#FF0000 " ); 
		System.err.println("1111111");
		dump20year();
	}	
	
	@SuppressWarnings("deprecation")
	@Test
	@Ignore
	/**
	 * @deprecated
	 * 
	 * @throws IOException
	 * @throws RrdException
	 */
	public void postCreateAndUpdate20years() throws IOException, RrdException {
		long initDate = 920804700;
		long  secToLive = 20 * 365 * 24 * 60 *60;	//1 000 001 111    630 720 000	
		
 		RrdMerger m = new RrdMerger();
 		String a = RrdFileBackend.CALC_DEFAULT_WORKDIR() +"/"+RrdFileBackend.RRD_HOME+"/"+"ODDyearsdata.rrd";
		String b = RrdFileBackend.CALC_DEFAULT_WORKDIR() +"/"+RrdFileBackend.RRD_HOME+"/"+"EVENyearsdata.rrd";
		String c = RrdFileBackend.CALC_DEFAULT_WORKDIR() +"/"+RrdFileBackend.RRD_HOME+"/"+"ODD_plus_EVENyearsdata.rrd";
		
		m.mergeRRD(a, b, c);
		RrdCommander.execute("rrdtool graph  ODD_plus_EVENyearsdata.gif --start "+920804700+"  --end "+ (secToLive+initDate) + " DEF:data=ODD_plus_EVENyearsdata.rrd:data:AVERAGE AREA:data#6666ff " );
	}
	
	 
	/** 
	 * 
	 * @throws IOException
	 * @throws RrdException
	 */
	public void dump20year() throws IOException, RrdException {
		long initDate = 920804700; 
		 
		// rrdtool fetch  20yearsdata.rrd AVERAGE -r 1 -s 920804700 > 20yearsdata.txt
		String data = ""+ RrdCommander.execute("rrdtool fetch 20yearsdata.rrd AVERAGE -r 1 -s   "+initDate );
		writeToTextSubFIle(RrdFileBackend.CALC_DEFAULT_WORKDIR() +"/"+RrdFileBackend.RRD_HOME+"/20yearsdata", data);
	}
	
	@Test 
	/** 
	 * 
	 * @throws IOException
	 * @throws RrdException
	 */
	public void postCreateAndUpdate20yearsWithOwnMergeOverFetchToUpdate() throws IOException, RrdException {
		long initDate = 920804700; 
		
		long  secToLive = 20 * 365 * 24 * 60 *60;	//1 000 001 111    630 720 000	
 		String aRRD = "ODDyearsdata.rrd";
		String a = RrdFileBackend.CALC_DEFAULT_WORKDIR() +"/"+RrdFileBackend.RRD_HOME+"/"+aRRD;
		String bRRD = "EVENyearsdata.rrd";
		String b = RrdFileBackend.CALC_DEFAULT_WORKDIR() +"/"+RrdFileBackend.RRD_HOME+"/"+bRRD;
		String cRRD = "!ODD_merge_EVENyearsdata";
		String c = RrdFileBackend.CALC_DEFAULT_WORKDIR() +"/"+RrdFileBackend.RRD_HOME+"/"+cRRD;
		String crTmp = " rrdtool create  "+cRRD+".rrd  --start  "+initDate+" --step 1 \n"
		+ "		DS:data:GAUGE:1110000:U:U \n"
		+ "		RRA:AVERAGE:0.5:60:10080 \n"
		+ "		RRA:AVERAGE:0.5:300:9504 \n"
		+ "		RRA:AVERAGE:0.5:600:15984 \n"
		+ "		RRA:AVERAGE:0.5:900:35040 \n"
		+ " 	RRA:AVERAGE:0.5:1800:350400 \n"
		+ "		RRA:MAX:0.5:60:10080 \n"
		+ "		RRA:MAX:0.5:300:9504 \n"
		+ "		RRA:MAX:0.5:600:15984 \n"
		+ "		RRA:MAX:0.5:900:35040 \n"
		+ "		RRA:MAX:0.5:1800:350400 \n"
		+ "		RRA:MIN:0.5:60:10080 \n"
		+ "		RRA:MIN:0.5:300:9504 \n"
		+ "		RRA:MIN:0.5:600:15984 \n"
		+ "		RRA:MIN:0.5:900:35040 \n"
		+ "		RRA:MIN:0.5:1800:350400 \n";
		//System.setProperty( "RrdMemoryBackendFactory", "JO!");
		RrdCommander.execute(crTmp  );
		String updatetxtTmp= " rrdtool update  "+cRRD+".rrd "; //920804700:12345
		
		RrdCommander.execute(updatetxtTmp +(initDate+1) +":0"  );
		
		// replacement >> m.mergeRRD(a, b, c);
		// fetch all from a, b, then push it with update into c
		// rrdtool fetch  a.rrd AVERAGE -r 1 -s 920804700 > a.txt
		// rrdtool fetch  b.rrd AVERAGE -r 1 -s 920804700 > b.txt
		String sA = ""+RrdCommander.execute("rrdtool fetch "+aRRD+" -r 1 -s  "+initDate+" AVERAGE");
		writeToTextSubFIle(a,sA);
		String aTxt[] = sA.split("\n");
		String sB = ""+RrdCommander.execute("rrdtool fetch "+bRRD+" -r 1 -s  "+initDate+" AVERAGE");
		writeToTextSubFIle(b,sB);
		String bTxt[] = sB.split("\n");
		
		
		 
		long starttime = System.currentTimeMillis();
		int toSkip = 0;
		String upTmp = updatetxtTmp  ;
		String oldTmp = "----------EMPTY---------------------" ;
		for (int i=0;i<aTxt.length;i+=500) {
			try {

				String[] aLINE = aTxt[i].split(":"); 
				toSkip =  Integer.valueOf( aTxt[i].split(":")[0].trim() ) - Integer.valueOf( bTxt[i].split(":")[0].trim()  ); 
				if (toSkip >0) {
					System.out.println("TOSKIP."+toSkip);
				}
				Object valueToPush = aLINE[1].trim().equals("nan")?bTxt[i+toSkip].split(":")[1].trim():aLINE[1].trim();
				
				if ("nan". equals(valueToPush)) continue;
				
			// DO THE JOB
				upTmp +=  " " +aLINE[0].trim()+":"+ valueToPush;
				if (i%1 == 0) {
					Object o = RrdCommander.execute( upTmp );
					log.trace("{}",o);
					oldTmp = upTmp ;
					upTmp = updatetxtTmp ;
				}
				if (i%1000 == 0) {
					System.out.println("merge done:"+i+"-/-"+aTxt.length+ " update/per/sec:"+(System.currentTimeMillis()-starttime));
					String grafCmpTMP = "rrdtool graph  "+cRRD+""+(i/1000%5)+".gif -w 640 -h 480  --start "+initDate+"  --end "+ (secToLive+initDate) + " DEF:data="+cRRD+".rrd:data:AVERAGE AREA:data#FF55FF " ;
					RrdCommander.execute(grafCmpTMP);
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
		
		
		
		RrdCommander.execute("rrdtool graph  "+cRRD+  ".gif --start "+initDate+"  --end "+ (secToLive+initDate) + " DEF:data="+cRRD+".rrd:data:AVERAGE AREA:data#FF55FF " );
		String toXMPcmd = "rrdtool dump  " + cRRD+".rrd";
		String Cxml = (String) RrdCommander.execute(toXMPcmd); 
		String pathToExported = writeToTextSubFIle(c,Cxml);
		String cmdREST = "rrdtool restore "+pathToExported+"  AplusB_merged"+System.currentTimeMillis()+".rrd ";
		String retval = (String) RrdCommander.execute(cmdREST);
		log.trace("{}",retval);
		//System.out.println(retval);
		
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
	 
	public void testCreateAndUpdate() throws IOException, RrdException {
		testCreateA() ; // create before!! 
		String updatetxtTmp= " rrdtool update test.rrd 920804700:12345 920805000:12357 920805300:12363\r\n" + 
				" rrdtool update test.rrd 920805600:12363 920805900:12363 920806200:12373\r\n" + 
				" rrdtool update test.rrd 920806500:12383 920806800:12393 920807100:12399\r\n" + 
				" rrdtool update test.rrd 920807400:12405 920807700:12411 920808000:12415\r\n" + 
				" rrdtool update test.rrd 920808300:12420 920808600:12422 920808900:12423";
		String[] updates=  updatetxtTmp.split("\n");
		for (String update:updates) {
			Object o = RrdCommander.execute( update );
			log.trace("{}",o);
		} 
 		
 		Object o1 = RrdCommander.execute("rrdtool fetch test.rrd AVERAGE --start 920804400 --end 920809200" );
 		(new Diff()).diff((""+o1).split("\n"), readFully("t2.txt").split("\n"));
 		//Assert.assertEquals( (""+o1).length() , readFully("t2.txt").length());
 		
	}	
	
	
	private String readFully(String resourceName) throws IOException {
		java.io.InputStream fileInputStream = this.getClass().getClassLoader().getResourceAsStream(this.getClass().getPackage().getName().replaceAll("\\.", "/")+"/"+resourceName);
		BufferedReader rdTmp = new BufferedReader( new InputStreamReader (fileInputStream));
		StringBuffer retval = new StringBuffer();
		for (String lnTmp=rdTmp.readLine();lnTmp!=null;lnTmp=rdTmp.readLine()){
			retval .append( lnTmp);
			retval .append(  "\n");
		}
		return retval.toString();
		
	}
	@Test 
	public void testBench553689133() throws IOException, RrdException {
		String RRD_WORK_DIRECTORY = RrdFileBackend.CALC_DEFAULT_WORKDIR() +"/"+RrdFileBackend.RRD_HOME+"/";
		Path source = new File(new File("").getAbsoluteFile() ,"/src/test/resources/mergeData/29GB/X-553689133.rrd").toPath();
		File tTmp = new File(RRD_WORK_DIRECTORY, "X-553689133NEW.rrd");
		File testTmp = new File(RRD_WORK_DIRECTORY, "X-553689133.rrd");
		Path target =  tTmp.toPath();
		tTmp.mkdirs();
		// 1st copy  NEW -> A
		Files.copy(source, target, StandardCopyOption.REPLACE_EXISTING);
		RestoreService.postCreateAndUpdateSTDRRD_OwnMergeOverFetchToUpdate("X-553689133.rrd");
		//assertEquals(tTmp.length(),testTmp.length()); // created and copied into 1st original
		// 2nd : merge NEW->B, C= A+B
		Path sourceB = new File(new File("").getAbsoluteFile() ,"/src/test/resources/mergeData/16.8GB/X-553689133.rrd").toPath();
		Files.copy(sourceB, target, StandardCopyOption.REPLACE_EXISTING);
		long start = System.currentTimeMillis();
		RestoreService.postCreateAndUpdateSTDRRD_OwnMergeOverFetchToUpdate("X-553689133.rrd");
		System.out.println("EXECTIME:"+(System.currentTimeMillis()-start)+" ms.");
		assertEquals(178356,testTmp.length()); 
		
		
	}
	@Test 
	public void testBench553689133x3() throws IOException, RrdException {
		String RRD_WORK_DIRECTORY = RrdFileBackend.CALC_DEFAULT_WORKDIR() +"/"+RrdFileBackend.RRD_HOME+"/";
		
		String originalRoundRobinDatabaseName = "X-553689133.rrd";
		File originRRD = new File(RRD_WORK_DIRECTORY, originalRoundRobinDatabaseName);
		
		File externalNewRrdTmp = new File(RRD_WORK_DIRECTORY);
		externalNewRrdTmp.mkdirs();
		Path THE_NEW_PATH_TO_FILE_SHOULD_BE_REMOVED_AFTER_ALL =  null;
		 
		// 1st copy  NEW -> A
		Path sourceA = new File(new File("").getAbsoluteFile() ,"/src/test/resources/mergeData/29GB/X-553689133.rrd").toPath();
		THE_NEW_PATH_TO_FILE_SHOULD_BE_REMOVED_AFTER_ALL =  new File(RRD_WORK_DIRECTORY, originalRoundRobinDatabaseName.replace(".rrd", "."+System.currentTimeMillis()+".B.rrd")).toPath();
		Files.copy(sourceA, THE_NEW_PATH_TO_FILE_SHOULD_BE_REMOVED_AFTER_ALL);
		// TODO --->>doGraph(sourceA);
		doGraph(THE_NEW_PATH_TO_FILE_SHOULD_BE_REMOVED_AFTER_ALL);
		RestoreService.mergeAndB(originRRD, THE_NEW_PATH_TO_FILE_SHOULD_BE_REMOVED_AFTER_ALL.toFile());
		
		//assertEquals(tTmp.length(),testTmp.length()); // created and copied into 1st original		
		// 2nd : merge NEW->B, C= A+B
		Path sourceB = new File(new File("").getAbsoluteFile() ,"/src/test/resources/mergeData/16.8GB/X-553689133.rrd").toPath();
		THE_NEW_PATH_TO_FILE_SHOULD_BE_REMOVED_AFTER_ALL =  new File(RRD_WORK_DIRECTORY, originalRoundRobinDatabaseName.replace(".rrd", "."+System.currentTimeMillis()+".B.rrd")).toPath();
		Files.copy(sourceB, THE_NEW_PATH_TO_FILE_SHOULD_BE_REMOVED_AFTER_ALL);
		// TODO --->>		doGraph(sourceB);
		doGraph(THE_NEW_PATH_TO_FILE_SHOULD_BE_REMOVED_AFTER_ALL);
		
		long start1 = System.currentTimeMillis();
		RestoreService.mergeAndB(originRRD, THE_NEW_PATH_TO_FILE_SHOULD_BE_REMOVED_AFTER_ALL.toFile());
		System.out.println("EXECTIME#1:"+(System.currentTimeMillis()-start1)+" ms.");
		// 3rd : merge NEW->B, C= A+B
		Path sourceC = new File(new File("").getAbsoluteFile() ,"/src/test/resources/mergeData/16GB/X-553689133.rrd").toPath();
		THE_NEW_PATH_TO_FILE_SHOULD_BE_REMOVED_AFTER_ALL =  new File(RRD_WORK_DIRECTORY, originalRoundRobinDatabaseName.replace(".rrd", "."+System.currentTimeMillis()+".D.rrd")).toPath();
		Files.copy(sourceC, THE_NEW_PATH_TO_FILE_SHOULD_BE_REMOVED_AFTER_ALL);
		// TODO --->>		doGraph(sourceC);
		doGraph(THE_NEW_PATH_TO_FILE_SHOULD_BE_REMOVED_AFTER_ALL);
		
		long start2 = System.currentTimeMillis();
		File result2 = RestoreService.mergeAndB(originRRD, THE_NEW_PATH_TO_FILE_SHOULD_BE_REMOVED_AFTER_ALL.toFile());
		// TODO --->>		doGraph(originRRD.toPath());
		doGraph(result2.toPath());
		File bakRRD = new File(RRD_WORK_DIRECTORY, originalRoundRobinDatabaseName.replace(".rrd", "."+System.currentTimeMillis()+".BAK.rrd"));
		//originRRD.renameTo(bakRRD );
		//CopyOptions "REPLACE_EXISTING" and "ATOMIC_MOVE".
		 
		Files.move(originRRD.toPath(), bakRRD.toPath(), StandardCopyOption.ATOMIC_MOVE ,StandardCopyOption.REPLACE_EXISTING );
		Files.move(result2.toPath(), originRRD.toPath(), StandardCopyOption.ATOMIC_MOVE ,StandardCopyOption.REPLACE_EXISTING );
		
		doGraph(bakRRD.toPath());
		doGraph(originRRD.toPath());
		
		System.out.println("EXECTIME#2:"+(System.currentTimeMillis()-start2)+" ms.");
		assertEquals(78684,originRRD.length()); 

		
	}
	private static String calcToGraphCmp(String absolutePath) {
		String cmdGraphrrdTmp = "rrdtool graph  "+"RRDNAMETOREPLACE"+  ".gif  ";
		String _v=  "- ";
		String _h =  " 480 ";
		String _w = " 640 ";
		String _start = "end-4month";
		String _end = "now";
		String _t = "  - "; 
		cmdGraphrrdTmp +="-v '"+_v+"' -t '"+_t+"'  -h "+ _h +" -w  ";
		cmdGraphrrdTmp += _w+" --start="+_start+"   --end="+_end;
		cmdGraphrrdTmp += " DEF:dbdata="+"RRDNAMETOREPLACE"+":data:AVERAGE  ";
		cmdGraphrrdTmp += " DEF:min1="+"RRDNAMETOREPLACE"+":data:MIN  ";
		cmdGraphrrdTmp += " DEF:max1="+"RRDNAMETOREPLACE"+":data:MAX  ";
		cmdGraphrrdTmp += " LINE1:min1#EE444499  ";
		cmdGraphrrdTmp += " LINE1:max1#4444EE99 ";
		cmdGraphrrdTmp += " LINE2:dbdata#44EE4499  LINE1:dbdata#003300AA ";
		cmdGraphrrdTmp += "";
		
		
		return cmdGraphrrdTmp.replace("RRDNAMETOREPLACE",absolutePath); 
	}	
	private void doGraph(Path source2RRD) throws IOException, RrdException {
		String grafCmpTMP = calcToGraphCmp(source2RRD.toString());
		RrdCommander.execute(grafCmpTMP ); 
	}
	
	

}


 