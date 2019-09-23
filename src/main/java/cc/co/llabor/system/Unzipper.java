package cc.co.llabor.system;

import java.io.File;
import java.io.FileInputStream; 
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.logging.Logger;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import org.datanucleus.metadata.MetaDataMerger;
import org.jrobin.core.RrdException;

import eu.blky.cep.polo2rrd.SysoUpdater; 

public class Unzipper { 
	private String outForlderToUnzip;
	private String inputZipArchiveFileName;
	static final Logger log = Logger.getLogger(Unzipper.class.getName()); 
	
	    public Unzipper(String inName, String outName) {
	    	this.inputZipArchiveFileName =inName;  
	        this.outForlderToUnzip = outName;
	    }

	    public Unzipper(File toRestore, File workdirTmp) {
	    	this.inputZipArchiveFileName =toRestore.getAbsolutePath();  
	        this.outForlderToUnzip = workdirTmp.getAbsolutePath();
		}

		public final void unzip(Merger m) throws IOException{
	    	byte[] buffer = new byte[1024];
	    	//get the zip file content
	    	ZipInputStream zis = null;
	    	try{
	    		zis = new ZipInputStream(new FileInputStream(inputZipArchiveFileName));
		    	//get the zipped file list entry
		    	ZipEntry ze = zis.getNextEntry();
		    	while(ze!=null){
	
		     	   String fileName = ze.getName();
		            File newFile = new File(outForlderToUnzip + File.separator + fileName);
	
		            log.info( "file unzip : "+ newFile.getAbsoluteFile());
	
		             //create all non exists folders
		             //else you will hit FileNotFoundException for compressed folder
		             
		             FileOutputStream fos = null ;
		             try {
		            	 new File(newFile.getParent()).mkdirs();
		              
			             fos= new FileOutputStream(newFile.toPath().toString().replace(".rrd", "NEW.rrd"));
		
			             int len;
			             while ((len = zis.read(buffer)) > 0) {
			        		fos.write(buffer, 0, len);
			             }
		
			             
		             }finally {
		            	 if(null!=fos) {
		            		 fos.close();
		            		 try {
								m.merge(fileName);
							} catch (RrdException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
		            	 }
		            	 
		             }
		             ze = zis.getNextEntry();
		     	}
	
		        zis.closeEntry();
	    	}finally { 
	    		if(null!=zis) {
	    			zis.close();
	    		}
         		
	    	}

	     	
	     	log.info( "Done"); 
	    }
	    
	    public static void main(String[] args) throws IOException {
		    String sSRC = "/tmp/yyy/xxx.ZIP";
		    String sDST = "/tmp/zzz/"; // SourceFolder path	    	
	    	Unzipper zTmp = new Unzipper(sSRC, sDST);
	    	zTmp.unzip(new Merger() {

				@Override
				public boolean merge(String rrdname) {
					System.out.println("TO MERGE:"+rrdname); 
					return false; 
				}});
	    }

	    
	}