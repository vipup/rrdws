package eu.blky.cep.polo2rrd;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.espertech.esper.client.Configuration;
import com.espertech.esper.client.EPAdministrator;
import com.espertech.esper.client.EPRuntime;
import com.espertech.esper.client.EPServiceProvider;
 
@Service
public class CepKeeper {
	
	@PostConstruct
	public void init(){
		System.out.println("CepKeeper init method called..." ); 
	} 
	
	@PreDestroy
	public void destory(){
		System.out.println("CepKeeper destroy method called..");
		try{cep.destroy();}catch(Throwable e) {e.printStackTrace();}
		try{cepConfig = null;}catch(Throwable e) {e.printStackTrace();}
		try{cepRT = null;}catch(Throwable e) {e.printStackTrace();}
		try{cepAdm.destroyAllStatements();}catch(Throwable e) {e.printStackTrace();}
	}

    private EPServiceProvider cep;
	public EPServiceProvider getCep() {
		return cep;
	}

	public void setCep(EPServiceProvider cep) {
		this.cep = cep;
	}	
 
	private EPAdministrator cepAdm;
	// The Configuration is meant only as an initialization-time object.
	@Autowired
	private Configuration cepConfig ;//= new Configuration();

	private EPRuntime cepRT;  
	
	public EPAdministrator getCepAdm() {
		return cepAdm;
	}

	public void setCepAdm(EPAdministrator cepAdm) {
		this.cepAdm = cepAdm;
	}

	public Configuration getCepConfig() {
		return cepConfig;
	}

	public void setCepConfig(Configuration cepConfig) {
		this.cepConfig = cepConfig;
	}


	public EPRuntime getCepRT() {
		return cepRT;
	}

	public void setCepRT(EPRuntime cepRT) {
		this.cepRT = cepRT;
	}	
}
