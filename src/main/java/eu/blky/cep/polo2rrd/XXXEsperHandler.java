package eu.blky.cep.polo2rrd;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.espertech.esper.client.EPAdministrator;
import com.espertech.esper.client.EPRuntime; 
import com.espertech.esper.client.EPStatement;
import com.espertech.esper.client.UpdateListener;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper; 

import cc.co.llabor.websocket.ErrorProcessingException;
import cc.co.llabor.websocket.MessageHandler; 
import cc.co.llabor.websocket.WS2RRDPump;
import cc.co.llabor.websocket.cep.DiffTracker;
import cc.co.llabor.websocket.cep.OrderTick;

public class XXXEsperHandler implements MessageHandler {
	final Polo2RddForwarderService parentService;
	public XXXEsperHandler (Polo2RddForwarderService parent) {
		parentService=parent;
		
	}

	
	//private EPAdministrator cepAdm;
	//private EPRuntime cepRT;
	//EPServiceProvider cep;
	/** Logger */
	private static Logger LOG = LoggerFactory.getLogger(XXXEsperHandler.class);
	UpdateListener diffTracker = null;
	private Map<String, RrdDirectUpdater> updaterRepo = new HashMap<String, RrdDirectUpdater>();
	// http://www.espertech.com/esper/solution-patterns/#aggregate-3
	// select avg(value) as avgValue, count(value) as countValue,
	//min(value) as minValue, max(value) as maxValue from MyEvent#time(60 sec)
	// TODO http://www.espertech.com/esper/solution-patterns/#triple-bottom-pattern
	private void esperXXXYYY(  String MARKET_PAIR, JsonNode nodeTmp) {
		JsonNode xxx = nodeTmp.get(2).get(0);
		if ("o".equals( xxx.get(0).asText() ) ) {
			String typeTMP = xxx.get(1).asText() ;
			BigDecimal priceTMP = new BigDecimal(xxx.get(2).asText()); 
			BigDecimal volTMP = new BigDecimal(xxx.get(3).asText()); 
			
			registerRRDUpdaterIfAny(MARKET_PAIR, typeTMP,  "price", 10 );
			registerRRDUpdaterIfAny(MARKET_PAIR, typeTMP,  "volume", 10 );
			registerRRDUpdaterIfAny(MARKET_PAIR, typeTMP,  "total", 10 );
//			registerRRDUpdaterIfAny(MARKET_PAIR, typeTMP,  "price", 60 );
//			registerRRDUpdaterIfAny(MARKET_PAIR, typeTMP,  "volume", 60 );
//			registerRRDUpdaterIfAny(MARKET_PAIR, typeTMP,  "total", 60 );
//			registerRRDUpdaterIfAny(MARKET_PAIR, typeTMP,  "price", 300 );
//			registerRRDUpdaterIfAny(MARKET_PAIR, typeTMP,  "volume", 300 );
//			registerRRDUpdaterIfAny(MARKET_PAIR, typeTMP,  "total", 300 );
//			registerRRDUpdaterIfAny(MARKET_PAIR, typeTMP,  "price", 600 );
//			registerRRDUpdaterIfAny(MARKET_PAIR, typeTMP,  "price", 1200 );
//			registerRRDUpdaterIfAny(MARKET_PAIR, typeTMP,  "price", 2400 );
//			registerRRDUpdaterIfAny(MARKET_PAIR, typeTMP,  "volume", 600 );
//			registerRRDUpdaterIfAny(MARKET_PAIR, typeTMP,  "total", 600 );
			
			OrderTick order = new OrderTick(MARKET_PAIR, typeTMP, priceTMP, volTMP );
			// processing Event
		    getCepRT().sendEvent(order); 
 
		}else {
			LOG.debug( "TODO XXXYYY_O:" + xxx);
		}		
	}

