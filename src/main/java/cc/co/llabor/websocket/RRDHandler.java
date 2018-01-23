package cc.co.llabor.websocket;

import java.io.IOException;

public final class RRDHandler implements RRDWebsocketClientEntPoint.MessageHandler {
	/**
	 * 
	 */
	private final WS2RRDPump RRDHandler;

	/**
	 * @param ws2rrdPump
	 */
	RRDHandler(WS2RRDPump ws2rrdPump) {
		RRDHandler = ws2rrdPump;
	}

	long lastHandledTimestamp = 0;
	long messageCunter = 0;
	long messagesPerSec = 0;
	long sizePerSec = 0;

	public void handleMessage(String message) {
		messageCunter ++; messagesPerSec++; sizePerSec+=message.length();
		if ( System.currentTimeMillis() -1000 >lastHandledTimestamp ) {
			if (lastHandledTimestamp>111111111111L && System.currentTimeMillis()  -lastHandledTimestamp  >100000) { // FULL Restart
				try {
					RRDHandler.rrdWS.destroy();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}						
			System.out.println("RRDRCVD:<<<"+(lastHandledTimestamp-System.currentTimeMillis())+">>>   " + "/ "+messagesPerSec +" msg/sec  // "+sizePerSec+"  bytes/per sec  :::" + (sizePerSec/messagesPerSec) +" bytes/message["+messageCunter  );
			lastHandledTimestamp = System.currentTimeMillis();
			messagesPerSec = 0;
			sizePerSec =0;
			System.out.println(">>>>RRD>>>>" + message);
		}						
		//System.out.println(">>>>RRD>>>>" + message);
	}
}