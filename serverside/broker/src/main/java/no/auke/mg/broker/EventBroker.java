package no.auke.mg.broker;

import java.io.File;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.websocket.Session;

import no.auke.mg.event.EventMonitor;
import no.auke.mg.event.EventService;
import no.auke.mg.event.TimeFrame;
import no.auke.mg.event.UserSession;
import no.auke.mg.event.basic.BasicEvent;
import no.auke.mg.event.dao.EventDao;
import no.auke.mg.event.football.FootballEvent;
import no.auke.mg.event.models.EventInfo;

public class EventBroker implements Runnable {

	private static EventBroker instance;

	private static Map<String, EventService> events = new ConcurrentHashMap<String, EventService>();

	private static Map<String, Session> sessions = new ConcurrentHashMap<String, Session>();
	private static Map<String, UserSession> usersessions = new ConcurrentHashMap<String, UserSession>();

	private static int timeslot_period_default=2000;
	public static String reportDir="";

	private static EventDao eventdao;
	public static EventDao getEventDao() {
		if(eventdao==null) {

		}
		return eventdao;
	}

	public static EventMonitor monitor;

	private EventBroker() {}

	public static void addSession(Session session, String eventtype, String eventid, String userid, String support, String position) {

		initialize();

		try {

			if(!events.containsKey(eventid.trim())) {

				// get eventifo

				EventInfo info = new EventInfo();
				info.setEventid(eventid.trim());
				info.setType(eventtype.trim());
				info.setTimeslot_period(timeslot_period_default);

				// check what type of event
				if(info.equals("football")) {

					events.put(info.getEventid(), new FootballEvent(info, monitor));

				} else {
					events.put(info.getEventid(), new BasicEvent(info, monitor));
				}
				// read event info

				// initialize and start event
				events.get(info.getEventid()).init(EventBroker.reportDir);


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
			System.out.println(session.getId() + ":" + response);
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
			monitor = new EventMonitor();

			new Thread(instance).start();
		}

	}

	@Override
	public void run() {

		while (true) {

			try {

				Thread.sleep(100);
				//System.out.println("do push size clients " + sessions.size());

				for(EventService event:events.values()) {

					//System.out.println("event " + event.getEventid());
					//System.out.println("push event");

					for(TimeFrame frame:event.getTimeframes()) {

						String frameresult = frame.readResults();
						if(frameresult!=null) {

							for(UserSession usersession:frame.getUserSessions()) {

								System.out.println("push user:" + usersession.getUserid() + ":" + usersession.getUserid() + ":" + frameresult);
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