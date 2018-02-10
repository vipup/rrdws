package cc.co.llabor.websocket;

public interface DestroyTracker {

	void destroyed(DestroyableWebSocketClientEndpoint destroyableWebSocketClientEndpoint);

	void stop();

}
