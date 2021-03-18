package cc.co.llabor.system;

import java.io.File;
import java.io.FileInputStream; 
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.util.List;
import java.util.logging.Logger;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;


import org.jrobin.core.RrdException;

 

public class Unzipper { 
	private String outForlderToUnzip;
	private String inputZipArchiveFileName;
	private List<String> whiteList;
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
			long length = new File(inputZipArchiveFileName).length();
	    	byte[] buffer = new byte[1024];
	    	//get the zip file content
	    	ZipInputStream zis = null;
	    	try{
	    		
	    		FileInputStream is = new FileInputStream(inputZipArchiveFileName);
	    		FileChannel channel = is.getChannel();
				zis = new ZipInputStream(is);
		    	//get the zipped file list entry
		    	ZipEntry ze = zis.getNextEntry();
		    	for(ze = zis.getNextEntry();ze!=null;ze = zis.getNextEntry()){
		    		String fileName = ze.getName();//ze.getLastModifiedTime() (new File(outForlderToUnzip +"/"+fileName  )).exists()
		    		if (this.whiteList!=null) {this.whiteList.add("X-1491558113.rrd");
		    			if (!this.whiteList.contains(fileName.replace("/rrd.home/", ""))) {
		    				continue; 
		    			}
		    		}
	
		     	  
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
		
		             }catch(IOException e) {
		            	 
		            	 System.out.println("}catch(IOException e) {"+e.getMessage());
		             }catch(Throwable e) {
		            	 e.printStackTrace();
		            	 System.out.println("}catch(IOException e) {"+e.getMessage());
		             }finally {
		            	 if(null!=fos) {
		            		 fos.close();
		            		 try {
								m.merge(fileName.replace("/rrd.home/", ""));
							} catch (RrdException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
				             }catch(Throwable e) {
				            	 e.printStackTrace();
				            	 System.out.println("}catch(IOException e) {"+e.getMessage());								
							}
		            	 }
		            	 
		             }
		            
					System.out.println("ZZZZZZZZ  :"+(channel.position()*100.0/length)+"% ZZZZZZ Done: "+channel.position() +"from ::::"+ length);
		             try {
						Thread.sleep(1);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
		             
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

		public void setWhilelist(List<String> whiteList) {
			this.whiteList = whiteList;
		}

	    
	}