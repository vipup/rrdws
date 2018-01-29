package eu.blky.springmvc;
import java.io.File;
import java.io.FileInputStream;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse; 
import org.jrobin.mrtg.server.Config;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.AbstractController;

import cc.co.llabor.system.Zipper;  

public class BackupController extends AbstractController{

	{
		System.out.println("BackupController inited");
	}
 
	
	@Override
	public ModelAndView handleRequestInternal(HttpServletRequest request, HttpServletResponse response) throws Exception {		  
	    response.setContentType("application/zip");
	    response.setHeader("Content-Disposition", "inline;filename=backup#"+System.currentTimeMillis()+".zip" );
	    response.setHeader("Pragma", "no-cache");
		response.setHeader("Cache-Control","no-cache"); //HTTP 1.1
		response.setHeader("Pragma","no-cache"); //HTTP 1.0
		response.setDateHeader ("Expires", 0); //prevents caching at the proxy server
		
		File zzz = backup(null);  
		java.io.InputStream fio = new FileInputStream(zzz);
		byte[]buf = new byte[1023];
		for (int i=fio.read(buf);i>0;i=fio.read(buf)){
			response.getOutputStream().write(buf,0,i);
			response.getOutputStream().flush();
		}	
		fio.close(); 
		return null;
	}	
 

	public static File backup(Map<String, String> status) {
		status = status==null?new HashMap<String, String>():status;
		// restore prev RRDDB, if any
		try{
			File workdirTmp = new File ( Config.CALC_DEFAULT_WORKDIR() );
			File tmpdirTmp = new File (System.getProperty("java.io.tmpdir")); 
			File backupTmp = new File(tmpdirTmp, "rrd"+System.currentTimeMillis()+".backup"); 
			Zipper zTmp = new Zipper(workdirTmp, backupTmp); 
			zTmp.zip();
			status.put("backupDB", "backupIsDone");
			return backupTmp;
			 
		}catch(Exception e){
			status.put("backupDB", "Fail"); 
		}
		return null;
		
	}


}