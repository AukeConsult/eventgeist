package no.auke.mg.event;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import no.auke.mg.event.models.EventInfo;
import no.auke.mg.event.models.Team;

public class EventStorage {

	private String location="";

	private Map<String,EventInfo> events = new HashMap<String,EventInfo>();

	public EventStorage() {}
	public EventStorage(String location) {
		this.location=location;
	}

	public EventInfo getEvent(String eventid, String type) {

		if(events.containsKey(eventid+type)) {

			EventInfo info = new EventInfo();
			info.setEventid(eventid);
			info.setType(type);
			info.setTimeslot_period(2000);
			info.setAvg1Time(1000*15);
			info.setAvg2Time(1000*60*5);
			info.setEventname("Navnet er " + eventid);

			Calendar cal = Calendar.getInstance();
			info.setEventstart(cal.getTime());
			cal.add(Calendar.HOUR, 3);
			info.setEventstop(cal.getTime());

			info.getTeams().put("team1", new Team("team1","dette er team 1","",null));
			info.getTeams().put("team2", new Team("team2","dette er team 2","",null));
			info.getProps().put("bilde", null);
			info.getProps().put("kampfakta", "sasfasdasd");
			info.getProps().put("osv1", "osv");
			info.getProps().put("osv2", "osv");



		}


		return new EventInfo();
	}

	public void saveEvent(EventInfo event) {

	}


}
