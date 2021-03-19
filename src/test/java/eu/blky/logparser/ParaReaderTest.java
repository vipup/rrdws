package eu.blky.logparser;

import static org.junit.Assert.*;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;

import org.junit.Ignore;
import org.junit.Test;

public class ParaReaderTest {

	@Test
	@Ignore
	// ((((((((((((( this is VEEEEEEEEEEEEEEEEERY CPU intensive test )))))))))))))))
	public void testGetNextTimestampWithLoop() throws IOException {
		String aTXT = 
				""
						+ "                         data\n"
						+ "                         data\n"
						+ "             data\n"
						+ "\n"
				+ "\n"
				+ " 920910000:               nan\n" + 
				" 1020920000:               nan\n" + 
				" 1121080000:  +8.4575808991E02\n" +  
				" 1269020000:  +7.4575808991E02\n" + 
				" 1369030000:  +6.4773875566E02\n" + 
				" 1469040000:  +5.4903809616E02\n" + 
				" 1569050000:               nan\n" + 
				" 1569060000:               nan\n" + 
				" 1569070000:               nan\n" + 
				" 1569050000:  +4.4776999332E02\n" +  
				" 1569080000:  +3.4612173360E02\n" + 
				" 1569090000:  +2.4625488692E02\n" + 
				" 1569100000:  +1.4598222442E02\n" + 
				"";
		String bTXT = 
				" 920910000:   +1.8773875566E02\n" + 
				" 1020920000:                nan\n" + 
				" 1121080000:               nan\n" +  
				" 1269020000:  +2.8575808991E02\n" + 
				" 1369030000:  +3.8773875566E02\n" + 
				" 1469040000:  +4.8903809616E02\n" + 
				" 1569050000:               nan\n" + 
				" 1569060000:               nan\n" + 
				" 1569070000:               nan\n" + 
				" 1569050000:  +5.8776999332E02\n" +  
				" 1569080000:               nan\n" +  
				" 1569090000:  +6.8625488692E02\n" + 
				" 1569100000:  +7.8598222442E02\n" + 
				"";
		ParaReader a = new ParaReader(new BufferedReader(new InputStreamReader( new ByteArrayInputStream(aTXT.getBytes()))));
		ParaReader b = new ParaReader(new BufferedReader(new InputStreamReader( new ByteArrayInputStream(bTXT.getBytes()))));
		
		String expectedC = " 920910000:   +1.8773875566E02\n" + 
				" 1121080000:  +8.4575808991E02\n" + 
				" 1269020000:  +2.8575808991E02\n" + 
				" 1269020000:  +7.4575808991E02\n" + 
				" 1369030000:  +3.8773875566E02\n" + 
				" 1369030000:  +6.4773875566E02\n" + 
				" 1469040000:  +4.8903809616E02\n" + 
				" 1469040000:  +5.4903809616E02\n" + 
				" 1569050000:  +5.8776999332E02\n" + 
				" 1569050000:  +4.4776999332E02\n" + 
				" 1569090000:  +6.8625488692E02\n" + 
				" 1569080000:  +3.4612173360E02\n" + 
				"";
		String c = "";
		try {
			for (long aT=a.getNextTimestamp();aT>0;aT=a.getNextTimestamp()) {
				for (long bT=b.getNextTimestamp();bT>0;aT=b.getNextTimestamp()) {
					if (bT>aT) {
						String popSample = a.popSample();
						System.out.println(""+a.getNextTimestamp()+"::"+popSample);
						c += popSample;
						c += "\n";
						continue;
					}else {
						String popSample = b.popSample();
						System.out.println(""+b.getNextTimestamp()+"::"+popSample);
						c += popSample;
						c += "\n";
						break;
					}
				}
			}
		}catch(Exception e) {e.printStackTrace();}
		assertEquals(expectedC, c);
			
		 
	}