	private void registerRRDUpdaterIfAny(String MARKET_PAIR, String typeTMP, String propPar, int timeWindowAverage) {
			String XPATH_PREFIX = WS2RRDPump.PO_LO + "/EQLOrder/" +MARKET_PAIR +"/"+ typeTMP ;
			// PRICE
			String nsTmp = XPATH_PREFIX + "/"+ propPar+"_"+timeWindowAverage+"sec";
			String AGGRSUFFIX = (""+nsTmp.hashCode()+System.currentTimeMillis()).replaceAll("-", "_");
			// TODO even don't think about sync :)))
			if (updaterRepo.get(nsTmp)== null) {
				  
				
				if ("price".equals(propPar)) {
					// step 1 : 
					
					String 	eqlDEFsecSELLBUY = " insert into OrderTick"+AGGRSUFFIX+"  \n"
							+ " select "
							+ "   type BOS ,  \n"
							+ "   ( avg ( price )  ) data ,  \n"
							+ "   ( avg ( price )  ) dataAVG ,  \n" 
							+ "   ( max ( count( price )  + avg ( (price * volume) ) / avg (volume)  / 1000000 )  ) dataMAX ,  \n"
							+ "   ( min ( count( price )  + avg ( (price * volume) ) / avg (volume)  / 1000000 )  ) dataMIN ,  \n"
							+ "   ( max ( count( price )  + avg ( (price)  / 1000000 )  )   ) priceMAX ,  \n"
							+ "   ( min ( count( price )  + avg ( (price)  / 1000000 )  )   ) priceMIN ,  \n"
							+ "   ( count ( price )  ) dataCNT ,  \n"
							+ "   (  sum ( price ) / count( price ) ) dataCAL , \n"
							+ "   (  sum (total) / sum (volume) ) dataTOV , \n"															
							+ "   "+ timeWindowAverage +" timewindow , \n"
							+ " 'price' name \n"
							+ "from OrderTick(pair='"+MARKET_PAIR+"').win:time( "+timeWindowAverage+" sec) "
									+ "where"
									+ " volume >0 "
	//								+ " and type = true " // only buy  
									+ "\n";
				EPStatement cepDEFsec= getCepAdm().createEPL(eqlDEFsecSELLBUY);
				LOG.trace("cepDEFsec::", cepDEFsec);
	 		
					
					// step 2 : 10 sec
					String eql10sec = " insert into DiffTracker "
							+ "select "
							
							+ "		BOS BOS, "
							+ "		'"+MARKET_PAIR+"' pair, "
							+ "		max (dataMAX) dataMAX, "
							+ "		min (dataMIN) dataMIN, "
							+ "		max (priceMAX) priceMAX, "
							+ "		min (priceMIN) priceMIN, "
							
							+ "		avg (dataAVG) dataAVG, "
							+ "		max (dataCNT) dataCNT, "
							+ "		avg (dataCAL) dataCAL, "	
							+ "		avg (dataTOV) dataTOV, "
							+ "		'price' type, "
							+ "		'"+timeWindowAverage+"' timewindow, "
							+ "		'price'  name, "
							+ "		avg( data ) data from OrderTick"+AGGRSUFFIX+".win:time_batch(  "+10+ "   sec) "
							+ " where name ='price' "
							+ "group by BOS,timewindow, name "
							
											+ "";
					EPStatement cep10sec= getCepAdm().createEPL(eql10sec);
					
					//RrdOrderUpdater rrdUpdaterTmp = new RrdOrderUpdater(rrdWS, nsTmp, "price");
					//cep10sec.addListener(rrdUpdaterTmp);
					RrdDirectUpdater rrdWS = new RrdDirectUpdater(nsTmp, "data" );
					cep10sec.addListener(rrdWS); // <-- DirectRrdUpdater
					updaterRepo.put(nsTmp,rrdWS);
					
				
				}else { // volume and total  
					// step 1 : 
					 
					String 	eqlDEFsec = " insert into OrderTick_ELSE"+AGGRSUFFIX+"  \n"
							+ " select "
							+ "   avg ("+propPar+") data ,  \n"    
							+ "   "+ timeWindowAverage +" timewindow , \n"
							+ " '"+propPar+ "' name \n"
							+ "from OrderTick(pair='"+MARKET_PAIR+"').win:time( "+timeWindowAverage+" sec) \n";
					EPStatement cepDEFsec= getCepAdm().createEPL(eqlDEFsec);
					LOG.trace("cepDEFsec::", cepDEFsec);
					// step 2 : 10 sec
					String eql10sec = "  "
							+ " select "
							+ "   avg (data) data ,  \n"    
							+ "   "+ timeWindowAverage +" timewindow , \n"
							+ " '"+propPar+ "' name \n"
							+ "from OrderTick_ELSE"+AGGRSUFFIX+".win:time_batch(  "+10+ "   sec) ";
					
					EPStatement cep10sec= getCepAdm().createEPL(eql10sec);
					
					//RrdOrderUpdater rrdUpdaterTmp = new RrdOrderUpdater(rrdWS, nsTmp, propPar);
					//cep10sec.addListener(rrdUpdaterTmp);
					RrdDirectUpdater rrdWS = new RrdDirectUpdater(nsTmp, "data" );
					cep10sec.addListener(rrdWS); // <-- DirectRrdUpdater
					//updaterRepo.put(nsTmp,rrdUpdaterTmp);
					updaterRepo.put(nsTmp,rrdWS);	
				}
				
				// step 3 : diffTracker 
				step3_doItOnlyOnce(); 
			    
				// step 4: summaryze that all
			    String eql4 = "insert into TicksPerSecond\n" + 
			    		"select pair symbol, 'OrderTick' type,    count(*) as cnt\n" + 
			    		"from OrderTick.win:time_batch(10 second)\n" + 
			    		"group by pair";
			    EPStatement statStmtTmp = getCepAdm().createEPL(eql4); 
			    LOG.trace("statStmtTmp:::::", statStmtTmp);
			    //statStmtTmp.addListener(new StatisticPrinter());
			}
		}

