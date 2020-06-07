package no.auke.mg.event.dao;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import no.auke.mg.event.models.EventInfo;

public class EventDao {

	public void storeEvent(EventInfo event) {}

	public EventInfo readEvent(String eventid) {

		EventInfo event = new EventInfo();
		event.setEventid(eventid);
		event.setType("football");
		event.setEventname(eventid + " kampen");
		event.setEventstart(new Date());

		Map<String, Object> team = new HashMap<String,Object>();
		team.put("name", "Strømsgodset");
		team.put("logo", new Byte[]{});

		event.getTeams().put("team1", team);

		team.put("name", "Brann");
		team.put("logo", new Byte[]{});

		event.getTeams().put("team2", team);

		return event;

	}

}
