package no.auke.mg.broker;

import java.io.File;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.websocket.Session;

import no.auke.mg.event.EventService;
import no.auke.mg.event.Storage;
import no.auke.mg.event.UserSession;
import no.auke.mg.event.dao.EventDao;
import no.auke.mg.event.impl.FileSysStorage;
import no.auke.mg.event.models.EventInfo;
import no.auke.mg.eventimpl.basic.BasicEvent;
import no.auke.mg.eventimpl.football.FootballEvent;

public class EventBroker {

	private static EventBroker instance;

	private static Map<String, EventService> events = new ConcurrentHashMap<String, EventService>();
	private static Map<String, UserSession> usersessions = new ConcurrentHashMap<String, UserSession>();

	private static int timeslot_period_default=2000;
	public static String reportDir="";

	private static EventDao eventdao;
	public static EventDao getEventDao() {
		if(eventdao==null) {

		}
		return eventdao;
	}

	public static WsMonitor monitor;
	public static Storage storage;

	private EventBroker() {}

	public static void addSession(Session session, String eventtype, String eventid, String userid, String support, String position) {

		initialize();

		try {

			if(!events.containsKey(eventid.trim())) {

				// get eventifo

				EventInfo info = new EventInfo(eventid.trim());
				info.setEventid(eventid.trim());
				info.setType(eventtype.trim());
				info.setTimeslot_period(timeslot_period_default);

				// check what type of event
				if(info.equals("football")) {
					events.put(info.getEventid(), new FootballEvent(info, monitor,storage));
				} else {
					events.put(info.getEventid(), new BasicEvent(info, monitor,storage));
				}

				// read event info
				// initialize and start event
				events.get(info.getEventid()).init();


			}

			EventService event = events.get(eventid.trim());

			UserSession usersession = new UserSession(session.getId(), event, userid.trim(), support.trim(), position.trim(),0);
			event.addUser(usersession);

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
		if(usersessions.containsKey(session.getId())) {
			usersessions.get(session.getId()).close();
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
			monitor = new WsMonitor();
			storage = new FileSysStorage(reportDir);

		}

	}

}