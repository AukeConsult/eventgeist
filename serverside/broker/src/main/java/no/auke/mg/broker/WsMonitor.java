package no.auke.mg.broker;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.websocket.Session;

import no.auke.mg.channel.TimeFrame;
import no.auke.mg.channel.UserSession;
import no.auke.mg.services.impl.JsonMonitor;

public class WsMonitor extends JsonMonitor {

	private static Map<String, Session> sessions = new ConcurrentHashMap<String, Session>();

	public void addSession(Session session) {
		sessions.put(session.getId(), session);
	}

	public WsMonitor() {}

	@Override
	public void sendSession(UserSession usersession, String result) {

		System.out.println("push user:" + usersession.getUserid() + ":" + usersession.getUserid() + ":" + result);
		if (usersession.isOpen()) {
			try {
				sessions.get(usersession.getId()).getBasicRemote().sendText(result);
			} catch (IOException e) {
				e.printStackTrace();
			}
		} else {
			//TODO add logging
			System.out.println("close " + usersession.getId());
			sessions.remove(usersession.getId());
			usersession.close();
		}
	}

	@Override
	public void sendTimeFrame(TimeFrame frame, String result) {}

}
