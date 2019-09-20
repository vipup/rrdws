package ws.rrd.xml;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Date;

import org.jrobin.cmd.RrdCommander;
import org.jrobin.core.RrdException;
import org.jrobin.core.RrdFileBackend;
import org.junit.Assert;
import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import eu.blky.cep.polo2rrd.SysoUpdater;

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
		// fileA.deleteOnExit();
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

}


 