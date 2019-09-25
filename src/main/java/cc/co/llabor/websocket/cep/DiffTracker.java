package cc.co.llabor.websocket.cep;
 
import java.util.HashMap; 
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.espertech.esper.client.EventBean;
import com.espertech.esper.client.UpdateListener;
import com.espertech.esper.event.map.MapEventBean; 
import com.fasterxml.jackson.databind.JsonNode; 
import com.fasterxml.jackson.databind.ObjectMapper; 
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory; 
import com.fasterxml.jackson.databind.node.ObjectNode; 

import cc.co.llabor.cache.CacheManager; 
import net.sf.jsr107cache.Cache;

public class DiffTracker implements UpdateListener {
	private int callCounter = 0;
	ObjectMapper objectMapper ;
	Cache diffCacher = CacheManager.getInstance().getCache("DiffTracker",true);
 
	public DiffTracker (){
		objectMapper = new ObjectMapper();  
		
	}
	
	
	Map<String, JsonNode> pairsMap = new HashMap<String, JsonNode>();
	// --name---
	String []props = "pair---timewindow---BOS---percentDIFF---priceDIFF---middlePCENT---tovPCENT---dataFIRST---tovFIRST---iTOV---iAVG---pTOV---pAVG---dTOV---dAVG---dMIN---startTIMESTAMP---diffTIME---dCAL---dCNT---type---dMAX---name---stopTIME---startTIME".split("---");
	
	String []keyprops = "BOS---timewindow---type---pair".split("---");
	/** Logger */
	private static Logger LOG = LoggerFactory.getLogger(DiffTracker.class);
	
	@Override
	public void update(EventBean[] newEvents, EventBean[] oldEvents) {
		JsonNodeFactory nc = JsonNodeFactory.instance;
		ArrayNode list = new ArrayNode(nc);
		
		for (Object e:newEvents) {
			MapEventBean eBean = (MapEventBean)e;
			String key = calcKey(eBean);
			JsonNode value = pairsMap.get(key);
			value = value == null? nc.objectNode():value  ;//nc.pojoNode(eBean) ;
			for (String property:props) {
				if (!eBean.getProperties().containsKey(property))continue;
				if (null == eBean.get(property)) continue;
				String object = ""+eBean.get(property);
				((ObjectNode) value).put(property, object);
			}
			
			//list.add( value );
			pairsMap.put(key, value);
			callCounter++;
			//if ((""+eBean.get("pair")).contains("BTC_ETH")) {
			if ((""+eBean.get("pair")).contains("USDT_BTC")) { 
			 LOG.info( "+DIFF+"+callCounter+"+--:"+" ==:"+eBean.getProperties());
			}
		}
		list.removeAll();
		list.addAll( pairsMap.values() );
		try {  
			 
	 
			String valueAsString = objectMapper.writeValueAsString( list );
			
			//Object data = diffCacher .get("last");//diffCacher.put("last", tableDataMessage);
			//tableDataMessage = data==null?""+data:tableDataMessage;
			diffCacher .put("last", valueAsString);//diffCacher.put("last", tableDataMessage);
		}catch (Exception ex) {
			ex.printStackTrace();
		}				
		
	}

	private String calcKey(MapEventBean eBean) {
		String key = "";
		
		for (String k:keyprops) {
			key +=eBean.get(k)+"::";
		}
		return key;
	}

}
