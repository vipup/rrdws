package eu.blky.springmvc;
import java.io.File;

import org.jrobin.core.RrdDbPool;
import org.jrobin.mrtg.server.Config;
import org.springframework.stereotype.Service; 

import cc.co.llabor.system.StatusMonitor;
import cc.co.llabor.system.Zipper;  

@Service
public class BackupService {
 
 
	private StatusMonitor sm;
	private RestoreService restoreService; 


	{
		System.out.println("BackupService inited");
	}
 
	
	public BackupService ( StatusMonitor st ) {
		this.sm = st;
	
		System.out.println("BackupService created....");
	}
 

	public File backup() {
		System.out.println("backup called.............................................................");
		System.out.println("..........backup called.............................................................");
		System.out.println("backup cal...........led.............................................................");
		System.out.println("backup ..............called.............................................................");
		System.out.println("backup c.............alled.............................................................");
		System.out.println("..................backup called.............................................................");
		if (restoreService.restorePerformedFromExternal()) {
			this.sm.getStatus().put("BackupService","backup process was suspendet from prev Restore-Action. Try to restart APP before.");
			System.out.println("backup process was suspendet from prev Restore-Action. Try to restart APP before.");
			return null;
		}
		// restore prev RRDDB, if any
		try{
			File workdirTmp = new File ( Config.CALC_DEFAULT_WORKDIR() );
			File tmpdirTmp = new File (System.getProperty("java.io.tmpdir")); 
			File backupTmp = new File(tmpdirTmp, "rrd"+System.currentTimeMillis()+".backup"); 
			RrdDbPool.getInstance().reset();
			Zipper zTmp = new Zipper(workdirTmp, backupTmp); 
			zTmp.zip();
			sm.getStatus().put("backupDB", "backupIsDone");
			return backupTmp;
			 
		}catch(Exception e){
			sm.getStatus().put("backupDB", "Fail"); 
		}
		return null;
		
	}


	public RestoreService getRestoreService() {
		return restoreService;
	}


	public void setRestoreService(RestoreService restoreService) {
		this.restoreService = restoreService;
	}


}