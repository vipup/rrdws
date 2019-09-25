package com.journaldev.spring.service;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import cc.co.llabor.system.StatusMonitor;
import eu.blky.cep.polo2rrd.Polo2RddForwarderService;
import ws.rrd.csv.RrdKeeper;

@Service
public class CepService {
public static final String POLO2RRD2 = "polo2rrd";
	//    ApplicationContext applicationContext = WebApplicationContextUtils.getWebApplicationContext(getServletContext());
//    statusMonitor= (StatusMonitor) applicationContext.getBean("StatusMonitor");
//    
//    myBackupService =   (BackupService) applicationContext.getBean("myBackupService");
//    try {
//    	eu.blky.cep.polo2rrd.Polo2RddForwarderService polo2rrd =    (Polo2RddForwarderService) applicationContext.getBean("polo2rrd");
//    	// crazyOverDozedWorkaround 
//    	polo2rrd.setStatusMonitor(statusMonitor);
//    }catch(Throwable e) {
//    	e.printStackTrace();
//    }
	@Autowired
	Polo2RddForwarderService polo2rrd ;
	@Autowired
	StatusMonitor statusMonitor;
	
	@PostConstruct
	public void init(){
		System.out.println("CepRrdService init method called..." );
//		try {
//			polo2rrd.setStatusMonitor(statusMonitor);
//			statusMonitor.addObjectForMonitoring(POLO2RRD2,polo2rrd);
//			System.out.println("CepRrdService init method called..." );
//		}catch(Exception e) {
//			e.printStackTrace();
//		}
		
	}
	
	public CepService(){
		System.out.println("CepRrdService no-args constructor called");
	}
	
	@PreDestroy
	public void destory(){
		System.out.println("CepRrdService destroy method called..");
		polo2rrd.destroy();
	}
}