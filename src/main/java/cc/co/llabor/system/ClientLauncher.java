package cc.co.llabor.system;

import java.lang.instrument.Instrumentation;

import org.collectd.mx.RemoteMBeanSender;

/** 
 * <b>Description:TODO</b>
 * @author      vipup<br>
 * <br>
 * <b>Copyright:</b>     Copyright (c) 2006-2008 Monster AG <br>
 * <b>Company:</b>       Monster AG  <br>
 * 
 * Creation:  04.04.2011::10:43:09<br> 
 */
public class ClientLauncher implements Runnable {

	@Override
	public void run(){
		try {
			Instrumentation instr = null;
		////java -javaagent:collectd.jar="udp://localhost#javalang" -jar sigar.jar
		////java -javaagent:collectd.jar="udp://localhost#javalang" -jar sigar.jar					
			String args = "udp://239.192.74.66:25826#javalang#tomcat";
			RemoteMBeanSender.premain(args , instr);
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	}

}


 