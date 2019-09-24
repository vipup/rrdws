package eu.blky.springmvc;

import static org.junit.Assert.*;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;

import org.junit.Test;

public class DFReaderTest {

	@Test
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
		DFReader a = new DFReader(new BufferedReader(new InputStreamReader( new ByteArrayInputStream(aTXT.getBytes()))));
		DFReader b = new DFReader(new BufferedReader(new InputStreamReader( new ByteArrayInputStream(bTXT.getBytes()))));
		
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
				" 1569100000:  +1.4598222442E02\n" + 
				" 1569100001:  +1.4198222442E02\n" + 
				" 1569100002:  +1.4298222442E02\n" + 
				" 1569100003:  +1.4398222442E02\n" + 
				" 1569100004:  +1.4498222442E02\n" + 
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
				" 1569100000:  +7.8598222442E02\n" + 
				"";
		DFReader a = new DFReader(new BufferedReader(new InputStreamReader( new ByteArrayInputStream(aTXT.getBytes()))));
		DFReader b = new DFReader(new BufferedReader(new InputStreamReader( new ByteArrayInputStream(bTXT.getBytes()))),a);
		
		String expectedC = 
				" 820910000:   +0.8773875566E02\n" + 
				" 920910000:   +1.8773875566E02\n" + 
				" 1121080000:  +8.4575808991E02\n" + 
				" 1269020000:  +2.8575808991E02\n" + 
				" 1269020000:  +7.4575808991E02\n" + 
				" 1369030000:  +3.8111111111E01\n" + 
				" 1369030000:  +6.4111111111E01\n" + 
				" 1469040000:  +4.8903809616E02\n" + 
				" 1469040000:  +5.4903809616E02\n" + 
				" 1569050000:  +5.8776999332E02\n" + 
				" 1569050000:  +4.4776999332E02\n" + 
				" 1569080000:  +3.4612173360E02\n" + 
				" 1569090000:  +6.8625488692E02\n" + 
				" 1569090000:  +2.4625488692E02\n" + 
				" 1569100000:  +7.8598222442E02\n" + 
				" 1569100001:  +1.4198222442E02\n" + 
				" 1569100002:  +1.4298222442E02\n" + 
				" 1569100003:  +1.4398222442E02\n" + 
				" 1569100004:  +1.4498222442E02\n";
		String c = "";
		try {
			 
				for (String popSample = b.readNextChainedSample(); popSample != null; popSample = b.readNextChainedSample() ) {
			 
						System.out.println(" ::  "+popSample);
						c += popSample;
						c += "\n";
						 
				}
			 
		}catch(Exception e) {e.printStackTrace();}
		assertEquals(expectedC, c);
			
		 
	}

}
