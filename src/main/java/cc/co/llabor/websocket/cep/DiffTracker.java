package cc.co.llabor.websocket.cep;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.espertech.esper.client.EventBean;
import com.espertech.esper.client.UpdateListener;
import com.espertech.esper.event.map.MapEventBean;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonPointer;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.core.ObjectCodec;
import com.fasterxml.jackson.core.JsonParser.NumberType;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.JsonNodeType;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.node.ValueNode;

import cc.co.llabor.cache.CacheManager;
import net.sf.jsr107cache.Cache;

public class DiffTracker implements UpdateListener {
	private int callCounter = 0;
	
	
	Map<String, JsonNode> pairsMap = new HashMap<String, JsonNode>();
	// --name---
	String []props = "pair---timewindow---tovPCENT---BOS---middlePCENT---dAVG---dMIN---dCAL---diffMIN---dCNT---type---dMAX---diffDIF---diffMAX---dTOV---diffTOV".split("---");
	String []keyprops = "BOS---timewindow---type---pair".split("---");
	
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
				if (null == eBean.get(property)) continue;
				String object = ""+eBean.get(property);
				((ObjectNode) value).put(property, object);
			}
			
			//list.add( value );
			pairsMap.put(key, value);
			callCounter++;
			//if ((""+eBean.get("pair")).contains("BTC_ETH")) {
			if ((""+eBean.get("pair")).contains("USDT_BTC")) { 
			 System.out.println("+DIFF+"+callCounter+"+--:"+" ==:"+eBean.getProperties());
			}
		}
		list.removeAll();
		list.addAll( pairsMap.values() );
		try {  
			String valueAsString = (new ObjectMapper()).writeValueAsString( list );
			Cache diffCacher = CacheManager.getInstance().getCache("DiffTracker",true);
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