	public EPAdministrator getCepAdm() {
		return this.parentService.getCepKeeper().getCepAdm();
	}

	public void setCepAdm(EPAdministrator cepAdm) {
		throw new RuntimeException("wrong using");
	}

	public EPRuntime getCepRT() {
		return this.parentService.getCepKeeper().getCepRT();
	}

	public void setCepRT(EPRuntime cepRT) {
		throw new RuntimeException("wrong using");
	}

	private void step3_doItOnlyOnce() {
			// step 3 : diffTracker  
			if (diffTracker == null) { 
				String eql3 = "" + 
						"select BOS, name, timewindow, "
						+ "( dataAVG - (1000000.0000000000001* (dataMAX%1.0000000000000000001))    )							diffMAX , \n"
						+ "( dataAVG - (1000000.0000000000001* (dataMIN%1.0000000000000000001))    )							diffMIN ,\n"
						+ "( (1000000.0000000000001* (dataMAX%1.0000000000000000001))   - (1000000.0000000000001* (dataMIN%1.0000000000000000001))    )							diffDIF ,\n"
						
						+ "( (dataMAX + dataMIN)/2   - dataTOV )				diffTOV ,\n"
						+ "( dataAVG - (dataMAX + dataMIN)/2 )/dataAVG*100 	middlePCENT ,\n"
						+ "( dataAVG - dataTOV )/dataAVG*100				tovPCENT ,\n"
						+ " dataAVG dAVG, \n"
						+ " (1000000.0000000000001* (dataMAX%1.0000000000000000001))  dMAX, \n" // last  on timeWin
						+ " (1000000.0000000000001* (dataMIN%1.0000000000000000001))  dMIN, \n" // first on timeWin
						+ " 100 * (  (1000000.0000000000001* (dataMAX%1.0000000000000000001))  - (1000000.0000000000001* (dataMIN%1.0000000000000000001))  ) / (  dataTOV    )  percentDIFF, \n" // drowing in percent 
						+ " 100 * (  (1000000.0000000000001* (priceMAX%1.0000000000000000001))  - (1000000.0000000000001* (priceMIN%1.0000000000000000001))  ) / (  dataTOV    )  priceDIFF, \n" // drowing in percent 
						+ " dataCAL dCAL, \n"
						+ " dataCNT dCNT, \n"
						+ " dataTOV dTOV, \n"
						+ " pair, \n" 
						+ " type \n"
						+ " \n" 
						+ "from DiffTracker.win:time_batch(   10   sec) \n"
						+ " where name='price' \n" 
						+ " group by pair, type, name , timewindow, BOS \n"
						+ " ";
				EPStatement difftrackerTMP = getCepAdm().createEPL(eql3); 
				diffTracker = new DiffTracker();
				difftrackerTMP.addListener(diffTracker );
			}
		}

