package no.auke.mg.broker;

import java.io.File;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.websocket.Session;

import no.auke.mg.channel.ChannelService;
import no.auke.mg.channel.UserSession;
import no.auke.mg.channel.impl.football.FootballChannel;
import no.auke.mg.channel.models.ChannelInfo;
import no.auke.mg.rest.EventApi;
import no.auke.mg.services.Monitor;
import no.auke.mg.services.Storage;

public class EventBroker {

	private static EventBroker instance;

	private static Map<String, ChannelService> channels = new ConcurrentHashMap<String, ChannelService>();
	private static Map<String, UserSession> usersessions = new ConcurrentHashMap<String, UserSession>();

	public static String reportDir="";

	public static WsMonitor monitor;
	public static Storage storage;
	public static EventApi eventapi;

	private EventBroker() {}

	public static void addSession(Session session, String eventtype, String eventid, String userid, String support, String position) {

		initialize();

		try {

			if(!channels.containsKey(eventid.trim())) {

				// get eventifo

				ChannelInfo channellinfo = ChannelInfo.create(eventid);
				channellinfo.setType(eventtype);
				channellinfo.setEventid(eventid);

				// check what type of channel
				if(eventtype.equals("football")) {
					channels.put(channellinfo.getChannelid(), new FootballChannel(channellinfo));
				}
				storage.saveChannelInfo(channellinfo);

				// read channel info
				// initialize and start channel
				channels.get(channellinfo.getChannelid()).init();


			}

			ChannelService channel = channels.get(eventid.trim());

			UserSession usersession = new UserSession(session.getId(), channel, userid.trim(), support.trim(), position.trim(),0);
			channel.addUser(usersession);
			monitor.addSession(session);

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
			reportDir="C:/projects/evstorage/";
			new File(reportDir).mkdir();

			//reportDir = System.getProperty("user.dir") + "/channel/";

			instance = new EventBroker();

			monitor = new WsMonitor();
			monitor.init();
			Monitor.instance = monitor;

			storage = new no.auke.mg.services.impl.FileSysStorage(reportDir);
			storage.init();
			Storage.instance=storage;

		}

	}

}