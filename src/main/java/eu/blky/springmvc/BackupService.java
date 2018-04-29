package eu.blky.springmvc;
import java.io.File; 
import java.util.Map;
 
import org.jrobin.mrtg.server.Config;
import org.springframework.stereotype.Service; 

import cc.co.llabor.system.StatusMonitor;
import cc.co.llabor.system.Zipper;  

@Service
public class BackupService {
 
 
	private StatusMonitor sm;


	{
		System.out.println("BackupService inited");
	}
 
	
	public BackupService ( StatusMonitor st ) {
		this.sm = st;
	
		System.out.println("BackupService created....");
	}
 

	public File backup() {
		// restore prev RRDDB, if any
		try{
			File workdirTmp = new File ( Config.CALC_DEFAULT_WORKDIR() );
			File tmpdirTmp = new File (System.getProperty("java.io.tmpdir")); 
			File backupTmp = new File(tmpdirTmp, "rrd"+System.currentTimeMillis()+".backup"); 
			Zipper zTmp = new Zipper(workdirTmp, backupTmp); 
			zTmp.zip();
			sm.getStatus().put("backupDB", "backupIsDone");
			return backupTmp;
			 
		}catch(Exception e){
			sm.getStatus().put("backupDB", "Fail"); 
		}
		return null;
		
	}


}