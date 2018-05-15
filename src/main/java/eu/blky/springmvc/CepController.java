package eu.blky.springmvc;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;  
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.AbstractController;

import com.espertech.esper.client.ConfigurationVariable;
import com.espertech.esper.client.EPStatement;
import com.espertech.esper.client.StatementAwareUpdateListener;
import com.espertech.esper.client.UpdateListener;

import cc.co.llabor.system.StatusMonitor;
import eu.blky.cep.polo2rrd.CepKeeper;
 
 
public class CepController extends AbstractController{
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
	protected ModelAndView handleRequestInternal(HttpServletRequest request,
		HttpServletResponse response) throws Exception {


		ModelAndView model = new ModelAndView("cep");
		 
		try {
			Object env = cepKeeper.getCepRT().getCurrentTime();
			model.addObject("env", env );
		}catch (Throwable e) {}		 
		try { 
			String[] StatementNames = cepKeeper.getCepAdm().getStatementNames();
			model.addObject("StatementNames", array2string(StatementNames) );
			Object[] statements = listAllStatements(StatementNames);
			model.addObject("statements", array2string(statements ) );
			
			Object[] listeners = listAllListeners(StatementNames);
			model.addObject("statementsListeners", array2string(listeners ) );
			
			// RT
			String[] dataflows = cepKeeper.getCepRT().getDataFlowRuntime().getDataFlows() ;
			model.addObject("dataflows", array2string(dataflows) );
			String[] savedConfiguratios= cepKeeper.getCepRT().getDataFlowRuntime().getSavedConfigurations() ;
			model.addObject("savedConfiguratios", array2string(savedConfiguratios) );
			String[] savedInstances= cepKeeper.getCepRT().getDataFlowRuntime().getSavedInstances()  ;
			model.addObject("savedInstances", array2string(savedInstances) );
			
			// getCepConfig
			Map<String, ConfigurationVariable> vars = cepKeeper.getCepConfig().getVariables() ;
			model.addObject("vars", map2string(vars) );
			Map<String, String> etnames = cepKeeper.getCepConfig().getEventTypeNames() ;
			model.addObject("etnames", map2string(vars) );
		
		
		}catch (Throwable e) {}
		

		return model;
	}
	private Object[] listAllListeners(String[] StatementNames) {
		ArrayList<String>  listSet= new ArrayList<String>();
		for(String name:StatementNames) {
			EPStatement stmtTmp = cepKeeper.getCepAdm().getStatement(name);
			// getUpdateListeners
			Iterator<UpdateListener> listTmp = stmtTmp.getUpdateListeners() ;
			while (listTmp.hasNext()   ) {
				UpdateListener l = listTmp.next(); 
				listSet.add(  name +" = { " + l +"} \n");
			}
			// getStatementAwareListeners
			Iterator<StatementAwareUpdateListener> stnlistenersTmp = stmtTmp.getStatementAwareListeners();
			while (stnlistenersTmp.hasNext()   ) {
				StatementAwareUpdateListener l = stnlistenersTmp.next(); 
				listSet.add(  name +" <=st= {{ " + l +"}} \n");
			}
			
		}
		Object[] listeners = listSet.toArray();
		return listeners;
	}
	private Object[] listAllStatements(String[] StatementNames) {
		ArrayList<String> statementsList= new ArrayList();
		for(String name:StatementNames) {
			EPStatement stmtTmp = cepKeeper.getCepAdm().getStatement(name);
			statementsList.add( name +" =  '" + stmtTmp.getText() +"' \n");
		}
		Object[] statements = statementsList.toArray();
		return statements;
	}

	
	private String map2string(Map  vars) {
		String retval = "";
		Set<String> keys = vars.keySet();
		for (String key:keys ) {
			retval += key +" ==: [" + vars.get(key) +"]," ;
		}
		return retval;
	}
	String array2string(Object []oPar){
		String retval = "";
		for (Object o:oPar)retval+=o+" , ";
		return retval;
	}

}