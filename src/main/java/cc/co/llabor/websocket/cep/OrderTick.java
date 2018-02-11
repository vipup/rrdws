package cc.co.llabor.websocket.cep;

import java.math.BigDecimal;

public class OrderTick {

	private double price;
	private double volume;
	private String pair;
	private boolean type;
	private double total;

	public OrderTick(String pair, String typeTMP, BigDecimal price, BigDecimal volume) {
		this(pair, "0".equals(typeTMP), price.doubleValue(), volume.doubleValue());
		
	}

	public OrderTick(String pair, boolean type,  double price, double volume) {
		this.setPair(pair);
		this.setVolume(volume);
		this.setPrice(price);
		this.setType(type);
		this.setTotal(price*volume);
	}
	
//	public double getTotal() {
//		return price * volume;
//	}

	public double getPrice() {
		return price;
	}

	public void setPrice(double price) {
		this.price = price;
	}

	public double getVolume() {
		return volume;
	}

	public void setVolume(double volume) {
		this.volume = volume;
	}

	public String getPair() {
		return pair;
	}

	public void setPair(String pair) {
		this.pair = pair;
	}

	public boolean isType() {
		return type;
	}

	public void setType(boolean type) {
		this.type = type;
	}

	public double getTotal() {
		return total;
	}

	public void setTotal(double total) {
		this.total = total;
	}

}
