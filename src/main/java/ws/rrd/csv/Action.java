package ws.rrd.csv;
/** 
 * <b>Description:TODO</b>
 * @author      vipup<br>
 * <br>
 * <b>Copyright:</b>     Copyright (c) 2006-2008 Monster AG <br>
 * <b>Company:</b>       Monster AG  <br>
 * 
 * Creation:  20.04.2010::14:01:26<br> 
 */
public interface Action {

	public Object perform(String xpath, String timestamp, String data);
	public Object perform(String xpath, long timestamp, String data);

}


 