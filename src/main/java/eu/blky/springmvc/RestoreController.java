package eu.blky.springmvc; 
import java.io.File; 
import java.util.Iterator;
import java.util.List; 

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.tomcat.util.http.fileupload.FileUploadException;
import org.jrobin.core.RrdException;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.AbstractController;
 
 
public class RestoreController extends AbstractController{
	  
	
	@Override
	protected ModelAndView handleRequestInternal(HttpServletRequest request,
		HttpServletResponse response)   {

        boolean isMultipart = ServletFileUpload.isMultipartContent(request);

        if (isMultipart) {
            try {
				return processUploadedZip(request);
			} catch (org.apache.commons.fileupload.FileUploadException e) {
				e.printStackTrace();
			} catch (RrdException e) {
				e.printStackTrace();
	    		ModelAndView model = new ModelAndView("justrebootyourserver");
	    		model.addObject("msg", "EERROROOROR"+e.getMessage()); 
	    		return model;    
			} catch (Exception e) { 
				e.printStackTrace();
	    		ModelAndView model = new ModelAndView("justrebootyourserver");
	    		model.addObject("msg", "EERROROOROR"+e.getMessage()); 
	    		return model; 
			}   
        }else {
    		ModelAndView model = new ModelAndView("restore");
    		model.addObject("msg", "hello world"); 
    		return model;        	
        }
		ModelAndView model = new ModelAndView("justrebootyourserver");
		model.addObject("msg", "?!?!?!?\""); 
		return model;  
	}

	private ModelAndView processUploadedZip(HttpServletRequest request)
			throws org.apache.commons.fileupload.FileUploadException, Exception, RrdException {
		// Create a factory for disk-based file items
		FileItemFactory factory = new org.apache.commons.fileupload.disk.DiskFileItemFactory();

		// Create a new file upload handler
		ServletFileUpload upload = new ServletFileUpload( factory); 
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
 
		
		restore();
		ModelAndView model =  new ModelAndView ("justrebootyourserver");
		model.addObject("msg", "restore ok!");
		return model;
	}
	
	private RestoreService  restoreService;
	
	private void restore() throws RrdException {		
		restoreService.restore( );
		restoreService.setRestorePerformedFromExternal(true);
	}

	public RestoreService getRestoreService() {
		return restoreService;
	}

	public void setRestoreService(RestoreService restoreService) {
		this.restoreService = restoreService;
	}
 
}