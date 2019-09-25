package eu.blky.springmvc;

import java.util.Map; 
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.AbstractController;
import cc.co.llabor.system.StatusMonitor;
import eu.blky.cep.polo2rrd.CepKeeper;
import eu.blky.cep.polo2rrd.Polo2RddForwarderService;
 
 
public class CepController extends AbstractController{
	
	@Autowired // not works !?
	Polo2RddForwarderService p2r;
	
	private CepKeeper cepKeeper;
	/**
	 * @return the cepKeeper
	 */
	public CepKeeper getCepKeeper() {
		return cepKeeper;
	}
	/**
	 * @param cepKeeper the cepKeeper to set
	 */
	public void setCepKeeper(CepKeeper cepKeeper) {
		this.cepKeeper = cepKeeper;
	}

	{
		System.out.println("CepController inited...");
	} 

	private StatusMonitor statusMonitor;
//	@PostConstruct -  workaround via StartStopServlet 
	public void setStatusMonitor(StatusMonitor sm){
		this.statusMonitor = sm;
		System.out.println("Assigned StatusMonitor:"+this.statusMonitor);

	}	
	
	@Override
	protected ModelAndView handleRequestInternal(
			 
		HttpServletRequest request,
		HttpServletResponse response) throws Exception {


		ModelAndView model = new ModelAndView("cep");
		 
		try {
			Object env = cepKeeper.getCepRT().getCurrentTime();
			model.addObject("env", env );
		}catch (Throwable e) {}		 
		
		try {			
			model.addObject("p2r", p2r);
		}catch (Throwable e) {}		 
		
		
		try {
			Map<String, Object> vars = cepKeeper.getCepRT().getVariableValueAll();
			model.addObject("vars", vars );
			
		}catch (Throwable e) {}
		

		return model;
	}


}