package eu.blky.springmvc; 
import java.io.File;
import java.io.FilenameFilter;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.tomcat.util.http.fileupload.FileUploadException; 
import org.jrobin.mrtg.server.Config;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.AbstractController;

import cc.co.llabor.system.StatusMonitor;
import cc.co.llabor.system.Unzipper; 

@Service
public class RestoreService  {
	 

	private Map<String, String> status;

	public RestoreService ( StatusMonitor st ) {
		this.status = st.getStatus();

		System.out.println("RestoreController created....");
	}

	@PostConstruct
	public void init() {
		this.restore(status);
		System.out.println("RestoreController inited."	+ "");
	}
	
	private static Logger log = LoggerFactory.getLogger(RestoreService.class);

	{
		System.out.println("RestoreController loaded");
	}
	 

	public void restore( Map<String, String> status) {
		status = status==null?new HashMap<String, String>():status;
		try{
			File workdirTmp = new File ( Config.CALC_DEFAULT_WORKDIR() );
			File tmpdirTmp = new File (System.getProperty("java.io.tmpdir"));
			FilenameFilter filterTmp = new FilenameFilter(){

				@Override
				public boolean accept(File dir, String name) {
					return name.startsWith("rrd") && name.endsWith(".backup") ; 
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
				
				Unzipper zTmp = new Unzipper(toRestore, workdirTmp);
				zTmp.unzip();
				status.put("restoreDB", "DB restore Done"); 
			}
		}catch(Exception e){
			log.error("restoreDB", e);
			status.put("restoreDB", "DB restore is not possible! New Server/instance/App/Node/DB?");
		}
	}
}