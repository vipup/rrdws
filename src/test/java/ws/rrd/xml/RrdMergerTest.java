package ws.rrd.xml;

import static org.junit.Assert.*;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Date;
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

import eu.blky.cep.polo2rrd.SysoUpdater;
import eu.blky.springmvc.DFReader;
import eu.blky.springmvc.RestoreService;

//import eu.blky.net.mrtg.server.Config;
//import eu.blky.rrd.cmd.RrdCommander;
//import eu.blky.rrd.core.RrdException;
//import eu.blky.rrd.core.RrdFileBackend;

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
		Object b = RrdCommander.execute("rrdtool fetch testB.rrd AVERAGE --start 920804400 --end 920809200" );
		Object c = RrdCommander.execute("rrdtool fetch testC.rrd AVERAGE --start 920804400 --end 920809200" );
		(new Diff()).diff((""+a).split("\n"), readFully("t3.txt").split("\n"));
		(new Diff()).diff((""+b).split("\n"), readFully("t3.txt").split("\n"));
		(new Diff()).diff((""+c).split("\n"), readFully("t3.txt").split("\n"));
		(new Diff()).diff((""+a).split("\n"), (""+b).split("\n"));
		(new Diff()).diff((""+c).split("\n"), (""+b).split("\n"));
		(new Diff()).diff((""+c).split("\n"), (""+a).split("\n"));
		// TODO Assert.assertEquals(readFully("t4.txt") , (""+c)   );
		
//		rrdtool graph testA.png \
//		 --start 920804400 --end 920808000 \
//		 DEF:myspeed=testA.rrd:speed:AVERAGE \
//		 AREA:myspeed#FF0000
		Object aG = RrdCommander.execute("rrdtool graph testA.png  --start 920804400 --end 920808000 DEF:myspeed=testA.rrd:speed:AVERAGE AREA:myspeed#FF0000 " );
		Object bG = RrdCommander.execute("rrdtool graph testB.png  --start 920804400 --end 920808000 DEF:myspeed=testB.rrd:speed:AVERAGE AREA:myspeed#22FF00 " );
		Object cG = RrdCommander.execute("rrdtool graph testC.png  --start 920804400 --end 920808000 DEF:myspeed=testC.rrd:speed:AVERAGE AREA:myspeed#2222FF " );
		
