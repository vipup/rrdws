package eu.blky.springmvc;
import java.io.File;
import java.io.FileInputStream;  
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;  
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.AbstractController;
 
public class BackupController extends AbstractController{
 
	private BackupService myBackupService; 

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
		
		File zzz = getMyBackupService().backup();  
		java.io.InputStream fio = new FileInputStream(zzz);
		byte[]buf = new byte[1023];
		for (int i=fio.read(buf);i>0;i=fio.read(buf)){
			response.getOutputStream().write(buf,0,i);
			response.getOutputStream().flush();
		}	
		fio.close(); 
		return null;
	}

	public BackupService getMyBackupService() {
		return myBackupService;
	}

	public void setMyBackupService(BackupService myBackupService) {
		this.myBackupService = myBackupService;
	}	 

}