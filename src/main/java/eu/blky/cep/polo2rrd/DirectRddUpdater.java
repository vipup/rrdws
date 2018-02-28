package eu.blky.cep.polo2rrd;

import java.io.IOException;

import cc.co.llabor.websocket.ErrorProcessingException;
import cc.co.llabor.websocket.MessageHandler;

public class DirectRddUpdater implements MessageHandler {

	private int counter;

	@Override
	public void handleMessage(String message) throws ErrorProcessingException {
		// TODO Auto-generated method stub
		counter++;
		System.out.println("TODO Auto-generated method stub["+counter+"]::"+message);

	}

	@Override
	public void destroy() throws IOException {
		// TODO Auto-generated method stub
		System.out.println("TODO Auto-generated method stub");

	}

}
