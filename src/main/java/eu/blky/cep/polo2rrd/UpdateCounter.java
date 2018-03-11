package eu.blky.cep.polo2rrd;

import java.io.IOException;

import cc.co.llabor.websocket.ErrorProcessingException;
import cc.co.llabor.websocket.MessageHandler;

public class UpdateCounter implements MessageHandler {
	private int updateCounter = 0;
	
	@Override
	public void handleMessage(String message) throws ErrorProcessingException {
		if (updateCounter%1000==0) {
			System.out.println("+#"+updateCounter+"#+-to  ==:" + message);
		}
		updateCounter++;
	}

	@Override
	public void destroy() throws IOException {
		// TODO Auto-generated method stub

	}

}
