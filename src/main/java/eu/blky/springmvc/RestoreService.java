package eu.blky.springmvc; 
import java.io.File;
import java.io.FilenameFilter; 

import javax.annotation.PostConstruct;

import org.jrobin.core.RrdDbPool; 
import org.jrobin.mrtg.server.Config;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
//!! not possible to use ANNOTATION  together with  /rrd/src/main/webapp/WEB-INF/applicationContext.xml !! 
//!! import org.springframework.stereotype.Service; 

import cc.co.llabor.system.StatusMonitor;
import cc.co.llabor.system.Unzipper; 

//!! not possible to use ANNOTATION  together with  /rrd/src/main/webapp/WEB-INF/applicationContext.xml !! 
//!! @Service
public class RestoreService  {
	 
 

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
				zTmp.unzip();
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
 
}