	@Override
	public void handleMessage(String message) throws ErrorProcessingException {
		message.length();

		LOG.trace( "<<<<POLO<<<<" + message);
		ObjectMapper mapper = new ObjectMapper();

		try {
			JsonNode nodeTmp = mapper.readTree(message); 
			String theType = "" + nodeTmp.get(0);
 
			if (parentService.getPoloWS().getPairNameByID(theType) != null ) { 
				String MARKET_PAIR = parentService.getPoloWS().getPairNameByID(theType); 
				esperXXXYYY( MARKET_PAIR, nodeTmp); 
			}

		} catch (JsonProcessingException e) {
			LOG.error("public void handleMessage(String message) throws ErrorProcessingException {;", e);
		} catch (IOException e) {
			LOG.error("public void handleMessage(String message) throws ErrorProcessingException {;", e);
		} catch (RuntimeException e) {e.printStackTrace();
			// ignore
			LOG.debug( "error processing  message: "+message);
			throw new ErrorProcessingException(message);
		}

	} // [121,186032845,[["i",{"currencyPair":"USDT_BTC","orderBook":[{"13462.99999743":"1.68000000","13462.99999744":"1.31805000","13462.99999758":"0.50000000","13462.99999780":"0.86497017","13464.31079843":"0.04915124","13466.41196196":"3.00000000","13466.49750000":"0.88461905","13482.70813480":"13.92564466","13482.70813481":"1.08300000","13488.82829699":"0.90000000","13489.34775108":"0.00019170","13490.76857260":"0.00063599","13493.80163500":"0.00022948","13494.13137433":"0.00019181","13494.44121843":"0.00009985","13496.38331450":"1.03000000","13498.42589197":"0.05000000","13499.47126843":"0.00009985","13499.88752490":"0.00022938","13499.99999997":"0.01997000","13500.00000000":"0.02065193","13500.03793296":"0.03397761","13502.47422778":"2.20670152","13503.55947272":"1.02000000","13504.25258338":"0.50000000","13504.50131843":"0.00009985","13509.53136843":"0.00009985","13510.20131351":"1.20000000","13514.56141843":"0.00009985","13514.68190260":"0.00022913","13514.92569885":"1.21408000","13518.37136828":"0.00802997","13519.98787904":"9.52664948","13520.44786103":"6.51758385","13520.69353400":"0.00022903","13521.20861368":"0.00120401","13521.40112840":"0.03458640","13525.06105573":"0.00305614","13529.79846790":"0.00022887","13536.26357894":"0.00819816","13543.03718681":"0.00035126","13544.00000000":"0.00300000","13544.41536720":"0.00480379","13544.53935886":"0.00149580","13544.66500000":"0.02000000","13545.00000000":"0.01284051","13545.99999997":"0.00022561","13546.00000000":"0.46600000","13547.55741097":"0.00017270","13547.55741098":"0.00018062","13547.96718448":"0.00162206","13549.99679808":"0.00017356","13550.00000000":"0.04098545","13552.36575670":"0.00010000","13553.99999970":"0.00199300","13556.83617810":"0.00022842","13557.58024092":"0.00320895","13560.00000000":"0.00680299","13562.71015106":"0.00015471","13563.54149010":"0.00022830","13563.95837746":"12.14083000","13563.99999997":"0.00022531","13566.36439158":"0.00154384","13572.99999997":"0.00022516","13577.96615909":"0.00283683","13578.21164280":"0.00832087","13578.86249999":"0.00033213","13579.58294507":"0.01115122","13580.00000000":"1.24419218","13581.73223348":"0.00169496","13581.99999997":"0.00022501","13584.24648360":"0.00022796","13584.40130836":"0.00028200","13588.26132407":"0.00168694","13589.01515374":"0.00326652","13589.23889545":"1.00000000","13589.23889546":"0.08675296","13591.76779016":"0.00336940","13593.98579808":"0.00017649","13597.45000000":"0.00086992","13597.85490000":"0.00347238","13598.61145006":"0.00163433","13599.00000000":"0.00036410","13599.99998912":"0.01013749","13599.99999997":"0.00022470","13600.00000000":"0.40158959","13600.00000008":"0.09999686","13600.42656000":"0.00239492","13601.16247510":"0.00029249","13603.22643828":"0.00736218","13608.99999997":"0.00022455","13614.64495686":"0.00110457","13615.00000000":"0.04073169","13615.32846420":"0.01328305","13616.85051890":"0.00131622","13617.99999997":"0.00022440","13618.97305236":"0.01684223","13619.99999997":"0.00015539","13624.48999248":"0.00159909","13624.48999251":"0.00011414","13624.48999252":"0.00035347","13624.48999304":"0.00114652","13624.82882292":"0.00209369","13626.99999997":"0.00022425","13627.29489225":"0.00906975","13627.47540016":"0.00036756","13627.80419240":"0.00353787","13628.19959934":"0.00124975","13628.19959955":"0.02282889","13628.19959975":"0.00459043","13630.13602349":"0.00175442","13630.18163273":"0.03668329","13631.33932520":"0.00010000","13635.99999997":"0.00022410","13637.31911912":"0.00193465","13638.13052751":"0.00017947","13638.50642660":"0.00015126","13638.50642729":"0.00161045","13642.17442734":"0.00395179","13644.99999997":"0.00022395","13645.82399940":"0.00066466","13648.32134667":"0.15181964","13649.12121100":"0.15000389","13649.55578030":"0.00044439","13649.91999947":"0.00012861","13650.00000000":"0.02756745","13651.67999092":"0.00090717","13653.56792863":"0.00200000","13653.99999997":"0.00022380","13654.85000000":"0.00600000","13655.83614612":"0.00712642","13656.87295994":"0.00007658","13656.88319985":"0.00086548","13657.99999997":"0.00015493","13659.00000001":"0.00500000","13659.18773670":"0.00843147","13659.63411113":"0.00928539","13659.99999991":"0.00075509","13660.15999999":"0.00020930","13662.99999997":"0.00022365","13663.86307722":"0.00546364","13664.52965758":"0.00138915","13664.82923349":"0.00228238","13664.94614006":"0.00171606","13665.44733680":"0.00116489","13665.90090876":"0.00371476","13667.50238790":"0.00022657","13668.75607504":"0.00199300","13669.02000000":"0.00027413","13669.84230595":"0.05999882","13671.42399052":"0.00132283","13671.99999997":"0.00022350","13673.72035975":"0.00182459","13673.72370121":"0.00368344","13676.37814170":"0.00988602","13676.68572800":"0.00038593","13676.99999997":"0.00015470","13678.82881753":"0.10656410","13678.92105899":"0.00049901","13678.92105910":"0.00037052","13678.92105912":"0.00014224","13678.92105914":"0.00491847","13680.00000000":"1.19771669","13680.99999997":"0.00022335","13682.40015277":"0.00018250","13682.63618137":"0.00034512","13685.52690793":"0.03170850","13686.63124999":"0.00034873","13686.74732043":"0.00255416","13689.00000001":"0.00500000","13689.99999997":"0.00022320","13690.00000000":"0.00075413","13693.34493532":"0.00269810","13695.99999997":"0.00015448","13696.79999990":"0.00012263","13698.00000000":"0.01000000","13698.26967113":"0.00035126","13698.99999997":"0.00022305","13699.48895446":"0.03027916","13700.00000000":"0.65428679","13700.66166420":"0.00045204","13702.27999999":"0.00187116","13702.48945941":"0.01009144","13703.70000043":"0.00015471","13706.30789114":"0.00390050","13707.85362398":"0.03458362","13707.99999997":"0.00022290","13708.75607504":"0.00199300","13709.43863601":"0.00165664","13710.00000000":"0.00738535","13713.26436367":"0.00010524","13714.92000000":"0.08749595","13714.99999997":"0.00015425","13716.99999997":"0.00022275","13719.00000001":"0.00500000","13719.15989713":"0.00189758","13720.00000000":"0.00455234","13720.86063131":"0.01869487","13724.64155079":"0.00095593","13725.46139115":"0.01077577","13725.89605583":"0.00040523","13725.99999997":"0.00022260","13726.54375622":"0.00219837","13726.95015277":"0.00018558","13727.95000000":"0.00112797","13728.00000000":"0.08433672","13728.84490000":"0.00380997","13729.26673574":"0.00655533","13731.28083006":"0.00180185","13731.83998819":"0.00142596","13731.83999888":"0.00045331","13731.83999889":"0.00013560","13731.97780873":"0.00013457","13733.99999997":"0.00015402","13734.99999997":"0.00022245","13737.72828040":"0.00022541","13738.00000000":"0.00700000","13740.00000000":"0.04448460","13742.00000001":"0.00500000","13743.99999997":"0.00022230","13745.51726558":"0.00106889","13747.56205051":"1.26556507","13748.49953349":"0.00307341","13748.75607504":"0.00199300","13748.80431482":"0.00035022","13749.32353081":"0.00409552","13750.00000000":"0.29325385","13750.02194700":"0.00504074","13750.07667150":"0.00063956","13752.00000001":"0.13213988","13752.99999997":"0.00037596","13753.65517330":"0.01024817","13759.44196488":"0.00095029","13760.00000000":"0.00398800","13761.37274800":"0.00022502","13761.99999997":"0.00022201","13762.55999983":"0.00594619","13765.47507267":"0.05741293","13766.61962050":"0.00197348","13770.00000000":"0.00726217","13770.99999997":"0.00022186","13771.33729812":"0.00055595","13771.99999997":"0.00015357","13773.28647461":"0.00045028","13774.54464059":"0.01174558","13775.10638367":"0.00042549","13777.00000000":"0.01498453","13778.38100738":"0.00061142","13778.43856815":"0.00061142","13779.97416903":"0.00414938","13779.99999997":"0.00022171","13780.00000000":"0.02177068","13783.35835924":"0.00463733","13785.29070000":"0.03104198","13785.29072410":"0.00362705","13785.55620384":"0.03396800","13788.75607504":"0.00199300","13789.43264000":"0.00022456","13790.99999997":"0.00015335","13791.77016587":"0.00084994","13794.39999999":"0.00036617","13795.30804436":"0.00430030","13797.61552006":"0.00189195","13797.99999997":"0.00022142","13798.84060000":"0.02089790","13798.84061948":"1.06268000","13799.00000000":"0.50373552","13799.58178976":"0.00734021","13800.00000000":"7.85242572","13800.00000002":"0.49754693","13800.00410512":"0.00885304","13800.21250000":"0.37120127","13801.76713789":"0.00059282","13802.55520967":"0.00138804","13803.41973335":"0.00974966","13803.87342950":"0.01610141","13804.22361040":"0.00022432","13804.99999979":"0.07584000","13805.95029853":"0.26029126","13806.13006224":"0.00052547","13806.72000000":"0.00027154","13806.99999997":"0.00022127","13809.99999997":"0.00015313","13813.03258145":"0.00010961","13813.75999977":"0.00142033","13813.75999989":"0.00067403","13813.75999991":"0.00022931","13813.75999994":"0.00393494","13813.76000000":"0.00053395","13813.76000003":"0.00080707","13813.97973521":"0.00058316","13816.28755143":"0.00205242","13817.41837370":"0.00022411","13818.54505264":"0.00802631","13819.32000000":"0.53000000","13821.59708129":"0.00060188","13822.64436679":"0.00057670","13822.74821025":"0.02075131","13823.35734000":"0.08029398","13823.62789004":"0.01280269","13824.31671150":"0.00044677","13824.99999997":"0.00022098","13825.03289060":"0.00060555","13825.47507268":"0.02000000","13826.11626626":"0.00057426","13826.85855539":"0.10818970","13826.89230160":"0.00578868","13828.75607504":"0.00199300","13828.99999997":"0.00015291","13829.47267081":"0.00512078","13830.16600832":"0.03615285","13831.80327730":"0.00022388","13832.60453349":"0.00413863","13833.80957215":"0.00175780","13833.99999997":"0.00022083","13836.05636104":"0.00605240","13836.13056000":"0.00477777","13838.05000044":"0.00015471","13839.07463584":"2.00000000","13840.00000000":"0.00398800","13840.29540320":"0.00022374","13841.85863683":"0.00230829","13841.99489187":"0.00056602","13842.00000001":"0.00500000","13842.99999997":"0.00022069","13844.70182919":"0.00451531","13844.89999988":"0.12517213","13850.00000000":"0.07181648","13850.00000084":"0.07651607","13850.14031790":"0.04992782","13850.18492473":"0.00019411","13853...


	@Override
	public void destroy() {
		LOG.error("public void destroy() {} . . . . . . ....", this);
		this.getCepAdm().destroyAllStatements();
		this.parentService.getCepKeeper().getCep().destroy();
		LOG.error("DONE ! :::public void destroy() {} . . . . . . ....", this);
	}

}

	