	@Test
	public void testGetNextTimestamp() throws IOException {
		String aTXT = 
				" 920910000:               nan\n" + 
				" 1020920000:               nan\n" + 
				" 1121080000:  +8.4575808991E02\n" +  
				" 1269020000:  +7.4575808991E02\n" + 
				" 1369030000:  +6.4111111111E01\n" + 
				" 1469040000:  +5.4903809616E02\n" + 
				" 1569050000:               nan\n" + 
				" 1569060000:               nan\n" + 
				" 1569070000:               nan\n" + 
				" 1569050000:  +4.4776999332E02\n" +  
				" 1569080000:  +3.4612173360E02\n" + 
				" 1569090000:  +2.4625488692E02\n" + 
				" 1569100000:  +1.111E02\n" + 
				" 1569100001:  +1.22222E02\n" + 
				" 1569100002:  +1.333333E02\n" + 
				" 1569100003:  +1.4444444E02\n" + 
				" 1569100004:  +1.555555555E02\n" + 
				"";
		String bTXT = 
				" 820910000:   +0.8773875566E02\n" + 
				" 920910000:   +1.8773875566E02\n" + 
				" 1020920000:                nan\n" + 
				" 1121080000:               nan\n" +  
				" 1269020000:  +2.8575808991E02\n" + 
				" 1369030000:  +3.8111111111E01\n" + 
				" 1469040000:  +4.8903809616E02\n" + 
				" 1569050000:               nan\n" + 
				" 1569060000:               nan\n" + 
				" 1569070000:               nan\n" + 
				" 1569050000:  +5.8776999332E02\n" +  
				" 1569080000:               nan\n" +  
				" 1569090000:  +6.8625488692E02\n" + 
				" 1569100000:  +7.6666666666E02\n" + 
				"";
		ParaReader a = new ParaReader(new BufferedReader(new InputStreamReader( new ByteArrayInputStream(aTXT.getBytes()))));
		ParaReader b = new ParaReader(new BufferedReader(new InputStreamReader( new ByteArrayInputStream(bTXT.getBytes()))),a);
		
		String expectedC = 
				" 820910000:   +0.8773875566E02\n" + 
				" 920910000:   +1.8773875566E02\n" + 
				" 1121080000:  +8.4575808991E02\n" + 
				" 1269020000:  +2.8575808991E02\n" + 
				" 1269020001:  +7.4575808991E02\n" + 
				" 1369030000:  +3.8111111111E01\n" + 
				" 1369030001:  +6.4111111111E01\n" + 
				" 1469040000:  +4.8903809616E02\n" + 
				" 1469040001:  +5.4903809616E02\n" + 
				" 1569050000:  +5.8776999332E02\n" + 
				" 1569050001:  +4.4776999332E02\n" + 
				" 1569080000:  +3.4612173360E02\n" + 
				" 1569090000:  +6.8625488692E02\n" + 
				" 1569090001:  +2.4625488692E02\n" + 
				" 1569100000:  +7.6666666666E02\n"+
				" 1569100001:  +1.111E02\n"+
				" 1569100002:  +1.22222E02\n"+
				" 1569100003:  +1.333333E02\n"+
				" 1569100004:  +1.4444444E02\n"+
				" 1569100005:  +1.555555555E02\n"
				;
		String c = "";
		try {
			 	long lastTIMESTAMP = 0;
				for (String popSample = b.readLine(); popSample != null; popSample = b.readLine() ) {
						long newTIMESTAMP = Long.valueOf(popSample.split(":")[0].trim());
						if (newTIMESTAMP ==  lastTIMESTAMP) {
							newTIMESTAMP++;
							popSample = popSample.replace(""+lastTIMESTAMP, ""+newTIMESTAMP); 
						}
						lastTIMESTAMP = newTIMESTAMP;
						System.out.println(" ::  "+popSample);
						
						c += popSample;
						c += "\n";
						 
				}
			 
		}catch(Exception e) {e.printStackTrace();}
		assertEquals(expectedC, c);
			
		 
	}

