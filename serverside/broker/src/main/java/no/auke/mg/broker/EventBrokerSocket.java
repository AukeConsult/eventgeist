package no.auke.mg.broker;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;

@ServerEndpoint("/evsocket")
public class EventBrokerSocket {

	@OnOpen
	public void onOpen(Session session) {

		System.out.println("open " + session.getId());
		Map<String, List<String>> params = session.getRequestParameterMap();

		EventBroker.initialize();
		EventBroker.addSession(
				session,
				params.get("eventtype") != null?params.get("eventtype").get(0).trim():"standard",
						params.get("event") != null?params.get("event").get(0).trim():"TestServlet",
								params.get("user") != null?params.get("user").get(0).trim():session.getId(),
										params.get("support") != null?params.get("support").get(0).trim():"",
												params.get("position") != null?params.get("position").get(0).trim():""
				);

	}

	@OnClose
	public void onClose(Session session) {
		System.out.println("onClose::" +  session.getId());
		EventBroker.closeSession(session);
	}

	@OnMessage
	public void onMessage(String message, Session session) {

		System.out.println("HIT:" + session.getId() + ":" + message);
		EventBroker.addResponse(session, message);
		try {
			session.getBasicRemote().sendText("HIT:" + message);
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	@OnError
	public void onError(Throwable t) {
		t.printStackTrace();
		System.out.println("onError::" + t.getMessage() + t.getStackTrace().toString());
	}
}