package no.auke.mg.broker;

import java.io.File;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.websocket.Session;

import no.auke.mg.channel.ChannelService;
import no.auke.mg.channel.UserSession;
import no.auke.mg.channel.impl.FileSysStorage;
import no.auke.mg.channel.models.ChannelInfo;
import no.auke.mg.channelimpl.football.FootballChannel;
import no.auke.mg.services.Storage;

public class EventBroker {

	private static EventBroker instance;

	private static Map<String, ChannelService> channels = new ConcurrentHashMap<String, ChannelService>();
	private static Map<String, UserSession> usersessions = new ConcurrentHashMap<String, UserSession>();

	private static int timeslot_period_default=2000;
	public static String reportDir="";

	public static WsMonitor monitor;
	public static Storage storage;

	private EventBroker() {}

	public static void addSession(Session session, String eventtype, String eventid, String userid, String support, String position) {

		initialize();

		try {

			if(!channels.containsKey(eventid.trim())) {

				// get eventifo

				ChannelInfo info = new ChannelInfo(eventid.trim());
				info.setChannelid(eventid.trim());
				info.setType(eventtype.trim());
				info.setTimeslot_period(timeslot_period_default);

				// check what type of channel
				if(info.equals("football")) {
					channels.put(info.getChannelid(), new FootballChannel(info, monitor,storage));
				} else {
					channels.put(info.getChannelid(), new FootballChannel(info, monitor,storage));
				}

				// read channel info
				// initialize and start channel
				channels.get(info.getChannelid()).init();

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
			reportDir="C:/projects/tmp_testoutput/channel/";
			new File(reportDir).mkdir();

			//reportDir = System.getProperty("user.dir") + "/channel/";
			instance = new EventBroker();
			monitor = new WsMonitor();
			monitor.init();
			storage = new FileSysStorage(reportDir);
			storage.init();

		}

	}

}