	@Test
	public void testGetNextX3Timestamp() throws IOException {
		String aTXT = 
				" 920910000:               nan\n" + 
				" 1020920000:               nan\n" + 
				" 1121080000:  +8.4575808991E02\n" +  
				" 1269020000:  +7.4575808991E02\n" + 
				" 1369030000:  +6.4111111111E01\n" + 
				" 1469040000:  +5.4903809616E02\n" + 
				" 1569050000:               nan\n" + 
				" 1569060000:               nan\n" + 
				" 1569070000:               nan\n" + 
				" 1569050000:  +4.4776999332E02\n" +  
				" 1569080000:  +3.4612173360E02\n" + 
				" 1569090000:  +2.4625488692E02\n" + 
				" 1569100000:  +1.111E02\n" + 
				" 1569100001:  +1.22222E02\n" + 
				" 1569100002:  +1.333333E02\n" + 
				" 1569100003:  +1.4444444E02\n" + 
				" 1569100004:  +1.555555555E02\n" + 
				"";
		String bTXT = 
				" 820910000:   +0.8773875566E02\n" + 
				" 920910000:   +1.8773875566E02\n" + 
				" 1020920000:                nan\n" + 
				" 1121080000:               nan\n" +  
				" 1269020000:  +2.8575808991E02\n" + 
				" 1369030000:  +3.8111111111E01\n" + 
				" 1469040000:  +4.8903809616E02\n" + 
				" 1569050000:               nan\n" + 
				" 1569060000:               nan\n" + 
				" 1569070000:               nan\n" + 
				" 1569050000:  +5.8776999332E02\n" +  
				" 1569080000:               nan\n" +  
				" 1569090000:  +6.8625488692E02\n" + 
				" 1569100000:  +7.6666666666E02\n" + 
				"";
		String cTXT = 
				" 820910000:   +9.011111566E02\n" + 
				" 920910000:   +9.0222225566E02\n" + 
				" 1020920000:                nan\n" + 
				" 1121080000:               nan\n" +  
				" 1269020000:  +9.0333308991E02\n" + 
				" 1369030000:  +9.044441111E01\n" + 
				" 1469040000:  +9.055555616E02\n" + 
				" 1569050000:               nan\n" + 
				" 1569060000:               nan\n" + 
				" 1569070000:               nan\n" + 
				" 1569050000:  +9.066669332E02\n" +  
				" 1569080000:               nan\n" +  
				" 1569090000:  +9.077777692E02\n" + 
				" 1569100100:  +9.0888822442E02\n" +  
				" 1569200200:  +9.0999992442E02\n" +  
				" 1569300300:  +9.101010122442E02\n" +  
				"";		
		ParaReader a = new ParaReader(new BufferedReader(new InputStreamReader( new ByteArrayInputStream(aTXT.getBytes()))));
		a = new ParaReader(new BufferedReader(new InputStreamReader( new ByteArrayInputStream(bTXT.getBytes()))),a);
		a = new ParaReader(new BufferedReader(new InputStreamReader( new ByteArrayInputStream(cTXT.getBytes()))),a);
		
		String expectedC =
						" 820910000:   +9.011111566E02\n" + 
						" 820910001:   +0.8773875566E02\n" + 
						" 920910000:   +9.0222225566E02\n" + 
						" 920910001:   +1.8773875566E02\n" + 
						" 1121080000:  +8.4575808991E02\n" + 
						" 1269020000:  +9.0333308991E02\n" + 
						" 1269020001:  +2.8575808991E02\n" + 
						" 1269020000:  +7.4575808991E02\n" + 
						" 1369030000:  +9.044441111E01\n" + 
						" 1369030001:  +3.8111111111E01\n" + 
						" 1369030000:  +6.4111111111E01\n" + 
						" 1469040000:  +9.055555616E02\n" + 
						" 1469040001:  +4.8903809616E02\n" + 
						" 1469040000:  +5.4903809616E02\n" + 
						" 1569050000:  +9.066669332E02\n" + 
						" 1569050001:  +5.8776999332E02\n" + 
						" 1569050000:  +4.4776999332E02\n" + 
						" 1569080000:  +3.4612173360E02\n" + 
						" 1569090000:  +9.077777692E02\n" + 
						" 1569090001:  +6.8625488692E02\n" + 
						" 1569090000:  +2.4625488692E02\n" + 
						" 1569100000:  +7.8598222442E02\n" + 
						" 1569100001:  +1.4198222442E02\n" + 
						" 1569100002:  +1.4298222442E02\n" + 
						" 1569100003:  +1.4398222442E02\n" + 
						" 1569100004:  +1.4498222442E02\n" + 
						" 1569100100:  +9.0888822442E02\n" + 
						" 1569200200:  +9.0999992442E02\n" + 
						" 1569300300:  +9.101010122442E02\n" +
						"";

		String c = "";
		try {
			 	long lastTIMESTAMP = 0;
				for (String popSample = a.readLine(); popSample != null; popSample = a.readLine() ) {
						long newTIMESTAMP = Long.valueOf(popSample.split(":")[0].trim());
						if (newTIMESTAMP ==  lastTIMESTAMP) {
							newTIMESTAMP++;
							popSample = popSample.replace(""+lastTIMESTAMP, ""+newTIMESTAMP); 
						}
						lastTIMESTAMP = newTIMESTAMP;
						System.out.println(" ::  "+popSample);
						
						c += popSample;
						c += "\n";
						 
				}
			 
		}catch(Exception e) {e.printStackTrace();}
		assertEquals("- #nan== "+(-countSubstring(aTXT,"nan\n")- countSubstring(cTXT,"nan\n")- countSubstring(bTXT,"nan\n")), cTXT.split("\n").length+bTXT.split("\n").length+aTXT.split("\n").length - countSubstring(aTXT,"nan")- countSubstring(cTXT,"nan")- countSubstring(bTXT,"nan") , expectedC.split("\n").length);
		assertEquals(expectedC, c);
			
		 
	}
	
	public static int countSubstring(String str, String subStr){
		return (str.length() - str.replace(subStr, "").length()) / subStr.length();
	}

}
