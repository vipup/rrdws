package cc.co.llabor.system;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class Zipper {
	private List <String> fileList =  new ArrayList < String > ();
	private String inForlderToZip;
	private String outputZipArchiveFileName;

 
	    public Zipper(String inName, String outName) {
	    	this.inForlderToZip =inName; 
	    	generateFileList(new File(inName));
	        this.outputZipArchiveFileName = outName;
	    }

	    public Zipper(File workdirTmp, File backupTmp) {
	    	this.inForlderToZip = workdirTmp.getAbsolutePath();
	    	generateFileList(workdirTmp);
	    	this.outputZipArchiveFileName = backupTmp.getAbsolutePath();
		}

		public final void zip(){
	    	zipIt(this.outputZipArchiveFileName);
	    }
	    
	    public static void main(String[] args) {
		    String OUTPUT_ZIP_FILE = "/tmp/yyy/xxx.ZIP";
		    String SOURCE_FOLDER_MAIN = "/tmp/xxx/"; // SourceFolder path	    	
	    	Zipper zTmp = new Zipper(SOURCE_FOLDER_MAIN, OUTPUT_ZIP_FILE);
	    	zTmp.zip();
	    }

	    private void zipIt(String zipFile) {
	        byte[] buffer = new byte[1024]; 
	        FileOutputStream fos = null;
	        ZipOutputStream zos = null;
	        try {
	        	
	            fos = new FileOutputStream(zipFile);
	            zos = new ZipOutputStream(fos);
	            zos.setLevel(9);

	            System.out.println("Output to Zip : " + zipFile);
	            FileInputStream in = null;

	            for (String file: this.fileList) {
	                
	                String zipentryName = file.substring(inForlderToZip.length());
	                System.out.println("File Added : " + zipentryName);
	                ZipEntry ze = new ZipEntry(zipentryName);
	                zos.putNextEntry(ze);
	                try {
	                	File sourceFile=new File(file); 
	                    in = new FileInputStream(sourceFile);
	                    int len;
	                    while ((len = in .read(buffer)) > 0) {
	                        zos.write(buffer, 0, len);
	                    }
	                }catch(java.io.FileNotFoundException e){
	                	e.printStackTrace();
	                } finally {
	                    if(in!=null)in.close();
	                }
	            }

	            zos.closeEntry();
	            System.out.println("Folder successfully compressed");

	        } catch (IOException ex) {
	            ex.printStackTrace();
	        } finally {
	            try {
	                zos.close();
	            } catch (IOException e) {
	                e.printStackTrace();
	            }
	        }
	    }

	    private void generateFileList(File node) {
	        // add file only
	        if (node.isFile()) {
	            fileList.add(generateZipEntry(node.toString()));
	        }

	        if (node.isDirectory()) {
	            String[] subNote = node.list();
	            for (String filename: subNote) {
	                generateFileList(new File(node, filename));
	            }
	        }
	    }

	    private String generateZipEntry(String file) {
	        return file;
	    }

		public Object getData() {
			// TODO Auto-generated method stub
			return null;
		}
	}