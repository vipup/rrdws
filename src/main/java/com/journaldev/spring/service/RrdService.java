package com.journaldev.spring.service;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import ws.rrd.csv.RrdKeeper;

public class RrdService {

	@PostConstruct
	public void init(){
		System.out.println("RrdService init method called"+RrdKeeper.class.getClassLoader());
	}
	
	public RrdService(){
		System.out.println("RrdService no-args constructor called");
	}
	
	@PreDestroy
	public void destory(){
		System.out.println("RrdService destroy method called");
		RrdKeeper.getInstance().destroy();
	}
}