package cc.co.llabor.system;

import java.io.File;
import java.io.FileInputStream; 
import java.io.FileOutputStream;
import java.io.IOException; 
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream; 

public class Unzipper { 
	private String outForlderToUnzip;
	private String inputZipArchiveFileName; 
	
	    public Unzipper(String inName, String outName) {
	    	this.inputZipArchiveFileName =inName;  
	        this.outForlderToUnzip = outName;
	    }

	    public Unzipper(File toRestore, File workdirTmp) {
	    	this.inputZipArchiveFileName =toRestore.getAbsolutePath();  
	        this.outForlderToUnzip = workdirTmp.getAbsolutePath();
		}

		public final void unzip() throws IOException{
	    	byte[] buffer = new byte[1024];
	    	//get the zip file content
	    	ZipInputStream zis =
	    		new ZipInputStream(new FileInputStream(inputZipArchiveFileName));
	    	//get the zipped file list entry
	    	ZipEntry ze = zis.getNextEntry();
	    	while(ze!=null){

	     	   String fileName = ze.getName();
	            File newFile = new File(outForlderToUnzip + File.separator + fileName);

	            System.out.println("file unzip : "+ newFile.getAbsoluteFile());

	             //create all non exists folders
	             //else you will hit FileNotFoundException for compressed folder
	             new File(newFile.getParent()).mkdirs();

	             FileOutputStream fos = new FileOutputStream(newFile);

	             int len;
	             while ((len = zis.read(buffer)) > 0) {
	        		fos.write(buffer, 0, len);
	             }

	             fos.close();
	             ze = zis.getNextEntry();
	     	}

	         zis.closeEntry();
	     	zis.close();

	     	System.out.println("Done"); 
	    }
	    
	    public static void main(String[] args) throws IOException {
		    String SRC = "/tmp/yyy/xxx.ZIP";
		    String DST = "/tmp/zzz/"; // SourceFolder path	    	
	    	Unzipper zTmp = new Unzipper(SRC, DST);
	    	zTmp.unzip();
	    }

	    
	}