//
//		rrdtool graph testB.png \
//		 --start 920804400 --end 920808000 \
//		 DEF:myspeed=testB.rrd:speed:AVERAGE \
//		 AREA:myspeed#22FF00
//
//		rrdtool graph testC.png \
//		 --start 920804400 --end 920808000 \
//		 DEF:myspeed=testC.rrd:speed:AVERAGE \
//		 AREA:myspeed#2222FF

		
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
		
	}
	
	
	@Test
	public void testCreateAndUpdateAndFetch() throws IOException, RrdException {
		testCreateA();
		testCreateAndUpdate();
		Object o =  RrdCommander.execute("rrdtool fetch test.rrd AVERAGE --start 920804400 --end 920809200" );
		(new Diff()).diff((""+o).split("\n"), readFully("t3.txt").split("\n"));
		//Assert.assertEquals( (""+o)  , readFully("t3.txt") );
		
	}
	
	@Test
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
 		//Assert.assertEquals(d[0], (""+o).length() , readFully("t0.txt").length());
 		
 		
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
		//RrdCommander.execute("rrdtool graph 20yearsdata.png --start  "+initDate+" --end "+ (secToLive+initDate) + " DEF:data=20yearsdata.rrd:data:AVERAGE AREA:data#FF0000 " );
		RrdCommander.execute("rrdtool graph  20yearsdata.gif --start "+initDate+"  --end "+ (secToLive+initDate) + " DEF:data=20yearsdata.rrd:data:AVERAGE AREA:data#FF0000 " );
		
		String updatetxtTmp= " rrdtool update 20yearsdata.rrd "; //920804700:12345
		long j = 0;
		long starttime = System.currentTimeMillis();
		long n = (secToLive - 111 ) / (secToLive/10000); // 1111111
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
			//Assert.assertEquals( o , "test.rrd");
		} 
		RrdCommander.execute("rrdtool graph  20yearsdata.gif --start "+920804700+"  --end "+ (secToLive+initDate) + " DEF:data=20yearsdata.rrd:data:AVERAGE AREA:data#FF0000 " ); 
		System.err.println("1111111");
		dump20year();
	}	
	
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
	
	@Test 
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
	
	@Test
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
			//Assert.assertEquals( o , "test.rrd");
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
	/** 
	 * 
	 * @throws IOException
	 * @throws RrdException
	 */
	public void postCreateAndUpdateSTDRRD_OwnMergeOverFetchToUpdate() throws IOException, RrdException {
		long initDate = 920804700; 
		
		long  secToLive = 20 * 365 * 24 * 60 *60;	//1 000 001 111    630 720 000	
		String aRRD = "X-553689133.rrd";
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
//		String crTmp = 		 "rrdtool create " +
//				""+cRRD+" --start "+(920804700)+"" + 
//				" --step 1 " +
//				"				DS:data:GAUGE:11111240:U:U " +
//				"				RRA:AVERAGE:0.5:1:592 " +
//				"				RRA:AVERAGE:0.5:15:80320 " + 
//				"				RRA:AVERAGE:0.5:60:43800 " + 
//				//"				RRA:AVERAGE:0.5:2:592 " +
//				//"				RRA:AVERAGE:0.5:10:340 " +
//				//"				RRA:AVERAGE:0.5:20:719 " +
//				"				RRA:AVERAGE:0.5:4800:13140 " + // 8 hour summ // 4 values per day X 1 year: 6 * 60 *  365
//				"				RRA:MAX:0.5:30:40320 " +
//				"				RRA:MAX:0.5:60:43800 " +
//				//"				RRA:MAX:0.5:17:592 " +
//				//"				RRA:MAX:0.5:131:340 " +
//				//"				RRA:MAX:0.5:731:719 " +
//				"				RRA:MAX:0.5:4800:13140 " +
//				"				RRA:MIN:0.5:30:40320 " +
//				"				RRA:MIN:0.5:60:43800 " +
//				//"				RRA:MIN:0.5:17:592 " +
//				//"				RRA:MIN:0.5:131:340 " +
//				//"				RRA:MIN:0.5:731:719 " +
//				"				RRA:MIN:0.5:4800:13140 " + // 10*8 hour summ // 4 values per day X 1 year: 6 * 60 *  365  
//												" "; 
		//System.setProperty( "RrdMemoryBackendFactory", "JO!");
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
		postCreateAndUpdateSTDRRD_OwnMergeOverFetchToUpdate("X-553689133.rrd");
		//assertEquals(tTmp.length(),testTmp.length()); // created and copied into 1st original
		// 2nd : merge NEW->B, C= A+B
		Path sourceB = new File(new File("").getAbsoluteFile() ,"/src/test/resources/mergeData/16.8GB/X-553689133.rrd").toPath();
		Files.copy(sourceB, target, StandardCopyOption.REPLACE_EXISTING);
		long start = System.currentTimeMillis();
		postCreateAndUpdateSTDRRD_OwnMergeOverFetchToUpdate("X-553689133.rrd");
		System.out.println("EXECTIME:"+(System.currentTimeMillis()-start)+" ms.");
		assertEquals(178356,testTmp.length()); 
		
		
	}
	
			/** 
			 * 
			 * @throws IOException
			 * @throws RrdException
			 */
			public boolean postCreateAndUpdateSTDRRD_OwnMergeOverFetchToUpdate(String aRRD /* = "X-553689133.rrd";*/) throws IOException, RrdException {
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
						"				RRA:MAX:0.5:10000:7731 " +
						"				RRA:MIN:0.5:3:480 " +
						"				RRA:MIN:0.5:17:592 " +
						"				RRA:MIN:0.5:131:340 " +
						"				RRA:MIN:0.5:731:719 " +
						"				RRA:MIN:0.5:10000:7731 " + 
														 
						" "; 		
	 
				RrdCommander.execute(crTmp  );
				String updatetxtTmp= " rrdtool update "+ cRRD+" "; //920804700:12345
				
				int toshft=1;
				RrdCommander.execute(updatetxtTmp +(initDate+toshft++) +":0"  );
				
				// replacement >> m.mergeRRD(a, b, c);
				// fetch all from a, b, then push it with update into c
				// rrdtool fetch  a.rrd AVERAGE -r 1 -s 920804700 > a.txt
				// rrdtool fetch  b.rrd AVERAGE -r 1 -s 920804700 > b.txt
				DFReader aR = null;
				/**
				 * new DFReader(aIN); 
				DFReader bR = new DFReader(bIN,aR);
				 */
				for (String rate:  new String[]{ "MAX","MIN","AVERAGE"} ) {
								
							
							String command = "rrdtool fetch "+aRRD+" -r 1 -s  "+initDate+" AVERAGE"; // 1hour  . see 3600 https://oss.oetiker.ch/rrdtool/doc/rrdfetch.en.html
							log.info(command); // command = "rrdtool info "+aRRD; RrdCommander.execute("rrdtool tune -h data:240 "+aRRD);RrdCommander.execute("rrdtool info "+aRRD);
							String sA = ""+RrdCommander.execute(command);
							File a2DEL = writeToSubFIle(a+"."+rate+".A.txt",sA);
							
							String command2 = "rrdtool fetch "+bRRD+"   -r 3600  -s  "+initDate+" AVERAGE";
							log.info(command2);
							String sB = ""+RrdCommander.execute(command2);
							File b2DEL = writeToSubFIle(b+"."+rate+".B.txt",sB);
							
							//String aTxt[] = sA.split("\n");
							//String bTxt[] = sB.split("\n");
							
							BufferedReader bIN = new BufferedReader(new FileReader(b2DEL));
							aR = new   DFReader(bIN,aR);
							BufferedReader aIN = new BufferedReader(new FileReader(a2DEL));
							aR = new   DFReader(aIN,aR);
				
				}
					
				  
				String upTmp = updatetxtTmp  ;
				String oldTmp = "----------EMPTY---------------------" ;
				int i = 0;
				int j = 0;

				try { 
					long lastTIMESTAMP = 0; 
					for (String popSample = aR.readNextChainedSample(); popSample != null; popSample = aR.readNextChainedSample() ) {
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
							log.debug("::{}",o);
							oldTmp = upTmp ;
							upTmp = updatetxtTmp ;
						}
					}
				}catch (IOException e) {
					try { 
						Object o = RrdCommander.execute( upTmp );
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
				RrdCommander.execute("rrdtool graph  "+cRRD+  ".gif --start "+initDate+"  --end "+ (secToLive+initDate) + " DEF:data="+cRRD+":data:AVERAGE AREA:data#FF55FF " );
				System.out.println("RESULT GIF:"+cmdGraphrrdTmp);
				if (cmdGraphrrdTmp.indexOf("graph")>=0)			RrdCommander.execute(cmdGraphrrdTmp);
				
		
				
		
				
				String toXMPcmd = "rrdtool dump  " + cRRD+" ";
				String Cxml = (String) RrdCommander.execute(toXMPcmd); 
				File pathToExported = writeToSubFIle(c+".XML",Cxml);
				String resultTmp= aRRD.replace(".rrd",".AB_merged."+System.currentTimeMillis()+".rrd"); // cRRD = aRRD.replace(".rrd","TMP.rrd");
				String cmdREST = "rrdtool restore "+pathToExported.getCanonicalPath()+"   "+ resultTmp;
		
		
				String retval = (String) RrdCommander.execute(cmdREST);
				log.info("AFTER RESTORE::{}:::",retval );
				
//				bIN.close();
//				aIN.close();
				//pathToExported.delete();
				//b2DEL.delete();
				//a2DEL.delete();
				
				String cToGraph = cmdGraphrrdTmp.replace(cRRD, resultTmp);
				log.info("TO EXEC::",cToGraph);
				if (cToGraph.indexOf("X-553689133")>=0)				RrdCommander.execute(cToGraph); 
				
				
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
	private File writeToSubFIle(String path, String data) throws IOException {
		File fileA = new File( path);
		fileA.deleteOnExit();
		FileWriter fwA = new FileWriter(fileA);
		fwA.write(data);
		fwA.flush();
		fwA.close();
		return fileA;
	}

}


 