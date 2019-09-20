/* ============================================================
 * JRobin : Pure java implementation of RRDTool's functionality
 * ============================================================
 *
 * Project Info:  http://www.jrobin.org
 * Project Lead:  Sasa Markovic (saxon@jrobin.org);
 *
 * (C) Copyright 2003-2005, by Sasa Markovic.
 *
 * Developers:    Sasa Markovic (saxon@jrobin.org)
 *
 *
 * This library is free software; you can redistribute it and/or modify it under the terms
 * of the GNU Lesser General Public License as published by the Free Software Foundation;
 * either version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License along with this
 * library; if not, write to the Free Software Foundation, Inc., 59 Temple Place, Suite 330,
 * Boston, MA 02111-1307, USA.
 */

package org.jrobin.core;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import ws.rrd.csv.RrdKeeper;

/**
 * Class to represent various JRobin checked exceptions.
 * JRobin code can throw only <code>RrdException</code>
 * (for various JRobin related errors) or <code>IOException</code>
 * (for various I/O errors).
 *
 * @author <a href="mailto:saxon@jrobin.org">Sasa Markovic</a>
 */
public class RrdException extends Exception {
	private static final long serialVersionUID = 1L;
	private String message;
	private Object errorObject;

	/**
	 * Creates new RrdException with the supplied message in it.
	 *
	 * @param message Error message.
	 * @deprecated
	 */
	public RrdException(String message) {
		super(message);
	}

	/**
	 * Creates new RrdException object from any java.lang.Exception object
	 *
	 * @param e Exception object
	 */
	private RrdException(Exception e) {
		super(e);
	}

	public RrdException(String string, String command) {
		this(string, (Object)command);
	}
	public RrdException(String string, Object errorObject) {
		this.message = string;
		this.errorObject = errorObject ;
	}
	
	public String getMessage() {
		return this.message;
	}
	
	public String toString () {
		return this.message + "::::::::"+ errorObject;
	}

	static String ENABLED_THIS ="11111111111111111111111";
	public void collectError() {
		if (ENABLED_THIS.length() >11)RrdKeeper.getInstance().error(this);		
	}
	// hope the number of new errors-types will not the reason for OOM-Exception
	static final List<String> tmpRepoToAvoidDuplicatedTryToWriteDownTheCalLStackTrace = new ArrayList<String>();
	public String getUUID() {
		String retval = ""+(""+this.message).hashCode();
		if(!tmpRepoToAvoidDuplicatedTryToWriteDownTheCalLStackTrace.contains(retval))
		try {
			tmpRepoToAvoidDuplicatedTryToWriteDownTheCalLStackTrace.add(retval);
			File dumpTmp = new File(RrdFileBackend.CALC_DEFAULT_WORKDIR()  + "/RRD-ERROR-HASH-TOFIX-"+retval+".log");
			
			writeToTextSubFIle(dumpTmp,this.message);
			System.out.println("ERROR added:++++++++++++"+retval+"++++++++::"+this.message);
		}catch(Throwable e) {
			// ignore all possible errors
		}
		return retval;
	}
	
	private static final String writeToTextSubFIle(File out , String data) throws IOException {
		 
		// fileA.deleteOnExit();
		FileWriter fwA = new FileWriter(out);
		fwA.write(data);
		fwA.flush();
		fwA.close();
		return out.getCanonicalPath();
	}

}
