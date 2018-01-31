package cc.co.llabor.websocket.cep;

import java.math.BigDecimal;
import java.util.Date;

public class PoloTick {
	String symbol; 
	String name; 
    Double price; 
    Date timeStamp; 

    public PoloTick(String s, String n, BigDecimal p, long t) {
    	this(s,n,p.doubleValue(),t);
    }
    public PoloTick(String s, String n, double p, long t) {	
        symbol = s; 
        name = n;
        price = p; 
        timeStamp = new Date(t); 
    } 

    public double getPrice() { 
        return price; 
    } 
    public String getName() { 
        return name; 
    } 

    public String getSymbol() { 
        return symbol; 
    } 

    public Date getTimeStamp() { 
        return timeStamp; 
    } 
}
