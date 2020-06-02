package no.auke.mg.broker;

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

	private static Map<String, Session> sessions = new ConcurrentHashMap<String, Session>();
	private static Map<String, UserSession> usersessions = new ConcurrentHashMap<String, UserSession>();

	private static int report_period_default=5000;
	public static String reportDir="";

	private EventBroker() {}

	public static void addSession(Session session, String eventtype, String eventid, String userid, String support, String position) {

		try {

			if(!events.containsKey(eventid.trim())) {
				// check what type of event
				if(eventtype.trim().equals("football")) {
					events.put(eventid, new FootballEvent(eventid.trim(), report_period_default));
				} else {
					events.put(eventid, new BasicEvent(eventid.trim(), report_period_default));
				}
				// read event info

				// initialize and start event
				events.get(eventid.trim()).init(EventBroker.reportDir);
			}

			EventService event = events.get(eventid.trim());

			UserSession usersession = new UserSession(session.getId(), event, userid.trim(), support.trim(), position.trim(),0);
			event.addUser(usersession);

			sessions.put(session.getId(), session);
			usersessions.put(session.getId(), usersession);

		} catch (Exception ex) {
			System.out.println(ex.getMessage());
		}

	}

	public static void addResponse(Session session, String response) {
		if(usersessions.containsKey(session.getId())) {
			usersessions.get(session.getId()).addResponse(response);
		}
	}

	public static void closeSession(Session session) {
		if(sessions.containsKey(session.getId())) {
			usersessions.get(session.getId()).close();
			sessions.remove(session.getId());
			usersessions.remove(session.getId());
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

							for(UserSession usersession:frame.getUserSessions()) {

								System.out.println("push " + usersession.getId());
								if (usersession.isOpen()) {
									sessions.get(usersession.getId()).getBasicRemote().sendText(frameresult);
								} else {
									//TODO add logging
									System.out.println("close " + usersession.getId());
									sessions.remove(usersession.getId());
									usersessions.remove(usersession.getId());
									frame.closeSession(usersession);
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