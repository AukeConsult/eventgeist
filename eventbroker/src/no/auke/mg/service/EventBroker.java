package no.auke.mg.service;

import java.io.File;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.websocket.Session;

import no.auke.mg.event.EventService;
import no.auke.mg.event.TimeFrame;
import no.auke.mg.event.UserSession;
import no.auke.mg.event.basic.BasicEvent;
import no.auke.mg.event.football.FootballEvent;

public class EventBroker implements Runnable {

	private static EventBroker instance;

	private static Map<String, EventService> events = new ConcurrentHashMap<String, EventService>();
	private static Map<String, UserSession> sessions = new ConcurrentHashMap<String, UserSession>();
	private static int report_period_default=5000;

	public static String reportDir="";

	private EventBroker() {}

	public static void addSession(Session session, String eventtype, String eventid, String userid, String support, String position) {

		if(!events.containsKey(eventid.trim())) {
			// check what type of event
			if(eventtype.trim().equals("football")) {
				events.put(eventid, new FootballEvent(eventid.trim(), report_period_default));
			} else {
				events.put(eventid, new BasicEvent(eventid.trim(), report_period_default));
			}
			// read event info

			// initialize and start event
			events.get(eventid.trim()).init();
		}

		EventService event = events.get(eventid.trim());
		UserSession usersession = new UserSession(session, event, userid.trim(), support.trim(), position.trim(),0);
		event.addUser(usersession);
		sessions.put(session.getId(), usersession);

	}

	public static void addResponse(Session session, String response) {
		if(sessions.containsKey(session.getId())) {
			sessions.get(session.getId()).addResponse(response);
		}
	}

	public static void closeSession(Session session) {
		if(sessions.containsKey(session.getId())) {
			sessions.get(session.getId()).close();
			sessions.remove(session.getId());
		}
	}

	public static void initialize() {

		if (instance == null) {

			// read parameters
			reportDir="C:/projects/tmp_testoutput/events/";
			new File(reportDir).mkdir();

			//reportDir = System.getProperty("user.dir") + "/events/";
			instance = new EventBroker();
			new Thread(instance).start();

		}

	}

	@Override
	public void run() {

		while (true) {

			try {

				Thread.sleep(1000);
				System.out.println("do push size clients " + sessions.size());

				for(EventService event:events.values()) {

					//System.out.println("push event");
					for(TimeFrame frame:event.getTimeframes()) {

						String frameresult = frame.readResults();
						if(frameresult!=null) {

							for(UserSession session:frame.getUserSessions()) {

								System.out.println("push " + session.getSession().getId());
								if (session.isOpen()) {
									session.getSession().getBasicRemote().sendText("T##"+frameresult);
									if(session.hasResult()) {
										session.getSession().getBasicRemote().sendText("S##" + session.readResults());
									}
								} else {
									//TODO add logging
									System.out.println("close " + session.getSession().getId());
									frame.closeSession(session);
									sessions.remove(session.getId());
								}
							}
						}
					}
				}

			} catch (InterruptedException e) {
			} catch (Exception e) {
				e.printStackTrace();
			}

		}

	}
}