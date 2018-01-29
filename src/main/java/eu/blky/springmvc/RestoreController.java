package eu.blky.springmvc; 
import java.io.File;
import java.io.FilenameFilter;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.tomcat.util.http.fileupload.FileUploadException; 
import org.jrobin.mrtg.server.Config;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.AbstractController;

import cc.co.llabor.system.Unzipper; 

public class RestoreController extends AbstractController{

	private static Logger log = LoggerFactory.getLogger(RestoreController.class);

	{
		System.out.println("RestoreController inited");
	}
	
	@Override
	protected ModelAndView handleRequestInternal(HttpServletRequest request,
		HttpServletResponse response) throws Exception {

        boolean isMultipart = ServletFileUpload.isMultipartContent(request);

        if (isMultipart) {
            // Create a factory for disk-based file items
        	FileItemFactory factory = new org.apache.commons.fileupload.disk.DiskFileItemFactory();

            // Create a new file upload handler
            ServletFileUpload upload = new ServletFileUpload( factory);

            try {
                // Parse the request
                List items = upload.parseRequest(request);
                Iterator iterator = items.iterator();
                while (iterator.hasNext()) {
                    FileItem item = (FileItem) iterator.next();
                    if (!item.isFormField()) {
                        String fileName = item.getName();    
//                        String root = getServletContext().getRealPath("/");
//                        File path = new File(root + "/uploads");
                        File path = new File (System.getProperty("java.io.tmpdir"));
//                        if (!path.exists()) {
//                            boolean status = path.mkdirs();
//                        }

                        File uploadedFile = new File(path + "/" + fileName);
                        System.out.println(uploadedFile.getAbsolutePath());
                        item.write(uploadedFile); 
                    }
                    
                }
            } catch (FileUploadException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }
            
            restore( null );
        }else {
    		ModelAndView model = new ModelAndView("restore");
    		model.addObject("msg", "hello world");

    		return model;        	
        }
        
        
		return null;
 
	}

	public static void restore( Map<String, String> status) {
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